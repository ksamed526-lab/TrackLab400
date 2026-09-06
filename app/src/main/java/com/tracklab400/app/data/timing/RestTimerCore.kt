package com.tracklab400.app.data.timing

/**
 * Saf geri sayım motoru. Zamanı dışarıdan `nowMs` (elapsedRealtime) olarak alır.
 */
class RestTimerCore(initialMs: Long) {

    val initial: Long = initialMs.coerceAtLeast(0L)

    var finished: Boolean = false
        private set

    var isRunning: Boolean = false
        private set

    private var remainingMs: Long = this.initial
    private var endAtMs: Long? = null

    fun start(nowMs: Long): Boolean {
        if (isRunning || finished) return false
        endAtMs = nowMs + remainingMs
        isRunning = true
        return true
    }

    fun pause(nowMs: Long): Boolean {
        if (!isRunning) return false
        val left = (endAtMs!! - nowMs).coerceAtLeast(0L)
        remainingMs = left
        endAtMs = null
        isRunning = false
        if (left <= 0L) finished = true
        return true
    }

    fun resume(nowMs: Long): Boolean = start(nowMs)

    fun remaining(nowMs: Long): Long {
        if (finished) return 0L
        if (!isRunning) return remainingMs
        val left = (endAtMs!! - nowMs).coerceAtLeast(0L)
        if (left <= 0L) {
            finished = true
            isRunning = false
            remainingMs = 0L
            endAtMs = null
        }
        return left
    }

    fun addMillis(deltaMs: Long, nowMs: Long): Long {
        if (finished) return 0L
        if (isRunning) {
            endAtMs = (endAtMs!! + deltaMs).coerceAtLeast(nowMs)
        } else {
            remainingMs = (remainingMs + deltaMs).coerceAtLeast(0L)
        }
        return remaining(nowMs)
    }

    fun skip() {
        finished = true
        isRunning = false
        remainingMs = 0L
        endAtMs = null
    }
}
