package com.sadaqahcode.muslimlauncher.ui.tools

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ToolsPageViewModel : ViewModel() {

    // ── Qibla ────────────────────────────────────────────────────────
    private val _qiblaBearing = MutableStateFlow(23f)
    val qiblaBearing: StateFlow<Float> = _qiblaBearing.asStateFlow()

    // ── Tasbih ───────────────────────────────────────────────────────
    private val _tasbihCount = MutableStateFlow(33)
    val tasbihCount: StateFlow<Int> = _tasbihCount.asStateFlow()

    private val _tasbihRound = MutableStateFlow(3)
    val tasbihRound: StateFlow<Int> = _tasbihRound.asStateFlow()

    // ── Salawat ──────────────────────────────────────────────────────
    private val _salawatCount = MutableStateFlow(47)
    val salawatCount: StateFlow<Int> = _salawatCount.asStateFlow()

    private val _salawatGoal = MutableStateFlow(100)
    val salawatGoal: StateFlow<Int> = _salawatGoal.asStateFlow()

    // ── Adhkar ───────────────────────────────────────────────────────
    private val _adhkarProgress = MutableStateFlow(3 to 8)
    val adhkarProgress: StateFlow<Pair<Int, Int>> = _adhkarProgress.asStateFlow()

    private val _adhkarSession = MutableStateFlow("Morning")
    val adhkarSession: StateFlow<String> = _adhkarSession.asStateFlow()

    // ── Hijri ────────────────────────────────────────────────────────
    private val _hijriDay = MutableStateFlow(12)
    val hijriDay: StateFlow<Int> = _hijriDay.asStateFlow()

    private val _hijriMonth = MutableStateFlow("Jumada al-Thani")
    val hijriMonth: StateFlow<String> = _hijriMonth.asStateFlow()

    private val _hijriYear = MutableStateFlow(1448)
    val hijriYear: StateFlow<Int> = _hijriYear.asStateFlow()

    private val _hijriWeekday = MutableStateFlow("Thursday")
    val hijriWeekday: StateFlow<String> = _hijriWeekday.asStateFlow()

    // ── Fasting (weekly sunna + Ramadan) ─────────────────────────────
    private val _fastingThisWeek = MutableStateFlow(2)
    val fastingThisWeek: StateFlow<Int> = _fastingThisWeek.asStateFlow()

    private val _fastingGoal = MutableStateFlow(3)
    val fastingGoal: StateFlow<Int> = _fastingGoal.asStateFlow()

    // ── Sadaqah (this month) ─────────────────────────────────────────
    private val _sadaqahThisMonth = MutableStateFlow(5)
    val sadaqahThisMonth: StateFlow<Int> = _sadaqahThisMonth.asStateFlow()

    private val _sadaqahAmount = MutableStateFlow<String?>("Rp 150k")
    val sadaqahAmount: StateFlow<String?> = _sadaqahAmount.asStateFlow()

    // ── Zakat (last calculation) ─────────────────────────────────────
    private val _lastZakatAmount = MutableStateFlow<String?>("Rp 2.5jt")
    val lastZakatAmount: StateFlow<String?> = _lastZakatAmount.asStateFlow()

    private val _lastZakatAge = MutableStateFlow("3 months ago")
    val lastZakatAge: StateFlow<String> = _lastZakatAge.asStateFlow()

    // ── Contextual (Friday / Ramadan / Ayyam al-Bid) ─────────────────
    private val _contextual = MutableStateFlow<ContextualTool?>(null)
    val contextual: StateFlow<ContextualTool?> = _contextual.asStateFlow()

    private val _ramadanDay = MutableStateFlow(12)
    val ramadanDay: StateFlow<Int> = _ramadanDay.asStateFlow()
}

/** Contextual banner shown above the grid on special days. */
enum class ContextualTool { Friday, Ramadan, AyyamAlBid }