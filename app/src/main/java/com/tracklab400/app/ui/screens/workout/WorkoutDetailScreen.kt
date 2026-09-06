package com.tracklab400.app.ui.screens.workout

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Info
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tracklab400.app.R
import com.tracklab400.app.data.exercises.ExerciseEntry
import com.tracklab400.app.data.exercises.ExerciseLibrary
import com.tracklab400.app.data.model.ExerciseType
import com.tracklab400.app.data.model.SessionStatus
import com.tracklab400.app.data.model.WorkoutExercise
import com.tracklab400.app.data.model.WorkoutSession
import com.tracklab400.app.ui.common.displayName
import com.tracklab400.app.ui.common.fullName
import com.tracklab400.app.ui.common.metaLine
import com.tracklab400.app.ui.components.TrackLabButton
import com.tracklab400.app.ui.components.TrackLabCard
import com.tracklab400.app.ui.components.TrackLabLoading
import com.tracklab400.app.ui.components.TrackLabOutlinedButton
import com.tracklab400.app.ui.components.TrackLabSectionHeader
import com.tracklab400.app.ui.components.TrackLabTag
import com.tracklab400.app.ui.components.TrackLabTextButton
import com.tracklab400.app.ui.theme.TrackLabSpacing
import com.tracklab400.app.ui.theme.trackLabColors
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutDetailScreen(
    session: WorkoutSession?,
    events: Flow<WorkoutEvent>,
    onMarkStatus: (SessionStatus) -> Unit,
    onOpenStopwatch: (Int) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    onOpenCompletion: () -> Unit = {},
) {
    var showSkipDialog by rememberSaveable { mutableStateOf(false) }
    val exerciseLibrary = rememberExerciseLibrary()

    LaunchedEffect(Unit) {
        events.collect { event ->
            if (event == WorkoutEvent.SessionMarked) onBack()
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = session?.let {
                            stringResource(R.string.workout_title_format, it.dayOfWeek.fullName(), it.weekNumber)
                        } ?: "",
                    )
                },
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
        if (session == null) {
            TrackLabLoading(Modifier.padding(innerPadding))
            return@Scaffold
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(TrackLabSpacing.md),
        ) {
            Text(
                text = session.title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
            )
            session.focus?.let { focus ->
                Spacer(Modifier.height(TrackLabSpacing.xs))
                Text(
                    text = focus,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Spacer(Modifier.height(TrackLabSpacing.md))
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (session.status != SessionStatus.PLANNED) {
                    TrackLabTag(
                        text = session.status.displayName(),
                        containerColor = statusColor(session.status),
                        contentColor = statusContentColor(session.status),
                    )
                }
                if (session.isRestDay) {
                    TrackLabTag(text = stringResource(R.string.tag_rest))
                }
            }

            val warmUp = session.exercises.mapIndexedNotNull { index, exercise ->
                if (exercise.type == ExerciseType.WARMUP) index to exercise else null
            }
            val main = session.exercises.mapIndexedNotNull { index, exercise ->
                if (exercise.type != ExerciseType.WARMUP) index to exercise else null
            }

            if (warmUp.isNotEmpty()) {
                Spacer(Modifier.height(TrackLabSpacing.lg))
                TrackLabSectionHeader(title = stringResource(R.string.section_warmup))
                Spacer(Modifier.height(TrackLabSpacing.sm))
                warmUp.forEachIndexed { position, (index, exercise) ->
                    ExerciseCard(
                        exercise = exercise,
                        index = position + 1,
                        library = exerciseLibrary,
                        onOpenStopwatch = if (exercise.distanceM != null) {
                            { onOpenStopwatch(index) }
                        } else {
                            null
                        },
                    )
                    Spacer(Modifier.height(TrackLabSpacing.sm))
                }
            }

            if (main.isNotEmpty()) {
                Spacer(Modifier.height(TrackLabSpacing.md))
                TrackLabSectionHeader(title = stringResource(R.string.section_workout))
                Spacer(Modifier.height(TrackLabSpacing.sm))
                main.forEach { (index, exercise) ->
                    ExerciseCard(
                        exercise = exercise,
                        library = exerciseLibrary,
                        onOpenStopwatch = if (exercise.distanceM != null) {
                            { onOpenStopwatch(index) }
                        } else {
                            null
                        },
                    )
                    Spacer(Modifier.height(TrackLabSpacing.sm))
                }
            }

            Spacer(Modifier.height(TrackLabSpacing.lg))
            TrackLabButton(
                text = stringResource(R.string.action_log_session),
                onClick = onOpenCompletion,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(TrackLabSpacing.md))
            TrackLabButton(
                text = stringResource(R.string.action_mark_completed),
                onClick = { onMarkStatus(SessionStatus.COMPLETED) },
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(TrackLabSpacing.sm))
            TrackLabOutlinedButton(
                text = stringResource(R.string.action_mark_partial),
                onClick = { onMarkStatus(SessionStatus.PARTIAL) },
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(TrackLabSpacing.sm))
            TrackLabTextButton(
                text = stringResource(R.string.action_mark_skipped),
                onClick = { showSkipDialog = true },
            )
            Spacer(Modifier.height(TrackLabSpacing.lg))
        }
    }

    if (showSkipDialog) {
        AlertDialog(
            onDismissRequest = { showSkipDialog = false },
            title = { Text(stringResource(R.string.skip_confirm_title)) },
            text = { Text(stringResource(R.string.skip_confirm_desc)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showSkipDialog = false
                        onMarkStatus(SessionStatus.SKIPPED)
                    },
                ) {
                    Text(stringResource(R.string.action_skip))
                }
            },
            dismissButton = {
                TextButton(onClick = { showSkipDialog = false }) {
                    Text(stringResource(R.string.action_cancel))
                }
            },
        )
    }
}

