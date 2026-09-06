package com.tracklab400.app.data.plan

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class PaceCalculatorTest {

    @Test
    fun scales300mFor68SecondTarget() {
        val range = PaceCalculator.scale(68_000L, PaceFactor(0.853, 0.882))
        assertEquals(58_000L, range.first)
        assertEquals(60_000L, range.last)
    }

    @Test
    fun scales100mTempoFor68SecondTarget() {
        val range = PaceCalculator.scale(68_000L, PaceFactor(0.280, 0.310))
        assertEquals(19_000L, range.first)
        assertEquals(21_000L, range.last)
    }

    @Test
    fun scales350mFor68SecondTarget() {
        val range = PaceCalculator.scale(68_000L, PaceFactor(0.897, 0.926))
        assertEquals(61_000L, range.first)
        assertEquals(63_000L, range.last)
    }

    @Test
    fun raceSplitsMatchPdfFor68Seconds() {
        val splits = PaceCalculator.raceSplits(68_000L)
        val first100 = splits.first { it.distanceM == 100 }
        val at200 = splits.first { it.distanceM == 200 }
        val at300 = splits.first { it.distanceM == 300 }
        val at400 = splits.first { it.distanceM == 400 }

        assertEquals(16_500L, first100.minMs)
        assertEquals(17_000L, first100.maxMs)
        assertEquals(33_000L, at200.minMs)
        assertEquals(34_000L, at200.maxMs)
        assertEquals(50_500L, at300.minMs)
        assertEquals(51_500L, at300.maxMs)
        assertEquals(68_000L, at400.minMs)
        assertEquals(68_000L, at400.maxMs)
    }

    @Test
    fun targetsScaleDifferentlyForDifferentTargetTimes() {
        val range68 = PaceCalculator.scale(68_000L, PaceFactor(0.853, 0.882))
        val range70 = PaceCalculator.scale(70_000L, PaceFactor(0.853, 0.882))
        assertNotEquals(range68, range70)
        assertEquals(59_500L, range70.first)
        assertEquals(61_500L, range70.last)
    }

    @Test
    fun roundingTo500ms() {
        assertEquals(58_000L, PaceCalculator.roundTo500(58_004.0))
        assertEquals(60_000L, PaceCalculator.roundTo500(59_976.0))
        assertEquals(19_000L, PaceCalculator.roundTo500(18_999.2))
    }
}
