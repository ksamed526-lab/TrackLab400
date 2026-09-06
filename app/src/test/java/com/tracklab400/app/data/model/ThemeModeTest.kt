package com.tracklab400.app.data.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ThemeModeTest {

    @Test
    fun fromStoredValue_knownValues_roundTrip() {
        assertEquals(ThemeMode.SYSTEM, ThemeMode.fromStoredValue("system"))
        assertEquals(ThemeMode.LIGHT, ThemeMode.fromStoredValue("light"))
        assertEquals(ThemeMode.DARK, ThemeMode.fromStoredValue("dark"))
        assertEquals(ThemeMode.PINK, ThemeMode.fromStoredValue("pink"))
    }

    @Test
    fun fromStoredValue_unknownOrNull_defaultsToSystem() {
        assertEquals(ThemeMode.SYSTEM, ThemeMode.fromStoredValue("night"))
        assertEquals(ThemeMode.SYSTEM, ThemeMode.fromStoredValue(""))
        assertEquals(ThemeMode.SYSTEM, ThemeMode.fromStoredValue(null))
    }

    @Test
    fun resolveDarkTheme_system_followsSystem() {
        assertTrue(resolveDarkTheme(ThemeMode.SYSTEM, systemDark = true))
        assertFalse(resolveDarkTheme(ThemeMode.SYSTEM, systemDark = false))
    }

    @Test
    fun resolveDarkTheme_light_alwaysLight() {
        assertFalse(resolveDarkTheme(ThemeMode.LIGHT, systemDark = true))
        assertFalse(resolveDarkTheme(ThemeMode.LIGHT, systemDark = false))
    }

    @Test
    fun resolveDarkTheme_dark_alwaysDark() {
        assertTrue(resolveDarkTheme(ThemeMode.DARK, systemDark = true))
        assertTrue(resolveDarkTheme(ThemeMode.DARK, systemDark = false))
    }

    @Test
    fun resolveDarkTheme_pink_followsSystem() {
        assertTrue(resolveDarkTheme(ThemeMode.PINK, systemDark = true))
        assertFalse(resolveDarkTheme(ThemeMode.PINK, systemDark = false))
    }
}