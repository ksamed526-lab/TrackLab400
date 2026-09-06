package com.tracklab400.app.data.plan

import com.tracklab400.app.data.model.ExerciseType
import com.tracklab400.app.data.model.Profile
import com.tracklab400.app.data.model.SessionKind
import com.tracklab400.app.data.model.SessionStatus
import com.tracklab400.app.data.model.TrainingPlan
import com.tracklab400.app.data.model.TrainingWeek
import com.tracklab400.app.data.model.WorkoutExercise
import com.tracklab400.app.data.model.WorkoutSession
import java.time.DayOfWeek

object TrainingPlanFactory {

    fun build(
        profile: Profile,
        statuses: Map<Pair<Int, Int>, SessionStatus> = emptyMap(),
        now: Long = System.currentTimeMillis(),
    ): TrainingPlan {
        val targetMs = profile.targetTimeMs
        val weeks = PlanTemplate.WEEKS_400.map { weekTemplate ->
            TrainingWeek(
                weekNumber = weekTemplate.weekNumber,
                block = weekTemplate.block,
                focus = weekTemplate.focus,
                isDeload = weekTemplate.isDeload,
                isRaceWeek = weekTemplate.isRaceWeek,
                checkpointNote = if (weekTemplate.hasCheckpoint) buildCheckpointNote(targetMs) else null,
                days = weekTemplate.sessions.map { sessionTemplate ->
                    val status = statuses[weekTemplate.weekNumber to sessionTemplate.day.value]
                    WorkoutSession(
                        weekNumber = weekTemplate.weekNumber,
                        dayOfWeek = sessionTemplate.day,
                        kind = sessionTemplate.kind,
                        title = sessionTemplate.title,
                        focus = sessionTemplate.focus,
                        isRestDay = sessionTemplate.isRest,
                        status = status ?: SessionStatus.PLANNED,
                        exercises = if (sessionTemplate.isRest) {
                            emptyList()
                        } else {
                            warmUpBlocks() +
                                sessionTemplate.blocks.map { it.toExercise(targetMs) } +
                                raceNoteBlocks(sessionTemplate.kind, targetMs)
                        },
                    )
                },
            )
        }
        return TrainingPlan(
            id = 1L,
            eventDistanceM = 400,
            targetTimeMs = targetMs,
            startDateMillis = profile.startDateMillis,
            currentWeekNumber = currentWeekNumber(
                startDateMillis = profile.startDateMillis,
                weekCount = weeks.size,
                now = now,
            ),
            weeks = weeks,
        )
    }

    fun currentWeekNumber(startDateMillis: Long, weekCount: Int, now: Long): Int {
        val startEpochDay = startDateMillis / DAY_MS
        val todayEpochDay = now / DAY_MS
        val week = ((todayEpochDay - startEpochDay) / 7).toInt() + 1
        return week.coerceIn(1, weekCount)
    }

    fun daysRemaining(startDateMillis: Long, prepWeeks: Int, now: Long): Long {
        val endMillis = startDateMillis + prepWeeks * 7L * DAY_MS
        return (endMillis - now) / DAY_MS
    }

    fun completionStats(plan: TrainingPlan): Pair<Int, Int> {
        val mainSessions = plan.weeks.flatMap { it.days }.filter { it.kind.isMainSession }
        val completed = mainSessions.count { it.status == SessionStatus.COMPLETED }
        return completed to mainSessions.size
    }

    fun sessionFor(plan: TrainingPlan, day: DayOfWeek): WorkoutSession? =
        plan.weeks.firstOrNull { it.weekNumber == plan.currentWeekNumber }
            ?.days?.firstOrNull { it.dayOfWeek == day }

