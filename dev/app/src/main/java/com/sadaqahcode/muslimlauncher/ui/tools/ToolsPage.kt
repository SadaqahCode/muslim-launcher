package com.sadaqahcode.muslimlauncher.ui.tools

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBackIos
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Calculate
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Nightlight
import androidx.compose.material.icons.outlined.RadioButtonChecked
import androidx.compose.material.icons.outlined.VolunteerActivism
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sadaqahcode.muslimlauncher.domain.model.AppTextSize
import com.sadaqahcode.muslimlauncher.ui.home.helper.doubleTapGesture
import com.sadaqahcode.muslimlauncher.ui.theme.LocalLegibleColors
import com.sadaqahcode.muslimlauncher.ui.tools.components.ContextualBanner
import com.sadaqahcode.muslimlauncher.ui.tools.components.ToolCard
import my.nanihadesuka.compose.LazyVerticalGridScrollbar
import my.nanihadesuka.compose.ScrollbarSettings

private val PageMaxWidth = 600.dp

/** ≥ this width → 3 columns. Below → 2. Keeps cards ≥ ~145dp on any device. */
private val ThreeColumnMinWidth = 520.dp

enum class ToolId {
    Qibla, Tasbih, Salawat, Adhkar,
    Hijri, Fasting, Sadaqah, Zakat,
    Friday, Ramadan, AyyamAlBid,
}

