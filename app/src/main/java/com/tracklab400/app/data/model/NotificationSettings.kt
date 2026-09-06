package com.tracklab400.app.data.model

data class NotificationSettings(
    val enabled: Boolean = true,
    val trainingDayReminder: Boolean = true,
    val workoutStartReminder: Boolean = true,
    val restDayReminder: Boolean = true,
    val weeklyReviewReminder: Boolean = true,
    val testWeekReminder: Boolean = true,
    val completionReminder: Boolean = true,
    val dayReminderHour: Int = 9,
    val dayReminderMinute: Int = 0,
    val workoutStartHour: Int = 18,
    val workoutStartMinute: Int = 30,
)
