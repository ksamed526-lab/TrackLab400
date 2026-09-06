package com.tracklab400.app.data.plan

import com.tracklab400.app.data.model.ExerciseType
import com.tracklab400.app.data.model.PlanBlock
import com.tracklab400.app.data.model.SessionKind
import java.time.DayOfWeek

data class BlockTemplate(
    val type: ExerciseType,
    val name: String,
    val distanceM: Int? = null,
    val reps: Int? = null,
    val paceFactor: PaceFactor? = null,
    val restMinMs: Long? = null,
    val restMaxMs: Long? = null,
    val restNote: String? = null,
    val isFlying: Boolean = false,
    val isTest: Boolean = false,
    val intensity: String? = null,
    val cues: String? = null,
    val perLeg: Boolean = false,
)

data class SessionTemplate(
    val day: DayOfWeek,
    val kind: SessionKind,
    val title: String,
    val focus: String? = null,
    val isRest: Boolean = false,
    val blocks: List<BlockTemplate> = emptyList(),
)

data class WeekTemplate(
    val weekNumber: Int,
    val block: PlanBlock,
    val focus: String,
    val isDeload: Boolean = false,
    val isRaceWeek: Boolean = false,
    val hasCheckpoint: Boolean = false,
    val sessions: List<SessionTemplate>,
)

// Tempo faktörleri (68 sn hedef bazlı PDF değerlerinden türetilmiştir)
private val P_100_W1 = PaceFactor(0.280, 0.310)
private val P_100_W2 = PaceFactor(0.280, 0.301)
private val P_100_W4 = PaceFactor(0.287, 0.309)
private val P_100_W6 = PaceFactor(0.272, 0.294)
private val P_100_W7 = PaceFactor(0.265, 0.287)
private val P_100_W8 = PaceFactor(0.265, 0.279)
private val P_150_W3 = PaceFactor(0.412, 0.441)
private val P_150_W5 = PaceFactor(0.397, 0.426)
private val P_150_W7 = PaceFactor(0.353, 0.368)
private val P_300_W1 = PaceFactor(0.853, 0.882)
private val P_300_W3 = PaceFactor(0.809, 0.838)
private val P_300_TEST = PaceFactor(0.779, 0.809)
private val P_300_W6 = PaceFactor(0.765, 0.794)
private val P_250_W2 = PaceFactor(0.647, 0.676)
private val P_250_W5 = PaceFactor(0.618, 0.647)
private val P_250_W7 = PaceFactor(0.618, 0.632)
private val P_350_W5 = PaceFactor(0.897, 0.926)
private val P_200_W6 = PaceFactor(0.500, 0.515)

private val KUVVET_A = listOf(
    BlockTemplate(
        ExerciseType.STRENGTH, "Squat / Trap-bar deadlift",
        intensity = "3×5 · Zorluk 7–8",
        cues = "Dizler ayak yönünü takip etsin; son tekrar zor ama temiz kalsın.",
    ),
    BlockTemplate(
        ExerciseType.STRENGTH, "Bulgar split squat",
        intensity = "3×6 / bacak",
        cues = "Ön ayağın tamamına bas; gövdeyi dik tut.",
        perLeg = true,
    ),
    BlockTemplate(
        ExerciseType.STRENGTH, "Romanian deadlift",
        intensity = "3×6",
        cues = "Diz hafif bükülü; barı geriye gönder, sırt nötr tut.",
    ),
)

private val KUVVET_B = listOf(
    BlockTemplate(
        ExerciseType.STRENGTH, "Hip thrust",
        intensity = "3×6 · üstte 2 sn",
        cues = "Kaburgayı aşağıda tut; hareketi belden değil kalçadan üret.",
    ),
    BlockTemplate(
        ExerciseType.STRENGTH, "Front squat / Step-up",
        intensity = "3×5–6",
        cues = "Kontrollü indir, güçlü kalk; diz içeri kaçmasın.",
    ),
    BlockTemplate(
        ExerciseType.STRENGTH, "Nordic hamstring",
        cues = "Öne düşüşü yavaşlat; keskin arka bacak ağrısında bırak.",
    ),
)

