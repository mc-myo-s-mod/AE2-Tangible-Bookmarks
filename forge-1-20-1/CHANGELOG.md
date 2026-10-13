# Changelog

## Unreleased

- Added an opt-in terminal config checkbox to show AE storage amounts in the JEI/EMI ingredient list, independently of bookmark amounts.
- Render visible list amounts using one AE key lookup map per grid draw; bookmark interactions remain unchanged.

## 15.1.0 (Minecraft 1.20.1)

### Fixes
- Fixed missing REI favorite amounts when fast entry rendering batches icons without calling the individual widget render method.
- Fixed REI input and bookmark amount mixin mappings in production Forge builds.

### Features
- Added common QoL config.
    - `QoL`: allows bookmark interaction and bookmark amount rendering without the Terminal Bookmark Interact Card.

### Versions
- Updated Myotus to `15.1.0`.
- Updated JEI integration to `15.32.0.173`; this is now the minimum version when JEI is installed.
