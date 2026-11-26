package com.sadaqahcode.muslimlauncher.ui.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBackIos
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.mikepenz.aboutlibraries.ui.compose.android.produceLibraries
import com.mikepenz.aboutlibraries.ui.compose.m3.LibrariesContainer
import com.sadaqahcode.muslimlauncher.R
import com.sadaqahcode.muslimlauncher.ui.common.layout.PageMaxWidthScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LicensesScreen(onBack: () -> Unit) {
    // Android: pass the generated file, do not call with no args
    val libraries by produceLibraries(R.raw.aboutlibraries)

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter,
    ) {
        Scaffold(
            modifier = Modifier
                .widthIn(max = PageMaxWidthScreen)
                .fillMaxSize(),
            topBar = {
                TopAppBar(
                    title = { Text("Open source licenses") },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Outlined.ArrowBackIos, "Back")
                        }
                    },
                )
            },
        ) { padding ->
            LibrariesContainer(
                libraries = libraries,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
            )
        }
    }
}