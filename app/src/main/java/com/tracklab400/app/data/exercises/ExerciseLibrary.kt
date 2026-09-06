package com.tracklab400.app.data.exercises

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/** Tek bir kuvvet/koşu hareketinin kütüphane kaydı. */
@Serializable
data class ExerciseEntry(
    val name: String,
    val force: String = "",
    val level: String = "",
    val mechanic: String = "",
    val equipment: String = "",
    val primaryMuscles: String = "",
    val secondaryMuscles: String = "",
    val instructions: String = "",
    val category: String = "",
    val image: String = "",
    val key: String = "",
)

/**
 * Plan dosyasındaki (Türkçe) hareket adlarını kütüphane anahtarlarına eşleyen kural seti.
 * KUVVET_A / KUVVET_B / TAMAMLAYICILAR bloğunda kullanılan ifadelerin tamamı karşılanır.
 */
object PlanExerciseAliases {

    val BY_EXACT_NAME: Map<String, List<String>> = mapOf(
        "Squat / Trap-bar deadlift" to listOf("barbell-squat", "trap-bar-deadlift"),
        "Bulgar split squat" to listOf("split-squat-with-dumbbells", "barbell-lunge"),
        "Romanian deadlift" to listOf("romanian-deadlift"),
        "Hip thrust" to listOf("barbell-hip-thrust"),
        "Front squat / Step-up" to listOf("front-squat-clean-grip", "barbell-step-ups"),
        "Nordic hamstring" to listOf("natural-glute-ham-raise"),
        "Baldır yükseltme" to listOf("standing-barbell-calf-raise"),
        "Broad jump veya box jump" to listOf("box-jump-multiple-response", "standing-long-jump"),
        "Pogo / ayak bileği sıçraması" to listOf("fast-skipping", "knee-tuck-jump"),
        "Kısa plank / dead bug" to listOf("plank", "dead-bug"),
    )

    /** Tanımsız plan ifadeleri için anahtar kelime tabanlı geri düşme. */
    private val KEYWORD_MAP: List<Pair<Regex, String>> = listOf(
        Regex("""(?i)\bdeadlift\b""") to "barbell-deadlift",
        Regex("""(?i)\bsquat\b""") to "barbell-squat",
        Regex("""(?i)\bsplit squat\b""") to "split-squat-with-dumbbells",
        Regex("""(?i)\blunge\b""") to "barbell-lunge",
        Regex("""(?i)\bstep ?up\b""") to "barbell-step-ups",
        Regex("""(?i)\bhip thrust\b""") to "barbell-hip-thrust",
        Regex("""(?i)\bplank\b""") to "plank",
        Regex("""(?i)\bdead bug\b""") to "dead-bug",
        Regex("""(?i)\bcal[fv]\b""") to "standing-barbell-calf-raise",
        Regex("""(?i)\b(?:box|broad) ?jump\b""") to "box-jump-multiple-response",
        Regex("""(?i)\bpogo\b""") to "fast-skipping",
        Regex("""(?i)\bnordic\b""") to "natural-glute-ham-raise",
        Regex("""(?i)\bpush ?up\b""") to "pushups",
        Regex("""(?i)\bchin ?up\b""") to "chin-up",
        Regex("""(?i)\bpull ?up\b""") to "wide-grip-rear-pull-up",
    )

    /**
     * Plan ifadesi için kütüphane anahtarlarını döndürür. Bileşik adlar (örn. "Front squat / Step-up")
     * "/" ile bölünür; önce tam ad eşleşmesi, sonra anahtar kelime eşleşmesi denenir.
     */
    fun keysFor(planName: String): List<String> {
        BY_EXACT_NAME[planName.trim()]?.let { return it }
        val parts = planName.split("/").map { it.trim() }.filter { it.isNotBlank() }
        val result = linkedSetOf<String>()
        parts.forEach { part ->
            BY_EXACT_NAME[part]?.let { result += it; return@forEach }
            PROPERTY_PART_OVERRIDES[part]?.let { result += it; return@forEach }
            KEYWORD_MAP.firstOrNull { it.first.containsMatchIn(part) }?.let {
                result += it.second
                return@forEach
            }
        }
        return result.toList()
    }

