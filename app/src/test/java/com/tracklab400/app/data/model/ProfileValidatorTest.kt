package com.tracklab400.app.data.model

import com.tracklab400.app.ui.screens.profile.ProfileFormState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ProfileValidatorTest {

    private fun validState() = ProfileFormState(
        current100 = "15",
        current400 = "76",
        targetDistance = 400,
        targetTime = "68",
        trackAccess = TrackAccess.PIST_VAR,
        gymStatus = GymStatus.SALON_VAR,
        experience = TrainingExperience.ORTA,
        stopwatchType = StopwatchType.EL_KRONOMETRESI,
    )

    @Test
    fun emptyFormHasAllRequiredErrors() {
        val errors = ProfileValidator.validate(ProfileFormState())
        assertEquals(ProfileFieldError.EMPTY, errors[ProfileField.CURRENT_400])
        assertEquals(ProfileFieldError.EMPTY, errors[ProfileField.TARGET_TIME])
        assertEquals(ProfileFieldError.EMPTY, errors[ProfileField.TRACK_ACCESS])
        assertEquals(ProfileFieldError.EMPTY, errors[ProfileField.GYM_STATUS])
        assertEquals(ProfileFieldError.EMPTY, errors[ProfileField.EXPERIENCE])
        assertEquals(ProfileFieldError.EMPTY, errors[ProfileField.STOPWATCH_TYPE])
    }

    @Test
    fun negativeTimeRejected() {
        val errors = ProfileValidator.validate(validState().copy(current400 = "-5"))
        assertEquals(ProfileFieldError.NON_POSITIVE, errors[ProfileField.CURRENT_400])
    }

    @Test
    fun zeroTimeRejected() {
        val errors = ProfileValidator.validate(validState().copy(targetTime = "0"))
        assertEquals(ProfileFieldError.NON_POSITIVE, errors[ProfileField.TARGET_TIME])
    }

    @Test
    fun invalidTextRejected() {
        val errors = ProfileValidator.validate(validState().copy(current400 = "abc"))
        assertEquals(ProfileFieldError.INVALID, errors[ProfileField.CURRENT_400])
    }

    @Test
    fun outOfRangeRejected() {
        val tooSlow = ProfileValidator.validate(validState().copy(current400 = "200"))
        assertEquals(ProfileFieldError.OUT_OF_RANGE, tooSlow[ProfileField.CURRENT_400])

        val tooFast = ProfileValidator.validate(validState().copy(current100 = "8"))
        assertEquals(ProfileFieldError.OUT_OF_RANGE, tooFast[ProfileField.CURRENT_100])
    }

    @Test
    fun targetNotBelowCurrentRejected() {
        val slower = ProfileValidator.validate(validState().copy(targetTime = "80"))
        assertEquals(ProfileFieldError.NOT_BELOW_CURRENT, slower[ProfileField.TARGET_TIME])

        val equal = ProfileValidator.validate(validState().copy(targetTime = "76"))
        assertEquals(ProfileFieldError.NOT_BELOW_CURRENT, equal[ProfileField.TARGET_TIME])
    }

    @Test
    fun targetBelowCurrentAccepted() {
        val errors = ProfileValidator.validate(validState())
        assertTrue(errors[ProfileField.TARGET_TIME] == null)
    }

    @Test
    fun targetCheckedAgainstSameDistanceCurrent() {
        val state = validState().copy(targetDistance = 100, targetTime = "16")
        val errors = ProfileValidator.validate(state)
        assertEquals(ProfileFieldError.NOT_BELOW_CURRENT, errors[ProfileField.TARGET_TIME])
    }

    @Test
    fun invalidAgeRejected() {
        val nonNumeric = ProfileValidator.validate(validState().copy(age = "abc"))
        assertEquals(ProfileFieldError.INVALID, nonNumeric[ProfileField.AGE])

        val outOfRange = ProfileValidator.validate(validState().copy(age = "120"))
        assertEquals(ProfileFieldError.OUT_OF_RANGE, outOfRange[ProfileField.AGE])
    }

    @Test
    fun validAgeAccepted() {
        val errors = ProfileValidator.validate(validState().copy(age = "24"))
        assertTrue(errors[ProfileField.AGE] == null)
    }

    @Test
    fun demoValuesAreValid() {
        val errors = ProfileValidator.validate(
            ProfileFormState.from(DemoProfile.build()),
        )
        assertTrue(errors.isEmpty())
    }

    @Test
    fun validStateHasNoErrors() {
        assertTrue(ProfileValidator.validate(validState()).isEmpty())
    }
}
