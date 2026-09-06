package com.tracklab400.app.ui.screens.rest

import android.app.Application
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.SystemClock
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.tracklab400.app.data.timing.RestTimerCore
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

data class RestUiState(
    val remainingMs: Long = 0L,
    val initialMs: Long = 0L,
    val isRunning: Boolean = false,
    val finished: Boolean = false,
    val keepScreenOn: Boolean = false,
)

class RestViewModel(
    application: Application,
    initialMs: Long,
) : AndroidViewModel(application) {

    private val core = RestTimerCore(initialMs)

    private val _uiState = MutableStateFlow(
        RestUiState(remainingMs = initialMs, initialMs = initialMs),
    )
    val uiState: StateFlow<RestUiState> = _uiState.asStateFlow()

    private val toneGenerator: ToneGenerator = ToneGenerator(
        AudioManager.STREAM_NOTIFICATION,
        80,
    )
    private val vibrator: Vibrator? =
        application.getSystemService(Vibrator::class.java)

    private var tickerJob: Job? = null
    private var visible = false
    private var alertPlayed = false
    private var started = false

    fun setVisible(isVisible: Boolean) {
        visible = isVisible
        if (isVisible) {
            publish()
            startTicker()
        } else {
            tickerJob?.cancel()
            tickerJob = null
        }
    }

    fun ensureStarted() {
        if (started) return
        started = true
        core.start(now())
        publish()
    }

    fun toggleRunning() {
        val now = now()
        if (core.isRunning) {
            core.pause(now)
        } else {
            core.resume(now)
        }
        publish()
    }

    fun addSeconds(seconds: Long) {
        core.addMillis(seconds * 1000L, now())
        publish()
    }

    fun skip() {
        core.skip()
        publish()
    }

    fun setKeepScreenOn(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(keepScreenOn = enabled)
    }

    private fun startTicker() {
        if (tickerJob?.isActive == true) return
        tickerJob = viewModelScope.launch {
            while (isActive) {
                publish()
                delay(100L)
            }
        }
    }

    private fun publish() {
        val remaining = core.remaining(now())
        val finishedNow = core.finished
        _uiState.value = _uiState.value.copy(
            remainingMs = remaining,
            isRunning = core.isRunning,
            finished = finishedNow,
        )
        if (finishedNow && !alertPlayed) {
            alertPlayed = true
            playAlert()
        }
    }

    private fun playAlert() {
        try {
            toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP2, 900)
        } catch (_: RuntimeException) {
            // Ses kanalı kullanılamıyorsa sessizce geç
        }
        try {
            vibrator?.vibrate(
                VibrationEffect.createOneShot(600, VibrationEffect.DEFAULT_AMPLITUDE),
            )
        } catch (_: RuntimeException) {
            // Titreşim kullanılamıyorsa sessizce geç
        }
    }

    private fun now(): Long = SystemClock.elapsedRealtime()

    override fun onCleared() {
        tickerJob?.cancel()
        toneGenerator.release()
        super.onCleared()
    }
}
