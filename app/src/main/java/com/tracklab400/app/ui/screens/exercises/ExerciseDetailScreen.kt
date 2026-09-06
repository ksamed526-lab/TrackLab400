package com.tracklab400.app.ui.screens.exercises

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tracklab400.app.R
import com.tracklab400.app.data.exercises.CoolDownExercise
import com.tracklab400.app.data.exercises.PoseKind
import com.tracklab400.app.data.exercises.RunningInterval
import com.tracklab400.app.data.exercises.StrengthExerciseRecord
import com.tracklab400.app.data.exercises.WarmUpExercise
import com.tracklab400.app.data.model.TimeUtils
import com.tracklab400.app.ui.components.TrackLabButton
import com.tracklab400.app.ui.components.TrackLabCard
import com.tracklab400.app.ui.components.TrackLabLoading
import com.tracklab400.app.ui.components.TrackLabOutlinedButton
import com.tracklab400.app.ui.components.TrackLabSlider
import com.tracklab400.app.ui.components.TrackLabSectionHeader
import com.tracklab400.app.ui.components.TrackLabTag
import com.tracklab400.app.ui.components.TrackLabTextField
import com.tracklab400.app.ui.exercises.PoseFigure
import com.tracklab400.app.ui.exercises.poseContentDescription
import com.tracklab400.app.ui.theme.TrackLabSpacing
import com.tracklab400.app.ui.theme.trackLabColors
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseDetailScreen(
    uiState: ExerciseDetailUiState,
    onOpenStopwatch: (() -> Unit)?,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(text = detailTitle(uiState)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        when (uiState) {
            ExerciseDetailUiState.Loading -> TrackLabLoading(Modifier.padding(innerPadding))
            ExerciseDetailUiState.NotFound -> NotFoundContent(
                modifier = Modifier.padding(innerPadding),
                onBack = onBack,
            )
            is ExerciseDetailUiState.WarmUp -> BodyColumn(innerPadding) {
                WarmUpContent(state = uiState)
            }
            is ExerciseDetailUiState.Running -> BodyColumn(innerPadding) {
                RunningContent(
                    interval = uiState.interval,
                    canOpenStopwatch = uiState.canOpenStopwatch,
                    onOpenStopwatch = onOpenStopwatch,
                )
            }
            is ExerciseDetailUiState.Strength -> BodyColumn(innerPadding) {
                StrengthContent(
                    planName = uiState.planName,
                    planCues = uiState.planCues,
                    record = uiState.record,
                )
            }
            is ExerciseDetailUiState.CoolDown -> BodyColumn(innerPadding) {
                CoolDownContent(state = uiState)
            }
        }
    }
}

@Composable
private fun detailTitle(state: ExerciseDetailUiState): String = when (state) {
    ExerciseDetailUiState.Loading -> ""
    ExerciseDetailUiState.NotFound -> stringResource(R.string.detail_not_found_title)
    is ExerciseDetailUiState.WarmUp -> state.title
    is ExerciseDetailUiState.Running -> state.interval.name
    is ExerciseDetailUiState.Strength -> state.planName
    is ExerciseDetailUiState.CoolDown -> state.title
}

@Composable
private fun BodyColumn(
    innerPadding: androidx.compose.foundation.layout.PaddingValues,
    modifier: Modifier = Modifier,
    content: @Composable androidx.compose.foundation.layout.ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(innerPadding)
            .verticalScroll(rememberScrollState())
            .padding(TrackLabSpacing.md),
        content = content,
    )
}

@Composable
private fun NotFoundContent(modifier: Modifier = Modifier, onBack: () -> Unit) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(TrackLabSpacing.lg),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = Icons.Default.Info,
            contentDescription = null,
            modifier = Modifier.size(40.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(TrackLabSpacing.md))
        Text(
            text = stringResource(R.string.detail_not_found_title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(TrackLabSpacing.xs))
        Text(
            text = stringResource(R.string.detail_not_found_desc),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(TrackLabSpacing.lg))
        TrackLabOutlinedButton(text = stringResource(R.string.back), onClick = onBack)
    }
}

