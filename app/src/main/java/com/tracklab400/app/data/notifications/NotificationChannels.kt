package com.tracklab400.app.data.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationManagerCompat

object NotificationChannels {

    const val TRAINING = "training_reminders"
    const val REST = "rest_reminders"
    const val WEEKLY = "weekly_review"
    const val COMPLETION = "program_completion"

    fun create(context: Context) {
        val manager = NotificationManagerCompat.from(context)
        listOf(
            NotificationChannel(
                TRAINING,
                "Antrenman Hatırlatmaları",
                NotificationManager.IMPORTANCE_HIGH,
            ).apply {
                description = "Antrenman günü ve başlangıç hatırlatmaları"
            },
            NotificationChannel(
                REST,
                "Dinlenme Hatırlatmaları",
                NotificationManager.IMPORTANCE_DEFAULT,
            ).apply {
                description = "Dinlenme günü hatırlatmaları"
            },
            NotificationChannel(
                WEEKLY,
                "Haftalık Değerlendirme",
                NotificationManager.IMPORTANCE_DEFAULT,
            ).apply {
                description = "Pazar günü haftalık değerlendirme hatırlatması"
            },
            NotificationChannel(
                COMPLETION,
                "Program Tamamlanma",
                NotificationManager.IMPORTANCE_HIGH,
            ).apply {
                description = "Program tamamlanma bildirimi"
            },
        ).forEach { manager.createNotificationChannel(it) }
    }
}
