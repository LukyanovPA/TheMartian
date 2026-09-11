package com.pavellukyanov.themartian.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

/**
 * The "Field Journal" scheme — the only palette the app ships. There is no light theme or
 * system dynamic-color toggle anywhere in the UI, so rather than keep a dead branch this maps
 * every Material3 role directly onto the redesign's tokens.
 */
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
