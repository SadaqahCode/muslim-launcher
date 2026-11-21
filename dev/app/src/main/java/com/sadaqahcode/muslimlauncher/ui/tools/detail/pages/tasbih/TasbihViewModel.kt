package com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.tasbih

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sadaqahcode.muslimlauncher.data.local.store.TasbihDataStore
import com.sadaqahcode.muslimlauncher.data.local.tools.tasbih.DefaultDhikrItems
import com.sadaqahcode.muslimlauncher.domain.model.Result
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
    data object StepDone : TasbihHaptic
    data object RoutineFinished : TasbihHaptic
}

private const val MIN_BAR_VISIBLE_MS = 1600L
private const val INSTANT_RESPONSE_MS = 100L
/**
 * Free   : without a flow. Choose any dhikr, any target, count as freely as you like.
 * Guided : sequential dhikr sequence, automatic step transition, then a completion panel.
 */
enum class TasbihMode { Free, Guided }

fun Dhikr.freeKey(): String = arabic
fun DhikrItem.guidedKey(): String = name("en")


data class TasbihUiState(
    /** Visible browser-style bar at the very top while the repository refreshes. */
    val isRefreshing: Boolean = true,
    /** Defensive only — never populated during normal refresh, since defaults always exist. */
    val error: String? = null,
    /** Seeded with [DefaultDhikrItems.ALL] so the UI is usable on frame 1. */
    val items: List<DhikrItem> = DefaultDhikrItems.ALL,
    val mode: TasbihMode = TasbihMode.Free,
    val confirmingReset: Boolean = false,
    val hapticMode: HapticMode = HapticMode.Haptic,

    // ── Free ──
    val selectedFreeKey: String? = null,
    val freeTarget: Int = 33,
    val freeCounts: Map<String, Int> = emptyMap(),
    val favoriteFree: Set<String> = emptySet(),

    // ── Guided ──
    val selectedItemIndex: Int = 0,
    val entryIndex: Int = 0,
    val guidedCount: Int = 0,
    val finished: Boolean = false,
    val favoriteGuided: Set<String> = emptySet(),
) {
    val autoRun: Boolean get() = mode == TasbihMode.Guided

    // ── Free ──
    val freeList: List<Dhikr>
        get() = items.flatMap { it.entries }.map { it.dhikr }.distinctBy { it.freeKey() }

    val freeDisplay: List<Dhikr>
        get() = freeList.sortedByDescending { it.freeKey() in favoriteFree }

    val currentFree: Dhikr?
        get() = freeList.firstOrNull { it.freeKey() == selectedFreeKey } ?: freeDisplay.firstOrNull()

    // ── Guided ──
    val currentItem: DhikrItem? get() = items.getOrNull(selectedItemIndex)
    val currentEntry: DhikrEntry? get() = currentItem?.entries?.getOrNull(entryIndex)
    val guidedDhikr: Dhikr? get() = currentEntry?.dhikr
    val isLastEntry: Boolean
        get() = entryIndex >= (currentItem?.entries?.lastIndex ?: 0)

    // ── Aktive ──
    val activeDhikr: Dhikr? get() = if (autoRun) guidedDhikr else currentFree

    val count: Int
        get() = if (autoRun) guidedCount
        else currentFree?.let { freeCounts[it.freeKey()] } ?: 0

    val target: Int
        get() = if (autoRun) currentEntry?.repetitions ?: 0 else freeTarget

    val isFavoriteCurrent: Boolean
        get() = if (autoRun) currentItem?.guidedKey()?.let { it in favoriteGuided } ?: false
        else currentFree?.freeKey()?.let { it in favoriteFree } ?: false

    /* Progress */
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
) : ViewModel() {

    private var hideBarJob: Job? = null
    private var barShownAt: Long = 0L

    private val _state = MutableStateFlow(TasbihUiState())
    val state: StateFlow<TasbihUiState> = _state.asStateFlow()

    private val _haptics = MutableSharedFlow<TasbihHaptic>(extraBufferCapacity = 8)
    val haptics: SharedFlow<TasbihHaptic> = _haptics.asSharedFlow()

    private var resetConfirmJob: Job? = null

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
            _state.update {
                it.copy(
                    selectedFreeKey = saved.freeKey,
                    freeTarget = saved.freeTarget ?: it.freeTarget,
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
        hideBarJob?.cancel()
        barShownAt = System.currentTimeMillis()
        _state.update { it.copy(isRefreshing = true, error = null) }

        getDhikrItems(tryOnlineFirst, cacheValiditySeconds)
            .onEach { result ->
                when (result) {
                    Result.Loading -> Unit   // isRefreshing already true

                    is Result.Success -> {
                        val wasInstant = System.currentTimeMillis() - barShownAt < INSTANT_RESPONSE_MS
                        val fresh = result.data
                        if (fresh.isEmpty()) {
                            scheduleHideBar(wasInstant)
                            return@onEach
                        }
                        _state.update { current ->
                            val newItemIndex = current.selectedItemIndex
                                .coerceIn(0, (fresh.size - 1).coerceAtLeast(0))
                            val entryCount = fresh.getOrNull(newItemIndex)?.entries?.size ?: 0
                            val newEntryIndex = current.entryIndex
                                .coerceIn(0, (entryCount - 1).coerceAtLeast(0))

                            val next = current.copy(
                                items = fresh,
                                error = null,
                                selectedItemIndex = newItemIndex,
                                entryIndex = newEntryIndex,
                                confirmingReset = false,
                            )
                            next.copy(selectedFreeKey = next.currentFree?.freeKey())
                        }
                        scheduleHideBar(wasInstant)
                    }

                    is Result.Error -> {
                        val wasInstant = System.currentTimeMillis() - barShownAt < INSTANT_RESPONSE_MS
                        _state.update { current ->
                            if (current.items.isEmpty()) current.copy(error = result.message)
                            else current
                        }
                        scheduleHideBar(wasInstant)
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    private fun scheduleHideBar(wasInstant: Boolean = false) {
        hideBarJob?.cancel()
        hideBarJob = viewModelScope.launch {
            if (!wasInstant) {
                val elapsed = System.currentTimeMillis() - barShownAt
                val remaining = (MIN_BAR_VISIBLE_MS - elapsed).coerceAtLeast(0L)
                if (remaining > 0) delay(remaining)
            }
            _state.update { it.copy(isRefreshing = false) }
        }
    }

    // ───────────────────────── FEEDBACK ─────────────────────────
    fun setHapticMode(mode: HapticMode) {
        _state.update { it.copy(hapticMode = mode) }
        viewModelScope.launch { prefs.saveHapticMode(mode) }
    }

    // ───────────────────────── FREE ─────────────────────────
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
        viewModelScope.launch { prefs.saveLastFree(key) }
    }

    /** Swipe. */
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

    /** 0 = no target */
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

    // ───────────────────────── GUIDED ─────────────────────────

    fun startGuided(itemIndex: Int) {
        val items = _state.value.items
        if (itemIndex !in items.indices) return
        if (items[itemIndex].entries.isEmpty()) return
        cancelResetConfirm()
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

    // ───────────────────────── TAP / RESET ─────────────────────────

    fun tap() {
        val s = _state.value
        if (s.items.isEmpty() || s.finished) return
        cancelResetConfirm()
        val target = s.target
        val next = s.count + 1

        if (s.autoRun) {
            if (target > 0 && next >= target) {
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

        // Free mode
        // Finite target (33 / 99 / 100): next tap after target wraps to 0 with haptic.
        // Infinite target (0):   no cap, counts forever.
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
            delay(3_000)
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
        super.onCleared()
        cancelResetConfirm()
        hideBarJob?.cancel()
        hideBarJob = null
    }
}