package com.tracklab400.app.data.notifications

import com.tracklab400.app.data.model.NotificationSettings
import com.tracklab400.app.data.model.SessionKind
import com.tracklab400.app.data.model.TrainingPlan
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset

data class NotificationRequest(
    val uniqueName: String,
    val delayMs: Long,
    val channelId: String,
    val title: String,
    val body: String,
    val route: String,
)

/**
 * Bildirim listesini tamamen saf biçimde üretir (unit test edilebilir).
 * Zamanlama WorkManager tarafında, içerik ve adlar burada hesaplanır.
 */
object NotificationPlanner {

    fun buildRequests(
        settings: NotificationSettings,
        plan: TrainingPlan?,
        startDateMillis: Long?,
        now: Long = System.currentTimeMillis(),
        zoneId: ZoneId = ZoneId.systemDefault(),
    ): List<NotificationRequest> {
        if (!settings.enabled || plan == null || startDateMillis == null) return emptyList()

        val startDate = Instant.ofEpochMilli(startDateMillis)
            .atZone(ZoneOffset.UTC)
            .toLocalDate()

        val requests = mutableListOf<NotificationRequest>()

        fun add(
            name: String,
            date: LocalDate,
            hour: Int,
            minute: Int,
            channel: String,
            title: String,
            body: String,
            route: String,
        ) {
            val millis = date.atTime(hour, minute).atZone(zoneId).toInstant().toEpochMilli()
            val delay = millis - now
            if (delay > 0L) {
                requests += NotificationRequest(name, delay, channel, title, body, route)
            }
        }

        plan.weeks.forEach { week ->
            week.days.forEach { session ->
                val sessionDate = startDate.plusDays(
                    (((week.weekNumber - 1) * 7) + (session.dayOfWeek.value - 1)).toLong(),
                )
                val workoutRoute = "workout/${week.weekNumber}/${session.dayOfWeek.value}"

                if (session.isRestDay) {
                    if (settings.restDayReminder) {
                        add(
                            "rest-${week.weekNumber}-${session.dayOfWeek.value}",
                            sessionDate,
                            settings.dayReminderHour,
                            settings.dayReminderMinute,
                            NotificationChannels.REST,
                            "Dinlenme günü",
                            "Toparlanma da gelişimin parçası. Hafif hareketlilik yapabilirsin.",
                            workoutRoute,
                        )
                    }
                    return@forEach
                }

                val isRaceDay = session.kind == SessionKind.TEST_YARIS
                val isTest300 = session.exercises.any { it.isTest && it.distanceM == 300 }

                if (settings.trainingDayReminder) {
                    when {
                        isRaceDay -> add(
                            "day-${week.weekNumber}-${session.dayOfWeek.value}",
                            sessionDate,
                            settings.dayReminderHour,
                            settings.dayReminderMinute,
                            NotificationChannels.TRAINING,
                            "Yarış günü: 400 m",
                            "Hedef ritmi uygula; ilk 100 m'yi kontrollü aç.",
                            workoutRoute,
                        )
                        isTest300 -> add(
                            "day-${week.weekNumber}-${session.dayOfWeek.value}",
                            sessionDate,
                            settings.dayReminderHour,
                            settings.dayReminderMinute,
                            NotificationChannels.TRAINING,
                            "300 m test",
                            "Aynı pistte, aynı kronometre yöntemiyle ölç.",
                            workoutRoute,
                        )
                        else -> add(
                            "day-${week.weekNumber}-${session.dayOfWeek.value}",
                            sessionDate,
                            settings.dayReminderHour,
                            settings.dayReminderMinute,
                            NotificationChannels.TRAINING,
                            "Bugün: ${session.title}",
                            "Odak: ${week.focus}",
                            workoutRoute,
                        )
                    }
                }

                if (settings.workoutStartReminder && !isRaceDay) {
                    add(
                        "start-${week.weekNumber}-${session.dayOfWeek.value}",
                        sessionDate,
                        settings.workoutStartHour,
                        settings.workoutStartMinute,
                        NotificationChannels.TRAINING,
                        "Antrenman başlıyor",
                        "${session.title} — ısınmayı atlama.",
                        workoutRoute,
                    )
                }
            }
        }

        // 4. hafta 300 m test arifesi hatırlatması (Çarşamba akşamı)
        if (settings.testWeekReminder && plan.weeks.any { it.weekNumber == 4 }) {
            val eveDate = startDate.plusDays((3 * 7) + 2L)
            add(
                "test-eve",
                eveDate,
                20,
                0,
                NotificationChannels.TRAINING,
                "Yarın 300 m test",
                "Bugün hafif kal, erken uyu. Test iyi uyku ister.",
                "workout/4/4",
            )
        }

        // Haftalık değerlendirme (Pazar 19:00)
        if (settings.weeklyReviewReminder) {
            for (week in plan.weeks) {
                val reviewDate = startDate.plusDays(((week.weekNumber - 1) * 7) + 6L)
                add(
                    "review-${week.weekNumber}",
                    reviewDate,
                    19,
                    0,
                    NotificationChannels.WEEKLY,
                    "Haftalık değerlendirme",
                    "Bu haftanın seanslarını ve bacak hissi notlarını gözden geçir.",
                    "plan",
                )
            }
        }

        // Program tamamlanma bildirimi
        if (settings.completionReminder) {
            val endDate = startDate.plusDays((plan.weeks.size * 7).toLong())
            add(
                "completion",
                endDate,
                10,
                0,
                NotificationChannels.COMPLETION,
                "Program tamamlandı!",
                "8 haftalık program bitti. 400 m hedefine ne kadar yaklaştığını ölçmeyi unutma.",
                "home",
            )
        }

        return requests
    }
}
