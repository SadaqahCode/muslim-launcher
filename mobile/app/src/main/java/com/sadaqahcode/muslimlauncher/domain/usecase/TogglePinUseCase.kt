package com.sadaqahcode.muslimlauncher.domain.usecase

import com.sadaqahcode.muslimlauncher.data.local.AppPreferenceEntity
import com.sadaqahcode.muslimlauncher.data.repository.AppPreferencesRepository
import com.sadaqahcode.muslimlauncher.domain.model.LauncherApp

sealed interface ToggleResult {
    data object Success : ToggleResult
    data class Rejected(val reason: String) : ToggleResult
}

class TogglePinUseCase(private val repo: AppPreferencesRepository) {

    suspend operator fun invoke(app: LauncherApp, maxPinned: Int): ToggleResult {
        val existing = repo.get(app.packageName)
        val currentlyPinned = existing?.isPinned ?: false

        if (currentlyPinned) {
            repo.upsert(
                (existing ?: defaultEntity(app)).copy(isPinned = false)
            )
            return ToggleResult.Success
        }

        if (repo.getPinnedCount() >= maxPinned) {
            return ToggleResult.Rejected(
                "Home is full at $maxPinned. Unpin one to make room."
            )
        }

        repo.upsert(
            (existing ?: defaultEntity(app)).copy(isPinned = true)
        )
        return ToggleResult.Success
    }

    private fun defaultEntity(app: LauncherApp) = AppPreferenceEntity(
        packageName = app.packageName,
        category = app.category.name,
        isPinned = false,
        isDocked = app.isDocked,
    )
}