package com.tracklab400.app.ui.screens.stopwatch

import android.app.Application
import android.os.SystemClock
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.tracklab400.app.TrackLabApplication
import com.tracklab400.app.data.model.ExerciseType
import com.tracklab400.app.data.model.WorkoutExercise
import com.tracklab400.app.data.repository.PlanRepository
import com.tracklab400.app.data.timing.RepEntry
import com.tracklab400.app.data.timing.StopwatchCore
import com.tracklab400.app.data.timing.StopwatchPhase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

data class StopwatchUiState(
    val blockName: String = "",
    val distanceM: Int? = null,
    val expectedReps: Int? = null,
    val targetMinMs: Long? = null,
    val targetMaxMs: Long? = null,
    val phase: StopwatchPhase = StopwatchPhase.IDLE,
    val displayMs: Long = 0L,
    val currentLapMs: Long = 0L,
    val reps: List<RepEntry> = emptyList(),
    val nextRepIndex: Int = 1,
    val totalDistanceM: Int = 0,
    val averageMs: Long? = null,
    val bestMs: Long? = null,
    val slowestMs: Long? = null,
    val suggestedRestMs: Long? = null,
) {
    val allRepsRecorded: Boolean
        get() = expectedReps != null && nextRepIndex > expectedReps
}

class StopwatchViewModel(
    application: Application,
    private val weekNumber: Int,
    private val dayIndex: Int,
    private val blockIndex: Int,
) : AndroidViewModel(application) {

    private val planRepository: PlanRepository =
        (application as TrackLabApplication).container.planRepository

    private val core = StopwatchCore()

    private val _uiState = MutableStateFlow(StopwatchUiState())
    val uiState: StateFlow<StopwatchUiState> = _uiState.asStateFlow()

    private var tickerJob: Job? = null
    private var visible = false
    private var configured = false
    private var suggestedRestMs: Long? = null

    init {
        viewModelScope.launch {
            planRepository.plan.collect { plan ->
                if (configured) return@collect
                val block = plan?.weeks
                    ?.firstOrNull { it.weekNumber == weekNumber }
                    ?.days?.firstOrNull { it.dayOfWeek.value == dayIndex }
                    ?.exercises?.getOrNull(blockIndex)
                if (block != null) {
                    configured = true
                    core.configure(
                        targetMinMs = block.targetMinMs,
                        targetMaxMs = block.targetMaxMs,
                        distanceM = block.distanceM,
                    )
                    suggestedRestMs = block.restMaxMs ?: block.restMinMs
                    _uiState.value = _uiState.value.copy(
                        blockName = block.name,
                        distanceM = block.distanceM,
                        expectedReps = block.reps,
                        targetMinMs = block.targetMinMs,
                        targetMaxMs = block.targetMaxMs,
                    )
                }
            }
        }
    }

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

    fun start() {
        val now = now()
        if (core.start(now)) publish()
    }

    fun pause() {
        val now = now()
        if (core.pause(now)) publish()
    }

    fun resume() {
        val now = now()
        if (core.resume(now)) publish()
    }

    fun recordLap() {
        val now = now()
        val entry = core.recordLap(now)
        if (entry != null) {
            publish()
        }
    }

    fun finish() {
        val now = now()
        if (core.finish(now)) publish()
    }

    fun reset() {
        core.reset()
        publish()
    }

    fun correctRep(index: Int, correctedMs: Long) {
        if (core.correctRep(index, correctedMs)) publish()
    }

    private fun startTicker() {
        if (tickerJob?.isActive == true) return
        tickerJob = viewModelScope.launch {
            while (isActive) {
                publish()
                delay(if (core.phase == StopwatchPhase.RUNNING) 50L else 300L)
            }
        }
    }

    private fun publish() {
        val now = now()
        _uiState.value = _uiState.value.copy(
            phase = core.phase,
            displayMs = core.elapsed(now),
            currentLapMs = core.currentLapElapsed(now),
            reps = core.reps,
            nextRepIndex = core.nextRepIndex,
            totalDistanceM = core.totalDistanceM,
            averageMs = core.averageMs(),
            bestMs = core.bestMs(),
            slowestMs = core.slowestMs(),
            suggestedRestMs = suggestedRestMs,
        )
    }

    private fun now(): Long = SystemClock.elapsedRealtime()

    override fun onCleared() {
        tickerJob?.cancel()
        super.onCleared()
    }
}
