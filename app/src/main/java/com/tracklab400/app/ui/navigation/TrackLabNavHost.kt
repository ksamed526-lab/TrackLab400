package com.tracklab400.app.ui.navigation

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.tracklab400.app.R
import com.tracklab400.app.ui.screens.completion.CompletionScreen
import com.tracklab400.app.ui.screens.completion.CompletionViewModel
import com.tracklab400.app.ui.screens.history.HistoryScreen
import com.tracklab400.app.ui.screens.history.HistoryViewModel
import com.tracklab400.app.ui.screens.home.HomeScreen
import com.tracklab400.app.ui.screens.home.HomeViewModel
import com.tracklab400.app.ui.screens.onboarding.StartRoute
import com.tracklab400.app.ui.screens.onboarding.WelcomeScreen
import com.tracklab400.app.ui.screens.plan.PlanScreen
import com.tracklab400.app.ui.screens.plan.PlanViewModel
import com.tracklab400.app.ui.screens.profile.EditProfileScreen
import com.tracklab400.app.ui.screens.profile.ProfileFormCallbacks
import com.tracklab400.app.ui.screens.profile.ProfileFormScreen
import com.tracklab400.app.ui.screens.profile.ProfileViewModel
import com.tracklab400.app.ui.screens.progress.ProgressScreen
import com.tracklab400.app.ui.screens.progress.ProgressViewModel
import com.tracklab400.app.ui.screens.exercises.ExerciseDetailScreen
import com.tracklab400.app.ui.screens.exercises.ExerciseDetailViewModel
import com.tracklab400.app.ui.screens.exercises.ExerciseLibraryScreen
import com.tracklab400.app.ui.screens.rest.RestScreen
import com.tracklab400.app.ui.screens.rest.RestViewModel
import com.tracklab400.app.ui.screens.settings.AppearanceScreen
import com.tracklab400.app.ui.screens.settings.AppearanceViewModel
import com.tracklab400.app.ui.screens.settings.NotificationsSettingsScreen
import com.tracklab400.app.ui.screens.settings.NotificationsViewModel
import com.tracklab400.app.ui.screens.settings.SettingsScreen
import com.tracklab400.app.ui.screens.stopwatch.StopwatchScreen
import com.tracklab400.app.ui.screens.stopwatch.StopwatchViewModel
import com.tracklab400.app.ui.screens.workout.WorkoutDetailScreen
import com.tracklab400.app.ui.screens.workout.WorkoutViewModel

