package com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.tasbih

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sadaqahcode.muslimlauncher.data.local.store.SettingsDataStore
import com.sadaqahcode.muslimlauncher.data.local.store.TasbihDataStore
import com.sadaqahcode.muslimlauncher.data.local.tools.tasbih.DefaultDhikrItems
import com.sadaqahcode.muslimlauncher.domain.model.Result
import com.sadaqahcode.muslimlauncher.domain.model.UserSettings
import com.sadaqahcode.muslimlauncher.domain.model.tools.Dhikr
import com.sadaqahcode.muslimlauncher.domain.model.tools.DhikrEntry
import com.sadaqahcode.muslimlauncher.domain.model.tools.DhikrItem
import com.sadaqahcode.muslimlauncher.domain.model.tools.HapticMode
import com.sadaqahcode.muslimlauncher.domain.model.tools.name
import com.sadaqahcode.muslimlauncher.domain.repository.DhikrRepository
import com.sadaqahcode.muslimlauncher.domain.usecase.tools.GetDhikrItemsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

sealed interface TasbihHaptic {
    data object Tap : TasbihHaptic
    data object TargetReached : TasbihHaptic
    data object ResetConfirmNeeded : TasbihHaptic
    data object ResetConfirmed : TasbihHaptic
    data object StepDone : TasbihHaptic
    data object RoutineFinished : TasbihHaptic
}

enum class TasbihMode { Free, Guided }

fun Dhikr.freeKey(): String = arabic
fun DhikrItem.guidedKey(): String = name("en")

data class TasbihUiState(
    val isRefreshing: Boolean = false,
    val error: String? = null,
    val items: List<DhikrItem> = DefaultDhikrItems.ALL,
    val mode: TasbihMode = TasbihMode.Free,
    val confirmingReset: Boolean = false,
    val hapticMode: HapticMode = HapticMode.Haptic,
    val selectedFreeKey: String? = null,
    val freeTarget: Int = 33,
    val freeCounts: Map<String, Int> = emptyMap(),
    val favoriteFree: Set<String> = emptySet(),
    val selectedItemIndex: Int = 0,
    val entryIndex: Int = 0,
    val guidedCount: Int = 0,
    val finished: Boolean = false,
    val favoriteGuided: Set<String> = emptySet(),
) {
    val autoRun: Boolean get() = mode == TasbihMode.Guided
    val freeList: List<Dhikr>
        get() = items.flatMap { it.entries }.map { it.dhikr }.distinctBy { it.freeKey() }
    val freeDisplay: List<Dhikr>
        get() = freeList.sortedByDescending { it.freeKey() in favoriteFree }
    val guidedDisplay: List<DhikrItem>
        get() = items.sortedByDescending { it.guidedKey() in favoriteGuided }
    val currentFree: Dhikr?
        get() = freeList.firstOrNull { it.freeKey() == selectedFreeKey } ?: freeDisplay.firstOrNull()
    val currentItem: DhikrItem? get() = items.getOrNull(selectedItemIndex)
    val currentEntry: DhikrEntry? get() = currentItem?.entries?.getOrNull(entryIndex)
    val guidedDhikr: Dhikr? get() = currentEntry?.dhikr
    val isLastEntry: Boolean
        get() = entryIndex >= (currentItem?.entries?.lastIndex ?: 0)
    val activeDhikr: Dhikr? get() = if (autoRun) guidedDhikr else currentFree
    val count: Int
        get() = if (autoRun) guidedCount
        else currentFree?.let { freeCounts[it.freeKey()] } ?: 0
    val target: Int
        get() = if (autoRun) currentEntry?.repetitions ?: 0 else freeTarget
    val isFavoriteCurrent: Boolean
        get() = if (autoRun) currentItem?.guidedKey()?.let { it in favoriteGuided } ?: false
        else currentFree?.freeKey()?.let { it in favoriteFree } ?: false
    val totalRepetitions: Int get() = currentItem?.entries?.sumOf { it.repetitions } ?: 0
    val doneRepetitions: Int
        get() = if (finished) totalRepetitions
        else (currentItem?.entries?.take(entryIndex)?.sumOf { it.repetitions } ?: 0) + guidedCount
    val progress: Float
        get() = if (totalRepetitions == 0) 0f else doneRepetitions / totalRepetitions.toFloat()
}

