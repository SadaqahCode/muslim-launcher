package com.sadaqahcode.muslimlauncher.ui.tools.detail.pages

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.sadaqahcode.muslimlauncher.domain.model.Tool
import com.sadaqahcode.muslimlauncher.ui.theme.ArabicType
import com.sadaqahcode.muslimlauncher.ui.theme.GlassEmphasis
import com.sadaqahcode.muslimlauncher.ui.theme.glass
import com.sadaqahcode.muslimlauncher.ui.tools.detail.components.ToolPageDefaults
import com.sadaqahcode.muslimlauncher.ui.tools.detail.components.ToolTopAppBar
import kotlinx.coroutines.delay

private enum class Dhikr(val label: String, val arabic: String, val meaning: String) {
    Subhanallah("Subhanallah", "سُبْحَانَ اللَّهِ", "Glory be to Allah"),
    Alhamdulillah("Alhamdulillah", "الْحَمْدُ لِلَّهِ", "All praise is due to Allah"),
    AllahuAkbar("Allahu Akbar", "اللَّهُ أَكْبَرُ", "Allah is the Greatest"),
    LaIlaha("La ilaha illallah", "لَا إِلَهَ إِلَّا اللَّهُ", "There is no deity but Allah"),
    Astaghfirullah("Astaghfirullah", "أَسْتَغْفِرُ اللَّهَ", "I seek Allah's forgiveness"),
    CountOnly("Count only", "", "Count anything you like."),
}

private val Targets = listOf(33, 99, 100, 0)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TasbihPage(tool: Tool, onClose: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = colors.background,
        contentColor = colors.onBackground,
        topBar = {
            ToolTopAppBar(
                title = stringResource(tool.titleRes),
                onClose = onClose,
                scrollBehavior = scrollBehavior,
            )
        },
    ) { innerPadding ->
        TasbihContent(innerPadding)
    }
}

@Composable
private fun TasbihContent(contentPadding: PaddingValues) {
    val haptic = LocalHapticFeedback.current

    var dhikrIndex by rememberSaveable { mutableStateOf(0) }
    var target by rememberSaveable { mutableStateOf(33) }
    val counts: SnapshotStateList<Int> = rememberSaveable(
        saver = listSaver<SnapshotStateList<Int>, Int>(
            save = { it.toList() },
            restore = { it.toMutableStateList() },
        )
    ) { List(Dhikr.entries.size) { 0 }.toMutableStateList() }

    val dhikr = Dhikr.entries[dhikrIndex]
    val count = counts[dhikrIndex]

    var confirmingReset by remember { mutableStateOf(false) }
    LaunchedEffect(confirmingReset) {
        if (confirmingReset) {
            delay(3_000)
            confirmingReset = false
        }
    }

    val onTap: () -> Unit = {
        if (target in 1..count) {
            counts[dhikrIndex] = 0
            confirmingReset = false
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        } else {
            val next = count + 1
            counts[dhikrIndex] = next
            confirmingReset = false
            haptic.performHapticFeedback(
                if (target > 0 && next == target) HapticFeedbackType.LongPress
                else HapticFeedbackType.TextHandleMove
            )
        }
    }
    val onReset: () -> Unit = {
        if (confirmingReset) {
            counts[dhikrIndex] = 0
            confirmingReset = false
        } else {
            confirmingReset = true
        }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .padding(ToolPageDefaults.contentPadding(contentPadding)),
    ) {
        val landscape = maxWidth > maxHeight && maxHeight < 520.dp
        val compact = maxHeight < 600.dp

        if (landscape) {
            TasbihLandscape(
                dhikr = dhikr,
                dhikrIndex = dhikrIndex,
                count = count,
                target = target,
                confirmingReset = confirmingReset,
                onSelectDhikr = { i -> dhikrIndex = i; confirmingReset = false },
                onSelectTarget = { target = it },
                onTap = onTap,
                onReset = onReset,
            )
        } else {
            TasbihPortrait(
                dhikr = dhikr,
                dhikrIndex = dhikrIndex,
                count = count,
                target = target,
                confirmingReset = confirmingReset,
                compact = compact,
                dense = maxHeight < 460.dp,
                onSelectDhikr = { i -> dhikrIndex = i; confirmingReset = false },
                onSelectTarget = { target = it },
                onTap = onTap,
                onReset = onReset,
            )
        }
    }
}

