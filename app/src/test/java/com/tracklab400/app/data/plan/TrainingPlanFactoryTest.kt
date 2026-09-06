package com.tracklab400.app.data.plan

import com.tracklab400.app.data.model.DemoProfile
import com.tracklab400.app.data.model.SessionKind
import com.tracklab400.app.data.model.SessionStatus
import com.tracklab400.app.data.model.WorkoutSession
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.DayOfWeek

class TrainingPlanFactoryTest {

    private val now = 1_800_000_000_000L

    private fun buildPlan(
        statuses: Map<Pair<Int, Int>, SessionStatus> = emptyMap(),
    ) = TrainingPlanFactory.build(DemoProfile.build(now), statuses, now)

    private fun session(
        plan: com.tracklab400.app.data.model.TrainingPlan,
        week: Int,
        day: DayOfWeek,
    ): WorkoutSession? = plan.weeks.first { it.weekNumber == week }.days.first { it.dayOfWeek == day }

    @Test
    fun planHasEightWeeksWithSevenDays() {
        val plan = buildPlan()
        assertEquals(8, plan.weeks.size)
        plan.weeks.forEach { week -> assertEquals(7, week.days.size) }
    }

    @Test
    fun weeklyStructureMatchesTemplate() {
        val week1 = buildPlan().weeks.first()

        val monday = week1.days.first { it.dayOfWeek == DayOfWeek.MONDAY }
        assertEquals(SessionKind.HIZLANMA_KUVVET_A, monday.kind)
        assertEquals("Hızlanma + Kuvvet A", monday.title)

        val tuesday = week1.days.first { it.dayOfWeek == DayOfWeek.TUESDAY }
        assertEquals(SessionKind.TEMPO, tuesday.kind)

        val wednesday = week1.days.first { it.dayOfWeek == DayOfWeek.WEDNESDAY }
        assertTrue(wednesday.isRestDay)
        assertEquals(SessionKind.DINLENME, wednesday.kind)

        val thursday = week1.days.first { it.dayOfWeek == DayOfWeek.THURSDAY }
        assertEquals(SessionKind.OZEL_DAYANIKLILIK, thursday.kind)

        val friday = week1.days.first { it.dayOfWeek == DayOfWeek.FRIDAY }
        assertEquals(SessionKind.HAFIF_TOPARLANMA, friday.kind)
        assertTrue(friday.isRestDay)

        val saturday = week1.days.first { it.dayOfWeek == DayOfWeek.SATURDAY }
        assertEquals(SessionKind.MAKS_HIZ_KUVVET_B, saturday.kind)

        val sunday = week1.days.first { it.dayOfWeek == DayOfWeek.SUNDAY }
        assertTrue(sunday.isRestDay)
    }

    @Test
    fun week1ThursdayHasScaled300mTargets() {
        val thursday = session(buildPlan(), 1, DayOfWeek.THURSDAY)!!
        val block300 = thursday.exercises.first { it.distanceM == 300 && it.reps == 2 }
        assertEquals(58_000L, block300.targetMinMs)
        assertEquals(60_000L, block300.targetMaxMs)
    }

    @Test
    fun week1TempoHasScaled100mTargets() {
        val tuesday = session(buildPlan(), 1, DayOfWeek.TUESDAY)!!
        val block = tuesday.exercises.first { it.distanceM == 100 }
        assertEquals(8, block.reps)
        assertEquals(19_000L, block.targetMinMs)
        assertEquals(21_000L, block.targetMaxMs)
    }

    @Test
    fun week4IsDeloadWithCheckpointAndTest() {
        val plan = buildPlan()
        val week4 = plan.weeks.first { it.weekNumber == 4 }
        assertTrue(week4.isDeload)
        assertNotNull(week4.checkpointNote)

        val thursday = session(plan, 4, DayOfWeek.THURSDAY)!!
        val testBlock = thursday.exercises.first { it.isTest }
        assertEquals(300, testBlock.distanceM)
        assertEquals(53_000L, testBlock.targetMinMs)
        assertEquals(55_000L, testBlock.targetMaxMs)
    }

    @Test
    fun week8SaturdayIsRaceSessionWithScaledRhythm() {
        val saturday = session(buildPlan(), 8, DayOfWeek.SATURDAY)!!
        assertEquals(SessionKind.TEST_YARIS, saturday.kind)
        val raceBlock = saturday.exercises.first { it.distanceM == 400 }
        assertTrue(raceBlock.isTest)
        val rhythm = saturday.exercises.first { it.name == "Hedef ritim" }
        assertTrue(rhythm.cues.orEmpty().contains("16,5"))
        assertTrue(rhythm.cues.orEmpty().contains("33,0"))
        assertTrue(rhythm.cues.orEmpty().contains("50,5"))
    }

