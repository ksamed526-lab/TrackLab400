package com.tracklab400.app.ui.screens.stopwatch

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.LifecycleResumeEffect
import com.tracklab400.app.R
import com.tracklab400.app.data.model.TimeUtils
import com.tracklab400.app.data.timing.RepEntry
import com.tracklab400.app.data.timing.StopwatchPhase
import com.tracklab400.app.data.timing.TargetComparison
import com.tracklab400.app.ui.components.TrackLabAccentButton
import com.tracklab400.app.ui.components.TrackLabButton
import com.tracklab400.app.ui.components.TrackLabCard
import com.tracklab400.app.ui.components.TrackLabOutlinedButton
import com.tracklab400.app.ui.components.TrackLabSectionHeader
import com.tracklab400.app.ui.components.TrackLabTag
import com.tracklab400.app.ui.components.TrackLabTextButton
import com.tracklab400.app.ui.components.TrackLabTextField
import com.tracklab400.app.ui.theme.Cream50
import com.tracklab400.app.ui.theme.Lime400
import com.tracklab400.app.ui.theme.Navy800
import com.tracklab400.app.ui.theme.TrackLabSpacing
import com.tracklab400.app.ui.theme.statValue
import com.tracklab400.app.ui.theme.trackLabColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StopwatchScreen(
    uiState: StopwatchUiState,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onLap: () -> Unit,
    onFinish: () -> Unit,
    onReset: () -> Unit,
    onCorrectRep: (Int, Long) -> Unit,
    onOpenRest: (Long) -> Unit,
    onSetVisible: (Boolean) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LifecycleResumeEffect(Unit) {
        onSetVisible(true)
        onPauseOrDispose { onSetVisible(false) }
    }

    var correctingRep by rememberSaveable { mutableStateOf<Int?>(null) }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(uiState.blockName.ifEmpty { stringResource(R.string.sw_title) }) },
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
            TimeDisplayCard(uiState = uiState)
            Spacer(Modifier.height(TrackLabSpacing.md))
            ControlRow(
                uiState = uiState,
                onStart = onStart,
                onPause = onPause,
                onResume = onResume,
                onLap = onLap,
                onFinish = onFinish,
                onReset = onReset,
            )
            Spacer(Modifier.height(TrackLabSpacing.md))
            if (uiState.suggestedRestMs != null && uiState.reps.isNotEmpty()) {
                TrackLabOutlinedButton(
                    text = stringResource(
                        R.string.sw_rest_suggest,
                        TimeUtils.formatDuration(uiState.suggestedRestMs),
                    ),
                    onClick = { onOpenRest(uiState.suggestedRestMs) },
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(TrackLabSpacing.md))
            }
            if (uiState.allRepsRecorded) {
                TrackLabTag(
                    text = stringResource(R.string.sw_all_reps_done),
                    containerColor = MaterialTheme.trackLabColors.success,
                    contentColor = MaterialTheme.trackLabColors.onSuccess,
                )
                Spacer(Modifier.height(TrackLabSpacing.md))
            }

            TrackLabSectionHeader(title = stringResource(R.string.sw_stats))
            Spacer(Modifier.height(TrackLabSpacing.sm))
            StatsCard(uiState = uiState)

            Spacer(Modifier.height(TrackLabSpacing.lg))
            TrackLabSectionHeader(title = stringResource(R.string.sw_reps_list))
            Spacer(Modifier.height(TrackLabSpacing.sm))
            if (uiState.reps.isEmpty()) {
                TrackLabCard {
                    Text(
                        text = stringResource(R.string.sw_empty_reps),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            } else {
                uiState.reps.forEach { rep ->
                    RepRow(
                        rep = rep,
                        onClick = { correctingRep = rep.index },
                    )
                    Spacer(Modifier.height(TrackLabSpacing.sm))
                }
            }
            Spacer(Modifier.height(TrackLabSpacing.lg))
        }
    }

    correctingRep?.let { index ->
        val rep = uiState.reps.firstOrNull { it.index == index }
        if (rep != null) {
            CorrectionDialog(
                rep = rep,
                onConfirm = { correctedMs ->
                    onCorrectRep(index, correctedMs)
                    correctingRep = null
                },
                onDismiss = { correctingRep = null },
            )
        }
    }
}

