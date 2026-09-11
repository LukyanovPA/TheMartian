package com.pavellukyanov.themartian.ui.theme

import androidx.compose.ui.graphics.Color

// ============== "Field Journal" palette (redesign direction A) ==============
// Warm, near-black charcoal base with a single Mars-orange accent. Every screen
// in the app is built on these tokens instead of ad hoc Color.White/Black calls.

/** Base app background — deep warm charcoal, never pure black. */
val BgDeep = Color(0xFF12100E)

/** Elevated surface for cards, sheets, dialogs. */
val SurfaceCard = Color(0xFF1C1916)

/** A step above [SurfaceCard] — thumbnails placeholders, secondary chips. */
val SurfaceMuted = Color(0xFF232019)

/** Hairline borders on cards/chips. */
val SurfaceBorder = Color(0xFF2C2723)

/** Primary text — warm off-white, never pure white (keeps contrast soft on OLED). */
val TextPrimary = Color(0xFFF2EDE6)

/** Secondary text — labels, subtitles. */
val TextSecondary = Color(0xFFA39A8E)

/** Tertiary/meta text — timestamps, mono captions. */
val TextTertiary = Color(0xFF6E665C)

/** Disabled / de-emphasized text (completed missions, inactive chips). */
val TextDisabled = Color(0xFF56504A)

/** Mars accent — primary interactive color, replaces Purple40 everywhere. */
val AccentMars = Color(0xFFE2703A)

/** Lighter accent tint for text-on-dark and subtle highlights. */
val AccentMarsLight = Color(0xFFF0A275)

/** Active-mission indicator. */
val StatusActive = Color(0xFF6FBF73)

/** Completed-mission / muted indicator. */
val StatusMuted = Color(0xFF8A8178)

/** Destructive / error accent (kept distinct from the Mars accent). */
val StatusError = Color(0xFFE0574B)

// ============== Legacy tokens still referenced by cache-chart/settings UI ==============
// Left in place (recolored to fit the new palette) rather than touched screen-by-screen —
// see CircularChart/SettingsDrawer.
val RedRibbon = Color(0xFFFF2950)
val GrayBac = Color(0xFFE4E4E4)
val MediaRed = AccentMars
val DbPink = Color(0xFFD9A441)
