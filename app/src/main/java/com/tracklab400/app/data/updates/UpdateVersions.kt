package com.tracklab400.app.data.updates

object UpdateVersions {

    data class Version(val major: Int, val minor: Int, val patch: Int) : Comparable<Version> {
        override fun compareTo(other: Version): Int =
            compareValuesBy(
                this,
                other,
                { it.major },
                { it.minor },
                { it.patch },
            )
    }

    private val VERSION_REGEX = Regex("""v?(\d+)\.(\d+)\.(\d+)""")

    fun parse(raw: String): Version? =
        VERSION_REGEX.find(raw.trim())?.let {
            Version(
                major = it.groupValues[1].toInt(),
                minor = it.groupValues[2].toInt(),
                patch = it.groupValues[3].toInt(),
            )
        }

    fun isNewer(remoteTag: String, currentVersionName: String): Boolean {
        val remote = parse(remoteTag) ?: return false
        val current = parse(currentVersionName) ?: return false
        return remote > current
    }
}