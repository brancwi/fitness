package com.muscu.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.muscu.app.data.repository.AppSettingsRepository
import com.muscu.app.data.repository.WorkoutRepository
import com.muscu.app.domain.calculator.TempoAnalysis
import com.muscu.app.domain.calculator.TempoAnalyzer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class SettingsUiState(
    val weightKg: String = "",
    val proteinTarget: String = "150",
    val saved: Boolean = false,
    // Advanced settings
    val moderateRestSeconds: String = "90",
    val lightRestSeconds: String = "60",
    val bodyweightRestSeconds: String = "45",
    val minRestSeconds: String = "15",
    val maxRestSeconds: String = "180",
    val beepDurationMs: String = "200",
    val finalBeepDurationMs: String = "400",
    val toneVolume: String = "100",
    val lumbarRulesText: String = "",
    val advancedSaved: Boolean = false,
    // Workout automation
    val autoStartNextSet: Boolean = true,
    val autoFillRepsWeight: Boolean = true,
    val defaultReps: String = "10",
    val defaultWeightKg: String = "10",
    val tempoEccentric: String = "3",
    val tempoIsometricBottom: String = "0",
    val tempoConcentric: String = "1",
    val tempoIsometricTop: String = "1",
    val tempoProfile: String = "hypertrophie",
    val tempoAccent: String = "neutre",
    val prepCountdownSeconds: String = "5",
    val automationSaved: Boolean = false,
    val tempoAnalysis: TempoAnalysis? = null
)

