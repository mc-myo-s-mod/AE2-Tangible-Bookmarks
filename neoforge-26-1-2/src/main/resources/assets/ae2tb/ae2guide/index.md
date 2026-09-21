---
navigation:
  title: "AE2 Tangible Bookmarks"
  position: 80
---

# AE2 Tangible Bookmarks

AE2 Tangible Bookmarks connects your item list mod(JEI/REI) bookmarks to AE2 terminals.

When you are using an AE2 terminal, you can point at a bookmarked or favorited item in JEI or REI and request that item directly from the network without typing it into the terminal search first.

## Items
- <ItemImage id="ae2tb:terminal_bookmark_interact_card"/>[Terminal Bookmark Interact Card](terminal_bookmark_interact_card.md)

## Controls

With a supported ME terminal open, point at an item bookmark:

- Middle-click: extract one item.
- Shift + middle-click: extract a stack.
- Ctrl + middle-click: open an autocrafting request.

All supported viewers use AE2TB's key bindings. Change them in Minecraft's Controls menu or the AE2TB terminal configuration tab.

These actions use item stacks. Displaying a fluid or custom AE resource amount does not add direct extraction support for that resource.

## Configuration

By default, bookmark interaction and storage amount display require a Terminal Bookmark Interact Card in the terminal upgrade slot. Set `upgrade.QoL=true` in `ae2tb-common.toml` to remove the card requirement.

Toggle `display.showBookmarkAmounts` in `ae2tb-client.toml` or the AE2TB terminal configuration tab to show or hide amounts. Set `performance.enableBookmarkAmountCounting=false` in `ae2tb-common.toml` to disable amount lookups. These amount settings do not disable item interaction.

Amounts remain visible on recipe-viewer screens while a supported terminal menu is open. They are hidden on GuideME screens.

JEI and REI support item and fluid amounts through their AE2 conversion paths; custom resources require a matching converter. EMI is not supported in this version.
