package com.tracklab400.app.ui.screens.workout

import android.content.Context
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import com.tracklab400.app.data.exercises.ExerciseEntry
import com.tracklab400.app.data.exercises.ExerciseLibrary
import com.tracklab400.app.data.exercises.ExerciseLibraryJson
import java.io.InputStream

private const val LIBRARY_ASSET = "exercise_library.json"

/**
 * Uygulama assets klasöründeki "exercise_library.json" tabanlı kütüphaneyi yükler.
 * Yüklenemezse (asset eksik/bozuk) null döner; ekranlar bunu sessizce tolere eder.
 */
@Composable
fun rememberExerciseLibrary(): ExerciseLibrary? {
    val context = LocalContext.current
    return remember(context) {
        try {
            val jsonText = context.assets.open(LIBRARY_ASSET).bufferedReader().use { it.readText() }
            ExerciseLibraryJson.decodeToLibrary(jsonText)
        } catch (e: Exception) {
            null
        }
    }
}

/**
 * Kütüphanedeki hareket görselini "assets/exercises/<key>.jpg" dosyasından gösterir.
 * Görsel yoksa veya okunamazsa hiçbir şey çizmez; olayı sessizce atlar.
 */
@Composable
fun ExerciseAssetImage(
    entry: ExerciseEntry,
    contentDescription: String,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val bitmap = remember(context, entry.key) {
        loadExerciseBitmap(context, entry)
    }
    if (bitmap != null) {
        Image(
            bitmap = bitmap,
            contentDescription = contentDescription,
            modifier = modifier,
        )
    }
}

internal fun loadExerciseBitmap(context: Context, entry: ExerciseEntry): androidx.compose.ui.graphics.ImageBitmap? {
    return try {
        val stream: InputStream = context.assets.open("exercises/${entry.key}.jpg")
        stream.use { BitmapFactory.decodeStream(it) }?.asImageBitmap()
    } catch (e: Exception) {
        null
    }
}