package com.sadaqahcode.muslimlauncher.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

internal val NightColorScheme = darkColorScheme(
    // Primary
    primary = NightPrimary,
    onPrimary = NightOnPrimary,
    primaryContainer = NightPrimaryContainer,
    onPrimaryContainer = NightOnPrimaryContainer,
    inversePrimary = NightInversePrimary,

    // Secondary
    secondary = NightSecondary,
    onSecondary = NightOnSecondary,
    secondaryContainer = NightSecondaryContainer,
    onSecondaryContainer = NightOnSecondaryContainer,

    // Tertiary
    tertiary = NightTertiary,
    onTertiary = NightOnTertiary,
    tertiaryContainer = NightTertiaryContainer,
    onTertiaryContainer = NightOnTertiaryContainer,

    // Background
    background = NightBackground,
    onBackground = NightOnSurface,

    // Surface
    surface = NightSurface,
    onSurface = NightOnSurface,
    surfaceVariant = NightSurfaceContainer,
    onSurfaceVariant = NightMutedText,
    surfaceTint = NightPrimary,
    inverseSurface = NightInverseSurface,
    inverseOnSurface = NightInverseOnSurface,

    // Error
    error = NightError,
    onError = NightOnError,
    errorContainer = NightErrorContainer,
    onErrorContainer = NightOnErrorContainer,

    // Outline
    outline = NightOutline,
    outlineVariant = NightContainerOutline,

    // Scrim
    scrim = Scrim,

    // Surface scale
    surfaceBright = NightSurfaceBright,
    surfaceDim = NightSurfaceDim,
    surfaceContainer = NightSurfaceContainer,
    surfaceContainerHigh = NightSurfaceContainerHigh,
    surfaceContainerHighest = NightSurfaceContainerHighest,
    surfaceContainerLow = NightSurfaceContainerLow,
    surfaceContainerLowest = NightSurfaceContainerLowest,

    // Fixed
    primaryFixed = PrimaryFixed,
    primaryFixedDim = PrimaryFixedDim,
    onPrimaryFixed = OnPrimaryFixed,
    onPrimaryFixedVariant = OnPrimaryFixedVariant,
    secondaryFixed = SecondaryFixed,
    secondaryFixedDim = SecondaryFixedDim,
    onSecondaryFixed = OnSecondaryFixed,
    onSecondaryFixedVariant = OnSecondaryFixedVariant,
    tertiaryFixed = TertiaryFixed,
    tertiaryFixedDim = TertiaryFixedDim,
    onTertiaryFixed = OnTertiaryFixed,
    onTertiaryFixedVariant = OnTertiaryFixedVariant,
)

internal val ParchmentColorScheme = lightColorScheme(
    // Primary
    primary = DayPrimary,
    onPrimary = DayOnPrimary,
    primaryContainer = DayPrimaryContainer,
    onPrimaryContainer = DayOnPrimaryContainer,
    inversePrimary = DayInversePrimary,

    // Secondary
    secondary = DaySecondary,
    onSecondary = DayOnSecondary,
    secondaryContainer = DaySecondaryContainer,
    onSecondaryContainer = DayOnSecondaryContainer,

    // Tertiary
    tertiary = DayTertiary,
    onTertiary = DayOnTertiary,
    tertiaryContainer = DayTertiaryContainer,
    onTertiaryContainer = DayOnTertiaryContainer,

    // Background
    background = DayBackground,
    onBackground = DayOnSurface,

    // Surface
    surface = DaySurface,
    onSurface = DayOnSurface,
    surfaceVariant = DaySurfaceContainer,
    onSurfaceVariant = DayMutedText,
    surfaceTint = DayPrimary,
    inverseSurface = DayInverseSurface,
    inverseOnSurface = DayInverseOnSurface,

    // Error
    error = DayError,
    onError = DayOnError,
    errorContainer = DayErrorContainer,
    onErrorContainer = DayOnErrorContainer,

    // Outline
    outline = DayOutline,
    outlineVariant = DayContainerOutline,

    // Scrim
    scrim = Scrim,

    // Surface scale
    surfaceBright = DaySurfaceBright,
    surfaceDim = DaySurfaceDim,
    surfaceContainer = DaySurfaceContainer,
    surfaceContainerHigh = DaySurfaceContainerHigh,
    surfaceContainerHighest = DaySurfaceContainerHighest,
    surfaceContainerLow = DaySurfaceContainerLow,
    surfaceContainerLowest = DaySurfaceContainerLowest,

    // Fixed
    primaryFixed = PrimaryFixed,
    primaryFixedDim = PrimaryFixedDim,
    onPrimaryFixed = OnPrimaryFixed,
    onPrimaryFixedVariant = OnPrimaryFixedVariant,
    secondaryFixed = SecondaryFixed,
    secondaryFixedDim = SecondaryFixedDim,
    onSecondaryFixed = OnSecondaryFixed,
    onSecondaryFixedVariant = OnSecondaryFixedVariant,
    tertiaryFixed = TertiaryFixed,
    tertiaryFixedDim = TertiaryFixedDim,
    onTertiaryFixed = OnTertiaryFixed,
    onTertiaryFixedVariant = OnTertiaryFixedVariant,
)

@Composable
fun MuslimLauncherTheme(
    colorScheme: ColorScheme,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
    )
}