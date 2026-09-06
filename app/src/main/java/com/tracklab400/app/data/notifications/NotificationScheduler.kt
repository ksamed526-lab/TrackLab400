package com.tracklab400.app.data.notifications

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.tracklab400.app.data.model.NotificationSettings
import com.tracklab400.app.data.model.TrainingPlan
import java.util.concurrent.TimeUnit

class NotificationScheduler(private val context: Context) {

    fun reschedule(
        settings: NotificationSettings,
        plan: TrainingPlan?,
        startDateMillis: Long?,
    ) {
        val workManager = WorkManager.getInstance(context)
        workManager.cancelAllWorkByTag(TAG)

        val requests = NotificationPlanner.buildRequests(settings, plan, startDateMillis)
        requests.forEach { request ->
            val work = OneTimeWorkRequestBuilder<NotificationWorker>()
                .setInitialDelay(request.delayMs, TimeUnit.MILLISECONDS)
                .setInputData(
                    workDataOf(
                        NotificationWorker.KEY_TITLE to request.title,
                        NotificationWorker.KEY_BODY to request.body,
                        NotificationWorker.KEY_ROUTE to request.route,
                        NotificationWorker.KEY_CHANNEL to request.channelId,
                    ),
                )
                .addTag(TAG)
                .build()
            workManager.enqueueUniqueWork(
                request.uniqueName,
                ExistingWorkPolicy.REPLACE,
                work,
            )
        }
    }

    companion object {
        const val TAG = "tracklab_reminder"
    }
}
