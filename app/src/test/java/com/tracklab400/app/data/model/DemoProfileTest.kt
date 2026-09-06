package com.tracklab400.app.data.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DemoProfileTest {

    @Test
    fun demoConstantsMatchSpec() {
        assertEquals(76_000L, DemoProfile.CURRENT_400_MS)
        assertEquals(15_000L, DemoProfile.CURRENT_100_MS)
        assertEquals(68_000L, DemoProfile.TARGET_400_MS)
    }

    @Test
    fun demoProfileIsMarkedAsDemo() {
        val profile = DemoProfile.build()
        assertTrue(profile.isDemo)
        assertEquals(400, profile.targetDistanceM)
        assertEquals(StopwatchType.EL_KRONOMETRESI, profile.stopwatchType)
        assertEquals("Demo Koşucu", profile.nickname)
    }

    @Test
    fun demoProfilePassesValidation() {
        val state = com.tracklab400.app.ui.screens.profile.ProfileFormState.from(
            DemoProfile.build(),
        )
        assertTrue(ProfileValidator.validate(state).isEmpty())
    }
}
