package com.tracklab400.app.data.updates

import android.content.Context
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object UpdateDownloader {

    private const val USER_AGENT = "TrackLab400-Android"
    private const val TIMEOUT_MS = 15_000

    suspend fun downloadToCache(context: Context, url: String, fileName: String): File =
        withContext(Dispatchers.IO) {
            val dir = File(context.cacheDir, "app_updates").apply { mkdirs() }
            val outputFile = File(dir, fileName)
            val connection = URL(url).openConnection() as HttpURLConnection
            try {
                connection.connectTimeout = TIMEOUT_MS
                connection.readTimeout = TIMEOUT_MS
                connection.setRequestProperty("User-Agent", USER_AGENT)
                connection.connect()
                connection.inputStream.use { input ->
                    outputFile.outputStream().use { output -> input.copyTo(output) }
                }
            } finally {
                connection.disconnect()
            }
            outputFile
        }
}