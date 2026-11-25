# PT8

Version: 0.4.2
Date: 2026-11-21

## Added

- feat: tasbih — add ReaderSheet bottom action Row (Pakai dzikir ini + Tutup); Tutup always enabled, Pakai disabled when not selectable or already in use
- feat: tasbih — persist last opened dhikr and last tab via TasbihDataStore, auto-select on reopen

## Changed

- changed: tasbih — split TasbihPage into components/ (ReaderSheet, ReaderPage, etc.)
- changed: nav — detail tools navigation via manual onOpenTool params, add ToolDetailViewModel for detail shell settings

## Fixed

- fix: tasbih — prevent ReaderSheet from jumping on fast fling overscroll; sheet now full height (heightFraction = 1f), fling swallowed, drag still dismisses
- fix: tasbih — favorites on top, guided display uses saved last mode
