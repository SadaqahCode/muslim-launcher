# PT6

Version: 0.4.1
Date: 2026-11-20

## Added

- feat: Hilt DI — add DataStoreModule with 4 qualified DataStore providers and DatabaseModule with Room + DAO providers
- feat: add DataStore qualifiers — @UserSettingsDataStore, @DhikrDataStore, @TasbihDataStore, @ToolsPreferencesDataStore

## Changed

- changed: data sources — SettingsDataStore, DhikrLocalDataSource, TasbihPrefs, ToolPreferencesRepository now inject qualified DataStore instead of Context
- changed: repositories — InstalledApps, AppPreferences, WidgetPlacement, Shortcut, ToolPreferences now use @Singleton + @Inject constructors
- changed: ViewModels — HomeViewModel, SettingsViewModel, WidgetPageViewModel now use @HiltViewModel
- changed: call sites — replace viewModel() with hiltViewModel() across AppNavHost, SettingsScreen, HomeScreen, and WidgetPage

## Fixed

- fix: multiple active DataStores — centralize delegates in DataStoreModule
- fix: MissingBinding chain — provide repositories, DAOs, and qualified DataStores
- fix: Cannot create ViewModel at runtime — use HiltViewModelFactory via hiltViewModel()
- fix: remove DatabaseProvider — build Room through DatabaseModule singleton
