package com.tracklab400.app

import android.content.Context
import androidx.core.app.NotificationManagerCompat
import androidx.test.core.app.ApplicationProvider
import androidx.work.WorkManager
import com.tracklab400.app.data.model.DemoProfile
import com.tracklab400.app.data.model.NotificationSettings
import com.tracklab400.app.data.notifications.NotificationScheduler
import com.tracklab400.app.data.plan.TrainingPlanFactory
import java.util.Calendar
import org.junit.After
import org.junit.Assert.assertNotNull
import org.junit.Test

class NotificationDeliveryTest {

    private val context get() = ApplicationProvider.getApplicationContext<Context>()

    @After
    fun cleanup() {
        WorkManager.getInstance(context).cancelAllWorkByTag(NotificationScheduler.TAG)
        NotificationManagerCompat.from(context).cancelAll()
    }

    @Test
    fun scheduledReminderDeliversNotificationViaWorker() {
        val target = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis() + 15_000L
        }
        val settings = NotificationSettings(
            enabled = true,
            trainingDayReminder = true,
            workoutStartReminder = false,
            restDayReminder = true,
            weeklyReviewReminder = false,
            testWeekReminder = false,
            completionReminder = false,
            dayReminderHour = target.get(Calendar.HOUR_OF_DAY),
            dayReminderMinute = target.get(Calendar.MINUTE),
            workoutStartHour = 0,
            workoutStartMinute = 0,
        )
        val profile = DemoProfile.build()
        val startDate = Calendar.getInstance().apply {
            timeInMillis = profile.startDateMillis
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        val plan = TrainingPlanFactory.build(
            profile = profile.copy(startDateMillis = startDate),
        )

        NotificationScheduler(context).reschedule(settings, plan, startDate)

        val deadline = System.currentTimeMillis() + 90_000L
        var seen: String? = null
        while (System.currentTimeMillis() < deadline && seen == null) {
            Thread.sleep(1_000L)
            NotificationManagerCompat.from(context).activeNotifications
                .filter { it.packageName == context.packageName }
                .forEach { notification ->
                    val title = notification.notification.extras.getCharSequence(
                        android.app.Notification.EXTRA_TITLE,
                    )?.toString()
                    if (!title.isNullOrBlank()) seen = title
                }
        }
        assertNotNull("Beklenen zaman aralığında bildirim düşmedi", seen)
    }
}