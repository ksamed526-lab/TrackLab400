package com.tracklab400.app.ui.screens.rest

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.LifecycleResumeEffect
import com.tracklab400.app.R
import com.tracklab400.app.data.model.TimeUtils
import com.tracklab400.app.ui.components.TrackLabButton
import com.tracklab400.app.ui.components.TrackLabCard
import com.tracklab400.app.ui.components.TrackLabOutlinedButton
import com.tracklab400.app.ui.theme.Cream50
import com.tracklab400.app.ui.theme.Lime400
import com.tracklab400.app.ui.theme.Navy800
import com.tracklab400.app.ui.theme.TrackLabSpacing
import com.tracklab400.app.ui.theme.statValue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestScreen(
    uiState: RestUiState,
    onEnsureStarted: () -> Unit,
    onToggleRunning: () -> Unit,
    onAdd10: () -> Unit,
    onSub10: () -> Unit,
    onSkip: () -> Unit,
    onKeepScreenOnChange: (Boolean) -> Unit,
    onSetVisible: (Boolean) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LifecycleResumeEffect(Unit) {
        onSetVisible(true)
        onPauseOrDispose { onSetVisible(false) }
    }

    LaunchedEffect(Unit) {
        onEnsureStarted()
    }

    val view = LocalView.current
    DisposableEffect(uiState.keepScreenOn) {
        view.keepScreenOn = uiState.keepScreenOn
        onDispose { view.keepScreenOn = false }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.rest_title)) },
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
                .padding(TrackLabSpacing.md),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(TrackLabSpacing.lg))
            TrackLabCard(
                containerColor = Navy800,
                contentColor = Cream50,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = stringResource(R.string.rest_remaining),
                    style = MaterialTheme.typography.labelMedium,
                    color = Cream50.copy(alpha = 0.7f),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                )
                val remainingText = TimeUtils.formatClock(
                    ((uiState.remainingMs + 999L) / 1000L) * 1000L,
                )
                val remainingDescription =
                    stringResource(R.string.rest_remaining_desc, remainingText)
                Text(
                    text = remainingText,
                    style = MaterialTheme.typography.statValue.copy(
                        fontSize = 64.sp,
                        lineHeight = 72.sp,
                        fontFamily = FontFamily.Monospace,
                    ),
                    color = if (uiState.finished) MaterialTheme.colorScheme.error else Lime400,
                    modifier = Modifier
                        .fillMaxWidth()
                        .semantics {
                            contentDescription = remainingDescription
                        },
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(TrackLabSpacing.sm))
                Text(
                    text = stringResource(
                        R.string.rest_initial,
                        TimeUtils.formatDuration(uiState.initialMs),
                    ),
                    style = MaterialTheme.typography.labelMedium,
                    color = Cream50.copy(alpha = 0.7f),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                )
            }

            Spacer(Modifier.height(TrackLabSpacing.lg))

            if (uiState.finished) {
                Text(
                    text = stringResource(R.string.rest_finished_title),
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(TrackLabSpacing.xs))
                Text(
                    text = stringResource(R.string.rest_finished_desc),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(TrackLabSpacing.lg))
                TrackLabButton(
                    text = stringResource(R.string.rest_next_rep),
                    onClick = onBack,
                    modifier = Modifier.fillMaxWidth(),
                )
            } else {
                TrackLabButton(
                    text = stringResource(
                        if (uiState.isRunning) R.string.rest_pause else R.string.rest_resume,
                    ),
                    onClick = onToggleRunning,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(TrackLabSpacing.sm))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(TrackLabSpacing.sm),
                ) {
                    TrackLabOutlinedButton(
                        text = stringResource(R.string.rest_sub10),
                        onClick = onSub10,
                        modifier = Modifier.weight(1f),
                    )
                    TrackLabOutlinedButton(
                        text = stringResource(R.string.rest_add10),
                        onClick = onAdd10,
                        modifier = Modifier.weight(1f),
                    )
                }
                Spacer(Modifier.height(TrackLabSpacing.sm))
                TrackLabOutlinedButton(
                    text = stringResource(R.string.rest_skip),
                    onClick = onSkip,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            Spacer(Modifier.height(TrackLabSpacing.xl))
            TrackLabCard(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.rest_keep_screen),
                            style = MaterialTheme.typography.titleSmall,
                        )
                        Text(
                            text = stringResource(R.string.rest_keep_screen_desc),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    Switch(
                        checked = uiState.keepScreenOn,
                        onCheckedChange = onKeepScreenOnChange,
                    )
                }
            }
        }
    }
}
