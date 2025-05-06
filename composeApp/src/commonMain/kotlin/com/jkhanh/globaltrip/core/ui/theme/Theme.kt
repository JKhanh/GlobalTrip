package com.jkhanh.globaltrip.core.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

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

@Composable
fun GlobalTripTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColors
    } else {
        LightColors
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}