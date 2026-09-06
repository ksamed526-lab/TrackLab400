package com.tracklab400.app.data.local.db.entity

import androidx.room.Entity
import com.tracklab400.app.data.model.SessionStatus

@Entity(
    tableName = "session_status",
    primaryKeys = ["weekNumber", "dayIndex"],
)
data class SessionStatusEntity(
    val weekNumber: Int,
    val dayIndex: Int,
    val status: String,
    val completedAt: Long?,
    val updatedAt: Long,
) {
    fun toDomain(): SessionStatus = SessionStatus.valueOf(status)

    companion object {
        fun from(
            weekNumber: Int,
            dayIndex: Int,
            status: SessionStatus,
            now: Long = System.currentTimeMillis(),
        ): SessionStatusEntity = SessionStatusEntity(
            weekNumber = weekNumber,
            dayIndex = dayIndex,
            status = status.name,
            completedAt = if (status == SessionStatus.COMPLETED) now else null,
            updatedAt = now,
        )
    }
}
