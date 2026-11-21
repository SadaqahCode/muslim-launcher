package com.sadaqahcode.muslimlauncher.ui.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.res.stringResource
import com.sadaqahcode.muslimlauncher.R

// Sections — start with Snackbar now, add others
@Immutable
data class HomeSnackbarStrings(
    val defaultLauncherMsg: String,
    val defaultLauncherAction: String,
)

@Composable
fun rememberHomeSnackbarStrings(): HomeSnackbarStrings = HomeSnackbarStrings(
    defaultLauncherMsg = stringResource(R.string.home_set_default_launcher),
    defaultLauncherAction = stringResource(R.string.home_set_default_launcher_action),
)

val LocalHomeSnackbarStrings = staticCompositionLocalOf<HomeSnackbarStrings> {
    error("LocalHomeSnackbarStrings not provided — wrap Home content in CompositionLocalProvider")
}