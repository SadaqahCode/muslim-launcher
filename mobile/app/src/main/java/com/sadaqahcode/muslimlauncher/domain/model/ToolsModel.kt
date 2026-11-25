package com.sadaqahcode.muslimlauncher.domain.model

import androidx.annotation.StringRes
import com.sadaqahcode.muslimlauncher.R

enum class ToolId(val key: String) {
    Qibla("qibla"),
    Tasbih("tasbih"),
    Salawat("salawat"),
    Adhkar("adhkar"),
    Hijri("hijri"),
    Fasting("fasting"),
    Sadaqah("sadaqah"),
    Zakat("zakat"),
    PrayerTimes("prayer_times"),
    QadhaTracker("qadha_tracker"),
    QuranTracker("quran_tracker"),
    KhatamPlanner("khatam_planner"),
    AmalTracker("amal_tracker"),
    HafalanTracker("hafalan_tracker"),
    DuaCollection("dua_collection"),
    AsmaulHusna("asmaul_husna"),
    PrayerFocus("prayer_focus"),
    ImsakIftar("imsak_iftar"),
    FidyahCalculator("fidyah_calculator"),
    KaffarahGuide("kaffarah_guide"),
    RamadanPlanner("ramadan_planner"),
    EidCountdown("eid_countdown"),
    HadithReference("hadith_reference"),
    IslamicKnowledge("islamic_knowledge"),
    Manzil("manzil"),
    IslamicEvents("islamic_events"),
    Jumuah("jumuah"),
    TravelPrayerGuide("travel_prayer_guide"),
    FaraidCalculator("faraid_calculator"),
    DebtTracker("debt_tracker"),
    WasiatNotes("wasiat_notes"),
    HalalExpenseTracker("halal_expense_tracker"),
    SadaqahPlanner("sadaqah_planner"),
    MasjidFinder("masjid_finder"),
    PrayerTravel("prayer_travel"),
    QiblaTravel("qibla_travel"),
    HalalFoodFinder("halal_food_finder"),
    IslamCalculator("islam_calculator"),
    IslamicCountdown("islamic_countdown"),
    RemindMe("remind_me");

    companion object {
        private val byKey = entries.associateBy { it.key }
        fun fromKey(k: String): ToolId? = byKey[k]
    }
}

enum class ToolGroup { Worship, Tracking, Knowledge, Finance, Journey, Seasonal }

enum class ToolContext { Friday, Ramadan, AyyamAlBid, Eid }

data class Tool(
    val id: ToolId,
    val group: ToolGroup,
    @StringRes val titleRes: Int,
    @StringRes val subtitleRes: Int,
    val context: ToolContext? = null,
    val defaultEnabled: Boolean = true,
)

