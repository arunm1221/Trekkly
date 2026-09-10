package com.example.trekkly.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = SecondaryColor,
    onPrimary = PrimaryColor,
    primaryContainer = PrimaryColor,
    onPrimaryContainer = TextColor,

    secondary = PrimaryCard,
    onSecondary = TextColor,
    secondaryContainer = PrimaryColor,
    onSecondaryContainer = PrimaryText,

    tertiary = DifficultyEasy,
    onTertiary = PrimaryColor,

    background = ForestBackground,
    onBackground = TextColor,

    surface = ForestBackground,
    onSurface = TextColor,
    // Cards, search field, unselected chips
    surfaceVariant = ForestSurface,
    // Secondary text: location, "21d", placeholder
    onSurfaceVariant = PrimaryText,

    surfaceContainerLowest = ForestBackground,
    surfaceContainerLow = ForestSurfaceElevated,
    surfaceContainer = ForestSurfaceElevated,   // NavigationBar background
    surfaceContainerHigh = ForestSurface,
    surfaceContainerHighest = ForestSurface,

    outline = ForestOutline,
    outlineVariant = ForestOutline,

    error = DifficultyExpert,
    onError = PrimaryColor
)

@Composable
fun TrekklyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}