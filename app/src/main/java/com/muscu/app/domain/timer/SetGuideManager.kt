package com.muscu.app.domain.timer

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

enum class GuidePhase {
    Idle,
    Eccentric,      // descente / allongement
    IsometricBottom,// pause basse
    Concentric,     // montée / contraction
    IsometricTop    // pause haute
}

data class GuideState(
    val phase: GuidePhase = GuidePhase.Idle,
    val repCount: Int = 0,
    val targetReps: Int = 10,
    val phaseProgress: Float = 0f,   // 0..1 within current phase
    val cycleProgress: Float = 0f,   // 0..1 over entire cycle
    val elapsedMs: Long = 0L         // temps total de la série
)

class SetGuideManager(private val scope: CoroutineScope) {

    private val _state = MutableStateFlow(GuideState())
    val state: StateFlow<GuideState> = _state

    private var guideJob: Job? = null
    private var startTime: Long = 0L
    private var speedMultiplier: Float = 1.0f

    fun setSpeedMultiplier(multiplier: Float) {
        speedMultiplier = multiplier.coerceIn(0.5f, 2.0f)
    }

    fun startGuide(targetReps: Int, tempo: TempoConfig) {
        guideJob?.cancel()
        _state.value = GuideState(targetReps = targetReps)
        startTime = System.currentTimeMillis()

        guideJob = scope.launch {
            while (true) {
                val realElapsed = System.currentTimeMillis() - startTime
                val elapsed = (realElapsed * speedMultiplier).toLong()
                _state.value = _state.value.copy(elapsedMs = realElapsed)

                val cycleDuration = tempo.totalSeconds * 1000L
                if (cycleDuration > 0) {
                    val cycleElapsed = elapsed % cycleDuration
                    val (phase, phaseProgress, cycleProgress) = tempo.phaseAt(cycleElapsed)
                    _state.value = _state.value.copy(
                        phase = phase,
                        phaseProgress = phaseProgress,
                        cycleProgress = cycleProgress
                    )
                }

                delay(50)
            }
        }
    }

    fun incrementRep() {
        val current = _state.value
        if (current.repCount < current.targetReps) {
            _state.value = current.copy(repCount = current.repCount + 1)
        }
    }

    fun decrementRep() {
        val current = _state.value
        if (current.repCount > 0) {
            _state.value = current.copy(repCount = current.repCount - 1)
        }
    }

    fun stopGuide() {
        guideJob?.cancel()
        _state.value = _state.value.copy(phase = GuidePhase.Idle, phaseProgress = 0f, cycleProgress = 0f)
    }

    data class TempoConfig(
        val eccentricSeconds: Int,
        val isometricBottomSeconds: Int,
        val concentricSeconds: Int,
        val isometricTopSeconds: Int
    ) {
        val totalSeconds: Int = eccentricSeconds + isometricBottomSeconds + concentricSeconds + isometricTopSeconds

        fun phaseAt(elapsedMs: Long): Triple<GuidePhase, Float, Float> {
            var remaining = elapsedMs
            val totalMs = totalSeconds * 1000L
            val eccMs = eccentricSeconds * 1000L
            if (remaining < eccMs) {
                val progress = (remaining / eccMs.toFloat()).coerceIn(0f, 1f)
                val cycleProgress = (remaining / totalMs.toFloat()).coerceIn(0f, 1f)
                return Triple(GuidePhase.Eccentric, progress, cycleProgress)
            }
            remaining -= eccMs

            val isoBotMs = isometricBottomSeconds * 1000L
            if (remaining < isoBotMs) {
                val progress = (remaining / isoBotMs.toFloat()).coerceIn(0f, 1f)
                val cycleProgress = ((eccMs + remaining) / totalMs.toFloat()).coerceIn(0f, 1f)
                return Triple(GuidePhase.IsometricBottom, progress, cycleProgress)
            }
            remaining -= isoBotMs

            val conMs = concentricSeconds * 1000L
            if (remaining < conMs) {
                val progress = (remaining / conMs.toFloat()).coerceIn(0f, 1f)
                val cycleProgress = ((eccMs + isoBotMs + remaining) / totalMs.toFloat()).coerceIn(0f, 1f)
                return Triple(GuidePhase.Concentric, progress, cycleProgress)
            }
            remaining -= conMs

            val isoTopMs = isometricTopSeconds * 1000L
            val progress = (remaining / isoTopMs.toFloat()).coerceIn(0f, 1f)
            val cycleProgress = ((eccMs + isoBotMs + conMs + remaining) / totalMs.toFloat()).coerceIn(0f, 1f)
            return Triple(GuidePhase.IsometricTop, progress, cycleProgress)
        }
    }
}
