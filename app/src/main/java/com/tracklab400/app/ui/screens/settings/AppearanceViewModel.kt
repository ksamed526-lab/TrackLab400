package com.tracklab400.app.ui.screens.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.tracklab400.app.TrackLabApplication
import com.tracklab400.app.data.model.ThemeMode
import com.tracklab400.app.data.prefs.UserPreferencesManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AppearanceViewModel(application: Application) : AndroidViewModel(application) {

    private val preferences: UserPreferencesManager =
        (application as TrackLabApplication).container.preferences

    val themeMode: StateFlow<ThemeMode> = preferences.themeMode
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ThemeMode.SYSTEM,
        )

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch { preferences.setThemeMode(mode) }
    }
}