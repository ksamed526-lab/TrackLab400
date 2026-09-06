package com.tracklab400.app.ui.screens.settings

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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tracklab400.app.R
import com.tracklab400.app.ui.components.TrackLabCard
import com.tracklab400.app.ui.theme.TrackLabSpacing

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
    var showUpdateDialog by remember { mutableStateOf(false) }
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
            )
            Spacer(Modifier.width(TrackLabSpacing.sm))
            TrackLabSettingsItem(
                icon = Icons.Default.SystemUpdate,
                title = stringResource(R.string.settings_update),
                description = stringResource(R.string.settings_update_desc),
                onClick = { showUpdateDialog = true },
            )
        }
    }

    if (showUpdateDialog) {
        AlertDialog(
            onDismissRequest = { showUpdateDialog = false },
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
                TextButton(onClick = { showUpdateDialog = false }) {
                    Text(stringResource(R.string.action_ok))
                }
            },
        )
    }
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