@Composable
fun ToolsPage(
    viewModel: ToolsPageViewModel,
    textScale: AppTextSize,
    onOpenTool: (ToolId) -> Unit,
    onDoubleTapHome: () -> Unit,
    onBack: () -> Unit,
) {
    val lc = LocalLegibleColors.current

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val qiblaBearing by viewModel.qiblaBearing.collectAsStateWithLifecycle()
    val tasbihCount by viewModel.tasbihCount.collectAsStateWithLifecycle()
    val tasbihRound by viewModel.tasbihRound.collectAsStateWithLifecycle()
    val salawatCount by viewModel.salawatCount.collectAsStateWithLifecycle()
    val salawatGoal by viewModel.salawatGoal.collectAsStateWithLifecycle()
    val adhkarProgress by viewModel.adhkarProgress.collectAsStateWithLifecycle()
    val adhkarSession by viewModel.adhkarSession.collectAsStateWithLifecycle()
    val hijriDay by viewModel.hijriDay.collectAsStateWithLifecycle()
    val hijriMonth by viewModel.hijriMonth.collectAsStateWithLifecycle()
    val hijriYear by viewModel.hijriYear.collectAsStateWithLifecycle()
    val hijriWeekday by viewModel.hijriWeekday.collectAsStateWithLifecycle()
    val fastingThisWeek by viewModel.fastingThisWeek.collectAsStateWithLifecycle()
    val fastingGoal by viewModel.fastingGoal.collectAsStateWithLifecycle()
    val sadaqahThisMonth by viewModel.sadaqahThisMonth.collectAsStateWithLifecycle()
    val sadaqahAmount by viewModel.sadaqahAmount.collectAsStateWithLifecycle()
    val lastZakatAmount by viewModel.lastZakatAmount.collectAsStateWithLifecycle()
    val lastZakatAge by viewModel.lastZakatAge.collectAsStateWithLifecycle()
    val contextual by viewModel.contextual.collectAsStateWithLifecycle()
    val ramadanDay by viewModel.ramadanDay.collectAsStateWithLifecycle()

    val softScrollbarSettings = ScrollbarSettings.Default.copy(
        thumbThickness = 5.dp,
        thumbUnselectedColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.9f),
        thumbSelectedColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.8f),
        scrollbarPadding = 0.dp,
    )

    val gridState = rememberLazyGridState()

    Box(
        modifier = Modifier.fillMaxSize().doubleTapGesture(
            onDoubleTap = onDoubleTapHome,
            isGatingOk = { true }
        ),
        contentAlignment = Alignment.TopCenter,
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = PageMaxWidth)
                .fillMaxSize(),
        ) {
            // Header:
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(
                        start = 4.dp,
                        end = 16.dp,
                        top = if (isLandscape) 4.dp else 16.dp,
                        bottom = if (isLandscape) 8.dp else 16.dp,
                    ),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBackIos,
                        contentDescription = "Back to home",
                        tint = lc.scheme.onBackground,
                        modifier = Modifier.size(14.dp),
                    )
                }
                Column(Modifier.weight(1f)) {
                    Text(
                        text = "Tools",
                        style = textScale.scaled(MaterialTheme.typography.titleLarge),
                        fontWeight = FontWeight.Medium,
                        color = lc.scheme.onBackground,
                    )
                    Text(
                        text = "Daily Islamic essentials",
                        style = textScale.scaled(MaterialTheme.typography.bodySmall),
                        color = lc.scheme.onBackground.copy(alpha = 0.72f),
                    )
                }
            }

            BoxWithConstraints(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            ) {
                val columns = if (maxWidth >= ThreeColumnMinWidth) 3 else 2

                LazyVerticalGridScrollbar(
                    state = gridState,
                    settings = softScrollbarSettings,
                    modifier = Modifier.fillMaxSize(),
                ) {
                    LazyVerticalGrid(
                        state = gridState,
                        columns = GridCells.Fixed(columns),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            start = 28.dp,
                            end = 28.dp,
                            top = 8.dp,
                            bottom = 38.dp,
                        ),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        // Contextual banner
                        contextual?.let { ctx ->
                            item(
                                key = "contextual-banner",
                                span = { GridItemSpan(maxLineSpan) },
                            ) {
                                when (ctx) {
                                    ContextualTool.Friday -> ContextualBanner(
                                        icon = Icons.Outlined.WbSunny,
                                        title = "Jumu'ah Mubarak",
                                        subtitle = "Read Surah Al-Kahf · Send salawat",
                                        onClick = { onOpenTool(ToolId.Friday) },
                                    )
                                    ContextualTool.Ramadan -> ContextualBanner(
                                        icon = Icons.Outlined.Nightlight,
                                        title = "Ramadan Day $ramadanDay",
                                        subtitle = "Keep your fast · Taraweeh tonight",
                                        onClick = { onOpenTool(ToolId.Ramadan) },
                                    )
                                    ContextualTool.AyyamAlBid -> ContextualBanner(
                                        icon = Icons.Outlined.DarkMode,
                                        title = "Ayyam al-Bid",
                                        subtitle = "Sunna fast today ($hijriDay ${hijriMonth.substringBefore(' ')})",
                                        onClick = { onOpenTool(ToolId.AyyamAlBid) },
                                    )
                                }
                            }
                        }

                        // Worship
                        item(key = "qibla") {
                            QiblaCard(
                                bearing = qiblaBearing,
                                onClick = { onOpenTool(ToolId.Qibla) },
                            )
                        }
                        item(key = "tasbih") {
                            TasbihCard(
                                count = tasbihCount,
                                round = tasbihRound,
                                onClick = { onOpenTool(ToolId.Tasbih) },
                            )
                        }
                        item(key = "salawat") {
                            SalawatCard(
                                count = salawatCount,
                                goal = salawatGoal,
                                onClick = { onOpenTool(ToolId.Salawat) },
                            )
                        }
                        item(key = "adhkar") {
                            AdhkarCard(
                                done = adhkarProgress.first,
                                total = adhkarProgress.second,
                                session = adhkarSession,
                                onClick = { onOpenTool(ToolId.Adhkar) },
                            )
                        }

                        // Calendar & Habits
                        item(key = "hijri") {
                            HijriCard(
                                day = hijriDay,
                                month = hijriMonth,
                                year = hijriYear,
                                weekday = hijriWeekday,
                                onClick = { onOpenTool(ToolId.Hijri) },
                            )
                        }
                        item(key = "fasting") {
                            FastingCard(
                                thisWeek = fastingThisWeek,
                                goal = fastingGoal,
                                onClick = { onOpenTool(ToolId.Fasting) },
                            )
                        }
                        item(key = "sadaqah") {
                            SadaqahCard(
                                countThisMonth = sadaqahThisMonth,
                                amount = sadaqahAmount,
                                onClick = { onOpenTool(ToolId.Sadaqah) },
                            )
                        }
                        item(key = "zakat") {
                            ZakatCard(
                                lastAmount = lastZakatAmount,
                                lastAge = lastZakatAge,
                                onClick = { onOpenTool(ToolId.Zakat) },
                            )
                        }
                    }
                }
            }
        }
    }
}

