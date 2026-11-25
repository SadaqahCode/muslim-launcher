package com.sadaqahcode.muslimlauncher.domain.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Apps
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.Work
import androidx.compose.ui.graphics.vector.ImageVector

internal val DrawerCategoryOrder = listOf(
    AppCategory.DEEN,
    AppCategory.DUNYA_NECESSARY,
    AppCategory.DUNYA_OPTIONAL,
    AppCategory.FITNAH_RISK,
)

internal data class DrawerChip(val filter: AppCategory?, val count: Int)

internal data class DrawerSection(val category: AppCategory?, val apps: List<LauncherApp>)

internal data class DrawerModel(
    val chips: List<DrawerChip>,
    val selected: AppCategory?,
    val sections: List<DrawerSection>,
)

internal fun AppCategory.chipLabel(): String = when (this) {
    AppCategory.DEEN -> "Deen"
    AppCategory.DUNYA_NECESSARY -> "Necessary"
    AppCategory.DUNYA_OPTIONAL -> "Optional"
    AppCategory.FITNAH_RISK -> "Fitnah risk"
}

internal val AllAppsIcon: ImageVector get() = Icons.Outlined.Apps

internal val AppCategory.icon: ImageVector
    get() = when (this) {
        AppCategory.DEEN -> Icons.Outlined.Book
        AppCategory.DUNYA_NECESSARY -> Icons.Outlined.Work
        AppCategory.DUNYA_OPTIONAL -> Icons.Outlined.ShoppingBag
        AppCategory.FITNAH_RISK -> Icons.Outlined.Visibility
    }
internal fun buildDrawerModel(
    apps: List<LauncherApp>,
    query: String,
    filter: AppCategory?,
): DrawerModel {
    if (query.isNotBlank()) {
        return DrawerModel(
            chips = listOf(DrawerChip(filter = null, count = apps.size)),
            selected = null,
            sections = listOf(DrawerSection(category = null, apps = apps)),
        )
    }

    val byCategory = apps.groupBy { it.category }
    val present = DrawerCategoryOrder.filter { !byCategory[it].isNullOrEmpty() }
    val chips = listOf(DrawerChip(filter = null, count = apps.size)) +
            present.map { DrawerChip(filter = it, count = byCategory.getValue(it).size) }

    val selected = filter?.takeIf { it in present }
    val visible = if (selected != null) listOf(selected) else present

    return DrawerModel(
        chips = chips,
        selected = selected,
        sections = visible.map { DrawerSection(category = it, apps = byCategory.getValue(it)) },
    )
}