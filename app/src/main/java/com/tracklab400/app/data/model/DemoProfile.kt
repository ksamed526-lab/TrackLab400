package com.tracklab400.app.data.model

object DemoProfile {

    const val CURRENT_400_MS = 76_000L
    const val CURRENT_100_MS = 15_000L
    const val TARGET_400_MS = 68_000L

    fun build(now: Long = System.currentTimeMillis()): Profile = Profile(
        id = 1L,
        nickname = "Demo Koşucu",
        age = 24,
        current100mMs = CURRENT_100_MS,
        current200mMs = null,
        current300mMs = null,
        current400mMs = CURRENT_400_MS,
        targetDistanceM = 400,
        targetTimeMs = TARGET_400_MS,
        prepWeeks = 8,
        trainingDaysPerWeek = 4,
        startDateMillis = now,
        trackAccess = TrackAccess.PIST_VAR,
        gymStatus = GymStatus.SALON_VAR,
        equipment = setOf(Equipment.BARBELL, Equipment.DUMBBELL, Equipment.TRAP_BAR),
        experience = TrainingExperience.ORTA,
        injuryInfo = null,
        stopwatchType = StopwatchType.EL_KRONOMETRESI,
        isDemo = true,
        createdAt = now,
        updatedAt = now,
    )
}
