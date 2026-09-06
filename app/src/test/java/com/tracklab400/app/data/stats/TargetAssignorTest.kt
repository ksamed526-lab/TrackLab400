package com.tracklab400.app.data.stats

import com.tracklab400.app.data.model.Equipment
import com.tracklab400.app.data.model.GymStatus
import com.tracklab400.app.data.model.PainReport
import com.tracklab400.app.data.model.Profile
import com.tracklab400.app.data.model.RepRecord
import com.tracklab400.app.data.model.SessionRecord
import com.tracklab400.app.data.model.SessionStatus
import com.tracklab400.app.data.model.StopwatchType
import com.tracklab400.app.data.model.TrackAccess
import com.tracklab400.app.data.model.TrainingExperience
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class TargetAssignorTest {

    private val now = 1_700_000_000_000L
    private val DAY = 86_400_000L

    private fun profile(
        prepWeeks: Int = 8,
        current400mMs: Long = 76_000L,
        targetTimeMs: Long = 68_000L,
        current100mMs: Long? = 15_000L,
        current200mMs: Long? = 39_000L,
        current300mMs: Long? = 58_000L,
        startDateMillis: Long = now - 7 * DAY,
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
        prepWeeks = prepWeeks,
        trainingDaysPerWeek = 4,
        startDateMillis = startDateMillis,
        trackAccess = TrackAccess.PIST_VAR,
        gymStatus = GymStatus.SALON_VAR,
        equipment = setOf(Equipment.BARBELL, Equipment.DUMBBELL),
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
        reps: List<Pair<Int, Long>> = emptyList(),
        sleepHours: Double? = null,
        pain: Boolean = false,
    ) = SessionRecord(
        id = id,
        weekNumber = week,
        dayOfWeek = 4,
        sessionKind = "TEMPO",
        completedAt = now - daysAgo * DAY,
        status = status,
        sleepHours = sleepHours,
        painReported = pain,
        reps = reps.mapIndexed { index, (distance, actual) ->
            RepRecord(
                id = id * 10 + index,
                sessionRecordId = id,
                repIndex = index + 1,
                distanceM = distance,
                actualMs = actual,
            )
        },
    )

    private fun pains(record: SessionRecord): SessionRecord =
        record.copy(painReports = listOf(PainReport(sessionRecordId = record.id, location = "diz", severity = 5, reportedAt = record.completedAt)))

    @Test
    fun noProfile_returnsNull() {
        assertNull(TargetAssessor.assess(profile = null, records = emptyList(), now = now))
    }

    @Test
    fun targetGap_isDifferenceBetweenCurrentAndTarget() {
        val assessment = TargetAssessor.assess(profile(), emptyList(), now)
        assertNotNull(assessment)
        assertEquals(8_000L, assessment!!.gapMs)
    }

    @Test
    fun targetPercent_isRelativeImprovement() {
        val assessment = TargetAssessor.assess(profile(), emptyList(), now)
        assertNotNull(assessment)
        assertEquals(8_000f / 76_000f, assessment!!.targetPercent, 0.0001f)
    }

    @Test
    fun reachedTarget_marksReaached() {
        val assessment = TargetAssessor.assess(
            profile(current400mMs = 67_000L, targetTimeMs = 68_000L),
            emptyList(),
            now,
        )
        assertNotNull(assessment)
        assertEquals(TargetOutlook.REACHED, assessment!!.outlook)
        assertEquals(0L, assessment.gapMs)
    }

    @Test
    fun aggressiveTarget_isDetected() {
        // %10,5 iyileşme gerekli, 8 hafta (7 kalan) → haftada %1,50 > 1,8×0,5?? Değil; bu yüzden daha
        // büyük bir fark kullan: 76→64 sn (%15,8) → haftada %2,26 > 1,5×1,2 = 1,8 → agresif
        val assessment = TargetAssessor.assess(
            profile(current400mMs = 76_000L, targetTimeMs = 64_000L),
            emptyList(),
            now,
        )
        assertNotNull(assessment)
        assertEquals(TargetOutlook.AGGRESSIVE, assessment!!.outlook)
        assertTrue(assessment.recommendations.any { it.kind == RecommendationKind.FOCUS_FORM })
        assertTrue(assessment.recommendations.any { it.kind == RecommendationKind.INTERMEDIATE_TARGET })
        assertNotNull(assessment.weakestDistanceM)
    }

    @Test
    fun easyTarget_isDetected() {
        val assessment = TargetAssessor.assess(
            profile(current400mMs = 69_000L, targetTimeMs = 68_000L, prepWeeks = 12),
            emptyList(),
            now,
        )
        assertNotNull(assessment)
        assertEquals(TargetOutlook.EASY, assessment!!.outlook)
    }

    @Test
    fun realisticTarget_isDetected() {
        // 76→71 sn: %6,6 iyileşme, 7 kalan hafta → haftada %0,94 → 0,75 (kolay) < 0,94 < 1,8 (agresif)
        val assessment = TargetAssessor.assess(
            profile(current400mMs = 76_000L, targetTimeMs = 71_000L),
            emptyList(),
            now,
        )
        assertNotNull(assessment)
        assertEquals(TargetOutlook.REALISTIC, assessment!!.outlook)
    }

    @Test
    fun weakestDistance_considersAllDistances() {
        val weak200 = profile(
            current100mMs = 17_000L,
            current200mMs = 47_000L, // 400*0.51=34_680 → oran 1.35 (en zayıf)
            current300mMs = 58_000L,
            current400mMs = 76_000L,
            targetTimeMs = 68_000L,
        )
        val assessment = TargetAssessor.assess(weak200, emptyList(), now)
        assertNotNull(assessment)
        assertEquals(200, assessment!!.weakestDistanceM)
    }

    @Test
    fun lowSleep_recommendsIntensityReduction() {
        val records = listOf(
            session(1, 1, 0, SessionStatus.COMPLETED, sleepHours = 5.5,
                reps = listOf(200 to 39_000L)),
        )
        val assessment = TargetAssessor.assess(profile(), records, now)
        assertNotNull(assessment)
        assertTrue(assessment!!.recommendations.any { it.kind == RecommendationKind.INTENSITY_REDUCTION })
    }

    @Test
    fun adequateSleep_doesNotReduceIntensity() {
        val records = listOf(
            session(1, 1, 0, SessionStatus.COMPLETED, sleepHours = 8.5,
                reps = listOf(200 to 39_000L)),
        )
        val assessment = TargetAssessor.assess(profile(), records, now)
        assertNotNull(assessment)
        assertTrue(assessment!!.recommendations.none { it.kind == RecommendationKind.INTENSITY_REDUCTION })
    }

    @Test
    fun pain_recommendsStoppingAndConsult() {
        val records = listOf(
            pains(session(1, 1, 1, SessionStatus.COMPLETED, reps = listOf(200 to 39_000L))),
        )
        val assessment = TargetAssessor.assess(profile(), records, now)
        assertNotNull(assessment)
        assertTrue(assessment!!.recommendations.any { it.kind == RecommendationKind.STOP_HARD_PAIN })
        assertTrue(assessment.consultProfessional)
        assertTrue(assessment.recommendations.any { it.kind == RecommendationKind.CONSULT_PROFESSIONAL })
    }

    @Test
    fun slowReps_recommendVolumeReduction() {
        val records = listOf(
            session(1, 1, 3, SessionStatus.COMPLETED, reps = listOf(200 to 39_000L)),
            session(2, 2, 1, SessionStatus.COMPLETED, reps = listOf(200 to 42_000L)), // 7,7% yavaş
        )
        val assessment = TargetAssessor.assess(profile(), records, now)
        assertNotNull(assessment)
        assertTrue(assessment!!.recommendations.any { it.kind == RecommendationKind.VOLUME_REDUCTION })
    }

    @Test
    fun consistentPace_doesNotReduceVolume() {
        val records = listOf(
            session(1, 1, 3, SessionStatus.COMPLETED, reps = listOf(200 to 39_000L)),
            session(2, 2, 1, SessionStatus.COMPLETED, reps = listOf(200 to 39_500L)), // %1,3 yavaş
        )
        val assessment = TargetAssessor.assess(profile(), records, now)
        assertNotNull(assessment)
        assertTrue(assessment!!.recommendations.none { it.kind == RecommendationKind.VOLUME_REDUCTION })
    }

    @Test
    fun missedWorkout_recommendsNoCompensation() {
        val records = listOf(
            session(1, 1, 1, SessionStatus.SKIPPED),
        )
        val assessment = TargetAssessor.assess(profile(), records, now)
        assertNotNull(assessment)
        assertTrue(assessment!!.recommendations.any { it.kind == RecommendationKind.NO_COMPENSATION })
    }

    @Test
    fun fastProgress_givesSmallIncrement() {
        val records = listOf(
            session(1, 1, 10, SessionStatus.COMPLETED, reps = listOf(400 to 72_500L)),
            session(2, 2, 4, SessionStatus.COMPLETED, reps = listOf(400 to 71_000L)),
            session(3, 3, 1, SessionStatus.COMPLETED, reps = listOf(400 to 68_500L)), // gap 8,0 sn, 6,0 sn kapandı
        )
        val assessment = TargetAssessor.assess(profile(), records, now)
        assertNotNull(assessment)
        assertTrue(assessment!!.recommendations.any { it.kind == RecommendationKind.SMALL_INCREMENT })
    }

    @Test
    fun checkpoint_atHalfway_givesFeedback() {
        val records = listOf(
            session(1, 4, 1, SessionStatus.COMPLETED, reps = listOf(400 to 70_000L)),
        )
        val assessment = TargetAssessor.assess(profile(), records, now)
        assertNotNull(assessment)
        assertTrue(
            assessment!!.recommendations.any { it.kind == RecommendationKind.CHECKPOINT_ON_TRACK } ||
                assessment.recommendations.any { it.kind == RecommendationKind.CHECKPOINT_BEHIND },
        )
    }

    @Test
    fun checkpointAtHalfway_whenBehindFlagsBehind() {
        // 4. haftada beklenen ~72 s; ama 4. haftada 76 s → geride
        val records = listOf(
            session(1, 4, 1, SessionStatus.COMPLETED, reps = listOf(400 to 76_000L)),
        )
        val assessment = TargetAssessor.assess(profile(), records, now)
        assertNotNull(assessment)
        assertTrue(assessment!!.recommendations.any { it.kind == RecommendationKind.CHECKPOINT_BEHIND })
    }

    @Test
    fun intermediateTarget_isSlightlyBelowCurrent() {
        val assessment = TargetAssessor.assess(profile(), emptyList(), now)
        assertNotNull(assessment)
        val expected = (76_000L * (1f - TargetAssessor.SAFE_WEEKLY_IMPROVEMENT)).toLong()
        assertEquals(expected, assessment!!.intermediateTargetMs)
        assertTrue(assessment.intermediateTargetMs < 76_000L)
        assertTrue(assessment.intermediateTargetMs > 68_000L)
    }

    @Test
    fun alwaysDisclaimsGuarantee() {
        val assessment = TargetAssessor.assess(profile(), emptyList(), now)
        assertNotNull(assessment)
        assertTrue(
            assessment!!.recommendations.any {
                it.kind == RecommendationKind.CONSULT_PROFESSIONAL && it.message.contains("garanti")
            },
        )
    }
}