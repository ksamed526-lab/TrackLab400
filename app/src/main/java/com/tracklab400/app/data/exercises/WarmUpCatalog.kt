package com.tracklab400.app.data.exercises

import com.tracklab400.app.data.exercises.PoseKind.BRIDGE_DOWN
import com.tracklab400.app.data.exercises.PoseKind.BRIDGE_UP
import com.tracklab400.app.data.exercises.PoseKind.HAMSTRING_PUMP
import com.tracklab400.app.data.exercises.PoseKind.HEEL_RAISE
import com.tracklab400.app.data.exercises.PoseKind.HINGE
import com.tracklab400.app.data.exercises.PoseKind.KNEE_RAISE
import com.tracklab400.app.data.exercises.PoseKind.LATERAL_LUNGE
import com.tracklab400.app.data.exercises.PoseKind.LEG_SWING
import com.tracklab400.app.data.exercises.PoseKind.RUNNING
import com.tracklab400.app.data.exercises.PoseKind.SKIP
import com.tracklab400.app.data.exercises.PoseKind.STAND

/** Isınma kütüphanesi: 10 hareket. */
object WarmUpCatalog {

    val all: List<WarmUpExercise> = listOf(
        WarmUpExercise(
            id = "warmup_hafif_kosu",
            name = "Hafif Koşu",
            purpose = "Vücut sıcaklığını yükseltir, kalp-damar sistemini çalıştırır ve kaslara kan akışını artırır.",
            durationSeconds = 360,
            steps = listOf(
                "5–7 dakika boyunca konuşabileceğin kadar rahat bir tempoda koş.",
                "İlk 3 dakikayı yürüyüş temposunda, sonraki dakikaları hafif koşu temposunda geçir.",
                "Kolları dirsekten ~90° bük, ileri-geri doğal salınım yap.",
                "Ayaklar kalça genişliğinde, yere orta ayak-uç bölgesiyle yumuşak bassın.",
            ),
            tips = listOf(
                "Konuşma testi: eşlik eden biriyle konuşabilmelisin; konuşamıyorsan tempo çok hızlı demektir.",
                "Nefes ritmini 3 adım nefes al / 2 adım ver gibi sabit tutmaya çalış.",
                "Sırt dik, omuzlar gevşek ve kulaklardan uzak olsun.",
            ),
            commonMistakes = listOf(
                "Isınmayı sprint gibi koşmak — erken yorgunluk ve sakatlık riski.",
                "Sert zemine topukla çarparak koşmak.",
                "Omuzları kulaklara doğru kasılı tutmak.",
            ),
            safety = listOf(
                "Zeminin düz ve kaymaz olduğundan emin ol.",
                "Yokuş inişlerde hızı kontrol et, adımları kısalt.",
            ),
            poseStart = RUNNING,
            poseEnd = RUNNING,
        ),
        WarmUpExercise(
            id = "warmup_dinamik_ayak_bilegi",
            name = "Dinamik Ayak Bileği",
            purpose = "Ayak bileği eklem hareketliliğini artırır; hızlanma ve çıkış adımlarında itiş kalitesini geliştirir.",
            durationSeconds = 60,
            steps = listOf(
                "Ayakta dur, bir elinle denge için duvara ya da bir sabite dokun.",
                "Ayak ucunda yüksel, topukları yere indir; her bacakta 10 tekrar.",
                "Ardından ayak ucunda küçük zıplamalara geç, 10 saniye sürdür.",
                "Bileği saat yönünde ve ters yönde 10'ar kez döndür.",
            ),
            tips = listOf(
                "Yükselişte topuğu olabildiğince yukarı çek, inişte kontrollü ol.",
                "Bilek işi yaparken dizleri kilitleme, hafif bükük tut.",
            ),
            commonMistakes = listOf(
                "İnişte ayak bileğine sert yük bindirmek.",
                "Kısa süre ve aşırı hızlı tekrar yapmak.",
            ),
            safety = listOf(
                "Bilek ağrısı varsa zıplama kısmını atla, sadece hareketlilik yap.",
            ),
            poseStart = STAND,
            poseEnd = HEEL_RAISE,
        ),
        WarmUpExercise(
            id = "warmup_kalca_rotasyonlari",
            name = "Kalça Rotasyonları",
            purpose = "Kalça eklemini açarak koşu adımındaki kalça geniş açısını ve diz kaldırma hareketini serbestleştirir.",
            durationSeconds = 60,
            steps = listOf(
                "Dik dur, denge için bir sabite hafifçe dokun.",
                "Dizini yukarı ve dışa doğru büyük bir yay çizerek kaldır/indir; her bacakta 10 tekrar.",
                "Aynı hareketi içten dışa ve dıştan içe döndürerek yap.",
            ),
            tips = listOf(
                "Gövdeyi dik tut, hareket kalçadan gelsin.",
                "Denge geliştikçe destek elini yalnızca düşme anlarında kullan.",
            ),
            commonMistakes = listOf(
                "Gövdeyi öne-eğilerek veya bele doğru bükerek dengelemek.",
                "Diz yerine ayak bileğiyle döndürme yapmak.",
            ),
            safety = listOf(
                "Ani ve zorlama genlik yerine kontrollü yay tercih et.",
            ),
            poseStart = STAND,
            poseEnd = KNEE_RAISE,
        ),
        WarmUpExercise(
            id = "warmup_hamstring_gevsetme",
            name = "Hamstring Gevşetme",
            purpose = "Hamstring ve alt sırt bölgesini dinamik olarak ısıtır; kalça mentesi (hip hinge) kalitesine katkı sağlar.",
            durationSeconds = 60,
            steps = listOf(
                "Ayakta dur, dizler hafif bükük, eller kalçada.",
                "Kalçandan geriye doğru menteşelenerek gövdeyi öne indir, sırt düz kalsın.",
                "İndiğin pozisyondan dizini göğse çekerek hamstringi esnet/pompa yap; her bacakta 10 tekrar.",
            ),
            tips = listOf(
                "Sırtı yuvarlaklaştırma; eğilme kalçadan gelsin.",
                "Hareketi akıcı yap, pozisyonda bekleme.",
            ),
            commonMistakes = listOf(
                "Belden eğilerek sırtı yuvarlaklaştırmak.",
                "Dizleri kilitleyip ani esneme yapmak.",
            ),
            safety = listOf(
                "Hamstring bölgesinde keskin ağrı hissedersen genliği küçült.",
            ),
            poseStart = HINGE,
            poseEnd = HAMSTRING_PUMP,
        ),
        WarmUpExercise(
            id = "warmup_leg_swings",
            name = "Leg Swings",
            purpose = "Kalça fleksör ve hamstring kaslarını tam hareket aralığında ısıtır; adım boyunu serbestleştirir.",
            durationSeconds = 60,
            steps = listOf(
                "Bir sabite yanlamasına tutun, tek bacak üzerinde dur.",
                "Serbest bacağı ileri-geri büyük bir açıyla savur; her bacakta 10 tekrar.",
                "Gövdeyi sabit tut, salınım kalçadan başlasın.",
            ),
            tips = listOf(
                "Savunma bacak hafif bükük, gövde dik.",
                "Genliği yavaş yavaş artır, hızı değil.",
            ),
            commonMistakes = listOf(
                "Salınımı dizden değil de kalçadan yapmayıp kısa genlikte kalmak.",
                "Gövdeyi ileri-geri sallamak.",
            ),
            safety = listOf(
                "Destek noktası sağlam olsun; kaygan zeminden kaçın.",
            ),
            poseStart = STAND,
            poseEnd = LEG_SWING,
        ),
        WarmUpExercise(
            id = "warmup_lateral_lunge",
            name = "Lateral Lunge",
            purpose = "İç bacak (adduktor), kalça ve ayak bileğini yanal olarak ısıtır; yön değiştirme ve dönüş adımlarını hazırlar.",
            durationSeconds = 60,
            steps = listOf(
                "Geniş bir duruş al, ayaklar ileri baksın.",
                "Bir yana büyük bir adım at, o diz bükülüp oturtur gibi in, diğer bacak düz kalsın.",
                "Kalçayla iterek başlangıca dön; her yönde 10 tekrar.",
            ),
            tips = listOf(
                "İnen diz ayak parmaklarıyla aynı hizada kalsın.",
                "Göğsü dik tut, öne eğilme.",
            ),
            commonMistakes = listOf(
                "Dizin içe doğru çökmesi.",
                "Gövdeyi aşırı öne eğmek.",
            ),
            safety = listOf(
                "Genişlik konusunu zorlama; hamstring geriliyorsa genliği küçült.",
            ),
            poseStart = STAND,
            poseEnd = LATERAL_LUNGE,
        ),
        WarmUpExercise(
            id = "warmup_hip_hinge",
            name = "Hip Hinge",
            purpose = "Kalça mentesi hareket modelini ısıtır; koşuda gövde kontrolü ve güç üretimi için temel desendir.",
            durationSeconds = 45,
            steps = listOf(
                "Dik dur, eller kalçada, dizler hafif bükük.",
                "Kalçadan geriye it, gövdeyi neredeyse yere paralel indir, sırt düz.",
                "Kalçayı sıkıp başlangıca dön; 8 tekrar.",
            ),
            tips = listOf(
                "Sırtı düz tut (bel çukurundan ödün verme).",
                "Hareket kalçada, sırtta değil.",
            ),
            commonMistakes = listOf(
                "Belden kırılarak sırtı yuvarlaklaştırmak.",
                "Dizleri kilitli tutmak.",
            ),
            safety = listOf(
                "Bel ağrısı varsa genliği küçült ve hareketi yavaş yap.",
            ),
            poseStart = STAND,
            poseEnd = HINGE,
        ),
        WarmUpExercise(
            id = "warmup_a_skip",
            name = "A-Skip Drill",
            purpose = "Diz kaldırma, ayak bileği sertliği ve kulaç koordinasyonunu geliştirir; sprint formunun temelidir.",
            durationSeconds = 120,
            steps = listOf(
                "20 m'lik bir düzlükte, skip tarzı ileri: her adımda diz yatayı geçsin.",
                "Ayak bileğini gerip yere hızlı ve sert şekilde bassın.",
                "Karşı kolla aynı ritimde kulaç at; 2×20 m tekrarla.",
            ),
            tips = listOf(
                "Dizler yüksek, ayak parmakları yukarı çekili olsun.",
                "Temasta ayak bileği gergin (dorsifleksiyon) kalsın.",
            ),
            commonMistakes = listOf(
                "Topukla yere yumuşak basmak.",
                "Kulacı çalıştırmayıp sadece bacakla yürümek.",
            ),
            safety = listOf(
                "Kaygan zemin yerine kauçuk pist veya saha tercih et.",
            ),
            poseStart = RUNNING,
            poseEnd = SKIP,
        ),
        WarmUpExercise(
            id = "warmup_glute_bridge",
            name = "Glute Bridge",
            purpose = "Glute kaslarını uyandırır; sprint çıkışındaki kalça ekstansiyonunu ve yaylanmayı güçlendirir.",
            durationSeconds = 60,
            steps = listOf(
                "Sırt üstü yat, dizler bükük, ayaklar kalça genişliğinde.",
                "Kalçayı sıkarak yukarı kaldır, vücut omuzdan dize düz bir çizgi olsun.",
                "Üstte 1–2 saniye bekleyip kontrollü in; 12 tekrar.",
            ),
            tips = listOf(
                "Yükselişi belden değil kalçadan it.",
                "Üstte göbeği içe çek, bel çukuru aşırı olmasın.",
            ),
            commonMistakes = listOf(
                "Bel bel bölgesinden hiperekstansiyon yapmak.",
                "Ayaklarını çok geniş/kaygan açmak.",
            ),
            safety = listOf(
                "Bel bölgesinde ağrıyla yukarı itme; genliği küçült.",
            ),
            poseStart = BRIDGE_DOWN,
            poseEnd = BRIDGE_UP,
        ),
        WarmUpExercise(
            id = "warmup_speed_strides",
            name = "Speed Strides",
            purpose = "Maksimal temponun %80–90'ına çıkan kısa hızlanmalarla sinir sistemi ve sprint paterni hazırlanır.",
            durationSeconds = 180,
            steps = listOf(
                "60 m düzlükte 3 tekrar; hızları düşük başlayıp tepeye çık.",
                "1. tekrar %60, 2. tekrar %75, 3. tekrar %90 tempo.",
                "Tekrarlar arasında tam toparlan için 60–90 saniye yürü.",
            ),
            tips = listOf(
                "Hız artarken teknik bozulmasın: diz yüksekliği ve duruş korun.",
                "Yürüyüşle toparlanma tam olsun, üst üste yorgun çıkma.",
            ),
            commonMistakes = listOf(
                "İlk tekrardan tam sprint yapmak.",
                "Toparlanmayı kısaltmak.",
            ),
            safety = listOf(
                "Düzlük sonunda yeterli frenleme alanı bırak (duvar, tel örgü yok).",
            ),
            poseStart = RUNNING,
            poseEnd = RUNNING,
        ),
    )

    private val byId: Map<String, WarmUpExercise> = all.associateBy { it.id }

    fun findById(id: String): WarmUpExercise? = byId[id]

    /** Plan ısınma bloğu adına karşılık gelen katalog hareketleri. */
    fun blocksForPlanName(name: String): List<WarmUpExercise> {
        val normalized = name.trim().lowercase()
        val range = when {
            normalized.contains("strides") -> 9..9
            normalized.contains("drill") -> 5..8
            normalized.contains("hareketlilik") || normalized.contains("mobil") -> 1..4
            normalized.contains("hafif koşu") || normalized.contains("koşu") -> 0..0
            else -> 0..0
        }
        return all.filterIndexed { index, _ -> index in range }
    }
}