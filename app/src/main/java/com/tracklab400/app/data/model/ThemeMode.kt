package com.tracklab400.app.data.model

enum class ThemeMode(val storedValue: String) {
    SYSTEM("system"),
    LIGHT("light"),
    DARK("dark"),
    PINK("pink");

    companion object {
        fun fromStoredValue(value: String?): ThemeMode =
            entries.firstOrNull { it.storedValue == value } ?: SYSTEM
    }
}

fun resolveDarkTheme(mode: ThemeMode, systemDark: Boolean): Boolean = when (mode) {
    ThemeMode.SYSTEM -> systemDark
    ThemeMode.LIGHT -> false
    ThemeMode.DARK -> true
    ThemeMode.PINK -> systemDark
}