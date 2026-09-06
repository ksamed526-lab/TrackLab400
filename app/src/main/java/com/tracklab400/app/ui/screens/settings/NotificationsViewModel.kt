package com.tracklab400.app.ui.screens.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.tracklab400.app.TrackLabApplication
import com.tracklab400.app.data.model.NotificationSettings
import com.tracklab400.app.data.prefs.UserPreferencesManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NotificationsViewModel(application: Application) : AndroidViewModel(application) {

    private val preferences: UserPreferencesManager =
        (application as TrackLabApplication).container.preferences

    val settings: StateFlow<NotificationSettings> = preferences.notificationSettings
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = NotificationSettings(),
        )

    fun setEnabled(value: Boolean) {
        viewModelScope.launch { preferences.setNotificationsEnabled(value) }
    }

    fun setTrainingDayReminder(value: Boolean) {
        viewModelScope.launch { preferences.setTrainingDayReminder(value) }
    }

    fun setWorkoutStartReminder(value: Boolean) {
        viewModelScope.launch { preferences.setWorkoutStartReminder(value) }
    }

    fun setRestDayReminder(value: Boolean) {
        viewModelScope.launch { preferences.setRestDayReminder(value) }
    }

    fun setWeeklyReviewReminder(value: Boolean) {
        viewModelScope.launch { preferences.setWeeklyReviewReminder(value) }
    }

    fun setTestWeekReminder(value: Boolean) {
        viewModelScope.launch { preferences.setTestWeekReminder(value) }
    }

    fun setCompletionReminder(value: Boolean) {
        viewModelScope.launch { preferences.setCompletionReminder(value) }
    }

    fun setDayReminderTime(hour: Int, minute: Int) {
        viewModelScope.launch { preferences.setDayReminderTime(hour, minute) }
    }

    fun setWorkoutStartTime(hour: Int, minute: Int) {
        viewModelScope.launch { preferences.setWorkoutStartTime(hour, minute) }
    }
}
