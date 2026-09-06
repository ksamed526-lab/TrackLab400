package com.tracklab400.app.data.plan

import kotlin.math.roundToLong

data class PaceFactor(val min: Double, val max: Double)

data class RaceSplit(val distanceM: Int, val minMs: Long, val maxMs: Long)

/**
 * Hedef 400 m süresine göre tempo hedeflerini ve yarış ritmini ölçekler.
 * PDF'deki tüm hedefler 68 sn için yazılmıştır; bu sınıf bunları hedef süreye
 * oranlayarak her kullanıcı için yeniden hesaplar.
 */
object PaceCalculator {

    private const val RACE_100_MIN = 0.243
    private const val RACE_100_MAX = 0.250
    private const val RACE_200_MIN = 0.485
    private const val RACE_200_MAX = 0.500
    private const val RACE_300_MIN = 0.743
    private const val RACE_300_MAX = 0.757

    fun scale(targetMs: Long, factor: PaceFactor): LongRange {
        val min = roundTo500(targetMs * factor.min)
        val max = roundTo500(targetMs * factor.max)
        return min..max
    }

    fun raceSplits(targetMs: Long): List<RaceSplit> = listOf(
        RaceSplit(
            100,
            roundTo100(targetMs * RACE_100_MIN),
            roundTo100(targetMs * RACE_100_MAX),
        ),
        RaceSplit(
            200,
            roundTo100(targetMs * RACE_200_MIN),
            roundTo100(targetMs * RACE_200_MAX),
        ),
        RaceSplit(
            300,
            roundTo100(targetMs * RACE_300_MIN),
            roundTo100(targetMs * RACE_300_MAX),
        ),
        RaceSplit(400, targetMs, targetMs),
    )

    fun roundTo500(ms: Double): Long = (ms / 500.0).roundToLong() * 500

    fun roundTo100(ms: Double): Long = (ms / 100.0).roundToLong() * 100
}
