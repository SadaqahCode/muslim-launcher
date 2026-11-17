package com.sadaqahcode.muslimlauncher.domain.usecase

import com.sadaqahcode.muslimlauncher.data.local.AppPreferenceEntity
import com.sadaqahcode.muslimlauncher.data.repository.AppPreferencesRepository
import com.sadaqahcode.muslimlauncher.domain.model.LauncherApp

class ToggleDockUseCase(private val repo: AppPreferencesRepository) {

    suspend operator fun invoke(app: LauncherApp): ToggleResult {
        val existing = repo.get(app.packageName)
        val currentlyDocked = existing?.isDocked ?: false

        repo.upsert(
            (existing ?: defaultEntity(app)).copy(isDocked = !currentlyDocked)
        )
        return ToggleResult.Success
    }

    private fun defaultEntity(app: LauncherApp) = AppPreferenceEntity(
        packageName = app.packageName,
        category = app.category.name,
        isPinned = app.isPinned,
        isDocked = false,
    )
}