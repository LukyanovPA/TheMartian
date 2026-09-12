package com.pavellukyanov.themartian.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val MartianColorScheme = darkColorScheme(
    primary = AccentMars,
    onPrimary = BgDeep,
    primaryContainer = SurfaceMuted,
    onPrimaryContainer = AccentMarsLight,
    secondary = AccentMarsLight,
    onSecondary = BgDeep,
    tertiary = StatusActive,
    onTertiary = BgDeep,
    background = BgDeep,
    onBackground = TextPrimary,
    surface = SurfaceCard,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceMuted,
    onSurfaceVariant = TextSecondary,
    outline = SurfaceBorder,
    outlineVariant = SurfaceBorder,
    error = StatusError,
    onError = TextPrimary
)

@Composable
fun TheMartianTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = MartianColorScheme,
        typography = Typography,
        content = content
    )
}
