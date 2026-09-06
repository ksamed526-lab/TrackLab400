# TrackLab 400 — Ürün Gereksinim Dokümanı (PRD)

Sürüm: v0.1 · Tarih: 03.09.2026 · Durum: Onay bekliyor
Kaynak: `400m_68_saniye_8_haftalik_program.pdf` (ayrıntılı plan: `docs/plan_reference.md`)

---

## 1. Uygulama Amacı

TrackLab 400, PDF'deki 8 haftalık 400 m performans planını temel alan, **offline çalışan**
yerel bir atletizm performans uygulamasıdır.

Amaçlar (öncelik sırasıyla):

1. Kullanıcının mevcut/güncel derecelerine göre **kişiselleştirilmiş 8 haftalık 400 m
   antrenman planı** üretmek (hedef süreye göre tempolar yeniden hesaplanır).
2. Antrenman sırasında kullanıcıya yardımcı olmak: **koşu kronometresi** (ileri sayan,
   tur/hedef karşılaştırmalı) ve **dinlenme geri sayımı**.
3. Antrenmanları, testleri, uyku/enerji/bacak yorgunluğunu kaydedip **ilerlemeyi
   grafiklerle** göstermek.
4. **Gerçekçi ve dürüst hedef analizi** sunmak (Truth Mode): ulaşılmaz hedefleri
   satmak değil, güvenli maksimum gelişimi ve gerektiğinde ara hedefi önermek.
5. Kuvvet hareketleri kütüphanesi (görseller + teknik ipuçları) ve hatırlatıcı
   bildirimlerle plana uyumu desteklemek.

### Kapsam Dışı (v1)

- Bulut/backend, hesap sistemi, sosyal paylaşım
- 100 m / 200 m plan üretimi (veri modeli hazır olacak, ekran yalnızca 400 m)
- Kalp atışı / GPS sensör entegrasyonu
- Koç iletişimi

---

## 2. Hedef Kullanıcı ve Değer Önerisi

- **Kim:** 400 m koşusunu geliştirmek isteyen, pist/spor salonu erişimi olan veya
  olmayan, amatör/yaygın atlet.
- **Temel değer:** "Koç yokken bile PDF'deki plan gibi profesyonel bir planı,
  tempolarına göre uyarlanmış biçimde telefonundan uygulayabilmek."

---

## 3. Kullanıcı Akışları

### 3.1 Ana akışlar (özet)

| # | Akış | Girdi | Çıktı |
|---|---|---|---|
| F1 | İlk kurulum (onboarding) | Profil verileri | Kişisel 8 haftalık plan |
| F2 | Günlük antrenman | Seans tamamlama verisi | Tamamlanan seans + geçmiş kaydı |
| F3 | Haftalık görünüm | Hafta seçimi | O haftanın 4 seansı ve durumu |
| F4 | Koşu + dinlenme | Kronometre/lap/geri sayım | Tur kayıtları, hedef uyumu |
| F5 | Günlük kayıt | Uyku/enerji/yorgunluk | Grafiklere girdi |
| F6 | Test ve kontrol noktaları | 300 m test / 400 m yarış | Hedef analizi güncellemesi |
| F7 | Ayarlar | Bildirim/profil/tema | Kalıcı tercihler |

### 3.2 F1 — İlk kurulum (onboarding)

```
Hoş Geldin
   │  "Başla" veya "Demo kullanıcı ile dene"
   ▼
Profil Formu  (mevcut 400m, mevcut 100m, hedef 400m, hazırlık süresi,
               yaş, ekipman çoklu seçim, antrenman günü sayısı, ölçüm yöntemi)
   │  Doğrulama (aralık dışı / tutarsız değerler uyarısı)
   ▼
Gerçekçi Hedef Analizi (Truth Mode)
   │  Kullanıcı: "Planı oluştur" veya "Hedefi düzelt"
   ▼
Plan Oluşturuldu özeti  →  Ana Ekran
```

### 3.3 F2 — Günlük antrenman

