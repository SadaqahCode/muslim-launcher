# PT3

Version: 0.4.1
Date: 2026-11-17

## Added

- feat: update AppContextMenu UX
- feat: add Hilt and DI
- feat: add tools models
- feat: add nav tools
- feat: widgets reorder — same swipe / back save pattern as Tools, WidgetPage pageActive + saveAndClose
- feat: ReorderWidgetList — onDraftChanged with widgetIds, shortcutIds, removedWidgetIds, removedShortcutIds
- feat: add nav tools detail — AppRoutes tools/detail/{toolId}, AppNavHost composable, HomeScreen onOpenTool wiring
- feat: ToolDetailScreen — TopAppBar title + centered desc body

## Fixed

- fix: tools reorder — swipe to Home saves draft, back saves draft via pageActive + saveAndClose
- fix: ReorderToolsList — single source of truth, removed duplicate orderedIds / enabled state, draft emitted on every move / toggle
- fix: ReorderToolsList — implicit it shadowing in onToggle, renamed to isChecked
- fix: nav tools detail — type inference failed for ToolRegistry.byId, convert String key to ToolId first
