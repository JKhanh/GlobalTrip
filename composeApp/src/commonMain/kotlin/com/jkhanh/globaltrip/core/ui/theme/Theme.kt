package com.jkhanh.globaltrip.core.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// Current app theme colors
private val LightColors = lightColors(
    primary = GlobalTripBlue,
    primaryVariant = GlobalTripDarkBlue,
    secondary = GlobalTripOrange,
    secondaryVariant = GlobalTripDarkOrange,
    background = GlobalTripWhite,
    surface = GlobalTripWhite,
    error = GlobalTripRed,
    onPrimary = GlobalTripWhite,
    onSecondary = GlobalTripWhite,
    onBackground = GlobalTripBlack,
    onSurface = GlobalTripBlack,
    onError = GlobalTripWhite
)

private val DarkColors = darkColors(
    primary = GlobalTripLightBlue,
    primaryVariant = GlobalTripBlue,
    secondary = GlobalTripLightOrange,
    secondaryVariant = GlobalTripOrange,
    background = GlobalTripBlack,
    surface = GlobalTripDarkGray,
    error = GlobalTripRed,
    onPrimary = GlobalTripBlack,
    onSecondary = GlobalTripBlack,
    onBackground = GlobalTripWhite,
    onSurface = GlobalTripWhite,
    onError = GlobalTripWhite
)

// New Theme Palettes for Material 2 compatibility
// Adventure & Exploration
private val AdventureLightColors = lightColors(
    primary = AdventurePalette.primaryLight,
    primaryVariant = Color(0xFF0052CC), // Darker variant of the primary
    secondary = AdventurePalette.secondaryLight,
    secondaryVariant = Color(0xFF008577), // Darker variant of the secondary
    background = AdventurePalette.backgroundLight,
    surface = AdventurePalette.surfaceLight,
    error = AdventurePalette.errorLight,
    onPrimary = AdventurePalette.onPrimaryLight,
    onSecondary = AdventurePalette.onSecondaryLight,
    onBackground = AdventurePalette.onSurfaceLight,
    onSurface = AdventurePalette.onSurfaceLight,
    onError = AdventurePalette.onErrorLight
)

private val AdventureDarkColors = darkColors(
    primary = AdventurePalette.primaryDark,
    primaryVariant = Color(0xFF003D99), // Darker variant of the primary
    secondary = AdventurePalette.secondaryDark,
    secondaryVariant = Color(0xFF00695C), // Darker variant of the secondary
    background = AdventurePalette.backgroundDark,
    surface = AdventurePalette.surfaceDark,
    error = AdventurePalette.errorDark,
    onPrimary = AdventurePalette.onPrimaryDark,
    onSecondary = AdventurePalette.onSecondaryDark,
    onBackground = AdventurePalette.onSurfaceDark,
    onSurface = AdventurePalette.onSurfaceDark,
    onError = AdventurePalette.onErrorDark
)

// Serene & Tranquil
private val SereneLightColors = lightColors(
    primary = SerenePalette.primaryLight,
    primaryVariant = Color(0xFF0288D1), // Darker variant of the primary
    secondary = SerenePalette.secondaryLight,
    secondaryVariant = Color(0xFF43A047), // Darker variant of the secondary
    background = SerenePalette.backgroundLight,
    surface = SerenePalette.surfaceLight,
    error = SerenePalette.errorLight,
    onPrimary = SerenePalette.onPrimaryLight,
    onSecondary = SerenePalette.onSecondaryLight,
    onBackground = SerenePalette.onSurfaceLight,
    onSurface = SerenePalette.onSurfaceLight,
    onError = SerenePalette.onErrorLight
)

private val SereneDarkColors = darkColors(
    primary = SerenePalette.primaryDark,
    primaryVariant = Color(0xFF0277BD), // Darker variant of the primary
    secondary = SerenePalette.secondaryDark,
    secondaryVariant = Color(0xFF2E7D32), // Darker variant of the secondary
    background = SerenePalette.backgroundDark,
    surface = SerenePalette.surfaceDark,
    error = SerenePalette.errorDark,
    onPrimary = SerenePalette.onPrimaryDark,
    onSecondary = SerenePalette.onSecondaryDark,
    onBackground = SerenePalette.onSurfaceDark,
    onSurface = SerenePalette.onSurfaceDark,
    onError = SerenePalette.onErrorDark
)

// Dynamic & Vibrant
private val VibrantLightColors = lightColors(
    primary = VibrantPalette.primaryLight,
    primaryVariant = Color(0xFFE91E63), // Darker variant of the primary
    secondary = VibrantPalette.secondaryLight,
    secondaryVariant = Color(0xFF1E88E5), // Darker variant of the secondary
    background = VibrantPalette.backgroundLight,
    surface = VibrantPalette.surfaceLight,
    error = VibrantPalette.errorLight,
    onPrimary = VibrantPalette.onPrimaryLight,
    onSecondary = VibrantPalette.onSecondaryLight,
    onBackground = VibrantPalette.onSurfaceLight,
    onSurface = VibrantPalette.onSurfaceLight,
    onError = VibrantPalette.onErrorLight
)

private val VibrantDarkColors = darkColors(
    primary = VibrantPalette.primaryDark,
    primaryVariant = Color(0xFFAD1457), // Darker variant of the primary
    secondary = VibrantPalette.secondaryDark,
    secondaryVariant = Color(0xFF1565C0), // Darker variant of the secondary
    background = VibrantPalette.backgroundDark,
    surface = VibrantPalette.surfaceDark,
    error = VibrantPalette.errorDark,
    onPrimary = VibrantPalette.onPrimaryDark,
    onSecondary = VibrantPalette.onSecondaryDark,
    onBackground = VibrantPalette.onSurfaceDark,
    onSurface = VibrantPalette.onSurfaceDark,
    onError = VibrantPalette.onErrorDark
)

// Theme enum to manage different themes
enum class GlobalTripThemeOption {
    DEFAULT,
    ADVENTURE,
    SERENE,
    VIBRANT
}

// Local provider for theme selection
val LocalGlobalTripTheme = staticCompositionLocalOf { GlobalTripThemeOption.DEFAULT }

@Composable
fun GlobalTripTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    themeOption: GlobalTripThemeOption = GlobalTripThemeOption.DEFAULT,
    content: @Composable () -> Unit
) {
    val colors = when (themeOption) {
        GlobalTripThemeOption.DEFAULT -> if (darkTheme) DarkColors else LightColors
        GlobalTripThemeOption.ADVENTURE -> if (darkTheme) AdventureDarkColors else AdventureLightColors
        GlobalTripThemeOption.SERENE -> if (darkTheme) SereneDarkColors else SereneLightColors
        GlobalTripThemeOption.VIBRANT -> if (darkTheme) VibrantDarkColors else VibrantLightColors
    }

    CompositionLocalProvider(LocalGlobalTripTheme provides themeOption) {
        MaterialTheme(
            colors = colors,
            typography = Typography,
            shapes = Shapes,
            content = content
        )
    }
}