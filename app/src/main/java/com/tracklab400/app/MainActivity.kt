package com.tracklab400.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tracklab400.app.data.model.ThemeMode
import com.tracklab400.app.data.model.resolveDarkTheme
import com.tracklab400.app.ui.theme.TrackLabTheme

class MainActivity : ComponentActivity() {

    private var pendingRoute by mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        pendingRoute = intent.getStringExtra(EXTRA_TARGET_ROUTE)
        val preferences = (application as TrackLabApplication).container.preferences
        setContent {
            val themeMode by preferences.themeMode
                .collectAsStateWithLifecycle(initialValue = ThemeMode.SYSTEM)
            val darkTheme = resolveDarkTheme(themeMode, isSystemInDarkTheme())
            TrackLabTheme(
                darkTheme = darkTheme,
                pinkTheme = themeMode == ThemeMode.PINK,
            ) {
                TrackLabApp(
                    pendingRoute = pendingRoute,
                    onPendingRouteConsumed = { pendingRoute = null },
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        pendingRoute = intent.getStringExtra(EXTRA_TARGET_ROUTE)
    }

    companion object {
        const val EXTRA_TARGET_ROUTE = "target_route"
    }
}
