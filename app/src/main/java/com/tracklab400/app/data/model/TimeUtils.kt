package com.tracklab400.app.data.model

import java.util.Locale

object TimeUtils {

    /**
     * Saniye cinsinden süre metnini Double'a çevirir.
     * Ondalık ayırıcı olarak hem '.' hem ',' kabul edilir.
     * Boş veya sayısal olmayan giriş için null döner.
     * Negatif ve sıfır değerler ayrıştırılır; doğrulama ProfileValidator'da yapılır.
     */
    fun parseSeconds(text: String): Double? {
        val normalized = text.trim().replace(',', '.')
        if (normalized.isEmpty()) return null
        return normalized.toDoubleOrNull()
    }

    /**
     * Milisaniyeyi Türkçe biçimde süre metnine çevirir.
     * 60 sn altı: "15,0" — 60 sn üstü: "1:16,0"
     */
    fun formatMs(millis: Long): String {
        val totalSeconds = millis / 1000.0
        val minutes = (millis / 60_000L).toInt()
        val seconds = totalSeconds - minutes * 60.0
        return if (minutes > 0) {
            val secondPart = String.format(Locale.US, "%04.1f", seconds).replace('.', ',')
            "$minutes:$secondPart"
        } else {
            String.format(Locale.US, "%.1f", seconds).replace('.', ',')
        }
    }

    /**
     * Her zaman saniye cinsinden biçimler (dakikaya çevirmez): "58,0", "61,5"
     */
    fun formatSeconds(millis: Long): String =
        String.format(Locale.US, "%.1f", millis / 1000.0).replace('.', ',')

    /**
     * Kronometre biçimi: dakika her zaman gösterilir, saniyeler 0-dolgulu.
     * "0:05,3", "0:58,0", "1:16,0"
     */
    fun formatClock(millis: Long): String {
        val totalSeconds = millis / 1000.0
        val minutes = (millis / 60_000L).toInt()
        val seconds = totalSeconds - minutes * 60.0
        return String.format(Locale.US, "%d:%04.1f", minutes, seconds).replace('.', ',')
    }

    /**
     * Dinlenme/aralık süresi: 60 sn altı "45 sn", üstü dakika ("2,5 dk").
     */
    fun formatDuration(millis: Long): String {
        if (millis < 60_000L) {
            val value = millis / 1000.0
            val text = if (value % 1.0 == 0.0) value.toInt().toString() else value.toString()
            return "${text.replace('.', ',')} sn"
        }
        val minutes = millis / 60_000.0
        val text = if (minutes % 1.0 == 0.0) {
            minutes.toInt().toString()
        } else {
            String.format(Locale.US, "%.1f", minutes).replace('.', ',')
        }
        return "$text dk"
    }
}
