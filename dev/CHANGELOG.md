## CHANGELOG

### 0.4.3 (2026-11-25)

##### Added
- `PT12` feat: common/layout — new reusable `SoftScrollbar`
- `PT12` feat: project — add root-level `THIRD-PARTY-NOTICES.md`

##### Changed
- `PT12` changed: typography — Arabic font switched from Uthmani to Amiri
- `PT12` updated: common/layout — `ScreenLayout` updated to support `SoftScrollbar`
- `PT12` updated: components — `AdaptiveSidebarLayout` adds `permanentSidebar: Boolean` and `onDrawerOpen`

##### Fixed
- `PT12` fix: scrolling — fix scroll indicator behavior on scrollable pages

---

##### Added
- `PT11` feat: common — reusable `AppBarPage`, `ScreenLayout`, `ScreenTopPadding` under `ui/common/layout`
- `PT11` feat: common — reusable `AdaptiveSidebarLayout` with `sidebarWindowInsets` / `contentWindowInsets` for edge-to-edge sidebar + content panes
- `PT11` feat: settings — new "Extra space at top" toggle (`paddingTopEnabled`); when off, ignores `paddingTop` **and** forces short `TopAppBar`
- `PT11` feat: tasbih — `LibraryContent` fade-in on open / replay on done (`Animatable` + trigger counter)
- `PT11` feat: components — `ScrimBottomSheet.showRounded` now renders 28dp top corners (was a no-op)

##### Changed
- `PT11` changed: navigation — `NavHost` transitions replaced with modern fade (220ms in / 160ms out, `FastOutSlowInEasing` / `LinearOutSlowInEasing`)
- `PT11` changed: tools — `ToolsPage` refactored to `AppBarPage`; reorder-mode overlay unchanged
- `PT11` changed: widgets — `WidgetPage` refactored to `AppBarPage`, wired `paddingTop` / `useCollapsingBar` / `paddingTopEnabled` (previously accepted but ignored)
- `PT11` changed: tools detail — `PlaceholderToolPage` and `NotFoundPage` refactored to `AppBarPage`, inherit bar mode + top padding from `rememberScreenLayout()`
- `PT11` changed: tools detail — `ToolDetailScreen` computes `paddingTop` / `useCollapsingBar` / `paddingTopEnabled` inline; `ToolPageContent` typealias now receives `topTransparentModal`
- `PT11` changed: tasbih — `TasbihPage` sets `contentWindowInsets = WindowInsets(0)`; progress indicator overlays content (no reserved 2dp, no layout shift)
- `PT11` changed: ux — snackbar polish
- `PT11` changed: home gesture — shade down-swipe observed at `PointerEventPass.Initial` (no longer skipped when scrollables consume the drag)

##### Fixed
- `PT11` fix: navigation — surface flash during crossfade over transparent pages (transitions no longer expose `NavHost` background)
- `PT11` fix: common — `AppBarPage` large bar no longer extra-tall when `paddingTopEnabled = false` (short bar enforced, padding ignored)
- `PT11` fix: tasbih — double top padding when inner `Scaffold` insets were left on
- `PT11` fix: home gesture — down-swipe to expand notification shade did not trigger on pages with scrollable content

---

### 0.4.2 (2026-11-20)
 
##### Added
- `PT10` feat: settings — `setWallpaperMode(v, schemeBackground)`, System = `colorScheme.background`, applied to lockscreen
- `PT10` feat: settings — `setDarkMode(v, schemeBackground)` + `LaunchedEffect` listener for System wallpaper
- `PT10` feat: home — rotate listener, pager remap / clamp

##### Changed
- `PT10` changed: settings — System uses bg image path, no `clear()` / read system file
- `PT10` changed: tools — auto scroll top on close / unfocus / back to Home

##### Fixed
- `PT10` fix: settings — stale `wallpaperColor` copy, `READ_EXTERNAL_STORAGE` `SecurityException` on System
- `PT10` fix: settings — `MaterialTheme` inside `remember { }` crash
- `PT10` fix: home — test AVD S8 Android 9 rotate layout broken, pager out of range

---

