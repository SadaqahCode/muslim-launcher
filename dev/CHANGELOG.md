## CHANGELOG

### 0.4.2 (2026-11-20)

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