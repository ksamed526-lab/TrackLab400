package com.tracklab400.app.data.notifications

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat

/** Bildirim çalışma zamanı izni (Android 13+) denetimi. */
object NotificationPermission {

    /**
     * Android 13+ izin istenir ve kullanıcı onayına bağlıdır. Önceki sürümlerde
     * izin kavramı yoktur; bildirimler [NotificationManagerCompat.areNotificationsEnabled]
     * üzerinden kontrol edilir.
     */
    fun canPost(context: Context): Boolean =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS,
            ) == PackageManager.PERMISSION_GRANTED
}