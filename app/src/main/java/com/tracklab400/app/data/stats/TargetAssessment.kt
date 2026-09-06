package com.tracklab400.app.data.stats

import com.tracklab400.app.data.model.Profile
import com.tracklab400.app.data.model.SessionRecord
import com.tracklab400.app.data.model.SessionStatus
import com.tracklab400.app.data.model.TimeUtils
import kotlin.math.max
import kotlin.math.roundToInt

enum class TargetOutlook {
    AGGRESSIVE,
    REALISTIC,
    EASY,
    REACHED,
}

enum class RecommendationKind {
    FOCUS_FORM,
    INTERMEDIATE_TARGET,
    SMALL_INCREMENT,
    VOLUME_REDUCTION,
    INTENSITY_REDUCTION,
    STOP_HARD_PAIN,
    NO_COMPENSATION,
    CHECKPOINT_ON_TRACK,
    CHECKPOINT_BEHIND,
    CONSULT_PROFESSIONAL,
}

data class Recommendation(
    val kind: RecommendationKind,
    val message: String,
)

/**
 * Hedef değerlendirmesi: mevcut ve hedef derece arasındaki fark, agresif/gerçekçi/kolay
 * sınıflandırması, hazırlık süresi, 100-200-300-400 m birlikte değerlendirme,
 * 4. hafta kontrol noktası ve güvenli, cezalandırıcı olmayan uyarlama önerileri.
 *
 * Güvenlik ilkeleri:
 *  - Kesin başarı garantisi verilmez.
 *  - Tıbbi teşhis konmaz; ağrı durumunda antrenöre/sağlık uzmanına yönlendirilir.
 */
data class TargetAssessment(
    val outlook: TargetOutlook,
    val gapMs: Long,
    val targetPercent: Float,
    val ratePerWeekPercent: Float,
    val remainingWeeks: Float,
    val currentWeek: Int,
    val checkpointWeek: Int,
    val weakestDistanceM: Int?,
    val intermediateTargetMs: Long,
    val best400RangeMs: Long?,
    val recommendations: List<Recommendation>,
    val consultProfessional: Boolean,
)

object TargetAssessor {

    /** Haftalık güvenli iyileşme üst sınırı (hedef sürenin oranı). */
    const val SAFE_WEEKLY_IMPROVEMENT = 0.015f

    /** Gerekli hız bu oranın altındaysa hedef "kolay". */
    const val EASY_RATIO = 0.5f

    /** Gerekli hız bu oranın üzerindeyse hedef "agresif". */
    const val AGGRESSIVE_RATIO = 1.2f

    /** Bu değerin altında ortalama uyku → yoğunluk düşürme önerisi. */
    const val SLEEP_LOW_HOURS = 6.5

    /** Son tekrarların bu kattan yavaşlaması → hacim düşürme. */
    const val DECLINE_TRIGGER = 1.04f

    /** Aralık içinde hedef farkının bu oranı kapatılmışsa "hızlı ilerleme". */
    const val FAST_PROGRESS_FRACTION = 0.6f

    /** Ağrı için bu gün sayısı içindeki kayıtlar dikkate alınır. */
    const val PAIN_LOOKBACK_DAYS = 14L

    /** Son antrenman penceresi (uyku, kaçırılan antrenman). */
    const val RECENT_DAYS = 7L

    /** En iyi 400 m değerlendirme penceresi. */
    const val BEST_LOOKBACK_DAYS = 28L

    private const val DAY_MS = 86_400_000L

    /**
     * 400 m hedefine göre bölüm katsayıları (plan modeli varsayımı):
     * 100 m = %25, 200 m = %51, 300 m = %76. Başlangıç ivmesi ve yorgunluk
     * nedeniyle 100 m oranı düşük, son bölümler yüksek tutulur.
     */
    private val SPLIT_COEFF = mapOf(
        100 to 0.25f,
        200 to 0.51f,
        300 to 0.76f,
        400 to 1.0f,
    )

    fun assess(
        profile: Profile?,
        records: List<SessionRecord>,
        now: Long = System.currentTimeMillis(),
    ): TargetAssessment? {
        if (profile == null) return null
        return compute(profile, records, now)
    }

