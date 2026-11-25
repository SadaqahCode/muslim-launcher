package com.sadaqahcode.muslimlauncher.domain.usecase

import com.sadaqahcode.muslimlauncher.data.repository.WidgetPlacementRepository

class AddWidgetUseCase(private val repo: WidgetPlacementRepository) {
    suspend operator fun invoke(widgetId: Int, providerName: String) {
        repo.add(widgetId = widgetId, providerName = providerName)
    }
}