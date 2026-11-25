package com.sadaqahcode.muslimlauncher.ui.home.strings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.res.stringResource
import com.sadaqahcode.muslimlauncher.R

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