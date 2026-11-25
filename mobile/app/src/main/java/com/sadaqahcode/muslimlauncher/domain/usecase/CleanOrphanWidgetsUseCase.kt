package com.sadaqahcode.muslimlauncher.domain.usecase

import com.sadaqahcode.muslimlauncher.data.repository.WidgetPlacementRepository

class CleanOrphanWidgetsUseCase(private val repo: WidgetPlacementRepository) {

    suspend operator fun invoke(installedProviders: List<String>): List<Int> {
        if (installedProviders.isEmpty()) return emptyList()

        val orphans = repo.findOrphans(installedProviders)
        if (orphans.isEmpty()) return emptyList()

        repo.deleteOrphans(installedProviders)
        return orphans.map { it.widgetId }
    }
}