# CurseForge handoff — HR Fixes 1.1.1-rc.1

Upload to existing project **HR Fixes / 1736729**. The owner chose to upload manually; this task does not submit a CurseForge file. Confirm the display version is unused in the author dashboard before uploading.

- File: `release/hr-fixes-1.1.1-rc.1.jar`
- Display name: **HR Fixes 1.1.1-rc.1**
- Minecraft: **1.20.1**; loader: **Forge**; tested Forge **47.4.16**, runtime Java **21**.
- Release type: **Beta**.
- No third-party mod is a required dependency of this version. List patch targets as **optional**, including Eternal Starlight. Do not retain a required Eternal Starlight relation for this new file.
- HR Fixes license: **MIT**. No third-party implementation JAR is included.

## Summary

Targeted compatibility and bug fixes for the Hollow Realm modpack.

## Description

HR Fixes is an independent third-party compatibility mod maintained for Hollow Realm, not an official Eternal Starlight release. It retains the attachment, targeted Structurify/CoO, Goety random-source, Quest Enhance lifecycle and Apotheosis/JEI socketing fixes.

Install one JAR while the game is closed, replacing the previous HR Fixes file. The mod ID remains `hollowrealm_performance_compat`. Only Minecraft and Forge are loading dependencies. A missing or unaudited optional target skips its patch instead of blocking startup; skipping a patch does not certify that target's own compatibility.

Audited targets: Eternal Starlight 0.9.0 hotfix; CoO 4.6/6.5 with Structurify 2.0.42/2.0.43; Goety Awaken 1.3.9.4; Goety Ominous 3.0.0.fix-1.20.1; FTB Quests 2001.4.22 with Quest Enhance 3.13; Apotheosis 7.4.8 with JEI 15.56.0.205. These boundaries apply to patch activation, not installation requirements.

Native client and minimal dedicated-server loading were tested with both Structurify builds, missing Structurify, missing CoO and all patch targets absent. Full Hollow Realm dedicated-server gameplay, multiplayer and long-term stability remain NOT_TESTED. No FPS, TPS or memory improvement is claimed.

The existing intelligent-gem correction is separate modpack data, unchanged by this release and not bundled in the JAR. No quest content, rewards, equipment balance, saved-world format or structure-generation rule is changed by HR Fixes.

## File changelog — copy for upload

Removed third-party loading dependencies and exact installation-version constraints. Missing or unaudited optional targets now skip their corresponding patches safely.

Added guarded Mixin activation using audited versions and original target method signatures, without loading or transforming target classes early. Retained the existing Eternal Starlight, Goety, quest-panel and JEI socketing fixes.

Kept the targeted CoO/Structurify overlap workaround and verified Structurify 2.0.43 alongside 2.0.42. Unrelated CoO gates and original structure-generation behavior are preserved.

Tested on Minecraft 1.20.1 / Forge 47.4.16 / Java 21. Native minimal dedicated-server loading was tested; full modpack dedicated-server gameplay is NOT_TESTED. No measured performance improvement is claimed.