private val TAMAMLAYICILAR = listOf(
    BlockTemplate(
        ExerciseType.STRENGTH, "Baldır yükseltme",
        intensity = "3×10–12",
        cues = "Tam aralık kullan; üstte 1 sn bekle. Ağrı değil, kontrollü yanma hedeflenir.",
    ),
    BlockTemplate(
        ExerciseType.STRENGTH, "Broad jump veya box jump",
        intensity = "3×3",
        cues = "Ağırlıktan önce, patlayıcı.",
    ),
    BlockTemplate(
        ExerciseType.STRENGTH, "Pogo / ayak bileği sıçraması",
        intensity = "2×20",
    ),
    BlockTemplate(
        ExerciseType.STRENGTH, "Kısa plank / dead bug",
        intensity = "3 set",
    ),
)

private val MOBILITY_NOTE = BlockTemplate(
    ExerciseType.NOTE,
    "Hareketlilik",
    cues = "Seans sonunda 5–10 dk dinamik esneme: kalça, hamstring, ayak bileği.",
)

private fun pazartesi(
    focus: String?,
    runBlocks: List<BlockTemplate>,
    strengthNote: String?,
): SessionTemplate = SessionTemplate(
    day = DayOfWeek.MONDAY,
    kind = SessionKind.HIZLANMA_KUVVET_A,
    title = "Hızlanma + Kuvvet A",
    focus = focus,
    blocks = runBlocks + strengthNoteBlock(strengthNote) + KUVVET_A + TAMAMLAYICILAR,
)

private fun sali(runBlocks: List<BlockTemplate>): SessionTemplate = SessionTemplate(
    day = DayOfWeek.TUESDAY,
    kind = SessionKind.TEMPO,
    title = "Tempo + Hareketlilik",
    focus = "Ritim: tekrar sürelerini hedef aralıkta tut, form bozulursa bırak.",
    blocks = runBlocks + MOBILITY_NOTE,
)

private fun persembe(runBlocks: List<BlockTemplate>, focus: String? = null): SessionTemplate = SessionTemplate(
    day = DayOfWeek.THURSDAY,
    kind = SessionKind.OZEL_DAYANIKLILIK,
    title = "400 m Özel Dayanıklılık",
    focus = focus ?: "Yorgunken tekniği koru: dik gövde, rahat kollar.",
    blocks = runBlocks,
)

private fun cumartesi(
    runBlocks: List<BlockTemplate>,
    strengthNote: String?,
): SessionTemplate = SessionTemplate(
    day = DayOfWeek.SATURDAY,
    kind = SessionKind.MAKS_HIZ_KUVVET_B,
    title = "Maksimum Hız + Kuvvet B",
    focus = "Uçan mesafelerde amaç hız değil: hızlı bölümde formu korumak.",
    blocks = runBlocks + strengthNoteBlock(strengthNote) + KUVVET_B + TAMAMLAYICILAR,
)

private fun strengthNoteBlock(note: String?): List<BlockTemplate> =
    if (note == null) emptyList() else listOf(BlockTemplate(ExerciseType.NOTE, "Yük ilerlemesi", cues = note))

private val REST_CARSAMBA = SessionTemplate(
    day = DayOfWeek.WEDNESDAY,
    kind = SessionKind.DINLENME,
    title = "Dinlenme",
    focus = "Tam dinlenme: sert günler arasında en az 48 saat bırak.",
    isRest = true,
)

