package com.tracklab400.app.data.exercises

import com.tracklab400.app.data.model.DemoProfile
import com.tracklab400.app.data.model.ExerciseType
import com.tracklab400.app.data.plan.TrainingPlanFactory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CatalogsTest {

    private val now = 1_800_000_000_000L

    @Test
    fun warmUpCatalogIsComplete() {
        assertTrue(WarmUpCatalog.all.isNotEmpty())
        assertEquals(WarmUpCatalog.all.size, WarmUpCatalog.all.map { it.id }.distinct().size)
        WarmUpCatalog.all.forEach { entry ->
            assertTrue(entry.id.startsWith("warmup_"))
            assertTrue(entry.name.isNotBlank())
            assertTrue(entry.durationSeconds > 0)
            assertTrue(entry.purpose.isNotBlank())
            assertTrue(entry.steps.isNotEmpty())
            assertTrue(entry.tips.isNotEmpty())
            assertTrue(entry.commonMistakes.isNotEmpty())
            assertTrue(entry.safety.isNotEmpty())
            assertNotNull(entry.poseStart)
            assertNotNull(entry.poseEnd)
        }
    }

    @Test
    fun cooldownCatalogIsComplete() {
        assertTrue(CoolDownCatalog.all.isNotEmpty())
        assertEquals(CoolDownCatalog.all.size, CoolDownCatalog.all.map { it.id }.distinct().size)
        CoolDownCatalog.all.forEach { entry ->
            assertTrue(entry.id.startsWith("cooldown_"))
            assertTrue(entry.durationSeconds > 0)
            assertTrue(entry.steps.isNotEmpty())
            assertTrue(entry.tips.isNotEmpty())
        }
    }

    @Test
    fun strengthCatalogIsComplete() {
        assertTrue(StrengthCatalog.all.isNotEmpty())
        assertEquals(StrengthCatalog.all.size, StrengthCatalog.all.map { it.id }.distinct().size)
        StrengthCatalog.all.forEach { record ->
            assertTrue(record.id.startsWith("strength_"))
            assertTrue(record.name.isNotBlank())
            assertTrue(record.sets > 0)
            assertTrue(record.reps > 0)
            assertTrue(record.restSeconds >= 0)
            assertTrue(record.rpeTarget in 1..10)
            assertTrue(record.targetMuscles.isNotEmpty())
            assertTrue(record.technique.isNotEmpty())
            assertTrue(record.commonMistakes.isNotEmpty())
            assertTrue(record.safety.isNotEmpty())
            assertTrue(record.noEquipmentAlternative.isNotBlank())
        }
    }

    @Test
    fun findByIdIgnoresMissingEntries() {
        val warmUp = WarmUpCatalog.findById("warmup_hafif_kosu")
        assertNotNull(warmUp)
        assertNull(WarmUpCatalog.findById("missing"))
        assertNull(StrengthCatalog.findById("missing"))
        assertNull(CoolDownCatalog.findById("missing"))
    }

    @Test
    fun everyPlanStrengthNameResolves() {
        val plan = TrainingPlanFactory.build(DemoProfile.build(now), emptyMap(), now)
        val unresolved = plan.weeks
            .flatMap { it.days }
            .flatMap { it.exercises }
            .filter { it.type == ExerciseType.STRENGTH }
            .map { it.name }
            .distinct()
            .filter { StrengthCatalog.byPlanName(it) == null }
        assertTrue("Çözülemeyen kuvvet hareketleri: $unresolved", unresolved.isEmpty())
    }

    @Test
    fun everyPlanWarmUpNameHasBlocks() {
        val plan = TrainingPlanFactory.build(DemoProfile.build(now), emptyMap(), now)
        val unresolved = plan.weeks
            .flatMap { it.days }
            .flatMap { it.exercises }
            .filter { it.type == ExerciseType.WARMUP }
            .map { it.name }
            .distinct()
            .filter { WarmUpCatalog.blocksForPlanName(it).isEmpty() }
        assertTrue("Çözülemeyen ısınma adları: $unresolved", unresolved.isEmpty())
    }

    @Test
    fun expectedPlanNamesResolve() {
        assertNotNull(StrengthCatalog.byPlanName("Squat / Trap-bar deadlift"))
        assertNotNull(StrengthCatalog.byPlanName("Bulgar split squat"))
        assertNotNull(StrengthCatalog.byPlanName("Romanian deadlift"))
        assertNotNull(StrengthCatalog.byPlanName("Hip thrust"))
        assertNotNull(StrengthCatalog.byPlanName("Baldır yükseltme"))
        assertNotNull(StrengthCatalog.byPlanName("Broad jump veya box jump"))
    }
}