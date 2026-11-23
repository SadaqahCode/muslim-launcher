package com.sadaqahcode.muslimlauncher.navigation

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.sadaqahcode.muslimlauncher.domain.model.ToolId
import com.sadaqahcode.muslimlauncher.domain.model.ToolRegistry
import com.sadaqahcode.muslimlauncher.domain.model.WallpaperTextMode
import com.sadaqahcode.muslimlauncher.ui.home.HomeScreen
import com.sadaqahcode.muslimlauncher.ui.home.HomeViewModel
import com.sadaqahcode.muslimlauncher.ui.settings.SettingsScreen
import com.sadaqahcode.muslimlauncher.ui.tools.detail.ToolDetailScreen
import kotlinx.coroutines.flow.StateFlow

private const val NAV_DURATION = 320
private const val FADE_DURATION = 220
private val navEasing = CubicBezierEasing(0.0f, 0.0f, 0.2f, 1.0f)

@Composable
fun AppNavHost(
    homePressCount: StateFlow<Long>,
) {
    val navController = rememberNavController()

    // Center dot / Home pill while inside a tool -> close to Home
    LaunchedEffect(navController) {
        var lastSeen = homePressCount.value
        homePressCount.collect { count ->
            if (count != lastSeen) {
                lastSeen = count
                // Already on HOME = no-op. else = pop to HOME.
                navController.popBackStack(AppRoutes.HOME, inclusive = false)
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = AppRoutes.HOME,
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        enterTransition = {
            slideInHorizontally(tween(NAV_DURATION, easing = navEasing)) { 24 } +
                    fadeIn(tween(FADE_DURATION)) +
                    scaleIn(tween(NAV_DURATION, easing = navEasing), initialScale = 0.98f)
        },
        exitTransition = {
            slideOutHorizontally(tween(NAV_DURATION, easing = navEasing)) { -24 } +
                    fadeOut(tween(FADE_DURATION)) +
                    scaleOut(tween(NAV_DURATION, easing = navEasing), targetScale = 0.98f)
        },
        popEnterTransition = {
            slideInHorizontally(tween(NAV_DURATION, easing = navEasing)) { -24 } +
                    fadeIn(tween(FADE_DURATION)) +
                    scaleIn(tween(NAV_DURATION, easing = navEasing), initialScale = 0.98f)
        },
        popExitTransition = {
            slideOutHorizontally(tween(NAV_DURATION, easing = navEasing)) { 24 } +
                    fadeOut(tween(FADE_DURATION)) +
                    scaleOut(tween(NAV_DURATION, easing = navEasing), targetScale = 0.98f)
        },
    ) {
        composable(AppRoutes.HOME) {
            val vm: HomeViewModel = hiltViewModel()

            HomeScreen(
                viewModel = vm,
                onOpenSettings = {
                    navController.navigate(AppRoutes.SETTINGS) {
                        launchSingleTop = true
                    }
                }, 
                onOpenTool = { toolId, wallpaperIsDark, wallpaperTextMode, needsHalo, adaptToWallpaper ->
                    navController.navigate(
                        AppRoutes.toolDetail(
                            toolId = toolId,
                            wallpaperIsDark = wallpaperIsDark,
                            wallpaperTextMode = wallpaperTextMode,
                            needsHalo = needsHalo,
                            adaptToWallpaper = adaptToWallpaper,
                        )
                    ) {
                        launchSingleTop = true
                    }
                },
            )
        }
        composable(AppRoutes.SETTINGS) {
            SettingsScreen(
                onClose = { navController.popBackStack() },
            )
        }
        composable(
            route = AppRoutes.TOOL_DETAIL,
            arguments = listOf(
                navArgument("toolId") { type = NavType.StringType },
                navArgument("dark") { type = NavType.BoolType; defaultValue = false },
                navArgument("mode") { type = NavType.StringType; defaultValue = WallpaperTextMode.Auto.name },
                navArgument("halo") { type = NavType.BoolType; defaultValue = true },
                navArgument("adapt") { type = NavType.BoolType; defaultValue = true },
            ),
        ) { backStackEntry ->
            val toolKey = backStackEntry.arguments?.getString("toolId")
            val toolId = ToolId.entries.firstOrNull { it.key == toolKey }
            val tool = toolId?.let { ToolRegistry.byId[it] }

            val modeRaw = backStackEntry.arguments?.getString("mode")
            val mode = runCatching { WallpaperTextMode.valueOf(modeRaw ?: "Auto") }
                .getOrDefault(WallpaperTextMode.Auto)

            ToolDetailScreen(
                tool = tool,
                onClose = { navController.popBackStack() },
                wallpaperIsDark = backStackEntry.arguments?.getBoolean("dark") ?: false,
                wallpaperTextMode = mode,
                needsHalo = backStackEntry.arguments?.getBoolean("halo") ?: true,
                adaptToWallpaper = backStackEntry.arguments?.getBoolean("adapt") ?: true,
            )
        }
    }
}