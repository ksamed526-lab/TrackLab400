package com.tracklab400.app.data.notifications

import com.tracklab400.app.data.model.DemoProfile
import com.tracklab400.app.data.model.NotificationSettings
import com.tracklab400.app.data.plan.TrainingPlanFactory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.ZoneOffset

class NotificationPlannerTest {

    private val startMillis = 1_800_000_000_000L // bazı sabit an
    private val now = startMillis - 86_400_000L // plandan 1 gün önce
    private val plan = TrainingPlanFactory.build(DemoProfile.build(startMillis), emptyMap(), now)

    private fun build(
        settings: NotificationSettings = NotificationSettings(),
    ): List<NotificationRequest> = NotificationPlanner.buildRequests(
        settings = settings,
        plan = plan,
        startDateMillis = startMillis,
        now = now,
        zoneId = ZoneOffset.UTC,
    )

    @Test
    fun disabledSettingsProduceNoRequests() {
        assertTrue(build(NotificationSettings(enabled = false)).isEmpty())
    }

    @Test
    fun nullPlanProducesNoRequests() {
        assertTrue(
            NotificationPlanner.buildRequests(
                NotificationSettings(),
                null,
                startMillis,
                now,
                ZoneOffset.UTC,
            ).isEmpty(),
        )
    }

    @Test
    fun allRequestNamesAreUnique() {
        val names = build().map { it.uniqueName }
        assertEquals(names.size, names.toSet().size)
    }

    @Test
    fun pastNotificationsAreFiltered() {
        val late = NotificationPlanner.buildRequests(
            NotificationSettings(),
            plan,
            startMillis,
            now = startMillis + 100 * 86_400_000L, // plan bitiminden sonra
            ZoneOffset.UTC,
        )
        assertTrue(late.isEmpty())
    }

    @Test
    fun trainingDayReminderCreatedForEachMainSession() {
        val requests = build()
        val dayReminders = requests.filter {
            it.uniqueName.startsWith("day-") && it.channelId == NotificationChannels.TRAINING
        }
        assertEquals(32, dayReminders.size)
        val mondayWeek1 = dayReminders.first { it.uniqueName == "day-1-1" }
        assertTrue(mondayWeek1.title.contains("Hızlanma"))
        assertEquals("workout/1/1", mondayWeek1.route)
    }

    @Test
    fun startReminderUsesWorkoutTime() {
        val requests = build()
        val start = requests.first { it.uniqueName == "start-1-1" }
        // day 09:00 vs start 18:30 → start daha geç
        val day = requests.first { it.uniqueName == "day-1-1" }
        assertTrue(start.delayMs > day.delayMs)
    }

    @Test
    fun restDayReminderOnlyForRestDays() {
        val requests = build()
        val restReminders = requests.filter {
            it.channelId == NotificationChannels.REST
        }
        // 8 hafta × (Çarşamba + Cuma + Pazar) = 24
        assertEquals(24, restReminders.size)
        assertTrue(restReminders.all { it.title == "Dinlenme günü" })
    }

    @Test
    fun testWeekProducesSpecialMessages() {
        val requests = build()
        val testDay = requests.first { it.uniqueName == "day-4-4" }
        assertEquals("300 m test", testDay.title)

        val eve = requests.first { it.uniqueName == "test-eve" }
        assertTrue(eve.title.contains("Yarın 300 m test"))
        assertEquals("workout/4/4", eve.route)
    }

    @Test
    fun raceWeekProducesRaceMessage() {
        val requests = build()
        val raceDay = requests.first { it.uniqueName == "day-8-6" }
        assertTrue(raceDay.title.contains("Yarış günü"))
    }

    @Test
    fun weeklyReviewScheduledForEverySunday() {
        val reviews = build().filter { it.uniqueName.startsWith("review-") }
        assertEquals(8, reviews.size)
        assertTrue(reviews.all { it.channelId == NotificationChannels.WEEKLY })
    }

    @Test
    fun completionScheduledOnce() {
        val completions = build().filter { it.uniqueName == "completion" }
        assertEquals(1, completions.size)
        assertEquals(NotificationChannels.COMPLETION, completions.first().channelId)
    }

    @Test
    fun togglingTypesOffRemovesThem() {
        val allOff = build(
            NotificationSettings(
                trainingDayReminder = false,
                workoutStartReminder = false,
                restDayReminder = false,
                weeklyReviewReminder = false,
                testWeekReminder = false,
                completionReminder = false,
            ),
        )
        assertTrue(allOff.isEmpty())
    }

    @Test
    fun togglingIndividualTypeOffRemovesOnlyThatType() {
        val noStart = build(NotificationSettings(workoutStartReminder = false))
        assertTrue(noStart.none { it.uniqueName.startsWith("start-") })
        assertTrue(noStart.any { it.uniqueName.startsWith("day-") })

        val noRest = build(NotificationSettings(restDayReminder = false))
        assertTrue(noRest.none { it.channelId == NotificationChannels.REST })
    }

    @Test
    fun delaysArePositive() {
        assertTrue(build().all { it.delayMs > 0 })
    }

    @Test
    fun routesPointToCorrectWorkoutScreens() {
        val requests = build()
        val thursdayWeek3 = requests.first { it.uniqueName == "day-3-4" }
        assertEquals("workout/3/4", thursdayWeek3.route)
    }
}
