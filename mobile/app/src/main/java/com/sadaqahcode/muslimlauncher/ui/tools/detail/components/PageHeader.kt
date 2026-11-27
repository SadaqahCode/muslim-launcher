package com.sadaqahcode.muslimlauncher.ui.tools.detail.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBackIos
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.tasbih.tr


@Composable
fun PageHeader(
    language: String,
    onClose: () -> Unit,
    showSidebar: Boolean,
    scheme: ColorScheme,
    content: @Composable RowScope.() -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 64.dp)
            .padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onClose) {
            if(showSidebar){
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                    tint = scheme.onBackground,
                )
            }else{
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBackIos,
                    contentDescription = tr(language, "Kembali", "Back"),
                    tint = scheme.onBackground,
                )
            }
        }
        content()
    }
}
