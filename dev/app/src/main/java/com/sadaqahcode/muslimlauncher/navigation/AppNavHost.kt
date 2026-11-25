package com.sadaqahcode.muslimlauncher.navigation

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
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
import com.sadaqahcode.muslimlauncher.ui.settings.LicensesScreen
import com.sadaqahcode.muslimlauncher.ui.settings.SettingsScreen
import com.sadaqahcode.muslimlauncher.ui.tools.detail.ToolDetailScreen
import kotlinx.coroutines.flow.StateFlow

@Composable
fun AppNavHost(
    homePressCount: StateFlow<Long>,
) {
    val navController = rememberNavController()

    LaunchedEffect(navController) {
        var lastSeen = homePressCount.value
        homePressCount.collect { count ->
            if (count != lastSeen) {
                lastSeen = count
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
            fadeIn(tween(220, easing = FastOutSlowInEasing)) +
                    slideInVertically(
                        animationSpec = tween(220, easing = FastOutSlowInEasing),
                        initialOffsetY = { fullHeight -> fullHeight / 20 } // Small upward drift from center
                    ) +
                    scaleIn(
                        animationSpec = tween(220, easing = FastOutSlowInEasing),
                        initialScale = 0.94f,
                        transformOrigin = TransformOrigin(0.5f, 0f) // Anchored to top-center
                    )
        },
        exitTransition = {
            fadeOut(tween(160, easing = FastOutSlowInEasing))
        },
        popEnterTransition = {
            fadeIn(tween(220, easing = FastOutSlowInEasing)) +
                    slideInVertically(
                        animationSpec = tween(220, easing = FastOutSlowInEasing),
                        initialOffsetY = { fullHeight -> fullHeight / 20 }
                    ) +
                    scaleIn(
                        animationSpec = tween(220, easing = FastOutSlowInEasing),
                        initialScale = 0.94f,
                        transformOrigin = TransformOrigin(0.5f, 0f)
                    )
        },
        popExitTransition = {
            fadeOut(tween(160, easing = FastOutSlowInEasing))
        }
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
                onOpenLicenses = {
                    navController.navigate(AppRoutes.LICENSES) {
                        launchSingleTop = true
                    }
                },
            )
        }
        composable(AppRoutes.LICENSES) {
            LicensesScreen(
                onBack = { navController.popBackStack() },
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