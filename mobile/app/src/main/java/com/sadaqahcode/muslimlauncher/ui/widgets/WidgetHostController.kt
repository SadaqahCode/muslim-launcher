package com.sadaqahcode.muslimlauncher.ui.widgets

import android.appwidget.AppWidgetHostView
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProviderInfo
import android.content.ComponentName
import android.content.Context
import com.flagodna.debugprint.debugPrint
import com.sadaqahcode.muslimlauncher.MainActivity
import com.sadaqahcode.muslimlauncher.data.repository.WidgetPlacementRepository

private const val TAG = "WidgetHostController"

internal class WidgetHostController(
    private val appContext: Context,
    private val repository: WidgetPlacementRepository,
) {
    var errorTextColor: Int = android.graphics.Color.GRAY

    private val appWidgetManager: AppWidgetManager =
        AppWidgetManager.getInstance(appContext)

    private val host get() = MainActivity.appWidgetHost

    fun start() { /* lifecycle managed by MainActivity */ }
    fun stop() { /* lifecycle managed by MainActivity */ }

    fun availableProviders(): List<AppWidgetProviderInfo> =
        appWidgetManager.installedProviders
            .filter { info ->
                info.widgetCategory and
                        AppWidgetProviderInfo.WIDGET_CATEGORY_HOME_SCREEN != 0
            }
            .sortedBy { it.loadLabel(appContext.packageManager).toString().lowercase() }

    fun providerInfoFor(widgetId: Int): AppWidgetProviderInfo? =
        runCatching { appWidgetManager.getAppWidgetInfo(widgetId) }.getOrNull()

    fun allocate(): Int {
        val id = host.allocateAppWidgetId()
        debugPrint.d(TAG, "[WidgetFlow] allocate ✓ id=$id")
        return id
    }

    fun bind(widgetId: Int, provider: ComponentName): Boolean {
        val bound = runCatching {
            appWidgetManager.bindAppWidgetIdIfAllowed(widgetId, provider)
        }.getOrDefault(false)
        debugPrint.d(
            TAG,
            "[WidgetFlow] bind id=$widgetId provider=${provider.flattenToString()} → $bound"
        )
        return bound
    }

    suspend fun completeAdd(widgetId: Int, providerName: String) {
        debugPrint.d(TAG, "[WidgetFlow] completeAdd → id=$widgetId provider=$providerName")
        repository.add(widgetId = widgetId, providerName = providerName)
        debugPrint.d(TAG, "[WidgetFlow] completeAdd ✓ id=$widgetId")
    }

    fun cancel(widgetId: Int) {
        debugPrint.d(TAG, "[WidgetFlow] cancel id=$widgetId")
        runCatching { host.deleteAppWidgetId(widgetId) }
            .onFailure { debugPrint.w(TAG, "[WidgetFlow] cancel ✗ $widgetId: ${it.message}") }
    }

    suspend fun remove(widgetId: Int) {
        debugPrint.d(TAG, "[WidgetFlow] remove id=$widgetId")
        runCatching { host.deleteAppWidgetId(widgetId) }
            .onFailure { debugPrint.w(TAG, "[WidgetFlow] remove host ✗ $widgetId: ${it.message}") }
        repository.remove(widgetId)
    }

    suspend fun cleanOrphans(installedProviders: List<String>) {
        if (installedProviders.isEmpty()) return
        val orphans = repository.findOrphans(installedProviders)
        if (orphans.isEmpty()) return
        debugPrint.d(TAG, "[WidgetFlow] cleanOrphans → ${orphans.size} orphans")
        orphans.forEach { row ->
            runCatching { host.deleteAppWidgetId(row.widgetId) }
        }
        repository.deleteOrphans(installedProviders)
    }

    fun createView(
        activityContext: Context,
        widgetId: Int,
        errorTextColor: Int = this.errorTextColor,
    ): AppWidgetHostView? {
        val info = providerInfoFor(widgetId)
        if (info == null) {
            debugPrint.e(
                TAG,
                "[WidgetFlow] createView id=$widgetId ✗ providerInfo NULL — widget unbound or deleted"
            )
            return null
        }
        return runCatching {
            host.createView(activityContext, widgetId, info).also { view ->
                if (view is LauncherAppWidgetHostView) {
                    view.errorTextColor = errorTextColor
                }
                debugPrint.d(
                    TAG,
                    "[WidgetFlow] createView id=$widgetId ✓ view=${view?.javaClass?.simpleName}"
                )
            }
        }.onFailure {
            debugPrint.e(
                TAG,
                "[WidgetFlow] createView id=$widgetId ✗ ${it.javaClass.simpleName}: ${it.message}"
            )
        }.getOrNull()
    }
}