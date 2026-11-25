package com.sadaqahcode.muslimlauncher.ui.common.layout

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class DeviceClass { Phone, MediumTablet, LargeTablet }

object ScreenTopPadding {

    private const val PHONE_MAX_DP  = 600
    private const val MEDIUM_MAX_DP = 840

    private val BreathPhonePortrait   = 4.dp
    private val BreathPhoneLandscape  = 0.dp
    private val BreathMediumPortrait  = 12.dp
    private val BreathMediumLandscape = 6.dp
    private val BreathLargePortrait   = 20.dp
    private val BreathLargeLandscape  = 10.dp

    private val FallbackStatusBar = 0.dp

    fun deviceClass(shortestSideDp: Int): DeviceClass = when {
        shortestSideDp < PHONE_MAX_DP  -> DeviceClass.Phone
        shortestSideDp < MEDIUM_MAX_DP -> DeviceClass.MediumTablet
        else                           -> DeviceClass.LargeTablet
    }

    private fun breathFor(device: DeviceClass, isLandscape: Boolean): Dp = when (device) {
        DeviceClass.Phone        -> if (isLandscape) BreathPhoneLandscape   else BreathPhonePortrait
        DeviceClass.MediumTablet -> if (isLandscape) BreathMediumLandscape  else BreathMediumPortrait
        DeviceClass.LargeTablet  -> if (isLandscape) BreathLargeLandscape   else BreathLargePortrait
    }

    @Composable
    fun rememberTopPadding(screen: ScreenLayout): Dp {
        val density = LocalDensity.current
        val statusBar = with(density) {
            WindowInsets.statusBars.getTop(density).toDp()
        }.takeIf { it > 0.dp } ?: FallbackStatusBar
        return resolve(screen, statusBar)
    }

    fun resolve(screen: ScreenLayout): Dp =
        resolve(screen, FallbackStatusBar)

    fun resolve(screen: ScreenLayout, statusBarInset: Dp): Dp {
        val breath = breathFor(deviceClass(screen.shortSide.toInt()), screen.isLandscape)
        return statusBarInset + breath
    }
}