@Composable
fun TrackLabNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = TrackLabRoutes.START,
        modifier = modifier,
    ) {
        composable(TrackLabRoutes.START) {
            val viewModel: ProfileViewModel = viewModel()
            StartRoute(
                onboardingCompleted = viewModel.onboardingCompleted,
                onNavigateToOnboarding = {
                    navController.navigate(TrackLabRoutes.WELCOME) {
                        popUpTo(TrackLabRoutes.START) { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    navController.navigate(TrackLabRoutes.HOME) {
                        popUpTo(TrackLabRoutes.START) { inclusive = true }
                    }
                },
            )
        }
        composable(TrackLabRoutes.WELCOME) {
            val viewModel: ProfileViewModel = viewModel()
            WelcomeScreen(
                events = viewModel.events,
                onCreateProfile = { navController.navigate(TrackLabRoutes.PROFILE_FORM) },
                onTryDemo = viewModel::applyDemo,
                onDemoApplied = {
                    navController.navigate(TrackLabRoutes.HOME) {
                        popUpTo(TrackLabRoutes.START) { inclusive = true }
                    }
                },
            )
        }
        composable(TrackLabRoutes.PROFILE_FORM) {
            val viewModel: ProfileViewModel = viewModel()
            val formState by viewModel.formState.collectAsStateWithLifecycle()
            ProfileFormScreen(
                formState = formState,
                events = viewModel.events,
                callbacks = profileFormCallbacks(viewModel),
                onDone = {
                    navController.navigate(TrackLabRoutes.HOME) {
                        popUpTo(TrackLabRoutes.START) { inclusive = true }
                    }
                },
            )
        }
        composable(TrackLabRoutes.PROFILE_EDIT) {
            val viewModel: ProfileViewModel = viewModel()
            val formState by viewModel.formState.collectAsStateWithLifecycle()
            val isDemo by viewModel.isDemoMode.collectAsStateWithLifecycle(initialValue = false)
            EditProfileScreen(
                formState = formState,
                events = viewModel.events,
                callbacks = profileFormCallbacks(viewModel),
                isDemo = isDemo,
                onBack = { navController.popBackStack() },
                onDelete = viewModel::deleteProfile,
                onDeleted = {
                    navController.navigate(TrackLabRoutes.START) {
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
                },
            )
        }
        composable(TrackLabRoutes.HOME) {
            val viewModel: HomeViewModel = viewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            HomeScreen(
                uiState = uiState,
                onOpenSettings = { navController.navigate(TrackLabRoutes.SETTINGS) },
                onOpenPlan = { navController.navigate(TrackLabRoutes.PLAN) },
                onOpenWorkout = { weekNumber, dayIndex ->
                    navController.navigate(TrackLabRoutes.workout(weekNumber, dayIndex))
                },
            )
        }
        composable(TrackLabRoutes.PLAN) {
            val viewModel: PlanViewModel = viewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            PlanScreen(
                uiState = uiState,
                onSelectWeek = viewModel::selectWeek,
                onOpenSession = { weekNumber, dayIndex ->
                    navController.navigate(TrackLabRoutes.workout(weekNumber, dayIndex))
                },
            )
        }
        composable(
            route = TrackLabRoutes.WORKOUT,
            arguments = listOf(
                navArgument("weekNumber") { type = NavType.IntType },
                navArgument("dayIndex") { type = NavType.IntType },
            ),
        ) { backStackEntry ->
            val weekNumber = backStackEntry.arguments?.getInt("weekNumber") ?: 1
            val dayIndex = backStackEntry.arguments?.getInt("dayIndex") ?: 1
            val viewModel: WorkoutViewModel = viewModel(
                factory = viewModelFactory {
                    initializer {
                        val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]
                            as Application
                        WorkoutViewModel(app, weekNumber, dayIndex)
                    }
                },
            )
            val session by viewModel.session.collectAsStateWithLifecycle()
            WorkoutDetailScreen(
                session = session,
                events = viewModel.events,
                onMarkStatus = viewModel::markStatus,
                onOpenStopwatch = { blockIndex ->
                    navController.navigate(TrackLabRoutes.stopwatch(weekNumber, dayIndex, blockIndex))
                },
                onOpenCompletion = {
                    navController.navigate(TrackLabRoutes.completion(weekNumber, dayIndex))
                },
                onOpenExercise = { blockIndex ->
                    navController.navigate(
                        TrackLabRoutes.sessionExercise(weekNumber, dayIndex, blockIndex),
                    )
                },
                onOpenLibraryExercise = { id ->
                    navController.navigate(TrackLabRoutes.exercise(id))
                },
                onBack = { navController.popBackStack() },
            )
        }
        composable(
            route = TrackLabRoutes.STOPWATCH,
            arguments = listOf(
                navArgument("weekNumber") { type = NavType.IntType },
                navArgument("dayIndex") { type = NavType.IntType },
                navArgument("blockIndex") { type = NavType.IntType },
            ),
        ) { backStackEntry ->
            val weekNumber = backStackEntry.arguments?.getInt("weekNumber") ?: 1
            val dayIndex = backStackEntry.arguments?.getInt("dayIndex") ?: 1
            val blockIndex = backStackEntry.arguments?.getInt("blockIndex") ?: 0
            val viewModel: StopwatchViewModel = viewModel(
                factory = viewModelFactory {
                    initializer {
                        val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]
                            as Application
                        StopwatchViewModel(app, weekNumber, dayIndex, blockIndex)
                    }
                },
            )
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            StopwatchScreen(
                uiState = uiState,
                onStart = viewModel::start,
                onPause = viewModel::pause,
                onResume = viewModel::resume,
                onLap = viewModel::recordLap,
                onFinish = viewModel::finish,
                onReset = viewModel::reset,
                onCorrectRep = viewModel::correctRep,
                onOpenRest = { restMs ->
                    viewModel.pause()
                    navController.navigate(TrackLabRoutes.rest(restMs))
                },
                onSetVisible = viewModel::setVisible,
                onBack = { navController.popBackStack() },
            )
        }
        composable(
            route = TrackLabRoutes.REST,
            arguments = listOf(
                navArgument("restMs") { type = NavType.LongType },
            ),
        ) { backStackEntry ->
            val restMs = backStackEntry.arguments?.getLong("restMs") ?: 60_000L
            val viewModel: RestViewModel = viewModel(
                factory = viewModelFactory {
                    initializer {
                        val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]
                            as Application
                        RestViewModel(app, restMs)
                    }
                },
            )
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            RestScreen(
                uiState = uiState,
                onEnsureStarted = viewModel::ensureStarted,
                onToggleRunning = viewModel::toggleRunning,
                onAdd10 = { viewModel.addSeconds(10) },
                onSub10 = { viewModel.addSeconds(-10) },
                onSkip = viewModel::skip,
                onKeepScreenOnChange = viewModel::setKeepScreenOn,
                onSetVisible = viewModel::setVisible,
                onBack = { navController.popBackStack() },
            )
        }
        composable(
            route = TrackLabRoutes.COMPLETION,
            arguments = listOf(
                navArgument("weekNumber") { type = NavType.IntType },
                navArgument("dayIndex") { type = NavType.IntType },
            ),
        ) { backStackEntry ->
            val weekNumber = backStackEntry.arguments?.getInt("weekNumber") ?: 1
            val dayIndex = backStackEntry.arguments?.getInt("dayIndex") ?: 1
            val viewModel: CompletionViewModel = viewModel(
                factory = viewModelFactory {
                    initializer {
                        val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]
                            as Application
                        CompletionViewModel(app, weekNumber, dayIndex)
                    }
                },
            )
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            val saved by viewModel.saved.collectAsStateWithLifecycle()
            LaunchedEffect(saved) {
                if (saved) navController.popBackStack()
            }
            CompletionScreen(
                uiState = uiState,
                onAddRep = viewModel::addRep,
                onUndoLastRep = viewModel::undoLastRep,
                onSetRpe = viewModel::setRpe,
                onSetSleepHours = viewModel::setSleepHours,
                onSetEnergy = viewModel::setEnergyLevel,
                onSetLegFeeling = viewModel::setLegFeeling,
                onSetPain = viewModel::setPain,
                onClearPain = viewModel::clearPain,
                onShowPainWarning = viewModel::showPainWarning,
                onDismissPainWarning = viewModel::dismissPainWarning,
                onSetNotes = viewModel::setNotes,
                onSave = viewModel::save,
                onBack = { navController.popBackStack() },
            )
        }
        composable(TrackLabRoutes.HISTORY) {
            val viewModel: HistoryViewModel = viewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            HistoryScreen(
                uiState = uiState,
                onFilterStatusChange = viewModel::setFilterStatus,
                onSearchChange = viewModel::setSearchQuery,
                onClearFilters = viewModel::clearFilters,
                onDelete = viewModel::deleteRecord,
                onBack = { navController.popBackStack() },
            )
        }
        composable(TrackLabRoutes.PROGRESS) {
            val viewModel: ProgressViewModel = viewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            ProgressScreen(
                uiState = uiState,
                onRangeChange = viewModel::setRange,
            )
        }
        composable(TrackLabRoutes.STRENGTH) {
            ExerciseLibraryScreen(
                onOpenExercise = { id ->
                    navController.navigate(TrackLabRoutes.exercise(id))
                },
            )
        }
        composable(
            route = TrackLabRoutes.EXERCISE,
            arguments = listOf(
                navArgument("exerciseId") { type = NavType.StringType },
            ),
        ) { backStackEntry ->
            val exerciseId = backStackEntry.arguments?.getString("exerciseId").orEmpty()
            val viewModel: ExerciseDetailViewModel = viewModel(
                factory = viewModelFactory {
                    initializer {
                        val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]
                            as Application
                        ExerciseDetailViewModel(app, exerciseId = exerciseId)
                    }
                },
            )
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            ExerciseDetailScreen(
                uiState = uiState,
                onOpenStopwatch = null,
                onBack = { navController.popBackStack() },
            )
        }
        composable(
            route = TrackLabRoutes.SESSION_EXERCISE,
            arguments = listOf(
                navArgument("weekNumber") { type = NavType.IntType },
                navArgument("dayIndex") { type = NavType.IntType },
                navArgument("blockIndex") { type = NavType.IntType },
            ),
        ) { backStackEntry ->
            val weekNumber = backStackEntry.arguments?.getInt("weekNumber") ?: 1
            val dayIndex = backStackEntry.arguments?.getInt("dayIndex") ?: 1
            val blockIndex = backStackEntry.arguments?.getInt("blockIndex") ?: 0
            val exerciseViewModel: ExerciseDetailViewModel = viewModel(
                factory = viewModelFactory {
                    initializer {
                        val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]
                            as Application
                        ExerciseDetailViewModel(app, weekNumber, dayIndex, blockIndex)
                    }
                },
            )
            val exerciseUiState by exerciseViewModel.uiState.collectAsStateWithLifecycle()
            ExerciseDetailScreen(
                uiState = exerciseUiState,
                onOpenStopwatch = {
                    navController.navigate(
                        TrackLabRoutes.stopwatch(weekNumber, dayIndex, blockIndex),
                    )
                },
                onBack = { navController.popBackStack() },
            )
        }
        composable(TrackLabRoutes.SETTINGS) {
            SettingsScreen(
                onBack = { navController.popBackStack() },
                onOpenProfile = { navController.navigate(TrackLabRoutes.PROFILE_EDIT) },
                onOpenNotifications = {
                    navController.navigate(TrackLabRoutes.SETTINGS_NOTIFICATIONS)
                },
                onOpenHistory = { navController.navigate(TrackLabRoutes.HISTORY) },
                onOpenAppearance = {
                    navController.navigate(TrackLabRoutes.SETTINGS_APPEARANCE)
                },
            )
        }
        composable(TrackLabRoutes.SETTINGS_APPEARANCE) {
            val viewModel: AppearanceViewModel = viewModel()
            val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
            AppearanceScreen(
                themeMode = themeMode,
                onThemeModeChange = viewModel::setThemeMode,
                onBack = { navController.popBackStack() },
            )
        }
        composable(TrackLabRoutes.SETTINGS_NOTIFICATIONS) {
            val viewModel: NotificationsViewModel = viewModel()
            val settings by viewModel.settings.collectAsStateWithLifecycle()
            NotificationsSettingsScreen(
                settings = settings,
                onEnabledChange = viewModel::setEnabled,
                onTrainingDayReminderChange = viewModel::setTrainingDayReminder,
                onWorkoutStartReminderChange = viewModel::setWorkoutStartReminder,
                onRestDayReminderChange = viewModel::setRestDayReminder,
                onWeeklyReviewReminderChange = viewModel::setWeeklyReviewReminder,
                onTestWeekReminderChange = viewModel::setTestWeekReminder,
                onCompletionReminderChange = viewModel::setCompletionReminder,
                onDayTimeChange = viewModel::setDayReminderTime,
                onStartTimeChange = viewModel::setWorkoutStartTime,
                onBack = { navController.popBackStack() },
            )
        }
    }
}

private fun profileFormCallbacks(viewModel: ProfileViewModel): ProfileFormCallbacks =
    ProfileFormCallbacks(
        onNicknameChange = viewModel::updateNickname,
        onAgeChange = viewModel::updateAge,
        onCurrent100Change = viewModel::updateCurrent100,
        onCurrent200Change = viewModel::updateCurrent200,
        onCurrent300Change = viewModel::updateCurrent300,
        onCurrent400Change = viewModel::updateCurrent400,
        onTargetDistanceChange = viewModel::selectTargetDistance,
        onTargetTimeChange = viewModel::updateTargetTime,
        onPrepWeeksChange = viewModel::selectPrepWeeks,
        onTrainingDaysChange = viewModel::selectTrainingDays,
        onStartDateChange = viewModel::selectStartDate,
        onTrackAccessChange = viewModel::selectTrackAccess,
        onGymStatusChange = viewModel::selectGymStatus,
        onEquipmentToggle = viewModel::toggleEquipment,
        onExperienceChange = viewModel::selectExperience,
        onInjuryChange = viewModel::updateInjury,
        onStopwatchTypeChange = viewModel::selectStopwatchType,
        onSave = viewModel::save,
    )