##### Added
- `PT9` feat: nav — center dot / Home pill listener button to action, tap center dot = back to home page, not FAB
- `PT9` feat: home — `HomeExt.kt` single source for page widths / layout
- `PT9` feat: home — `AutoHomeLayout` dynamic columns / pinned grid for small + large screen
- `PT9` feat: tasbih — `Selesai / Done` opens library drawer / `ModalSidebar`, keeps prev data until pick
- `PT9` feat: home — pinned grid `CircularProgressIndicator` while `loading`

##### Changed
- `PT9` changed: home — `HomeExt.kt` single source for `PageMaxWidthScreen` / `Medium` / `Large` / `ThreeColumnMinWidth` / `pageMaxWidthFor()`
- `PT9` changed: nav — app nav transition effect updated, soft fade / snap, no zoom / scale, no stuck fast fade
- `PT9` changed: nav — bottom bar `barHorizontalShift` dynamic: `maxWidth * 0.25f` for `isLandscape && !isLargeLandscape && homePageIndex`, else `0.dp`
- `PT9` changed: tasbih — `MainPane` uses `onDone` param, not `actions.onExitGuided`
- `PT9` changed: tasbih — `Done` only opens drawer, `exitGuided()` only after pick
- `PT9` changed: tasbih — UX page update, auto-save logic: last dhikr / last mode / last tab via `TasbihDataStore`, auto-select on reopen
- `PT9` changed: home — UX fix both small screen and large screen dynamic, `pageMaxWidthFor()` + `AutoHomeLayout`

##### Fixed
- `PT9` fix: nav — disable predictive back `android:enableOnBackInvokedCallback="false"` in `AndroidManifest.xml`, system back = appbar back / `onClose`, no inconsistent slide / fade
- `PT9` fix: tasbih — `Done` ignored `onDone`, drawer never opened
- `PT9` fix: tasbih — `Done` reset data even if drawer closed without picking, now prev finished data kept
- `PT9` fix: home — pinned grid loading was `intentionally empty` blank gap, now circular loading
- `PT9` fix: home — bottom bar shift always `-25%` / `0f` in landscape, now `+25%` right only for small landscape home

---

##### Added
- `PT8` feat: tasbih — `ReaderSheet` bottom action `Row` (`Pakai dzikir ini` + `Tutup`), `Tutup` always enabled, `Pakai` disabled when not selectable / in use
- `PT8` feat: tasbih — persist last open dhikr / last tab via `TasbihDataStore`, auto-select on reopen

##### Changed
- `PT8` changed: tasbih — split `TasbihPage` into `components/` (`ReaderSheet`, `ReaderPage`, etc.)
- `PT8` changed: nav — detail tools navigation via manual `onOpenTool` params, `ToolDetailViewModel` for detail shell settings

##### Fixed
- `PT8` fix: tasbih — `ReaderSheet` jump on fast fling overscroll; sheet now full height (`heightFraction = 1f`), fling swallowed, drag still dismisses
- `PT8` fix: tasbih — favorites on top, guided display uses saved last mode

---

#### Added
- `PT7` feat: i18n base — localized `res/values-<lang>/strings.xml` for 6 locales: en, id, ar, ms, ur, hi (defaults stay in `res/values/strings.xml`)
- `PT7` feat: i18n — `res/xml/locales_config.xml` with `<locale android:name="…"/>` per supported language, declared as `android:localeConfig` in the manifest
- `PT7` feat: i18n — user setting for language picker, wired into `SettingsDataStore` and applied via `AppCompatDelegate.setApplicationLocales`
- `PT7` feat: ui — `ScrimDialog` and `ScrimBottomSheet` helpers under `ui/components/` (shared dim + dismiss pattern for `ModalBottomSheet` / `Dialog`)
- `PT7` feat: tasbih — `HapticMode` enum (`Haptic`, `Vibration`, `Silent`) with selectable feedback persisted via `TasbihDataStore`
- `PT7` feat: tasbih — horizontal progress/loading bar at the top of the page while the repository refreshes

#### Changed
- `PT7` changed: data layer — moved `data/local/` → `data/local/store/`, updated imports across `SettingsDataStore`, `DhikrLocalDataSource`, `TasbihPrefs`, `ToolsPreferences`

#### Fixed
- `PT7` fix: drawer — duplicate LazyColumn `key` for the same package installed in work and personal profiles; keys now include the user/profile id
- `PT7` fix: tasbih — loading bar lingered ~1600 ms after a cache hit; min-visible delay now only applies to real network responses, cache reads hide instantly

