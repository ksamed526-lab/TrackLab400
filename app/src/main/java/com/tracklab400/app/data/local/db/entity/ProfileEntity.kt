package com.tracklab400.app.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tracklab400.app.data.model.Equipment
import com.tracklab400.app.data.model.GymStatus
import com.tracklab400.app.data.model.Profile
import com.tracklab400.app.data.model.StopwatchType
import com.tracklab400.app.data.model.TrackAccess
import com.tracklab400.app.data.model.TrainingExperience
import com.tracklab400.app.data.local.Converters

@Entity(tableName = "profile")
data class ProfileEntity(
    @PrimaryKey val id: Long = 1L,
    val nickname: String?,
    val age: Int?,
    val current100mMs: Long?,
    val current200mMs: Long?,
    val current300mMs: Long?,
    val current400mMs: Long,
    val targetDistanceM: Int,
    val targetTimeMs: Long,
    val prepWeeks: Int,
    val trainingDaysPerWeek: Int,
    val startDateMillis: Long,
    val trackAccess: TrackAccess,
    val gymStatus: GymStatus,
    val equipmentJson: String,
    val experience: TrainingExperience,
    val injuryInfo: String?,
    val stopwatchType: StopwatchType,
    val isDemo: Boolean,
    val createdAt: Long,
    val updatedAt: Long,
)

fun Profile.toEntity(createdAt: Long = System.currentTimeMillis()): ProfileEntity {
    val converters = Converters()
    return ProfileEntity(
        id = id,
        nickname = nickname,
        age = age,
        current100mMs = current100mMs,
        current200mMs = current200mMs,
        current300mMs = current300mMs,
        current400mMs = current400mMs,
        targetDistanceM = targetDistanceM,
        targetTimeMs = targetTimeMs,
        prepWeeks = prepWeeks,
        trainingDaysPerWeek = trainingDaysPerWeek,
        startDateMillis = startDateMillis,
        trackAccess = trackAccess,
        gymStatus = gymStatus,
        equipmentJson = converters.equipmentToJson(equipment),
        experience = experience,
        injuryInfo = injuryInfo,
        stopwatchType = stopwatchType,
        isDemo = isDemo,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )
}

fun ProfileEntity.toDomain(): Profile {
    val converters = Converters()
    return Profile(
        id = id,
        nickname = nickname,
        age = age,
        current100mMs = current100mMs,
        current200mMs = current200mMs,
        current300mMs = current300mMs,
        current400mMs = current400mMs,
        targetDistanceM = targetDistanceM,
        targetTimeMs = targetTimeMs,
        prepWeeks = prepWeeks,
        trainingDaysPerWeek = trainingDaysPerWeek,
        startDateMillis = startDateMillis,
        trackAccess = trackAccess,
        gymStatus = gymStatus,
        equipment = converters.jsonToEquipment(equipmentJson),
        experience = experience,
        injuryInfo = injuryInfo,
        stopwatchType = stopwatchType,
        isDemo = isDemo,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )
}
