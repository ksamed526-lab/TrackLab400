package com.tracklab400.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.ui.graphics.vector.ImageVector
import com.tracklab400.app.R

object TrackLabRoutes {
    const val START = "start"
    const val WELCOME = "onboarding/welcome"
    const val PROFILE_FORM = "onboarding/profile"
    const val PROFILE_EDIT = "settings/profile"

    const val HOME = "home"
    const val PLAN = "plan"
    const val PROGRESS = "progress"
    const val STRENGTH = "strength"
    const val SETTINGS = "settings"
    const val SETTINGS_NOTIFICATIONS = "settings/notifications"
    const val SETTINGS_APPEARANCE = "settings/appearance"

    // Gelecek adımlarda eklenecek detay rotaları (PRD §12)
    const val WORKOUT = "workout/{weekNumber}/{dayIndex}"
    const val STOPWATCH = "stopwatch/{weekNumber}/{dayIndex}/{blockIndex}"
    const val REST = "rest/{restMs}"
    const val HISTORY = "history"
    const val HISTORY_DETAIL = "history/{sessionId}"
    const val COMPLETION = "completion/{weekNumber}/{dayIndex}"
    const val LOG = "log/{date}"
    const val TEST = "test/new"
    const val RACE = "race/{weekId}"
    const val EXERCISE = "exercise/{exerciseId}"

    fun workout(weekNumber: Int, dayIndex: Int) = "workout/$weekNumber/$dayIndex"
    fun stopwatch(weekNumber: Int, dayIndex: Int, blockIndex: Int) =
        "stopwatch/$weekNumber/$dayIndex/$blockIndex"
    fun rest(restMs: Long) = "rest/$restMs"
    fun historyDetail(sessionId: Long) = "history/$sessionId"
    fun completion(weekNumber: Int, dayIndex: Int) = "completion/$weekNumber/$dayIndex"
    fun log(date: String) = "log/$date"
    fun race(weekId: Long) = "race/$weekId"
    fun exercise(exerciseId: Long) = "exercise/$exerciseId"
}

data class TrackLabBottomItem(
    val route: String,
    val labelRes: Int,
    val icon: ImageVector,
)

val trackLabBottomItems = listOf(
    TrackLabBottomItem(TrackLabRoutes.HOME, R.string.nav_home, Icons.Default.Home),
    TrackLabBottomItem(TrackLabRoutes.PLAN, R.string.nav_plan, Icons.Default.DateRange),
    TrackLabBottomItem(TrackLabRoutes.PROGRESS, R.string.nav_progress, Icons.Default.TrendingUp),
    TrackLabBottomItem(TrackLabRoutes.STRENGTH, R.string.nav_strength, Icons.Default.FitnessCenter),
)

val trackLabBottomRoutes = trackLabBottomItems.map { it.route }.toSet()