---

#### Added
- `PT6` feat: Hilt DI — DataStoreModule with 4 qualified DataStore providers, DatabaseModule with Room + DAO providers
- `PT6` feat: DataStore qualifiers — @UserSettingsDataStore, @DhikrDataStore, @TasbihDataStore, @ToolsPreferencesDataStore

#### Changed
- `PT6` changed: data sources — SettingsDataStore, DhikrLocalDataSource, TasbihPrefs, ToolPreferencesRepository inject qualified DataStore instead of Context
- `PT6` changed: repositories — InstalledApps, AppPreferences, WidgetPlacement, Shortcut, ToolPreferences now @Singleton + @Inject constructor
- `PT6` changed: ViewModels — HomeViewModel, SettingsViewModel, WidgetPageViewModel now @HiltViewModel
- `PT6` changed: call sites — viewModel() → hiltViewModel() across AppNavHost, SettingsScreen, HomeScreen, WidgetPage

#### Fixed
- `PT6` fix: multiple active DataStores — delegates centralized in DataStoreModule
- `PT6` fix: MissingBinding chain — repositories, DAOs, qualified DataStores all provided
- `PT6` fix: Cannot create ViewModel at runtime — HiltViewModelFactory via hiltViewModel()
- `PT6` fix: DatabaseProvider removed — Room now built via DatabaseModule singleton

---

### 0.4.1 (2026-11-17)

#### Added
- `PT1` feat: text size - AppTextSize with scale, Medium = 1.0, scaled for TextUnit and TextStyle
- `PT1` feat: update ui ux - AppContextMenu
- `PT2` feat: add border - DrawerChipsRow
- `PT3` feat: update ux - AppContextMenu
- `PT3` feat: add hilt and DI
- `PT3` feat: add tools models
- `PT3` feat: add nav tools
- `PT3` feat: widgets reorder - same swipe / back save pattern as Tools, WidgetPage pageActive + saveAndClose
- `PT3` feat: ReorderWidgetList - onDraftChanged with widgetIds, shortcutIds, removedWidgetIds, removedShortcutIds
- `PT3` feat: add nav tools detail - AppRoutes tools/detail/{toolId}, AppNavHost composable, HomeScreen onOpenTool wiring
- `PT3` feat: ToolDetailScreen - TopAppBar title + centered desc body
- `PT4` feat: shade swipe - notification shade swipe now works on Widgets page too, updated Home gating, no listener in WidgetPage
- `PT5` feat: NetworkModule - OkHttp + Retrofit setup
- `PT5` feat: HMAC for challenge - HMAC signing for /challenge/get
- `PT5` feat: SessionInterceptor - auto fetch challenge, add session to dhikr
- `PT5` feat: SessionAuthenticator - auto refresh challenge on 401
- `PT5` feat: HmacSigningInterceptor - HMAC only for challenge

#### Changed
- `PT1` changed: color settings button

#### Fixed
- `PT1` fix: drawer gesture - swipe up works on Home and Widget, disabled on Tools
- `PT3` fix: tools reorder - swipe to Home saves draft, back saves draft via pageActive + saveAndClose
- `PT3` fix: ReorderToolsList - single source of truth, removed duplicate orderedIds / enabled state, draft emitted on every move / toggle
- `PT3` fix: ReorderToolsList - implicit it shadowing in onToggle, renamed to isChecked
- `PT3` fix: nav tools detail - type inference failed for ToolRegistry.byId, convert String key to ToolId first
- `PT5` fix: HMAC string-to-sign - timestamp, method, path, body
- `PT5` fix: HMAC key - raw 64 bytes

---

### 0.4.0 (2026-11-11)

#### Added
- feat: home - pinned apps, dock, wallpaper, prayer hero
- feat: app drawer - search, categories, drag handle, work profile tabs, pause / resume
- feat: app menu - pin, dock, category, app info, uninstall
- feat: widgets - add, resize, long press to edit
- feat: gestures - swipe down shade, swipe up drawer, double tap to lock
- feat: system - transparent system bars, light / dark theme
- feat: default launcher check - snackbar with Set default action
- feat: default launcher snackbar - Set default action, re-shows on return to Home
- chore: tools page - dummy UI only, no real calculation yet

#### Fixed
- fix: widget error view - uses colorScheme