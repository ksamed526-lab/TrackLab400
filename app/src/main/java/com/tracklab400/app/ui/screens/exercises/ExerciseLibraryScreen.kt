package com.tracklab400.app.ui.screens.exercises

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.tracklab400.app.R
import com.tracklab400.app.data.exercises.CoolDownExercise
import com.tracklab400.app.data.exercises.CoolDownCatalog
import com.tracklab400.app.data.exercises.PoseKind
import com.tracklab400.app.data.exercises.StrengthCatalog
import com.tracklab400.app.data.exercises.WarmUpCatalog
import com.tracklab400.app.data.exercises.WarmUpExercise
import com.tracklab400.app.ui.components.TrackLabCard
import com.tracklab400.app.ui.components.TrackLabTag
import com.tracklab400.app.ui.exercises.PoseFigure
import com.tracklab400.app.ui.exercises.libraryThumbnail
import com.tracklab400.app.ui.exercises.poseContentDescription
import com.tracklab400.app.ui.theme.TrackLabSpacing

private enum class LibraryTab(val labelRes: Int) {
    STRENGTH(R.string.library_tab_strength),
    WARMUP(R.string.library_tab_warmup),
    COOLDOWN(R.string.library_tab_cooldown),
}

/** Kuvvet sekmesi: yerel katalog tabanlı hareket kütüphanesi. */
@Composable
fun ExerciseLibraryScreen(
    onOpenExercise: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedTab by rememberSaveable { mutableStateOf(LibraryTab.STRENGTH) }

    Scaffold(modifier = modifier) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(TrackLabSpacing.md),
        ) {
            Text(
                text = stringResource(R.string.library_title),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(TrackLabSpacing.sm))
            Row(horizontalArrangement = Arrangement.spacedBy(TrackLabSpacing.xs)) {
                LibraryTab.entries.forEach { tab ->
                    val selected = tab == selectedTab
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (selected) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant
                        },
                        modifier = Modifier.padding(top = TrackLabSpacing.xs),
                        onClick = { selectedTab = tab },
                    ) {
                        Text(
                            text = stringResource(tab.labelRes),
                            style = MaterialTheme.typography.labelMedium,
                            color = if (selected) {
                                MaterialTheme.colorScheme.onPrimary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            },
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        )
                    }
                }
            }
            Spacer(Modifier.height(TrackLabSpacing.sm))
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = TrackLabSpacing.lg),
                verticalArrangement = Arrangement.spacedBy(TrackLabSpacing.sm),
            ) {
                when (selectedTab) {
                    LibraryTab.STRENGTH -> items(
                        items = StrengthCatalog.all,
                        key = { it.id },
                    ) { record ->
                        LibraryRow(
                            id = record.id,
                            title = record.name,
                            meta = stringResource(
                                R.string.exercise_sets_reps,
                                record.sets,
                                record.reps,
                                record.restSeconds,
                            ),
                            contentDescription = poseContentDescription(record.startPose),
                            onClick = { onOpenExercise(record.id) },
                        )
                    }
                    LibraryTab.WARMUP -> items(
                        items = WarmUpCatalog.all,
                        key = { it.id },
                    ) { exercise ->
                        WarmUpLibraryRow(
                            exercise = exercise,
                            onClick = { onOpenExercise(exercise.id) },
                        )
                    }
                    LibraryTab.COOLDOWN -> items(
                        items = CoolDownCatalog.all,
                        key = { it.id },
                    ) { exercise ->
                        CoolDownLibraryRow(
                            exercise = exercise,
                            onClick = { onOpenExercise(exercise.id) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LibraryRow(
    id: String,
    title: String,
    meta: String,
    contentDescription: String,
    onClick: () -> Unit,
) {
    TrackLabCard(onClick = onClick) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            PoseThumbnail(id = id, contentDescription = contentDescription)
            Spacer(Modifier.width(TrackLabSpacing.md))
            Column(Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = meta,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun WarmUpLibraryRow(
    exercise: WarmUpExercise,
    onClick: () -> Unit,
) {
    LibraryRow(
        id = exercise.id,
        title = exercise.name,
        meta = stringResource(R.string.exercise_duration_format, exercise.durationSeconds),
        contentDescription = poseContentDescription(exercise.poseStart),
        onClick = onClick,
    )
}

@Composable
private fun CoolDownLibraryRow(
    exercise: CoolDownExercise,
    onClick: () -> Unit,
) {
    val pose = libraryThumbnail(exercise.id) ?: PoseKind.STAND
    LibraryRow(
        id = exercise.id,
        title = exercise.name,
        meta = stringResource(R.string.exercise_duration_format, exercise.durationSeconds),
        contentDescription = poseContentDescription(pose),
        onClick = onClick,
    )
}

@Composable
private fun PoseThumbnail(id: String, contentDescription: String) {
    val pose = libraryThumbnail(id)
    Surface(
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surfaceVariant,
    ) {
        if (pose != null) {
            PoseFigure(
                pose = pose,
                contentDescription = contentDescription,
                modifier = Modifier
                    .size(52.dp)
                    .padding(8.dp),
            )
        } else {
            Spacer(Modifier.size(52.dp))
        }
    }
}