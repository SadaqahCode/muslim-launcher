package com.sadaqahcode.muslimlauncher.navigation

import com.sadaqahcode.muslimlauncher.domain.model.ToolId

object AppRoutes {
    const val HOME = "home"
    const val SETTINGS = "settings"

    const val TOOL_DETAIL = "tools/detail/{toolId}"

    fun toolDetail(toolId: ToolId): String {
        return "tools/detail/${toolId.key}"
    }

    fun toolDetail(toolKey: String): String {
        return "tools/detail/$toolKey"
    }
}