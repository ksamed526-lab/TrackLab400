package com.tracklab400.app.data.stats

import com.tracklab400.app.data.model.Profile
import com.tracklab400.app.data.model.SessionRecord
import com.tracklab400.app.data.model.SessionStatus

enum class ProgressDateRange {
    LAST_7_DAYS,
    LAST_4_WEEKS,
    ALL,
}

data class DistanceSeriesPoint(
    val dateMillis: Long,
    val bestMs: Long,
    val repCount: Int,
    val targetMs: Long?,
)

data class WeekConsistency(
    val weekNumber: Int,
    val completed: Int,
    val attempted: Int,
    val planned: Int,
    val skipped: Int,
) {
    val percent: Float
        get() = if (planned <= 0) 0f else (attempted.toFloat() / planned).coerceIn(0f, 1f)
}

data class BucketPoint(
    val label: String,
    val count: Int,
    val avgMs: Long,
)

data class CompletionStats(
    val completed: Int = 0,
    val partial: Int = 0,
    val skipped: Int = 0,
    val totalMain: Int = 0,
) {
    val percent: Float
        get() = if (totalMain <= 0) {
            0f
        } else {
            ((completed + PARTIAL_CREDIT * partial) / totalMain).coerceIn(0f, 1f)
        }

    companion object {
        const val PARTIAL_CREDIT = 0.5f
    }
}

data class ProgressUiState(
    val range: ProgressDateRange = ProgressDateRange.ALL,
    val profile: Profile? = null,
    val hasSessions: Boolean = false,
    val distanceSeries: Map<Int, List<DistanceSeriesPoint>> = emptyMap(),
    val distanceBaselines: Map<Int, Long?> = emptyMap(),
    val distanceTargets: Map<Int, Long?> = emptyMap(),
    val targetGapMilliseconds: Long? = null,
    val targetReached: Boolean = false,
    val headlineProgress: Float = 0f,
    val best400RangeMs: Long? = null,
    val weeks: List<WeekConsistency> = emptyList(),
    val completion: CompletionStats = CompletionStats(),
    val avgRpe: Float? = null,
    val rpeSessionCount: Int = 0,
    val sleepPerformance: List<BucketPoint> = emptyList(),
    val legPerformance: List<BucketPoint> = emptyList(),
    val strengthDataAvailable: Boolean = false,
    val assessment: TargetAssessment? = null,
)

object ProgressStatsCalculator {

    const val MAIN_SESSIONS_PER_WEEK = 4
    const val TOTAL_MAIN_SESSIONS = 32

    val SUPPORTED_DISTANCES = listOf(100, 200, 300, 400)

    private const val DAY_MS = 86_400_000L

    fun compute(
        records: List<SessionRecord>,
        profile: Profile?,
        range: ProgressDateRange,
        now: Long = System.currentTimeMillis(),
    ): ProgressUiState {
        val start = rangeStartMillis(range, now)
        val inRange = records.filter { it.completedAt >= start }

        val series = buildDistanceSeries(inRange)

        val baselines = mapOf(
            100 to profile?.current100mMs,
            200 to profile?.current200mMs,
            300 to profile?.current300mMs,
            400 to profile?.current400mMs,
        )

        val targets = mutableMapOf<Int, Long?>(
            100 to null,
            200 to null,
            300 to null,
            400 to profile?.targetTimeMs,
        )
        for (entry in series) {
            if (entry.key != 400) {
                targets[entry.key] = entry.value.lastOrNull()?.targetMs
            }
        }

        val best400 = series[400]?.minOfOrNull { it.bestMs }

        val current400 = profile?.current400mMs
        val target400 = profile?.targetTimeMs
        val gap = if (current400 != null && target400 != null) current400 - target400 else null

        return ProgressUiState(
            range = range,
            profile = profile,
            hasSessions = records.isNotEmpty(),
            distanceSeries = series,
            distanceBaselines = baselines,
            distanceTargets = targets,
            targetGapMilliseconds = gap,
            targetReached = gap != null && gap <= 0,
            headlineProgress = headlineProgress(current400, target400, best400),
            best400RangeMs = best400,
            weeks = weeklyConsistency(inRange),
            completion = completionStats(records),
            avgRpe = averageRpe(inRange),
            rpeSessionCount = inRange.count { it.rpe != null },
            sleepPerformance = bucketize(
                items = inRange.filter { it.sleepHours != null && it.averageRepMs != null },
                key = { sleepBucket(it.sleepHours!!) },
                order = listOf(SLEEP_BELOW_7, SLEEP_7_8, SLEEP_ABOVE_8),
            ),
            legPerformance = bucketize(
                items = inRange.filter { it.legFeeling != null && it.averageRepMs != null },
                key = { legBucket(it.legFeeling!!) },
                order = LEG_ORDER,
            ),
            strengthDataAvailable = false,
            assessment = TargetAssessor.assess(profile, records, now),
        )
    }

