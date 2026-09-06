package com.tracklab400.app.ui.screens.progress

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tracklab400.app.R
import com.tracklab400.app.data.model.Profile
import com.tracklab400.app.data.model.SessionStatus
import com.tracklab400.app.data.model.TimeUtils
import com.tracklab400.app.data.stats.BucketPoint
import com.tracklab400.app.data.stats.ProgressDateRange
import com.tracklab400.app.data.stats.ProgressStatsCalculator
import com.tracklab400.app.data.stats.ProgressUiState
import com.tracklab400.app.data.stats.RecommendationKind
import com.tracklab400.app.data.stats.TargetOutlook
import com.tracklab400.app.ui.components.ProgressBarItem
import com.tracklab400.app.ui.components.TrackLabBarChart
import com.tracklab400.app.ui.components.TrackLabCard
import com.tracklab400.app.ui.components.TrackLabChoiceChips
import com.tracklab400.app.ui.components.TrackLabEmpty
import com.tracklab400.app.ui.components.TrackLabLineChart
import com.tracklab400.app.ui.components.TrackLabProgressBar
import com.tracklab400.app.ui.components.TrackLabSectionHeader
import com.tracklab400.app.ui.theme.TrackLabSpacing
import com.tracklab400.app.ui.theme.statValue
import com.tracklab400.app.ui.theme.trackLabColors
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressScreen(
    uiState: ProgressUiState,
    onRangeChange: (ProgressDateRange) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.progress_title)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = TrackLabSpacing.md),
        ) {
            RangeFilterChips(selected = uiState.range, onRangeChange = onRangeChange)
            Spacer(Modifier.height(TrackLabSpacing.sm))

            if (!uiState.hasSessions) {
                TrackLabEmpty(
                    title = stringResource(R.string.progress_empty_title),
                    description = stringResource(R.string.progress_empty_desc),
                )
            } else {
                ProgressContent(uiState = uiState)
            }
        }
    }
}

@Composable
private fun RangeFilterChips(
    selected: ProgressDateRange,
    onRangeChange: (ProgressDateRange) -> Unit,
) {
    TrackLabChoiceChips(
        options = listOf(
            ProgressDateRange.LAST_7_DAYS,
            ProgressDateRange.LAST_4_WEEKS,
            ProgressDateRange.ALL,
        ),
        optionLabel = { range ->
            when (range) {
                ProgressDateRange.LAST_7_DAYS -> stringResource(R.string.progress_range_7d)
                ProgressDateRange.LAST_4_WEEKS -> stringResource(R.string.progress_range_4w)
                ProgressDateRange.ALL -> stringResource(R.string.progress_range_all)
            }
        },
        isSelected = { it == selected },
        onToggle = onRangeChange,
    )
}

@Composable
private fun ProgressContent(uiState: ProgressUiState) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(TrackLabSpacing.md),
    ) {
        TargetAssessmentCard(uiState = uiState)
        TargetHeadlineCard(uiState = uiState)
        DistanceProgressCard(uiState = uiState)
        CompletionCard(uiState = uiState)
        WeeklyConsistencyCard(uiState = uiState)
        AvgRpeCard(uiState = uiState)
        SleepPerformanceCard(uiState = uiState)
        LegPerformanceCard(uiState = uiState)
        StrengthProgressCard(uiState = uiState)
    }
}

