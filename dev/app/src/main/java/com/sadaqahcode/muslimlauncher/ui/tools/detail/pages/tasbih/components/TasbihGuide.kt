package com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.tasbih.components

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import com.sadaqahcode.muslimlauncher.ui.common.layout.PageMaxWidthScreen
import com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.tasbih.tr
import io.github.tharukack.guidekit.GuideKit
import io.github.tharukack.guidekit.GuideKitAnchor
import io.github.tharukack.guidekit.GuideKitArrowConfig
import io.github.tharukack.guidekit.GuideKitArrowConfigOverride
import io.github.tharukack.guidekit.GuideKitArrowLineStyle
import io.github.tharukack.guidekit.GuideKitInstructionBoxStyle
import io.github.tharukack.guidekit.GuideKitStep
import io.github.tharukack.guidekit.GuideKitStyle
import io.github.tharukack.guidekit.GuideKitTargetHighlightShape
import io.github.tharukack.guidekit.GuideKitTargetHighlightStyle
import io.github.tharukack.guidekit.GuideKitTargetHighlightStyleOverride

@Composable
fun rememberTasbihGuideStyle(
    colors: ColorScheme = MaterialTheme.colorScheme,
): GuideKitStyle = remember(colors) {
    GuideKitStyle(
        // spotlight / arrow (render on scrim, not the card)
        accentColor = colors.primary,
        overlayColor = colors.scrim.copy(alpha = 0.72f),

        // text INSIDE the instruction box (on inverseSurface)
        titleColor = colors.inverseOnSurface,
        descriptionColor = colors.inverseOnSurface.copy(alpha = 0.78f),
        highlightedDescriptionColor = colors.inversePrimary,

        // step dots INSIDE the instruction box
        stepIndicatorActiveColor = colors.inversePrimary,
        stepIndicatorInactiveColor = colors.inverseOnSurface.copy(alpha = 0.30f),

        // primary CTA button INSIDE the instruction box
        primaryButtonContainerColor = colors.inversePrimary,
        primaryButtonContentColor = colors.inverseSurface,

        // skip X icon INSIDE the instruction box
        skipIconTint = colors.inverseOnSurface.copy(alpha = 0.70f),

        arrowConfig = GuideKitArrowConfig(
            lineStyle = GuideKitArrowLineStyle.SpacedDash,
        ),
        targetHighlight = GuideKitTargetHighlightStyle(
            shape = GuideKitTargetHighlightShape.RoundedRect,
        ),

        // the card
        instructionBox = GuideKitInstructionBoxStyle(
            containerColor = colors.inverseSurface,
            contentColor = colors.inverseOnSurface,
            fillMaxWidth = false,
            maxWidth = PageMaxWidthScreen,
        ),
    )
}

@Composable
fun TasbihGuide(
    visible: Boolean,
    language: String,
    libraryPickerBounds: Rect?,
    permanentSidebar: Boolean,
    pickerBounds: Rect?,
    favoriteBounds: Rect?,
    hapticBounds: Rect?,
    counterBounds: Rect?,
    onComplete: () -> Unit,
    modifier: Modifier = Modifier,
    style: GuideKitStyle = rememberTasbihGuideStyle(),
) {
    if (!visible) return

    val steps = remember(
        pickerBounds, libraryPickerBounds, permanentSidebar,
        favoriteBounds, hapticBounds, counterBounds, language,
    ) {
        buildList {
            // step 1: picker (conditional)
            if (permanentSidebar) {
                libraryPickerBounds?.let { bounds ->
                    add(
                        GuideKitStep(
                            targetBounds = bounds,
                            title = tr(language, "Mode dzikir", "Dhikr mode"),
                            description = tr(
                                language,
                                "Pilih dzikir bebas atau rangkaian terpandu.",
                                "Choose free dhikr or a guided routine.",
                            ),
                            arrowConfigOverride = GuideKitArrowConfigOverride(
                                from = GuideKitAnchor.TopRight,
                                to = GuideKitAnchor.BottomCenter,
                            ),
                        )
                    )
                }
            } else {
                pickerBounds?.let { bounds ->
                    add(
                        GuideKitStep(
                            targetBounds = bounds,
                            title = tr(language, "Pilih dzikir", "Choose dhikr"),
                            description = tr(
                                language,
                                "Ketuk di sini untuk membuka perpustakaan dzikir.",
                                "Tap here to open the dhikr library.",
                            ),
                            arrowConfigOverride = GuideKitArrowConfigOverride(
                                from = GuideKitAnchor.TopRight,
                                to = GuideKitAnchor.BottomCenter,
                            ),
                        )
                    )
                }
            }

            // step 2: favorite
            favoriteBounds?.let { bounds ->
                add(
                    GuideKitStep(
                        targetBounds = bounds,
                        title = tr(language, "Favorit", "Favorite"),
                        description = tr(
                            language,
                            "Sematkan dzikir ini agar mudah ditemukan nanti.",
                            "Pin this dhikr so you can find it later.",
                        ),
                        arrowConfigOverride = GuideKitArrowConfigOverride(
                            from = GuideKitAnchor.TopRight,
                            to = GuideKitAnchor.BottomCenter,
                        ),
                    )
                )
            }

            // step 3: haptic
            hapticBounds?.let { bounds ->
                add(
                    GuideKitStep(
                        targetBounds = bounds,
                        title = tr(language, "Umpan balik", "Feedback"),
                        description = tr(
                            language,
                            "Atur getaran atau suara setiap kali Anda menghitung.",
                            "Choose vibration or sound each time you count.",
                        ),
                        arrowConfigOverride = GuideKitArrowConfigOverride(
                            from = GuideKitAnchor.TopRight,
                            to = GuideKitAnchor.BottomCenter,
                        ),
                    )
                )
            }

            // step 4: counter
            counterBounds?.let { bounds ->
                add(
                    GuideKitStep(
                        targetBounds = bounds,
                        title = tr(language, "Hitung", "Count"),
                        description = tr(
                            language,
                            "Ketuk di sini untuk mulai menghitung dzikir.",
                            "Tap here to start counting your dhikr.",
                        ),
                        arrowConfigOverride = GuideKitArrowConfigOverride(
                            from = GuideKitAnchor.TopRight,
                            to = GuideKitAnchor.BottomCenter,
                        ),
                        targetHighlightOverride = GuideKitTargetHighlightStyleOverride(
                            shape = GuideKitTargetHighlightShape.Circle,
                        ),
                    )
                )
            }
        }
    }

    if (steps.isEmpty()) return

    GuideKit(
        steps = steps,
        style = style,
        onSkipped = onComplete,
        onFinished = onComplete,
        modifier = modifier,
    )
}