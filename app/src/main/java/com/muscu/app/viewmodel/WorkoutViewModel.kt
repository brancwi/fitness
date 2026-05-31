package com.muscu.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.muscu.app.data.model.AppSettings
import com.muscu.app.data.model.Exercise
import com.muscu.app.data.model.PerformedSet
import com.muscu.app.data.repository.AppSettingsRepository
import com.muscu.app.data.repository.WorkoutRepository
import com.muscu.app.domain.calculator.RestTimeRecommendations
import com.muscu.app.domain.calculator.TempoAnalysis
import com.muscu.app.domain.calculator.TempoAnalyzer
import com.muscu.app.domain.timer.GuideState
import com.muscu.app.domain.timer.RestTimerManager
import com.muscu.app.domain.timer.SetGuideManager
import com.muscu.app.domain.timer.TimerState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class WorkoutUiState(
    val exercises: List<Exercise> = emptyList(),
    val session: com.muscu.app.data.model.WorkoutSession? = null,
    val setsByExercise: Map<String, List<PerformedSet>> = emptyMap(),
    val isLoading: Boolean = false,
    val allCompleted: Boolean = false,
    val currentExerciseIndex: Int = 0,
    val currentSetIndex: Int = 0,
    val timerState: TimerState = TimerState.Idle,
    val showSetCompleteButton: Boolean = false,
    val minRestSeconds: Int = 15,
    val maxRestSeconds: Int = 180,
    val settings: AppSettings? = null,
    val guideState: GuideState = GuideState(),
    val prefilledReps: String = "",
    val prefilledWeight: String = "",
    val speedMultiplier: Float = 1.0f,
    val tempoAnalysis: TempoAnalysis? = null,
    val prepCountdown: Int? = null,
    val showExerciseRating: Boolean = false,
    val ratingExerciseId: String? = null,
    val lastSetForCurrentExercise: com.muscu.app.data.model.PerformedSet? = null
)

