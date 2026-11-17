package com.sadaqahcode.muslimlauncher.ui.settings

import android.content.Context
import android.graphics.BitmapFactory
import androidx.core.graphics.ColorUtils
import com.flagodna.debugprint.debugPrint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.core.graphics.toColorInt
import androidx.core.net.toUri

private const val TAG = "WallpaperPresets"
private const val DARK_LUMINANCE_THRESHOLD = 0.45f
private const val TEXT_REGION_FRACTION = 0.40f

internal data class ColorPreset(val name: String, val hex: String)
internal data class GradientPreset(val name: String, val start: String, val end: String)

internal fun isDarkColor(hex: String): Boolean {
    val parsed = runCatching { hex.toColorInt() }.getOrNull()
        ?: return false
    return ColorUtils.calculateLuminance(parsed) < DARK_LUMINANCE_THRESHOLD
}

internal fun isDarkGradient(start: String, end: String): Boolean {
    val startL = runCatching {
        ColorUtils.calculateLuminance(start.toColorInt())
    }.getOrNull() ?: return false
    val endL = runCatching {
        ColorUtils.calculateLuminance(end.toColorInt())
    }.getOrNull() ?: return false
    return (startL + endL) / 2f < DARK_LUMINANCE_THRESHOLD
}

internal suspend fun isDarkImage(context: Context, uriString: String): Boolean =
    withContext(Dispatchers.IO) {
        runCatching {
            val uri = uriString.toUri()
            val opts = BitmapFactory.Options().apply { inSampleSize = 8 }
            val bmp = context.contentResolver.openInputStream(uri)?.use {
                BitmapFactory.decodeStream(it, null, opts)
            } ?: return@runCatching true

            val sampleHeight = (bmp.height * TEXT_REGION_FRACTION)
                .toInt()
                .coerceAtLeast(1)

            val pixels = IntArray(bmp.width * sampleHeight)
            bmp.getPixels(pixels, 0, bmp.width, 0, 0, bmp.width, sampleHeight)
            debugPrint.d("$TAG: sampled ${bmp.width}×$sampleHeight = ${pixels.size} px")
            bmp.recycle()

            if (pixels.isEmpty()) return@runCatching true

            val avg = pixels.sumOf { ColorUtils.calculateLuminance(it) } / pixels.size
            val isDark = avg < DARK_LUMINANCE_THRESHOLD
            debugPrint.d(
                "$TAG: avg=${"%.4f".format(avg)} " +
                        "threshold=$DARK_LUMINANCE_THRESHOLD → isDark=$isDark"
            )
            isDark
        }.getOrElse { e ->
            debugPrint.e("$TAG: ${e::class.simpleName}: ${e.message} → default dark")
            true
        }
    }

// Wallpaper BACKGROUND colors

internal val ColorPresets: List<ColorPreset> = listOf(
    ColorPreset("Alabaster", "#FFF5F1EA"),
    ColorPreset("Sand",      "#FFE8D5B7"),
    ColorPreset("Olive",     "#FF8B9556"),
    ColorPreset("Teal",      "#FF2D5F5D"),
    ColorPreset("Indigo",    "#FF3D4A78"),
    ColorPreset("Midnight",  "#FF1F2937"),
    ColorPreset("Terra",     "#FFB8654B"),
    ColorPreset("Rose",      "#FFC08081"),
    ColorPreset("Slate",     "#FF475569"),
    ColorPreset("Charcoal",  "#FF374151"),
    ColorPreset("Amoled",    "#FF000000"),
)

