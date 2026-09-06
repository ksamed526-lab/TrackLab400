package com.tracklab400.app.data.stats

import com.tracklab400.app.data.model.Equipment
import com.tracklab400.app.data.model.GymStatus
import com.tracklab400.app.data.model.Profile
import com.tracklab400.app.data.model.RepRecord
import com.tracklab400.app.data.model.SessionRecord
import com.tracklab400.app.data.model.SessionStatus
import com.tracklab400.app.data.model.StopwatchType
import com.tracklab400.app.data.model.TrackAccess
import com.tracklab400.app.data.model.TrainingExperience
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ProgressStatsCalculatorTest {

    private val now = 1_700_000_000_000L
    private val DAY = 86_400_000L

    private fun profile(
        current100mMs: Long? = 14_000L,
        current200mMs: Long? = 30_000L,
        current300mMs: Long? = 48_000L,
        current400mMs: Long = 76_000L,
        targetTimeMs: Long = 68_000L,
    ) = Profile(
        id = 1L,
        nickname = "Koşucu",
        age = 24,
        current100mMs = current100mMs,
        current200mMs = current200mMs,
        current300mMs = current300mMs,
        current400mMs = current400mMs,
        targetDistanceM = 400,
        targetTimeMs = targetTimeMs,
        prepWeeks = 8,
        trainingDaysPerWeek = 4,
        startDateMillis = now - 30 * DAY,
        trackAccess = TrackAccess.PIST_VAR,
        gymStatus = GymStatus.SALON_VAR,
        equipment = setOf(Equipment.BARBELL),
        experience = TrainingExperience.ORTA,
        injuryInfo = null,
        stopwatchType = StopwatchType.EL_KRONOMETRESI,
        isDemo = true,
        createdAt = now,
        updatedAt = now,
    )

    private fun session(
        id: Long,
        week: Int,
        daysAgo: Long,
        status: SessionStatus,
        reps: List<Pair<Int, Long>>,
        rpe: Int? = null,
        sleepHours: Double? = null,
        legFeeling: Int? = null,
    ) = SessionRecord(
        id = id,
        weekNumber = week,
        dayOfWeek = 4,
        sessionKind = "TEMPO",
        completedAt = now - daysAgo * DAY,
        status = status,
        rpe = rpe,
        sleepHours = sleepHours,
        legFeeling = legFeeling,
        reps = reps.mapIndexed { index, (distance, actual) ->
            RepRecord(
                id = id * 10 + index,
                sessionRecordId = id,
                repIndex = index + 1,
                distanceM = distance,
                targetMaxMs = actual + 3_000L,
                actualMs = actual,
            )
        },
    )

    @Test
    fun emptyRecords_declareNoSessions() {
        val state = ProgressStatsCalculator.compute(
            records = emptyList(),
            profile = profile(),
            range = ProgressDateRange.ALL,
            now = now,
        )
        assertFalse(state.hasSessions)
        assertTrue(state.distanceSeries.isEmpty())
        assertEquals(0f, state.completion.percent, 0f)
        assertNull(state.avgRpe)
        assertTrue(state.weeks.isEmpty())
        assertTrue(state.sleepPerformance.isEmpty())
        assertTrue(state.legPerformance.isEmpty())
        assertFalse(state.strengthDataAvailable)
    }

    @Test
    fun targetGap_positiveBeforeTarget() {
        val state = ProgressStatsCalculator.compute(
            records = emptyList(),
            profile = profile(current400mMs = 76_000L, targetTimeMs = 68_000L),
            range = ProgressDateRange.ALL,
            now = now,
        )
        assertEquals(8_000L, state.targetGapMilliseconds)
        assertFalse(state.targetReached)
        assertEquals(0f, state.headlineProgress, 0f)
    }

    @Test
    fun targetGap_reachedWhenCurrentAtOrBelowTarget() {
        val state = ProgressStatsCalculator.compute(
            records = emptyList(),
            profile = profile(current400mMs = 67_500L, targetTimeMs = 68_000L),
            range = ProgressDateRange.ALL,
            now = now,
        )
        assertTrue(state.targetReached)
        assertEquals(1f, state.headlineProgress, 0f)
    }

    @Test
    fun headlineProgress_usesBestWithinRange() {
        val records = listOf(
            session(1, 4, 2, SessionStatus.COMPLETED, reps = listOf(400 to 71_500L)),
        )
        val state = ProgressStatsCalculator.compute(
            records = records,
            profile = profile(current400mMs = 76_000L, targetTimeMs = 68_000L),
            range = ProgressDateRange.ALL,
            now = now,
        )
        val expected = (76_000 - 71_500L).toFloat() / (76_000 - 68_000L)
        assertEquals(expected, state.headlineProgress, 0.001f)
        assertEquals(71_500L, state.best400RangeMs)
    }

    @Test
    fun last7Days_filtersToRecentSessions() {
        val records = listOf(
            session(1, 3, 10, SessionStatus.COMPLETED, reps = listOf(100 to 14_500L), rpe = 9),
            session(2, 4, 2, SessionStatus.COMPLETED, reps = listOf(100 to 14_000L), rpe = 6),
        )
        val state = ProgressStatsCalculator.compute(
            records = records,
            profile = profile(),
            range = ProgressDateRange.LAST_7_DAYS,
            now = now,
        )
        val series = state.distanceSeries.getValue(100)
        assertEquals(listOf(14_000L), series.map { it.bestMs })
        assertEquals(6f, state.avgRpe!!, 0.001f)
        assertEquals(1, state.rpeSessionCount)
    }

    @Test
    fun last4Weeks_includesSessionsWithin28Days() {
        val records = listOf(
            session(1, 3, 20, SessionStatus.COMPLETED, reps = listOf(100 to 14_500L)),
            session(2, 4, 2, SessionStatus.COMPLETED, reps = listOf(100 to 14_000L)),
        )
        val state = ProgressStatsCalculator.compute(
            records = records,
            profile = profile(),
            range = ProgressDateRange.LAST_4_WEEKS,
            now = now,
        )
        assertEquals(2, state.distanceSeries.getValue(100).size)
    }

    @Test
    fun allRange_includesEverything() {
        val records = listOf(
            session(1, 1, 30, SessionStatus.COMPLETED, reps = listOf(100 to 14_500L)),
            session(2, 4, 2, SessionStatus.COMPLETED, reps = listOf(100 to 14_000L)),
        )
        val state = ProgressStatsCalculator.compute(
            records = records,
            profile = profile(),
            range = ProgressDateRange.ALL,
            now = now,
        )
        assertEquals(2, state.distanceSeries.getValue(100).size)
    }

    @Test
    fun distanceSeries_groupsBySessionBestAndSortsByDate() {
        val records = listOf(
            session(
                1, 3, 6, SessionStatus.COMPLETED,
                reps = listOf(400 to 72_000L, 400 to 71_000L, 100 to 15_000L),
            ),
            session(2, 4, 2, SessionStatus.COMPLETED, reps = listOf(400 to 70_500L)),
        )
        val state = ProgressStatsCalculator.compute(
            records = records,
            profile = profile(),
            range = ProgressDateRange.ALL,
            now = now,
        )
        val series = state.distanceSeries.getValue(400)
        assertEquals(listOf(71_000L, 70_500L), series.map { it.bestMs })
        assertEquals(listOf(2, 1), series.map { it.repCount })
        assertTrue(series[0].dateMillis < series[1].dateMillis)
        assertEquals(75_000L, series[0].targetMs) // targetMaxMs = 71_000 + 3_000
    }

    @Test
    fun distanceTarget_for400UsesProfileTarget() {
        val records = listOf(
            session(5, 4, 2, SessionStatus.COMPLETED, reps = listOf(400 to 71_000L, 100 to 14_000L)),
        )
        val state = ProgressStatsCalculator.compute(
            records = records,
            profile = profile(targetTimeMs = 68_000L),
            range = ProgressDateRange.ALL,
            now = now,
        )
        assertEquals(68_000L, state.distanceTargets[400])
        assertEquals(14_000L + 3_000L, state.distanceTargets[100])
    }

    @Test
    fun unsupportedDistance_isExcluded() {
        val records = listOf(
            session(7, 4, 2, SessionStatus.COMPLETED, reps = listOf(600 to 90_000L)),
        )
        val state = ProgressStatsCalculator.compute(
            records = records,
            profile = profile(),
            range = ProgressDateRange.ALL,
            now = now,
        )
        assertTrue(state.distanceSeries.isEmpty())
    }

    @Test
    fun weeklyConsistency_countsStatuses() {
        val records = listOf(
            session(1, 1, 20, SessionStatus.COMPLETED, reps = emptyList()),
            session(2, 1, 19, SessionStatus.COMPLETED, reps = emptyList()),
            session(3, 1, 18, SessionStatus.PARTIAL, reps = emptyList()),
            session(4, 1, 17, SessionStatus.SKIPPED, reps = emptyList()),
            session(5, 2, 16, SessionStatus.PLANNED, reps = emptyList()),
            session(6, 3, 15, SessionStatus.COMPLETED, reps = emptyList()),
        )
        val state = ProgressStatsCalculator.compute(
            records = records,
            profile = profile(),
            range = ProgressDateRange.ALL,
            now = now,
        )
        assertEquals(listOf(1, 3), state.weeks.map { it.weekNumber })
        val week1 = state.weeks[0]
        assertEquals(2, week1.completed)
        assertEquals(3, week1.attempted)
        assertEquals(1, week1.skipped)
        assertEquals(4, week1.planned)
        assertEquals(0.75f, week1.percent, 0.001f)
        assertFalse(state.weeks.any { it.weekNumber == 2 }) // only PLANNED in week 2
    }

    @Test
    fun completion_appliesPartialCredit() {
        val records = listOf(
            session(1, 1, 20, SessionStatus.COMPLETED, reps = emptyList()),
            session(2, 1, 19, SessionStatus.COMPLETED, reps = emptyList()),
            session(3, 1, 18, SessionStatus.COMPLETED, reps = emptyList()),
            session(4, 2, 17, SessionStatus.COMPLETED, reps = emptyList()),
            session(5, 2, 16, SessionStatus.COMPLETED, reps = emptyList()),
            session(6, 3, 15, SessionStatus.COMPLETED, reps = emptyList()),
            session(7, 3, 14, SessionStatus.PARTIAL, reps = emptyList()),
            session(8, 3, 13, SessionStatus.PARTIAL, reps = emptyList()),
        )
        val state = ProgressStatsCalculator.compute(
            records = records,
            profile = profile(),
            range = ProgressDateRange.ALL,
            now = now,
        )
        val expected = (6 + 0.5f * 2) / ProgressStatsCalculator.TOTAL_MAIN_SESSIONS
        assertEquals(expected, state.completion.percent, 0.001f)
        assertEquals(6, state.completion.completed)
        assertEquals(2, state.completion.partial)
        assertEquals(0, state.completion.skipped)
        assertEquals(32, state.completion.totalMain)
    }

    @Test
    fun averageRpe_averagesNonNullRpe() {
        val records = listOf(
            session(1, 4, 3, SessionStatus.COMPLETED, reps = emptyList(), rpe = 6),
            session(2, 4, 2, SessionStatus.COMPLETED, reps = emptyList(), rpe = 8),
            session(3, 4, 1, SessionStatus.COMPLETED, reps = emptyList()),
        )
        val state = ProgressStatsCalculator.compute(
            records = records,
            profile = profile(),
            range = ProgressDateRange.ALL,
            now = now,
        )
        assertEquals(7f, state.avgRpe!!, 0.001f)
        assertEquals(2, state.rpeSessionCount)
    }

    @Test
    fun sleepBuckets_groupInOrder() {
        val records = listOf(
            session(1, 4, 3, SessionStatus.COMPLETED, rpe = 6, sleepHours = 6.5,
                reps = listOf(200 to 31_000L, 200 to 30_000L)),
            session(2, 4, 2, SessionStatus.COMPLETED, rpe = 6, sleepHours = 7.5,
                reps = listOf(200 to 29_500L)),
            session(3, 4, 1, SessionStatus.COMPLETED, rpe = 6, sleepHours = 8.5,
                reps = listOf(200 to 28_000L)),
            session(4, 4, 0, SessionStatus.COMPLETED, rpe = 6, sleepHours = 8.0,
                reps = listOf(200 to 33_000L)),
        )
        val state = ProgressStatsCalculator.compute(
            records = records,
            profile = profile(),
            range = ProgressDateRange.ALL,
            now = now,
        )
        assertEquals(listOf("<7 saat", "7–8 saat", ">8 saat"), state.sleepPerformance.map { it.label })
        val below7 = state.sleepPerformance[0]
        assertEquals((31_000L + 30_000L) / 2, below7.avgMs)
        assertEquals(1, below7.count)
        val mid = state.sleepPerformance[1]
        assertEquals(2, mid.count)
        assertEquals((29_500L + 33_000L) / 2, mid.avgMs)
    }

    @Test
    fun legBuckets_groupByFeeling() {
        val records = listOf(
            session(1, 4, 3, SessionStatus.COMPLETED, legFeeling = 1,
                reps = listOf(300 to 51_000L)),
            session(2, 4, 2, SessionStatus.COMPLETED, legFeeling = 4,
                reps = listOf(300 to 49_000L)),
            session(3, 4, 1, SessionStatus.COMPLETED, legFeeling = 3,
                reps = listOf(300 to 50_000L)),
            session(4, 4, 0, SessionStatus.COMPLETED, legFeeling = 5,
                reps = listOf(300 to 48_500L)),
        )
        val state = ProgressStatsCalculator.compute(
            records = records,
            profile = profile(),
            range = ProgressDateRange.ALL,
            now = now,
        )
        assertEquals(
            listOf("Seviye 1–2", "Seviye 3", "Seviye 4–5"),
            state.legPerformance.map { it.label },
        )
        assertEquals(1, state.legPerformance[0].count)
        assertEquals(2, state.legPerformance[2].count)
        assertEquals((49_000L + 48_500L) / 2, state.legPerformance[2].avgMs)
    }

    @Test
    fun bucketWithoutReps_isOmitted() {
        val records = listOf(
            session(1, 4, 2, SessionStatus.COMPLETED, reps = emptyList(), sleepHours = 5.0),
        )
        val state = ProgressStatsCalculator.compute(
            records = records,
            profile = profile(),
            range = ProgressDateRange.ALL,
            now = now,
        )
        assertTrue(state.sleepPerformance.isEmpty())
    }

    @Test
    fun averageRepMs_perSessionUsesActualReps() {
        val records = listOf(
            session(1, 4, 2, SessionStatus.COMPLETED,
                reps = listOf(100 to 16_000L, 100 to 14_000L), sleepHours = 7.5),
        )
        val state = ProgressStatsCalculator.compute(
            records = records,
            profile = profile(),
            range = ProgressDateRange.ALL,
            now = now,
        )
        assertEquals(15_000L, state.sleepPerformance[0].avgMs)
    }
}