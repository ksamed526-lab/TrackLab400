package com.tracklab400.app.data.timing

enum class StopwatchPhase { IDLE, RUNNING, PAUSED, FINISHED }

enum class TargetComparison { BELOW, IN_RANGE, ABOVE, NONE }

data class RepEntry(
    val index: Int,
    val timeMs: Long,
    val targetMinMs: Long? = null,
    val targetMaxMs: Long? = null,
    val distanceM: Int? = null,
    val manuallyCorrected: Boolean = false,
)

/**
 * Saf kronometre durum makinesi. Zamanı dışarıdan `nowMs` (elapsedRealtime)
 * olarak alır; hiçbir Android bağımlılığı yoktur, bu sayede unit test edilebilir.
 */
class StopwatchCore {

    var phase: StopwatchPhase = StopwatchPhase.IDLE
        private set

    var reps: List<RepEntry> = emptyList()
        private set

    var nextRepIndex: Int = 1
        private set

    var totalDistanceM: Int = 0
        private set

    private var accumulatedMs: Long = 0L
    private var runStartMs: Long? = null
    private var lapBaseMs: Long = 0L
    private var targetMinMs: Long? = null
    private var targetMaxMs: Long? = null
    private var distanceM: Int? = null

    fun configure(targetMinMs: Long?, targetMaxMs: Long?, distanceM: Int?) {
        this.targetMinMs = targetMinMs
        this.targetMaxMs = targetMaxMs
        this.distanceM = distanceM
    }

    fun elapsed(nowMs: Long): Long =
        accumulatedMs + (runStartMs?.let { nowMs - it } ?: 0L)

    fun currentLapElapsed(nowMs: Long): Long = elapsed(nowMs) - lapBaseMs

    fun start(nowMs: Long): Boolean {
        if (phase != StopwatchPhase.IDLE) return false
        runStartMs = nowMs
        lapBaseMs = 0L
        phase = StopwatchPhase.RUNNING
        return true
    }

    fun pause(nowMs: Long): Boolean {
        if (phase != StopwatchPhase.RUNNING) return false
        accumulatedMs = elapsed(nowMs)
        runStartMs = null
        phase = StopwatchPhase.PAUSED
        return true
    }

    fun resume(nowMs: Long): Boolean {
        if (phase != StopwatchPhase.PAUSED) return false
        runStartMs = nowMs
        phase = StopwatchPhase.RUNNING
        return true
    }

    fun finish(nowMs: Long): Boolean {
        if (phase != StopwatchPhase.RUNNING && phase != StopwatchPhase.PAUSED) return false
        if (phase == StopwatchPhase.RUNNING) {
            accumulatedMs = elapsed(nowMs)
            runStartMs = null
        }
        phase = StopwatchPhase.FINISHED
        return true
    }

    fun reset() {
        phase = StopwatchPhase.IDLE
        accumulatedMs = 0L
        runStartMs = null
        lapBaseMs = 0L
        reps = emptyList()
        nextRepIndex = 1
        totalDistanceM = 0
    }

    fun recordLap(nowMs: Long): RepEntry? {
        if (phase != StopwatchPhase.RUNNING) return null
        val entry = RepEntry(
            index = nextRepIndex,
            timeMs = currentLapElapsed(nowMs),
            targetMinMs = targetMinMs,
            targetMaxMs = targetMaxMs,
            distanceM = distanceM,
        )
        reps = reps + entry
        totalDistanceM += distanceM ?: 0
        lapBaseMs = elapsed(nowMs)
        nextRepIndex++
        return entry
    }

    fun correctRep(index: Int, correctedMs: Long): Boolean {
        if (correctedMs <= 0L) return false
        val target = reps.firstOrNull { it.index == index } ?: return false
        reps = reps.map {
            if (it.index == index) {
                it.copy(timeMs = correctedMs, manuallyCorrected = true)
            } else {
                it
            }
        }
        return target != null
    }

    fun averageMs(): Long? {
        if (reps.isEmpty()) return null
        return reps.sumOf { it.timeMs } / reps.size
    }

    fun bestMs(): Long? = reps.minOfOrNull { it.timeMs }

    fun slowestMs(): Long? = reps.maxOfOrNull { it.timeMs }

    fun compare(timeMs: Long): TargetComparison {
        val min = targetMinMs ?: return TargetComparison.NONE
        val max = targetMaxMs ?: min
        return when {
            timeMs < min -> TargetComparison.BELOW
            timeMs <= max -> TargetComparison.IN_RANGE
            else -> TargetComparison.ABOVE
        }
    }
}
