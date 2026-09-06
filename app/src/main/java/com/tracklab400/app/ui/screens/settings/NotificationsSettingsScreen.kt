package com.tracklab400.app.ui.screens.settings

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.core.content.ContextCompat
import com.tracklab400.app.R
import com.tracklab400.app.data.notifications.NotificationPermission
import com.tracklab400.app.data.model.NotificationSettings
import com.tracklab400.app.ui.components.TrackLabCard
import com.tracklab400.app.ui.components.TrackLabSectionHeader
import com.tracklab400.app.ui.theme.TrackLabSpacing
import com.tracklab400.app.ui.theme.trackLabColors

private enum class PickerTarget { DAY_TIME, START_TIME }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsSettingsScreen(
    settings: NotificationSettings,
    onEnabledChange: (Boolean) -> Unit,
    onTrainingDayReminderChange: (Boolean) -> Unit,
    onWorkoutStartReminderChange: (Boolean) -> Unit,
    onRestDayReminderChange: (Boolean) -> Unit,
    onWeeklyReviewReminderChange: (Boolean) -> Unit,
    onTestWeekReminderChange: (Boolean) -> Unit,
    onCompletionReminderChange: (Boolean) -> Unit,
    onDayTimeChange: (Int, Int) -> Unit,
    onStartTimeChange: (Int, Int) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    var hasPermission by remember {
        mutableStateOf(NotificationPermission.canPost(context))
    }
    var pickerTarget by remember { mutableStateOf<PickerTarget?>(null) }

    LifecycleResumeEffect(Unit) {
        hasPermission = NotificationPermission.canPost(context)
        onPauseOrDispose { }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted ->
        hasPermission = granted
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings_notifications)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(TrackLabSpacing.md),
        ) {
            TrackLabSectionHeader(title = stringResource(R.string.notif_general))
            Spacer(Modifier.height(TrackLabSpacing.sm))
            TrackLabCard {
                SwitchRow(
                    title = stringResource(R.string.notif_master),
                    description = stringResource(R.string.notif_master_desc),
                    checked = settings.enabled,
                    onCheckedChange = { value ->
                        if (value && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                            !hasPermission
                        ) {
                            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                        onEnabledChange(value)
                    },
                )
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !hasPermission) {
                Spacer(Modifier.height(TrackLabSpacing.sm))
                TrackLabCard {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = null,
                            tint = MaterialTheme.trackLabColors.warning,
                        )
                        Spacer(Modifier.padding(TrackLabSpacing.sm))
                        Column(Modifier.weight(1f)) {
                            Text(
                                text = stringResource(R.string.notif_permission_title),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                            )
                            Text(
                                text = stringResource(R.string.notif_permission_desc),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                    Spacer(Modifier.height(TrackLabSpacing.sm))
                    TextButton(
                        onClick = {
                            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        },
                    ) {
                        Text(stringResource(R.string.notif_grant))
                    }
                    TextButton(
                        onClick = {
                            context.startActivity(
                                Intent(
                                    Settings.ACTION_APP_NOTIFICATION_SETTINGS,
                                ).apply {
                                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    putExtra(
                                        Settings.EXTRA_APP_PACKAGE,
                                        context.packageName,
                                    )
                                },
                            )
                        },
                    ) {
                        Text(stringResource(R.string.notif_open_system_settings))
                    }
                }
            }

            Spacer(Modifier.height(TrackLabSpacing.lg))
            TrackLabSectionHeader(title = stringResource(R.string.notif_types))
            Spacer(Modifier.height(TrackLabSpacing.sm))
            TrackLabCard {
                Column {
                    SwitchRow(
                        title = stringResource(R.string.notif_day_title),
                        description = stringResource(R.string.notif_day_desc),
                        checked = settings.trainingDayReminder,
                        onCheckedChange = onTrainingDayReminderChange,
                    )
                    Spacer(Modifier.height(TrackLabSpacing.sm))
                    SwitchRow(
                        title = stringResource(R.string.notif_start_title),
                        description = stringResource(R.string.notif_start_desc),
                        checked = settings.workoutStartReminder,
                        onCheckedChange = onWorkoutStartReminderChange,
                    )
                    Spacer(Modifier.height(TrackLabSpacing.sm))
                    SwitchRow(
                        title = stringResource(R.string.notif_rest_title),
                        description = stringResource(R.string.notif_rest_desc),
                        checked = settings.restDayReminder,
                        onCheckedChange = onRestDayReminderChange,
                    )
                    Spacer(Modifier.height(TrackLabSpacing.sm))
                    SwitchRow(
                        title = stringResource(R.string.notif_weekly_title),
                        description = stringResource(R.string.notif_weekly_desc),
                        checked = settings.weeklyReviewReminder,
                        onCheckedChange = onWeeklyReviewReminderChange,
                    )
                    Spacer(Modifier.height(TrackLabSpacing.sm))
                    SwitchRow(
                        title = stringResource(R.string.notif_test_title),
                        description = stringResource(R.string.notif_test_desc),
                        checked = settings.testWeekReminder,
                        onCheckedChange = onTestWeekReminderChange,
                    )
                    Spacer(Modifier.height(TrackLabSpacing.sm))
                    SwitchRow(
                        title = stringResource(R.string.notif_completion_title),
                        description = stringResource(R.string.notif_completion_desc),
                        checked = settings.completionReminder,
                        onCheckedChange = onCompletionReminderChange,
                    )
                }
            }

            Spacer(Modifier.height(TrackLabSpacing.lg))
            TrackLabSectionHeader(title = stringResource(R.string.notif_times))
            Spacer(Modifier.height(TrackLabSpacing.sm))
            TrackLabCard {
                TimeRow(
                    label = stringResource(R.string.notif_day_time),
                    value = stringResource(
                        R.string.notif_time_format,
                        settings.dayReminderHour,
                        settings.dayReminderMinute,
                    ),
                    onClick = { pickerTarget = PickerTarget.DAY_TIME },
                )
                Spacer(Modifier.height(TrackLabSpacing.sm))
                TimeRow(
                    label = stringResource(R.string.notif_start_time),
                    value = stringResource(
                        R.string.notif_time_format,
                        settings.workoutStartHour,
                        settings.workoutStartMinute,
                    ),
                    onClick = { pickerTarget = PickerTarget.START_TIME },
                )
            }

            Spacer(Modifier.height(TrackLabSpacing.lg))
        }
    }

    pickerTarget?.let { target ->
        val initialHour = if (target == PickerTarget.DAY_TIME) {
            settings.dayReminderHour
        } else {
            settings.workoutStartHour
        }
        val initialMinute = if (target == PickerTarget.DAY_TIME) {
            settings.dayReminderMinute
        } else {
            settings.workoutStartMinute
        }
        val state = rememberTimePickerState(
            initialHour = initialHour,
            initialMinute = initialMinute,
            is24Hour = true,
        )
        AlertDialog(
            onDismissRequest = { pickerTarget = null },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (target == PickerTarget.DAY_TIME) {
                            onDayTimeChange(state.hour, state.minute)
                        } else {
                            onStartTimeChange(state.hour, state.minute)
                        }
                        pickerTarget = null
                    },
                ) {
                    Text(stringResource(R.string.action_confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { pickerTarget = null }) {
                    Text(stringResource(R.string.action_cancel))
                }
            },
            text = {
                TimePicker(state = state)
            },
        )
    }
}

@Composable
private fun SwitchRow(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun TimeRow(
    label: String,
    value: String,
    onClick: () -> Unit,
) {
    TrackLabCard(onClick = onClick) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}