```
Ana Ekran → "Bugünkü Antrenman" kartı → Antrenman Detay
   ├─ Isınma kontrol listesi (4 madde)
   ├─ Koşu blokları → Koşu Kronometresi (her tekrar: lap → hedef karşılaştırma)
   │                    └─ tekrar arası → Dinlenme Geri Sayımı (otomatik öneri)
   ├─ Kuvvet blokları (Pzt: A / Cmt: B) → set/tekrar işaretleme
   └─ Bitiş: RPE (1-10) + bacak yorgunluğu + not → "Antrenmanı Tamamla"
```

### 3.4 F6 — Test ve kontrol noktaları

- **4. hafta:** 300 m test girişi → kural tabanlı değerlendirme (bkz. §9):
  - ≤ 55 sn → "68 sn hedefi için doğru yoldasın"
  - 55–57 sn → "sınırda; dikkatli ilerle"
  - > 57 sn → "68 sn zorlama; ara hedef 70–72 sn önerilir" (hedef güncellenebilir,
    tempolar yeniden hesaplanır)
- **8. hafta:** 400 m test/yarış → sonuç kaydı + PB grafiğine işlenir.

### 3.5 F4 — Kronometre akışı (iki ayrı özellik)

```
Koşu Kronometresi (ileri sayar)
  Başlat → Durdur → Tur Kaydet (tekrar = lap)
  Her turda: hedef min–max karşılaştırması (yeşil/sarı/kırmızı)
  Son tekrarda: "Dinlenme başlat?" önerisi

Dinlenme Geri Sayımı (geri sayar)
  Başlangıç: bloğun dinlenme süresi (örn. 2 dk)
  ±15 sn / ±30 sn ayar, atla, bitince ses+titreşim
  (opsiyonel: ekran kapalıyken bildirim)
```

---

## 4. Demo Kullanıcı Akışı

| Adım | Davranış |
|---|---|
| 1 | Karşılama ekranında **"Demo kullanıcı ile dene"** seçilir |
| 2 | Profil otomatik doldurulur: 400 m = 76 sn, 100 m = 15.0 sn, hedef = 68 sn, süre = 8 hafta, gün = 4, ekipman = tam salon, ölçüm = el kronometresi |
| 3 | Analiz: "%10,5 gelişme gerekiyor → AGRESİF hedef. Garanti değil; plan güvenli maksimum gelişimi hedefler. 4. hafta 300 m testi 57 sn üzeri olursa 70–72 sn ara hedef önerilir." |
| 4 | 8 haftalık plan PDF'deki tablo ile birebir üretilir |
| 5 | Ana ekranda demo rozeti görünür; profil düzenlenip gerçek hesaba dönüştürülebilir |

> Demo değerleri yalnızca demo akışı içindir, sabit kodlanır; gerçek kullanıcı her
> alanı kendisi girer.

## 5. Gerçek Kullanıcı Akışı

| Adım | Davranış | Kural |
|---|---|---|
| 1 | Karşılama → **"Başla"** | — |
| 2 | Profil formu boş gelir, tüm alanlar manuel | Zorunlu: mevcut 400 m, 100 m, hedef, süre |
| 3 | Doğrulama | 100 m: 9–30 sn; 400 m: 35–180 sn; hedef < mevcut; süre 4–16 hafta |
| 4 | Ekipman çoklu seçim | Yok / Vücut ağırlığı / Dambıl / Bar / Trap-bar / Bant → "ekipman yoksa" alternatif hareketler planda kullanılır |
| 5 | Ölçüm yöntemi | El kronometresi / Elektronik → analizde ve notlarda kullanılır |
| 6 | Analiz + plan üretimi | Hedef düzeltilebilir; düzeltme analiz sonucunu günceller |

---

## 6. Ekranlar ve Kabul Kriterleri

### 6.0 Genel (tüm ekranlar)

- **K1** Koyu/açık tema sistem ayarını izler; ayarlardan değiştirilebilir.
- **K2** İnternet gerektirmez; uçuş modunda tüm işlevler çalışır.
- **K3** Geri tuşu/gezinme beklenen şekilde davranır; kayıt sırasında kazara çıkış
  "devam edilsin mi?" onayı ister.
- **K4** Türkçe metinler; zamanlar `ss,sn` veya `dk:sn` biçiminde tutarlı gösterilir.

### 6.1 Onboarding — Karşılama (`onboarding/welcome`)