// ---------- IsÄ±nma ----------

@Composable
private fun WarmUpContent(state: ExerciseDetailUiState.WarmUp) {
    state.context?.let { context ->
        Spacer(Modifier.height(TrackLabSpacing.md))
        TrackLabCard(containerColor = MaterialTheme.colorScheme.tertiaryContainer) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onTertiaryContainer,
                )
                Spacer(Modifier.width(TrackLabSpacing.sm))
                Text(
                    text = context,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                )
            }
        }
    }
    Spacer(Modifier.height(TrackLabSpacing.md))
    var completedIds by remember { mutableStateOf(setOf<String>()) }
    state.exercises.forEachIndexed { index, exercise ->
        WarmUpDrillCard(
            exercise = exercise,
            completed = exercise.id in completedIds,
            onCompleteChange = { checked ->
                completedIds = if (checked) {
                    completedIds + exercise.id
                } else {
                    completedIds - exercise.id
                }
            },
        )
        Spacer(Modifier.height(TrackLabSpacing.sm))
        if (index < state.exercises.lastIndex) Spacer(Modifier.height(TrackLabSpacing.sm))
    }
    val doneCount = completedIds.size
    if (doneCount > 0) {
        Spacer(Modifier.height(TrackLabSpacing.sm))
        TrackLabTag(
            text = stringResource(R.string.exercise_progress_format, doneCount, state.exercises.size),
            containerColor = MaterialTheme.trackLabColors.success,
            contentColor = MaterialTheme.trackLabColors.onSuccess,
            leadingIcon = Icons.Default.CheckCircle,
        )
    }
}

@Composable
private fun WarmUpDrillCard(
    exercise: WarmUpExercise,
    completed: Boolean,
    onCompleteChange: (Boolean) -> Unit,
) {
    TrackLabCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(
                    text = exercise.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = stringResource(
                        R.string.exercise_duration_format,
                        exercise.durationSeconds,
                    ),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            Checkbox(checked = completed, onCheckedChange = onCompleteChange)
        }
        Spacer(Modifier.height(TrackLabSpacing.sm))
        Text(
            text = exercise.purpose,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(TrackLabSpacing.md))
        PosePair(start = exercise.poseStart, end = exercise.poseEnd)
        DetailSection(title = stringResource(R.string.exercise_steps))
        NumberedList(items = exercise.steps)
        DetailSection(title = stringResource(R.string.exercise_tips))
        InfoList(
            items = exercise.tips,
            icon = Icons.Default.Check,
            iconTint = MaterialTheme.trackLabColors.success,
        )
        DetailSection(title = stringResource(R.string.exercise_mistakes))
        InfoList(
            items = exercise.commonMistakes,
            icon = Icons.Default.Warning,
            iconTint = MaterialTheme.colorScheme.error,
        )
        DetailSection(title = stringResource(R.string.exercise_safety))
        SafetyCard(items = exercise.safety)
    }
}

// ---------- Ana Ã‡alÄ±ÅŸma ----------

