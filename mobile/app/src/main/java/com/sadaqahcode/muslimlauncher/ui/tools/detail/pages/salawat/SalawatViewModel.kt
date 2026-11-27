package com.sadaqahcode.muslimlauncher.ui.tools.detail.pages.salawat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sadaqahcode.muslimlauncher.data.local.store.SalawatPrefsDataStore
import com.sadaqahcode.muslimlauncher.data.local.store.SettingsDataStore
import com.sadaqahcode.muslimlauncher.data.local.fallback.DefaultSalawatItems
import com.sadaqahcode.muslimlauncher.domain.model.Result
import com.sadaqahcode.muslimlauncher.domain.model.UserSettings
import com.sadaqahcode.muslimlauncher.domain.model.tools.salawat.Salawat
import com.sadaqahcode.muslimlauncher.domain.usecase.tools.GetSalawatUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SalawatUiState(
    val isRefreshing: Boolean = false,
    val hasLoaded: Boolean = false,
    val error: String? = null,

    // ── dependency loading ──────────────────────────────────────────────
    val isSettingsLoading: Boolean = true,
    val isPrefsLoading: Boolean = true,

    // ── default fallback (same pattern as TasbihUiState.items) ──────────
    val items: List<Salawat> = DefaultSalawatItems.ALL,

    val selectedId: Int? = null,
    val favorites: Set<Int> = emptySet(),
    val showingFavoritesOnly: Boolean = false,
) {
    val selected: Salawat?
        get() = items.firstOrNull { it.id == selectedId } ?: items.firstOrNull()

    val displayList: List<Salawat>
        get() {
            val base = if (showingFavoritesOnly) items.filter { it.id in favorites } else items
            return base.sortedByDescending { it.id in favorites }
        }

    val isInitialLoading: Boolean
        get() = !hasLoaded && items.isEmpty() && error == null

    val isLoading: Boolean
        get() = isInitialLoading || isRefreshing || isSettingsLoading || isPrefsLoading
}

@HiltViewModel
class SalawatViewModel @Inject constructor(
    private val getSalawat: GetSalawatUseCase,
    private val prefs: SalawatPrefsDataStore,
    private val settingsDs: SettingsDataStore,
) : ViewModel() {

    private val _state = MutableStateFlow(SalawatUiState())
    val state: StateFlow<SalawatUiState> = _state.asStateFlow()

    val settings: StateFlow<UserSettings> = settingsDs.settings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UserSettings())

    init {
        settingsDs.settings
            .onEach { _state.update { it.copy(isSettingsLoading = false) } }
            .launchIn(viewModelScope)

        // have emitted at least once.
        combine(prefs.favoriteIds, prefs.lastSelectedId) { favs, id -> favs to id }
            .onEach { (favs, id) ->
                _state.update {
                    it.copy(
                        favorites = favs,
                        selectedId = it.selectedId ?: id,
                        isPrefsLoading = false,
                    )
                }
            }
            .launchIn(viewModelScope)

        load()
    }

    fun retry() = load(tryOnlineFirst = true)

    fun load(tryOnlineFirst: Boolean = false) {
        _state.update { it.copy(isRefreshing = true, error = null) }
        getSalawat(tryOnlineFirst)
            .onEach { result ->
                when (result) {
                    Result.Loading -> Unit

                    is Result.Success -> {
                        val fresh = result.data
                        if (fresh.isEmpty()) {
                            _state.update { it.copy(isRefreshing = false, hasLoaded = true) }
                            return@onEach
                        }
                        _state.update { s ->
                            val nextSelected = s.selectedId
                                ?.takeIf { id -> fresh.any { it.id == id } }
                                ?: fresh.firstOrNull()?.id
                            s.copy(
                                items = fresh,
                                selectedId = nextSelected,
                                isRefreshing = false,
                                hasLoaded = true,
                                error = null,
                            )
                        }
                    }

                    is Result.Error -> {
                        _state.update { s ->
                            s.copy(
                                error = result.message,
                                isRefreshing = false,
                                hasLoaded = true,
                            )
                        }
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    fun select(id: Int) {
        _state.update { it.copy(selectedId = id) }
        viewModelScope.launch { prefs.saveLastSelected(id) }
    }

    fun toggleFavorite(id: Int) {
        viewModelScope.launch { prefs.toggleFavorite(id) }
    }

    fun toggleFavoritesOnly() {
        _state.update { it.copy(showingFavoritesOnly = !it.showingFavoritesOnly) }
    }
}