- İçerik: uygulama adı, kısa değer önerisi, "Başla", "Demo kullanıcı ile dene".
- Kabul kriterleri:
  - K1.1 Profil yoksa uygulama açılışta bu ekranı gösterir; profil varsa atlar.
  - K1.2 Demo seçimi profil formunu doldurup analize geçer.
  - K1.3 Geri tuşu uygulamadan çıkar (onboarding geri sarmaz).

### 6.2 Onboarding — Profil Formu (`onboarding/profile`)

- Alanlar: mevcut 400 m (dk:sn), mevcut 100 m (sn,ss), hedef 400 m (dk:sn),
  hazırlık süresi (hafta, varsayılan 8), yaş (opsiyonel), ekipman (çoklu seçim),
  antrenman günü sayısı (3–6, varsayılan 4), ölçüm yöntemi (radyo).
- Kabul kriterleri:
  - K2.1 Geçersiz giriş anında alan altında Türkçe uyarı gösterir.
  - K2.2 Hedef ≥ mevcut ise "Hedef mevcut derecenin altında olmalı" uyarısı.
  - K2.3 "Devam" ancak zorunlu alanlar geçerliyken aktif olur.
  - K2.4 Form değerleri sonraki adımlarda (analiz + plan) kullanılır.

### 6.3 Onboarding — Gerçekçi Hedef Analizi (`onboarding/analysis`)

- İçerik: gelişim yüzdesi, karar rozeti (GERÇEKÇİ / AGRESİF / ÇOK AGRESİF),
  açıklama metni, önerilen ara hedef (varsa), "Planı oluştur" / "Hedefi düzelt".
- Kabul kriterleri:
  - K3.1 Karar §9'daki kural tablosuna birebir uyar.
  - K3.2 "Hedefi düzelt" profil formuna geri döner, girilenler korunur.
  - K3.3 Analiz sonucu veritabanına kaydedilir ve profil ekranından yeniden görüntülenebilir.

### 6.4 Onboarding — Plan Oluşturuldu (`onboarding/plan-created`)

- İçerik: plan özeti (8 hafta, haftalık 4 gün, bloklar), "Ana Ekrana Git".
- Kabul kriterleri:
  - K4.1 Plan üretimi < 2 sn sürer; üretim sırasında iskelet (skeleton) gösterilir.
  - K4.2 Üretilen tempolar hedef süreye göre yeniden hesaplanmış olur (bkz. §10).
  - K4.3 Hata durumunda "Yeniden dene" gösterilir, uygulama çökmez.

### 6.5 Ana Ekran (`home`)

- İçerik: hafta göstergesi (örn. "Hafta 3 / 8 — Hız Dayanıklılığı"), bugünün ve
  yarının antrenman kartı (gün, tür, odak, durum), hızlı istatistikler (mevcut →
  hedef, kalan hafta), günlük kayıt kısayolu (uyku/enerji/yorgunluk), doğruluk notu
  (Truth Mode) kısa mesajı.
- Kabul kriterleri:
  - K5.1 Bugün antrenman yoksa "Bugün dinlenme günü" kartı gösterir.
  - K5.2 Antrenman kartı seansı doğru durumla (planlı/tamamlandı/kısmen) işaretler.
  - K5.3 4. ve 8. haftalarda test/yarış kartı öne çıkarılır.
  - K5.4 Boş durum: plan yoksa plan oluşturma CTA'sı gösterir (bkz. §7).

### 6.6 Haftalık Plan (`plan`)

- İçerik: 8 hafta listesi (blok rengiyle ayrılmış), her hafta: odak etiketi +
  4 seans özeti + tamamlanma göstergesi; hafta seçince seans detayları.
- Kabul kriterleri:
  - K6.1 Hafta 1–4 "Yüklenme", 5–8 "Özelleşme" bloğunda görünür.
  - K6.2 Her seans PDF'deki gün/odak/içerikle eşleşir (bkz. plan_reference.md).
  - K6.3 Tamamlanan seanslar işaretli, gelecek haftalar kilitli değil ama
    "ileri tarih" bilgisiyle görünür.
  - K6.4 4. hafta kartında "Kontrol noktası: 300 m test" notu görünür.

### 6.7 Antrenman Detay (`workout/{sessionId}`)