// Individual cards

@Composable
private fun QiblaCard(bearing: Float, onClick: () -> Unit) {
    ToolCard(
        icon = Icons.Outlined.Explore,
        title = "Qibla",
        liveValue = "${bearing.toInt()}° ${bearingToCompass(bearing)}",
        context = "From North",
        onClick = onClick,
    )
}

@Composable
private fun TasbihCard(count: Int, round: Int, onClick: () -> Unit) {
    ToolCard(
        icon = Icons.Outlined.RadioButtonChecked,
        title = "Tasbih",
        liveValue = count.toString(),
        context = "Round $round",
        onClick = onClick,
    )
}

@Composable
private fun SalawatCard(count: Int, goal: Int, onClick: () -> Unit) {
    ToolCard(
        icon = Icons.Outlined.Favorite,
        title = "Salawat",
        liveValue = "$count / $goal",
        context = "Today's salawat",
        onClick = onClick,
    )
}

@Composable
private fun AdhkarCard(
    done: Int,
    total: Int,
    session: String,
    onClick: () -> Unit,
) {
    ToolCard(
        icon = Icons.Outlined.MenuBook,
        title = "Adhkar",
        liveValue = "$done / $total",
        context = "$session adhkar",
        onClick = onClick,
    )
}

@Composable
private fun HijriCard(
    day: Int,
    month: String,
    year: Int,
    weekday: String,
    onClick: () -> Unit,
) {
    val shortMonth = month.substringBefore(' ')
    ToolCard(
        icon = Icons.Outlined.CalendarMonth,
        title = "Hijri",
        liveValue = "$day $shortMonth",
        context = "$year AH · $weekday",
        onClick = onClick,
    )
}

@Composable
private fun FastingCard(thisWeek: Int, goal: Int, onClick: () -> Unit) {
    ToolCard(
        icon = Icons.Outlined.DarkMode,
        title = "Fasting",
        liveValue = "$thisWeek / $goal",
        context = "Sunna fasts this week",
        onClick = onClick,
    )
}

@Composable
private fun SadaqahCard(
    countThisMonth: Int,
    amount: String?,
    onClick: () -> Unit,
) {
    ToolCard(
        icon = Icons.Outlined.VolunteerActivism,
        title = "Sadaqah",
        liveValue = amount ?: "None",
        context = if (countThisMonth > 0) {
            "$countThisMonth ${if (countThisMonth == 1) "gift" else "gifts"} · this month"
        } else {
            "This month"
        },
        onClick = onClick,
    )
}

@Composable
private fun ZakatCard(
    lastAmount: String?,
    lastAge: String,
    onClick: () -> Unit,
) {
    ToolCard(
        icon = Icons.Outlined.Calculate,
        title = "Zakat",
        liveValue = lastAmount ?: "Calculate",
        context = if (lastAmount != null) lastAge else "Yearly 2.5%",
        onClick = onClick,
    )
}

/** 0–360° → 8-point compass label. */
private fun bearingToCompass(bearing: Float): String {
    val directions = listOf("N", "NE", "E", "SE", "S", "SW", "W", "NW")
    val normalized = ((bearing % 360f) + 360f) % 360f
    val index = (((normalized + 22.5f) / 45f).toInt()) % 8
    return directions[index]
}