package com.sadaqahcode.muslimlauncher.ui.tools.detail

import androidx.compose.runtime.Composable
import com.sadaqahcode.muslimlauncher.domain.model.Tool
import com.sadaqahcode.muslimlauncher.domain.model.ToolId
import com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.NotFoundPage
import com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.PlaceholderToolPage
import com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.tasbih.TasbihPage

internal typealias ToolPageContent = @Composable (tool: Tool, onClose: () -> Unit) -> Unit

private val ToolPages: Map<ToolId, ToolPageContent> = mapOf<ToolId, ToolPageContent>(
    ToolId.Tasbih to { tool, onClose -> TasbihPage(tool, onClose) },
)

@Composable
fun ToolDetailScreen(
    tool: Tool?,
    onClose: () -> Unit,
) {
    if (tool == null) {
        NotFoundPage(onClose)
        return
    }
    val page = ToolPages[tool.id]
    if (page != null) page(tool, onClose) else PlaceholderToolPage(tool, onClose)
}