package com.tracklab400.app.data.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class RecordModelsTest {

    private fun rep(
        index: Int,
        actualMs: Long,
        targetMinMs: Long? = 48_000L,
        targetMaxMs: Long? = 52_000L,
    ) = RepRecord(
        repIndex = index,
        distanceM = 300,
        targetMinMs = targetMinMs,
        targetMaxMs = targetMaxMs,
        actualMs = actualMs,
    )

    private fun session(
        completedReps: Int,
        expectedReps: Int,
        reps: List<RepRecord>,
    ) = SessionRecord(
        weekNumber = 1,
        dayOfWeek = 4,
        sessionKind = "TEMPO",
        completedAt = 1_000L,
        status = SessionStatus.COMPLETED,
        completedReps = completedReps,
        expectedReps = expectedReps,
        reps = reps,
    )

    @Test
    fun completionPercent_isCompletedOverExpected() {
        val s = session(completedReps = 3, expectedReps = 4, reps = emptyList())
        assertEquals(0.75f, s.completionPercent, 0.0001f)
    }

    @Test
    fun completionPercent_isZeroWhenExpectedZero() {
        val s = session(completedReps = 0, expectedReps = 0, reps = emptyList())
        assertEquals(0f, s.completionPercent, 0.0001f)
    }

    @Test
    fun averageRepMs_isNullWhenNoReps() {
        val s = session(completedReps = 0, expectedReps = 4, reps = emptyList())
        assertNull(s.averageRepMs)
    }

    @Test
    fun averageRepMs_isMeanOfActualTimes() {
        val reps = listOf(rep(1, 50_000L), rep(2, 46_000L), rep(3, 48_000L))
        val s = session(completedReps = 3, expectedReps = 3, reps = reps)
        assertEquals(48_000L, s.averageRepMs!!)
    }

    @Test
    fun bestRepMs_isFastestRep() {
        val reps = listOf(rep(1, 50_000L), rep(2, 46_000L), rep(3, 48_000L))
        val s = session(completedReps = 3, expectedReps = 3, reps = reps)
        assertEquals(46_000L, s.bestRepMs!!)
    }

    @Test
    fun slowestRepMs_isSlowestRep() {
        val reps = listOf(rep(1, 50_000L), rep(2, 46_000L), rep(3, 48_000L))
        val s = session(completedReps = 3, expectedReps = 3, reps = reps)
        assertEquals(50_000L, s.slowestRepMs!!)
    }

    @Test
    fun avgDeviationMs_isNullWhenNoReps() {
        val s = session(completedReps = 0, expectedReps = 2, reps = emptyList())
        assertNull(s.avgDeviationMs)
    }

    @Test
    fun avgDeviationMs_isMeanDeviationFromTargetMin() {
        val reps = listOf(rep(1, 50_000L), rep(2, 46_000L), rep(3, 48_000L))
        val s = session(completedReps = 3, expectedReps = 3, reps = reps)
        // sapmalar: +2000, -2000, 0 -> ortalama 0
        assertEquals(0L, s.avgDeviationMs!!)
    }

    @Test
    fun repDeviation_usesTargetMinWhenAvailable() {
        val r = rep(1, 50_000L)
        assertEquals(2_000L, r.deviationMs!!)
    }

    @Test
    fun repDeviation_usesTargetMaxWhenNoTargetMin() {
        val r = RepRecord(
            repIndex = 1,
            distanceM = 300,
            targetMinMs = null,
            targetMaxMs = 52_000L,
            actualMs = 50_000L,
        )
        assertEquals(-2_000L, r.deviationMs!!)
    }

    @Test
    fun repDeviation_isNullWhenNoTarget() {
        val r = RepRecord(
            repIndex = 1,
            distanceM = 300,
            targetMinMs = null,
            targetMaxMs = null,
            actualMs = 50_000L,
        )
        assertNull(r.deviationMs)
    }
}