- İçerik: ısınma (4 adım, işaretlenebilir), koşu blokları (mesafe × tekrar,
  hedef süre aralığı, dinlenme), kuvvet blokları (hareket, set × tekrar, ipucu),
  alt bölüm notları (ör. "form bozulursa bırak"), bitiş formu (RPE, bacak yorgunluğu, not).
- Kabul kriterleri:
  - K7.1 Her koşu bloğu kronometre ekranına gider; tekrar kayıtları dönerken görünür.
  - K7.2 Kuvvet setleri tek tek veya "hepsi tamam" ile işaretlenebilir.
  - K7.3 RPE 1–10 kayar ölçek (slider), bacak yorgunluğu 1–5 ile girilir.
  - K7.4 "Tamamla" tüm zorunlu akışları (en az ana bloklar işaretli) ister;
    kısmi tamamlama "Kısmen" olarak kaydedilir.
  - K7.5 Seans tamamlanınca günlük kayıt (yorgunluk) önerisi çıkar.

### 6.8 Koşu Kronometresi (`stopwatch/{sessionId}/{blockId}`) — ileri sayar

- İçerik: büyük süre göstergesi (ss,ss), hedef min–max şeridi, tekrar sayacı
  (örn. "Tekrar 3/6"), tur listesi, Başlat/Durdur/Tur/Sıfırla, "Dinlenmeye geç".
- Kabul kriterleri:
  - K8.1 İleri sayar (10 ms çözünürlük); tur kaydı mevcut tekrarın süresini
    `lap` tablosuna yazar.
  - K8.2 Tur süresi hedef aralığındaysa yeşil, ±%3 içindeyse sarı, dışındaysa kırmızı.
  - K8.3 Ekran döndürme/arka plana alma süreyi kaybettirmez (elapsedRealtime tabanlı).
  - K8.4 Son tekrar tamamlanınca dinlenme sayacı önerisi çıkar.
  - K8.5 Koşu sırasında diğer sekmelere geçişte onay istenir.

### 6.9 Dinlenme Geri Sayımı (`rest/{restMs}`) — geri sayar

- İçerik: kalan süre (büyük), −15/+15/−30/+30 sn, "Atla", bitişte ses + titreşim +
  (izin varsa) bildirim, "Sonraki tekrara geç".
- Kabul kriterleri:
  - K9.1 Başlangıç süresi bloğun dinlenme değeridir; ayarlar anlık uygulanır.
  - K9.2 Sıfırda ses ve titreşim tetiklenir; kullanıcı kapatmadan durur.
  - K9.3 Uygulama arka plandayken de geri sayım doğru kalır.
  - K9.4 "Atla" kalan süreyi kaydedip kronometreye döner (opsiyonel kayıt).

### 6.10 Veri Girişi — Günlük Kayıt (`log/{date}`)

- İçerik: uyku saati (sayı, 0–24), enerji 1–5, bacak yorgunluğu 1–5, not.
- Kabul kriterleri:
  - K10.1 Günde tek kayıt; aynı gün tekrar girilirse günceller.
  - K10.2 Grafiklerde enerji/yorgunluk/uyku ayrı serilerde görünür.
  - K10.3 Boş gönderim engellenir (en az bir alan dolu olmalı).

### 6.11 Veri Girişi — Test Sonucu (`test/new`)

- İçerik: mesafe (300 m / 400 m), süre, tarih, ölçüm yöntemi, hafta otomatik.
- Kabul kriterleri:
  - K11.1 300 m girişi hafta 4'teyse kontrol noktası değerlendirmesi tetiklenir
    (§9) ve hedef güncelleme önerisi gösterilir.
  - K11.2 400 m girişi PB karşılaştırmasıyla birlikte geçmişe işlenir.
  - K11.3 Geçersiz süre (örn. 300 m < 20 sn) uyarılır.

### 6.12 Takip ve Grafikler (`progress`)

- İçerik: sekmeler — (a) Dereceler: 300 m test ve 400 m zaman çizgisi;
  (b) Tekrar tempoları: 100 m/150 m ortalamaları haftalık; (c) Günlük: uyku/enerji/
  yorgunluk; (d) Uyum: planlanan vs tamamlanan seans.
- Kabul kriterleri:
  - K12.1 Grafikler Compose Canvas ile çizilir (ek kütüphane yok).
  - K12.2 Veri yokken "henüz veri yok + nasıl eklenir" boş durumu gösterir.
  - K12.3 Veri noktalarına dokununca değer bilgisi (tooltip) görünür.

