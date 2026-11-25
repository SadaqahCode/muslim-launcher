package com.sadaqahcode.muslimlauncher.ui.tools

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sadaqahcode.muslimlauncher.data.repository.ToolPreferencesRepository
import com.sadaqahcode.muslimlauncher.domain.model.ToolContext
import com.sadaqahcode.muslimlauncher.domain.model.ToolId
import com.sadaqahcode.muslimlauncher.domain.model.ToolPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Calendar
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ToolsViewModel @Inject constructor(
    private val repo: ToolPreferencesRepository,
) : ViewModel() {

    val prefs: StateFlow<ToolPreferences> = repo.prefs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ToolPreferences())

    val today: StateFlow<Set<ToolContext>> =
        flowOf(computeToday()).stateIn(
            viewModelScope, SharingStarted.Eagerly, emptySet()
        )

    fun setEnabled(id: ToolId, enabled: Boolean) = viewModelScope.launch {
        repo.setEnabled(id, enabled)
    }
    fun setOrder(ids: List<ToolId>) = viewModelScope.launch { repo.setOrder(ids) }
    fun reset() = viewModelScope.launch { repo.reset() }

    private fun computeToday(): Set<ToolContext> {
        val result = mutableSetOf<ToolContext>()

        // Friday check with java.util.Calendar, API 1
        val gregorian = Calendar.getInstance()
        if (gregorian.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY) {
            result.add(ToolContext.Friday)
        }

        // Hijri check with android.icu, API 24
        try {
            val islamic = android.icu.util.IslamicCalendar.getInstance()
            val month = islamic.get(android.icu.util.Calendar.MONTH)
            val day = islamic.get(android.icu.util.Calendar.DAY_OF_MONTH)

            if (month == android.icu.util.IslamicCalendar.RAMADAN) {
                result.add(ToolContext.Ramadan)
            }
            if ((month == android.icu.util.IslamicCalendar.SHAWWAL && day == 1) ||
                (month == android.icu.util.IslamicCalendar.DHU_AL_HIJJAH && day == 10)
            ) {
                result.add(ToolContext.Eid)
            }
            if (day in 13..15) {
                result.add(ToolContext.AyyamAlBid)
            }
        } catch (_: Exception) {
        }
        return result
    }
}