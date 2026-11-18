package com.sadaqahcode.muslimlauncher.ui.widgets.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sadaqahcode.muslimlauncher.domain.model.AppTextSize

@Composable
internal fun EmptyWidgetState(
    modifier: Modifier,
    textScale: AppTextSize,
    lc: ColorScheme,
    onAdd: () -> Unit,
) {
    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .heightIn(min = maxHeight)
                .padding(horizontal = 28.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                "No widgets yet.",
                style = textScale.scaled(MaterialTheme.typography.titleMedium),
                color = lc.onBackground,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                "Add tasks, calendar, or others.",
                style = textScale.scaled(MaterialTheme.typography.bodySmall),
                color = lc.onSurfaceVariant,
            )
            Spacer(Modifier.height(20.dp))
            Button(
                onClick = onAdd,
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 400.dp)
                    .heightIn(min = 56.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = lc.background.copy(alpha = 0.80f),
                    contentColor = lc.onPrimaryContainer,
                ),
            ) {
                Text(
                    text = "Add a widget",
                    style = textScale.scaled(MaterialTheme.typography.bodyMedium).copy(
                        fontWeight = FontWeight.Medium
                    ),
                )
            }
        }
    }
}

@Composable
internal fun AddWidgetRow(
    onClick: () -> Unit,
    textScale: AppTextSize,
    lc: ColorScheme
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 36.dp),
        horizontalArrangement = Arrangement.Center,
    ) {
        Button(
            onClick = onClick,
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = lc.background.copy(alpha = 0.80f),
                contentColor = lc.onPrimaryContainer,
            ),
        ) {
            Text(
                text = "+ Add widget",
                style = textScale.scaled(MaterialTheme.typography.bodyMedium).copy(
                    fontWeight = FontWeight.Medium
                ),
            )
        }
    }
}