package com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.tasbih.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sadaqahcode.muslimlauncher.domain.model.tools.Dhikr
import com.sadaqahcode.muslimlauncher.domain.model.tools.text
import com.sadaqahcode.muslimlauncher.ui.theme.ArabicType
import com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.tasbih.displayTitle
import com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.tasbih.latinText
import com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.tasbih.referenceText

@Composable
fun ReaderPage(d: Dhikr, language: String) {
    val colors = MaterialTheme.colorScheme
    val title = d.displayTitle(language)
    val arabic = d.arabic
    val latin = d.latinText()
    val meaning = d.text(language)
    val reference = d.referenceText()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = colors.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        if (arabic.isNotEmpty()) {
            Text(
                text = arabic,
                style = if (arabic.length < 80) ArabicType.headlineMedium else ArabicType.headlineSmall,
                color = colors.onSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),

            )
        }
        if (latin.isNotEmpty()) {
            Text(
                text = latin,
                style = MaterialTheme.typography.bodyMedium,
                fontStyle = FontStyle.Italic,
                color = colors.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
        if (!meaning.isNullOrEmpty()) {
            Text(
                text = meaning,
                style = MaterialTheme.typography.bodyLarge,
                color = colors.onSurface,
                textAlign = TextAlign.Center,
            )
        }
        if (reference.isNotEmpty()) {
            Text(
                text = reference,
                style = MaterialTheme.typography.labelMedium,
                color = colors.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
        Spacer(Modifier.height(8.dp))
    }
}