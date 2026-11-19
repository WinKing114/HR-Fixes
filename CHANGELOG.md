# Changelog

All notable changes to **HollowRealm Fixes** (formerly **HR Fixes**, mod ID `hollowrealm_performance_compat`) are documented here using [Keep a Changelog](https://keepachangelog.com/en/1.1.0/) conventions. Historical entries (`1.0.0-rc.1` – `2.0.0`) were verified against published JAR binaries, Git commits/tags, and CurseForge file records.

---

## [2.0.1] — 2026-11-19

### Added
- Reconstructed full, cleanly compilable Java 17 source (`src/main/java`), JUnit 5 test suite (`src/test/java`), and reproducible build/verification script (`build.py`) targeting Forge `47.4.29` / Java 17 bytecode.
- Added `Apotheosis.enableAdventure` guard in `ApotheosisJeiMixin` so the JEI socketing extension skips cleanly when Apotheosis's adventure module is disabled.
- Added `ChatLineHeightGuard` helper and `HRFixesMixinPluginTest` / `SocketingJeiCacheBenchmarkTest` / `ChatLineHeightGuardTest` unit & benchmark coverage against real installed mod bytecode.

### Changed
- **Performance (`SocketingJeiExtension`)**: Short-circuit `ClientLevel == null` before copying registries or creating `ItemStack`s; replaced 5 unsynchronized mutable fields with an immutable `volatile SnapshotCache` that pre-splits `bases`, `gems`, and `outputs` and verifies registry snapshots without per-call `List.copyOf` or stream copying (measured **25.23x** faster cache-hit slot pass in final-build JUnit micro-benchmark: `108.745 ms` → `4.311 ms` over 20,000 iterations / 240 entries).
- **Code cleanup (`HRFixesMixinPlugin`)**: Removed dead 1.x constants, switch cases, and unused helper methods for removed/unaudited mods (`goetyawaken`, `goetyominous`, `eternal_starlight`, `quest_enhance`); de-duplicated `APPLY`/`SKIP` decision logging via lock-free `putIfAbsent`.
- **Metadata (`hr-fixes-manifest.json`, `mods.toml`, `PerformanceCompat`)**: Fixed erroneous gate descriptions for `l2_kubejs_listener_cache` (now accurately `L2HOSTILITY + L2DAMAGETRACKER + SHA-256 + shape`) and `socketing_jei_layout` (now accurately `APOTHEOSIS + JEI + signature + locals`), updated manifest `source` to Java 17 source compilation, and unified README / CurseForge docs.

### Fixed
- Closed `ChatLineHeightGuardMixin` divide-by-zero edge cases for line spacings in `(-1.0, -8/9)` (e.g., `-0.95` where `(int)(9 * (spacing + 1)) == 0`), `Double.NaN`, and `±Infinity` by validating `(int)(9.0 * (spacing + 1.0)) < 1` and `!Double.isFinite(spacing)`.

[Verification notes](docs/VERIFICATION-2.0.1.md) · [Release notes](docs/RELEASE-2.0.1.md)

---

## [2.0.0] — 2026-11-15 (Beta)

### Changed
- Renamed display name from **HR Fixes** to **HollowRealm Fixes** while retaining internal mod ID `hollowrealm_performance_compat` and logger prefix `HR Fixes`.
- Added machine-readable `hr-fixes-manifest.json` ledger and packaging consistency checks (no Java bytecode changes relative to the pre-rename 2.0.0 build).
- Updated GitHub and CurseForge project descriptions and backfilled historical file changelogs.

### Removed
- Removed 4 inactive or unaudited Mixins (`SorcererWorldgenRandomMixin`, `ZealotWorldgenRandomMixin`, `AttachmentClientMixin`, `QuestPanelLifecycleMixin`) and the orphaned `WorldgenRandom` helper class.
- Retained the 5 live Mixins (`KubeJsAttackCacheMixin`, `ApotheosisJeiMixin`, `SocketingJeiLayoutMixin`, `LodestoneTooltipMixin`, `ChatLineHeightGuardMixin`), per-patch compatibility gates, and the CoO × Structurify coremod gate.

[CurseForge file `9629391`](https://www.curseforge.com/minecraft/mc-mods/hollowrealm-fixes/files/9629391) · [GitHub tag `v2.0.0`](https://github.com/WinKing114/HR-Fixes/releases/tag/v2.0.0) · [Release notes](docs/RELEASE-2.0.0.md)

---

## [1.2.0] — 2026-11-03 (Release)

### Added
- Added `KubeJsAttackCacheMixin` and `L2RegistrationLock` to synchronize L2 Hostility's `LHKJSPlugin.clearCaches()` under `AttackEventHandler`'s registration lock.
- Added `ChatLineHeightGuardMixin` to guard vanilla `ChatComponent` line-height divide-by-zero crashes for `spacing <= -1.0`.
- Extended `HRFixesMixinPlugin` version, SHA-256 fingerprint, and ASM bytecode-shape gates for the new targets while retaining all earlier patches.

[CurseForge file `9423453`](https://www.curseforge.com/minecraft/mc-mods/hollowrealm-fixes/files/9423453)

---

## [1.1.4] — 2026-10-31 (Beta)

### Changed
- Refined `HRFixesMixinPlugin` with structured `Gate` / `Decision` records and diagnostic `APPLY` / `SKIP` logging.
- Updated `LodestoneTooltipMixin` synchronization handling (`postApply`) and `SocketingJeiExtension` example caching.
- Retained all 7 registered Mixins and the CoO / Structurify overlap workaround.

[CurseForge file `9387542`](https://www.curseforge.com/minecraft/mc-mods/hollowrealm-fixes/files/9387542)

---

## [1.1.3-rc.1] — 2026-10-24 (Beta)

### Added
- Added `LodestoneTooltipMixin` to synchronize the audited Lodestone / AttributesLib `AttributesLibClient.lambda$applyTextFor$6` base-modifier callback while preserving tooltip behavior.
- Retained the 6 earlier Mixins and the CoO / Structurify workaround.

*Historical note: full tooltip/FastJEI interaction testing and a separate Goety Ominous dedicated-server issue remained outside the validated scope of this build.*

[CurseForge file `9307690`](https://www.curseforge.com/minecraft/mc-mods/hollowrealm-fixes/files/9307690)

---

## [1.1.2-rc.1] — 2026-10-18 (Beta)

### Changed
- Updated `HRFixesMixinPlugin` compatibility-gate verification logic (`codex/hr-fixes-1.1.2-001` review candidate); no new patch Mixin added relative to `1.1.1-rc.1`.
- Retained the 6 Mixins for Eternal Starlight, Goety Awaken / Goety Ominous, FTB Quests / Quest Enhance, and Apotheosis / JEI socketing.

[CurseForge file `9237950`](https://www.curseforge.com/minecraft/mc-mods/hollowrealm-fixes/files/9237950)

---

## [1.1.1-rc.1] — 2026-10-13 (Beta)

### Added
- Introduced `HRFixesMixinPlugin` compatibility-gate plugin for optional, version-specific targets without hard mod-loading locks.

### Changed
- Updated Eternal Starlight attachment handling (`AttachmentClientMixin`) and guarded the CoO / Structurify coremod (`structurify_overlap_gate.js`).
- Widened Forge dependency range in `mods.toml` from exact `47.4.16` to `[47.4.16,48)`.
- Retained the 6 registered Mixins. (*Known limitation noted at release: CoO 4.8 × Structurify 2.0.43 not addressed.*)

[CurseForge file `9159195`](https://www.curseforge.com/minecraft/mc-mods/hollowrealm-fixes/files/9159195)

---

## [1.1.0] — 2026-10-12 (Beta)

### Added
- Added `QuestPanelLifecycleMixin` for FTB Quests / Quest Enhance empty-panel lifecycle handling.
- Added `ApotheosisJeiMixin`, `SocketingJeiLayoutMixin`, and `SocketingJeiExtension` for Apotheosis socketing recipes and linked example slots in JEI.

### Changed
- Retained Eternal Starlight attachment handling and Goety worldgen random-source patches; extended the CoO / Structurify overlap workaround to audited CoO `4.6` / `6.5` builds.

[CurseForge file `9148335`](https://www.curseforge.com/minecraft/mc-mods/hollowrealm-fixes/files/9148335)

---

## [1.0.0-rc.1] — 2026-10-11 (Beta)

### Added
- Initial published candidate containing Eternal Starlight attachment handling (`AttachmentClientMixin`), Goety Awaken / Goety Ominous worldgen random-source patches (`SorcererWorldgenRandomMixin`, `ZealotWorldgenRandomMixin`, `WorldgenRandom`), and the CoO / Structurify overlap workaround (`structurify_overlap_gate.js`).

*Historical artifact note: the uploaded filename is `hr-fixes-1.0.0-rc.1.jar`, while its embedded `META-INF/mods.toml` version string reports `1.1.0-rc.1`. The original binary is preserved unmodified.*

[CurseForge file `9127662`](https://www.curseforge.com/minecraft/mc-mods/hollowrealm-fixes/files/9127662)

---

## Installation & Verification Scope

Minecraft `1.20.1` · Forge `[47.4.16, 48)` · Java `17`. Keep only one JAR with mod ID `hollowrealm_performance_compat`. Loading or unit verification on `2.0.1` does not retroactively certify historical builds or untested dedicated-server / full-pack client GUI runs; see [2.0.1 verification](docs/VERIFICATION-2.0.1.md) and [artifact identities](docs/HISTORICAL-ARTIFACTS.json).
