package com.tracklab400.app.data.exercises

import com.tracklab400.app.data.exercises.PoseKind.BOX_LOAD
import com.tracklab400.app.data.exercises.PoseKind.BRIDGE_DOWN
import com.tracklab400.app.data.exercises.PoseKind.BRIDGE_UP
import com.tracklab400.app.data.exercises.PoseKind.HEEL_RAISE
import com.tracklab400.app.data.exercises.PoseKind.HINGE
import com.tracklab400.app.data.exercises.PoseKind.NORDIC_LEAN
import com.tracklab400.app.data.exercises.PoseKind.NORDIC_START
import com.tracklab400.app.data.exercises.PoseKind.PLANK
import com.tracklab400.app.data.exercises.PoseKind.PLANK_KNEE
import com.tracklab400.app.data.exercises.PoseKind.PROWLER
import com.tracklab400.app.data.exercises.PoseKind.PULLUP
import com.tracklab400.app.data.exercises.PoseKind.PULLUP_UP
import com.tracklab400.app.data.exercises.PoseKind.PUSHUP
import com.tracklab400.app.data.exercises.PoseKind.PUSHUP_LOW
import com.tracklab400.app.data.exercises.PoseKind.SPLIT_SQUAT_DOWN
import com.tracklab400.app.data.exercises.PoseKind.SPLIT_SQUAT_UP
import com.tracklab400.app.data.exercises.PoseKind.SQUAT
import com.tracklab400.app.data.exercises.PoseKind.STAND
import com.tracklab400.app.data.exercises.PoseKind.STEP_UP

/**
 * Kuvvet kütüphanesi: 11 hareket. Şablonlar paylaşımlıdır; oturum içinde
 * [freshAll] ile her seferinde temiz çalışma alanı kopyaları üretilir.
 */
object StrengthCatalog {

