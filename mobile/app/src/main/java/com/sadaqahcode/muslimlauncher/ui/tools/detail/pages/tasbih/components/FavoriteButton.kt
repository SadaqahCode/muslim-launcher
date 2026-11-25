package com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.tasbih.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.tasbih.tr


@Composable
fun FavoriteButton(
    favorite: Boolean,
    language: String,
    scheme: ColorScheme,
    onClick: () -> Unit
) {
    IconButton(onClick = onClick) {
        Icon(
            imageVector = if (favorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
            contentDescription = if (favorite) {
                tr(language, "Lepas dari favorit", "Remove from favorites")
            } else {
                tr(language, "Sematkan ke favorit", "Pin to favorites")
            },
            tint = if (favorite) scheme.onSurface else scheme.onSurfaceVariant,
        )
    }
}