@Composable
private fun TasbihPortrait(
    dhikr: Dhikr,
    dhikrIndex: Int,
    count: Int,
    target: Int,
    confirmingReset: Boolean,
    compact: Boolean,
    dense: Boolean,
    onSelectDhikr: (Int) -> Unit,
    onSelectTarget: (Int) -> Unit,
    onTap: () -> Unit,
    onReset: () -> Unit,
) {
    val colors = MaterialTheme.colorScheme

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        DhikrChipRow(
            selectedIndex = dhikrIndex,
            onSelect = onSelectDhikr,
            compact = compact,
        )

        Spacer(Modifier.height(if (compact) 12.dp else 20.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            if (dhikr.arabic.isNotEmpty()) {
                Text(
                    text = dhikr.arabic,
                    style = if (dense) ArabicType.headlineSmall else ArabicType.headlineMedium,
                    color = colors.onSurface,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                )
            }
            if (!dense) {
                Text(
                    text = dhikr.label,
                    style = if (compact) MaterialTheme.typography.titleMedium
                    else MaterialTheme.typography.titleLarge,
                    color = colors.onSurface,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = dhikr.meaning,
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            BoxWithConstraints {
                CounterDisc(
                    count = count,
                    target = target,
                    size = minOf(maxWidth, maxHeight, 360.dp),
                    onTap = onTap,
                )
            }
        }

        Spacer(Modifier.height(if (compact) 4.dp else 12.dp))
        TargetRow(
            target = target,
            compact = compact,
            onSelectTarget = onSelectTarget,
        )

        Spacer(Modifier.height(if (compact) 4.dp else 8.dp))
        ResetButton(
            confirmingReset = confirmingReset,
            enabled = count > 0,
            onClick = onReset,
        )
    }
}

@Composable
private fun TasbihLandscape(
    dhikr: Dhikr,
    dhikrIndex: Int,
    count: Int,
    target: Int,
    confirmingReset: Boolean,
    onSelectDhikr: (Int) -> Unit,
    onSelectTarget: (Int) -> Unit,
    onTap: () -> Unit,
    onReset: () -> Unit,
) {
    val colors = MaterialTheme.colorScheme

    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            DhikrChipRow(
                selectedIndex = dhikrIndex,
                onSelect = onSelectDhikr,
                compact = true,
            )

            Spacer(Modifier.height(8.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                if (dhikr.arabic.isNotEmpty()) {
                    Text(
                        text = dhikr.arabic,
                        style = ArabicType.headlineSmall,
                        color = colors.onSurface,
                        textAlign = TextAlign.Center,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Text(
                    text = dhikr.label,
                    style = MaterialTheme.typography.titleLarge,
                    color = colors.onSurface,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = dhikr.meaning,
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                BoxWithConstraints {
                    CounterDisc(
                        count = count,
                        target = target,
                        size = minOf(maxWidth, maxHeight),
                        onTap = onTap,
                    )
                }
            }

            TargetRow(
                target = target,
                compact = true,
                onSelectTarget = onSelectTarget,
            )
            Spacer(Modifier.height(4.dp))
            ResetButton(
                confirmingReset = confirmingReset,
                enabled = count > 0,
                onClick = onReset,
            )
        }
    }
}

@Composable
private fun DhikrChipRow(
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    compact: Boolean,
) {
    val scrollState = rememberScrollState()
    var viewportWidthPx by remember { mutableIntStateOf(0) }
    var rowStartPx by remember { mutableIntStateOf(-1) }
    val chipCenters = remember { mutableStateMapOf<Int, Int>() }

    LaunchedEffect(Dhikr.entries) {
        chipCenters.clear()
        scrollState.scrollTo(0)
    }

    LaunchedEffect(selectedIndex, viewportWidthPx, chipCenters.size) {
        if (viewportWidthPx == 0) return@LaunchedEffect
        if (chipCenters.size != Dhikr.entries.size) return@LaunchedEffect
        val centerX = chipCenters[selectedIndex] ?: return@LaunchedEffect
        val target = (centerX - viewportWidthPx / 2)
            .coerceIn(0, scrollState.maxValue)
        scrollState.animateScrollTo(target)
    }

    BoxWithConstraints(Modifier.fillMaxWidth()) {
        viewportWidthPx = constraints.maxWidth

        Row(
            modifier = Modifier
                .horizontalScroll(scrollState)
                .selectableGroup()
                .onGloballyPositioned {
                    rowStartPx = it.positionInRoot().x.toInt()
                },
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Dhikr.entries.forEachIndexed { index, d ->
                Box(
                    modifier = Modifier.onGloballyPositioned { coords ->
                        if (index !in chipCenters && rowStartPx >= 0) {
                            val contentX = coords.positionInRoot().x.toInt() -
                                    rowStartPx + scrollState.value
                            chipCenters[index] = contentX + coords.size.width / 2
                        }
                    }
                ) {
                    ChoiceChip(
                        label = d.label,
                        selected = index == selectedIndex,
                        onClick = { onSelect(index) },
                        compact = compact,
                    )
                }
            }
        }
    }
}

@Composable
private fun TargetRow(
    target: Int,
    compact: Boolean,
    onSelectTarget: (Int) -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .selectableGroup()
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Target",
                style = MaterialTheme.typography.labelSmall,
                color = colors.onSurfaceVariant,
                modifier = Modifier.padding(end = 4.dp),
            )
            Targets.forEach { t ->
                ChoiceChip(
                    label = when {
                        t != 0 -> "$t"
                        compact -> "∞"
                        else -> "None"
                    },
                    selected = t == target,
                    onClick = { onSelectTarget(t) },
                    compact = compact,
                )
            }
        }
    }
}

@Composable
private fun ResetButton(
    confirmingReset: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    Box(
        modifier = Modifier
            .heightIn(min = 44.dp)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = if (confirmingReset) "Tap again to reset" else "Reset",
            style = MaterialTheme.typography.labelMedium,
            color = if (enabled) colors.onSurface else colors.onSurfaceVariant,
        )
    }
}

