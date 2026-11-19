package com.sadaqahcode.muslimlauncher.ui.tools.helper

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Calculate
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.RadioButtonChecked
import androidx.compose.material.icons.outlined.VolunteerActivism
import androidx.compose.ui.graphics.vector.ImageVector
import com.sadaqahcode.muslimlauncher.domain.model.ToolId

fun toolIcon(id: ToolId): ImageVector = when (id) {
    ToolId.Qibla   -> Icons.Outlined.Explore
    ToolId.Tasbih  -> Icons.Outlined.RadioButtonChecked
    ToolId.Salawat -> Icons.Outlined.Favorite
    ToolId.Adhkar  -> Icons.AutoMirrored.Outlined.MenuBook
    ToolId.Hijri   -> Icons.Outlined.CalendarMonth
    ToolId.Fasting -> Icons.Outlined.DarkMode
    ToolId.Sadaqah -> Icons.Outlined.VolunteerActivism
    ToolId.Zakat   -> Icons.Outlined.Calculate
    // phase 5 — one branch per tool. Exhaustive: compiler forces it.
    ToolId.PrayerTimes -> TODO()
    ToolId.QadhaTracker -> TODO()
    ToolId.QuranTracker -> TODO()
}