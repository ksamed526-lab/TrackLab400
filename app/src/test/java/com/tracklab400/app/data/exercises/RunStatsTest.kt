package com.tracklab400.app.data.exercises

import com.tracklab400.app.data.model.ExerciseType
import com.tracklab400.app.data.model.SessionKind
import com.tracklab400.app.data.model.WorkoutExercise
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class RunStatsTest {

    @Test
    fun emptyReturnsNull() {
        assertNull(RunStats.compute(emptyList()))
    }

    @Test
    fun singleRepMapsAllStats() {
        val result = RunStats.compute(listOf(58_500L))!!
        assertEquals(1, result.reps)
        assertEquals(58_500L, result.avgMs)
        assertEquals(58_500L, result.bestMs)
        assertEquals(58_500L, result.slowestMs)
        assertEquals(0L, result.meanDeviationMs)
        assertEquals(100, result.consistencyPercent)
    }

    @Test
    fun averageRoundsHalfUp() {
        val result = RunStats.compute(listOf(10_010L, 10_005L))!!
        assertEquals(10_008L, result.avgMs)
    }

    @Test
    fun identicalRepsGiveFullConsistency() {
        val result = RunStats.compute(listOf(55_000L, 55_000L, 55_000L))!!
        assertEquals(55_000L, result.bestMs)
        assertEquals(55_000L, result.slowestMs)
        assertEquals(100, result.consistencyPercent)
    }

    @Test
    fun spreadResultsLowerConsistency() {
        val consistent = RunStats.compute(listOf(60_000L, 60_200L, 59_800L))!!
        val scattered = RunStats.compute(listOf(60_000L, 70_000L, 50_000L))!!
        assertTrue(consistent.consistencyPercent > scattered.consistencyPercent)
    }

    @Test
    fun intervalMapsExerciseFields() {
        val exercise = WorkoutExercise(
            type = ExerciseType.RUN,
            name = "100 m tekrar",
            distanceM = 100,
            reps = 8,
            targetMinMs = 19_000L,
            targetMaxMs = 21_000L,
            restMinMs = 90_000L,
            restMaxMs = 150_000L,
            restNote = "yürüyüş",
            intensity = "orta",
            cues = "Diz yüksek, Ayak ucundan, Stride",
        )
        val interval = RunningIntervalFactory.from(exercise, SessionKind.TEMPO)
        assertEquals("100 m tekrar", interval.name)
        assertEquals(8, interval.reps)
        assertEquals(19_000L, interval.targetMinMs)
        assertEquals(21_000L, interval.targetMaxMs)
        assertEquals(90_000L, interval.restMinMs)
        assertEquals(150_000L, interval.restMaxMs)
        assertEquals(listOf("Diz yüksek", "Ayak ucundan", "Stride"), interval.cues)
        assertTrue(interval.purpose.contains("tempo"))
        assertTrue(interval.notes.orEmpty().contains("yürüyüş"))
    }

    @Test
    fun testAndFlyingOverridePurpose() {
        val flying = RunningIntervalFactory.from(
            WorkoutExercise(
                type = ExerciseType.RUN,
                name = "Uçan",
                distanceM = 60,
                reps = 3,
                isFlying = true,
            ),
            SessionKind.MAKS_HIZ_KUVVET_B,
        )
        assertTrue(flying.purpose.lowercase().contains("flying"))

        val test = RunningIntervalFactory.from(
            WorkoutExercise(
                type = ExerciseType.RUN,
                name = "Test",
                distanceM = 400,
                reps = 1,
                isTest = true,
            ),
            SessionKind.TEST_YARIS,
        )
        assertTrue(test.purpose.lowercase().contains("test"))
    }
}