### 6.13 Antrenman Geçmişi (`history`)

- İçerik: haftaya göre gruplanmış tamamlanan seanslar; filtre (tür/hafta); detaya git.
- Kabul kriterleri:
  - K13.1 Tamamlanan/kısmen seanslar RPE ve süre ile listelenir.
  - K13.2 Geçmiş detay salt okunurdur; silme mümkündür (onaylı).
  - K13.3 Boş durum: "Henüz antrenman yok" + CTA.

### 6.14 Kuvvet Hareketleri (`strength` + `exercise/{id}`)

- İçerik: kategoriler (Kuvvet A, Kuvvet B, Tamamlayıcı, Patlayıcılık, Core,
  Ekipmansız alternatifler); hareket detayı: görsel, set × tekrar, RPE, teknik
  ipuçları, yapılmaması gerekenler, ekipmansız alternatif.
- Kabul kriterleri:
  - K14.1 PDF'deki tüm hareketler kütüphanede yer alır (squat, trap-bar DL, hip
    thrust, bulgar split squat, front squat/step-up, RDL, nordic hamstring, baldır
    yükseltme, broad/box jump, pogo, plank/dead bug).
  - K14.2 Her hareketin görseli (illüstrasyon) ve en az 3 teknik ipucu vardır.
  - K14.3 Ekipmansız kullanıcı için her ana harekette alternatif gösterilir.

### 6.15 Bildirim Ayarları (`settings/notifications`)

- İçerik: aç/kapa, gün bazlı saat (Pzt/Salı/Perş/Cmt), yarış günü hatırlatması,
  bildirim izni durumu (API 33+ izin akışı).
- Kabul kriterleri:
  - K15.1 Kapatıldığında tüm hatırlatmalar iptal edilir; açılınca yeniden kurulur.
  - K15.2 Android 13+ izin reddinde açıklama + ayarlara kısayol gösterilir.
  - K15.3 Planlama WorkManager/AlarmManager ile yapılır; saat değişince güncellenir.

### 6.16 Profil ve Ayarlar (`settings`)

- İçerik: profil düzenleme (yeniden analiz + planı yeniden üretme onaylı), tema
  (sistem/açık/koyu), ölçüm yöntemi, veri yönetimi (geçmişi temizle, fabrika
  sıfırlama — onaylı), uygulama bilgisi.
- Kabul kriterleri:
  - K16.1 Profil değişikliği planı yeniden üretir; eski seans kayıtları korunur.
  - K16.2 Tema değişikliği anında uygulanır ve kalıcıdır.
  - K16.3 Yıkıcı işlemler iki adımlı onay ister.

---

## 7. Boş / Hata / Yükleniyor Durumları

| Ekran | Boş | Hata | Yükleniyor |
|---|---|---|---|
| Ana ekran | Plan yok → "Planını oluştur" CTA | DB okuma hatası → yeniden dene | İskelet kartlar |
| Haftalık plan | Plan yok → onboarding'e yönlendir | Üretim hatası → retry | Plan üretimi iskeleti |
| Kronometre | — | Kayıt hatası → tekrar dene (kayıp yok) | — |
| Grafikler | "Henüz veri yok" + nasıl ekleneceği | Veri okuma hatası → retry | Shimmer/iskelet |
| Geçmiş | "Henüz antrenman yok" | — | Sayfalama iskeleti |
| Kuvvet | (statik, boş olmaz) | — | — |
| Günlük kayıt | Bugünkü kayıt yok → form | Kaydetme hatası → toast | Kaydet butonu spinner |
| Bildirimler | İzin verilmemiş → açıklama kartı | Planlama hatası → tekrar dene | — |

- Uygulama tamamen offline: ağ kaynaklı hata durumu yoktur.
- Form doğrulama hataları alan altında gösterilir (bkz. §6.2).
- Kronometre/geri sayım çalışırken ekrandan çıkış onay gerektirir.

---

## 8. Bildirim Tasarımı

