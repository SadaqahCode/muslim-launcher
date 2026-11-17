package com.sadaqahcode.muslimlauncher.domain.usecase

import com.sadaqahcode.muslimlauncher.data.local.AppPreferenceEntity
import com.sadaqahcode.muslimlauncher.data.repository.AppPreferencesRepository
import com.sadaqahcode.muslimlauncher.domain.model.AppCategory
import com.sadaqahcode.muslimlauncher.domain.model.LauncherApp

class SetCategoryUseCase(private val repo: AppPreferencesRepository) {

    suspend operator fun invoke(app: LauncherApp, category: AppCategory) {
        val existing = repo.get(app.packageName)
        val updated = (existing ?: defaultEntity(app)).copy(category = category.name)
        repo.upsert(updated)
    }

    private fun defaultEntity(app: LauncherApp) = AppPreferenceEntity(
        packageName = app.packageName,
        category = app.category.name,
        isPinned = app.isPinned,
        isDocked = app.isDocked,
    )
}