@Composable
private fun RunningContent(
    interval: RunningInterval,
    canOpenStopwatch: Boolean,
    onOpenStopwatch: (() -> Unit)?,
) {
    TrackLabCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.DirectionsRun,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(10.dp)
                        .size(22.dp),
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
            Spacer(Modifier.width(TrackLabSpacing.md))
            Column {
                Text(
                    text = stringResource(R.string.exercise_reps_meta, interval.distanceM, interval.reps),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                Row {
                    interval.targetMinMs?.let { min ->
                        val label = if (interval.targetMaxMs != null) {
                            stringResource(
                                R.string.exercise_target_band,
                                TimeUtils.formatSeconds(min),
                                TimeUtils.formatSeconds(interval.targetMaxMs),
                            )
                        } else {
                            stringResource(R.string.exercise_target_single, TimeUtils.formatSeconds(min))
                        }
                        TrackLabTag(text = label)
                        Spacer(Modifier.width(TrackLabSpacing.xs))
                    }
                    interval.restMinMs?.let { restMin ->
                        val label = if (interval.restMaxMs != null) {
                            stringResource(
                                R.string.exercise_rest_band,
                                TimeUtils.formatDuration(restMin),
                                TimeUtils.formatDuration(interval.restMaxMs),
                            )
                        } else {
                            stringResource(R.string.exercise_rest_single, TimeUtils.formatDuration(restMin))
                        }
                        TrackLabTag(text = label, containerColor = MaterialTheme.colorScheme.secondaryContainer)
                    }
                }
            }
        }
        Spacer(Modifier.height(TrackLabSpacing.md))
        DetailSection(title = stringResource(R.string.exercise_purpose))
        Text(
            text = interval.purpose,
            style = MaterialTheme.typography.bodyMedium,
        )
        if (interval.cues.isNotEmpty()) {
            DetailSection(title = stringResource(R.string.exercise_cues))
            InfoList(items = interval.cues, icon = Icons.Default.Check, iconTint = MaterialTheme.trackLabColors.success)
        }
        interval.notes?.let { notes ->
            DetailSection(title = stringResource(R.string.exercise_notes))
            Text(
                text = notes,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
    if (canOpenStopwatch && onOpenStopwatch != null) {
        Spacer(Modifier.height(TrackLabSpacing.lg))
        TrackLabButton(
            text = stringResource(R.string.exercise_start_stopwatch),
            icon = Icons.Default.Schedule,
            onClick = onOpenStopwatch,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(TrackLabSpacing.sm))
        Text(
            text = stringResource(R.string.exercise_per_rep_note),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(TrackLabSpacing.xs))
        Text(
            text = stringResource(R.string.exercise_rpe_after_note),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

// ---------- Kuvvet ----------

@Composable
private fun StrengthContent(
    planName: String,
    planCues: String?,
    record: StrengthExerciseRecord,
) {
    var state by remember(record.id) { mutableStateOf(record) }
    var completed by remember(record.id) { mutableStateOf(false) }

    TrackLabCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
            ) {
                Icon(
                    imageVector = Icons.Default.FitnessCenter,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(10.dp)
                        .size(22.dp),
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
            Spacer(Modifier.width(TrackLabSpacing.md))
            Row {
                state.targetMuscles.forEach { muscle ->
                    TrackLabTag(text = muscle)
                    Spacer(Modifier.width(TrackLabSpacing.xs))
                }
            }
        }
        if (planCues != null) {
            Spacer(Modifier.height(TrackLabSpacing.sm))
            Text(
                text = planCues,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
            )
        }
        Spacer(Modifier.height(TrackLabSpacing.md))
        PosePair(start = state.startPose, end = state.endPose)

        DetailSection(title = stringResource(R.string.exercise_plan_header))
        Row {
            TrackLabTag(
                text = stringResource(
                    R.string.exercise_sets_reps,
                    state.sets,
                    state.reps,
                    state.restSeconds,
                ),
            )
        }

        DetailSection(title = stringResource(R.string.exercise_rpe_target))
        TrackLabSlider(
            value = state.rpeTarget.toFloat(),
            onValueChange = { state = state.copy(rpeTarget = it.toInt()) },
            min = 1f,
            max = 10f,
            steps = 8,
            label = { "Zorluk ${it.toInt()}" },
            modifier = Modifier.padding(vertical = TrackLabSpacing.xs),
        )

        DetailSection(title = stringResource(R.string.exercise_weight))
        TrackLabTextField(
            value = state.weight?.let { formatWeight(it) } ?: "",
            onValueChange = { text ->
                state = state.copy(weight = text.toDoubleOrNull())
            },
            label = stringResource(R.string.exercise_weight_label),
            modifier = Modifier.padding(vertical = TrackLabSpacing.xs),
            keyboardType = androidx.compose.ui.text.input.KeyboardType.Number,
        )
    }

    Spacer(Modifier.height(TrackLabSpacing.md))

    TrackLabCard {
        DetailSection(title = stringResource(R.string.exercise_completed_sets))
        Row {
            (0..state.sets).forEach { count ->
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (state.completedSets == count) {
                        MaterialTheme.trackLabColors.success
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant
                    },
                    modifier = Modifier.padding(end = TrackLabSpacing.xs),
                    onClick = { state = state.copy(completedSets = count) },
                ) {
                    Text(
                        text = count.toString(),
                        style = MaterialTheme.typography.labelMedium,
                        color = if (state.completedSets == count) {
                            MaterialTheme.trackLabColors.onSuccess
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    )
                }
            }
        }
        for (setNumber in 1..state.sets) {
            Spacer(Modifier.height(TrackLabSpacing.sm))
            TrackLabTextField(
                value = state.perSetNotes.getOrElse(setNumber - 1) { "" },
                onValueChange = { text ->
                    val notes = state.perSetNotes.toMutableList()
                    while (notes.size < setNumber) notes.add("")
                    notes[setNumber - 1] = text
                    state = state.copy(perSetNotes = notes)
                },
                label = stringResource(R.string.exercise_set_note, setNumber),
                modifier = Modifier.padding(vertical = TrackLabSpacing.xs),
            )
        }
    }

    Spacer(Modifier.height(TrackLabSpacing.md))

    TrackLabCard {
        DetailSection(title = stringResource(R.string.exercise_technique))
        NumberedList(items = state.technique)
        DetailSection(title = stringResource(R.string.exercise_mistakes))
        InfoList(
            items = state.commonMistakes,
            icon = Icons.Default.Warning,
            iconTint = MaterialTheme.colorScheme.error,
        )
        DetailSection(title = stringResource(R.string.exercise_safety))
        SafetyCard(items = state.safety)
    }

    Spacer(Modifier.height(TrackLabSpacing.md))

    TrackLabCard(containerColor = MaterialTheme.colorScheme.secondaryContainer) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSecondaryContainer,
            )
            Spacer(Modifier.width(TrackLabSpacing.sm))
            Column {
                Text(
                    text = stringResource(R.string.exercise_alternative),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                )
                Text(
                    text = state.noEquipmentAlternative,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                )
            }
        }
    }

    Spacer(Modifier.height(TrackLabSpacing.lg))
    TrackLabButton(
        text = stringResource(R.string.exercise_complete),
        onClick = {
            state = state.copy(completedSets = state.sets)
            completed = true
        },
        modifier = Modifier.fillMaxWidth(),
    )
    if (completed) {
        Spacer(Modifier.height(TrackLabSpacing.sm))
        Text(
            text = stringResource(R.string.exercise_marked_complete),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.trackLabColors.success,
        )
    }
    Spacer(Modifier.height(TrackLabSpacing.lg))
}

// ---------- SoÄŸuma ----------

@Composable
private fun CoolDownContent(state: ExerciseDetailUiState.CoolDown) {
    var completedIds by remember { mutableStateOf(setOf<String>()) }
    state.exercises.forEach { exercise ->
        CoolDownCard(
            exercise = exercise,
            completed = exercise.id in completedIds,
            onCompleteChange = { checked ->
                completedIds = if (checked) {
                    completedIds + exercise.id
                } else {
                    completedIds - exercise.id
                }
            },
        )
        Spacer(Modifier.height(TrackLabSpacing.sm))
    }
}

@Composable
private fun CoolDownCard(
    exercise: CoolDownExercise,
    completed: Boolean,
    onCompleteChange: (Boolean) -> Unit,
) {
    TrackLabCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(
                    text = exercise.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = stringResource(
                        R.string.exercise_duration_format,
                        exercise.durationSeconds,
                    ),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            Checkbox(checked = completed, onCheckedChange = onCompleteChange)
        }
        Spacer(Modifier.height(TrackLabSpacing.sm))
        Text(
            text = exercise.purpose,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(TrackLabSpacing.md))
        CoolDownTimer(totalSeconds = exercise.durationSeconds)
        DetailSection(title = stringResource(R.string.exercise_steps))
        NumberedList(items = exercise.steps)
        DetailSection(title = stringResource(R.string.exercise_tips))
        InfoList(items = exercise.tips, icon = Icons.Default.Check, iconTint = MaterialTheme.trackLabColors.success)
        DetailSection(title = stringResource(R.string.exercise_mistakes))
        InfoList(items = exercise.commonMistakes, icon = Icons.Default.Warning, iconTint = MaterialTheme.colorScheme.error)
        DetailSection(title = stringResource(R.string.exercise_safety))
        SafetyCard(items = exercise.safety)
    }
}