object ToolRegistry {
    val all: List<Tool> = listOf(
        Tool(ToolId.Qibla, ToolGroup.Worship, R.string.tool_qibla_title, R.string.tool_qibla_sub),
        Tool(ToolId.Tasbih, ToolGroup.Worship, R.string.tool_tasbih_title, R.string.tool_tasbih_sub),
        Tool(ToolId.Salawat, ToolGroup.Worship, R.string.tool_salawat_title, R.string.tool_salawat_sub),
        Tool(ToolId.Adhkar, ToolGroup.Worship, R.string.tool_adhkar_title, R.string.tool_adhkar_sub),
        Tool(ToolId.Hijri, ToolGroup.Seasonal, R.string.tool_hijri_title, R.string.tool_hijri_sub),
        Tool(ToolId.PrayerTimes, ToolGroup.Worship, R.string.tool_prayer_times_title, R.string.tool_prayer_times_sub),
        Tool(ToolId.DuaCollection, ToolGroup.Worship, R.string.tool_dua_collection_title, R.string.tool_dua_collection_sub),
        Tool(ToolId.AsmaulHusna, ToolGroup.Knowledge, R.string.tool_asmaul_husna_title, R.string.tool_asmaul_husna_sub),
        Tool(ToolId.PrayerFocus, ToolGroup.Worship, R.string.tool_prayer_focus_title, R.string.tool_prayer_focus_sub),
        Tool(ToolId.IslamicKnowledge, ToolGroup.Knowledge, R.string.tool_islamic_knowledge_title, R.string.tool_islamic_knowledge_sub),
        Tool(ToolId.IslamicEvents, ToolGroup.Seasonal, R.string.tool_islamic_events_title, R.string.tool_islamic_events_sub),
        Tool(ToolId.MasjidFinder, ToolGroup.Journey, R.string.tool_masjid_finder_title, R.string.tool_masjid_finder_sub),
        Tool(ToolId.IslamCalculator, ToolGroup.Finance, R.string.tool_islam_calculator_title, R.string.tool_islam_calculator_sub),
        Tool(ToolId.IslamicCountdown, ToolGroup.Seasonal, R.string.tool_islamic_countdown_title, R.string.tool_islamic_countdown_sub),
        Tool(ToolId.RemindMe, ToolGroup.Worship, R.string.tool_remind_me_title, R.string.tool_remind_me_sub),
        // Tool(ToolId.Fasting, ToolGroup.Tracking, R.string.tool_fasting_title, R.string.tool_fasting_sub),
        // Tool(ToolId.Sadaqah, ToolGroup.Finance, R.string.tool_sadaqah_title, R.string.tool_sadaqah_sub),
        // Tool(ToolId.Zakat, ToolGroup.Finance, R.string.tool_zakat_title, R.string.tool_zakat_sub),
        // Tool(ToolId.QadhaTracker, ToolGroup.Tracking, R.string.tool_qadha_tracker_title, R.string.tool_qadha_tracker_sub, defaultEnabled = false),
        // Tool(ToolId.QuranTracker, ToolGroup.Tracking, R.string.tool_quran_tracker_title, R.string.tool_quran_tracker_sub, defaultEnabled = false),
        // Tool(ToolId.KhatamPlanner, ToolGroup.Tracking, R.string.tool_khatam_planner_title, R.string.tool_khatam_planner_sub, defaultEnabled = false),
        // Tool(ToolId.AmalTracker, ToolGroup.Tracking, R.string.tool_amal_tracker_title, R.string.tool_amal_tracker_sub, defaultEnabled = false),
        // Tool(ToolId.HafalanTracker, ToolGroup.Tracking, R.string.tool_hafalan_tracker_title, R.string.tool_hafalan_tracker_sub, defaultEnabled = false),
        // Tool(ToolId.ImsakIftar, ToolGroup.Seasonal, R.string.tool_imsak_iftar_title, R.string.tool_imsak_iftar_sub, context = ToolContext.Ramadan, defaultEnabled = false),
        // Tool(ToolId.FidyahCalculator, ToolGroup.Finance, R.string.tool_fidyah_calculator_title, R.string.tool_fidyah_calculator_sub, defaultEnabled = false),
        // Tool(ToolId.KaffarahGuide, ToolGroup.Knowledge, R.string.tool_kaffarah_guide_title, R.string.tool_kaffarah_guide_sub, defaultEnabled = false),
        // Tool(ToolId.RamadanPlanner, ToolGroup.Seasonal, R.string.tool_ramadan_planner_title, R.string.tool_ramadan_planner_sub, context = ToolContext.Ramadan, defaultEnabled = false),
        // Tool(ToolId.EidCountdown, ToolGroup.Seasonal, R.string.tool_eid_countdown_title, R.string.tool_eid_countdown_sub, context = ToolContext.Eid, defaultEnabled = false),
        // Tool(ToolId.HadithReference, ToolGroup.Knowledge, R.string.tool_hadith_reference_title, R.string.tool_hadith_reference_sub, defaultEnabled = false),
        // Tool(ToolId.Jumuah, ToolGroup.Worship, R.string.tool_jumuah_title, R.string.tool_jumuah_sub, context = ToolContext.Friday, defaultEnabled = false),
        // Tool(ToolId.TravelPrayerGuide, ToolGroup.Journey, R.string.tool_travel_prayer_guide_title, R.string.tool_travel_prayer_guide_sub, defaultEnabled = false),
        // Tool(ToolId.FaraidCalculator, ToolGroup.Finance, R.string.tool_faraid_calculator_title, R.string.tool_faraid_calculator_sub, defaultEnabled = false),
        // Tool(ToolId.DebtTracker, ToolGroup.Finance, R.string.tool_debt_tracker_title, R.string.tool_debt_tracker_sub, defaultEnabled = false),
        // Tool(ToolId.WasiatNotes, ToolGroup.Finance, R.string.tool_wasiat_notes_title, R.string.tool_wasiat_notes_sub, defaultEnabled = false),
        // Tool(ToolId.HalalExpenseTracker, ToolGroup.Finance, R.string.tool_halal_expense_tracker_title, R.string.tool_halal_expense_tracker_sub, defaultEnabled = false),
        // Tool(ToolId.SadaqahPlanner, ToolGroup.Finance, R.string.tool_sadaqah_planner_title, R.string.tool_sadaqah_planner_sub, defaultEnabled = false),
        // Tool(ToolId.Manzil, ToolGroup.Worship, R.string.tool_manzil_title, R.string.tool_manzil_sub, defaultEnabled = false),
        // Tool(ToolId.PrayerTravel, ToolGroup.Journey, R.string.tool_prayer_travel_title, R.string.tool_prayer_travel_sub, defaultEnabled = false),
        // Tool(ToolId.QiblaTravel, ToolGroup.Journey, R.string.tool_qibla_travel_title, R.string.tool_qibla_travel_sub, defaultEnabled = false),
        // Tool(ToolId.HalalFoodFinder, ToolGroup.Journey, R.string.tool_halal_food_finder_title, R.string.tool_halal_food_finder_sub, defaultEnabled = false),

    )

    val byId: Map<ToolId, Tool> = all.associateBy { it.id }
    internal val defaultOrder: List<ToolId> = all.map { it.id }
}

data class ToolPreferences(
    val enabledOverrides: Map<ToolId, Boolean> = emptyMap(),
    val order: List<ToolId> = emptyList(),
) {
    fun isEnabled(id: ToolId): Boolean =
        enabledOverrides[id] ?: ToolRegistry.byId[id]?.defaultEnabled ?: false
}

data class ToolSection(val group: ToolGroup, val tools: List<Tool>)
data class ResolvedTools(
    val sections: List<ToolSection>,
    val banner: Tool?,
    val tools: List<Tool>,
)

fun resolveTools(prefs: ToolPreferences, today: Set<ToolContext>): ResolvedTools {
    val visible = ToolRegistry.all
        .filter { prefs.isEnabled(it.id) }
        .filter { it.context == null || it.context in today }

    val orderIndex = buildMap {
        prefs.order.forEachIndexed { i, id -> put(id, i) }
        ToolRegistry.defaultOrder.forEachIndexed { i, id ->
            putIfAbsent(id, prefs.order.size + i)
        }
    }
    val sorted = visible.sortedBy { orderIndex[it.id] ?: Int.MAX_VALUE }

    val banner = sorted.firstOrNull { it.context != null }
    val tools = sorted.filter { it != banner }

    val sections = ToolGroup.entries.mapNotNull { g ->
        tools.filter { it.group == g }.takeIf { it.isNotEmpty() }
            ?.let { ToolSection(g, it) }
    }
    return ResolvedTools(sections, banner, tools)
}