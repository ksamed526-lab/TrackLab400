package com.tracklab400.app.ui.screens.home

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
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.tracklab400.app.data.model.TimeUtils
import com.tracklab400.app.data.model.WorkoutSession
import com.tracklab400.app.ui.common.briefSummary
import com.tracklab400.app.ui.common.fullName
import com.tracklab400.app.ui.components.TrackLabAccentButton
import com.tracklab400.app.ui.components.TrackLabBrandLockup
import com.tracklab400.app.ui.components.TrackLabButton
import com.tracklab400.app.ui.components.TrackLabCard
import com.tracklab400.app.ui.components.TrackLabEmpty
import com.tracklab400.app.ui.components.TrackLabOutlinedButton
import com.tracklab400.app.ui.components.TrackLabProgressBar
import com.tracklab400.app.ui.components.TrackLabSectionHeader
import com.tracklab400.app.ui.components.TrackLabTag
import com.tracklab400.app.ui.theme.Cream200
import com.tracklab400.app.ui.theme.Cream50
import com.tracklab400.app.ui.theme.Lime400
import com.tracklab400.app.ui.theme.Navy800
import com.tracklab400.app.ui.theme.TrackLabSpacing
import com.tracklab400.app.ui.theme.TrackLabTheme
import com.tracklab400.app.ui.theme.Turquoise200
import com.tracklab400.app.ui.theme.Turquoise700
import com.tracklab400.app.ui.theme.statValue

