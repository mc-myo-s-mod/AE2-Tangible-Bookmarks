<p align="center">
<img src="readme/AE2TB.png" width="200">
</p>
<h1 align="center">AE2 TangibleBookmarks</h1>
<p align="center">
<a href="https://www.curseforge.com/minecraft/mc-mods/ae2-tangible-bookmarks">
<img src="https://img.shields.io/curseforge/dt/1394275?style=flat-square&logo=curseforge&color=f16436">
</a>

<a href="https://modrinth.com/mod/ae2-tangible-bookmarks">
<img src="https://img.shields.io/modrinth/dt/ae2-tangible-bookmarks?style=flat-square&logo=modrinth&color=00af5c">
</a>

<img src="https://img.shields.io/github/license/mc-myo-s-mod/AE2-Tangible-Bookmarks?style=flat-square">

</p>

Bookmark your frequently used items in JEI/EMI/REI and grab them instantly without typing in the search bar!


## Feature

- Middle Click: Pick up a single item.
- Shift + Middle Click: Pick up a full stack of items.
- Ctrl + Middle Click: Open the Auto-crafting request window.

## Description
AE2 Tangible Bookmarks significantly improves the workflow between Applied Energistics 2 (AE2) and JE/EMI/REI

Normally, retrieving an item from the AE2 terminal requires searching for it by name. With this mod, you can directly interact with items displayed in JEI's Bookmarks (Left) or (EMI/REI)’s favorite while the AE2 terminal is open.

## Development

This repository is a multi-module build:

- `common` contains shared resources used by every loader module.
- `forge-1-20-1` builds the Forge 1.20.1 artifact with Java 17.
- `neoforge-1-21-1` builds the NeoForge 1.21.1 artifact with Java 21.
- `neoforge-26-1-2` builds the NeoForge 26.1.2 artifact with its native Gradle 9 wrapper and Java 25.

Build from the repository root:

```bash
gradlew.bat :forge-1-20-1:build --console=plain
gradlew.bat :neoforge-1-21-1:build --console=plain
gradlew.bat :neoforge-26-1-2:build --console=plain
```

Run the root Gradle wrapper itself with Java 21; the modules keep separate Java toolchains for their target Minecraft versions (17, 21, and 25).

`common` resources are copied into loader builds only when the loader module does not provide the same path. This keeps the Forge 1.20.1 texture override local while sharing the current texture with newer versions.

CI runs on pushes to `master`, pull requests targeting `master`, and manual dispatch. It builds all three Minecraft versions. New commits cancel superseded CI runs.

To publish, merge the changes into `master`, create a tag `v<minecraft_version>-<mod_version>` matching that module's `gradle.properties`, then publish its GitHub Release. Pushing a tag alone does not publish. The workflow checks that the tag belongs to `master`, builds only the selected module, attaches the JAR to the release, and publishes to CurseForge and Modrinth using `CURSEFORGE_TOKEN` and `MODRINTH_TOKEN`. Mod and Myotus `-SNAPSHOT` versions are rejected. Forge releases use the bundled `-all.jar` artifact, uploaded with the normal JAR filename.

Tags matching the current module properties and their Myotus requirements are:

| Target | Release tag | Myotus |
| --- | --- | --- |
| Forge 1.20.1 | `v1.20.1-15.1.0` | `15.1.0` |
| NeoForge 1.21.1 | `v1.21.1-19.1.3` | `19.1.1` |
| NeoForge 26.1.2 | `v26.1.2-26.0.0` | `26.0.0` |

The Myotus versions above are resolved from Maven Central by the module builds.
If re-releasing an existing 1.20.1 or 1.21.1 line, including `v1.21.1-19.1.3`, increment that module's `mod_version` and publish a new tag instead of reusing an already-published tag.