    private fun compute(profile: Profile, records: List<SessionRecord>, now: Long): TargetAssessment {
        val current = profile.current400mMs
        val target = profile.targetTimeMs
        val gap = current - target

        val targetPercent = if (current > 0 && gap > 0) gap.toFloat() / current else 0f
        val elapsedWeeks = ((now - profile.startDateMillis).coerceAtLeast(0L)).toFloat() / (7 * DAY_MS)
        val remainingWeeks = max(0.5f, profile.prepWeeks - elapsedWeeks)
        val ratePerWeek = targetPercent / remainingWeeks

        val outlook = when {
            gap <= 0 -> TargetOutlook.REACHED
            ratePerWeek <= SAFE_WEEKLY_IMPROVEMENT * EASY_RATIO -> TargetOutlook.EASY
            ratePerWeek > SAFE_WEEKLY_IMPROVEMENT * AGGRESSIVE_RATIO -> TargetOutlook.AGGRESSIVE
            else -> TargetOutlook.REALISTIC
        }

        val dataWeek = records.maxOfOrNull { it.weekNumber } ?: 0
        val currentWeek = max(1, max(dataWeek, elapsedWeeks.toInt() + 1))
        val checkpointWeek = max(1, profile.prepWeeks / 2)

        val best400 = bestWithin(records, 400, BEST_LOOKBACK_DAYS, now)
        val weakest = weakestDistance(profile, target)

        val intermediateTarget = if (gap > 0) {
            (current * (1f - SAFE_WEEKLY_IMPROVEMENT)).toLong()
        } else {
            target
        }

        val recommendations = buildRecommendations(
            outlook = outlook,
            targetMs = target,
            weakestM = weakest,
            intermediateTargetMs = intermediateTarget,
            records = records,
            now = now,
            current = current,
            target = target,
            gap = gap,
            best400 = best400,
            currentWeek = currentWeek,
            checkpointWeek = checkpointWeek,
            prepWeeks = profile.prepWeeks,
        )

        val consultProfessional = recommendations.any { it.kind == RecommendationKind.STOP_HARD_PAIN }

        return TargetAssessment(
            outlook = outlook,
            gapMs = gap.coerceAtLeast(0L),
            targetPercent = targetPercent,
            ratePerWeekPercent = ratePerWeek,
            remainingWeeks = remainingWeeks,
            currentWeek = currentWeek,
            checkpointWeek = checkpointWeek,
            weakestDistanceM = weakest,
            intermediateTargetMs = intermediateTarget,
            best400RangeMs = best400,
            recommendations = recommendations,
            consultProfessional = consultProfessional,
        )
    }

    private fun buildRecommendations(
        outlook: TargetOutlook,
        targetMs: Long,
        weakestM: Int?,
        intermediateTargetMs: Long,
        records: List<SessionRecord>,
        now: Long,
        current: Long,
        target: Long,
        gap: Long,
        best400: Long?,
        currentWeek: Int,
        checkpointWeek: Int,
        prepWeeks: Int,
    ): List<Recommendation> {
        val out = mutableListOf<Recommendation>()
        val targetSec = (targetMs / 1000).toString()
        val focusM = weakestM ?: 400

        when (outlook) {
            TargetOutlook.AGGRESSIVE -> {
                out += Recommendation(
                    RecommendationKind.FOCUS_FORM,
                    "$targetSec saniye hedefi agresif. " +
                        "Bu hafta önceliğin daha hızlı başlamak değil, $focusM metre tekrarlarında formunu korumak.",
                )
                out += Recommendation(
                    RecommendationKind.INTERMEDIATE_TARGET,
                    "Ara hedef: önce ${TimeUtils.formatSeconds(intermediateTargetMs)} saniyeye odaklan; " +
                        "nihai hedefe bir sonraki blokta ilerle.",
                )
            }
            TargetOutlook.REALISTIC -> {
                out += Recommendation(
                    RecommendationKind.FOCUS_FORM,
                    "$targetSec saniye hedefi gerçekçi. Güvenli ilerleme için plan ritmini koru; " +
                        "$focusM metre tekrarlarında tekniğe odaklan.",
                )
            }
            TargetOutlook.EASY -> {
                out += Recommendation(
                    RecommendationKind.FOCUS_FORM,
                    "$targetSec saniye hedefi kolay. Hacmi artırmak yerine tekniği geliştir; " +
                        "güncellenmiş hedefi blok sonunda değerlendir.",
                )
            }
            TargetOutlook.REACHED -> {
                out += Recommendation(
                    RecommendationKind.FOCUS_FORM,
                    "Hedef süreni şimdiden yakaladın. Yeni bir hedef için antrenörle görüş; " +
                        "kalan haftaları koruyucu çalışmayla geçir.",
                )
            }
        }

        if (best400 != null && current > target) {
            val closed = current - best400
            if (closed >= gap * FAST_PROGRESS_FRACTION) {
                out += Recommendation(
                    RecommendationKind.SMALL_INCREMENT,
                    "İlerleme hızlı gidiyor; tek antrenmanda büyük sıçrama yapma. " +
                        "Küçük ve güvenli artışlarla devam et (tekrar hedefini %1'den fazla hızlandırma).",
                )
            }
        }

        if (hasRecentDecline(records, now)) {
            out += Recommendation(
                RecommendationKind.VOLUME_REDUCTION,
                "Son tekrarlar hedef tempodan yavaş; bu hafta toplam hacmi ~%20 azalt " +
                    "ve formu koru, tempo artırımı yapma.",
            )
        }

        if (hasLowSleep(records, now)) {
            out += Recommendation(
                RecommendationKind.INTENSITY_REDUCTION,
                "Uyku düşük; yoğunluğu azalt — sert aralıklar yerine kolay koşu ve form çalışması yap.",
            )
        }

        if (hasPain(records, now)) {
            out += Recommendation(
                RecommendationKind.STOP_HARD_PAIN,
                "Ağrı bildirildi; sert antrenmanı durdur ve dinlenme/hareketlilikle geçir. " +
                    "Şiddetli veya geçmeyen ağrıda fizyoterapist veya doktora başvur.",
            )
        }

        if (hasMissedRecently(records, now)) {
            out += Recommendation(
                RecommendationKind.NO_COMPENSATION,
                "Kaçırdığın antrenmanı cezalandırıcı biçimde telafi etmeye çalışma; " +
                    "plana kaldığın normal hacimle devam et.",
            )
        }

        if (currentWeek >= checkpointWeek) {
            val fraction = checkpointWeek.toFloat() / prepWeeks
            val expected = (current - gap * fraction.toDouble()).toLong()
            val expectedLabel = TimeUtils.formatMs(expected)
            val bestLabel = best400?.let { TimeUtils.formatMs(it) } ?: "—"
            if (best400 != null && best400 <= expected) {
                out += Recommendation(
                    RecommendationKind.CHECKPOINT_ON_TRACK,
                    "Kontrol noktası (Hafta $checkpointWeek): bu aşamada $expectedLabel civarında olmalısın. " +
                        "Şu an $bestLabel — iyi gidiyorsun; küçük ve güvenli artışlarla devam.",
                )
            } else if (best400 != null) {
                out += Recommendation(
                    RecommendationKind.CHECKPOINT_BEHIND,
                    "Kontrol noktası (Hafta $checkpointWeek): bu aşamada $expectedLabel civarında olmalıydın; " +
                        "şu an $bestLabel. Geridesin; bu hafta zorlama artırımı yapma, eksikleri telafi etme.",
                )
            }
        }

        out += Recommendation(
            RecommendationKind.CONSULT_PROFESSIONAL,
            "Kesin başarı garantisi verilemez. Keskin veya sürekli ağrıda, yaralanma şüphesinde " +
                "antrenöre veya sağlık uzmanına başvur.",
        )

        return out
    }

