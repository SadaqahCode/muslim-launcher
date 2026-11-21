package com.sadaqahcode.muslimlauncher.navigation

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.sadaqahcode.muslimlauncher.domain.model.Tool
import com.sadaqahcode.muslimlauncher.domain.model.ToolId
import com.sadaqahcode.muslimlauncher.domain.model.ToolRegistry
import com.sadaqahcode.muslimlauncher.ui.home.HomeScreen
import com.sadaqahcode.muslimlauncher.ui.home.HomeViewModel
import com.sadaqahcode.muslimlauncher.ui.settings.SettingsScreen
import com.sadaqahcode.muslimlauncher.ui.tools.detail.ToolDetailScreen

private const val NAV_DURATION = 280
private val navEasing = FastOutSlowInEasing

@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AppRoutes.HOME,
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),

        enterTransition = {
            slideInHorizontally(tween(NAV_DURATION, easing = navEasing)) { it } +
                    fadeIn(tween(NAV_DURATION))
        },
        exitTransition = {
            slideOutHorizontally(tween(NAV_DURATION, easing = navEasing)) { -it / 4 } +
                    fadeOut(tween(NAV_DURATION))
        },
        popEnterTransition = {
            slideInHorizontally(tween(NAV_DURATION, easing = navEasing)) { -it / 4 } +
                    fadeIn(tween(NAV_DURATION))
        },
        popExitTransition = {
            slideOutHorizontally(tween(NAV_DURATION, easing = navEasing)) { it } +
                    fadeOut(tween(NAV_DURATION))
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
                onOpenTool = { toolId ->
                    navController.navigate(AppRoutes.toolDetail(toolId)) {
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
                navArgument("toolId") { type = NavType.StringType }
            ),
        ) { backStackEntry ->
            val toolKey: String? = backStackEntry.arguments?.getString("toolId")
            val toolId: ToolId? = ToolId.entries.firstOrNull { item -> item.key == toolKey }
            val tool: Tool? = toolId?.let { id -> ToolRegistry.byId[id] }

            ToolDetailScreen(
                tool = tool,
                onClose = { navController.popBackStack() },
            )
        }
    }
}