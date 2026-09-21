---
navigation:
  title: "AE2 Tangible Bookmarks"
  position: 80
---

# AE2 Tangible Bookmarks

AE2 Tangible Bookmarks能将物品管理器模组（JEI/EMI/REI）的书签和AE2的终端连接起来。

使用AE2终端时，点击JEI、EMI、REI中的书签/收藏物品，即可直接向网络请求它们，无需再在终端搜索栏内输入对应名称。

## 物品
- <ItemImage id="ae2tb:terminal_bookmark_interact_card"/>[终端书签交互卡](terminal_bookmark_interact_card.md)

## 操作

打开支持的 ME 终端后，将鼠标指向物品书签：

- 中键：取出一个物品。
- Shift + 中键：取出一组物品。
- Ctrl + 中键：打开自动合成请求。

所有支持的配方查看器共用 AE2TB 的按键设置，可在 Minecraft 的控制菜单或 AE2TB 终端配置页中修改。

这些操作基于物品堆。显示流体或自定义 AE 资源的存储量，并不代表支持直接取出该资源。

## 配置

默认情况下，书签交互和存储量显示都需要在终端升级槽中安装终端书签交互卡。将 `ae2tb-common.toml` 中的 `upgrade.QoL` 设为 `true` 可取消此要求。

通过 `ae2tb-client.toml` 中的 `display.showBookmarkAmounts` 或 AE2TB 终端配置页切换数量显示。将 `ae2tb-common.toml` 中的 `performance.enableBookmarkAmountCounting` 设为 `false` 可停用数量查询。这些数量设置不会禁用物品交互。

支持的终端菜单保持打开时，包括切换到配方查看器页面后，仍可显示存储量。GuideME 页面中不显示这些数量。

JEI 支持物品和流体数量；自定义资源需要兼容的 AE2 JEI 集成转换器。EMI 使用已注册的 AE2 转换器。REI 仅支持可转换为物品堆的收藏项。