- Kanal: `antrenman_hatirlatma` (önem: yüksek, varsayılan ses).
- Tarihçe: antrenman günü, kullanıcının seçtiği saatte (varsayılan 18:00).
- İçerik: "Bugün: 400 m özel dayanıklılık — 2×300 m / 58–60 sn".
- Yarış haftası: yarış sabahı ek hatırlatma + ritim bilgisi.
- Araç: WorkManager (tek seferlik iş) + gerektiğinde AlarmManager kesin alarmı.
- İzin: API 33+ `POST_NOTIFICATIONS`; reddinde ayarlar ekranında durum kartı.

---

## 9. Gerçekçi Hedef Analizi (Truth Mode) Kuralları

Girdi: mevcut 400 m, hedef, mevcut 100 m, süre (hafta), ölçüm yöntemi.

| Koşul (gelişim %) | Karar | Mesaj |
|---|---|---|
| < %4 | GERÇEKÇİ | "Sağlıklı hedef; plana bağlı kal." |
| %4–8 | AGRESİF | "Mümkün ama disiplin şart; haftalık kontrol önemli." |
| > %8 | ÇOK AGRESİF | "Garanti değil. Güvenli maksimum gelişimi hedefliyoruz." + ara hedef öner |

Demo hesabı (76 → 68 = %10,5) → ÇOK AGRESİF kategorisi mesajını alır.

**4. hafta kapısı (300 m test):**

| Test sonucu | Aksiyon |
|---|---|
| ≤ 55 sn | Hedef korunur: "68 sn için doğru yoldasın." |
| 55–57 sn | "Sınırdasın; toparlanmaya öncelik ver." |
| > 57 sn | "68 sn'yi zorlama: ara hedef 70–72 sn önerilir." → hedef düzeltme akışı, tempolar yeniden hesaplanır |

**Tempoların yeniden hesaplanması:** PDF tempoları 68 sn hedefi için hazırlanmıştır;
farklı hedef girilirse her koşu bloğunun hedef aralığı, hedef 400 m ile orantılı
olarak ölçeklenir (örn. 300 m hedefi ≈ hedef × 0,75–0,76). Hesap kuralı §10'daki
sabit oranlarla yapılır; sapma ±1 sn toleransla yuvarlanır.

---

## 10. Plan Üretim Kuralları (PDF'den türetilen veri)

- 4 gün: Pazartesi, Salı, Perşembe, Cumartesi (antrenman günü sayısı 4'ten az
  seçilirse PDF'deki "48 saat" kuralına uygun gün silme önerilir: önce Cmt, sonra
  Salı; plan ekranında uyarı gösterilir).
- Hafta 1–4: Yüklenme bloğu; Hafta 5–8: Özelleşme bloğu (tablolar
  `docs/plan_reference.md` içinde).
- Hafta 4: deload + 300 m test; Hafta 8: taper + 400 m test/yarış.
- Her koşu bloğu: mesafe, tekrar, hedef aralığı (min–max sn), dinlenme, uçan 20/30 m
  işareti.
- Kuvvet A → Pazartesi, Kuvvet B → Cumartesi seanslarına eklenir; yük ilerlemesi
  kuralı (H1–3 teknik, H4 −%25, H5–6 artış, H7–8 hafif) haftaya göre ipucu olarak gösterilir.

---

## 11. Room Veri Modelleri

> Enum'lar `String` sütununa yazılır (TypeConverter). Liste sütunları
> (ekipman, gün-saat eşlemeleri) **Kotlin Serialization** ile JSON saklanır.

