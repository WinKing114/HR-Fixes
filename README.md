# HR Fixes

Targeted compatibility and bug fixes for the Hollow Realm modpack.

**Current Beta candidate: 1.1.0 — Minecraft 1.20.1 / Forge 47.4.16.**

An independent third-party project, not an official Eternal Starlight release. The stable mod ID is `hollowrealm_performance_compat`.

- Eternal Starlight client attachment handling.
- The audited Collections Of Optimizations 4.6/6.5 and Structurify 2.0.42 overlap injection.
- Goety Awaken/Ominous worldgen random-source access.
- FTB Quests 2001.4.22 and Quest Enhance 3.13 empty-panel lifecycle.
- Apotheosis 7.4.8 and JEI 15.56.0.205 socketing display.

Replace the previous HR Fixes JAR while the game is closed; install only one copy of the mod ID. The separate intelligent-gem data correction under `overrides/kubejs/data/` belongs to the audited Hollow Realm setup and is **not bundled in the JAR**. It retains existing numeric values.

[Build instructions](quality/2026-10-10-performance/BUILD.md) · [Changelog](quality/2026-10-10-performance/CHANGELOG.md) · [Validation and limitations](quality/2026-10-10-performance/RELEASE-1.1.0.md) · [CurseForge handoff](quality/2026-10-10-performance/CURSEFORGE.md)

Validated with the current Hollow Realm client and integrated server. Dedicated servers and long-term stability are NOT_TESTED. No FPS/TPS/memory improvement is claimed. Quest identities, rewards/progress, equipment balance, save formats and structure-generation rules are preserved. See the validation report for existing warnings that were investigated and left unchanged.

## Source layout

`quality/2026-10-10-performance/` remains the maintained project. `.code/performance/build_patch.py` builds Java 17 bytecode from local audited compile dependencies. Source packages contain no dependency JARs, personal saves, launcher credentials or raw game logs.

## License

MIT for HR Fixes. Attribution for the companion data definition is retained in `overrides/NOTICE.md`.
