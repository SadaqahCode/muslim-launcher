package com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.tasbih.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.sadaqahcode.muslimlauncher.domain.model.tools.dhikr.Dhikr
import com.sadaqahcode.muslimlauncher.domain.model.tools.dhikr.text
import com.sadaqahcode.muslimlauncher.domain.model.tools.dhikr.title
import com.sadaqahcode.muslimlauncher.ui.theme.ArabicType


@Composable
fun WordsText(
    d: Dhikr,
    language: String,
    onOverflow: (Boolean) -> Unit,
    scheme: ColorScheme,
) {
    val arabic = d.arabic
    val title = d.title(language)
    val meaning = d.text(language)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        if (title.isNotEmpty()) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                color = scheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
        if (arabic.isNotEmpty()) {
            Text(
                text = arabic,
                style = if (arabic.length < 60) ArabicType.headlineMedium else ArabicType.headlineSmall,
                color = scheme.onSurface,
                textAlign = TextAlign.Center,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis,
                onTextLayout = { onOverflow(it.hasVisualOverflow) },
            )
        }
        if (!meaning.isNullOrEmpty()) {
            Text(
                text = meaning,
                style = MaterialTheme.typography.bodyMedium,
                color = scheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                onTextLayout = { onOverflow(it.hasVisualOverflow) },
            )
        }
    }
}
