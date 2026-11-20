package com.sadaqahcode.muslimlauncher.ui.widgets

import android.app.Application
import android.appwidget.AppWidgetHostView
import android.appwidget.AppWidgetProviderInfo
import android.content.Intent
import android.content.pm.ResolveInfo
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.flagodna.debugprint.debugPrint
import com.sadaqahcode.muslimlauncher.data.local.SettingsDataStore
import com.sadaqahcode.muslimlauncher.data.local.ShortcutEntity
import com.sadaqahcode.muslimlauncher.data.repository.ShortcutRepository
import com.sadaqahcode.muslimlauncher.data.repository.WidgetPlacementRepository
import com.sadaqahcode.muslimlauncher.domain.model.UserSettings
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

private const val TAG = "WidgetPageViewModel"

enum class WidgetPreset(val heightDp: Int, val label: String) {
    Auto(0, "Auto"),
    Small(120, "Small"),
    Medium(200, "Medium"),
    Large(320, "Large"),
}

@HiltViewModel
class WidgetPageViewModel @Inject constructor(
    app: Application,
    private val widgetRepository: WidgetPlacementRepository,
    private val shortcutRepository: ShortcutRepository,
    private val settingsDs: SettingsDataStore,
) : AndroidViewModel(app) {

    private val controller = WidgetHostController(app, widgetRepository)

    val placements = widgetRepository.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val shortcuts: StateFlow<List<ShortcutEntity>> = shortcutRepository.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val settings: StateFlow<UserSettings> = settingsDs.settings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UserSettings())

    private val _pickerOpen = MutableStateFlow(false)
    val pickerOpen: StateFlow<Boolean> = _pickerOpen.asStateFlow()

    private val _availableProviders = MutableStateFlow<List<AppWidgetProviderInfo>>(emptyList())
    val availableProviders: StateFlow<List<AppWidgetProviderInfo>> = _availableProviders.asStateFlow()

    private val _availableShortcuts = MutableStateFlow<List<ResolveInfo>>(emptyList())
    val availableShortcuts: StateFlow<List<ResolveInfo>> = _availableShortcuts.asStateFlow()

    private val _pickerLoading = MutableStateFlow(false)
    val pickerLoading: StateFlow<Boolean> = _pickerLoading.asStateFlow()

    init {
        debugPrint.d(TAG, "[WidgetFlow] init → controller.start()")
        controller.start()
        viewModelScope.launch {
            val installed = withContext(Dispatchers.IO) {
                controller.availableProviders().map { it.provider.flattenToString() }
            }
            debugPrint.d(TAG, "[WidgetFlow] init → cleaning orphans against ${installed.size} providers")
            controller.cleanOrphans(installed)
        }
    }

    override fun onCleared() {
        debugPrint.d(TAG, "[WidgetFlow] onCleared → controller.stop()")
        controller.stop()
        super.onCleared()
    }

    fun openPicker() {
        debugPrint.d(TAG, "[WidgetFlow] openPicker → start")
        _pickerOpen.value = true
        _pickerLoading.value = true

        viewModelScope.launch {
            try {
                val app = getApplication<Application>()

                val providers = withContext(Dispatchers.IO) {
                    controller.availableProviders()
                }
                debugPrint.d(TAG, "[WidgetFlow] openPicker → providers=${providers.size}")

                val shortcuts = withContext(Dispatchers.IO) {
                    app.packageManager
                        .queryIntentActivities(Intent(Intent.ACTION_CREATE_SHORTCUT), 0)
                        .sortedBy {
                            it.loadLabel(app.packageManager).toString().lowercase()
                        }
                }
                debugPrint.d(TAG, "[WidgetFlow] openPicker → shortcuts=${shortcuts.size}")

                _availableProviders.value = providers
                _availableShortcuts.value = shortcuts
                debugPrint.d(TAG, "[WidgetFlow] openPicker ✓ ready")
            } catch (e: Exception) {
                debugPrint.e(TAG, "[WidgetFlow] openPicker ✗ ${e::class.simpleName}: ${e.message}")
            } finally {
                _pickerLoading.value = false
            }
        }
    }

    fun closePicker() {
        debugPrint.d(TAG, "[WidgetFlow] closePicker")
        _pickerOpen.value = false
    }

    fun allocateWidgetId(): Int? {
        val id = runCatching { controller.allocate() }.getOrNull()
        if (id == null) {
            debugPrint.e(TAG, "[WidgetFlow] allocateWidgetId ✗ returned null")
        } else {
            debugPrint.d(TAG, "[WidgetFlow] allocateWidgetId ✓ id=$id")
        }
        return id
    }

    fun bindWidget(widgetId: Int, provider: AppWidgetProviderInfo): Boolean {
        val bound = controller.bind(widgetId, provider.provider)
        debugPrint.d(
            TAG,
            "[WidgetFlow] bindWidget id=$widgetId " +
                    "provider=${provider.provider.flattenToString()} → bound=$bound"
        )
        return bound
    }

    fun completeAdd(widgetId: Int, provider: AppWidgetProviderInfo) {
        debugPrint.d(
            TAG,
            "[WidgetFlow] completeAdd → id=$widgetId " +
                    "provider=${provider.provider.flattenToString()}"
        )
        viewModelScope.launch {
            try {
                controller.completeAdd(widgetId, provider.provider.flattenToString())
                // Force new widgets to Small instead of Auto (0)
                widgetRepository.updateHeight(widgetId, WidgetPreset.Small.heightDp)
                debugPrint.d(TAG, "[WidgetFlow] completeAdd ✓ id=$widgetId saved to DB")
                _pickerOpen.value = false
            } catch (e: Exception) {
                debugPrint.e(TAG, "[WidgetFlow] completeAdd ✗ ${e::class.simpleName}: ${e.message}")
            }
        }
    }

    fun cancelAdd(widgetId: Int) {
        debugPrint.d(TAG, "[WidgetFlow] cancelAdd id=$widgetId")
        controller.cancel(widgetId)
    }

    fun removeWidget(widgetId: Int) {
        debugPrint.d(TAG, "[WidgetFlow] removeWidget id=$widgetId")
        viewModelScope.launch { controller.remove(widgetId) }
    }


    fun createViewFor(
        activityContext: android.content.Context,
        widgetId: Int,
        errorTextColor: Int,
    ): AppWidgetHostView? {
        controller.errorTextColor = errorTextColor
        return controller.createView(activityContext, widgetId)
    }

    fun autoHeightDpFor(widgetId: Int): Int {
        val info = controller.providerInfoFor(widgetId) ?: return 200

        return when {
            // 1. Modern widgets (Android 12+) — use the exact cells the author requested
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && info.targetCellHeight > 0 -> {
                // Still need an approximation, but this is the closest without a real grid
                (info.targetCellHeight * 80).coerceIn(80, 520)
            }

            // 2. Older widgets — use the real minHeight the author set
            info.minHeight > 0 -> {
                pxToDp(info.minHeight).toInt().coerceIn(80, 520)
            }

            else -> 200
        }
    }

    private fun pxToDp(px: Int): Float {
        val density = getApplication<Application>().resources.displayMetrics.density
        return px / density
    }

    fun resizeWidget(widgetId: Int, preset: WidgetPreset) {
        debugPrint.d(TAG, "[WidgetFlow] resizeWidget id=$widgetId preset=${preset.label}")
        viewModelScope.launch(Dispatchers.IO) {
            widgetRepository.updateHeight(widgetId, preset.heightDp)
        }
    }

    fun reorderWidgets(orderedIds: List<Int>) {
        debugPrint.d(TAG, "[WidgetFlow] reorderWidgets size=${orderedIds.size}")
        viewModelScope.launch(Dispatchers.IO) {
            orderedIds.forEachIndexed { index, id ->
                widgetRepository.updatePosition(id, index)
            }
        }
    }

    fun reorderShortcuts(orderedIds: List<String>) {
        debugPrint.d(TAG, "[WidgetFlow] reorderShortcuts size=${orderedIds.size}")
        viewModelScope.launch(Dispatchers.IO) {
            orderedIds.forEachIndexed { index, id ->
                shortcutRepository.updatePosition(id, index)
            }
        }
    }

    fun completeAddShortcut(
        source: ResolveInfo,
        shortcutIntent: Intent,
        shortcutLabel: String,
    ) {
        debugPrint.d(
            TAG,
            "[WidgetFlow] completeAddShortcut pkg=${source.activityInfo.packageName} " +
                    "label=$shortcutLabel"
        )
        val app = getApplication<Application>()
        val fallbackLabel = shortcutLabel.ifBlank {
            source.loadLabel(app.packageManager).toString()
        }
        viewModelScope.launch(Dispatchers.IO) {
            shortcutRepository.add(
                id = UUID.randomUUID().toString(),
                packageName = source.activityInfo.packageName,
                activityName = source.activityInfo.name,
                intentUri = shortcutIntent.toUri(Intent.URI_INTENT_SCHEME),
                label = fallbackLabel,
            )
            debugPrint.d(TAG, "[WidgetFlow] completeAddShortcut ✓ saved")
            _pickerOpen.value = false
        }
    }

    fun removeShortcut(id: String) {
        debugPrint.d(TAG, "[WidgetFlow] removeShortcut id=$id")
        viewModelScope.launch(Dispatchers.IO) {
            shortcutRepository.remove(id)
        }
    }

    fun launchShortcut(activityContext: android.content.Context, shortcut: ShortcutEntity) {
        debugPrint.d(TAG, "[WidgetFlow] launchShortcut id=${shortcut.id} label=${shortcut.label}")
        runCatching {
            val intent = Intent.parseUri(shortcut.intentUri, Intent.URI_INTENT_SCHEME)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            activityContext.startActivity(intent)
        }.onFailure {
            debugPrint.e(TAG, "[WidgetFlow] launchShortcut ✗ ${it.message}")
        }
    }
}