    @Test
    fun statusesAreMappedOntoSessions() {
        val plan = buildPlan(
            mapOf(
                (1 to DayOfWeek.MONDAY.value) to SessionStatus.COMPLETED,
                (1 to DayOfWeek.TUESDAY.value) to SessionStatus.SKIPPED,
            ),
        )
        assertEquals(
            SessionStatus.COMPLETED,
            session(plan, 1, DayOfWeek.MONDAY)!!.status,
        )
        assertEquals(
            SessionStatus.SKIPPED,
            session(plan, 1, DayOfWeek.TUESDAY)!!.status,
        )
        assertEquals(
            SessionStatus.PLANNED,
            session(plan, 1, DayOfWeek.THURSDAY)!!.status,
        )
    }

    @Test
    fun completionStatsCountsOnlyMainSessions() {
        val plan = buildPlan(
            mapOf(
                (1 to DayOfWeek.MONDAY.value) to SessionStatus.COMPLETED,
                (2 to DayOfWeek.MONDAY.value) to SessionStatus.COMPLETED,
                (3 to DayOfWeek.MONDAY.value) to SessionStatus.COMPLETED,
            ),
        )
        val (completed, total) = TrainingPlanFactory.completionStats(plan)
        assertEquals(3, completed)
        assertEquals(32, total)
    }

    @Test
    fun nextSessionFindsFirstPendingMainSession() {
        val plan = buildPlan(
            mapOf(
                (1 to DayOfWeek.MONDAY.value) to SessionStatus.COMPLETED,
                (1 to DayOfWeek.TUESDAY.value) to SessionStatus.COMPLETED,
                (1 to DayOfWeek.THURSDAY.value) to SessionStatus.SKIPPED,
            ),
        )
        val next = TrainingPlanFactory.nextSession(plan, DayOfWeek.FRIDAY)
        assertNotNull(next)
        assertEquals(SessionKind.MAKS_HIZ_KUVVET_B, next!!.kind)
        assertEquals(1, next.weekNumber)
    }

    @Test
    fun nextSessionSkipsRestAndRecoveryDays() {
        val plan = buildPlan(
            mapOf(
                (1 to DayOfWeek.MONDAY.value) to SessionStatus.COMPLETED,
                (1 to DayOfWeek.TUESDAY.value) to SessionStatus.COMPLETED,
                (1 to DayOfWeek.THURSDAY.value) to SessionStatus.COMPLETED,
                (1 to DayOfWeek.SATURDAY.value) to SessionStatus.COMPLETED,
            ),
        )
        val next = TrainingPlanFactory.nextSession(plan, DayOfWeek.SUNDAY)
        assertNotNull(next)
        assertEquals(2, next!!.weekNumber)
        assertEquals(DayOfWeek.MONDAY, next.dayOfWeek)
    }

    @Test
    fun currentWeekIsComputedFromStartDate() {
        val start = now - 21L * 86_400_000L
        assertEquals(4, TrainingPlanFactory.currentWeekNumber(start, 8, now))
        assertEquals(1, TrainingPlanFactory.currentWeekNumber(now, 8, now))
    }

    @Test
    fun daysRemainingIsComputedFromStartDateAndWeeks() {
        val start = now
        assertEquals(56L, TrainingPlanFactory.daysRemaining(start, 8, now))
        assertEquals(49L, TrainingPlanFactory.daysRemaining(start, 8, now + 7L * 86_400_000L))
    }

    @Test
    fun targetsScaleWithDifferentTargetTime() {
        val plan68 = buildPlan()
        val plan70 = TrainingPlanFactory.build(
            DemoProfile.build(now).copy(targetTimeMs = 70_000L),
            emptyMap(),
            now,
        )
        val block68 = session(plan68, 1, DayOfWeek.THURSDAY)!!.exercises.first { it.distanceM == 300 && it.reps == 2 }
        val block70 = session(plan70, 1, DayOfWeek.THURSDAY)!!.exercises.first { it.distanceM == 300 && it.reps == 2 }
        assertTrue(block68.targetMinMs != block70.targetMinMs)
        assertEquals(59_500L, block70.targetMinMs)
        assertEquals(61_500L, block70.targetMaxMs)
    }

    @Test
    fun raceRhythmScalesWithTargetTime() {
        val plan70 = TrainingPlanFactory.build(
            DemoProfile.build(now).copy(targetTimeMs = 70_000L),
            emptyMap(),
            now,
        )
        val saturday = session(plan70, 8, DayOfWeek.SATURDAY)!!
        val rhythm = saturday.exercises.first { it.name == "Hedef ritim" }
        assertTrue(rhythm.cues.orEmpty().contains("17,0"))
        assertFalse(rhythm.cues.orEmpty().contains("16,5"))
    }

    @Test
    fun restDaysHaveNoExercises() {
        val plan = buildPlan()
        val sunday = session(plan, 1, DayOfWeek.SUNDAY)!!
        assertTrue(sunday.exercises.isEmpty())
    }

    @Test
    fun allSessionsWithBlocksStartWithWarmUp() {
        val plan = buildPlan()
        val thursday = session(plan, 2, DayOfWeek.THURSDAY)!!
        assertEquals("Hafif koşu", thursday.exercises.first().name)
        assertTrue(thursday.exercises.count { it.type == com.tracklab400.app.data.model.ExerciseType.WARMUP } == 4)
    }
}
