package com.tracklab400.app.ui.screens.plan

import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tracklab400.app.R
import com.tracklab400.app.data.model.PlanBlock
import com.tracklab400.app.data.model.SessionStatus
import com.tracklab400.app.data.model.TrainingWeek
import com.tracklab400.app.data.model.WorkoutSession
import com.tracklab400.app.ui.common.displayName
import com.tracklab400.app.ui.common.shortName
import com.tracklab400.app.ui.components.TrackLabCard
import com.tracklab400.app.ui.components.TrackLabEmpty
import com.tracklab400.app.ui.components.TrackLabLoading
import com.tracklab400.app.ui.components.TrackLabProgressBar
import com.tracklab400.app.ui.components.TrackLabTag
import com.tracklab400.app.ui.theme.TrackLabSpacing
import com.tracklab400.app.ui.theme.TrackLabTheme
import com.tracklab400.app.ui.theme.trackLabColors

@Composable
fun PlanScreen(
    uiState: PlanUiState,
    onSelectWeek: (Int) -> Unit,
    onOpenSession: (Int, Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val plan = uiState.plan
    if (plan == null) {
        Column(modifier = modifier.fillMaxSize()) {
            TrackLabLoading()
        }
        return
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(TrackLabSpacing.md),
    ) {
        Text(
            text = stringResource(R.string.nav_plan),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(TrackLabSpacing.md))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
        ) {
            plan.weeks.forEach { week ->
                FilterChip(
                    selected = week.weekNumber == uiState.selectedWeekNumber,
                    onClick = { onSelectWeek(week.weekNumber) },
                    label = { Text(stringResource(R.string.week_short_format, week.weekNumber)) },
                    modifier = Modifier.padding(end = TrackLabSpacing.xs),
                )
            }
        }

        val week = uiState.selectedWeek
        if (week != null) {
            Spacer(Modifier.height(TrackLabSpacing.md))
            WeekHeaderCard(week = week)
            Spacer(Modifier.height(TrackLabSpacing.md))
            week.days.forEach { session ->
                SessionRow(
                    session = session,
                    onClick = if (session.isRestDay) null else {
                        { onOpenSession(session.weekNumber, session.dayOfWeek.value) }
                    },
                )
                Spacer(Modifier.height(TrackLabSpacing.sm))
            }
        }

        Spacer(Modifier.height(TrackLabSpacing.lg))
    }
}

@Composable
private fun WeekHeaderCard(week: TrainingWeek) {
    TrackLabCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(
                    text = stringResource(
                        R.string.plan_week_title,
                        week.weekNumber,
                    ),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = week.focus,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.height(TrackLabSpacing.xs))
                Row {
                    TrackLabTag(
                        text = stringResource(week.block.labelRes()),
                    )
                    if (week.isDeload) {
                        Spacer(Modifier.width(TrackLabSpacing.xs))
                        TrackLabTag(
                            text = stringResource(R.string.tag_deload),
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                            contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                        )
                    }
                    if (week.isRaceWeek) {
                        Spacer(Modifier.width(TrackLabSpacing.xs))
                        TrackLabTag(
                            text = stringResource(R.string.tag_race_week),
                            containerColor = MaterialTheme.trackLabColors.success,
                            contentColor = MaterialTheme.trackLabColors.onSuccess,
                        )
                    }
                }
            }
        }
        val completed = week.days.count { it.status == SessionStatus.COMPLETED }
        val total = week.days.count { !it.isRestDay && it.kind.isMainSession }
        Spacer(Modifier.height(TrackLabSpacing.md))
        Text(
            text = stringResource(R.string.plan_week_progress, completed, total),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(TrackLabSpacing.xs))
        TrackLabProgressBar(
            progress = if (total == 0) 0f else completed.toFloat() / total,
        )
        if (week.checkpointNote != null) {
            Spacer(Modifier.height(TrackLabSpacing.md))
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.tertiaryContainer,
            ) {
                Row(Modifier.padding(TrackLabSpacing.md)) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onTertiaryContainer,
                    )
                    Spacer(Modifier.width(TrackLabSpacing.sm))
                    Text(
                        text = week.checkpointNote,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                    )
                }
            }
        }
    }
}

@Composable
private fun SessionRow(
    session: WorkoutSession,
    onClick: (() -> Unit)?,
) {
    TrackLabCard(
        onClick = onClick,
        containerColor = if (session.isRestDay) {
            MaterialTheme.colorScheme.surfaceContainerLow.copy(alpha = 0.6f)
        } else {
            MaterialTheme.colorScheme.surfaceContainerLow
        },
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surfaceVariant,
            ) {
                Icon(
                    imageVector = if (session.isRestDay) Icons.Default.Info else Icons.Default.DirectionsRun,
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
                    text = session.dayOfWeek.shortName(),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = session.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            if (session.isRestDay) {
                TrackLabTag(text = stringResource(R.string.tag_rest))
            } else {
                TrackLabTag(
                    text = session.status.displayName(),
                    containerColor = session.status.containerColor(),
                    contentColor = session.status.contentColor(),
                    leadingIcon = if (session.status == SessionStatus.COMPLETED) {
                        Icons.Default.CheckCircle
                    } else {
                        null
                    },
                )
            }
        }
    }
}

private fun PlanBlock.labelRes(): Int = when (this) {
    PlanBlock.YUKLENME -> R.string.block_yuklenme
    PlanBlock.OZELLESME -> R.string.block_ozellesme
}

@Composable
private fun SessionStatus.containerColor() = when (this) {
    SessionStatus.COMPLETED -> MaterialTheme.trackLabColors.success
    SessionStatus.PARTIAL -> MaterialTheme.colorScheme.tertiaryContainer
    SessionStatus.SKIPPED -> MaterialTheme.colorScheme.errorContainer
    SessionStatus.PLANNED -> MaterialTheme.colorScheme.surfaceVariant
}

@Composable
private fun SessionStatus.contentColor() = when (this) {
    SessionStatus.COMPLETED -> MaterialTheme.trackLabColors.onSuccess
    SessionStatus.PARTIAL -> MaterialTheme.colorScheme.onTertiaryContainer
    SessionStatus.SKIPPED -> MaterialTheme.colorScheme.onErrorContainer
    SessionStatus.PLANNED -> MaterialTheme.colorScheme.onSurfaceVariant
}

@Preview(showBackground = true)
@Composable
private fun PlanScreenEmptyPreview() {
    TrackLabTheme {
        PlanScreen(
            uiState = PlanUiState(),
            onSelectWeek = {},
            onOpenSession = { _, _ -> },
        )
    }
}