@Composable
private fun CoolDownTimer(totalSeconds: Int) {
    var remaining by remember(totalSeconds) { mutableIntStateOf(totalSeconds) }
    var running by remember(totalSeconds) { mutableStateOf(false) }
    androidx.compose.runtime.LaunchedEffect(running, totalSeconds) {
        while (running && remaining > 0) {
            delay(1_000)
            remaining -= 1
        }
    }
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(TrackLabSpacing.md),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(R.string.exercise_timer),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(TrackLabSpacing.xs))
            Text(
                text = stringResource(R.string.exercise_seconds_format, remaining),
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(TrackLabSpacing.sm))
            Row {
                TrackLabButton(
                    text = if (running) {
                        stringResource(R.string.exercise_timer_pause)
                    } else {
                        stringResource(R.string.exercise_timer_start)
                    },
                    onClick = { running = !running },
                )
                Spacer(Modifier.width(TrackLabSpacing.sm))
                TrackLabOutlinedButton(
                    text = stringResource(R.string.exercise_timer_reset),
                    onClick = {
                        running = false
                        remaining = totalSeconds
                    },
                )
            }
        }
    }
}

// ---------- Ortak bileÅŸenler ----------

@Composable
private fun PosePair(start: PoseKind, end: PoseKind) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        PoseColumn(
            pose = start,
            label = stringResource(R.string.exercise_start_pose),
            contentDescription = stringResource(
                R.string.exercise_pose_cd,
                stringResource(R.string.exercise_start_pose),
                poseContentDescription(start),
            ),
        )
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        PoseColumn(
            pose = end,
            label = stringResource(R.string.exercise_end_pose),
            contentDescription = stringResource(
                R.string.exercise_pose_cd,
                stringResource(R.string.exercise_end_pose),
                poseContentDescription(end),
            ),
        )
    }
}

