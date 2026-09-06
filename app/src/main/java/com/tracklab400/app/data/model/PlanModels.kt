package com.tracklab400.app.data.model

import java.time.DayOfWeek

enum class PlanBlock { YUKLENME, OZELLESME }

enum class SessionKind {
    HIZLANMA_KUVVET_A,
    TEMPO,
    OZEL_DAYANIKLILIK,
    MAKS_HIZ_KUVVET_B,
    DINLENME,
    HAFIF_TOPARLANMA,
    TEST_YARIS,
    ;

    val isMainSession: Boolean
        get() = this == HIZLANMA_KUVVET_A ||
            this == TEMPO ||
            this == OZEL_DAYANIKLILIK ||
            this == MAKS_HIZ_KUVVET_B ||
            this == TEST_YARIS
}

enum class ExerciseType { WARMUP, RUN, STRENGTH, NOTE }

enum class SessionStatus { PLANNED, COMPLETED, PARTIAL, SKIPPED }

data class TrainingPlan(
    val id: Long = 1L,
    val eventDistanceM: Int = 400,
    val targetTimeMs: Long,
    val startDateMillis: Long,
    val currentWeekNumber: Int,
    val weeks: List<TrainingWeek>,
)

data class TrainingWeek(
    val weekNumber: Int,
    val block: PlanBlock,
    val focus: String,
    val isDeload: Boolean = false,
    val isRaceWeek: Boolean = false,
    val checkpointNote: String? = null,
    val days: List<WorkoutSession>,
)

data class WorkoutSession(
    val weekNumber: Int,
    val dayOfWeek: DayOfWeek,
    val kind: SessionKind,
    val title: String,
    val focus: String?,
    val isRestDay: Boolean = false,
    val status: SessionStatus = SessionStatus.PLANNED,
    val completedAt: Long? = null,
    val exercises: List<WorkoutExercise>,
)

data class WorkoutExercise(
    val type: ExerciseType,
    val name: String,
    val distanceM: Int? = null,
    val reps: Int? = null,
    val targetMinMs: Long? = null,
    val targetMaxMs: Long? = null,
    val restMinMs: Long? = null,
    val restMaxMs: Long? = null,
    val restNote: String? = null,
    val isFlying: Boolean = false,
    val isTest: Boolean = false,
    val intensity: String? = null,
    val cues: String? = null,
    val perLeg: Boolean = false,
)
