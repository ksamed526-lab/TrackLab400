package com.tracklab400.app.data.timing

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RestTimerCoreTest {

    @Test
    fun countdownReachesZeroAndFinishes() {
        val core = RestTimerCore(10_000L)
        core.start(0L)
        assertEquals(10_000L, core.remaining(0L))
        assertEquals(6_000L, core.remaining(4_000L))
        assertEquals(0L, core.remaining(11_000L))
        assertTrue(core.finished)
        assertFalse(core.isRunning)
    }

    @Test
    fun pauseFreezesRemainingTime() {
        val core = RestTimerCore(10_000L)
        core.start(0L)
        core.pause(4_000L)
        assertTrue(core.isRunning.not())
        assertEquals(6_000L, core.remaining(4_000L))
        assertEquals(6_000L, core.remaining(90_000L))
    }

    @Test
    fun resumeContinuesFromRemaining() {
        val core = RestTimerCore(10_000L)
        core.start(0L)
        core.pause(4_000L)
        core.resume(9_000L)
        assertEquals(4_000L, core.remaining(11_000L))
    }

    @Test
    fun addTenSecondsExtendsCountdown() {
        val core = RestTimerCore(10_000L)
        core.start(0L)
        core.addMillis(10_000L, 2_000L)
        assertEquals(18_000L, core.remaining(2_000L))
    }

    @Test
    fun removeTenSecondsShortensCountdown() {
        val core = RestTimerCore(30_000L)
        core.start(0L)
        core.addMillis(-10_000L, 2_000L)
        assertEquals(18_000L, core.remaining(2_000L))
    }

    @Test
    fun remainingNeverGoesNegativeWhenReduced() {
        val core = RestTimerCore(5_000L)
        core.start(0L)
        core.addMillis(-10_000L, 1_000L)
        assertEquals(0L, core.remaining(1_000L))
        assertTrue(core.finished)
    }

    @Test
    fun skipFinishesImmediately() {
        val core = RestTimerCore(60_000L)
        core.start(0L)
        core.skip()
        assertTrue(core.finished)
        assertEquals(0L, core.remaining(0L))
    }

    @Test
    fun startTwiceFails() {
        val core = RestTimerCore(10_000L)
        assertTrue(core.start(0L))
        assertFalse(core.start(1_000L))
    }

    @Test
    fun pausedCountdownCanBeAdjusted() {
        val core = RestTimerCore(20_000L)
        core.start(0L)
        core.pause(5_000L)
        assertEquals(15_000L, core.remaining(5_000L))
        core.addMillis(10_000L, 5_000L)
        assertEquals(25_000L, core.remaining(5_000L))
    }
}