@Composable
private fun PoseColumn(pose: PoseKind, label: String, contentDescription: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
        ) {
            PoseFigure(
                pose = pose,
                contentDescription = contentDescription,
                modifier = Modifier
                    .size(width = 72.dp, height = 144.dp)
                    .padding(6.dp),
            )
        }
        Spacer(Modifier.height(TrackLabSpacing.xs))
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun DetailSection(title: String) {
    Spacer(Modifier.height(TrackLabSpacing.md))
    TrackLabSectionHeader(title = title)
    Spacer(Modifier.height(TrackLabSpacing.xs))
}

@Composable
private fun NumberedList(items: List<String>) {
    items.forEachIndexed { index, item ->
        Row(
            modifier = Modifier.padding(vertical = 2.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Text(
                text = "${index + 1}.",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
            )
            Spacer(Modifier.width(TrackLabSpacing.sm))
            Text(
                text = item,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun InfoList(items: List<String>, icon: androidx.compose.ui.graphics.vector.ImageVector, iconTint: androidx.compose.ui.graphics.Color) {
    items.forEach { item ->
        Row(
            modifier = Modifier.padding(vertical = 2.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = iconTint,
            )
            Spacer(Modifier.width(TrackLabSpacing.sm))
            Text(
                text = item,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun SafetyCard(items: List<String>) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.errorContainer,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(Modifier.padding(TrackLabSpacing.sm)) {
            items.forEach { item ->
                Row(
                    modifier = Modifier.padding(vertical = 2.dp),
                    verticalAlignment = Alignment.Top,
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onErrorContainer,
                    )
                    Spacer(Modifier.width(TrackLabSpacing.sm))
                    Text(
                        text = item,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

private fun formatWeight(value: Double): String {
    val rounded = Math.round(value * 10.0) / 10.0
    return if (rounded % 1.0 == 0.0) rounded.toInt().toString() else rounded.toString().replace('.', ',')
}
