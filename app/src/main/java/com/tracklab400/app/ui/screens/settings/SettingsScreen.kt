package com.tracklab400.app.ui.screens.settings

import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.tracklab400.app.R
import com.tracklab400.app.data.updates.UpdateCheckResult
import com.tracklab400.app.data.updates.UpdateChecker
import com.tracklab400.app.data.updates.UpdateDownloader
import com.tracklab400.app.data.updates.UpdateInfo
import com.tracklab400.app.ui.components.TrackLabCard
import com.tracklab400.app.ui.theme.TrackLabSpacing
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    onOpenProfile: () -> Unit = {},
    onOpenNotifications: () -> Unit = {},
    onOpenHistory: () -> Unit = {},
    onOpenAppearance: () -> Unit = {},
) {
    val context = LocalContext.current
    val versionName = remember {
        runCatching {
            context.packageManager.getPackageInfo(context.packageName, 0).versionName
        }.getOrNull() ?: "0.1.0"
    }
    var updateState by remember {
        mutableStateOf<UpdateDialogState>(UpdateDialogState.Idle)
    }
    var aboutDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val startDownload: (UpdateInfo) -> Unit = { info ->
        updateState = UpdateDialogState.Downloading(info)
        scope.launch {
            runCatching {
                val file = UpdateDownloader.downloadToCache(context, info.apkUrl, info.apkFileName)
                val uri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    file,
                )
                Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(uri, "application/vnd.android.package-archive")
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }.also(context::startActivity)
            }.onFailure {
                updateState = UpdateDialogState.DownloadFailed
            }.onSuccess {
                updateState = UpdateDialogState.Idle
            }
        }
    }
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings_title)) },
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
            TrackLabSettingsItem(
                icon = Icons.Default.Person,
                title = stringResource(R.string.settings_profile),
                description = stringResource(R.string.settings_profile_desc),
                onClick = onOpenProfile,
            )
            Spacer(Modifier.width(TrackLabSpacing.sm))
            TrackLabSettingsItem(
                icon = Icons.Default.Notifications,
                title = stringResource(R.string.settings_notifications),
                description = stringResource(R.string.settings_notifications_desc),
                onClick = onOpenNotifications,
            )
            Spacer(Modifier.width(TrackLabSpacing.sm))
            TrackLabSettingsItem(
                icon = Icons.Default.List,
                title = stringResource(R.string.history_title),
                description = stringResource(R.string.settings_history_desc),
                onClick = onOpenHistory,
            )
            Spacer(Modifier.width(TrackLabSpacing.sm))
            TrackLabSettingsItem(
                icon = Icons.Default.DarkMode,
                title = stringResource(R.string.settings_appearance),
                description = stringResource(R.string.settings_appearance_desc),
                onClick = onOpenAppearance,
            )
            Spacer(Modifier.width(TrackLabSpacing.sm))
            TrackLabSettingsItem(
                icon = Icons.Default.Info,
                title = stringResource(R.string.settings_about),
                description = stringResource(R.string.settings_about_desc),
                onClick = { aboutDialog = true },
            )
            Spacer(Modifier.width(TrackLabSpacing.sm))
            TrackLabSettingsItem(
                icon = Icons.Default.SystemUpdate,
                title = stringResource(R.string.settings_update),
                description = stringResource(R.string.settings_update_desc),
                onClick = {
                    updateState = UpdateDialogState.Checking
                    scope.launch {
                        when (val result = UpdateChecker.checkLatest(versionName)) {
                            is UpdateCheckResult.UpdateAvailable ->
                                updateState = UpdateDialogState.Available(result.info)
                            UpdateCheckResult.NoUpdate -> updateState = UpdateDialogState.UpToDate
                            is UpdateCheckResult.CheckFailed ->
                                updateState = UpdateDialogState.CheckFailed
                        }
                    }
                },
            )
        }
    }

    val state = updateState
    when (state) {
        UpdateDialogState.Idle -> Unit

        UpdateDialogState.Checking, is UpdateDialogState.Downloading -> {
            AlertDialog(
                onDismissRequest = { updateState = UpdateDialogState.Idle },
                confirmButton = {},
                icon = {
                    Icon(
                        imageVector = Icons.Default.SystemUpdate,
                        contentDescription = null,
                    )
                },
                title = { Text(stringResource(R.string.settings_update_title)) },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 3.dp,
                        )
                        Spacer(Modifier.width(TrackLabSpacing.md))
                        Text(
                            stringResource(
                                if (state is UpdateDialogState.Downloading) {
                                    R.string.settings_update_downloading
                                } else {
                                    R.string.settings_update_checking
                                },
                            ),
                        )
                    }
                },
            )
        }

        UpdateDialogState.UpToDate -> {
            AlertDialog(
                onDismissRequest = { updateState = UpdateDialogState.Idle },
                icon = {
                    Icon(
                        imageVector = Icons.Default.SystemUpdate,
                        contentDescription = null,
                    )
                },
                title = { Text(stringResource(R.string.settings_update_title)) },
                text = {
                    Text(stringResource(R.string.settings_update_message, versionName))
                },
                confirmButton = {
                    TextButton(onClick = { updateState = UpdateDialogState.Idle }) {
                        Text(stringResource(R.string.action_ok))
                    }
                },
            )
        }

        is UpdateDialogState.Available -> {
            AlertDialog(
                onDismissRequest = { updateState = UpdateDialogState.Idle },
                icon = {
                    Icon(
                        imageVector = Icons.Default.SystemUpdate,
                        contentDescription = null,
                    )
                },
                title = { Text(stringResource(R.string.settings_update_title)) },
                text = {
                    Text(
                        stringResource(
                            R.string.settings_update_available_message,
                            state.info.version,
                        ),
                    )
                },
                confirmButton = {
                    TextButton(onClick = { startDownload(state.info) }) {
                        Text(stringResource(R.string.action_download))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { updateState = UpdateDialogState.Idle }) {
                        Text(stringResource(R.string.action_later))
                    }
                },
            )
        }

        UpdateDialogState.CheckFailed -> {
            AlertDialog(
                onDismissRequest = { updateState = UpdateDialogState.Idle },
                icon = {
                    Icon(
                        imageVector = Icons.Default.SystemUpdate,
                        contentDescription = null,
                    )
                },
                title = { Text(stringResource(R.string.settings_update_title)) },
                text = { Text(stringResource(R.string.settings_update_failed)) },
                confirmButton = {
                    TextButton(onClick = { updateState = UpdateDialogState.Idle }) {
                        Text(stringResource(R.string.action_ok))
                    }
                },
            )
        }

        UpdateDialogState.DownloadFailed -> {
            AlertDialog(
                onDismissRequest = { updateState = UpdateDialogState.Idle },
                icon = {
                    Icon(
                        imageVector = Icons.Default.SystemUpdate,
                        contentDescription = null,
                    )
                },
                title = { Text(stringResource(R.string.settings_update_title)) },
                text = { Text(stringResource(R.string.settings_download_failed)) },
                confirmButton = {
                    TextButton(onClick = { updateState = UpdateDialogState.Idle }) {
                        Text(stringResource(R.string.action_ok))
                    }
                },
            )
        }
    }

    if (aboutDialog) {
        AlertDialog(
            onDismissRequest = { aboutDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                )
            },
            title = { Text(stringResource(R.string.settings_about_title, versionName)) },
            text = { Text(stringResource(R.string.settings_about_attribution)) },
            confirmButton = {
                TextButton(onClick = { aboutDialog = false }) {
                    Text(stringResource(R.string.action_ok))
                }
            },
        )
    }
}

private sealed interface UpdateDialogState {
    data object Idle : UpdateDialogState
    data object Checking : UpdateDialogState
    data object UpToDate : UpdateDialogState
    data class Available(val info: UpdateInfo) : UpdateDialogState
    data class Downloading(val info: UpdateInfo) : UpdateDialogState
    data object DownloadFailed : UpdateDialogState
    data object CheckFailed : UpdateDialogState
}

@Composable
private fun TrackLabSettingsItem(
    icon: ImageVector,
    title: String,
    description: String? = null,
    onClick: () -> Unit = {},
) {
    TrackLabCard(onClick = onClick) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surfaceVariant,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(10.dp)
                        .size(20.dp),
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
            Spacer(Modifier.width(TrackLabSpacing.md))
            Column(Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                )
                if (description != null) {
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
