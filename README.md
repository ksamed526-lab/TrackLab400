# TrackLab 400

400 metre koşu performansını takip eden, profil/plan/antrenman kaydı üzerine kurulu Android uygulaması. Derecelere göre kişiselleştirilmiş 8 haftalık antrenman planı üretir; seansları, koşu kronometresini, dinlenme sayacını, antrenman sonrası formu (RPE, uyku, enerji, bacak hissi, ağrı) ve ilerleme grafiklerini tek ekranda birleştirir.

## Özellikler

- Kişisel profil oluşturma, demo profili ve profil düzenleme/silme
- Mevcut derecelere (100/200/300/400 m) dayalı güvenli hedef değerlendirmesi ve uyarlanabilir 8 haftalık plan üretimi
- Haftalık 3–5 antrenman günü, tekrar + hedef zaman aralığına göre koşu blokları
- Koşu kronometresi: hedef-zaman bandına göre renkli geri bildirim, tur kaydı, tekrar istatistikleri
- Dinlenme sayacı: 10 sn düzeltme, atlama, bitiş bildirimi (ses + titreşim)
- Antrenman sonrası form: RPE, uyku saati, enerji, bacak hissettirme, ağrı/rahatsızlık bildirimi, notlar, elle tekrar süresi düzeltme
- Antrenman geçmişi: arama, durum filtresi, kayıt silme
- İlerleme: seans/tekrar grafikleri, RPE eğilimi, bacak yorgunluğu, hedef değerlendirmesi kartı
- 78 hareketlik kuvvet kütüphanesi: her hareket için kategori, ekipman, seviye ve görsel
- Bildirim hatırlatıcıları (WorkManager periyodik + tek seferlik planlama)
- Koyu/açık tema (sistem tercihine göre)

## Teknoloji

- Kotlin 2.0.21, Jetpack Compose (BOM 2024.12.01), Material 3, tek-activity + Navigation Compose
- MVVM; manual DI ([`di/AppContainer.kt`](app/src/main/java/com/tracklab400/app/di/AppContainer.kt))
- Room 2.6.1 (KSP) + DataStore Preferences + kotlinx-serialization
- WorkManager 2.10.0 (bildirim planlama), AGP 8.7.3
- minSdk 26, compileSdk/targetSdk 35, JDK 17, Java 17

## Çalıştırma

### Android Studio ile
1. Android Studio'yu açın ve **TrackLab400** klasörünü proje olarak açın (JDK 17 kurulu olmalı).
2. API 26+ bir cihaz veya emülatör seçin.
3. `app` run configuration'unu seçip **Run** deyin.

### Komut satırı ile
```bat
gradlew.bat :app:assembleDebug
```
APK çıktısı: `app/build/outputs/apk/debug/app-debug.apk`

### Testler
```bat
gradlew.bat :app:testDebugUnitTest          REM JVM birim testleri (149 test / 14 suite)
gradlew.bat :app:connectedDebugAndroidTest  REM enstrumante UI smoke testleri (3 test)
gradlew.bat :app:lintDebug                  REM lint (sıfır hata / sıfır uyarı)
```

## Mimari

```
ui/navigation   → NavHost + rota tanımları
ui/screens/*    → Compose ekranları + ViewModel'ler
ui/components   → Takım bileşenleri (kart, buton, boş durum, grafik vb.)
ui/theme        → Renk, tipografi, aralık, tema (koyu/açık)
data/plan       → PaceCalculator, TrainingPlanFactory, plan şablonları
data/timing     → StopwatchCore, RestTimerCore (saf Kotlin, cihaz zamanına bağlı değildir)
data/stats      → ProgressStats, TargetAssessment (güvenli hedef yönlendirmesi)
data/exercises  → ExerciseLibrary (78 hareket, JSON + görsel asset'leri)
data/notifications → NotificationChannels, Planner, Scheduler, Worker
data/local      → Room database, DAO'lar, entity'ler
data/repository → Profile/Record/Plan repositorileri
di/AppContainer → Basit bağımlılık kapsayıcısı
```

Arayüzdeki tüm güncel veriler Room / DataStore tabanlı `Flow`'dan gelir; ekranlar veri boşsa boş durum gösterir, hatalı girişi profil doğrulayıcı engeller.

## Veri Saklama

- **Room**: `TrackLab400.db` (sürüm 3) — profil, antrenman kayıtları, seans durumları. Tüm veri cihazda yerel tutulur; ağ/çekirdek servisi yoktur.
- **DataStore**: `user_preferences` — bildirim ayarları, onboarding durumu vb.
- Veriler uygulama yeniden başlatılsa da kalıcıdır; antrenman kayıtları `İlerleme` sekmesinde grafiklere yansır.

## Bildirim İzinleri

- `POST_NOTIFICATIONS`: Android 13+ çalışma zamanı izni; `Ayarlar → Bildirimler` ekranında istenir ve izin durumu yansıtılır. API 26–32 arası izinsiz çalışır.
- `VIBRATE`: dinlenme sayacı bitişinde titreşim.
- Bildirim gönderimi öncesi hem `areNotificationsEnabled()` hem izin (`NotificationPermission.canPost`) denetlenir.

## Aktarım süresi (Kronometre) mantığı

`StopwatchCore`, `SystemClock.uptimeMillis()` ankerlerine dayalı saf Kotlin durum makinesidir (`IDLE → RUNNING ↔ PAUSED → FINISHED`); sayacın doğruluğu Compose yeniden çiziminden bağımsızdır. Her transfer başlangıcı/kesintisi `startBaseMs`/`pauseAccumMs` ile toplanır; tur süreleri `lapBaseMs` üzerinden hesaplanır. ViewModel, periyodik `delay` ile ekranı ilerletir ve tur/tekrar kayıtlarını `RecordDao`'ya yazar.

## Bilinen Sınırlamalar

- Demo sürümü debug anahtarıyla imzalanır (`release` de debug imzası kullanır); yayın için Play imza anahtarı kurulmalıdır.
- Küçük fiziksel ekranlarda birebir doğrulama yapılmayabilir; ekranlar kaydırılabilir düzendedir.
- GPS tabanlı hız ölçümü yoktur; kronometre hedef-zaman bandına dayalıdır.
- Kuvvet sekmesi ve ağırlık takibi henüz yer tutucudur (`İlerleme` içinde boş durum gösterilir).
- Girdi dili Türkçedir; yerelleştirme diğer diller için hazır değildir.
- Bağımlılık ve SDK sürümleri bilinçli sabitlenmiştir (bkz. `app/lint.xml`); sürüm yükseltme ayrıca değerlendirilmelidir.
- Hareket görselleri ve verileri [free-exercise-db](https://github.com/yuhonas/free-exercise-db) kaynaklıdır (kamu alanı).

> Bu uygulama tıbbi cihaz değildir; ağrı/rahatsızlık bildirimi ve hedef değerlendirmesi bilgilendirme amaçlıdır.