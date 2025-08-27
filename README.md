[![Java](https://img.shields.io/badge/Java-17%2B-orange.svg)](https://adoptium.net/)
[![Server](https://img.shields.io/badge/Paper%2FSpigot-1.20%E2%80%931.21-blue.svg)](https://papermc.io/)
[![License: MIT](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)
[![Release](https://img.shields.io/github/v/release/JobbeDeluxe/SimpleGuide?sort=semver)](https://github.com/JobbeDeluxe/SimpleGuide/releases)
[![Downloads](https://img.shields.io/github/downloads/JobbeDeluxe/SimpleGuide/total.svg)](https://github.com/JobbeDeluxe/SimpleGuide/releases)


# SimpleGuide v0.3.1 (Paper 1.21.x)

**SimpleGuide** is a lightweight “what should I do next?” helper for Paper/Spigot 1.20–1.21.
It shows three actionable hints **top-right** (scoreboard): (1) your next logical **advancement**,
(2–3) two **context-aware tips** (food, shelter, tools, etc.). Includes a **GUI**, a **help book**,
and a **real locate** navigator (bossbar arrow + compass target).

_Kurz:_ Zeigt oben rechts deinen nächsten logischen **Fortschritt** und zwei **Kontext-Tipps**.
Mit **GUI**, **Hilfe-Buch** und **Navigator** (echtes locate).


Advancement Coach + Navigator + Context Assistant — **Sidebar HUD (top-right)**, GUI & help book, and **real locate**.

## What’s fixed in 0.2.6
- Back to **Sidebar** only (no multi-bossbar HUD).
- Sidebar anchored **top-right** (scores 15..12), minimal lines, no fillers.
- **German advancements** via built-in `translations/de_de.yml` (drop a fuller file to override).
- **GUI** shows localized structure names (Village/Dorf, Fortress/Netherfestung, ...).
- **Bossbar navigation**: dynamic arrow updates each second; first static arrow removed via auto-migration of message files.
- Locate target **Y** moved to terrain surface.

## Commands
```
/guide                # open GUI
/guide on|off         # toggle navigator
/guide book           # get the help book (one per player)
/guide target <x> <y> <z>
/guide locate <structure>   # village | fortress | stronghold | monument
```

## Build
```bash
mvn -q -DskipTests package
```
Jar: `target/simpleguide-0.2.6-shaded.jar`


## 0.2.8
- Fix compile errors by removing unicode migration code.
- Sidebar anchored to rows **3,2,1**; `sidebar.show_usage_line` default **false**.
- Navigator bossbar text built internally to avoid stale arrows.


## 0.2.9
- Added DE translation for **Who is Cutting Onions?** (`nether/obtain_crying_obsidian`).
- Book page 1: headings in **gold**, titles in **white**, hints in **gray** for better readability.


## 0.3.0
- **Vollständige deutsche Advancement-Texte** via Adventure `GlobalTranslator` – automatisch anhand der Spielersprache.
- Reihenfolge der Fallbacks: YAML-Override → GlobalTranslator (DE) → goals.yml → Schlüssel.


## 0.3.1
- **Book readability**: dark colors (headers DARK_BLUE, content BLACK).
- **Config migration**: on first run, config is bumped to `configVersion: 4` and defaults enforced (`display.mode: sidebar`, `sidebar.show_usage_line: false`).
- **DE localization**: still uses GlobalTranslator, with fallback to our `de_de.yml` if the rendered text looks English.

## Screenshots

### Overview
![Overview](/img/Overview.png)

### In-game Book
![Book](/img/book.png)

### GUI
![GUI](/img/gui.png)

### Navigator
![Navigator](/img/nav.png)


## How it works

1. **Sidebar HUD (top-right)** – Three lines only, pinned using scores **3, 2, 1** (no filler lines).
2. **GUI** – `/guide` opens a small menu: toggle navigator, get the help book, or quick-**locate** (Village/Fortress/Stronghold/Monument).
3. **Help book** – `/guide book` (max. one per player). Inside you can click to open the GUI or toggle the navigator.
4. **Navigator** – `/guide on|off`, `/guide target <x> <y> <z>`, `/guide locate <structure>`.
   The bossbar shows a dynamic **direction arrow** and distance; your compass points to the target.
5. **Localization** – Advancement titles/descriptions follow the **player’s language** (Adventure GlobalTranslator).
   You can override texts by dropping `plugins/SimpleGuide/translations/de_de.json` or `de_de.yml`.
6. **Config defaults** – `display.mode: sidebar`, `sidebar.show_usage_line: false`.
   Old configs are migrated automatically to the latest `configVersion` on startup.

### Commands
```
/guide                # open GUI
/guide on|off         # toggle navigator
/guide book           # get the help book (one per player)
/guide target <x> <y> <z>
/guide locate <structure>   # village | fortress | stronghold | monument
```

### Permissions
- `simpleguide.use` (default: true)
- `simpleguide.admin` (default: op)
