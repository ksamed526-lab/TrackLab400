package com.tracklab400.app.ui.screens.profile

import com.tracklab400.app.data.model.Equipment
import com.tracklab400.app.data.model.GymStatus
import com.tracklab400.app.data.model.Profile
import com.tracklab400.app.data.model.ProfileField
import com.tracklab400.app.data.model.ProfileFieldError
import com.tracklab400.app.data.model.StopwatchType
import com.tracklab400.app.data.model.TimeUtils
import com.tracklab400.app.data.model.TrackAccess
import com.tracklab400.app.data.model.TrainingExperience
import kotlin.math.roundToLong

data class ProfileFormState(
    val nickname: String = "",
    val age: String = "",
    val current100: String = "",
    val current200: String = "",
    val current300: String = "",
    val current400: String = "",
    val targetDistance: Int = 400,
    val targetTime: String = "",
    val prepWeeks: Int = 8,
    val trainingDaysPerWeek: Int = 4,
    val startDateMillis: Long = System.currentTimeMillis(),
    val trackAccess: TrackAccess? = null,
    val gymStatus: GymStatus? = null,
    val equipment: Set<Equipment> = emptySet(),
    val experience: TrainingExperience? = null,
    val injuryInfo: String = "",
    val stopwatchType: StopwatchType? = null,
    val errors: Map<ProfileField, ProfileFieldError> = emptyMap(),
    val isSaving: Boolean = false,
) {
    fun toProfile(isDemo: Boolean, now: Long = System.currentTimeMillis()): Profile {
        fun ms(text: String): Long = (TimeUtils.parseSeconds(text)!! * 1000).roundToLong()
        return Profile(
            id = 1L,
            nickname = nickname.trim().ifEmpty { null },
            age = age.trim().toIntOrNull(),
            current100mMs = current100.takeIf { it.isNotBlank() }?.let { ms(it) },
            current200mMs = current200.takeIf { it.isNotBlank() }?.let { ms(it) },
            current300mMs = current300.takeIf { it.isNotBlank() }?.let { ms(it) },
            current400mMs = ms(current400),
            targetDistanceM = targetDistance,
            targetTimeMs = ms(targetTime),
            prepWeeks = prepWeeks,
            trainingDaysPerWeek = trainingDaysPerWeek,
            startDateMillis = startDateMillis,
            trackAccess = trackAccess!!,
            gymStatus = gymStatus!!,
            equipment = equipment,
            experience = experience!!,
            injuryInfo = injuryInfo.trim().ifEmpty { null },
            stopwatchType = stopwatchType!!,
            isDemo = isDemo,
            createdAt = now,
            updatedAt = now,
        )
    }

    companion object {
        fun from(profile: Profile): ProfileFormState {
            fun secondsText(millis: Long?): String {
                if (millis == null) return ""
                val value = millis / 1000.0
                return if (value % 1.0 == 0.0) {
                    value.toInt().toString()
                } else {
                    value.toString().replace('.', ',')
                }
            }

            return ProfileFormState(
                nickname = profile.nickname.orEmpty(),
                age = profile.age?.toString().orEmpty(),
                current100 = secondsText(profile.current100mMs),
                current200 = secondsText(profile.current200mMs),
                current300 = secondsText(profile.current300mMs),
                current400 = secondsText(profile.current400mMs),
                targetDistance = profile.targetDistanceM,
                targetTime = secondsText(profile.targetTimeMs),
                prepWeeks = profile.prepWeeks,
                trainingDaysPerWeek = profile.trainingDaysPerWeek,
                startDateMillis = profile.startDateMillis,
                trackAccess = profile.trackAccess,
                gymStatus = profile.gymStatus,
                equipment = profile.equipment,
                experience = profile.experience,
                injuryInfo = profile.injuryInfo.orEmpty(),
                stopwatchType = profile.stopwatchType,
            )
        }
    }
}