    /** Kalan özel ifadeler için kural dışı eşlemeler. */
    private val PROPERTY_PART_OVERRIDES: Map<String, List<String>> = mapOf(
        "Trap-bar deadlift" to listOf("trap-bar-deadlift"),
        "Front squat" to listOf("front-squat-clean-grip"),
        "Step-up" to listOf("barbell-step-ups"),
        "Box jump" to listOf("box-jump-multiple-response"),
        "Broad jump" to listOf("standing-long-jump"),
        "Ayak bileği sıçraması" to listOf("knee-tuck-jump"),
        "Pogo" to listOf("fast-skipping"),
        "Dead bug" to listOf("dead-bug"),
        "Kısa plank" to listOf("plank"),
    )

    /** Bilinen tüm plan ifadeleri (test kapsamı için). */
    val ALL_PLAN_EXPRESSIONS: List<String> =
        BY_EXACT_NAME.keys.toList() + BY_EXACT_NAME.keys.flatMap { it.split("/") }.map { it.trim() }.filter { it.isNotBlank() } +
            listOf("Baldır yükseltme", "Broad jump", "Pogo", "Ayak bileği sıçraması", "Kısa plank")
}

/** İmza/temizleme yardımcı sınıfı olmadan anahtar üretimi doğrudan JSON'dan okunur. */
object ExerciseKey {

    /** Kütüphane JSON'undaki [ExerciseEntry.key] ile isim tabanlı arama anahtarını çıkarır. */
    fun normalize(name: String): String {
        val lower = name.lowercase()
        val builder = StringBuilder(lower.length)
        var lastDash = false
        for (i in lower.indices) {
            val c = lower[i]
            when {
                c.isLetterOrDigit() -> {
                    builder.append(c); lastDash = false
                }
                c == '\'' -> {
                    // atları koru (ain't vb. gerekirse "ng")
                    builder.append('n'); lastDash = false
                }
                else -> if (!lastDash && builder.isNotEmpty()) {
                    builder.append('-'); lastDash = true
                }
            }
        }
        var result = builder.toString()
        while (result.endsWith("-")) result = result.dropLast(1)
        return result
    }

    /** JSON'da "image" kolonu "Key/0.jpg" biçimindeyse anahtar kısmını döndürür. */
    fun keyFromImage(imagePath: String): String {
        val trimmed = imagePath.trim()
        val slash = trimmed.lastIndexOf('/')
        return if (slash >= 0) {
            val base = trimmed.substring(0, slash)
            ExerciseKey.normalize(base)
        } else {
            ExerciseKey.normalize(trimmed)
        }
    }
}

/** Kütüphane: küratörlü egzersiz listesi üzerinde isim/anahtar/plan ifadesi araması. */
class ExerciseLibrary(private val entries: List<ExerciseEntry>) {

    val all: List<ExerciseEntry> = entries
        .map { it.copy(key = it.key.ifBlank { ExerciseKey.keyFromImage(it.image) }) }
        .sortedBy { it.key }

    private val byKey: Map<String, ExerciseEntry> = all.associateBy { it.key }

    private val byName: Map<String, ExerciseEntry> = all.associateBy { it.name.trim().lowercase() }

    val size: Int get() = all.size

    fun byKey(key: String): ExerciseEntry? = byKey[key.trim().lowercase()]

    fun byName(name: String): ExerciseEntry? = byName[name.trim().lowercase()]

    fun find(planName: String): List<ExerciseEntry> {
        val keys = PlanExerciseAliases.keysFor(planName)
        val resolved = keys.mapNotNull { byKey[it] }
        if (resolved.isNotEmpty()) return resolved
        // Plan ifadesi kütüphanede isim olarak geçiyorsa doğrudan bakılır.
        byName[planName.trim().lowercase()]?.let { return listOf(it) }
        return emptyList()
    }

    fun byCategory(category: String): List<ExerciseEntry> =
        all.filter { it.category.equals(category, ignoreCase = true) }

    fun categories(): List<String> = all.map { it.category }.distinct().sorted()
}

object ExerciseLibraryJson {

    private val json = Json { ignoreUnknownKeys = true }

    fun decode(jsonText: String): List<ExerciseEntry> =
        json.decodeFromString<List<ExerciseEntry>>(jsonText)

    fun decodeToLibrary(jsonText: String): ExerciseLibrary =
        ExerciseLibrary(decode(jsonText))
}