@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onOpenSettings: () -> Unit,
    onOpenPlan: () -> Unit,
    onOpenWorkout: (Int, Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(TrackLabSpacing.md),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            TrackLabBrandLockup(Modifier.weight(1f))
            IconButton(onClick = onOpenSettings) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = stringResource(R.string.settings_title),
                )
            }
        }

        Spacer(Modifier.height(TrackLabSpacing.sm))
        Text(
            text = uiState.nickname?.let { stringResource(R.string.home_greeting, it) }
                ?: stringResource(R.string.home_greeting_default),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(Modifier.height(TrackLabSpacing.md))
        TrackLabHeroCard(uiState = uiState, onOpenPlan = onOpenPlan)

        Spacer(Modifier.height(TrackLabSpacing.lg))
        TrackLabSectionHeader(title = stringResource(R.string.section_next_workout))
        Spacer(Modifier.height(TrackLabSpacing.sm))
        NextWorkoutCard(
            uiState = uiState,
            onOpenWorkout = onOpenWorkout,
        )

        Spacer(Modifier.height(TrackLabSpacing.lg))
        TrackLabSectionHeader(title = stringResource(R.string.home_section_this_week))
        Spacer(Modifier.height(TrackLabSpacing.sm))
        TrackLabCard {
            Text(
                text = stringResource(
                    R.string.plan_week_progress,
                    uiState.completedCount,
                    uiState.totalCount,
                ),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(TrackLabSpacing.sm))
            TrackLabProgressBar(progress = uiState.completionPercent)
            Spacer(Modifier.height(TrackLabSpacing.md))
            TrackLabOutlinedButton(
                text = stringResource(R.string.action_view_plan),
                onClick = onOpenPlan,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        Spacer(Modifier.height(TrackLabSpacing.lg))
        TrackLabSectionHeader(title = stringResource(R.string.home_section_daily_log))
        Spacer(Modifier.height(TrackLabSpacing.sm))
        TrackLabCard {
            Text(
                text = stringResource(R.string.home_daily_log_title),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = stringResource(R.string.home_daily_log_desc),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(TrackLabSpacing.md))
            TrackLabOutlinedButton(
                text = stringResource(R.string.action_save),
                onClick = {},
            )
        }

        Spacer(Modifier.height(TrackLabSpacing.lg))
    }
}

@Composable
private fun TrackLabHeroCard(
    uiState: HomeUiState,
    onOpenPlan: () -> Unit,
) {
    TrackLabCard(containerColor = Navy800, contentColor = Cream50) {
        Text(
            text = stringResource(R.string.home_hero_brand),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = Lime400,
        )
        Text(
            text = stringResource(R.string.home_hero_title),
            style = MaterialTheme.typography.headlineMedium,
            color = Cream50,
        )
        if (uiState.currentTimeMs != null && uiState.targetTimeMs != null) {
            Spacer(Modifier.height(TrackLabSpacing.sm))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = TimeUtils.formatMs(uiState.currentTimeMs),
                    style = MaterialTheme.typography.statValue,
                    color = Cream200,
                )
                Spacer(Modifier.width(TrackLabSpacing.sm))
                Text(
                    text = "→",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Lime400,
                )
                Spacer(Modifier.width(TrackLabSpacing.sm))
                Text(
                    text = TimeUtils.formatMs(uiState.targetTimeMs),
                    style = MaterialTheme.typography.statValue,
                    color = Lime400,
                )
            }
            Text(
                text = stringResource(R.string.home_current_target_label),
                style = MaterialTheme.typography.labelSmall,
                color = Cream200,
            )
        }
        uiState.daysRemaining?.let { days ->
            Spacer(Modifier.height(TrackLabSpacing.md))
            Text(
                text = if (days > 0) {
                    stringResource(R.string.home_days_remaining, days)
                } else {
                    stringResource(R.string.home_goal_reached)
                },
                style = MaterialTheme.typography.titleSmall,
                color = Lime400,
            )
        }
        Spacer(Modifier.height(TrackLabSpacing.md))
        Text(
            text = stringResource(
                R.string.home_week_format,
                uiState.currentWeekNumber,
                uiState.weekCount,
            ),
            style = MaterialTheme.typography.labelMedium,
            color = Cream200,
        )
        Spacer(Modifier.height(TrackLabSpacing.sm))
        TrackLabProgressBar(
            progress = uiState.currentWeekNumber.toFloat() / uiState.weekCount,
            progressColor = Lime400,
            trackColor = Cream50.copy(alpha = 0.18f),
            height = 10.dp,
        )
        Spacer(Modifier.height(TrackLabSpacing.sm))
        Row {
            TrackLabTag(
                text = stringResource(R.string.percent_format, (uiState.completionPercent * 100).toInt()),
                containerColor = Lime400,
                contentColor = Navy800,
            )
            Spacer(Modifier.width(TrackLabSpacing.sm))
            TrackLabTag(
                text = stringResource(
                    R.string.home_completed_sessions,
                    uiState.completedCount,
                    uiState.totalCount,
                ),
                containerColor = Cream50.copy(alpha = 0.12f),
                contentColor = Cream50,
            )
        }
        Spacer(Modifier.height(TrackLabSpacing.lg))
        TrackLabAccentButton(
            text = stringResource(R.string.action_view_plan),
            onClick = onOpenPlan,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun NextWorkoutCard(
    uiState: HomeUiState,
    onOpenWorkout: (Int, Int) -> Unit,
) {
    val todaySession = uiState.todaySession
    if (todaySession != null && todaySession.kind.name == "DINLENME") {
        TrackLabCard {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = Turquoise200.copy(alpha = 0.6f),
                ) {
                    Icon(
                        imageVector = Icons.Default.DirectionsRun,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(12.dp)
                            .size(22.dp),
                        tint = Turquoise700,
                    )
                }
                Spacer(Modifier.width(TrackLabSpacing.md))
                Column(Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.home_today_rest_title),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = stringResource(R.string.home_today_rest_desc),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                TrackLabTag(text = stringResource(R.string.home_tag_rest))
            }
        }
        val next = uiState.nextSession
        if (next != null) {
            Spacer(Modifier.height(TrackLabSpacing.sm))
            WorkoutCard(session = next, onOpenWorkout = onOpenWorkout)
        }
        return
    }

    val next = uiState.nextSession
    if (next != null) {
        WorkoutCard(session = next, onOpenWorkout = onOpenWorkout)
    } else if (uiState.plan != null) {
        TrackLabEmpty(
            title = stringResource(R.string.home_plan_complete_title),
            description = stringResource(R.string.home_plan_complete_desc),
        )
    } else {
        TrackLabEmpty(
            title = stringResource(R.string.home_no_plan_title),
            description = stringResource(R.string.home_no_plan_desc),
        )
    }
}

@Composable
private fun WorkoutCard(
    session: WorkoutSession,
    onOpenWorkout: (Int, Int) -> Unit,
) {
    TrackLabCard {
        Text(
            text = stringResource(
                R.string.next_workout_subtitle,
                session.dayOfWeek.fullName(),
                session.weekNumber,
            ),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
        )
        Text(
            text = session.title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(TrackLabSpacing.xs))
        Text(
            text = session.briefSummary(),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(TrackLabSpacing.md))
        TrackLabButton(
            text = stringResource(R.string.action_start),
            onClick = { onOpenWorkout(session.weekNumber, session.dayOfWeek.value) },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    TrackLabTheme {
        HomeScreen(
            uiState = HomeUiState(
                currentTimeMs = 76_000L,
                targetTimeMs = 68_000L,
                daysRemaining = 53L,
            ),
            onOpenSettings = {},
            onOpenPlan = {},
            onOpenWorkout = { _, _ -> },
        )
    }
}
