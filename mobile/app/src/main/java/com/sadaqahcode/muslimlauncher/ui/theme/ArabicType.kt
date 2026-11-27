package com.sadaqahcode.muslimlauncher.ui.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.sp
import com.sadaqahcode.muslimlauncher.R


val ArabicFont = FontFamily(
    Font(R.font.amiri_regular, FontWeight.Normal),
)

private val ArabicLineHeight = LineHeightStyle(
    alignment = LineHeightStyle.Alignment.Center,
    trim = LineHeightStyle.Trim.None,
)

private const val ARABIC_FEATURES = "\"calt\" 1, \"liga\" 1, \"mark\" 1, \"mkmk\" 1"


private fun arabic(size: Int, lineHeightRatio: Float): TextStyle = TextStyle(
    fontFamily = ArabicFont,
    fontWeight = FontWeight.Normal,
    fontSize = size.sp,
    lineHeight = (size * lineHeightRatio).sp,
    letterSpacing = 0.sp,
    lineHeightStyle = ArabicLineHeight,
    fontFeatureSettings = ARABIC_FEATURES,
    textDirection = TextDirection.Rtl,
)

object ArabicType {

    // ── DISPLAY — hero moments, single-line by design ─────────────────
    val displayLarge  = arabic(size = 72, lineHeightRatio = 1.90f)
    val displayMedium = arabic(size = 64, lineHeightRatio = 1.90f)
    val displaySmall  = arabic(size = 52, lineHeightRatio = 1.95f)

    // ── HEADLINE — section ceremony; the dhikr / salawat sweet spot ───
    val headlineLarge  = arabic(size = 44, lineHeightRatio = 2.05f)
    val headlineMedium = arabic(size = 40, lineHeightRatio = 2.10f)
    val headlineSmall  = arabic(size = 36, lineHeightRatio = 2.15f)

    // ── TITLE — card / block titles ───────────────────────────────────
    val titleLarge  = arabic(size = 32, lineHeightRatio = 2.20f)
    val titleMedium = arabic(size = 28, lineHeightRatio = 2.25f)
    val titleSmall  = arabic(size = 26, lineHeightRatio = 2.25f)

    // ── BODY — running Arabic text (paragraphs need the most air) ─────
    val bodyLarge  = arabic(size = 24, lineHeightRatio = 2.30f)
    val bodyMedium = arabic(size = 22, lineHeightRatio = 2.35f)
    val bodySmall  = arabic(size = 20, lineHeightRatio = 2.40f)

    // ── LABEL — smallest functional Arabic ────────────────────────────
    val labelLarge  = arabic(size = 20, lineHeightRatio = 2.40f)
    val labelMedium = arabic(size = 18, lineHeightRatio = 2.45f)
    val labelSmall  = arabic(size = 16, lineHeightRatio = 2.50f)
}
