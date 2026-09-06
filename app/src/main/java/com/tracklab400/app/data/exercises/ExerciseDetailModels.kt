package com.tracklab400.app.data.exercises

/**
 * Hareket detay sayfalarında gösterilen "başlangıç/bitiş" teknik çizimi.
 * Her değer, res/drawable/pose_*.xml içindeki yerel bir VectorDrawable'a karşılık gelir.
 */
enum class PoseKind {
    STAND,
    RUNNING,
    HEEL_RAISE,
    KNEE_RAISE,
    HAMSTRING_PUMP,
    LEG_SWING,
    LATERAL_LUNGE,
    HINGE,
    SKIP,
    BRIDGE_DOWN,
    BRIDGE_UP,
    SQUAT,
    SPLIT_SQUAT_UP,
    SPLIT_SQUAT_DOWN,
    NORDIC_START,
    NORDIC_LEAN,
    PULLUP,
    PULLUP_UP,
    PROWLER,
    BOX_LOAD,
    STEP_UP,
    PLANK,
    PLANK_KNEE,
    PUSHUP,
    PUSHUP_LOW,
    PIKE,
    QUAD,
    CHILD,
    ITBAND,
    CALF,
    PIGEON,
    BREATH,
}

/** Isınma hareketi. */
data class WarmUpExercise(
    val id: String,
    val name: String,
    val purpose: String,
    val durationSeconds: Int,
    val steps: List<String>,
    val tips: List<String>,
    val commonMistakes: List<String>,
    val safety: List<String>,
    val poseStart: PoseKind,
    val poseEnd: PoseKind,
)

/** Ana çalışma koşu bloğu. */
data class RunningInterval(
    val name: String,
    val distanceM: Int,
    val reps: Int,
    val targetMinMs: Long?,
    val targetMaxMs: Long?,
    val restMinMs: Long?,
    val restMaxMs: Long?,
    val purpose: String,
    val cues: List<String>,
    val notes: String?,
)

/** Kuvvet hareketi + oturum içi çalışma alanı. */
data class StrengthExerciseRecord(
    val id: String,
    val name: String,
    val targetMuscles: List<String>,
    val sets: Int,
    val reps: Int,
    val restSeconds: Int,
    val rpeTarget: Int,
    val noEquipmentAlternative: String,
    val startPose: PoseKind,
    val endPose: PoseKind,
    val technique: List<String>,
    val commonMistakes: List<String>,
    val safety: List<String>,
    var weight: Double? = null,
    var completedSets: Int = 0,
    var perSetNotes: MutableList<String> = mutableListOf(),
)

/** Soğuma hareketi. */
data class CoolDownExercise(
    val id: String,
    val name: String,
    val purpose: String,
    val durationSeconds: Int,
    val steps: List<String>,
    val tips: List<String>,
    val commonMistakes: List<String>,
    val safety: List<String>,
)