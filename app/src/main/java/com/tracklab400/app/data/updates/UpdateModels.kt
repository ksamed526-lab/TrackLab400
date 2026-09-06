package com.tracklab400.app.data.updates

import kotlinx.serialization.Serializable

@Serializable
data class GitHubRelease(
    val tag_name: String = "",
    val name: String? = null,
    val body: String? = null,
    val assets: List<GitHubAsset> = emptyList(),
)

@Serializable
data class GitHubAsset(
    val name: String = "",
    val browser_download_url: String = "",
    val size: Long = 0,
)

data class UpdateInfo(
    val version: String,
    val apkUrl: String,
    val apkFileName: String,
    val releaseName: String?,
    val sizeBytes: Long,
)

sealed interface UpdateCheckResult {
    data class UpdateAvailable(val info: UpdateInfo) : UpdateCheckResult
    data object NoUpdate : UpdateCheckResult
    data class CheckFailed(val message: String?) : UpdateCheckResult
}