    val all: List<StrengthExerciseRecord> = listOf(
        StrengthExerciseRecord(
            id = "strength_trapbar_deadlift",
            name = "Trap Bar Deadlift",
            targetMuscles = listOf("Hamstring", "Glute", "Alt sırt", "Trapez"),
            sets = 3,
            reps = 8,
            restSeconds = 120,
            rpeTarget = 6,
            noEquipmentAlternative = "Romen Deadlift (dambıl veya boş halter çubuğu)",
            startPose = STAND,
            endPose = HINGE,
            technique = listOf(
                "Trap bara yaklaş, ayaklar kalça genişliğinde, bar kaval orta hizasında.",
                "Kalçadan geriye iterek eğil, sırt düz, kavrama omuz genişliğinde.",
                "Nefes al, göbeği sık; bacak ve kalçayla birlikte barı yerden kaldır.",
                "Bar vücuda yakın kalır, dizler hafif bükülerek kalçayı tam ekstansiyona getir.",
                "Üstte 1 saniye dur, barı kontrollü indir.",
            ),
            commonMistakes = listOf(
                "Beli yuvarlayarak kaldırmak — en sık görülen başlangıç hatası.",
                "Barı gövdeden uzaklaştırmak.",
                "İndirme fazını hızlı ve kontrolsüz yapmak.",
            ),
            safety = listOf(
                "Ağırlığı ancak teknik bozulmadan kaldırabildiğin ölçüde artır.",
                "Bel sırt ağrısı varsa hareketi trap barda veya yüksek genlikte deneme.",
            ),
        ),
        StrengthExerciseRecord(
            id = "strength_back_squat",
            name = "Back Squat",
            targetMuscles = listOf("Quadriceps", "Glute", "Hamstring", "Çekirdek"),
            sets = 3,
            reps = 10,
            restSeconds = 120,
            rpeTarget = 6,
            noEquipmentAlternative = "Goblet Squat (dambıl önden kavrama)",
            startPose = STAND,
            endPose = SQUAT,
            technique = listOf(
                "Çubuğu trapez üzerine al, ayaklar omuz genişliğinde, parmaklar hafif dışa.",
                "Nefes al, göbeği sık; kalçayı arkaya-oturur gibi indir.",
                "Dizler parmaklarla aynı hizada kalır, topuklar yere basar.",
                "Uyluk yataya yaklaşınca dur, dizleri içe çökertme.",
                "Kalça ve dizleri dengeli iterek kalk, üstte nefes ver.",
            ),
            commonMistakes = listOf(
                "Dizlerin içe doğru çökmesi (yalnız çubukla bile).",
                "Topukların yerden kalkması.",
                "Gövdeyi öne düşürüp beli zorlamak.",
            ),
            safety = listOf(
                "Derinlikten daha önemli olan stabilitedir; kontrol edemediğin derinliğe inme.",
                "Ağırlık seçimini asistanslı veya süpervizyonsuz agresif artırma.",
            ),
        ),
        StrengthExerciseRecord(
            id = "strength_bulgarian_split_squat",
            name = "Bulgarian Split Squat",
            targetMuscles = listOf("Quadriceps", "Glute", "Kalça stabilizatörleri"),
            sets = 3,
            reps = 8,
            restSeconds = 90,
            rpeTarget = 6,
            noEquipmentAlternative = "Split Squat (arka ayak zeminde, dambılsız)",
            startPose = SPLIT_SQUAT_UP,
            endPose = SPLIT_SQUAT_DOWN,
            technique = listOf(
                "Arka ayağın üst kısmını bench/vari yüksekliğe yerleştir.",
                "Öne bir adım al, gövde dik; ön diz parmakla aynı hizada.",
                "Ön diz bükülerek arka diz neredeyse yere değene dek in.",
                "Ön ayak ortasıyla iterek kalk, gövdeyi dik tut.",
                "Bacak başına 8 tekrar; setler arasında bacak değiştir.",
            ),
            commonMistakes = listOf(
                "Ön dizin içe çökmesi veya öne taşması.",
                "Arka ayak yerleşiminin dengesiz olması.",
                "Gövdenin öne düşmesi.",
            ),
            safety = listOf(
                "Denge sorun yaşanırsa arka ayak zeminde (split squat) ile başla.",
            ),
        ),
        StrengthExerciseRecord(
            id = "strength_nordic_hamstring",
            name = "Nordic Hamstring Curl",
            targetMuscles = listOf("Hamstring", "Alt sırt (izometrik)"),
            sets = 3,
            reps = 6,
            restSeconds = 90,
            rpeTarget = 5,
            noEquipmentAlternative = "Hamstring Slider (havlu/kartonla evde)",
            startPose = NORDIC_START,
            endPose = NORDIC_LEAN,
            technique = listOf(
                "Dizler yerde, ayak bilekleri sabitlenmiş; gövde dik, kalça ekstansiyonda.",
                "Gövdeyi tek parça halinde öne indir, dizler yere bassın.",
                "Düşme hızını hamstringle kontrol et; mümkün olduğunca yavaş in.",
                "İnerken ellerle düşüşü tut (ilerleyen seviyede tutmadan dön).",
                "Konsantrik güç gelişene dek 3×6 tekrar hedeflenir.",
            ),
            commonMistakes = listOf(
                "Sırtı bükerek veya kalçayı kırarak yükü sırta bindirmek.",
                "Kontrollü inmeyip düşmek.",
                "Genliği aşırı zorlamak — negatif seviye ile başla.",
            ),
            safety = listOf(
                "Ayak bileği sabitlemesi sağlam olsun (partner veya Nordic bench).",
                "Hamstring ağrısı varsa genliği küçült ve eşmerkezli fazı atla.",
            ),
        ),
        StrengthExerciseRecord(
            id = "strength_pull_up",
            name = "Pull-Up / Chin-Up",
            targetMuscles = listOf("Latissimus", "Sırt orta", "Biceps", "Çekirdek"),
            sets = 3,
            reps = 8,
            restSeconds = 120,
            rpeTarget = 7,
            noEquipmentAlternative = "Bodyweight Row (masa altı) veya Seated Lat Pulldown",
            startPose = PULLUP,
            endPose = PULLUP_UP,
            technique = listOf(
                "Barı omuz genişliğinde kavra (pull-up avuç dış, chin-up avuç iç).",
                "Omuzları aşağı-gevşek skapulalarla kilitle, göğsü kabart.",
                "Dirsekleri yanlara doğru çekerek çeneyi barın üstüne taşı.",
                "Üstte 1 saniye dur, indişte omuzları tam ger.",
                "Tam tekrar yerine çalışma seti yap; gerektiğinde lastik bant yardımı al.",
            ),
            commonMistakes = listOf(
                "Sallanarak momentumla çıkmak.",
                "Çeneyi çubuğa yetiştirmek için boynu zorlamak.",
                "İnişte dirseği tam uzatmadan yarım tekrar yapmak.",
            ),
            safety = listOf(
                "Barda kayma olmaması için kavrama kuru ve sabit olsun.",
            ),
        ),
        StrengthExerciseRecord(
            id = "strength_romanian_deadlift",
            name = "Romanian Deadlift",
            targetMuscles = listOf("Hamstring", "Glute", "Alt sırt"),
            sets = 3,
            reps = 10,
            restSeconds = 90,
            rpeTarget = 6,
            noEquipmentAlternative = "Kettlebell / dambıl swing",
            startPose = STAND,
            endPose = HINGE,
            technique = listOf(
                "Çubuk (veya dambıllar) uyluk önünde, ayaklar kalça genişliğinde.",
                "Dizleri hafif bük, kalçadan geriye iterek gövdeyi indir.",
                "Çubuk vücuda yakın kaydırılıp diz altına kadar iner.",
                "Hamstringlerde gerginlik hissedince kalça ve dizle kaldır.",
                "Üstte kalçayı sık, sırt düz kalır.",
            ),
            commonMistakes = listOf(
                "Hareketi başlangıçta deadlift gibi yerden başlatmak.",
                "Çubuğu gövdeden uzağa taşımak.",
                "Bacakları fazla büküp squat'a dönüştürmek.",
            ),
            safety = listOf(
                "Sırt pozisyonun bozulduğu genliğe inme; hamstring esnekliğine göre ayarla.",
            ),
        ),
        StrengthExerciseRecord(
            id = "strength_prowler_push",
            name = "Prowler Push",
            targetMuscles = listOf("Quadriceps", "Glute", "İtme zinciri (omentum dahil gövde)"),
            sets = 4,
            reps = 4,
            restSeconds = 120,
            rpeTarget = 7,
            noEquipmentAlternative = "Rampa koşusu veya Bounds (isteksiz kapı)",
            startPose = STAND,
            endPose = PROWLER,
            technique = listOf(
                "Eller göğüs hizasında pist kollarında, gövde öne eğik.",
                "Kısa ve güçlü adımlarla ağırlığı it, topuklarla bas.",
                "Gövdeyi dikleştirmeden, göğüs havaya doğru açık.",
                "Hedef mesafeyi (20 m) aralıksız it; her sette biraz daha hızlı.",
                "Setler arasında tam toparlan (kalp hızı düşene dek).",
            ),
            commonMistakes = listOf(
                "Ayak yerine sırtı zorlayarak itmek.",
                "Adımları küçültüp hızı kaybetmek.",
                "Nefesi tutmak.",
            ),
            safety = listOf(
                "Pist dışı kaçış yönüne dikkat; kaygan zeminde itme.",
                "Ağırlığı yalnızca gövdeni dik tutabildiğin ölçüde ekle.",
            ),
        ),
        StrengthExerciseRecord(
            id = "strength_box_jump",
            name = "Box Jumps",
            targetMuscles = listOf("Quadriceps", "Glute", "Baldır", "Pliometrik zincir"),
            sets = 3,
            reps = 5,
            restSeconds = 90,
            rpeTarget = 7,
            noEquipmentAlternative = "Step-Up'lar (kutu yerine basamak)",
            startPose = BOX_LOAD,
            endPose = STAND,
            technique = listOf(
                "Kutunun önünde, ayaklar kalça genişliğinde, dizler hafif bükük.",
                "Kolları geriye al, kalçayı indirip yaylanarak hazırlan.",
                "Kolları ileri savurarak kutuya patlayıcı zıpla.",
                "Kutunun üstüne yumuşak ve tam ayakla in, dizler hafif bükülerek yaylan.",
                "Kutudan geri atla/inerken adım at, tekrarla.",
            ),
            commonMistakes = listOf(
                "Zıplarken dizleri içe çökertmek.",
                "Yarım ayakla kutunun kenarına inmek.",
                "Kutuyu düşüşü yumuşatmadan sert iniş yapmak.",
            ),
            safety = listOf(
                "Kutu yüksekliğini, güvenli iniş yapabildiğin seviyeden başlat.",
                "Birleşim noktası riskine karşı kutu sağlam ve kaymaz olsun.",
            ),
        ),
        StrengthExerciseRecord(
            id = "strength_weighted_step_up",
            name = "Weighted Step-Up",
            targetMuscles = listOf("Quadriceps", "Glute", "Kalça stabilizatörleri"),
            sets = 3,
            reps = 10,
            restSeconds = 90,
            rpeTarget = 6,
            noEquipmentAlternative = "Bodyweight Step-Up (merevli basamak)",
            startPose = STAND,
            endPose = STEP_UP,
            technique = listOf(
                "Kutu muhtarlığında dur, dambıllar yanda veya iki elde.",
                "Hayali dizle başlayan ayağı kutunun üstüne, tam tabanla yerleştir.",
                "Ön ayakla iterek kendini yukarı, gövdeyi dik kaldır.",
                "Üstte kalçayı sık, arka bacakla destek alma.",
                "Kontrollü in, ön ayakla ağırlığı al.",
            ),
            commonMistakes = listOf(
                "Arka ayakla zıplar gibi destek almak.",
                "Ön dizin içe çökmesi.",
                "Kutunun dar ve küçük olması nedeniyle dengesiz adım.",
            ),
            safety = listOf(
                "Kutu yüksekliğini ön diz yaklaşık 90° kalacak şekilde seç.",
            ),
        ),
        StrengthExerciseRecord(
            id = "strength_plank_leg_drive",
            name = "Plank with Leg Drive",
            targetMuscles = listOf("Çekirdek", "Kalça fleksörleri", "Quadriceps"),
            sets = 3,
            reps = 12,
            restSeconds = 60,
            rpeTarget = 5,
            noEquipmentAlternative = "High Plank (düz kollar)",
            startPose = PLANK,
            endPose = PLANK_KNEE,
            technique = listOf(
                "Dirsekler omuz altında, vücut baştan topuğa düz (plank).",
                "Bir dizi hızla göğse doğru çek (diz çalışması — leg drive).",
                "Diğer bacağı aynı şekilde çek; kontrollü, ritimli.",
                "Kalçayı yukarı kaldırma, leğen stabil kalsın.",
                "Her bacak 12 tekrar için dönüşümlü yap.",
            ),
            commonMistakes = listOf(
                "Kalçayı kaldırıp 'yürüme' hareketine dönüştürmek.",
                "Omuzların öne düşmesi.",
                "Diz çekimini hız için yarı genlik yapmak.",
            ),
            safety = listOf(
                "Bilek/omuz ağrısında dirsek plank ile başka yapma.",
            ),
        ),
        StrengthExerciseRecord(
            id = "strength_push_up_leg_drive",
            name = "Push-Up with Leg Drive",
            targetMuscles = listOf("Göğüs", "Triceps", "Ön omuz", "Çekirdek"),
            sets = 3,
            reps = 10,
            restSeconds = 60,
            rpeTarget = 5,
            noEquipmentAlternative = "Banded Push-Up veya Close-Grip Push-Up",
            startPose = PUSHUP,
            endPose = PUSHUP_LOW,
            technique = listOf(
                "Eller omuz genişliğinde, vücut düz bir çizgi.",
                "Her tekrarda bir dizi göğse doğru sürükle (leg drive).",
                "Göğsü yere indir, dirsekleri gövdeyle ~45° açıda tut.",
                "Avuç içi ve ayak parmaklarıyla iterek kalk, kalça stabil.",
                "Tekrar başına bacak değiştir veya sabit sürdür.",
            ),
            commonMistakes = listOf(
                "Kalçayı yukarı kaldırıp 'köprü' haline getirmek.",
                "Yarım genlik push-up yapmak.",
                "Dirsekleri dışa açıp omzu zorlamak.",
            ),
            safety = listOf(
                "Bilek ağrısında dambıl veya şınav sapı ile yap.",
            ),
        ),
        StrengthExerciseRecord(
            id = "strength_hip_thrust",
            name = "Hip Thrust",
            targetMuscles = listOf("Glute", "Hamstring", "Bel (izometrik)"),
            sets = 3,
            reps = 8,
            restSeconds = 90,
            rpeTarget = 6,
            noEquipmentAlternative = "Glute Bridge veya Single-Leg Glute Bridge",
            startPose = BRIDGE_DOWN,
            endPose = BRIDGE_UP,
            technique = listOf(
                "Üst sırtın benche dayan, bar (veya dambıl) kalça kemiğinin üzerinde.",
                "Ayaklar kalça genişliğinde, dizler hafif açık; dizler bitişte 90°.",
                "Çeneyi yumruk kadar yukarıda tutarak kalçayı patlayıcı şekilde kaldır.",
                "Üstte gövde omuzdan dize düz bir çizgi, kalça tam ekstansiyonda.",
                "Üstte 2 saniye kalçayı sık, kontrollü indir.",
            ),
            commonMistakes = listOf(
                "Yukarı çıkarken bel bölgesini kilitleyip sırta yük bindirmek.",
                "Ayakları çok öne/geriye açmak (diz açısı bozulur).",
                "Hareketi patlayıcı değil yavaşça itmek.",
            ),
            safety = listOf(
                "Bardaki ağırlığı, kalça kemerine yerleştirmeden önce sabitle.",
                "Asistan olmadan ağır yüklenmeden önce formu video ile kontrol et.",
            ),
        ),
        StrengthExerciseRecord(
            id = "strength_calf_raise",
            name = "Baldır Yükseltme (Calf Raise)",
            targetMuscles = listOf("Baldır (gastroknemius, soleus)"),
            sets = 3,
            reps = 12,
            restSeconds = 60,
            rpeTarget = 5,
            noEquipmentAlternative = "Ayakta tek bacakla (duvara tutunarak)",
            startPose = STAND,
            endPose = HEEL_RAISE,
            technique = listOf(
                "Ayak ucunda dur, topukları yumuşakça indir.",
                "Tam aralık: en düşük noktada baldır gergin, en yüksekte sıkıldı.",
                "Üstte 1 saniye bekleyerek tepe kasılmayı hisset.",
                "Kontrollü in; tek bacak seviyesinde dengeni duvara dayanarak bul.",
                "Pogo (ayak bileği sıçraması) varyantında ayak ucuyla ritmik zıpla.",
            ),
            commonMistakes = listOf(
                "Yarım aralıkla hızlı tekrarlamak.",
                "Dizleri bükerek hareketi yükü transfer etmek.",
                "İnişte aniden bırakmak.",
            ),
            safety = listOf(
                "Aşil tendonunda keskin ağrıda tekrarı durdur; genliği azalt.",
            ),
        ),
    )