    fun rangeStartMillis(range: ProgressDateRange, now: Long): Long = when (range) {
        ProgressDateRange.LAST_7_DAYS -> now - 7 * DAY_MS
        ProgressDateRange.LAST_4_WEEKS -> now - 28 * DAY_MS
        ProgressDateRange.ALL -> Long.MIN_VALUE / 4
    }

    private fun buildDistanceSeries(records: List<SessionRecord>): Map<Int, List<DistanceSeriesPoint>> {
        val result = mutableMapOf<Int, List<DistanceSeriesPoint>>()
        for (distance in SUPPORTED_DISTANCES) {
            val points = records
                .filter { record -> record.reps.any { it.distanceM == distance } }
                .map { record ->
                    val distReps = record.reps.filter { it.distanceM == distance }
                    DistanceSeriesPoint(
                        dateMillis = record.completedAt,
                        bestMs = distReps.minOf { it.actualMs },
                        repCount = distReps.size,
                        targetMs = distReps.mapNotNull { it.targetMaxMs ?: it.targetMinMs }.maxOrNull(),
                    )
                }
                .sortedBy { it.dateMillis }
            if (points.isNotEmpty()) {
                result[distance] = points
            }
        }
        return result
    }

    private fun weeklyConsistency(records: List<SessionRecord>): List<WeekConsistency> =
        records
            .filter { it.status != SessionStatus.PLANNED }
            .groupBy { it.weekNumber }
            .map { (weekNumber, list) ->
                WeekConsistency(
                    weekNumber = weekNumber,
                    completed = list.count { it.status == SessionStatus.COMPLETED },
                    attempted = list.count {
                        it.status == SessionStatus.COMPLETED || it.status == SessionStatus.PARTIAL
                    },
                    planned = MAIN_SESSIONS_PER_WEEK,
                    skipped = list.count { it.status == SessionStatus.SKIPPED },
                )
            }
            .sortedBy { it.weekNumber }

    private fun completionStats(records: List<SessionRecord>): CompletionStats = CompletionStats(
        completed = records.count { it.status == SessionStatus.COMPLETED },
        partial = records.count { it.status == SessionStatus.PARTIAL },
        skipped = records.count { it.status == SessionStatus.SKIPPED },
        totalMain = TOTAL_MAIN_SESSIONS,
    )

    private fun averageRpe(records: List<SessionRecord>): Float? {
        val rpes = records.mapNotNull { it.rpe }
        return if (rpes.isEmpty()) null else rpes.sum().toFloat() / rpes.size
    }

    private fun bucketize(
        items: List<SessionRecord>,
        key: (SessionRecord) -> String,
        order: List<String>,
    ): List<BucketPoint> =
        items.groupBy(key)
            .map { (label, list) ->
                val averages = list.mapNotNull { it.averageRepMs }
                if (averages.isEmpty()) {
                    null
                } else {
                    BucketPoint(
                        label = label,
                        count = list.size,
                        avgMs = averages.sum() / averages.size,
                    )
                }
            }
            .filterNotNull()
            .sortedBy { order.indexOf(it.label) }

    private fun headlineProgress(current: Long?, target: Long?, best: Long?): Float {
        if (current == null || target == null) return 0f
        if (current <= target) return 1f
        if (best == null) return 0f
        return ((current - best).toFloat() / (current - target).toFloat()).coerceIn(0f, 1f)
    }

    private const val SLEEP_BELOW_7 = "<7 saat"
    private const val SLEEP_7_8 = "7–8 saat"
    private const val SLEEP_ABOVE_8 = ">8 saat"
    private val LEG_ORDER = listOf("Seviye 1–2", "Seviye 3", "Seviye 4–5")

    private fun sleepBucket(hours: Double): String = when {
        hours < 7.0 -> SLEEP_BELOW_7
        hours > 8.0 -> SLEEP_ABOVE_8
        else -> SLEEP_7_8
    }

    private fun legBucket(feeling: Int): String = when {
        feeling <= 2 -> LEG_ORDER[0]
        feeling == 3 -> LEG_ORDER[1]
        else -> LEG_ORDER[2]
    }
}