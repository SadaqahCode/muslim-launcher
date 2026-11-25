# PT9

Version: 0.4.2
Date: 2026-11-23

## Added

- feat: nav — add center dot / Home pill listener button to action; tapping the center dot returns to the Home page, not FAB
- feat: home — add HomeExt.kt as the single source for page widths and layout
- feat: home — add AutoHomeLayout with dynamic columns and pinned grid for small and large screens
- feat: tasbih — Selesai / Done opens library drawer / ModalSidebar, keeping previous data until a selection is made
- feat: home — show CircularProgressIndicator in pinned grid while loading

## Changed

- changed: home — HomeExt.kt becomes the single source for PageMaxWidthScreen, Medium, Large, ThreeColumnMinWidth, and pageMaxWidthFor()
- changed: nav — update app navigation transition to soft fade / snap, without zoom / scale or stuck fast fade
- changed: nav — make bottom bar barHorizontalShift dynamic: maxWidth \* 0.25f for isLandscape && !isLargeLandscape && homePageIndex, otherwise 0.dp
- changed: tasbih — MainPane uses onDone parameter instead of actions.onExitGuided
- changed: tasbih — Done only opens drawer; exitGuided() runs only after a selection is made
- changed: tasbih — update page UX and auto-save last dhikr, last mode, and last tab via TasbihDataStore; auto-select on reopen
- changed: home — improve UX on small and large screens with dynamic pageMaxWidthFor() and AutoHomeLayout

## Fixed

- fix: nav — disable predictive back with android:enableOnBackInvokedCallback="false" in AndroidManifest.xml; system back now uses appbar back / onClose without inconsistent slide / fade
- fix: tasbih — Done ignored onDone, so the drawer never opened
- fix: tasbih — Done reset data even when the drawer was closed without selecting; previous finished data is now preserved
- fix: home — replace intentionally empty pinned grid loading gap with CircularProgressIndicator
- fix: home — correct bottom bar shift from always -25% / 0f in landscape to +25% right only for small-landscape Home
