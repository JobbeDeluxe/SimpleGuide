# SimpleGuide v0.2.1 (Paper 1.21.x)

Advancement Coach + Navigator + Context Assistant — **sidebar (top-right), GUI, help book**, and **real locate**.

## Fixes in 0.2.1
- Sidebar now **pinned to top-right** (padding lines), not mid-right.
- Book titles/text no longer show `bold=not_set...` (proper plain text extraction).
- Bossbar **updates direction & distance** every second while a target is set.
- Only **one** guide book per player (PDC tag), no duplicates.
- Book adds a **GUI link** on the last page.

## Features
- **Sidebar (Scoreboard)** shows the **Top 3** next steps:
  1) always **Advancement-based**
  2) **Context tip**
  3) **Another context tip**
  + usage hint line `/guide book`
- **GUI** (`/guide` or `/guide gui`): toggle navigator, get book, quick locate (Stronghold/Fortress/Village), clear target.
- **Help Book** (`/guide book`): pages with next steps + clickable ON/OFF buttons + GUI link.
- **Navigator**: sets **Compass** target and a **BossBar** direction with distance.
- **Real locate**: uses Bukkit's structure locate via reflection (supports multiple API variants) to find exact coordinates.
- **Multilingual**: English default; German automatically if the player's locale is German.

## Commands
```
/guide                # open GUI
/guide on|off         # toggle navigator
/guide book           # get the help book
/guide target <x> <y> <z>
/guide locate <structure>   # village | fortress | stronghold | monument
```

## Build
```bash
mvn -q -DskipTests package
```
Jar: `target/simpleguide-0.2.1-shaded.jar`
