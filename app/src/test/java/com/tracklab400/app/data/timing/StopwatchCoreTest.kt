package com.tracklab400.app.data.timing

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class StopwatchCoreTest {

    @Test
    fun startBeginsRunningAndElapsedAdvances() {
        val core = StopwatchCore()
        assertTrue(core.start(1_000L))
        assertEquals(StopwatchPhase.RUNNING, core.phase)
        assertEquals(0L, core.elapsed(1_000L))
        assertEquals(5_000L, core.elapsed(6_000L))
    }

    @Test
    fun startTwiceFails() {
        val core = StopwatchCore()
        core.start(1_000L)
        assertFalse(core.start(2_000L))
    }

    @Test
    fun pauseFreezesElapsedTime() {
        val core = StopwatchCore()
        core.start(1_000L)
        core.pause(6_000L)
        assertEquals(StopwatchPhase.PAUSED, core.phase)
        assertEquals(5_000L, core.elapsed(6_000L))
        assertEquals(5_000L, core.elapsed(60_000L))
    }

    @Test
    fun resumeKeepsTimeAccurateAcrossGap() {
        val core = StopwatchCore()
        core.start(1_000L)
        core.pause(6_000L)
        core.resume(10_000L)
        assertEquals(StopwatchPhase.RUNNING, core.phase)
        assertEquals(7_000L, core.elapsed(12_000L))
    }

    @Test
    fun lapRecordsRepTimesAndResetsLapBase() {
        val core = StopwatchCore()
        core.configure(targetMinMs = 55_000L, targetMaxMs = 60_000L, distanceM = 300)
        core.start(1_000L)

        val first = core.recordLap(59_000L)
        assertEquals(1, first!!.index)
        assertEquals(58_000L, first.timeMs)
        assertEquals(55_000L, first.targetMinMs)
        assertEquals(300, first.distanceM)

        val second = core.recordLap(123_000L)
        assertEquals(2, second!!.index)
        assertEquals(64_000L, second.timeMs)

        assertEquals(2, core.reps.size)
        assertEquals(3, core.nextRepIndex)
    }

    @Test
    fun lapNotAllowedWhenNotRunning() {
        val core = StopwatchCore()
        assertNull(core.recordLap(5_000L))
        core.start(1_000L)
        core.pause(3_000L)
        assertNull(core.recordLap(4_000L))
    }

    @Test
    fun finishStopsAndFreezesTotal() {
        val core = StopwatchCore()
        core.start(1_000L)
        core.finish(11_000L)
        assertEquals(StopwatchPhase.FINISHED, core.phase)
        assertEquals(10_000L, core.elapsed(11_000L))
        assertEquals(10_000L, core.elapsed(99_000L))
    }

    @Test
    fun totalTimeAcrossSegmentsIsCorrect() {
        val core = StopwatchCore()
        core.start(1_000L)
        core.pause(6_000L)
        core.resume(9_000L)
        core.finish(14_000L)
        assertEquals(10_000L, core.elapsed(14_000L))
    }

    @Test
    fun resetClearsEverything() {
        val core = StopwatchCore()
        core.configure(targetMinMs = 55_000L, targetMaxMs = 60_000L, distanceM = 300)
        core.start(1_000L)
        core.recordLap(59_000L)
        core.finish(61_000L)
        core.reset()

        assertEquals(StopwatchPhase.IDLE, core.phase)
        assertEquals(0L, core.elapsed(70_000L))
        assertTrue(core.reps.isEmpty())
        assertEquals(1, core.nextRepIndex)
        assertEquals(0, core.totalDistanceM)
    }

    @Test
    fun manualCorrectionUpdatesRepAndStats() {
        val core = StopwatchCore()
        core.configure(targetMinMs = null, targetMaxMs = null, distanceM = 100)
        core.start(1_000L)
        core.recordLap(21_000L)
        core.recordLap(42_000L)

        assertTrue(core.correctRep(1, 19_500L))
        assertFalse(core.correctRep(1, 0L))
        assertFalse(core.correctRep(9, 20_000L))

        val corrected = core.reps.first { it.index == 1 }
        assertEquals(19_500L, corrected.timeMs)
        assertTrue(corrected.manuallyCorrected)
        assertEquals(19_500L, core.bestMs())
        assertEquals(21_000L, core.slowestMs())
    }

    @Test
    fun statsAreComputedCorrectly() {
        val core = StopwatchCore()
        core.start(1_000L)
        core.recordLap(21_000L)
        core.recordLap(42_000L)
        core.recordLap(64_000L)

        assertEquals(21_000L, core.averageMs())
        assertEquals(20_000L, core.bestMs())
        assertEquals(22_000L, core.slowestMs())
    }

    @Test
    fun totalDistanceAccumulatesPerRep() {
        val core = StopwatchCore()
        core.configure(targetMinMs = null, targetMaxMs = null, distanceM = 100)
        core.start(1_000L)
        core.recordLap(21_000L)
        core.recordLap(41_000L)
        core.recordLap(61_000L)
        assertEquals(300, core.totalDistanceM)
    }

    @Test
    fun comparisonMatchesTargetRange() {
        val core = StopwatchCore()
        core.configure(targetMinMs = 58_000L, targetMaxMs = 60_000L, distanceM = 300)

        assertEquals(TargetComparison.BELOW, core.compare(57_500L))
        assertEquals(TargetComparison.IN_RANGE, core.compare(58_000L))
        assertEquals(TargetComparison.IN_RANGE, core.compare(60_000L))
        assertEquals(TargetComparison.ABOVE, core.compare(60_500L))
    }

    @Test
    fun comparisonWithoutTargetIsNone() {
        val core = StopwatchCore()
        assertEquals(TargetComparison.NONE, core.compare(10_000L))
    }
}
