# HR Fixes

Targeted compatibility and bug fixes for the Hollow Realm modpack.

**Current Beta candidate: 1.1.1-rc.1 — Minecraft 1.20.1 / Forge 47.4.16 / Java 21 runtime.**

Independent third-party project, not an official Eternal Starlight release. Stable mod ID: `hollowrealm_performance_compat`.

Only Minecraft and Forge are loading dependencies. Missing or unaudited third-party targets skip their patches instead of blocking startup. Audited optional patches cover Eternal Starlight attachments, CoO/Structurify overlap injection, Goety worldgen random-source access, Quest Enhance empty-panel lifecycle and Apotheosis/JEI socketing display. Structurify 2.0.43 and 2.0.42 were tested.

Replace the previous HR Fixes JAR while Minecraft is closed; install only one copy of the mod ID. Patch activation boundaries do not require installing those target mods. The separate intelligent-gem data correction under `overrides/kubejs/data/` is unchanged and is not bundled in the JAR.

[Build](quality/2026-10-10-performance/BUILD.md) · [Changelog](quality/2026-10-10-performance/CHANGELOG.md) · [Validation and limitations](quality/2026-10-10-performance/RELEASE-1.1.1-rc.1.md) · [CurseForge handoff](quality/2026-10-10-performance/CURSEFORGE.md)

Native client and minimal dedicated-server loading passed five optional-target combinations each. Full modpack dedicated-server gameplay, multiplayer and long-term stability remain NOT_TESTED. No performance improvement is claimed. Quest content, rewards, equipment balance, save formats and structure rules are unchanged by HR Fixes.

The existing source layout and Python/javac builder remain. The source package was independently extracted and rebuilt byte-for-byte against the candidate. No dependency JAR, QA mod, personal save, launcher account or raw game log is included.

MIT for HR Fixes; companion data attribution is retained in `overrides/NOTICE.md`.