// Wallpaper GRADIENTS
internal val GradientPresets: List<GradientPreset> = listOf(
    GradientPreset("Charcoal",       "#FF232526", "#FF414345"),
    GradientPreset("Olive Grove",    "#FF1E2A0E", "#FF606C38"),
    GradientPreset("Sandstorm",      "#FF2E1F10", "#FF8B6F47"),
    GradientPreset("Rose Gold",      "#FF241A2E", "#FFB993D6"),
    GradientPreset("Emerald",        "#FF134E5E", "#FF71B280"),
    GradientPreset("Night Sky",      "#FF0F2027", "#FF2C5364"),
    GradientPreset("Dusk",           "#FF2C3E50", "#FF4CA1AF"),
    GradientPreset("Dawn",           "#FF2B1A2E", "#FFFFB88C"),
    GradientPreset("Deep Ocean",     "#FF0B1929", "#FF1E3A5F"),
    GradientPreset("Plum Noir",      "#FF1A0B2E", "#FF3D1E5F"),
    GradientPreset("Forest Night",   "#FF0D1F14", "#FF2D4A3A"),
    GradientPreset("Copper Dusk",    "#FF3B1E10", "#FFB0651A"),
    GradientPreset("Slate Steel",    "#FF1E2229", "#FF4A5058"),
    GradientPreset("Midnight Teal",  "#FF0A1F24", "#FF1B6B6B"),
    GradientPreset("Royal Indigo",   "#FF120A2E", "#FF4A2E8A"),
    GradientPreset("Crimson Night",  "#FF1E0A0E", "#FF8A2E3A"),
    GradientPreset("Sapphire Deep",  "#FF0A1428", "#FF1F5FA8"),
    GradientPreset("Amber Noir",     "#FF1A120A", "#FFB07A1E"),
    GradientPreset("Violet Storm",   "#FF1A0F24", "#FF6B3FA0"),
    GradientPreset("Tropical Sunset","#FFFF512F", "#FFDD2476"),
    GradientPreset("Clear Sky",      "#FF2193B0", "#FF6DD5ED"),
    GradientPreset("Autumn Leaves",  "#FFCB2D3E", "#FFF06B1D"),
    GradientPreset("Spring Meadow",  "#FF56AB2F", "#FFA8E063"),
    GradientPreset("Desert Dune",    "#FFEDC79B", "#FFE6A15D"),
    GradientPreset("Cherry Blossom", "#FFFFAFBD", "#FFFFC3A0"),
    GradientPreset("Aurora Borealis","#FF00C9FF", "#FF92FE9D"),
    GradientPreset("Coral Reef",     "#FFFF6B6B", "#FF556270"),
    GradientPreset("Misty Mountain", "#FF757F9A", "#FFD7DDE8"),
    GradientPreset("Golden Hour",    "#FFFFE000", "#FF799A08"),
    GradientPreset("Cyberpunk",      "#FFFF007F", "#FF00FFFF"),
    GradientPreset("Synthwave",      "#FF8A2BE2", "#FF00BFFF"),
    GradientPreset("Lava Flow",      "#FFFF4500", "#FFFF0000"),
    GradientPreset("Acid Drop",      "#FF39FF14", "#FFFFFF00"),
    GradientPreset("Electric Dream", "#FF00F260", "#FF0575E6"),
    GradientPreset("Neon Nights",    "#FF12C2E9", "#FF22C1ED"),
    GradientPreset("Vaporwave",      "#FFFF6AD5", "#FFC774E8"),
    GradientPreset("Radioactive",    "#FFDFFF00", "#FF00FF00"),
    GradientPreset("Liquid Gold",    "#FFF7971E", "#FFFFD200"),
    GradientPreset("Velvet Rose",    "#FFEE9CA5", "#FF5F2C82"),
    GradientPreset("Burnished Bronze","#FF603813", "#FFB29F94"),
    GradientPreset("Platinum",       "#FFF5F5F5", "#FFD3D3D3"),
    GradientPreset("Champagne",      "#FFFFF8E7", "#FFF0E6D2"),
    GradientPreset("Midnight Sapphire","#FF0F2027", "#FF203A43"),
    GradientPreset("Ruby Elegance",  "#FF8E0E00", "#FF4A0000"),
    GradientPreset("Silver Fox",     "#FF757F9A", "#FF434A54"),
    GradientPreset("Cotton Candy",   "#FFFA71CD", "#FFA18CD1"),
    GradientPreset("Mint Choc",      "#FF00B09B", "#FF96C93D"),
    GradientPreset("Berry Blast",    "#FF8E2DE2", "#FF4A00E0"),
    GradientPreset("Peach Fizz",     "#FFFF9A9E", "#FFFAD0C4"),
    GradientPreset("Lemon Drop",     "#FFFAD961", "#FFF76B1C"),
    GradientPreset("Blue Raspberry", "#FF0052D4", "#FF6FB1FC"),
)

// Color source SEED PALETTE (for CustomColor)
internal val ColorPalletePresets: List<ColorPreset> = listOf(
    ColorPreset("Midnight", "#FF1F2937"), // Deep blue-grey
    ColorPreset("Charcoal", "#FF374151"), // Dark grey
    ColorPreset("Slate",    "#FF475569"), // Cool medium grey
    ColorPreset("Graphite", "#FF6B7280"), // True neutral grey
    ColorPreset("Silver",   "#FF9CA3AF"), // Light neutral grey
    ColorPreset("Pearl",    "#FFF3F4F6"), // Core off-white
    ColorPreset("Crimson",  "#FFC62828"), // Strong, classic red
    ColorPreset("Ruby",     "#FFE11D48"), // Vibrant, modern red
    ColorPreset("Fuchsia",  "#FFD81B60"), // Bold, vivid pink
    ColorPreset("Rose",     "#FFC08081"), // Muted, dusty rose
    ColorPreset("Blush",    "#FFF48FB1"), // Soft, warm pink
    ColorPreset("Terra",    "#FFB8654B"), // Earthy terracotta
    ColorPreset("Tangerine","#FFFF7043"), // Core vibrant orange
    ColorPreset("Saffron",  "#FFFF8F00"), // Deep, rich orange
    ColorPreset("Amber",    "#FFF9A825"), // Warm, glowing yellow-orange
    ColorPreset("Gold",     "#FFFFB300"), // Core metallic gold
    ColorPreset("Forest",   "#FF2E7D32"), // Core deep green
    ColorPreset("Emerald",  "#FF10B981"), // Core vibrant green
    ColorPreset("Jade",     "#FF26A69A"), // Soft, calming green-blue
    ColorPreset("Teal",     "#FF2D5F5D"), // Dark blue-green
    ColorPreset("Mint",     "#FF66BB6A"), // Fresh, light green
    ColorPreset("Olive",    "#FF8B9556"), // Muted, earthy green
    ColorPreset("Navy",     "#FF1A237E"), // Core deep dark blue
    ColorPreset("Indigo",   "#FF3D4A78"), // Deep blue with purple undertones
    ColorPreset("Cobalt",   "#FF1976D2"), // Strong, vivid blue
    ColorPreset("Azure",    "#FF03A9F4"), // Core bright sky blue
    ColorPreset("Cyan",     "#FF26C6DA"), // Bright blue-green
    ColorPreset("Plum",     "#FF7B1FA2"), // Core deep purple
    ColorPreset("Amethyst", "#FF7E57C2"), // Core medium purple
    ColorPreset("Violet",   "#FF8E24AA"), // Core bright purple
)
