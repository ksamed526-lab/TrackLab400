package com.tracklab400.app.data.prefs

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.tracklab400.app.data.model.NotificationSettings
import com.tracklab400.app.data.model.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.userDataStore by preferencesDataStore(name = "user_preferences")

class UserPreferencesManager(private val context: Context) {

    private object Keys {
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        val DEMO_MODE = booleanPreferencesKey("demo_mode")
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val DAY_REMINDER_ENABLED = booleanPreferencesKey("day_reminder_enabled")
        val START_REMINDER_ENABLED = booleanPreferencesKey("start_reminder_enabled")
        val REST_REMINDER_ENABLED = booleanPreferencesKey("rest_reminder_enabled")
        val WEEKLY_REMINDER_ENABLED = booleanPreferencesKey("weekly_reminder_enabled")
        val TEST_REMINDER_ENABLED = booleanPreferencesKey("test_reminder_enabled")
        val COMPLETION_REMINDER_ENABLED = booleanPreferencesKey("completion_reminder_enabled")
        val DAY_HOUR = intPreferencesKey("day_reminder_hour")
        val DAY_MINUTE = intPreferencesKey("day_reminder_minute")
        val START_HOUR = intPreferencesKey("start_reminder_hour")
        val START_MINUTE = intPreferencesKey("start_reminder_minute")
        val THEME_MODE = stringPreferencesKey("theme_mode")
    }

    val onboardingCompleted: Flow<Boolean?> = context.userDataStore.data
        .map { it[Keys.ONBOARDING_COMPLETED] }

    val isDemoMode: Flow<Boolean> = context.userDataStore.data
        .map { it[Keys.DEMO_MODE] ?: false }

    val themeMode: Flow<ThemeMode> = context.userDataStore.data
        .map { ThemeMode.fromStoredValue(it[Keys.THEME_MODE]) }

    val notificationsEnabled: Flow<Boolean> = context.userDataStore.data
        .map { it[Keys.NOTIFICATIONS_ENABLED] ?: true }

    val notificationSettings: Flow<NotificationSettings> = context.userDataStore.data
        .map { prefs ->
            NotificationSettings(
                enabled = prefs[Keys.NOTIFICATIONS_ENABLED] ?: true,
                trainingDayReminder = prefs[Keys.DAY_REMINDER_ENABLED] ?: true,
                workoutStartReminder = prefs[Keys.START_REMINDER_ENABLED] ?: true,
                restDayReminder = prefs[Keys.REST_REMINDER_ENABLED] ?: true,
                weeklyReviewReminder = prefs[Keys.WEEKLY_REMINDER_ENABLED] ?: true,
                testWeekReminder = prefs[Keys.TEST_REMINDER_ENABLED] ?: true,
                completionReminder = prefs[Keys.COMPLETION_REMINDER_ENABLED] ?: true,
                dayReminderHour = prefs[Keys.DAY_HOUR] ?: 9,
                dayReminderMinute = prefs[Keys.DAY_MINUTE] ?: 0,
                workoutStartHour = prefs[Keys.START_HOUR] ?: 18,
                workoutStartMinute = prefs[Keys.START_MINUTE] ?: 30,
            )
        }

    suspend fun setOnboardingCompleted(value: Boolean) {
        context.userDataStore.edit { it[Keys.ONBOARDING_COMPLETED] = value }
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.userDataStore.edit { it[Keys.THEME_MODE] = mode.storedValue }
    }

    suspend fun setDemoMode(value: Boolean) {
        context.userDataStore.edit { it[Keys.DEMO_MODE] = value }
    }

    suspend fun setNotificationsEnabled(value: Boolean) {
        context.userDataStore.edit { it[Keys.NOTIFICATIONS_ENABLED] = value }
    }

    suspend fun setTrainingDayReminder(value: Boolean) {
        context.userDataStore.edit { it[Keys.DAY_REMINDER_ENABLED] = value }
    }

    suspend fun setWorkoutStartReminder(value: Boolean) {
        context.userDataStore.edit { it[Keys.START_REMINDER_ENABLED] = value }
    }

    suspend fun setRestDayReminder(value: Boolean) {
        context.userDataStore.edit { it[Keys.REST_REMINDER_ENABLED] = value }
    }

    suspend fun setWeeklyReviewReminder(value: Boolean) {
        context.userDataStore.edit { it[Keys.WEEKLY_REMINDER_ENABLED] = value }
    }

    suspend fun setTestWeekReminder(value: Boolean) {
        context.userDataStore.edit { it[Keys.TEST_REMINDER_ENABLED] = value }
    }

    suspend fun setCompletionReminder(value: Boolean) {
        context.userDataStore.edit { it[Keys.COMPLETION_REMINDER_ENABLED] = value }
    }

    suspend fun setDayReminderTime(hour: Int, minute: Int) {
        context.userDataStore.edit {
            it[Keys.DAY_HOUR] = hour
            it[Keys.DAY_MINUTE] = minute
        }
    }

    suspend fun setWorkoutStartTime(hour: Int, minute: Int) {
        context.userDataStore.edit {
            it[Keys.START_HOUR] = hour
            it[Keys.START_MINUTE] = minute
        }
    }
}
