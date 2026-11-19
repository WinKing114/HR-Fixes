<p align="center">
  <img src="https://img.shields.io/badge/Minecraft-1.20.1-5b9c51?style=flat" alt="Minecraft 1.20.1" />
  <img src="https://img.shields.io/badge/Forge-47.4.x-5b9c51?style=flat" alt="Forge 47.4.x" />
  <img src="https://img.shields.io/badge/Java-17-5b9c51?style=flat" alt="Java 17" />
  <a href="https://github.com/WinKing114/HR-Fixes/issues"><img src="https://img.shields.io/badge/GitHub-Issues-5b9c51?style=flat" alt="GitHub Issues" /></a>
  <img src="https://img.shields.io/badge/License-MIT-5b9c51?style=flat" alt="MIT License" />
</p>

# HollowRealm Fixes

**HollowRealm Fixes** (formerly **HR Fixes**) is a lightweight code-level compatibility and bug-fix mod built for the **HollowRealm** modpack on **Minecraft 1.20.1 (Forge)**. It fixes confirmed crashes, thread-safety bugs, and broken UI/JEI integrations using version- and bytecode-gated Java/Mixin patches—without changing gameplay balance, recipes, quests, or progression.

Install this mod if you are playing HollowRealm (or running the same audited mod versions) and want silent, fail-closed fixes for vanilla and cross-mod defects. Data-pack and recipe/loot condition fixes live separately in **[HollowRealm Tweaks](https://www.curseforge.com/minecraft/mc-mods/hollowrealm-tweaks)**.

---

## What's Included

Every patch runs through `HRFixesMixinPlugin` (or a guarded Forge CoreMod) and **fails closed**: if a target mod is absent, updated to an unaudited version, or has unexpected bytecode, that patch skips cleanly without crashing startup.

- **Vanilla Chat Line-Height Crash Guard (`ChatLineHeightGuardMixin`)**  
  Prevents divide-by-zero crashes in `ChatComponent` when chat line spacing evaluates to an integer line height `< 1` or non-finite values (`NaN` / `±Infinity`), falling back safely to `0.0` while preserving all valid spacing settings.
- **L2 Hostility × KubeJS Cache Race Fix (`KubeJsAttackCacheMixin`)**  
  Synchronizes `LHKJSPlugin.clearCaches()` under `AttackEventHandler`'s registration lock to eliminate concurrent modification risks during script reloads and combat listener registration.
- **Lodestone × AttributesLib Tooltip Race Fix (`LodestoneTooltipMixin`)**  
  Marks the audited `AttributesLibClient` base-modifier lambda `synchronized`, preventing tooltip callback race conditions when multiple threads (such as FastJEI workers) build tooltips concurrently.
- **Apotheosis Socketing JEI Extension (`ApotheosisJeiMixin` & `SocketingJeiLayoutMixin`)**  
  Registers `SocketingRecipe` in JEI's vanilla smithing category (guarded by `Apotheosis.enableAdventure`), links focus slots cleanly, and caches immutable gem/rarity example lists across slot renders with zero wasted work when `ClientLevel` is unloaded.
- **CoO × Structurify Overlap Gate (`structurify_overlap_gate.js`)**  
  Optional ASM CoreMod that disables only Collections of Optimizations' `MixinStructureOverlapCheck` when paired with audited Structurify builds (`2.0.42` / `2.0.43`), avoiding descriptor validation crashes without disabling either mod globally.

---

## Fixes vs. Tweaks — Which One Do You Need?

HollowRealm's custom maintenance is split cleanly into two complementary mods with zero overlapping classes or resource targets:

| Mod | Layer | What It Solves | Side / Multiplayer |
| :--- | :--- | :--- | :--- |
| **HollowRealm Fixes** (`hollowrealm_performance_compat`) | **Code & Mixin** | Crashes, concurrency races, JEI socketing extension, CoO/Structurify coremod gate | `BOTH` (`IGNORE_SERVER_VERSION`) |
| **[HollowRealm Tweaks](https://www.curseforge.com/minecraft/mc-mods/hollowrealm-tweaks)** (`hollowrealm_tweaks`) | **In-Memory Server Data** | Broken clam GLM condition IDs, shadowed annihilation bottle recipe, optional Scguns affix conditions | `BOTH` (`IGNORE_ALL_VERSION`) |

Both mods are designed and verified to work together cleanly when co-installed.

---

## Requirements & Compatibility

- **Minecraft / Loader / Java**: Minecraft **1.20.1**, Forge **`[47.4.16, 48)`** (runtime-verified on **Forge 47.4.29**), **Java 17** (bytecode target 61).
- **Optional Target Mods**: All patched mods (`Apotheosis 7.4.8`, `JEI 15.56.0.205`, `Lodestone 1.20.1-1.6.4.1`, `ApothicAttributes 1.3.7`, `L2 Hostility 2.5.19`, `L2 DamageTracker 0.4.4`, `Collections of Optimizations 4.6/5.5/6.5`, `Structurify 2.0.42/2.0.43`) are strictly optional and gated independently.
- **Single-JAR Rule**: Keep only **one** JAR with mod ID `hollowrealm_performance_compat` in `mods/`. Always remove legacy `hr-fixes-*.jar` files when upgrading to `HollowRealm-Fixes-2.0.1.jar`.
- **Legacy 1.x Cleanup**: Patches for removed/unaudited mods (`Goety Awaken`, `Goety Ominous`, `Eternal Starlight`, `Quest Enhance 3.13/3.14`) were permanently removed starting in `2.0.0`.

---

## Related Projects

- **[HollowRealm Tweaks](https://www.curseforge.com/minecraft/mc-mods/hollowrealm-tweaks)** ([GitHub](https://github.com/WinKing114/HollowRealm-Tweaks)) — Companion high-priority `SERVER_DATA` compatibility mod for HollowRealm (`0.2.1`), repairing clam loot conditions, the annihilation bottle recipe, and optional Scguns gun affixes.
- **[CrosshairBobbing Fixed](https://www.curseforge.com/minecraft/mc-mods/crosshairbobbing-fixed)** ([GitHub](https://github.com/WinKing114/CrosshairBobbing-Fixed)) — Keeps your crosshair bobbing smoothly with view movement and playing action animations (walking, attacking, breaking blocks, charging bows/tridents/crossbows, eating, and drinking) while fixing Quilt/loader startup crashes and isolating Forge 1.20.x HUD matrix transforms so health bars and hotbars stay put.  
  *Attribution: Unofficial bug-fix fork of **Crosshair Bobbing** by **Krash220** — official original project on [CurseForge (`xbob`)](https://www.curseforge.com/minecraft/mc-mods/xbob) · [Modrinth (`xbob`)](https://modrinth.com/mod/xbob) · [GitHub (`Krash220/CrosshairBobbingMod`)](https://github.com/Krash220/CrosshairBobbingMod).*

---

## Feedback & Issue Reporting

- Browse **[Changelog](CHANGELOG.md)** for full version history (`1.0.0-rc.1` – `2.0.1`) and **[CurseForge](https://www.curseforge.com/minecraft/mc-mods/hollowrealm-fixes/files/all)** for released builds.
- Report bugs or mod-version update requests on **[GitHub Issues](https://github.com/WinKing114/HR-Fixes/issues)**. Please include your `latest.log` / `debug.log`, Forge version, and exact versions of the mods involved.

### Building from Source (`2.0.1`)

`src/main/java` and `src/test/java` contain the full Java 17 source and JUnit 5 suite for `2.0.1` (`quality/2026-10-10-performance/` preserves historical `1.1.0` files). With JDK 17 (`JAVA_HOME`) and local Minecraft 1.20.1 / Forge 47.4.29 + target mod JARs present:

```powershell
$env:JAVA_HOME = 'C:/Program Files/Zulu/zulu-17'
python -X utf8 build.py
```

---

## License & Credits

Licensed under the **[MIT License](LICENSE)**. Independent third-party compatibility project—not affiliated with or endorsed by the authors of the patched mods.