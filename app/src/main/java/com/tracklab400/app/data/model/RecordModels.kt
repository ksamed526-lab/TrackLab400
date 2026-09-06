package com.tracklab400.app.data.model

data class SessionRecord(
    val id: Long = 0L,
    val weekNumber: Int,
    val dayOfWeek: Int,
    val sessionKind: String,
    val completedAt: Long,
    val status: SessionStatus,
    val rpe: Int? = null,
    val sleepHours: Double? = null,
    val energyLevel: Int? = null,
    val legFeeling: Int? = null,
    val painReported: Boolean = false,
    val painLocation: String? = null,
    val painSeverity: Int? = null,
    val notes: String? = null,
    val totalDistanceM: Int = 0,
    val completedReps: Int = 0,
    val expectedReps: Int = 0,
    val totalTimeMs: Long = 0,
    val reps: List<RepRecord> = emptyList(),
    val painReports: List<PainReport> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
) {
    val completionPercent: Float
        get() = if (expectedReps > 0) completedReps.toFloat() / expectedReps else 0f

    val averageRepMs: Long?
        get() = if (reps.isEmpty()) {
            null
        } else {
            reps.map { it.actualMs }.sum() / reps.size
        }

    val bestRepMs: Long?
        get() = reps.minOfOrNull { it.actualMs }

    val slowestRepMs: Long?
        get() = reps.maxOfOrNull { it.actualMs }

    val avgDeviationMs: Long?
        get() {
            val deviations = reps.mapNotNull { it.deviationMs }
            return if (deviations.isEmpty()) {
                null
            } else {
                (deviations.sum().toDouble() / deviations.size).toLong()
            }
        }
}

data class RepRecord(
    val id: Long = 0L,
    val sessionRecordId: Long = 0L,
    val repIndex: Int,
    val distanceM: Int,
    val targetMinMs: Long? = null,
    val targetMaxMs: Long? = null,
    val actualMs: Long,
    val isManual: Boolean = false,
    val recordedAt: Long = System.currentTimeMillis(),
) {
    val deviationMs: Long?
        get() = targetMinMs?.let { actualMs - it }
        ?: targetMaxMs?.let { actualMs - it }
}

data class PainReport(
    val id: Long = 0L,
    val sessionRecordId: Long,
    val location: String,
    val severity: Int,
    val description: String? = null,
    val reportedAt: Long = System.currentTimeMillis(),
)