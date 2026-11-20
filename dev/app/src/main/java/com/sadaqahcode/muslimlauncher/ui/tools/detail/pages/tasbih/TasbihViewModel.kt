package com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.tasbih

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sadaqahcode.muslimlauncher.domain.model.Result
import com.sadaqahcode.muslimlauncher.domain.model.tools.Dhikr
import com.sadaqahcode.muslimlauncher.domain.model.tools.DhikrEntry
import com.sadaqahcode.muslimlauncher.domain.model.tools.DhikrItem
import com.sadaqahcode.muslimlauncher.domain.repository.DhikrRepository
import com.sadaqahcode.muslimlauncher.domain.usecase.tools.GetDhikrItemsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface TasbihHaptic {
    data object Tap : TasbihHaptic
    data object TargetReached : TasbihHaptic
    data object ResetConfirmNeeded : TasbihHaptic
    data object ResetConfirmed : TasbihHaptic
}

data class TasbihUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val items: List<DhikrItem> = emptyList(),
    val selectedItemIndex: Int = 0,
    val entryIndex: Int = 0,
    val count: Int = 0,
    val target: Int = 33,
    val confirmingReset: Boolean = false,
) {
    val currentItem: DhikrItem? get() = items.getOrNull(selectedItemIndex)
    val currentEntry: DhikrEntry? get() = currentItem?.entries?.getOrNull(entryIndex)
    val currentDhikr: Dhikr? get() = currentEntry?.dhikr
    val isDone: Boolean get() = target > 0 && count >= target
}

@HiltViewModel
class TasbihViewModel @Inject constructor(
    private val getDhikrItems: GetDhikrItemsUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(TasbihUiState())
    val state: StateFlow<TasbihUiState> = _state.asStateFlow()

    private val _haptics = MutableSharedFlow<TasbihHaptic>(extraBufferCapacity = 8)
    val haptics: SharedFlow<TasbihHaptic> = _haptics.asSharedFlow()

    private var resetConfirmJob: Job? = null

    init {
        load()
    }

    fun retry() = load(tryOnlineFirst = true)

    fun load(
        tryOnlineFirst: Boolean = false,
        cacheValiditySeconds: Long = DhikrRepository.DEFAULT_CACHE_VALIDITY_SECONDS,
    ) {
        getDhikrItems(tryOnlineFirst, cacheValiditySeconds)
            .onEach { result ->
                when (result) {
                    Result.Loading -> _state.update {
                        it.copy(isLoading = true, error = null)
                    }

                    is Result.Success -> {
                        val items = result.data
                        _state.update { current ->
                            val newItemIndex = current.selectedItemIndex
                                .coerceIn(0, (items.size - 1).coerceAtLeast(0))
                            val entryCount = items.getOrNull(newItemIndex)?.entries?.size ?: 0
                            val newEntryIndex = current.entryIndex
                                .coerceIn(0, (entryCount - 1).coerceAtLeast(0))
                            val defaultTarget = items
                                .getOrNull(newItemIndex)
                                ?.entries
                                ?.getOrNull(newEntryIndex)
                                ?.repetitions
                                ?.takeIf { it > 0 }
                                ?: current.target

                            current.copy(
                                isLoading = false,
                                items = items,
                                error = null,
                                selectedItemIndex = newItemIndex,
                                entryIndex = newEntryIndex,
                                count = 0,
                                target = defaultTarget,
                                confirmingReset = false,
                            )
                        }
                    }

                    is Result.Error -> _state.update {
                        // Only surface the error if there is nothing to show.
                        // If a previous Success emission put items on screen,
                        // a transient network failure should stay silent.
                        if (it.items.isEmpty()) {
                            it.copy(isLoading = false, error = result.message)
                        } else {
                            it.copy(isLoading = false)
                        }
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    fun selectItem(index: Int) {
        val items = _state.value.items
        if (index !in items.indices) return
        cancelResetConfirm()
        val defaultTarget = items[index].entries
            .firstOrNull()
            ?.repetitions
            ?.takeIf { it > 0 }
            ?: _state.value.target
        _state.update {
            it.copy(
                selectedItemIndex = index,
                entryIndex = 0,
                count = 0,
                target = defaultTarget,
                confirmingReset = false,
            )
        }
    }

    fun selectEntry(index: Int) {
        val item = _state.value.currentItem ?: return
        if (index !in item.entries.indices) return
        cancelResetConfirm()
        val defaultTarget = item.entries[index].repetitions
            .takeIf { it > 0 }
            ?: _state.value.target
        _state.update {
            it.copy(
                entryIndex = index,
                count = 0,
                target = defaultTarget,
                confirmingReset = false,
            )
        }
    }

    fun selectTarget(target: Int) {
        _state.update { it.copy(target = target, confirmingReset = false) }
    }

    fun tap() {
        val current = _state.value
        if (current.items.isEmpty()) return
        cancelResetConfirm()
        val target = current.target
        val count = current.count

        if (target in 1..count) {
            _state.update { it.copy(count = 0, confirmingReset = false) }
            _haptics.tryEmit(TasbihHaptic.Tap)
            return
        }

        val next = count + 1
        _state.update { it.copy(count = next, confirmingReset = false) }
        if (target > 0 && next == target) {
            _haptics.tryEmit(TasbihHaptic.TargetReached)
        } else {
            _haptics.tryEmit(TasbihHaptic.Tap)
        }
    }

    fun requestReset() {
        val current = _state.value
        if (current.count == 0) return

        if (current.confirmingReset) {
            cancelResetConfirm()
            _state.update { it.copy(count = 0, confirmingReset = false) }
            _haptics.tryEmit(TasbihHaptic.ResetConfirmed)
            return
        }

        _state.update { it.copy(confirmingReset = true) }
        _haptics.tryEmit(TasbihHaptic.ResetConfirmNeeded)
        resetConfirmJob?.cancel()
        resetConfirmJob = viewModelScope.launch {
            delay(3_000)
            _state.update { it.copy(confirmingReset = false) }
        }
    }

    private fun cancelResetConfirm() {
        resetConfirmJob?.cancel()
        resetConfirmJob = null
    }

    override fun onCleared() {
        super.onCleared()
        cancelResetConfirm()
    }
}