private val REST_CUMA = SessionTemplate(
    day = DayOfWeek.FRIDAY,
    kind = SessionKind.HAFIF_TOPARLANMA,
    title = "Hafif Toparlanma",
    focus = "Kan akışını artır; yorgunluk biriktirme.",
    isRest = true,
    blocks = listOf(
        BlockTemplate(
            ExerciseType.NOTE,
            "Opsiyonel: 20–30 dk rahat yürüyüş veya hafif hareketlilik",
        ),
    ),
)

private val REST_PAZAR = SessionTemplate(
    day = DayOfWeek.SUNDAY,
    kind = SessionKind.DINLENME,
    title = "Dinlenme",
    focus = "Toparlanma günü: uyku 8–9 saat, yeterli protein.",
    isRest = true,
)

object PlanTemplate {

    val WEEKS_400: List<WeekTemplate> = listOf(
        WeekTemplate(
            weekNumber = 1,
            block = PlanBlock.YUKLENME,
            focus = "Temel hız · ritme alış",
            sessions = listOf(
                pazartesi(
                    "Her tekrarı aynı teknikle koş; süre kovalama.",
                    listOf(
                        BlockTemplate(ExerciseType.RUN, "6×20 m", distanceM = 20, reps = 6, restMinMs = 120_000L, restMaxMs = 180_000L),
                        BlockTemplate(ExerciseType.RUN, "4×40 m", distanceM = 40, reps = 4, restMinMs = 120_000L, restMaxMs = 180_000L),
                    ),
                    "Hafta 1–3: 3×5-6, tekniği oturt.",
                ),
                sali(
                    listOf(
                        BlockTemplate(ExerciseType.RUN, "8×100 m", distanceM = 100, reps = 8, paceFactor = P_100_W1, restMinMs = 45_000L, restMaxMs = 60_000L),
                    ),
                ),
                REST_CARSAMBA,
                persembe(
                    listOf(
                        BlockTemplate(ExerciseType.RUN, "2×300 m", distanceM = 300, reps = 2, paceFactor = P_300_W1),
                        BlockTemplate(ExerciseType.RUN, "2×100 m", distanceM = 100, reps = 2, cues = "Tam dinlenme sonrası, form odaklı."),
                    ),
                ),
                REST_CUMA,
                cumartesi(
                    listOf(
                        BlockTemplate(ExerciseType.RUN, "4× uçan 20 m", distanceM = 20, reps = 4, isFlying = true, restMinMs = 180_000L, restMaxMs = 300_000L),
                    ),
                    "Hafta 1–3: 3×5-6, tekniği oturt.",
                ),
                REST_PAZAR,
            ),
        ),
        WeekTemplate(
            weekNumber = 2,
            block = PlanBlock.YUKLENME,
            focus = "Hacim · teknik",
            sessions = listOf(
                pazartesi(
                    "Hacim haftası: dinlenmeler tam, kalite öncelikli.",
                    listOf(
                        BlockTemplate(ExerciseType.RUN, "6×20 m", distanceM = 20, reps = 6, restNote = "Tam dinlen: formu koruyana kadar."),
                        BlockTemplate(ExerciseType.RUN, "4×40 m", distanceM = 40, reps = 4, restNote = "Tam dinlen: formu koruyana kadar."),
                    ),
                    "Hafta 1–3: 3×5-6, tekniği oturt.",
                ),
                sali(
                    listOf(
                        BlockTemplate(ExerciseType.RUN, "10×100 m", distanceM = 100, reps = 10, paceFactor = P_100_W2),
                    ),
                ),
                REST_CARSAMBA,
                persembe(
                    listOf(
                        BlockTemplate(ExerciseType.RUN, "3×250 m", distanceM = 250, reps = 3, paceFactor = P_250_W2),
                        BlockTemplate(ExerciseType.RUN, "2×120 m", distanceM = 120, reps = 2),
                    ),
                ),
                REST_CUMA,
                cumartesi(
                    listOf(
                        BlockTemplate(ExerciseType.RUN, "5× uçan 20 m", distanceM = 20, reps = 5, isFlying = true, restMinMs = 180_000L, restMaxMs = 300_000L),
                    ),
                    "Hafta 1–3: 3×5-6, tekniği oturt.",
                ),
                REST_PAZAR,
            ),
        ),
        WeekTemplate(
            weekNumber = 3,
            block = PlanBlock.YUKLENME,
            focus = "Hız dayanıklılığı",
            sessions = listOf(
                pazartesi(
                    "Dinlenmeler tam; hız tekrarı form bozulursa bırak.",
                    listOf(
                        BlockTemplate(ExerciseType.RUN, "6×30 m", distanceM = 30, reps = 6, restMinMs = 180_000L, restMaxMs = 240_000L),
                        BlockTemplate(ExerciseType.RUN, "3×50 m", distanceM = 50, reps = 3, restMinMs = 180_000L, restMaxMs = 240_000L),
                    ),
                    "Hafta 1–3: 3×5-6, tekniği oturt.",
                ),
                sali(
                    listOf(
                        BlockTemplate(ExerciseType.RUN, "6×150 m", distanceM = 150, reps = 6, paceFactor = P_150_W3, restMinMs = 60_000L, restMaxMs = 75_000L),
                    ),
                ),
                REST_CARSAMBA,
                persembe(
                    listOf(
                        BlockTemplate(ExerciseType.RUN, "2×300 m", distanceM = 300, reps = 2, paceFactor = P_300_W3),
                        BlockTemplate(ExerciseType.RUN, "1×150 m", distanceM = 150, reps = 1),
                    ),
                ),
                REST_CUMA,
                cumartesi(
                    listOf(
                        BlockTemplate(ExerciseType.RUN, "1× uçan 20 m", distanceM = 20, reps = 1, isFlying = true, restMinMs = 180_000L, restMaxMs = 300_000L),
                        BlockTemplate(ExerciseType.RUN, "2×60 m", distanceM = 60, reps = 2),
                    ),
                    "Hafta 1–3: 3×5-6, tekniği oturt.",
                ),
                REST_PAZAR,
            ),
        ),
        WeekTemplate(
            weekNumber = 4,
            block = PlanBlock.YUKLENME,
            focus = "Yük azaltma · test",
            isDeload = true,
            hasCheckpoint = true,
            sessions = listOf(
                pazartesi(
                    "Yük azaltma: hacim azaltılmış, kalite yüksek.",
                    listOf(
                        BlockTemplate(ExerciseType.RUN, "4×30 m", distanceM = 30, reps = 4, restMinMs = 180_000L, restMaxMs = 240_000L),
                        BlockTemplate(ExerciseType.RUN, "3×40 m", distanceM = 40, reps = 3, restMinMs = 180_000L, restMaxMs = 240_000L),
                    ),
                    "Yük azaltma: setleri yaklaşık %25 azalt.",
                ),
                sali(
                    listOf(
                        BlockTemplate(ExerciseType.RUN, "6×100 m", distanceM = 100, reps = 6, paceFactor = P_100_W4),
                    ),
                ),
                REST_CARSAMBA,
                persembe(
                    listOf(
                        BlockTemplate(ExerciseType.RUN, "300 m test", distanceM = 300, reps = 1, paceFactor = P_300_TEST, isTest = true),
                        BlockTemplate(ExerciseType.RUN, "1×150 m rahat", distanceM = 150, reps = 1, cues = "Yorgun bacaklarla formu koru."),
                    ),
                    focus = "Kontrol noktası: 300 m test. Aynı pistte, aynı kronometre yöntemiyle ölç.",
                ),
                REST_CUMA,
                cumartesi(
                    listOf(
                        BlockTemplate(ExerciseType.RUN, "4× uçan 20 m", distanceM = 20, reps = 4, isFlying = true, restMinMs = 180_000L, restMaxMs = 300_000L),
                    ),
                    "Yük azaltma: setleri yaklaşık %25 azalt.",
                ),
                REST_PAZAR,
            ),
        ),
        WeekTemplate(
            weekNumber = 5,
            block = PlanBlock.OZELLESME,
            focus = "Özel dayanıklılık",
            sessions = listOf(
                pazartesi(
                    "Yarışa yaklaşım: tekrar az, kalite yüksek.",
                    listOf(
                        BlockTemplate(ExerciseType.RUN, "4×30 m", distanceM = 30, reps = 4, restMinMs = 240_000L, restMaxMs = 360_000L),
                        BlockTemplate(ExerciseType.RUN, "3×60 m", distanceM = 60, reps = 3, restMinMs = 240_000L, restMaxMs = 360_000L),
                    ),
                    "Hafta 5–6: aynı kalite, küçük yük artışı.",
                ),
                sali(
                    listOf(
                        BlockTemplate(ExerciseType.RUN, "8×150 m", distanceM = 150, reps = 8, paceFactor = P_150_W5, restMinMs = 75_000L, restMaxMs = 90_000L),
                    ),
                ),
                REST_CARSAMBA,
                persembe(
                    listOf(
                        BlockTemplate(ExerciseType.RUN, "1×350 m", distanceM = 350, reps = 1, paceFactor = P_350_W5),
                        BlockTemplate(ExerciseType.RUN, "1×250 m", distanceM = 250, reps = 1, paceFactor = P_250_W5),
                    ),
                ),
                REST_CUMA,
                cumartesi(
                    listOf(
                        BlockTemplate(ExerciseType.RUN, "1× uçan 30 m", distanceM = 30, reps = 1, isFlying = true, restMinMs = 180_000L, restMaxMs = 300_000L),
                        BlockTemplate(ExerciseType.RUN, "2×80 m", distanceM = 80, reps = 2),
                    ),
                    "Hafta 5–6: aynı kalite, küçük yük artışı.",
                ),
                REST_PAZAR,
            ),
        ),
        WeekTemplate(
            weekNumber = 6,
            block = PlanBlock.OZELLESME,
            focus = "Yarış ritmi · kalite",
            sessions = listOf(
                pazartesi(
                    "Kalite öncelik: form bozulursa tekrarı bırak.",
                    listOf(
                        BlockTemplate(ExerciseType.RUN, "4×30 m", distanceM = 30, reps = 4, restMinMs = 240_000L, restMaxMs = 360_000L),
                        BlockTemplate(ExerciseType.RUN, "3×60 m", distanceM = 60, reps = 3, restMinMs = 240_000L, restMaxMs = 360_000L),
                    ),
                    "Hafta 5–6: aynı kalite, küçük yük artışı.",
                ),
                sali(
                    listOf(
                        BlockTemplate(ExerciseType.RUN, "10×100 m", distanceM = 100, reps = 10, paceFactor = P_100_W6),
                    ),
                ),
                REST_CARSAMBA,
                persembe(
                    listOf(
                        BlockTemplate(ExerciseType.RUN, "1×300 m", distanceM = 300, reps = 1, paceFactor = P_300_W6),
                        BlockTemplate(ExerciseType.RUN, "1×200 m", distanceM = 200, reps = 1, paceFactor = P_200_W6),
                        BlockTemplate(ExerciseType.RUN, "1×150 m", distanceM = 150, reps = 1),
                    ),
                ),
                REST_CUMA,
                cumartesi(
                    listOf(
                        BlockTemplate(ExerciseType.RUN, "5× uçan 30 m", distanceM = 30, reps = 5, isFlying = true, restMinMs = 180_000L, restMaxMs = 300_000L),
                    ),
                    "Hafta 5–6: aynı kalite, küçük yük artışı.",
                ),
                REST_PAZAR,
            ),
        ),
        WeekTemplate(
            weekNumber = 7,
            block = PlanBlock.OZELLESME,
            focus = "Keskinleşme · hacim düşük",
            sessions = listOf(
                pazartesi(
                    "Hacim düşük, kalite yarış temposunda.",
                    listOf(
                        BlockTemplate(ExerciseType.RUN, "4×30 m", distanceM = 30, reps = 4, restMinMs = 240_000L, restMaxMs = 360_000L),
                        BlockTemplate(ExerciseType.RUN, "2×60 m", distanceM = 60, reps = 2, restMinMs = 240_000L, restMaxMs = 360_000L),
                    ),
                    "Hafta 7–8: 2–3 set, hafif ve hızlı.",
                ),
                sali(
                    listOf(
                        BlockTemplate(ExerciseType.RUN, "6×100 m", distanceM = 100, reps = 6, paceFactor = P_100_W7),
                        BlockTemplate(ExerciseType.RUN, "60 sn sonra 150 m", distanceM = 150, reps = 1, paceFactor = P_150_W7, cues = "6×100 m biter bitmez 60 sn dinlenme, ardından 150 m."),
                    ),
                ),
                REST_CARSAMBA,
                persembe(
                    listOf(
                        BlockTemplate(ExerciseType.RUN, "1×250 m", distanceM = 250, reps = 1, paceFactor = P_250_W7),
                    ),
                ),
                REST_CUMA,
                cumartesi(
                    listOf(
                        BlockTemplate(ExerciseType.RUN, "3× uçan 30 m", distanceM = 30, reps = 3, isFlying = true, restMinMs = 180_000L, restMaxMs = 300_000L),
                        BlockTemplate(ExerciseType.RUN, "2×60 m", distanceM = 60, reps = 2),
                    ),
                    "Hafta 7–8: 2–3 set, hafif ve hızlı.",
                ),
                REST_PAZAR,
            ),
        ),
        WeekTemplate(
            weekNumber = 8,
            block = PlanBlock.OZELLESME,
            focus = "Tazeleme · yarış",
            isRaceWeek = true,
            sessions = listOf(
                pazartesi(
                    "Tazeleme: hacim en düşük, her şey taze.",
                    listOf(
                        BlockTemplate(ExerciseType.RUN, "4×20 m", distanceM = 20, reps = 4),
                        BlockTemplate(ExerciseType.RUN, "2×40 m", distanceM = 40, reps = 2),
                    ),
                    "Hafta 7–8: 2–3 set, hafif ve hızlı. Tükeniş seti yapma.",
                ),
                sali(
                    listOf(
                        BlockTemplate(ExerciseType.RUN, "4×100 m", distanceM = 100, reps = 4, paceFactor = P_100_W8),
                    ),
                ),
                REST_CARSAMBA,
                persembe(
                    listOf(
                        BlockTemplate(ExerciseType.RUN, "1×250 m rahat hızlı", distanceM = 250, reps = 1, cues = "Rahat hızlı: yarış temposundan 1-2 sn yavaş."),
                        BlockTemplate(ExerciseType.RUN, "2×80 m", distanceM = 80, reps = 2),
                    ),
                ),
                REST_CUMA,
                SessionTemplate(
                    day = DayOfWeek.SATURDAY,
                    kind = SessionKind.TEST_YARIS,
                    title = "Aktivasyon + Yarış: 400 m",
                    focus = "Yarış günü: ısınmayı tam yap, ilk 100 m'yi kontrollü aç.",
                    blocks = listOf(
                        BlockTemplate(
                            ExerciseType.NOTE,
                            "Aktivasyon",
                            cues = "Dinamik ısınma + 2×60 m rahat artış (hafif).",
                        ),
                        BlockTemplate(
                            ExerciseType.RUN,
                            "400 m test / yarış",
                            distanceM = 400,
                            reps = 1,
                            isTest = true,
                            cues = "Aynı pistte, aynı kronometre yöntemiyle ölç. El ve elektronik ölçümü karşılaştırma.",
                        ),
                    ),
                ),
                REST_PAZAR,
            ),
        ),
    )
}
