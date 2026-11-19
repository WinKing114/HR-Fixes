# HollowRealm Fixes 2.0.1

## What's New

- **Rebuilt from clean Java 17 source**: Restored full Java 17 source (`src/main/java`), JUnit 5 test suite (`src/test/java`), and reproducible build verification (`build.py`).
- **Chat line-height guard fix (`ChatLineHeightGuardMixin`)**: Closed divide-by-zero edge cases for line spacings where `(int)(9.0 * (spacing + 1.0)) < 1` (e.g. `-0.95`) and non-finite `Double` values (`NaN`, `±Infinity`), while keeping valid spacings untouched.
- **Apotheosis adventure guard (`ApotheosisJeiMixin`)**: Respects `Apotheosis.enableAdventure` before registering the JEI socketing extension.
- **JEI socketing cache optimization (`SocketingJeiExtension`)**: Skips `ClientLevel == null` before reading registries or creating stacks, replaces 5 unsynchronized mutable fields with a thread-safe `volatile SnapshotCache`, and eliminates per-slot `List.copyOf` / stream copies (measured **25.23x** faster cache-hit slot pass in JUnit benchmark).
- **Dead-code & metadata cleanup (`HRFixesMixinPlugin`, `hr-fixes-manifest.json`)**: Removed unused 1.x constants/branches/helpers left over from 1.2.0, de-duplicated `APPLY`/`SKIP` logging, and corrected manifest gate descriptions for `l2_kubejs_listener_cache` and `socketing_jei_layout`.

## Compatibility

- Minecraft **1.20.1** · Forge **`[47.4.16, 48)`** (tested on **47.4.29**) · **Java 17** (`IGNORE_SERVER_VERSION`).
- Keep only **one** JAR with mod ID `hollowrealm_performance_compat` installed; remove any older `hr-fixes-*.jar`.
- Companion data-layer compatibility mod: **[HollowRealm Tweaks 0.2.1](https://github.com/WinKing114/HollowRealm-Tweaks)** (verified co-installed).

## Verification Summary

- **8 / 8 JUnit 5 tests pass** (including gate checks against real installed JAR bytecode from HollowRealm, `ChatLineHeightGuardTest`, and `SocketingJeiCacheBenchmarkTest`).
- **Isolated Forge 47.4.29 dedicated-server co-installation pass** alongside `HollowRealm Tweaks 0.2.1` (verifies `PerformanceCompat` load, `KubeJsAttackCacheMixin` `APPLY` + `MERGED` onto `LHKJSPlugin`, and clean server-side skipping of client-only Mixins).
- **Pending**: Live 282-mod client GUI verification (`ApotheosisJeiMixin`, `SocketingJeiLayoutMixin`, `LodestoneTooltipMixin`, `ChatLineHeightGuardMixin`) and long-term multiplayer playtesting.

See [VERIFICATION-2.0.1.md](VERIFICATION-2.0.1.md) for SHA-256 digests and exact evidence layers.
