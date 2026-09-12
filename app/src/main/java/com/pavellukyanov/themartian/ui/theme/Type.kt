package com.pavellukyanov.themartian.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.pavellukyanov.themartian.R

val GolosFontFamily = FontFamily(
    Font(R.font.golos_regular, FontWeight.Normal),
    Font(R.font.golos_medium, FontWeight.Medium),
    Font(R.font.golos_semibold, FontWeight.SemiBold),
    Font(R.font.golos_bold, FontWeight.Bold)
)

val MonoFontFamily = FontFamily(
    Font(R.font.jetbrains_mono_regular, FontWeight.Normal),
    Font(R.font.jetbrains_mono_medium, FontWeight.Medium),
    Font(R.font.jetbrains_mono_semibold, FontWeight.SemiBold),
    Font(R.font.jetbrains_mono_bold, FontWeight.Bold)
)

object MartianType {
    val ScreenTitle = TextStyle(fontFamily = GolosFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 22.sp, letterSpacing = (-0.2).sp)
    val CardTitle = TextStyle(fontFamily = GolosFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 21.sp, letterSpacing = (-0.2).sp)
    val Body = TextStyle(fontFamily = GolosFontFamily, fontWeight = FontWeight.Medium, fontSize = 14.sp)
    val BodySmall = TextStyle(fontFamily = GolosFontFamily, fontWeight = FontWeight.Medium, fontSize = 13.sp)

    val Kicker = TextStyle(fontFamily = MonoFontFamily, fontWeight = FontWeight.Medium, fontSize = 11.sp, letterSpacing = 1.6.sp)
    val MonoLabel = TextStyle(fontFamily = MonoFontFamily, fontWeight = FontWeight.Medium, fontSize = 9.sp, letterSpacing = 1.sp)
    val MonoValue = TextStyle(fontFamily = MonoFontFamily, fontWeight = FontWeight.Normal, fontSize = 13.sp)
    val MonoCaption = TextStyle(fontFamily = MonoFontFamily, fontWeight = FontWeight.Normal, fontSize = 10.sp)
    val MonoTag = TextStyle(fontFamily = MonoFontFamily, fontWeight = FontWeight.Medium, fontSize = 11.sp, letterSpacing = 0.5.sp)
}

val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = GolosFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp
    )
)