    fun nextSession(plan: TrainingPlan, today: DayOfWeek): WorkoutSession? {
        var weekNumber = plan.currentWeekNumber
        var startDay = today
        while (weekNumber <= plan.weeks.size) {
            val week = plan.weeks.firstOrNull { it.weekNumber == weekNumber } ?: return null
            val candidates = if (weekNumber == plan.currentWeekNumber) {
                week.days.filter { it.dayOfWeek.value >= startDay.value }
            } else {
                week.days
            }
            val next = candidates.firstOrNull { session ->
                session.kind.isMainSession &&
                    session.status != SessionStatus.COMPLETED &&
                    session.status != SessionStatus.SKIPPED
            }
            if (next != null) return next
            weekNumber++
            startDay = DayOfWeek.MONDAY
        }
        return null
    }

    fun raceSplitsDescription(targetMs: Long): String {
        val splits = PaceCalculator.raceSplits(targetMs)
        fun fmt(millis: Long) = millis.toDouble() / 1000.0
        fun text(split: RaceSplit) =
            "${fmt(split.minMs)}–${fmt(split.maxMs)}".replace('.', ',')

        val first = splits.first { it.distanceM == 100 }
        val second = splits.first { it.distanceM == 200 }
        val third = splits.first { it.distanceM == 300 }
        return "Yarış günü hedef ritim: İlk 100 m: ${text(first)} sn · 200 m: ${text(second)} sn · " +
            "300 m: ${text(third)} sn. İlk 100 m'yi hızlı açma; son 100 m'de ritmi koruyacak pay bırak."
    }

    private fun buildCheckpointNote(targetMs: Long): String {
        val good = PaceCalculator.scale(targetMs, PaceFactor(0.779, 0.809))
        val threshold = PaceCalculator.roundTo500(targetMs * 0.838)
        val intermediateLow = targetMs + 2_000L
        val intermediateHigh = targetMs + 4_000L
        return "300 m testinde yaklaşık ${sec(good.first)}–${sec(good.last)} sn iyi işarettir. " +
            "${sec(threshold)} sn'nin üzerindeyse ${sec(targetMs)} sn'yi zorlamak yerine " +
            "${sec(intermediateLow)}–${sec(intermediateHigh)} sn ara hedefi koymak daha akıllıcadır."
    }

    private fun sec(millis: Long): String {
        val value = millis / 1000.0
        val text = if (value % 1.0 == 0.0) value.toInt().toString() else value.toString()
        return text.replace('.', ',')
    }

    private fun warmUpBlocks(): List<WorkoutExercise> = listOf(
        WorkoutExercise(ExerciseType.WARMUP, "Hafif koşu", intensity = "5–7 dk", cues = "Konuşabilecek kadar rahat tempo."),
        WorkoutExercise(ExerciseType.WARMUP, "Hareketlilik", intensity = "3–4 dk", cues = "Ayak bileği, kalça, hamstring."),
        WorkoutExercise(ExerciseType.WARMUP, "Drill", intensity = "2 tur", cues = "A-skip, diz çekme, ankling."),
        WorkoutExercise(ExerciseType.WARMUP, "Strides", intensity = "3×60 m", cues = "Her biri biraz daha hızlı."),
    )

    private fun raceNoteBlocks(kind: SessionKind, targetMs: Long): List<WorkoutExercise> =
        if (kind == SessionKind.TEST_YARIS) {
            listOf(
                WorkoutExercise(
                    ExerciseType.NOTE,
                    "Hedef ritim",
                    cues = raceSplitsDescription(targetMs),
                ),
            )
        } else {
            emptyList()
        }

    private fun BlockTemplate.toExercise(targetMs: Long): WorkoutExercise {
        val range = paceFactor?.let { PaceCalculator.scale(targetMs, it) }
        return WorkoutExercise(
            type = type,
            name = name,
            distanceM = distanceM,
            reps = reps,
            targetMinMs = range?.first,
            targetMaxMs = range?.last,
            restMinMs = restMinMs,
            restMaxMs = restMaxMs,
            restNote = restNote,
            isFlying = isFlying,
            isTest = isTest,
            intensity = intensity,
            cues = cues,
            perLeg = perLeg,
        )
    }

    private const val DAY_MS = 86_400_000L
}
