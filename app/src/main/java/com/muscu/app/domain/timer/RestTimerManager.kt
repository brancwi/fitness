package com.muscu.app.domain.timer

import android.media.AudioManager
import android.media.ToneGenerator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class TimerState {
    object Idle : TimerState()
    data class RunningRest(val remaining: Int, val total: Int) : TimerState()
}

class RestTimerManager(
    private val scope: CoroutineScope,
    private val beepDurationMs: Int = 200,
    private val finalBeepDurationMs: Int = 400,
    private val toneVolume: Int = 100
) {

    private val _state = MutableStateFlow<TimerState>(TimerState.Idle)
    val state: StateFlow<TimerState> = _state

    private var timerJob: Job? = null
    private val toneGen = ToneGenerator(AudioManager.STREAM_MUSIC, toneVolume)

    private val _onFinished = MutableStateFlow(false)
    val onFinished: StateFlow<Boolean> = _onFinished

    init {
        _onFinished.value = false
    }

    fun start(seconds: Int) {
        timerJob?.cancel()
        _onFinished.value = false
        _state.value = TimerState.RunningRest(remaining = seconds, total = seconds)

        timerJob = scope.launch {
            var remaining = seconds
            while (remaining > 0) {
                delay(1000)
                remaining--
                _state.value = TimerState.RunningRest(remaining = remaining, total = seconds)
                if (remaining in 1..3) {
                    toneGen.startTone(ToneGenerator.TONE_PROP_BEEP, beepDurationMs)
                }
            }
            toneGen.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, finalBeepDurationMs)
            _state.value = TimerState.Idle
            _onFinished.value = true
        }
    }

    fun skip() {
        timerJob?.cancel()
        _state.value = TimerState.Idle
        _onFinished.value = true
    }

    fun resetFinishedFlag() {
        _onFinished.value = false
    }

    fun release() {
        timerJob?.cancel()
        toneGen.release()
    }
}
