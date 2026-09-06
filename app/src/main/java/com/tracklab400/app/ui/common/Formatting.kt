package com.tracklab400.app.ui.common

import com.tracklab400.app.data.model.ExerciseType
import com.tracklab400.app.data.model.SessionStatus
import com.tracklab400.app.data.model.TimeUtils
import com.tracklab400.app.data.model.WorkoutExercise
import com.tracklab400.app.data.model.WorkoutSession
import java.time.DayOfWeek

fun DayOfWeek.shortName(): String = when (this) {
    DayOfWeek.MONDAY -> "Pzt"
    DayOfWeek.TUESDAY -> "Sal"
    DayOfWeek.WEDNESDAY -> "Çar"
    DayOfWeek.THURSDAY -> "Per"
    DayOfWeek.FRIDAY -> "Cum"
    DayOfWeek.SATURDAY -> "Cmt"
    DayOfWeek.SUNDAY -> "Paz"
}

fun DayOfWeek.fullName(): String = when (this) {
    DayOfWeek.MONDAY -> "Pazartesi"
    DayOfWeek.TUESDAY -> "Salı"
    DayOfWeek.WEDNESDAY -> "Çarşamba"
    DayOfWeek.THURSDAY -> "Perşembe"
    DayOfWeek.FRIDAY -> "Cuma"
    DayOfWeek.SATURDAY -> "Cumartesi"
    DayOfWeek.SUNDAY -> "Pazar"
}

fun SessionStatus.displayName(): String = when (this) {
    SessionStatus.PLANNED -> "Planlı"
    SessionStatus.COMPLETED -> "Tamamlandı"
    SessionStatus.PARTIAL -> "Kısmen"
    SessionStatus.SKIPPED -> "Atlandı"
}

fun WorkoutSession.briefSummary(): String {
    val mainBlock = exercises
        .firstOrNull { it.distanceM != null && it.type != ExerciseType.WARMUP }
    return mainBlock?.let {
        val base = if (it.reps != null && it.distanceM != null) {
            "${it.reps}×${it.distanceM} m"
        } else {
            it.name
        }
        if (it.targetMinMs != null && it.targetMaxMs != null) {
            "$base · ${TimeUtils.formatSeconds(it.targetMinMs)}–${TimeUtils.formatSeconds(it.targetMaxMs)} sn"
        } else {
            base
        }
    } ?: exercises.firstOrNull()?.name.orEmpty()
}

fun WorkoutExercise.metaLine(): String {
    val parts = mutableListOf<String>()
    val volume = when {
        reps != null && distanceM != null -> "${reps}×${distanceM} m"
        intensity != null -> intensity
        else -> null
    }
    volume?.let { parts.add(it) }
    if (targetMinMs != null && targetMaxMs != null) {
        parts.add(
            "${TimeUtils.formatSeconds(targetMinMs)}–${TimeUtils.formatSeconds(targetMaxMs)} sn",
        )
    }
    val rest = when {
        restMinMs != null && restMaxMs != null ->
            "dinlenme ${TimeUtils.formatDuration(restMinMs)}–${TimeUtils.formatDuration(restMaxMs)}"
        restNote != null -> restNote
        else -> null
    }
    rest?.let { parts.add(it) }
    if (isFlying) parts.add("uçan")
    return parts.joinToString(" · ")
}
