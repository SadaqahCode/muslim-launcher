# PT7

Version: 0.4.2
Date: 2026-11-21

## Added

- feat: i18n base — add localized res/values-<lang>/strings.xml for 6 locales: en, id, ar, ms, ur, hi (defaults stay in res/values/strings.xml)
- feat: i18n — add res/xml/locales_config.xml with <locale android:name="…"/> per supported language, declared as android:localeConfig in the manifest
- feat: i18n — add user setting for language picker, wired into SettingsDataStore and applied via AppCompatDelegate.setApplicationLocales
- feat: ui — add ScrimDialog and ScrimBottomSheet helpers under ui/components/ (shared dim + dismiss pattern for ModalBottomSheet / Dialog)
- feat: tasbih — add HapticMode enum (Haptic, Vibration, Silent) with selectable feedback persisted via TasbihDataStore
- feat: tasbih — add horizontal progress/loading bar at the top of the page while the repository refreshes

## Changed

- changed: data layer — move data/local/ to data/local/store/, update imports across SettingsDataStore, DhikrLocalDataSource, TasbihPrefs, ToolsPreferences

## Fixed

- fix: drawer — resolve duplicate LazyColumn key for the same package installed in work and personal profiles; keys now include the user/profile id
- fix: tasbih — loading bar lingered ~1600 ms after a cache hit; min-visible delay now only applies to real network responses, cache reads hide instantly
