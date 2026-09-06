package com.tracklab400.app.data.updates

import java.net.HttpURLConnection
import java.net.URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

object UpdateChecker {

    private const val USER_AGENT = "TrackLab400-Android"
    private const val TIMEOUT_MS = 10_000

    private val json = Json { ignoreUnknownKeys = true }

    suspend fun checkLatest(currentVersionName: String): UpdateCheckResult =
        withContext(Dispatchers.IO) {
            runCatching {
                val connection = URL(UpdateConfig.RELEASE_API_URL).openConnection() as HttpURLConnection
                try {
                    connection.requestMethod = "GET"
                    connection.connectTimeout = TIMEOUT_MS
                    connection.readTimeout = TIMEOUT_MS
                    connection.setRequestProperty("Accept", "application/vnd.github+json")
                    connection.setRequestProperty("User-Agent", USER_AGENT)
                    when (connection.responseCode) {
                        404 -> UpdateCheckResult.NoUpdate
                        in 200..299 -> {
                            val body = connection.inputStream.bufferedReader().use { it.readText() }
                            val release = json.decodeFromString<GitHubRelease>(body)
                            resolve(release, currentVersionName)
                        }
                        else -> UpdateCheckResult.CheckFailed(connection.responseMessage)
                    }
                } finally {
                    connection.disconnect()
                }
            }.getOrElse { error ->
                UpdateCheckResult.CheckFailed(error.message)
            }
        }

    private fun resolve(release: GitHubRelease, currentVersionName: String): UpdateCheckResult {
        val asset = release.assets.firstOrNull { it.name.endsWith(".apk", ignoreCase = true) }
            ?: return UpdateCheckResult.NoUpdate
        if (asset.browser_download_url.isBlank()) return UpdateCheckResult.NoUpdate
        if (!UpdateVersions.isNewer(release.tag_name, currentVersionName)) {
            return UpdateCheckResult.NoUpdate
        }
        val version = UpdateVersions.parse(release.tag_name) ?: return UpdateCheckResult.NoUpdate
        return UpdateCheckResult.UpdateAvailable(
            UpdateInfo(
                version = release.tag_name.removePrefix("v"),
                apkUrl = asset.browser_download_url,
                apkFileName = asset.name,
                releaseName = release.name,
                sizeBytes = asset.size,
            ),
        )
    }
}