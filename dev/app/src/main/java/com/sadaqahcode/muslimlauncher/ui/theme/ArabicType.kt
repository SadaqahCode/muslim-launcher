package com.sadaqahcode.muslimlauncher.ui.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.sp
import com.sadaqahcode.muslimlauncher.R


val KfgqpcHafs = FontFamily(
    Font(R.font.kfgqpc_uthmanic_hafs, FontWeight.Normal),
)

private val ArabicLineHeight = LineHeightStyle(
    alignment = LineHeightStyle.Alignment.Center,
    trim = LineHeightStyle.Trim.None,
)

private const val ARABIC_FEATURES = "\"calt\" 1, \"liga\" 1, \"mark\" 1, \"mkmk\" 1"


private fun arabic(size: Int, lineHeight: Int): TextStyle = TextStyle(
    fontFamily = KfgqpcHafs,
    fontWeight = FontWeight.Normal,
    fontSize = size.sp,
    lineHeight = lineHeight.sp,
    letterSpacing = 0.sp,
    lineHeightStyle = ArabicLineHeight,
    fontFeatureSettings = ARABIC_FEATURES,
)
object ArabicType {

    // ── DISPLAY — hero moments ────────────────────────────────────────
    val displayLarge  = arabic(size = 72, lineHeight = 130)
    val displayMedium = arabic(size = 64, lineHeight = 116)
    val displaySmall  = arabic(size = 52, lineHeight = 94)

    // ── HEADLINE — section ceremony ───────────────────────────────────
    val headlineLarge  = arabic(size = 44, lineHeight = 80)
    val headlineMedium = arabic(size = 40, lineHeight = 72)
    val headlineSmall  = arabic(size = 36, lineHeight = 66)

    // ── TITLE — card / block titles ───────────────────────────────────
    val titleLarge  = arabic(size = 32, lineHeight = 60)
    val titleMedium = arabic(size = 28, lineHeight = 52)
    val titleSmall  = arabic(size = 26, lineHeight = 48)

    // ── BODY — running Arabic text ────────────────────────────────────
    val bodyLarge  = arabic(size = 24, lineHeight = 44)
    val bodyMedium = arabic(size = 22, lineHeight = 40)
    val bodySmall  = arabic(size = 20, lineHeight = 36)

    // ── LABEL — smallest functional Arabic ────────────────────────────
    val labelLarge  = arabic(size = 20, lineHeight = 36)
    val labelMedium = arabic(size = 18, lineHeight = 32)
    val labelSmall  = arabic(size = 16, lineHeight = 28)
}