package com.sadaqahcode.muslimlauncher.data.launcher

import android.content.Context
import android.content.pm.LauncherApps
import android.content.pm.PackageManager
import android.os.Build
import android.os.Process
import android.os.UserHandle
import android.os.UserManager
import androidx.compose.ui.unit.Density
import com.sadaqahcode.muslimlauncher.domain.model.AppIconShape
import com.sadaqahcode.muslimlauncher.domain.model.LauncherApp
import kotlin.math.roundToInt

class InstalledAppsRepository(private val context: Context) {

    private val iconCache = mutableMapOf<String, androidx.compose.ui.graphics.ImageBitmap>()
    private var cachedShape: AppIconShape? = null

    fun loadLaunchableApps(shape: AppIconShape, density: Density): List<LauncherApp> {
        if (cachedShape != shape) {
            iconCache.clear()
            cachedShape = shape
        }
        val sizePx = (ICON_RENDER_DP * density.density).roundToInt()
        val composeShape = shapeFor(shape)

        val viaLauncherApps = runCatching { loadViaLauncherApps(shape, density, sizePx, composeShape) }.getOrNull()
        if (!viaLauncherApps.isNullOrEmpty()) return viaLauncherApps

        return loadViaPackageManager(shape, density, sizePx, composeShape)
    }

    private fun loadViaLauncherApps(
        shape: AppIconShape, density: Density, sizePx: Int, composeShape: androidx.compose.ui.graphics.Shape
    ): List<LauncherApp> {
        val launcherApps = context.getSystemService(Context.LAUNCHER_APPS_SERVICE) as LauncherApps
        val userManager = context.getSystemService(Context.USER_SERVICE) as UserManager
        val profiles = userManager.userProfiles

        val densityDpi = context.resources.displayMetrics.densityDpi

        val result = mutableListOf<LauncherApp>()
        for (profile in profiles) {
            val userId = profile.hashCode() // UserHandle.hashCode() == userId, works on min 24
            val isWork = isWorkProfile(userManager, profile, userId)
            val activities = try {
                launcherApps.getActivityList(null, profile)
            } catch (e: SecurityException) {
                continue
            }
            for (info in activities) {
                if (info.componentName.packageName == context.packageName) continue
                val cacheKey = "${info.componentName.packageName}|$userId|${info.componentName.className}"
                val icon = iconCache.getOrPut(cacheKey) {
                    info.getBadgedIcon(densityDpi).toShapedBitmap(sizePx, composeShape, density)
                }
                result.add(
                    LauncherApp(
                        packageName = info.componentName.packageName,
                        label = info.label.toString(),
                        icon = icon,
                        userId = userId,
                        isWork = isWork,
                        componentName = info.componentName.flattenToString(),
                        userHandle = profile,
                    )
                )
            }
        }
        return result.distinctBy { it.packageName + "|" + it.userId + "|" + it.componentName }
            .sortedBy { it.label.lowercase() }
    }

    // use reflection so it still compiles + runs on min 24
    private fun isWorkProfile(userManager: UserManager, profile: UserHandle, userId: Int): Boolean {
        if (userId == Process.myUserHandle().hashCode()) return false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return runCatching {
                UserManager::class.java
                    .getMethod("isManagedProfile", Int::class.javaPrimitiveType)
                    .invoke(userManager, userId) as? Boolean
            }.getOrNull() ?: (userId != 0)
        }
        // API 24-29: no isManagedProfile(userId), Private Space doesn't exist yet,
        // so any non-personal profile is work
        return userId != 0
    }

    private fun loadViaPackageManager(
        shape: AppIconShape, density: Density, sizePx: Int, composeShape: androidx.compose.ui.graphics.Shape
    ): List<LauncherApp> {
        val pm = context.packageManager
        val intent = android.content.Intent(android.content.Intent.ACTION_MAIN).apply {
            addCategory(android.content.Intent.CATEGORY_LAUNCHER)
        }
        val resolved = pm.queryIntentActivities(intent, PackageManager.MATCH_ALL)
        return resolved.asSequence()
            .filter { it.activityInfo.packageName != context.packageName }
            .map { ri ->
                val pkg = ri.activityInfo.packageName
                val icon = iconCache.getOrPut("$pkg|0") {
                    ri.activityInfo.loadIcon(pm).toShapedBitmap(sizePx, composeShape, density)
                }
                LauncherApp(packageName = pkg, label = ri.loadLabel(pm).toString(), icon = icon, userId = 0, isWork = false)
            }
            .distinctBy { it.packageName }
            .sortedBy { it.label.lowercase() }
            .toList()
    }

    private companion object { const val ICON_RENDER_DP = 96 }
}