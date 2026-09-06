package com.tracklab400.app.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "session_record")
data class SessionRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val weekNumber: Int,
    val dayOfWeek: Int, // DayOfWeek.value (1=Mon..7=Sun)
    val sessionKind: String,
    val completedAt: Long, // epoch millis
    val status: String, // COMPLETED, PARTIAL, SKIPPED
    val rpe: Int? = null,
    val sleepHours: Double? = null,
    val energyLevel: Int? = null, // 1-5
    val legFeeling: Int? = null, // 1-5
    val painReported: Boolean = false,
    val painLocation: String? = null,
    val painSeverity: Int? = null, // 1-10
    val notes: String? = null,
    val totalDistanceM: Int = 0,
    val completedReps: Int = 0,
    val expectedReps: Int = 0,
    val totalTimeMs: Long = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
)

@Entity(tableName = "rep_record")
data class RepRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val sessionRecordId: Long,
    val repIndex: Int, // 1-based
    val distanceM: Int,
    val targetMinMs: Long? = null,
    val targetMaxMs: Long? = null,
    val actualMs: Long,
    val isManual: Boolean = false,
    val recordedAt: Long = System.currentTimeMillis(),
)

@Entity(tableName = "pain_report")
data class PainReportEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val sessionRecordId: Long,
    val location: String,
    val severity: Int, // 1-10
    val description: String? = null,
    val reportedAt: Long = System.currentTimeMillis(),
)