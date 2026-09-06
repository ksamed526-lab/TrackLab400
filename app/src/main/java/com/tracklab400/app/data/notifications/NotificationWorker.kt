package com.tracklab400.app.data.notifications

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.tracklab400.app.MainActivity
import com.tracklab400.app.R

class NotificationWorker(
    appContext: Context,
    params: WorkerParameters,
) : Worker(appContext, params) {

    @SuppressLint("MissingPermission")
    override fun doWork(): Result {
        val manager = NotificationManagerCompat.from(applicationContext)
        if (!manager.areNotificationsEnabled()) return Result.success()
        if (!NotificationPermission.canPost(applicationContext)) return Result.success()

        val title = inputData.getString(KEY_TITLE) ?: return Result.success()
        val body = inputData.getString(KEY_BODY).orEmpty()
        val route = inputData.getString(KEY_ROUTE).orEmpty()
        val channel = inputData.getString(KEY_CHANNEL) ?: NotificationChannels.TRAINING

        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                Intent.FLAG_ACTIVITY_CLEAR_TOP or
                Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra(MainActivity.EXTRA_TARGET_ROUTE, route)
        }
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            route.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val notification = NotificationCompat.Builder(applicationContext, channel)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(
                if (channel == NotificationChannels.TRAINING ||
                    channel == NotificationChannels.COMPLETION
                ) {
                    NotificationCompat.PRIORITY_HIGH
                } else {
                    NotificationCompat.PRIORITY_DEFAULT
                },
            )
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        manager.notify(route.hashCode(), notification)
        return Result.success()
    }

    companion object {
        const val KEY_TITLE = "title"
        const val KEY_BODY = "body"
        const val KEY_ROUTE = "route"
        const val KEY_CHANNEL = "channel"
    }
}