class SettingsViewModel(
    private val repository: WorkoutRepository,
    private val appSettingsRepository: AppSettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState

    init {
        viewModelScope.launch {
            repository.getUserProfile().collect { profile ->
                _uiState.value = _uiState.value.copy(
                    weightKg = profile?.weightKg?.toString() ?: "",
                    proteinTarget = profile?.dailyProteinTargetGrams?.toString() ?: "150",
                    saved = false
                )
            }
        }
        viewModelScope.launch {
            appSettingsRepository.settings.collect { settings ->
                _uiState.value = _uiState.value.copy(
                    moderateRestSeconds = settings.moderateRestSeconds.toString(),
                    lightRestSeconds = settings.lightRestSeconds.toString(),
                    bodyweightRestSeconds = settings.bodyweightRestSeconds.toString(),
                    minRestSeconds = settings.minRestSeconds.toString(),
                    maxRestSeconds = settings.maxRestSeconds.toString(),
                    beepDurationMs = settings.beepDurationMs.toString(),
                    finalBeepDurationMs = settings.finalBeepDurationMs.toString(),
                    toneVolume = settings.toneVolume.toString(),
                    lumbarRulesText = settings.lumbarRulesJson,
                    autoStartNextSet = settings.autoStartNextSet,
                    autoFillRepsWeight = settings.autoFillRepsWeight,
                    defaultReps = settings.defaultReps.toString(),
                    defaultWeightKg = settings.defaultWeightKg.toString(),
                    tempoEccentric = settings.tempoEccentric.toString(),
                    tempoIsometricBottom = settings.tempoIsometricBottom.toString(),
                    tempoConcentric = settings.tempoConcentric.toString(),
                    tempoIsometricTop = settings.tempoIsometricTop.toString(),
                    tempoProfile = settings.tempoProfile,
                    tempoAccent = settings.tempoAccent,
                    prepCountdownSeconds = settings.prepCountdownSeconds.toString(),
                    tempoAnalysis = TempoAnalyzer.analyze(settings, settings.defaultReps)
                )
            }
        }
    }

    fun updateWeight(value: String) {
        _uiState.value = _uiState.value.copy(weightKg = value, saved = false)
    }

    fun updateTarget(value: String) {
        _uiState.value = _uiState.value.copy(proteinTarget = value, saved = false)
    }

    fun save() {
        viewModelScope.launch {
            val w = _uiState.value.weightKg.toFloatOrNull() ?: 80f
            val t = _uiState.value.proteinTarget.toIntOrNull() ?: 150
            repository.saveUserProfile(w, t)
            _uiState.value = _uiState.value.copy(saved = true)
        }
    }

    // Advanced settings updaters
    fun updateModerateRest(value: String) { _uiState.value = _uiState.value.copy(moderateRestSeconds = value, advancedSaved = false) }
    fun updateLightRest(value: String) { _uiState.value = _uiState.value.copy(lightRestSeconds = value, advancedSaved = false) }
    fun updateBodyweightRest(value: String) { _uiState.value = _uiState.value.copy(bodyweightRestSeconds = value, advancedSaved = false) }
    fun updateMinRest(value: String) { _uiState.value = _uiState.value.copy(minRestSeconds = value, advancedSaved = false) }
    fun updateMaxRest(value: String) { _uiState.value = _uiState.value.copy(maxRestSeconds = value, advancedSaved = false) }
    fun updateBeepDuration(value: String) { _uiState.value = _uiState.value.copy(beepDurationMs = value, advancedSaved = false) }
    fun updateFinalBeepDuration(value: String) { _uiState.value = _uiState.value.copy(finalBeepDurationMs = value, advancedSaved = false) }
    fun updateToneVolume(value: String) { _uiState.value = _uiState.value.copy(toneVolume = value, advancedSaved = false) }
    fun updateLumbarRules(value: String) { _uiState.value = _uiState.value.copy(lumbarRulesText = value, advancedSaved = false) }

    fun saveAdvanced() {
        viewModelScope.launch {
            val s = _uiState.value
            appSettingsRepository.updateRestTimes(
                moderate = s.moderateRestSeconds.toIntOrNull() ?: 90,
                light = s.lightRestSeconds.toIntOrNull() ?: 60,
                bodyweight = s.bodyweightRestSeconds.toIntOrNull() ?: 45
            )
            appSettingsRepository.updateRestSliderRange(
                min = s.minRestSeconds.toIntOrNull() ?: 15,
                max = s.maxRestSeconds.toIntOrNull() ?: 180
            )
            appSettingsRepository.updateAudioPrefs(
                beepMs = s.beepDurationMs.toIntOrNull() ?: 200,
                finalBeepMs = s.finalBeepDurationMs.toIntOrNull() ?: 400,
                volume = s.toneVolume.toIntOrNull() ?: 100
            )
            val rules = s.lumbarRulesText.lines().filter { it.isNotBlank() }
            if (rules.isNotEmpty()) {
                appSettingsRepository.updateLumbarRules(rules)
            }
            _uiState.value = s.copy(advancedSaved = true)
        }
    }

    // Automation settings
    fun toggleAutoStart(enabled: Boolean) { _uiState.value = _uiState.value.copy(autoStartNextSet = enabled, automationSaved = false) }
    fun toggleAutoFill(enabled: Boolean) { _uiState.value = _uiState.value.copy(autoFillRepsWeight = enabled, automationSaved = false) }
    fun updateDefaultReps(value: String) { _uiState.value = _uiState.value.copy(defaultReps = value, automationSaved = false) }
    fun updateDefaultWeight(value: String) { _uiState.value = _uiState.value.copy(defaultWeightKg = value, automationSaved = false) }
    fun updateTempoEccentric(value: String) {
        _uiState.value = _uiState.value.copy(tempoEccentric = value, automationSaved = false)
        recomputeAnalysis()
    }
    fun updateTempoIsoBottom(value: String) {
        _uiState.value = _uiState.value.copy(tempoIsometricBottom = value, automationSaved = false)
        recomputeAnalysis()
    }
    fun updateTempoConcentric(value: String) {
        _uiState.value = _uiState.value.copy(tempoConcentric = value, automationSaved = false)
        recomputeAnalysis()
    }
    fun updateTempoIsoTop(value: String) {
        _uiState.value = _uiState.value.copy(tempoIsometricTop = value, automationSaved = false)
        recomputeAnalysis()
    }
    fun updatePrepCountdown(value: String) { _uiState.value = _uiState.value.copy(prepCountdownSeconds = value, automationSaved = false) }

    fun updateTempoProfile(value: String) {
        _uiState.value = _uiState.value.copy(tempoProfile = value, automationSaved = false)
        applyPreset()
    }

    fun updateTempoAccent(value: String) {
        _uiState.value = _uiState.value.copy(tempoAccent = value, automationSaved = false)
        applyPreset()
    }

    private fun applyPreset() {
        val s = _uiState.value
        val base = when (s.tempoProfile) {
            "puissance" -> listOf(1, 0, 1, 0)
            "force" -> listOf(2, 0, 1, 0)
            "endurance" -> listOf(4, 1, 2, 1)
            else -> listOf(3, 0, 1, 1) // hypertrophie
        }
        val adjusted = when (s.tempoAccent) {
            "excentrique" -> listOf(base[0] + 1, base[1] + 1, base[2], base[3])
            "concentrique" -> listOf(kotlin.math.max(1, base[0] - 1), base[1], base[2] + 1, base[3])
            else -> base
        }
        _uiState.value = s.copy(
            tempoEccentric = adjusted[0].toString(),
            tempoIsometricBottom = adjusted[1].toString(),
            tempoConcentric = adjusted[2].toString(),
            tempoIsometricTop = adjusted[3].toString()
        )
        recomputeAnalysis()
    }

    private fun recomputeAnalysis() {
        val s = _uiState.value
        val settings = com.muscu.app.data.model.AppSettings(
            tempoEccentric = s.tempoEccentric.toIntOrNull() ?: 3,
            tempoIsometricBottom = s.tempoIsometricBottom.toIntOrNull() ?: 0,
            tempoConcentric = s.tempoConcentric.toIntOrNull() ?: 1,
            tempoIsometricTop = s.tempoIsometricTop.toIntOrNull() ?: 1,
            defaultReps = s.defaultReps.toIntOrNull() ?: 10
        )
        _uiState.value = s.copy(
            tempoAnalysis = TempoAnalyzer.analyze(settings, settings.defaultReps)
        )
    }

    fun saveAutomation() {
        viewModelScope.launch {
            val s = _uiState.value
            appSettingsRepository.updateAutoStart(s.autoStartNextSet)
            appSettingsRepository.updateAutoFill(
                enabled = s.autoFillRepsWeight,
                defaultReps = s.defaultReps.toIntOrNull() ?: 10,
                defaultWeightKg = s.defaultWeightKg.toFloatOrNull() ?: 10f
            )
            appSettingsRepository.updateTempo(
                eccentric = s.tempoEccentric.toIntOrNull() ?: 3,
                isoBottom = s.tempoIsometricBottom.toIntOrNull() ?: 0,
                concentric = s.tempoConcentric.toIntOrNull() ?: 1,
                isoTop = s.tempoIsometricTop.toIntOrNull() ?: 1
            )
            appSettingsRepository.updateTempoAccent(s.tempoAccent)
            appSettingsRepository.updateTempoProfile(s.tempoProfile)
            appSettingsRepository.updatePrepCountdown(s.prepCountdownSeconds.toIntOrNull() ?: 5)
            _uiState.value = s.copy(automationSaved = true)
        }
    }

    class Factory(
        private val repository: WorkoutRepository,
        private val appSettingsRepository: AppSettingsRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SettingsViewModel(repository, appSettingsRepository) as T
        }
    }
}
