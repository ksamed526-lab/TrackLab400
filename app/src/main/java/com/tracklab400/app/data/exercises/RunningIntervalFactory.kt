package com.tracklab400.app.data.exercises

import com.tracklab400.app.data.model.SessionKind
import com.tracklab400.app.data.model.WorkoutExercise

/** Plan koşu bloğunu detay sayfası için [RunningInterval]'e dönüştürür. */
object RunningIntervalFactory {

    fun from(exercise: WorkoutExercise, kind: SessionKind? = null): RunningInterval {
        return RunningInterval(
            name = exercise.name,
            distanceM = exercise.distanceM ?: 0,
            reps = exercise.reps ?: 1,
            targetMinMs = exercise.targetMinMs,
            targetMaxMs = exercise.targetMaxMs,
            restMinMs = exercise.restMinMs,
            restMaxMs = exercise.restMaxMs,
            purpose = purposeFor(kind, exercise),
            cues = exercise.cues?.split(",", "·", ";").orEmpty()
                .map { it.trim() }
                .filter { it.isNotEmpty() },
            notes = buildNotes(exercise),
        )
    }

    fun pagesForSession(kind: SessionKind?, name: String): List<String> = listOf(
        "Gövde dik, bakış ileri; koşu bitince tempoyu kademeli düşür.",
        "Karşı kol-bacak koordinasyonunu koru, adım frekansını artır.",
    )

    private fun purposeFor(kind: SessionKind?, exercise: WorkoutExercise): String {
        val base = kind?.let { purposeByKind(it) }
        return when {
            exercise.isTest -> "Test koşusu: hedef zaman bandını koruyarak eksiksiz tamamla."
            exercise.isFlying -> "Uçan (flying) başlangıç — hızlanma mesafesinden sonra saat gibi koş."
            exercise.reps == 1 -> "Tek tekrar: günün ana hedef zamanını kaliteli formla koş."
            base != null -> base
            else -> "Günün koşu bloğu: hedef zaman bandını koruyarak tüm tekrarları tamamla."
        }
    }

    private fun purposeByKind(kind: SessionKind): String = when (kind) {
        SessionKind.HIZLANMA_KUVVET_A ->
            "Hızlanma + kuvvet odağı: her tekrarda patlayıcı çıkış, kısa mesafede tam hız kalitesi."
        SessionKind.TEMPO ->
            "Tempo çalışması: bandın alt sınırında sabit ritimle koş, tempo gücünü geliştir."
        SessionKind.OZEL_DAYANIKLILIK ->
            "Hız dayanıklılığı: yorgunluğa rağmen teknik bozulmadan, bandın içinde koş."
        SessionKind.MAKS_HIZ_KUVVET_B ->
            "Maksimal hız + kuvvet: dinlenmiş şekilde yüksek kalite, tekrarlar arası tam toparlanma."
        SessionKind.TEST_YARIS ->
            "Test/yarış koşusu: 400 m ritmini bölerek koş, zaman bandını koru."
        SessionKind.DINLENME, SessionKind.HAFIF_TOPARLANMA ->
            "Toparlanma amaçlı kolay koşu — tempo yükseltme."
    }

    private fun buildNotes(exercise: WorkoutExercise): String? {
        val parts = mutableListOf<String>()
        exercise.restNote?.let { parts.add("Dinlenme: $it") }
        exercise.intensity?.let { parts.add("Şiddet: $it") }
        if (exercise.perLeg) parts.add("Her bacak için ayrı değerlendir.")
        return parts.joinToString("\n").ifBlank { null }
    }
}

/** Tekrar sürelerinden tekrar istatistikleri üretir (saf Kotlin, test edilebilir). */
object RunStats {

    data class Result(
        val reps: Int,
        val avgMs: Long,
        val bestMs: Long,
        val slowestMs: Long,
        val meanDeviationMs: Long,
    ) {
        val consistencyPercent: Int
            get() {
                if (avgMs == 0L) return 100
                val deviation = meanDeviationMs.toDouble() / avgMs
                return (100.0 - (deviation * 100.0)).roundToInt().coerceIn(0, 100)
            }
    }

    fun compute(timesMs: List<Long>): Result? {
        if (timesMs.isEmpty()) return null
        val avg = timesMs.average().roundToLong()
        val best = timesMs.minOrNull() ?: 0L
        val slowest = timesMs.maxOrNull() ?: 0L
        val deviation = timesMs.map {
            kotlin.math.abs(it - avg)
        }.average().roundToLong()
        return Result(
            reps = timesMs.size,
            avgMs = avg,
            bestMs = best,
            slowestMs = slowest,
            meanDeviationMs = deviation,
        )
    }

    private fun Double.roundToLong(): Long = ktRound(this)

    private fun Double.roundToInt(): Int = ktRound(this).toInt()

    private fun ktRound(value: Double): Long =
        java.lang.Math.round(value)
}