@HiltViewModel
class TasbihViewModel @Inject constructor(
    private val getDhikrItems: GetDhikrItemsUseCase,
    private val prefs: TasbihDataStore,
    private val settingsDs: SettingsDataStore,
) : ViewModel() {

    private val _state = MutableStateFlow(TasbihUiState())
    val state: StateFlow<TasbihUiState> = _state.asStateFlow()

    private val _haptics = MutableSharedFlow<TasbihHaptic>(extraBufferCapacity = 8)
    val haptics: SharedFlow<TasbihHaptic> = _haptics.asSharedFlow()

    private var resetConfirmJob: Job? = null

    val settings: StateFlow<UserSettings> = settingsDs.settings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UserSettings())

    init {
        prefs.favoriteFree
            .onEach { fav -> _state.update { it.copy(favoriteFree = fav) } }
            .launchIn(viewModelScope)
        prefs.favoriteGuided
            .onEach { fav -> _state.update { it.copy(favoriteGuided = fav) } }
            .launchIn(viewModelScope)
        prefs.hapticMode
            .onEach { mode -> _state.update { it.copy(hapticMode = mode) } }
            .launchIn(viewModelScope)

        viewModelScope.launch {
            val saved = prefs.readSaved()
            val savedMode = runCatching { TasbihMode.valueOf(saved.lastMode ?: "Free") }.getOrDefault(TasbihMode.Free)
            val savedGuidedIndex = DefaultDhikrItems.ALL.indexOfFirst { it.guidedKey() == saved.lastGuidedKey }.takeIf { it >= 0 } ?: 0
            _state.update {
                it.copy(
                    mode = savedMode,
                    selectedFreeKey = saved.freeKey,
                    freeTarget = saved.freeTarget ?: it.freeTarget,
                    selectedItemIndex = savedGuidedIndex,
                )
            }
            load()
        }
    }

    fun retry() = load(tryOnlineFirst = true)

    fun load(
        tryOnlineFirst: Boolean = false,
        cacheValiditySeconds: Long = DhikrRepository.DEFAULT_CACHE_VALIDITY_SECONDS,
    ) {
        // Show content instantly, refresh in background. No 1600ms forced bar.
        _state.update { it.copy(isRefreshing = true, error = null) }

        getDhikrItems(tryOnlineFirst, cacheValiditySeconds)
            .onEach { result ->
                when (result) {
                    Result.Loading -> Unit
                    is Result.Success -> {
                        val fresh = result.data
                        if (fresh.isEmpty()) {
                            _state.update { it.copy(isRefreshing = false) }
                            return@onEach
                        }
                        _state.update { current ->
                            val savedGuidedKey = current.items.getOrNull(current.selectedItemIndex)?.guidedKey()
                            val newItemIndex = if (current.mode == TasbihMode.Guided) {
                                val byKey = fresh.indexOfFirst { it.guidedKey() == savedGuidedKey }.takeIf { it >= 0 }
                                val byFav = fresh.indexOfFirst { it.guidedKey() in current.favoriteGuided }.takeIf { it >= 0 }
                                byKey ?: byFav ?: 0
                            } else {
                                current.selectedItemIndex.coerceIn(0, (fresh.size - 1).coerceAtLeast(0))
                            }
                            val entryCount = fresh.getOrNull(newItemIndex)?.entries?.size ?: 0
                            val newEntryIndex = current.entryIndex.coerceIn(0, (entryCount - 1).coerceAtLeast(0))
                            val next = current.copy(
                                items = fresh,
                                error = null,
                                selectedItemIndex = newItemIndex,
                                entryIndex = newEntryIndex,
                                confirmingReset = false,
                                isRefreshing = false,
                            )
                            val freeKey = next.selectedFreeKey?.takeIf { k -> next.freeList.any { it.freeKey() == k } }
                                ?: next.freeDisplay.firstOrNull { it.freeKey() in next.favoriteFree }?.freeKey()
                                ?: next.freeDisplay.firstOrNull()?.freeKey()
                            next.copy(selectedFreeKey = freeKey)
                        }
                    }
                    is Result.Error -> {
                        _state.update { current ->
                            if (current.items.isEmpty()) current.copy(error = result.message, isRefreshing = false)
                            else current.copy(isRefreshing = false)
                        }
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    fun setHapticMode(mode: HapticMode) {
        _state.update { it.copy(hapticMode = mode) }
        viewModelScope.launch { prefs.saveHapticMode(mode) }
    }

    fun selectFree(key: String) {
        val s = _state.value
        if (s.freeList.none { it.freeKey() == key }) return
        cancelResetConfirm()
        _state.update {
            it.copy(
                mode = TasbihMode.Free,
                selectedFreeKey = key,
                finished = false,
                guidedCount = 0,
                confirmingReset = false,
            )
        }
        viewModelScope.launch {
            prefs.saveLastFree(key)
            prefs.saveLastMode(TasbihMode.Free.name)
        }
    }

    fun moveFree(delta: Int) {
        val s = _state.value
        if (s.autoRun) return
        val list = s.freeDisplay
        val current = s.currentFree?.freeKey()
        val index = list.indexOfFirst { it.freeKey() == current }
        if (index < 0) return
        val target = (index + delta).coerceIn(0, list.lastIndex)
        if (target != index) selectFree(list[target].freeKey())
    }

    fun selectTarget(target: Int) {
        _state.update { it.copy(freeTarget = target, confirmingReset = false) }
        viewModelScope.launch { prefs.saveFreeTarget(target) }
    }

    fun toggleFavoriteFree(key: String) {
        viewModelScope.launch { prefs.toggleFavoriteFree(key) }
    }

    fun toggleFavoriteGuided(key: String) {
        viewModelScope.launch { prefs.toggleFavoriteGuided(key) }
    }

    fun startGuided(itemIndex: Int) {
        val items = _state.value.items
        if (itemIndex !in items.indices) return
        if (items[itemIndex].entries.isEmpty()) return
        cancelResetConfirm()
        val key = items[itemIndex].guidedKey()
        _state.update {
            it.copy(
                mode = TasbihMode.Guided,
                selectedItemIndex = itemIndex,
                entryIndex = 0,
                guidedCount = 0,
                finished = false,
                confirmingReset = false,
            )
        }
        viewModelScope.launch {
            prefs.saveLastGuided(key)
            prefs.saveLastMode(TasbihMode.Guided.name)
        }
    }

    fun restartGuided() = startGuided(_state.value.selectedItemIndex)

    fun exitGuided() {
        cancelResetConfirm()
        _state.update {
            it.copy(
                mode = TasbihMode.Free,
                guidedCount = 0,
                entryIndex = 0,
                finished = false,
                confirmingReset = false,
            )
        }
        viewModelScope.launch { prefs.saveLastMode(TasbihMode.Free.name) }
    }

    fun nextEntry() {
        val s = _state.value
        if (!s.autoRun || s.finished) return
        cancelResetConfirm()
        if (s.isLastEntry) {
            _state.update { it.copy(finished = true, confirmingReset = false) }
            _haptics.tryEmit(TasbihHaptic.RoutineFinished)
        } else {
            goToEntry(s.entryIndex + 1)
        }
    }

    fun previousEntry() {
        val s = _state.value
        if (!s.autoRun || s.finished || s.entryIndex == 0) return
        cancelResetConfirm()
        goToEntry(s.entryIndex - 1)
    }

    private fun goToEntry(index: Int) {
        _state.update { it.copy(entryIndex = index, guidedCount = 0, confirmingReset = false) }
    }

    fun tap() {
        val s = _state.value
        if (s.items.isEmpty() || s.finished) return
        cancelResetConfirm()
        val target = s.target
        val next = s.count + 1
        if (s.autoRun) {
            if (target in 1..next) {
                if (s.isLastEntry) {
                    _state.update { it.copy(guidedCount = next, confirmingReset = false, finished = true) }
                    _haptics.tryEmit(TasbihHaptic.RoutineFinished)
                } else {
                    _haptics.tryEmit(TasbihHaptic.StepDone)
                    goToEntry(s.entryIndex + 1)
                }
            } else {
                _state.update { it.withCount(next) }
                _haptics.tryEmit(TasbihHaptic.Tap)
            }
            return
        }
        if (target > 0 && s.count >= target) {
            _state.update { it.withCount(0) }
            _haptics.tryEmit(TasbihHaptic.ResetConfirmed)
            return
        }
        _state.update { it.withCount(next) }
        if (target > 0 && next == target) {
            _haptics.tryEmit(TasbihHaptic.TargetReached)
        } else {
            _haptics.tryEmit(TasbihHaptic.Tap)
        }
    }

    fun requestReset() {
        val s = _state.value
        if (s.count == 0) return
        if (s.confirmingReset) {
            cancelResetConfirm()
            _state.update { it.withCount(0) }
            _haptics.tryEmit(TasbihHaptic.ResetConfirmed)
            return
        }
        _state.update { it.copy(confirmingReset = true) }
        _haptics.tryEmit(TasbihHaptic.ResetConfirmNeeded)
        resetConfirmJob?.cancel()
        resetConfirmJob = viewModelScope.launch {
            delay(3_000.milliseconds)
            _state.update { it.copy(confirmingReset = false) }
        }
    }

    private fun TasbihUiState.withCount(n: Int): TasbihUiState {
        if (autoRun) return copy(guidedCount = n, confirmingReset = false)
        val key = currentFree?.freeKey() ?: return this
        return copy(freeCounts = freeCounts + (key to n), confirmingReset = false)
    }

    private fun cancelResetConfirm() {
        resetConfirmJob?.cancel()
        resetConfirmJob = null
    }

    override fun onCleared() {
        cancelResetConfirm()
    }
}