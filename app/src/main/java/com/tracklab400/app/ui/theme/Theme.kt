package com.tracklab400.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// Semantik token'lar (başarı, uyarı, bilgi)
@Immutable
data class TrackLabColors(
    val success: Color,
    val onSuccess: Color,
    val warning: Color,
    val info: Color,
)

private val LightTrackLabColors = TrackLabColors(
    success = Lime700,
    onSuccess = Color.White,
    warning = Orange700,
    info = Turquoise700,
)

private val DarkTrackLabColors = TrackLabColors(
    success = Lime400,
    onSuccess = Navy900,
    warning = Orange500,
    info = Turquoise500,
)

private val PinkLightTrackLabColors = TrackLabColors(
    success = PinkPrimary,
    onSuccess = Color.White,
    warning = Color(0xFFC77D00),
    info = PinkTertiary,
)

private val PinkDarkTrackLabColors = TrackLabColors(
    success = PinkPrimaryDark,
    onSuccess = PinkOnPrimaryDark,
    warning = Color(0xFFF2A33B),
    info = PinkTertiaryDark,
)

val LocalTrackLabColors = staticCompositionLocalOf { LightTrackLabColors }

val MaterialTheme.trackLabColors: TrackLabColors
    @Composable get() = LocalTrackLabColors.current

private val LightColors = lightColorScheme(
    primary = Navy700,
    onPrimary = Color.White,
    primaryContainer = Navy100,
    onPrimaryContainer = Navy900,
    secondary = Turquoise700,
    onSecondary = Color.White,
    secondaryContainer = Turquoise200,
    onSecondaryContainer = Navy900,
    tertiary = Orange700,
    onTertiary = Color.White,
    tertiaryContainer = Orange200,
    onTertiaryContainer = Color(0xFF4A2800),
    background = Cream50,
    onBackground = Navy900,
    surface = Cream50,
    onSurface = Navy900,
    surfaceVariant = Cream200,
    onSurfaceVariant = Navy600,
    surfaceContainerLowest = LightSurfaceContainerLowest,
    surfaceContainerLow = LightSurfaceContainerLow,
    surfaceContainer = LightSurfaceContainer,
    surfaceContainerHigh = LightSurfaceContainerHigh,
    surfaceContainerHighest = LightSurfaceContainerHighest,
    outline = Color(0xFF7A87A1),
    outlineVariant = Cream200,
    error = Color(0xFFBA1A1A),
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
)

private val DarkColors = darkColorScheme(
    primary = Lime400,
    onPrimary = Navy900,
    primaryContainer = Color(0xFF42510D),
    onPrimaryContainer = Lime200,
    secondary = Turquoise500,
    onSecondary = Navy900,
    secondaryContainer = Color(0xFF00524E),
    onSecondaryContainer = Turquoise200,
    tertiary = Orange500,
    onTertiary = Navy900,
    tertiaryContainer = Color(0xFF7A4A06),
    onTertiaryContainer = Orange200,
    background = Navy900,
    onBackground = Cream50,
    surface = Navy900,
    onSurface = Cream50,
    surfaceVariant = Color(0xFF1B2B4E),
    onSurfaceVariant = Navy200,
    surfaceContainerLowest = DarkSurfaceContainerLowest,
    surfaceContainerLow = DarkSurfaceContainerLow,
    surfaceContainer = DarkSurfaceContainer,
    surfaceContainerHigh = DarkSurfaceContainerHigh,
    surfaceContainerHighest = DarkSurfaceContainerHighest,
    outline = Color(0xFF8E9CC0),
    outlineVariant = Color(0xFF24365C),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
)

private val PinkColors = lightColorScheme(
    primary = PinkPrimary,
    onPrimary = Color.White,
    primaryContainer = PinkPrimaryContainer,
    onPrimaryContainer = PinkOnPrimaryContainer,
    secondary = PinkSecondary,
    onSecondary = Color.White,
    secondaryContainer = PinkSecondaryContainer,
    onSecondaryContainer = PinkOnSecondaryContainer,
    tertiary = PinkTertiary,
    onTertiary = Color.White,
    tertiaryContainer = PinkTertiaryContainer,
    onTertiaryContainer = PinkOnTertiaryContainer,
    background = PinkBackground,
    onBackground = PinkOnBackground,
    surface = PinkBackground,
    onSurface = PinkOnBackground,
    surfaceVariant = PinkSurfaceVariant,
    onSurfaceVariant = PinkOnSurfaceVariant,
    surfaceContainerLowest = PinkSurfaceContainerLowest,
    surfaceContainerLow = PinkSurfaceContainerLow,
    surfaceContainer = PinkSurfaceContainer,
    surfaceContainerHigh = PinkSurfaceContainerHigh,
    surfaceContainerHighest = PinkSurfaceContainerHighest,
    outline = PinkOutline,
    outlineVariant = PinkOutlineVariant,
    error = Color(0xFFBA1A1A),
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
)

private val PinkDarkColors = darkColorScheme(
    primary = PinkPrimaryDark,
    onPrimary = PinkOnPrimaryDark,
    primaryContainer = PinkPrimaryContainerDark,
    onPrimaryContainer = PinkOnPrimaryContainerDark,
    secondary = PinkSecondaryDark,
    onSecondary = PinkOnSecondaryDark,
    secondaryContainer = PinkSecondaryContainerDark,
    onSecondaryContainer = PinkOnSecondaryContainerDark,
    tertiary = PinkTertiaryDark,
    onTertiary = PinkOnTertiaryDark,
    tertiaryContainer = PinkTertiaryContainerDark,
    onTertiaryContainer = PinkOnTertiaryContainerDark,
    background = PinkBackgroundDark,
    onBackground = PinkOnBackgroundDark,
    surface = PinkBackgroundDark,
    onSurface = PinkOnBackgroundDark,
    surfaceVariant = PinkSurfaceVariantDark,
    onSurfaceVariant = PinkOnSurfaceVariantDark,
    surfaceContainerLowest = PinkSurfaceContainerLowestDark,
    surfaceContainerLow = PinkSurfaceContainerLowDark,
    surfaceContainer = PinkSurfaceContainerDark,
    surfaceContainerHigh = PinkSurfaceContainerHighDark,
    surfaceContainerHighest = PinkSurfaceContainerHighestDark,
    outline = PinkOutlineDark,
    outlineVariant = PinkOutlineVariantDark,
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
)

@Composable
fun TrackLabTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    pinkTheme: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        pinkTheme && darkTheme -> PinkDarkColors
        pinkTheme -> PinkColors
        darkTheme -> DarkColors
        else -> LightColors
    }
    val trackColors = when {
        pinkTheme && darkTheme -> PinkDarkTrackLabColors
        pinkTheme -> PinkLightTrackLabColors
        darkTheme -> DarkTrackLabColors
        else -> LightTrackLabColors
    }
    CompositionLocalProvider(LocalTrackLabColors provides trackColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = TrackLabTypography,
            shapes = TrackLabShapes,
            content = content,
        )
    }
}
