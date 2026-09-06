package com.tracklab400.app.data.exercises

/** Soğuma kütüphanesi: 8 hareket. */
object CoolDownCatalog {

    val all: List<CoolDownExercise> = listOf(
        CoolDownExercise(
            id = "cooldown_jogging",
            name = "Hafif Koşu (Jogging)",
            purpose = "Kalp atışını kademeli olarak düşürür, kanı kaslardan toplar ve toparlanmayı hızlandırır.",
            durationSeconds = 240,
            steps = listOf(
                "3–5 dakika boyunca çok rahat bir koşu temposuna in.",
                "Tempoyu yürümeye doğru kademeli azalt.",
                "Son 1 dakikayı tempolu yürüyüşle bitir.",
            ),
            tips = listOf(
                "Nefes sığlaşmamalı; konuşabilecek kadar rahat olmalı.",
            ),
            commonMistakes = listOf(
                "Soğumayı atlayıp aniden durmak — kan birikmesi ve baş dönmesi riski.",
            ),
            safety = listOf(
                "Durmadan önce 3–5 dakika yürüyerek kalp atışını düşür.",
            ),
        ),
        CoolDownExercise(
            id = "cooldown_hamstring",
            name = "Hamstring Stretch",
            purpose = "Koşu sonrası hamstring gerginliğini azaltır, esnekliği destekler.",
            durationSeconds = 40,
            steps = listOf(
                "Oturur pozisyonda bacakları öne uzat, sırt dik.",
                "Öne doğru kalçadan eğilerek parmak uçlarına uzan.",
                "Gerilme hissi yeterli olduğunda 20 saniye sabit kal, 2 tekrar.",
            ),
            tips = listOf(
                "Sırtı yuvarlaklaştırma, gerilme hamstringte olmalı.",
                "Nefes alıp vermeyi sürdür, nefesi tutma.",
            ),
            commonMistakes = listOf(
                "Gerilmeyi zorlamak (ağrı değil gerginlik hedefi).",
                "Sırtı eğerek kalçadan değil belden uzanmak.",
            ),
            safety = listOf(
                "Keskin / elektrik hissi veren ağrıda gerilmeyi hemen bırak.",
            ),
        ),
        CoolDownExercise(
            id = "cooldown_quad",
            name = "Quadriceps Stretch",
            purpose = "Koşuda yoğun çalışan ön bacak kaslarını rahatlatır.",
            durationSeconds = 40,
            steps = listOf(
                "Ayakta dur, bir elinle sabite tutun.",
                "Bir ayağını arkadan kalçaya çek, aynı elinle bilekten tut.",
                "Dizler yan yana, kalçayı öne iterek gerilmeyi artır.",
                "Her bacakta 20 saniye, 2 tekrar.",
            ),
            tips = listOf(
                "Gerilme kasın ortasını tutuyorsa dizleri ayrık bırakma.",
            ),
            commonMistakes = listOf(
                "Dizini dışa kaçırmak.",
                "Bel çukurunu artırıp beli zorlamak.",
            ),
            safety = listOf(
                "Diz ekleminde ağrı varsa bilek yerine uyluktan tut.",
            ),
        ),
        CoolDownExercise(
            id = "cooldown_full_body",
            name = "Full Body Stretch (Sırt)",
            purpose = "Koşunun kamburluğunu tersine çevirir, sırt ve omuzları rahatlatır.",
            durationSeconds = 40,
            steps = listOf(
                "Yere diz çök, kalçayı topukların üzerine oturt.",
                "Kolları öne uzatarak gövdeyi yere doğru indir (çocuk duruşu).",
                "Sırtın esneyişine odaklanarak 20 saniye bekle, 2 tekrar.",
            ),
            tips = listOf(
                "Alnı yere değdirmeye çalış, kalça topuklarda kalsın.",
            ),
            commonMistakes = listOf(
                "Nefesi tutmak.",
                "Ağırlığı omuzlara veremek yerine dağıtmak.",
            ),
            safety = listOf(
                "Diz ağrısında arka kısma destek koy veya ayakta öne katlan.",
            ),
        ),
        CoolDownExercise(
            id = "cooldown_itband",
            name = "IT Band Stretch",
            purpose = "Geri uyluk-dış yüzeydeki bant gerginliğini azaltır; diz dışı ağrıyı önlemeye yardımcı olur.",
            durationSeconds = 40,
            steps = listOf(
                "Ayakta dur, bir bacağı diğerinin önüne çapraz al.",
                "Karşı taraftaki kolu baş üzerinde yana doğru uzat.",
                "Gövdeyi yana doğru eğerek gerginliği hissedin.",
                "Her iki tarafta 20 saniye, 2 tekrar.",
            ),
            tips = listOf(
                "Gerilme uyluk dışından gelse de kalçada da hissedilebilir.",
            ),
            commonMistakes = listOf(
                "Çok derin eğilip bel tarafını zorlamak.",
            ),
            safety = listOf(
                "Denge için sabit bir noktaya tutun.",
            ),
        ),
        CoolDownExercise(
            id = "cooldown_calf",
            name = "Calf Stretch",
            purpose = "Koşu sonrası baldır gerginliğini azaltır; ayak bileği esnekliğini korur.",
            durationSeconds = 40,
            steps = listOf(
                "Duvarın önünde dur, bir elin duvara temas etsin.",
                "Bir ayağı geriye al, topuk yerde ve bacak düz.",
                "Öne doğru eğilerek arka bacakta gerginlik hisset.",
                "Her bacakta 20 saniye, 2 tekrar.",
            ),
            tips = listOf(
                "Topuğu yerden kesmeden gerilme hissi devam etmeli.",
            ),
            commonMistakes = listOf(
                "Topuğu kaldırıp gerilmeyi kaybetmek.",
            ),
            safety = listOf(
                "Baldır ağrısı artarsa şiddetli gerilmekten kaçın.",
            ),
        ),
        CoolDownExercise(
            id = "cooldown_glute",
            name = "Glute Stretch (Pigeon)",
            purpose = "Koşuda kalça gerginliğini azaltır; kalça stabilitesini korur.",
            durationSeconds = 40,
            steps = listOf(
                "Ayakta bir sabite tutun, bir bacağı önüne bükerek yere oturt.",
                "Arka bacağı arkaya uzat, kalçayı öne iterek otur.",
                "Gövdeyi dik tut, her iki tarafta 20 saniye, 2 tekrar.",
            ),
            tips = listOf(
                "Ön dizin içe dönmesine izin verme.",
            ),
            commonMistakes = listOf(
                "Gövdeyi öne çökertmek.",
            ),
            safety = listOf(
                "Diz önü ağrısında ön ayağı yakın tut / yüksek pozisyon kullan.",
            ),
        ),
        CoolDownExercise(
            id = "cooldown_breathing",
            name = "Deep Breathing",
            purpose = "Kalp atışını sakinleştirir, diyaframı çalıştırır ve zihinsel toparlanma sağlar.",
            durationSeconds = 90,
            steps = listOf(
                "Rahat bir pozisyonda otur, el ayası yukarı veya diz üstü.",
                "Burundan derin nefes al, karnı şişir (3–4 saniye).",
                "Ağızdan yavaş ver, karnı içeri çek (4–5 saniye).",
                "1–2 dakika boyunca ritmi sürdür.",
            ),
            tips = listOf(
                "Oran 4-6 gibi: nefes verme, alıştan uzun olsun.",
            ),
            commonMistakes = listOf(
                "Omuzları kaldırarak sığ göğüs nefesi almak.",
            ),
            safety = listOf(
                "Baş dönmesi olursa daha kısa nefes al.",
            ),
        ),
    )

    private val byId: Map<String, CoolDownExercise> = all.associateBy { it.id }

    fun findById(id: String): CoolDownExercise? = byId[id]
}