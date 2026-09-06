package com.tracklab400.app.data.model

import com.tracklab400.app.ui.screens.profile.ProfileFormState

enum class ProfileField {
    NICKNAME,
    AGE,
    CURRENT_100,
    CURRENT_200,
    CURRENT_300,
    CURRENT_400,
    TARGET_DISTANCE,
    TARGET_TIME,
    PREP_WEEKS,
    TRAINING_DAYS,
    START_DATE,
    TRACK_ACCESS,
    GYM_STATUS,
    EQUIPMENT,
    EXPERIENCE,
    INJURY,
    STOPWATCH_TYPE,
}

enum class ProfileFieldError {
    EMPTY,
    INVALID,
    NON_POSITIVE,
    OUT_OF_RANGE,
    NOT_BELOW_CURRENT,
}

object ProfileValidator {

    fun rangeForDistance(distance: Int): ClosedFloatingPointRange<Double>? = when (distance) {
        100 -> 9.0..30.0
        200 -> 18.0..60.0
        300 -> 28.0..90.0
        400 -> 35.0..180.0
        else -> null
    }

    fun validate(state: ProfileFormState): Map<ProfileField, ProfileFieldError> {
        val errors = mutableMapOf<ProfileField, ProfileFieldError>()

        validateRequiredTime(state.current400, ProfileField.CURRENT_400, 400, errors)
        validateOptionalTime(state.current100, ProfileField.CURRENT_100, 100, errors)
        validateOptionalTime(state.current200, ProfileField.CURRENT_200, 200, errors)
        validateOptionalTime(state.current300, ProfileField.CURRENT_300, 300, errors)

        val targetError = validateTime(state.targetTime, state.targetDistance)
        if (targetError != null) {
            errors[ProfileField.TARGET_TIME] = targetError
        } else {
            val currentForTarget = when (state.targetDistance) {
                100 -> state.current100
                200 -> state.current200
                300 -> state.current300
                else -> state.current400
            }
            val current = currentForTarget
                .takeIf { it.isNotBlank() }
                ?.let { TimeUtils.parseSeconds(it) }
            val target = TimeUtils.parseSeconds(state.targetTime)
            if (current != null && target != null && target >= current) {
                errors[ProfileField.TARGET_TIME] = ProfileFieldError.NOT_BELOW_CURRENT
            }
        }

        if (state.trackAccess == null) errors[ProfileField.TRACK_ACCESS] = ProfileFieldError.EMPTY
        if (state.gymStatus == null) errors[ProfileField.GYM_STATUS] = ProfileFieldError.EMPTY
        if (state.experience == null) errors[ProfileField.EXPERIENCE] = ProfileFieldError.EMPTY
        if (state.stopwatchType == null) errors[ProfileField.STOPWATCH_TYPE] = ProfileFieldError.EMPTY
        if (state.prepWeeks !in 4..16) errors[ProfileField.PREP_WEEKS] = ProfileFieldError.OUT_OF_RANGE
        if (state.trainingDaysPerWeek !in 3..6) errors[ProfileField.TRAINING_DAYS] = ProfileFieldError.OUT_OF_RANGE

        if (state.age.isNotBlank()) {
            val age = state.age.trim().toIntOrNull()
            when {
                age == null -> errors[ProfileField.AGE] = ProfileFieldError.INVALID
                age !in 5..100 -> errors[ProfileField.AGE] = ProfileFieldError.OUT_OF_RANGE
            }
        }

        return errors
    }

    private fun validateRequiredTime(
        text: String,
        field: ProfileField,
        distance: Int,
        errors: MutableMap<ProfileField, ProfileFieldError>,
    ) {
        val error = validateTime(text, distance)
        if (error != null) errors[field] = error
    }

    private fun validateOptionalTime(
        text: String,
        field: ProfileField,
        distance: Int,
        errors: MutableMap<ProfileField, ProfileFieldError>,
    ) {
        if (text.isBlank()) return
        val error = validateTime(text, distance)
        if (error != null) errors[field] = error
    }

    private fun validateTime(text: String, distance: Int): ProfileFieldError? {
        if (text.isBlank()) return ProfileFieldError.EMPTY
        val value = TimeUtils.parseSeconds(text) ?: return ProfileFieldError.INVALID
        if (value <= 0.0) return ProfileFieldError.NON_POSITIVE
        val range = rangeForDistance(distance)
        if (range != null && value !in range) return ProfileFieldError.OUT_OF_RANGE
        return null
    }
}
