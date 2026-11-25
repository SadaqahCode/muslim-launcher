package com.sadaqahcode.muslimlauncher.domain.usecase

import com.sadaqahcode.muslimlauncher.data.repository.WidgetPlacementRepository

class RemoveWidgetUseCase(private val repo: WidgetPlacementRepository) {
    suspend operator fun invoke(widgetId: Int) {
        repo.remove(widgetId)
    }
}