@Composable
private fun TimeDisplayCard(uiState: StopwatchUiState) {
    TrackLabCard(containerColor = Navy800, contentColor = Cream50) {
        Text(
            text = stringResource(R.string.sw_total),
            style = MaterialTheme.typography.labelMedium,
            color = Cream50.copy(alpha = 0.7f),
        )
        val totalText = TimeUtils.formatClock(uiState.displayMs)
        val totalDescription = stringResource(R.string.sw_total_desc, totalText)
        Text(
            text = totalText,
            style = MaterialTheme.typography.statValue.copy(
                fontSize = 56.sp,
                lineHeight = 64.sp,
                fontFamily = FontFamily.Monospace,
            ),
            color = Lime400,
            modifier = Modifier.semantics {
                contentDescription = totalDescription
            },
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(
                    R.string.sw_current_lap,
                    TimeUtils.formatClock(uiState.currentLapMs),
                ),
                style = MaterialTheme.typography.titleMedium,
                color = Cream50,
            )
            Spacer(Modifier.width(TrackLabSpacing.md))
            Text(
                text = stringResource(
                    R.string.sw_rep_counter,
                    uiState.nextRepIndex.coerceAtMost(uiState.expectedReps ?: uiState.nextRepIndex),
                    uiState.expectedReps ?: uiState.nextRepIndex,
                ),
                style = MaterialTheme.typography.titleMedium,
                color = Cream50.copy(alpha = 0.8f),
            )
        }
        Spacer(Modifier.height(TrackLabSpacing.md))
        Row(verticalAlignment = Alignment.CenterVertically) {
            val comparison = when {
                uiState.phase == StopwatchPhase.IDLE && uiState.reps.isEmpty() ->
                    TargetComparison.NONE
                else -> comparisonFor(uiState.currentLapMs, uiState)
            }
            if (uiState.targetMinMs != null || uiState.targetMaxMs != null) {
                TrackLabTag(
                    text = targetText(uiState),
                    containerColor = comparisonColor(comparison),
                    contentColor = comparisonContentColor(comparison),
                )
            }
        }
    }
}

@Composable
private fun targetText(uiState: StopwatchUiState): String {
    val min = uiState.targetMinMs
    val max = uiState.targetMaxMs
    return when {
        min != null && max != null ->
            stringResource(R.string.sw_target, TimeUtils.formatSeconds(min), TimeUtils.formatSeconds(max))
        min != null -> stringResource(R.string.sw_target_single, TimeUtils.formatSeconds(min))
        else -> stringResource(R.string.sw_no_target)
    }
}

@Composable
private fun comparisonFor(lapMs: Long, uiState: StopwatchUiState): TargetComparison {
    val min = uiState.targetMinMs ?: return TargetComparison.NONE
    val max = uiState.targetMaxMs ?: min
    return when {
        lapMs < min -> TargetComparison.BELOW
        lapMs <= max -> TargetComparison.IN_RANGE
        else -> TargetComparison.ABOVE
    }
}

@Composable
private fun comparisonColor(comparison: TargetComparison) = when (comparison) {
    TargetComparison.IN_RANGE -> MaterialTheme.trackLabColors.success
    TargetComparison.BELOW -> MaterialTheme.colorScheme.secondaryContainer
    TargetComparison.ABOVE -> MaterialTheme.trackLabColors.warning
    TargetComparison.NONE -> Cream50.copy(alpha = 0.15f)
}

@Composable
private fun comparisonContentColor(comparison: TargetComparison) = when (comparison) {
    TargetComparison.IN_RANGE -> MaterialTheme.trackLabColors.onSuccess
    TargetComparison.BELOW -> MaterialTheme.colorScheme.onSecondaryContainer
    TargetComparison.ABOVE -> MaterialTheme.colorScheme.onSurface
    TargetComparison.NONE -> Cream50
}