    /** 100-200-300-400 m arasında kendi bölüm oranının en çok gerisinde olan mesafe. */
    private fun weakestDistance(profile: Profile, target: Long): Int? {
        var weakest: Int? = null
        var worstRatio = 1.0f
        for ((distance, coeff) in SPLIT_COEFF.entries) {
            val currentMs = when (distance) {
                100 -> profile.current100mMs
                200 -> profile.current200mMs
                300 -> profile.current300mMs
                else -> profile.current400mMs
            }
            if (currentMs == null) continue
            val implied = target * coeff
            if (implied <= 0) continue
            val ratio = currentMs.toFloat() / implied
            if (ratio >= worstRatio) {
                worstRatio = ratio
                weakest = distance
            }
        }
        return weakest ?: 400
    }

    private fun bestWithin(
        records: List<SessionRecord>,
        distance: Int,
        days: Long,
        now: Long,
    ): Long? {
        val window = now - days * DAY_MS
        return records
            .filter { it.completedAt >= window }
            .flatMap { it.reps }
            .filter { it.distanceM == distance }
            .minOfOrNull { it.actualMs }
    }

    /** Son pencerede en iyi iki seansın "400 m eşdeğer tempo"su karşılaştırılır. */
    private fun hasRecentDecline(records: List<SessionRecord>, now: Long): Boolean {
        val window = now - BEST_LOOKBACK_DAYS * DAY_MS
        val sessionPaces = records
            .filter { it.completedAt >= window && it.status != SessionStatus.SKIPPED }
            .mapNotNull { record ->
                val dist = record.reps.firstOrNull { it.distanceM in SPLIT_COEFF.keys }
                    ?: return@mapNotNull null
                val coeff = SPLIT_COEFF[dist.distanceM] ?: return@mapNotNull null
                val best = record.reps.filter { it.distanceM == dist.distanceM }.minOf { it.actualMs }
                record.completedAt to best / coeff
            }
            .sortedBy { it.first }
        if (sessionPaces.size < 2) return false
        val (_, latest) = sessionPaces.last()
        val (_, previous) = sessionPaces[sessionPaces.size - 2]
        return latest > previous * DECLINE_TRIGGER
    }

    private fun hasLowSleep(records: List<SessionRecord>, now: Long): Boolean {
        val window = now - RECENT_DAYS * DAY_MS
        val sleeps = records
            .filter { it.completedAt >= window }
            .mapNotNull { it.sleepHours }
        return sleeps.isNotEmpty() && sleeps.average() < SLEEP_LOW_HOURS
    }

    private fun hasPain(records: List<SessionRecord>, now: Long): Boolean {
        val window = now - PAIN_LOOKBACK_DAYS * DAY_MS
        return records.any {
            it.completedAt >= window && (it.painReported || it.painReports.isNotEmpty())
        }
    }

    private fun hasMissedRecently(records: List<SessionRecord>, now: Long): Boolean {
        val window = now - RECENT_DAYS * DAY_MS
        return records.any { it.completedAt >= window && it.status == SessionStatus.SKIPPED }
    }
}