package com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.tasbih.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sadaqahcode.muslimlauncher.domain.model.tools.DhikrItem
import com.sadaqahcode.muslimlauncher.domain.model.tools.name

@Composable
internal fun DhikrPickerSheet(
    items: List<DhikrItem>,
    selectedIndex: Int,
    language: String,
    onSelect: (Int) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(bottom = 24.dp),
    ) {
        itemsIndexed(items) { index, item ->
            val isSelected = index == selectedIndex
            ListItem(
                headlineContent = {
                    Text(
                        text = item.name(language),
                        style = MaterialTheme.typography.titleMedium,
                    )
                },
                supportingContent = {
                    val steps = item.entries.size
                    val total = item.entries.sumOf { it.repetitions }
                    Text(
                        text = if (steps == 1) {
                            "$total repetition${if (total == 1) "" else "s"}"
                        } else {
                            "$steps steps · $total total"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                },
                trailingContent = if (isSelected) {
                    {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Selected",
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }
                } else null,
                modifier = Modifier.clickable { onSelect(index) },
            )
        }
    }
}