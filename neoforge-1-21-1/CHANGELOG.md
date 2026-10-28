# Changelog

## Unreleased

- Added an opt-in terminal config checkbox to show AE storage amounts in the JEI/EMI ingredient list, independently of bookmark amounts.
- Render visible list amounts using one AE key lookup map per grid draw; bookmark interactions remain unchanged.

## 19.1.5 (Minecraft 1.21.1)

### Fixes
- Fixed crashes with JEI's rewritten input and ingredient rendering in 19.64, preserving bookmark interactions and storage amount displays.
- Fixed JEI `19.70.0.460` compatibility without requiring a separate build for older supported JEI versions.

### Compatibility
- Requires JEI `19.64.0.455` or newer when JEI is installed.
- Updated Myotus to the CurseForge release `19.1.2` and requires that version or newer.

## 19.1.4 (Minecraft 1.21.1)

### Compatibility
- Updated JEI to `19.57.0.446`; requires JEI `19.55.0.432` or newer.

### Fixes
- Fixed a crash with newer JEI versions when initializing bookmark input handlers.

## 19.1.1 (Minecraft 1.21.1)

### Compatibility
- Updated Myotus to `19.1.1`.
- Raised the minimum supported NeoForge version to `21.1.238`.

### Fixes
- Fixed the JEI bookmark overlay mixin target lookup.
