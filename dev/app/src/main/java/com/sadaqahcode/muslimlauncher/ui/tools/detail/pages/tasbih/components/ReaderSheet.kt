import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sadaqahcode.muslimlauncher.domain.model.tools.Dhikr
import com.sadaqahcode.muslimlauncher.ui.components.ScrimBottomSheet
import com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.tasbih.PillButton
import com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.tasbih.ReaderPage
import com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.tasbih.freeKey
import com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.tasbih.tr

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReaderSheet(
    dhikrs: List<Dhikr>,
    startIndex: Int,
    selectable: Boolean,
    currentKey: String,
    favorites: Set<String>,
    language: String,
    onUse: (Dhikr) -> Unit,
    onToggleFavorite: (Dhikr) -> Unit,
    onDismiss: () -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    val pager = rememberPagerState(
        initialPage = startIndex.coerceIn(0, dhikrs.lastIndex),
    ) { dhikrs.size }
    val page = pager.currentPage.coerceIn(0, dhikrs.lastIndex)
    val shown = dhikrs[page]
    val isCurrent = shown.freeKey() == currentKey
    val isFavorite = shown.freeKey() in favorites

    ScrimBottomSheet(
        onDismiss = onDismiss,
        heightFraction = 0.92f,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, end = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "${page + 1} / ${dhikrs.size}",
                style = MaterialTheme.typography.labelLarge.copy(fontFeatureSettings = "tnum"),
                color = colors.onSurfaceVariant,
                modifier = Modifier.weight(1f),
            )
            if (selectable) {
                FavoriteButton(
                    favorite = isFavorite,
                    language = language,
                    onClick = { onToggleFavorite(shown) },
                )
            }
            IconButton(onClick = onDismiss) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = tr(language, "Tutup", "Close"),
                )
            }
        }

        HorizontalPager(
            state = pager,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
        ) { index ->
            ReaderPage(dhikrs[index], language)
        }

        if (selectable) {
            PillButton(
                label = if (isCurrent) {
                    tr(language, "Sedang dipakai", "In use")
                } else {
                    tr(language, "Pakai dzikir ini", "Use this dhikr")
                },
                filled = !isCurrent,
                enabled = !isCurrent,
                onClick = {
                    onUse(shown)
                    onDismiss()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 12.dp),
            )
        }
    }
}
