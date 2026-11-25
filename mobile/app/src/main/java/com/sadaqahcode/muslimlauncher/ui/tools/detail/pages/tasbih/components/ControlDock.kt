package com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.tasbih.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sadaqahcode.muslimlauncher.ui.theme.glass
import com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.tasbih.Actions
import com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.tasbih.DefaulDhikrTargets
import com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.tasbih.TasbihUiState
import com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.tasbih.tr


@Composable
fun ControlDock(
    state: TasbihUiState,
    language: String,
    actions: Actions,
    scheme: ColorScheme,
    modifier: Modifier,
) {
    Row(
        modifier = modifier
            .widthIn(max = 560.dp)
            .fillMaxWidth()
            .glass(RoundedCornerShape(28.dp))
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (state.autoRun) {
            DockAction(
                label = tr(language, "Sebelumnya", "Back"),
                enabled = state.entryIndex > 0,
                onClick = actions.onPrevious,
                scheme = scheme,
                modifier = Modifier.weight(1f),
            )
            ResetControl(state.confirmingReset, state.count > 0, language, actions.onReset)
            DockAction(
                label = if (state.isLastEntry) tr(language, "Selesai", "Finish")
                else tr(language, "Lewati", "Skip"),
                enabled = true,
                onClick = actions.onNext,
                scheme = scheme,
                modifier = Modifier.weight(1f),
            )
        } else {
            SegmentedPicker(
                labels = DefaulDhikrTargets.map { if (it == 0) tr(language, "Bebas", "Free") else "$it" },
                selectedIndex = DefaulDhikrTargets.indexOf(state.freeTarget),
                onSelect = { i -> actions.onSelectTarget(DefaulDhikrTargets[i]) },
                scheme = scheme,
                modifier = Modifier.weight(1f),
            )
            ResetControl(state.confirmingReset, state.count > 0, language, actions.onReset)
        }
    }
}