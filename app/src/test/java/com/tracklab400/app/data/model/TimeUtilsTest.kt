package com.tracklab400.app.data.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import kotlin.math.roundToLong

class TimeUtilsTest {

    @Test
    fun parsesPlainSeconds() {
        val value = TimeUtils.parseSeconds("76")
        assertEquals(76_000L, (value!! * 1000).roundToLong())
    }

    @Test
    fun parsesDecimalWithDot() {
        val value = TimeUtils.parseSeconds("15.5")
        assertEquals(15_500L, (value!! * 1000).roundToLong())
    }

    @Test
    fun parsesDecimalWithComma() {
        val value = TimeUtils.parseSeconds("15,5")
        assertEquals(15_500L, (value!! * 1000).roundToLong())
    }

    @Test
    fun rejectsEmptyInput() {
        assertNull(TimeUtils.parseSeconds(""))
        assertNull(TimeUtils.parseSeconds("   "))
    }

    @Test
    fun rejectsNonNumericInput() {
        assertNull(TimeUtils.parseSeconds("abc"))
        assertNull(TimeUtils.parseSeconds("76sn"))
    }

    @Test
    fun parsesNegativeForValidator() {
        val value = TimeUtils.parseSeconds("-5")
        assertEquals(-5.0, value!!, 0.001)
    }

    @Test
    fun formatsSecondsUnderMinute() {
        assertEquals("15,0", TimeUtils.formatMs(15_000L))
        assertEquals("8,5", TimeUtils.formatMs(8_500L))
    }

    @Test
    fun formatsSecondsOverMinute() {
        assertEquals("1:16,0", TimeUtils.formatMs(76_000L))
        assertEquals("1:08,5", TimeUtils.formatMs(68_500L))
        assertEquals("1:00,0", TimeUtils.formatMs(60_000L))
    }
}