@Composable
private fun TargetAssessmentCard(uiState: ProgressUiState) {
    val assessment = uiState.assessment ?: return
    TrackLabCard(
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
    ) {
        TrackLabSectionHeader(
            title = stringResource(R.string.progress_assessment_title),
            subtitle = stringResource(R.string.progress_assessment_subtitle),
        )
        Spacer(Modifier.height(TrackLabSpacing.sm))

        val outlookColor = when (assessment.outlook) {
            TargetOutlook.AGGRESSIVE -> MaterialTheme.trackLabColors.warning
            TargetOutlook.REALISTIC -> MaterialTheme.colorScheme.primary
            TargetOutlook.EASY -> MaterialTheme.trackLabColors.success
            TargetOutlook.REACHED -> MaterialTheme.trackLabColors.success
        }
        val outlookLabel = when (assessment.outlook) {
            TargetOutlook.AGGRESSIVE -> stringResource(R.string.progress_assessment_aggressive)
            TargetOutlook.REALISTIC -> stringResource(R.string.progress_assessment_realistic)
            TargetOutlook.EASY -> stringResource(R.string.progress_assessment_easy)
            TargetOutlook.REACHED -> stringResource(R.string.progress_assessment_reached)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .background(outlookColor, RoundedCornerShape(50))
                    .padding(horizontal = TrackLabSpacing.sm, vertical = 2.dp),
            ) {
                Text(
                    text = outlookLabel,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                )
            }
            Spacer(Modifier.width(TrackLabSpacing.sm))
            Text(
                text = stringResource(
                    R.string.progress_assessment_gap,
                    TimeUtils.formatSeconds(assessment.gapMs),
                    (assessment.targetPercent * 100).roundToInt(),
                ),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Spacer(Modifier.height(TrackLabSpacing.sm))

        assessment.recommendations.forEach { rec ->
            Column(Modifier.fillMaxWidth()) {
                Text(
                    text = rec.message,
                    style = when (rec.kind) {
                        RecommendationKind.CONSULT_PROFESSIONAL ->
                            MaterialTheme.typography.labelSmall
                        else -> MaterialTheme.typography.bodyMedium
                    },
                    color = when (rec.kind) {
                        RecommendationKind.STOP_HARD_PAIN,
                        RecommendationKind.CONSULT_PROFESSIONAL,
                        -> MaterialTheme.colorScheme.onErrorContainer
                        RecommendationKind.VOLUME_REDUCTION,
                        RecommendationKind.INTENSITY_REDUCTION,
                        RecommendationKind.CHECKPOINT_BEHIND,
                        -> MaterialTheme.trackLabColors.warning
                        else -> MaterialTheme.colorScheme.onSurface
                    },
                )
                if (rec != assessment.recommendations.last()) {
                    Spacer(Modifier.height(TrackLabSpacing.xs))
                }
            }
        }
    }
}

@Composable
private fun TargetHeadlineCard(uiState: ProgressUiState) {
    val profile = uiState.profile
    TrackLabCard {
        Text(
            text = stringResource(R.string.progress_target_title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(TrackLabSpacing.sm))

        val gap = uiState.targetGapMilliseconds
        if (profile != null && gap != null) {
            val progressColor = if (uiState.targetReached) {
                MaterialTheme.trackLabColors.success
            } else {
                MaterialTheme.colorScheme.primary
            }
            Text(
                text = if (uiState.targetReached) {
                    stringResource(
                        R.string.progress_target_gap_done,
                        TimeUtils.formatSeconds(0L),
                    )
                } else {
                    stringResource(R.string.progress_target_gap, TimeUtils.formatSeconds(gap))
                },
                style = MaterialTheme.typography.statValue,
                color = progressColor,
            )
            Spacer(Modifier.height(TrackLabSpacing.xs))
            Text(
                text = stringResource(
                    R.string.progress_target_subtitle,
                    TimeUtils.formatMs(profile.current400mMs),
                    TimeUtils.formatMs(profile.targetTimeMs),
                ),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(TrackLabSpacing.sm))
            TrackLabProgressBar(
                progress = uiState.headlineProgress,
                progressColor = progressColor,
            )
            uiState.best400RangeMs?.let { best ->
                Spacer(Modifier.height(TrackLabSpacing.sm))
                Text(
                    text = stringResource(R.string.progress_best_range, TimeUtils.formatMs(best)),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        } else {
            Text(
                text = stringResource(R.string.progress_target_missing),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun DistanceProgressCard(uiState: ProgressUiState) {
    TrackLabCard {
        TrackLabSectionHeader(
            title = stringResource(R.string.progress_distance_title),
            subtitle = stringResource(R.string.progress_distance_subtitle),
        )
        Spacer(Modifier.height(TrackLabSpacing.sm))

        val available = ProgressStatsCalculator.SUPPORTED_DISTANCES.filter {
            uiState.distanceSeries.containsKey(it)
        }
        if (available.isEmpty()) {
            Text(
                text = stringResource(R.string.progress_distance_empty),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        } else {
            var selected by remember { mutableIntStateOf(available.minOf { it }) }
            val effective = if (selected in available) selected else available.minOf { it }

            TrackLabChoiceChips(
                options = available,
                optionLabel = { distance -> stringResource(R.string.progress_distance_chip, distance) },
                isSelected = { it == effective },
                onToggle = { selected = it },
            )
            Spacer(Modifier.height(TrackLabSpacing.sm))

            val points = uiState.distanceSeries[effective].orEmpty()
            TrackLabLineChart(
                points = points,
                baselineMs = uiState.distanceBaselines[effective],
                targetMs = uiState.distanceTargets[effective],
                contentDescription = stringResource(
                    R.string.progress_distance_chart_desc,
                    effective,
                    points.size,
                    points.minOfOrNull { it.bestMs }?.let { TimeUtils.formatMs(it) } ?: "-",
                ),
                labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                gridColor = MaterialTheme.colorScheme.outlineVariant,
                seriesColor = MaterialTheme.colorScheme.primary,
                targetColor = MaterialTheme.colorScheme.tertiary,
                baselineColor = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(TrackLabSpacing.xs))
            ChartLegend(
                entries = listOf(
                    ChartLegendEntry(
                        label = stringResource(R.string.progress_legend_data),
                        color = MaterialTheme.colorScheme.primary,
                    ),
                    ChartLegendEntry(
                        label = stringResource(R.string.progress_legend_target),
                        color = MaterialTheme.colorScheme.tertiary,
                    ),
                    ChartLegendEntry(
                        label = stringResource(R.string.progress_legend_baseline),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    ),
                ),
            )
            val firstDate = points.first().dateMillis
            val lastDate = points.last().dateMillis
            Spacer(Modifier.height(TrackLabSpacing.xs))
            Text(
                text = if (firstDate == lastDate) {
                    progressDate(firstDate)
                } else {
                    stringResource(
                        R.string.progress_range_caption,
                        progressDate(firstDate),
                        progressDate(lastDate),
                    )
                },
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun CompletionCard(uiState: ProgressUiState) {
    val completion = uiState.completion
    TrackLabCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.progress_completion_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.height(TrackLabSpacing.xxs))
                Text(
                    text = stringResource(R.string.progress_completion_subtitle),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Text(
                text = "${(completion.percent * 100).roundToInt()}%",
                style = MaterialTheme.typography.statValue,
                color = MaterialTheme.colorScheme.primary,
            )
        }
        Spacer(Modifier.height(TrackLabSpacing.sm))
        TrackLabProgressBar(
            progress = completion.percent,
            progressColor = MaterialTheme.trackLabColors.success,
        )
        Spacer(Modifier.height(TrackLabSpacing.sm))
        Text(
            text = stringResource(
                R.string.progress_completion_counts,
                completion.completed,
                completion.partial,
                completion.skipped,
                completion.totalMain,
            ),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun WeeklyConsistencyCard(uiState: ProgressUiState) {
    TrackLabCard {
        TrackLabSectionHeader(
            title = stringResource(R.string.progress_weekly_title),
            subtitle = stringResource(R.string.progress_weekly_subtitle),
        )
        Spacer(Modifier.height(TrackLabSpacing.sm))
        if (uiState.weeks.isEmpty()) {
            Text(
                text = stringResource(R.string.progress_weekly_empty),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        } else {
            TrackLabBarChart(
                items = uiState.weeks.map { week ->
                    ProgressBarItem(
                        label = stringResource(R.string.progress_week_short, week.weekNumber),
                        value = week.attempted.toFloat(),
                        caption = "${week.attempted}/${week.planned}",
                    )
                },
                maxValue = ProgressStatsCalculator.MAIN_SESSIONS_PER_WEEK.toFloat(),
                contentDescription = uiState.weeks.map {
                    stringResource(R.string.progress_week_short, it.weekNumber) +
                        " ${it.attempted}/${it.planned}"
                }.joinToString(", "),
                labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                barColor = MaterialTheme.colorScheme.secondary,
                trackColor = MaterialTheme.colorScheme.outlineVariant,
            )
        }
    }
}

@Composable
private fun AvgRpeCard(uiState: ProgressUiState) {
    TrackLabCard {
        Text(
            text = stringResource(R.string.progress_rpe_title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(TrackLabSpacing.sm))
        val rpe = uiState.avgRpe
        if (rpe == null) {
            Text(
                text = stringResource(R.string.progress_rpe_empty),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        } else {
            Text(
                text = String.format(Locale.US, "%.1f", rpe).replace('.', ','),
                style = MaterialTheme.typography.statValue,
                color = MaterialTheme.colorScheme.primary,
            )
            Spacer(Modifier.height(TrackLabSpacing.xxs))
            Text(
                text = stringResource(R.string.progress_rpe_sessions, uiState.rpeSessionCount),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun SleepPerformanceCard(uiState: ProgressUiState) {
    TrackLabCard {
        TrackLabSectionHeader(
            title = stringResource(R.string.progress_sleep_title),
            subtitle = stringResource(R.string.progress_sleep_subtitle),
        )
        Spacer(Modifier.height(TrackLabSpacing.sm))
        if (uiState.sleepPerformance.isEmpty()) {
            Text(
                text = stringResource(R.string.progress_sleep_empty),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        } else {
            TrackLabBarChart(
                items = uiState.sleepPerformance.map { bucket ->
                    bucketToChartItem(bucket)
                },
                contentDescription = uiState.sleepPerformance.joinToString(", ") {
                    "${it.label} (${it.count}): ${TimeUtils.formatSeconds(it.avgMs)} sn"
                },
                labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                barColor = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.outlineVariant,
            )
            Spacer(Modifier.height(TrackLabSpacing.xs))
            Text(
                text = uiState.sleepPerformance.map {
                    stringResource(R.string.progress_bucket_count, it.label, it.count)
                }.joinToString(" · "),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = stringResource(R.string.progress_bar_hint),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun LegPerformanceCard(uiState: ProgressUiState) {
    TrackLabCard {
        TrackLabSectionHeader(
            title = stringResource(R.string.progress_leg_title),
            subtitle = stringResource(R.string.progress_leg_subtitle),
        )
        Spacer(Modifier.height(TrackLabSpacing.sm))
        if (uiState.legPerformance.isEmpty()) {
            Text(
                text = stringResource(R.string.progress_leg_empty),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        } else {
            TrackLabBarChart(
                items = uiState.legPerformance.map { bucket -> bucketToChartItem(bucket) },
                contentDescription = uiState.legPerformance.joinToString(", ") {
                    "${it.label} (${it.count}): ${TimeUtils.formatSeconds(it.avgMs)} sn"
                },
                labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                barColor = MaterialTheme.colorScheme.tertiary,
                trackColor = MaterialTheme.colorScheme.outlineVariant,
            )
            Spacer(Modifier.height(TrackLabSpacing.xs))
            Text(
                text = uiState.legPerformance.map {
                    stringResource(R.string.progress_bucket_count, it.label, it.count)
                }.joinToString(" · ") + " · " + stringResource(R.string.progress_leg_scale),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun StrengthProgressCard(uiState: ProgressUiState) {
    TrackLabCard {
        TrackLabSectionHeader(
            title = stringResource(R.string.progress_strength_title),
            subtitle = stringResource(R.string.progress_strength_subtitle),
        )
        Spacer(Modifier.height(TrackLabSpacing.sm))
        if (uiState.strengthDataAvailable) {
            Text(
                text = stringResource(R.string.progress_strength_empty),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        } else {
            TrackLabEmpty(
                title = stringResource(R.string.progress_strength_empty_title),
                description = stringResource(R.string.progress_strength_empty_desc),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(TrackLabSpacing.xs),
            )
        }
    }
}

@Composable
private fun bucketToChartItem(bucket: BucketPoint): ProgressBarItem = ProgressBarItem(
    label = bucket.label,
    value = bucket.avgMs.toFloat(),
    caption = "${TimeUtils.formatSeconds(bucket.avgMs)} sn",
)

@Composable
private fun ChartLegend(entries: List<ChartLegendEntry>) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(TrackLabSpacing.md),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        entries.forEach { entry ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(width = 16.dp, height = 4.dp)
                        .background(entry.color, RoundedCornerShape(2.dp)),
                )
                Spacer(Modifier.width(TrackLabSpacing.xxs))
                Text(
                    text = entry.label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

private data class ChartLegendEntry(
    val label: String,
    val color: Color,
)

private fun progressDate(millis: Long): String {
    val date = LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneId.systemDefault())
    return date.format(DateTimeFormatter.ofPattern("d MMM", Locale("tr", "TR")))
}

@Preview(showBackground = true)
@Composable
private fun ProgressScreenPreview() {
    val demo = ProgressStatsCalculator.compute(
        records = demoRecords(),
        profile = demoProfile(),
        range = ProgressDateRange.ALL,
        now = System.currentTimeMillis(),
    )
    ProgressScreen(uiState = demo, onRangeChange = {})
}

private fun demoProfile(): Profile = Profile(
    id = 1L,
    nickname = "Demo Koşucu",
    age = 24,
    current100mMs = 14_200L,
    current200mMs = 30_000L,
    current300mMs = 48_000L,
    current400mMs = 76_000L,
    targetDistanceM = 400,
    targetTimeMs = 68_000L,
    prepWeeks = 8,
    trainingDaysPerWeek = 4,
    startDateMillis = System.currentTimeMillis() - 30L * 86_400_000L,
    trackAccess = com.tracklab400.app.data.model.TrackAccess.PIST_VAR,
    gymStatus = com.tracklab400.app.data.model.GymStatus.SALON_VAR,
    equipment = setOf(
        com.tracklab400.app.data.model.Equipment.BARBELL,
        com.tracklab400.app.data.model.Equipment.DUMBBELL,
    ),
    experience = com.tracklab400.app.data.model.TrainingExperience.ORTA,
    injuryInfo = null,
    stopwatchType = com.tracklab400.app.data.model.StopwatchType.EL_KRONOMETRESI,
    isDemo = true,
    createdAt = System.currentTimeMillis(),
    updatedAt = System.currentTimeMillis(),
)

private fun demoRecords(): List<com.tracklab400.app.data.model.SessionRecord> {
    val now = System.currentTimeMillis()
    fun session(
        id: Long,
        week: Int,
        distance: Int,
        bestMs: Long,
        rpe: Int,
        sleepHours: Double,
        legFeeling: Int,
        daysAgo: Long,
    ): com.tracklab400.app.data.model.SessionRecord {
        val reps = listOf(
            com.tracklab400.app.data.model.RepRecord(
                id = id * 10,
                sessionRecordId = id,
                repIndex = 1,
                distanceM = distance,
                targetMinMs = null,
                targetMaxMs = bestMs + 4_000L,
                actualMs = bestMs + 500L,
                isManual = true,
                recordedAt = now - daysAgo * 86_400_000L,
            ),
            com.tracklab400.app.data.model.RepRecord(
                id = id * 10 + 1,
                sessionRecordId = id,
                repIndex = 2,
                distanceM = distance,
                targetMinMs = null,
                targetMaxMs = bestMs + 4_000L,
                actualMs = bestMs,
                isManual = true,
                recordedAt = now - daysAgo * 86_400_000L,
            ),
        )
        return com.tracklab400.app.data.model.SessionRecord(
            id = id,
            weekNumber = week,
            dayOfWeek = 4,
            sessionKind = "TEMPO",
            completedAt = now - daysAgo * 86_400_000L,
            status = if (daysAgo < 4) SessionStatus.COMPLETED else SessionStatus.PARTIAL,
            rpe = rpe,
            sleepHours = sleepHours,
            energyLevel = 3,
            legFeeling = legFeeling,
            completedReps = reps.size,
            expectedReps = 8,
            totalDistanceM = distance * reps.size,
            reps = reps,
        )
    }
    return listOf(
        session(1, 1, 100, 15_000L, 6, 7.5, 3, 20),
        session(2, 1, 100, 14_500L, 7, 8.5, 2, 15),
        session(3, 3, 300, 50_000L, 8, 6.5, 4, 6),
        session(4, 4, 300, 49_000L, 7, 8.0, 3, 3),
        session(5, 4, 400, 71_500L, 9, 7.0, 4, 2),
    )
}