@Composable
private fun CounterDisc(
    count: Int,
    target: Int,
    size: Dp,
    onTap: () -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    val done = target > 0 && count >= target

    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale = animateFloatAsState(
        targetValue = if (pressed) 0.97f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMedium,
        ),
        label = "discPress",
    )
    val progress = animateFloatAsState(
        targetValue = if (target > 0) (count.toFloat() / target).coerceIn(0f, 1f) else 0f,
        animationSpec = tween(durationMillis = 150),
        label = "discProgress",
    )
    val track = colors.onSurface.copy(alpha = 0.10f)
    val arc = colors.onSurface

    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .clickable(
                interactionSource = interaction,
                indication = null,
                role = Role.Button,
                onClickLabel = "Add one",
                onClick = onTap,
            )
            .semantics {
                contentDescription = "Tasbih counter"
                stateDescription = if (target > 0) "$count of $target" else "$count"
                liveRegion = LiveRegionMode.Polite
            },
        contentAlignment = Alignment.Center,
    ) {
        Canvas(Modifier.matchParentSize()) {
            val stroke = 6.dp.toPx()
            val inset = stroke / 2f
            val arcSize = Size(this.size.width - stroke, this.size.height - stroke)
            drawArc(
                color = track,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = Offset(inset, inset),
                size = arcSize,
                style = Stroke(width = stroke),
            )
            if (progress.value > 0f) {
                drawArc(
                    color = arc,
                    startAngle = -90f,
                    sweepAngle = 360f * progress.value,
                    useCenter = false,
                    topLeft = Offset(inset, inset),
                    size = arcSize,
                    style = Stroke(width = stroke, cap = StrokeCap.Round),
                )
            }
        }

        Column(
            modifier = Modifier
                .size(size - 28.dp)
                .graphicsLayer {
                    scaleX = scale.value
                    scaleY = scale.value
                }
                .glass(
                    shape = CircleShape,
                    emphasis = if (done) GlassEmphasis.Selected else GlassEmphasis.Regular,
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = "$count",
                style = MaterialTheme.typography.displayLarge.copy(
                    fontWeight = FontWeight.Light,
                    fontFeatureSettings = "tnum",
                ),
                color = colors.onSurface,
            )
            if (target > 0) {
                Text(
                    text = "of $target",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun ChoiceChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    compact: Boolean = false,
) {
    val colors = MaterialTheme.colorScheme
    val content = if (selected) colors.onSurface else colors.onSurfaceVariant
    val ring by animateColorAsState(
        targetValue = if (selected) colors.onSurface.copy(alpha = 0.7f) else Color.Transparent,
        animationSpec = tween(durationMillis = 150),
        label = "chipRing",
    )

    Box(
        modifier = Modifier
            .heightIn(min = 48.dp)
            .selectable(selected = selected, onClick = onClick, role = Role.RadioButton),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .height(if (compact) 34.dp else 40.dp)
                .glass(
                    shape = CircleShape,
                    emphasis = if (selected) GlassEmphasis.Selected else GlassEmphasis.Regular,
                )
                .border(width = 1.5.dp, color = ring, shape = CircleShape)
                .padding(horizontal = if (compact) 12.dp else 16.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = label,
                style = if (compact) MaterialTheme.typography.labelMedium
                else MaterialTheme.typography.labelLarge,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                color = content,
            )
        }
    }
}