    /** Oturum için temiz (ağırlık/set/not boş) kopyalar üretir. */
    fun freshAll(): List<StrengthExerciseRecord> =
        all.map {
            it.copy(
                weight = null,
                completedSets = 0,
                perSetNotes = mutableListOf(),
            )
        }

    private val byId: Map<String, StrengthExerciseRecord> = all.associateBy { it.id }

    fun findById(id: String): StrengthExerciseRecord? = byId[id]

    /** Plan kuvvet bloğu adını katalog kaydına eşler. */
    fun byPlanName(planName: String): StrengthExerciseRecord? {
        val lower = planName.trim().lowercase()
        val rules = listOf(
            Regex("""\bbulgar\b""") to "strength_bulgarian_split_squat",
            Regex("""\bsplit\b""") to "strength_bulgarian_split_squat",
            Regex("""\btrap\b""") to "strength_trapbar_deadlift",
            Regex("""\bromanian\b|\brdl\b""") to "strength_romanian_deadlift",
            Regex("""\bdeadlift\b""") to "strength_trapbar_deadlift",
            Regex("""\bsquat\b""") to "strength_back_squat",
            Regex("""\bnordic\b|\bhamstring\b""") to "strength_nordic_hamstring",
            Regex("""\bhip thrust\b|\bglute bridge\b""") to "strength_hip_thrust",
            Regex("""\bpull\b|\bchin\b""") to "strength_pull_up",
            Regex("""\bprowler\b|\bsled\b""") to "strength_prowler_push",
            Regex("""\bbox jump\b|\bbroad jump\b""") to "strength_box_jump",
            Regex("""\bstep\b""") to "strength_weighted_step_up",
            Regex("""\bplank\b|\bdead bug\b""") to "strength_plank_leg_drive",
            Regex("""\bpush\b""") to "strength_push_up_leg_drive",
            Regex("""baldır|\bcalf\b|\bpogo\b|sıçrama""") to "strength_calf_raise",
        )
        val id = rules.firstOrNull { it.first.containsMatchIn(lower) }?.second ?: return null
        return byId[id]
    }
}