| Tablo | Ana alanlar | Açıklama |
|---|---|---|
| `profile` (tek satır) | eventType, currentTimeMs, targetTimeMs, current100mMs, age, equipmentJson, trainingDaysPerWeek, prepWeeks, measurementMethod, isDemo, updatedAt | Kullanıcı profili; eventType=400M (gelecekte 100M/200M) |
| `training_plan` | profileId, eventType, startDate, weekCount, targetTimeMs, currentTimeMs, status | Aktif plan |
| `plan_week` | planId, weekNumber, block (YUKLENME/OZELLESME), focus, isDeload, checkpointNote | 8 haftalık hafta kayıtları |
| `session` | weekId, dayOfWeek (PZT/SAL/PER/CMT), kind (HIZ_KUVVET_A, TEMPO, OZEL_DAYANIKLILIK, MAKS_HIZ_KUVVET_B, AKTIVASYON, TEST_YARIS), title, focus, status (PLANLI/TAMAMLANDI/KISMEN/ATLANDI), scheduledDate, completedAt, rpe, notes, legFatigue | Antrenman seansı (ayrıca geçmiş kaynağı) |
| `run_block` | sessionId, orderIndex, name, distance, reps, targetMinMs, targetMaxMs, restMs, isFlying, isTest | Koşu bloğu |
| `lap` | blockId, repNumber, timeMs, restMsUsed, recordedAt, hitTarget | Tekrar kaydı |
| `strength_set` | sessionId, exerciseId, orderIndex, sets, reps, load, rpeTarget | Seansa programlanmış kuvvet seti |
| `strength_exercise` | key, name, category (A/B/TAMAMLAYICI/PLYO/CORE/ALTERNATIF), defaultSets, defaultReps, description, cues, imageRes, alternativeExerciseId | Statik hareket kütüphanesi |
| `daily_log` | date (tekil), sleepHours, energy (1–5), legFatigue (1–5), notes | Günlük sağlık kaydı |
| `test_result` | date, distance (300/400), timeMs, method, weekNumber, notes | Test/yarış sonuçları |
| `goal_analysis` (tek satır) | profileId, verdict, improvementPercent, suggestedTargetMs, message, createdAt | Son analiz sonucu |
| `notification_settings` (tek satır) | enabled, dayTimeJson (PZT:18:00...), raceReminder, remindBeforeMinutes | Bildirim tercihleri |

İlişkiler: profile 1–1 training_plan 1–N plan_week 1–N session 1–N run_block/strength_set;
run_block 1–N lap; session N–1 daily_log (tarih üzerinden yumuşak bağ).

### Gelecek genişletme (100 m / 200 m)

- `eventType` kolonu profil, plan, seans üzerinde mevcut; `run_block.distance` geneldir.
- Yeni mesafe ekleme = yeni veri tablosu (plan şablonu) + analiz kuralı; şema değişmez.

---

## 12. Navigasyon Haritası (Navigation Compose)

```
TrackLabApp
├─ start (giriş: profil var mı?)
│   ├─ onboarding/welcome
│   ├─ onboarding/profile
│   ├─ onboarding/analysis
│   └─ onboarding/plan-created        → home (plan hazır)
│
├─ main (alt sekmeler, Scaffold + NavigationBar)
│   ├─ home         Ana Ekran
│   ├─ plan         Haftalık Plan
│   ├─ progress     Takip ve Grafikler
│   └─ strength     Kuvvet Hareketleri
│
├─ workout/{sessionId}                Antrenman Detay
│   ├─ stopwatch/{sessionId}/{blockId}   Koşu Kronometresi (ileri)
│   ├─ rest/{restMs}                     Dinlenme Geri Sayımı (geri)
│   └─ (kuvvet setleri ekran içinde)
│
├─ history                            Antrenman Geçmişi
│   └─ history/{sessionId}            Geçmiş detay (salt okunur)
│
├─ log/{date}                         Günlük kayıt (uyku/enerji/yorgunluk)
├─ test/new                           Test sonucu girişi
├─ race/{weekId}                      Yarış günü ritim ekranı (Hafta 8)
├─ exercise/{exerciseId}              Hareket detayı
│
└─ settings                           Ayarlar
    ├─ settings/profile               Profil düzenleme → analiz → plan yenileme
    ├─ settings/notifications         Bildirim ayarları
    └─ settings/appearance            Tema (sistem/açık/koyu)
```

Derin bağlantı kuralı: `stopwatch`/`rest` geriye dönüşte `workout` ekranına döner;
kronometre aktifken `main` sekmelerine geçiş onaylıdır (§6.8).

---

## 13. Teknoloji Kararları (PRD düzeyinde)

- Kotlin + Jetpack Compose + Material 3; MVVM (Repository + ViewModel + Flow).
- Room (yerel DB), DataStore Preferences (tema, son açılan hafta), Kotlin
  Serialization (JSON sütunlar), Navigation Compose, WorkManager (bildirimler).
- Grafikler Compose Canvas ile özel çizim (kütüphane yok).
- Görseller: kuvvet hareketleri için statik illüstrasyon kaynakları (v1'de basit
  vektör illüstrasyonlar; fotoğraf gerekmez).
- Dil: Türkçe (v1).