class WorkoutViewModel(
    private val repository: WorkoutRepository,
    private val appSettingsRepository: AppSettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WorkoutUiState())
    val uiState: StateFlow<WorkoutUiState> = _uiState

    private lateinit var timerManager: RestTimerManager
    private val guideManager = SetGuideManager(viewModelScope)
    private var prepJob: kotlinx.coroutines.Job? = null

    init {
        viewModelScope.launch {
            val settings = appSettingsRepository.getLatest()
            timerManager = RestTimerManager(
                scope = viewModelScope,
                beepDurationMs = settings.beepDurationMs,
                finalBeepDurationMs = settings.finalBeepDurationMs,
                toneVolume = settings.toneVolume
            )
            guideManager.setSpeedMultiplier(settings.guideSpeedMultiplier)
            _uiState.value = _uiState.value.copy(
                minRestSeconds = settings.minRestSeconds,
                maxRestSeconds = settings.maxRestSeconds,
                settings = settings,
                speedMultiplier = settings.guideSpeedMultiplier
            )
            observeTimer()
            observeGuide()
        }
    }

    private fun observeTimer() {
        viewModelScope.launch {
            timerManager.state.collect { timerState ->
                _uiState.value = _uiState.value.copy(timerState = timerState)
            }
        }
        viewModelScope.launch {
            timerManager.onFinished.collect { finished ->
                if (finished) {
                    timerManager.resetFinishedFlag()
                    moveToNextSet()
                }
            }
        }
    }

    private fun observeGuide() {
        viewModelScope.launch {
            guideManager.state.collect { guideState ->
                _uiState.value = _uiState.value.copy(guideState = guideState)
            }
        }
    }

    fun loadDay(dayOfWeek: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            repository.getExercisesForDay(dayOfWeek).collect { exercises ->
                val session = repository.getLastSessionForDay(dayOfWeek)
                    ?: repository.startSession(dayOfWeek)

                val setsMap = mutableMapOf<String, List<PerformedSet>>()
                var allDone = true
                exercises.forEach { ex ->
                    repository.ensureSetsForExercise(session.id, ex)
                    val sets = repository.getSetsForExercise(session.id, ex.id).first()
                    setsMap[ex.id] = sets
                    if (sets.any { !it.isCompleted }) allDone = false
                }

                val firstExercise = exercises.getOrNull(0)
                val lastSet = firstExercise?.let {
                    repository.getLastCompletedSetForExercise(it.id)
                }

                _uiState.value = _uiState.value.copy(
                    exercises = exercises,
                    session = session,
                    setsByExercise = setsMap,
                    isLoading = false,
                    allCompleted = allDone && exercises.isNotEmpty(),
                    lastSetForCurrentExercise = lastSet
                )
            }
        }
    }

    fun loadTemplate(templateId: String, dayOfWeek: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val templateExercises = repository.getTemplateExercisesSync(templateId)
            val exercises = templateExercises.mapNotNull { te ->
                repository.getExerciseById(te.exerciseId)
            }

            val session = repository.startSession(dayOfWeek).copy(templateId = templateId)
            repository.updateSession(session)

            val setsMap = mutableMapOf<String, List<PerformedSet>>()
            var allDone = true
            templateExercises.forEach { te ->
                val ex = repository.getExerciseById(te.exerciseId) ?: return@forEach
                repository.ensureSetsForExerciseWithConfig(
                    sessionId = session.id,
                    exercise = ex,
                    targetSets = te.targetSets,
                    restSeconds = te.restSeconds
                )
                val sets = repository.getSetsForExercise(session.id, ex.id).first()
                setsMap[ex.id] = sets
                if (sets.any { !it.isCompleted }) allDone = false
            }

            val firstExercise = exercises.getOrNull(0)
            val lastSet = firstExercise?.let {
                repository.getLastCompletedSetForExercise(it.id)
            }

            _uiState.value = _uiState.value.copy(
                exercises = exercises,
                session = session,
                setsByExercise = setsMap,
                isLoading = false,
                allCompleted = allDone && exercises.isNotEmpty(),
                lastSetForCurrentExercise = lastSet
            )
        }
    }

    fun startSet() {
        val state = _uiState.value
        val exercise = state.exercises.getOrNull(state.currentExerciseIndex) ?: return
        val sets = state.setsByExercise[exercise.id] ?: return
        val currentSet = sets.getOrNull(state.currentSetIndex) ?: return
        val settings = state.settings ?: return

        val analysis = TempoAnalyzer.analyze(settings, exercise.targetRepsMax)

        _uiState.value = _uiState.value.copy(
            showSetCompleteButton = true,
            prefilledReps = currentSet.reps?.toString() ?: "",
            prefilledWeight = currentSet.weightKg?.toString() ?: "",
            tempoAnalysis = analysis,
            prepCountdown = settings.prepCountdownSeconds
        )

        // Lancer le compte à rebours de préparation
        prepJob?.cancel()
        val prepSeconds = settings.prepCountdownSeconds.coerceAtLeast(0)
        if (prepSeconds > 0) {
            prepJob = viewModelScope.launch {
                var remaining = prepSeconds
                while (remaining > 0) {
                    delay(1000)
                    remaining--
                    _uiState.value = _uiState.value.copy(prepCountdown = remaining)
                }
                startGuideInternal(exercise, settings)
            }
        } else {
            startGuideInternal(exercise, settings)
        }
    }

    private fun startGuideInternal(exercise: Exercise, settings: AppSettings) {
        val tempo = SetGuideManager.TempoConfig(
            eccentricSeconds = settings.tempoEccentric,
            isometricBottomSeconds = settings.tempoIsometricBottom,
            concentricSeconds = settings.tempoConcentric,
            isometricTopSeconds = settings.tempoIsometricTop
        )
        guideManager.startGuide(
            targetReps = exercise.targetRepsMax,
            tempo = tempo
        )
        _uiState.value = _uiState.value.copy(prepCountdown = null)
    }

    fun skipPrepCountdown() {
        prepJob?.cancel()
        val state = _uiState.value
        val exercise = state.exercises.getOrNull(state.currentExerciseIndex) ?: return
        val settings = state.settings ?: return
        startGuideInternal(exercise, settings)
    }

    fun completeSet(reps: String, weight: String) {
        viewModelScope.launch {
            val state = _uiState.value
            val exercise = state.exercises.getOrNull(state.currentExerciseIndex) ?: return@launch
            val sets = state.setsByExercise[exercise.id] ?: return@launch
            val set = sets.getOrNull(state.currentSetIndex) ?: return@launch

            prepJob?.cancel()
            guideManager.stopGuide()

            val updated = set.copy(
                reps = reps.toIntOrNull() ?: set.reps,
                weightKg = weight.toFloatOrNull() ?: set.weightKg,
                isCompleted = true
            )
            repository.updateSet(updated)

            val newSets = sets.toMutableList()
            newSets[state.currentSetIndex] = updated
            val newMap = state.setsByExercise.toMutableMap()
            newMap[exercise.id] = newSets

            val isLastSetOfExercise = state.currentSetIndex >= sets.size - 1
            val isLastExercise = state.currentExerciseIndex >= state.exercises.size - 1
            val allDone = isLastSetOfExercise && isLastExercise

            if (allDone) {
                _uiState.value = state.copy(
                    setsByExercise = newMap,
                    showSetCompleteButton = false,
                    allCompleted = true,
                    prefilledReps = "",
                    prefilledWeight = "",
                    showExerciseRating = false,
                    ratingExerciseId = null
                )
            } else if (isLastSetOfExercise) {
                // Show rating dialog before moving to next exercise
                _uiState.value = state.copy(
                    setsByExercise = newMap,
                    showSetCompleteButton = false,
                    prefilledReps = "",
                    prefilledWeight = "",
                    showExerciseRating = true,
                    ratingExerciseId = exercise.id
                )
            } else {
                _uiState.value = state.copy(
                    setsByExercise = newMap,
                    showSetCompleteButton = false,
                    prefilledReps = "",
                    prefilledWeight = "",
                    showExerciseRating = false,
                    ratingExerciseId = null
                )
                timerManager.start(updated.restSeconds)
            }
        }
    }

    fun submitExerciseRating(rating: Int) {
        viewModelScope.launch {
            val state = _uiState.value
            val exerciseId = state.ratingExerciseId ?: return@launch
            val sets = state.setsByExercise[exerciseId] ?: return@launch
            val lastSet = sets.lastOrNull() ?: return@launch

            val updated = lastSet.copy(difficultyRating = rating)
            repository.updateSet(updated)

            val newSets = sets.toMutableList()
            newSets[newSets.size - 1] = updated
            val newMap = state.setsByExercise.toMutableMap()
            newMap[exerciseId] = newSets

            _uiState.value = state.copy(
                setsByExercise = newMap,
                showExerciseRating = false,
                ratingExerciseId = null
            )
            timerManager.start(lastSet.restSeconds)
            moveToNextExercise()
        }
    }

    fun skipExerciseRating() {
        viewModelScope.launch {
            val state = _uiState.value
            val exerciseId = state.ratingExerciseId ?: return@launch
            val sets = state.setsByExercise[exerciseId] ?: return@launch
            val lastSet = sets.lastOrNull() ?: return@launch

            _uiState.value = state.copy(
                showExerciseRating = false,
                ratingExerciseId = null
            )
            timerManager.start(lastSet.restSeconds)
            moveToNextExercise()
        }
    }

    private fun moveToNextExercise() {
        val state = _uiState.value
        val nextExerciseIndex = state.currentExerciseIndex + 1
        if (nextExerciseIndex < state.exercises.size) {
            val nextExercise = state.exercises[nextExerciseIndex]
            viewModelScope.launch {
                val lastSet = repository.getLastCompletedSetForExercise(nextExercise.id)
                _uiState.value = state.copy(
                    currentExerciseIndex = nextExerciseIndex,
                    currentSetIndex = 0,
                    showSetCompleteButton = false,
                    lastSetForCurrentExercise = lastSet
                )
                if (state.settings?.autoStartNextSet == true) {
                    startSet()
                }
            }
        }
    }

    fun updateRestTimeForCurrentSet(newRestSeconds: Int) {
        viewModelScope.launch {
            val state = _uiState.value
            val exercise = state.exercises.getOrNull(state.currentExerciseIndex) ?: return@launch
            val sets = state.setsByExercise[exercise.id] ?: return@launch
            val set = sets.getOrNull(state.currentSetIndex) ?: return@launch
            val updated = set.copy(restSeconds = newRestSeconds)
            repository.updateSet(updated)

            val newSets = sets.toMutableList()
            newSets[state.currentSetIndex] = updated
            val newMap = state.setsByExercise.toMutableMap()
            newMap[exercise.id] = newSets
            _uiState.value = state.copy(setsByExercise = newMap)
        }
    }

    fun incrementGuideRep() = guideManager.incrementRep()
    fun decrementGuideRep() = guideManager.decrementRep()

    fun setGuideSpeed(speed: Float) {
        val newSpeed = speed.coerceIn(0.5f, 2.0f)
        guideManager.setSpeedMultiplier(newSpeed)
        _uiState.value = _uiState.value.copy(speedMultiplier = newSpeed)
        viewModelScope.launch { appSettingsRepository.updateGuideSpeedMultiplier(newSpeed) }
    }

    private fun moveToNextSet() {
        val state = _uiState.value
        val exercise = state.exercises.getOrNull(state.currentExerciseIndex) ?: return
        val sets = state.setsByExercise[exercise.id] ?: return

        val isLastSetOfExercise = state.currentSetIndex >= sets.size - 1
        if (isLastSetOfExercise) {
            val nextExerciseIndex = state.currentExerciseIndex + 1
            if (nextExerciseIndex < state.exercises.size) {
                _uiState.value = state.copy(
                    currentExerciseIndex = nextExerciseIndex,
                    currentSetIndex = 0,
                    showSetCompleteButton = false
                )
            }
        } else {
            _uiState.value = state.copy(
                currentSetIndex = state.currentSetIndex + 1,
                showSetCompleteButton = false
            )
        }

        // Auto-start next set if enabled
        if (state.settings?.autoStartNextSet == true) {
            startSet()
        }
    }

    fun finishSession() {
        viewModelScope.launch {
            val session = _uiState.value.session ?: return@launch
            repository.updateSession(session.copy(isCompleted = true))
            _uiState.value = _uiState.value.copy(allCompleted = true)
        }
    }

    fun saveSessionFeedback(overallRating: Int, energyLevel: Int, perceivedEffort: Int, notes: String) {
        viewModelScope.launch {
            val session = _uiState.value.session ?: return@launch
            val updated = session.copy(
                overallRating = overallRating,
                energyLevel = energyLevel,
                perceivedEffort = perceivedEffort,
                sessionNotes = notes.takeIf { it.isNotBlank() }
            )
            repository.updateSession(updated)
            _uiState.value = _uiState.value.copy(session = updated)
        }
    }

    fun skipTimer() {
        timerManager.skip()
    }

    override fun onCleared() {
        super.onCleared()
        guideManager.stopGuide()
        if (::timerManager.isInitialized) {
            timerManager.release()
        }
    }

    class Factory(
        private val repository: WorkoutRepository,
        private val appSettingsRepository: AppSettingsRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return WorkoutViewModel(repository, appSettingsRepository) as T
        }
    }
}