@Composable
private fun ControlRow(
    uiState: StopwatchUiState,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onLap: () -> Unit,
    onFinish: () -> Unit,
    onReset: () -> Unit,
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(TrackLabSpacing.sm),
        ) {
            when (uiState.phase) {
                StopwatchPhase.IDLE -> {
                    TrackLabButton(
                        text = stringResource(R.string.sw_start),
                        onClick = onStart,
                        modifier = Modifier.weight(1f),
                    )
                }
                StopwatchPhase.RUNNING -> {
                    TrackLabOutlinedButton(
                        text = stringResource(R.string.sw_pause),
                        onClick = onPause,
                        modifier = Modifier.weight(1f),
                    )
                    TrackLabAccentButton(
                        text = stringResource(R.string.sw_lap),
                        onClick = onLap,
                        modifier = Modifier.weight(1f),
                    )
                }
                StopwatchPhase.PAUSED -> {
                    TrackLabButton(
                        text = stringResource(R.string.sw_resume),
                        onClick = onResume,
                        modifier = Modifier.weight(1f),
                    )
                    TrackLabOutlinedButton(
                        text = stringResource(R.string.sw_finish),
                        onClick = onFinish,
                        modifier = Modifier.weight(1f),
                    )
                }
                StopwatchPhase.FINISHED -> {
                    TrackLabButton(
                        text = stringResource(R.string.sw_reset),
                        onClick = onReset,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
        if (uiState.phase == StopwatchPhase.RUNNING) {
            Spacer(Modifier.height(TrackLabSpacing.sm))
            TrackLabTextButton(
                text = stringResource(R.string.sw_finish),
                onClick = onFinish,
            )
        }
    }
}

@Composable
private fun StatsCard(uiState: StopwatchUiState) {
    TrackLabCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            StatItem(
                label = stringResource(R.string.sw_average),
                value = uiState.averageMs?.let { TimeUtils.formatSeconds(it) } ?: "—",
            )
            StatItem(
                label = stringResource(R.string.sw_best),
                value = uiState.bestMs?.let { TimeUtils.formatSeconds(it) } ?: "—",
            )
            StatItem(
                label = stringResource(R.string.sw_slowest),
                value = uiState.slowestMs?.let { TimeUtils.formatSeconds(it) } ?: "—",
            )
        }
        Spacer(Modifier.height(TrackLabSpacing.md))
        Text(
            text = stringResource(
                R.string.sw_distance,
                if (uiState.distanceM != null) "${uiState.totalDistanceM} m" else "—",
            ),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
private fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun RepRow(
    rep: RepEntry,
    onClick: () -> Unit,
) {
    TrackLabCard(onClick = onClick) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(R.string.sw_rep_item, rep.index),
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = TimeUtils.formatSeconds(rep.timeMs),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = repColor(rep),
            )
            if (rep.manuallyCorrected) {
                Spacer(Modifier.width(TrackLabSpacing.sm))
                TrackLabTag(text = stringResource(R.string.sw_manual_tag))
            }
        }
    }
}

@Composable
private fun repColor(rep: RepEntry): androidx.compose.ui.graphics.Color {
    val min = rep.targetMinMs ?: return MaterialTheme.colorScheme.onSurface
    val max = rep.targetMaxMs ?: min
    return when {
        rep.timeMs < min -> MaterialTheme.colorScheme.secondary
        rep.timeMs <= max -> MaterialTheme.trackLabColors.success
        else -> MaterialTheme.colorScheme.error
    }
}

@Composable
private fun CorrectionDialog(
    rep: RepEntry,
    onConfirm: (Long) -> Unit,
    onDismiss: () -> Unit,
) {
    var value by rememberSaveable { mutableStateOf(TimeUtils.formatSeconds(rep.timeMs).replace(',', '.')) }
    val parsed = com.tracklab400.app.data.model.TimeUtils.parseSeconds(value)
    val valid = parsed != null && parsed > 0.0

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.sw_correct_title)) },
        text = {
            Column {
                Text(stringResource(R.string.sw_correct_desc, rep.index))
                Spacer(Modifier.height(TrackLabSpacing.sm))
                TrackLabTextField(
                    value = value,
                    onValueChange = { value = it },
                    label = stringResource(R.string.sw_correct_hint),
                    keyboardType = KeyboardType.Decimal,
                    isError = value.isNotBlank() && !valid,
                )
            }
        },
        confirmButton = {
            TextButton(
                enabled = valid,
                onClick = {
                    parsed?.let { onConfirm((it * 1000).toLong()) }
                },
            ) {
                Text(stringResource(R.string.action_apply))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.action_cancel))
            }
        },
    )
}
