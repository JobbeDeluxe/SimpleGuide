# SimpleGuide v0.3.0 (Paper 1.21.x)

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
