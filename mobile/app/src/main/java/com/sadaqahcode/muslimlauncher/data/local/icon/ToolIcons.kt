package com.sadaqahcode.muslimlauncher.data.local.icon

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.EventNote
import androidx.compose.material.icons.automirrored.outlined.LibraryBooks
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Alarm
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.Calculate
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Cake
import androidx.compose.material.icons.outlined.CardGiftcard
import androidx.compose.material.icons.outlined.Checklist
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Fastfood
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Flight
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.Navigation
import androidx.compose.material.icons.outlined.Paid
import androidx.compose.material.icons.outlined.PieChart
import androidx.compose.material.icons.outlined.RadioButtonChecked
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.SelfImprovement
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.VolunteerActivism
import androidx.compose.ui.graphics.vector.ImageVector
import com.sadaqahcode.muslimlauncher.domain.model.ToolId

fun toolIcon(id: ToolId): ImageVector = when (id) {
    ToolId.Qibla -> Icons.Outlined.Explore
    ToolId.Tasbih -> Icons.Outlined.RadioButtonChecked
    ToolId.Salawat -> Icons.Outlined.Favorite
    ToolId.Adhkar -> Icons.AutoMirrored.Outlined.MenuBook
    ToolId.Hijri -> Icons.Outlined.CalendarMonth
    ToolId.Fasting -> Icons.Outlined.DarkMode
    ToolId.Sadaqah -> Icons.Outlined.VolunteerActivism
    ToolId.Zakat -> Icons.Outlined.Calculate
    ToolId.PrayerTimes -> Icons.Outlined.AccessTime
    ToolId.QadhaTracker -> Icons.Outlined.History
    ToolId.QuranTracker -> Icons.Outlined.Book
    ToolId.KhatamPlanner -> Icons.AutoMirrored.Outlined.EventNote
    ToolId.AmalTracker -> Icons.Outlined.Checklist
    ToolId.HafalanTracker -> Icons.Outlined.School
    ToolId.DuaCollection -> Icons.Outlined.FavoriteBorder
    ToolId.AsmaulHusna -> Icons.Outlined.Star
    ToolId.PrayerFocus -> Icons.Outlined.SelfImprovement
    ToolId.ImsakIftar -> Icons.Outlined.Restaurant
    ToolId.FidyahCalculator -> Icons.Outlined.Paid
    ToolId.KaffarahGuide -> Icons.Outlined.Info
    ToolId.RamadanPlanner -> Icons.Outlined.Event
    ToolId.EidCountdown -> Icons.Outlined.Cake
    ToolId.HadithReference -> Icons.AutoMirrored.Outlined.LibraryBooks
    ToolId.IslamicKnowledge -> Icons.Outlined.Lightbulb
    ToolId.Manzil -> Icons.Outlined.Shield
    ToolId.IslamicEvents -> Icons.Outlined.DateRange
    ToolId.Jumuah -> Icons.Outlined.Groups
    ToolId.TravelPrayerGuide -> Icons.Outlined.Flight
    ToolId.FaraidCalculator -> Icons.Outlined.PieChart
    ToolId.DebtTracker -> Icons.AutoMirrored.Outlined.ReceiptLong
    ToolId.WasiatNotes -> Icons.Outlined.Description
    ToolId.HalalExpenseTracker -> Icons.Outlined.AccountBalanceWallet
    ToolId.SadaqahPlanner -> Icons.Outlined.CardGiftcard
    ToolId.MasjidFinder -> Icons.Outlined.LocationOn
    ToolId.PrayerTravel -> Icons.Outlined.Map
    ToolId.QiblaTravel -> Icons.Outlined.Navigation
    ToolId.HalalFoodFinder -> Icons.Outlined.Fastfood
    ToolId.IslamCalculator -> Icons.Outlined.Calculate
    ToolId.IslamicCountdown -> Icons.Outlined.Event
    ToolId.RemindMe -> Icons.Outlined.Alarm
}