@Composable
private fun ExerciseCard(
    exercise: WorkoutExercise,
    index: Int? = null,
    onOpenStopwatch: (() -> Unit)? = null,
    library: ExerciseLibrary? = null,
) {
    TrackLabCard {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surfaceVariant,
                ) {
                    Icon(
                        imageVector = when (exercise.type) {
                            ExerciseType.WARMUP -> Icons.Default.DirectionsRun
                            ExerciseType.RUN -> Icons.Default.DirectionsRun
                            ExerciseType.STRENGTH -> Icons.Default.FitnessCenter
                            ExerciseType.NOTE -> Icons.Default.Info
                        },
                        contentDescription = null,
                        modifier = Modifier
                            .padding(10.dp)
                            .size(18.dp),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
                Spacer(Modifier.width(TrackLabSpacing.md))
                Column(Modifier.weight(1f)) {
                    Text(
                        text = index?.let { "$it. ${exercise.name}" } ?: exercise.name,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                    )
                    val meta = exercise.metaLine()
                    if (meta.isNotBlank()) {
                        Text(
                            text = meta,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                    exercise.cues?.let { cues ->
                        Text(
                            text = cues,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    if (onOpenStopwatch != null) {
                        Spacer(Modifier.height(TrackLabSpacing.sm))
                        TrackLabOutlinedButton(
                            text = stringResource(R.string.action_open_stopwatch),
                            onClick = onOpenStopwatch,
                        )
                    }
                }
                if (exercise.isTest) {
                    Spacer(Modifier.width(TrackLabSpacing.sm))
                    TrackLabTag(
                        text = stringResource(R.string.tag_test),
                        containerColor = MaterialTheme.trackLabColors.warning,
                        contentColor = MaterialTheme.trackLabColors.onSuccess,
                    )
                }
            }
            val matches = library?.find(exercise.name).orEmpty()
            if (matches.isNotEmpty()) {
                Spacer(Modifier.height(TrackLabSpacing.md))
                matches.forEach { entry ->
                    LibraryExerciseBlock(entry)
                    Spacer(Modifier.height(TrackLabSpacing.sm))
                }
            }
        }
    }
}

@Composable
private fun LibraryExerciseBlock(entry: ExerciseEntry) {
    Column {
        Text(
            text = entry.name,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary,
        )
        val equipment = if (entry.equipment.isNotBlank()) {
            stringResource(R.string.exercise_equipment_format, entry.equipment)
        } else {
            null
        }
        val level = if (entry.level.isNotBlank()) {
            stringResource(R.string.exercise_level_format, entry.level)
        } else {
            null
        }
        val meta = listOfNotNull(equipment, level).joinToString(" · ")
        if (meta.isNotBlank()) {
            Text(
                text = meta,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Spacer(Modifier.height(TrackLabSpacing.sm))
        ExerciseAssetImage(
            entry = entry,
            contentDescription = stringResource(R.string.exercise_image_content_description, entry.name),
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(12.dp)),
        )
    }
}

@Composable
private fun statusColor(status: SessionStatus) = when (status) {
    SessionStatus.COMPLETED -> MaterialTheme.trackLabColors.success
    SessionStatus.PARTIAL -> MaterialTheme.colorScheme.tertiaryContainer
    SessionStatus.SKIPPED -> MaterialTheme.colorScheme.errorContainer
    SessionStatus.PLANNED -> MaterialTheme.colorScheme.surfaceVariant
}

@Composable
private fun statusContentColor(status: SessionStatus) = when (status) {
    SessionStatus.COMPLETED -> MaterialTheme.trackLabColors.onSuccess
    SessionStatus.PARTIAL -> MaterialTheme.colorScheme.onTertiaryContainer
    SessionStatus.SKIPPED -> MaterialTheme.colorScheme.onErrorContainer
    SessionStatus.PLANNED -> MaterialTheme.colorScheme.onSurfaceVariant
}
