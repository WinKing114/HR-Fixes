# CurseForge file handoff — HR Fixes 1.1.0

Upload to the existing **HR Fixes** project, **1736729**. The owner will upload this file; no automated CurseForge submission is made in this task. Confirm in the author dashboard that this display version is unused before submitting.

- File: `release/hr-fixes-1.1.0.jar`
- Display name: **HR Fixes 1.1.0**
- Minecraft: **1.20.1**
- Mod loader: **Forge**; audited Forge build **47.4.16**
- Release type: **Beta**
- Tested environment: Minecraft client with its integrated server. **Dedicated server: NOT_TESTED**. Do not advertise dedicated-server certification.
- License: MIT for HR Fixes; the separately maintained Arcane Convergence data override retains attribution in `overrides/NOTICE.md`.

## Dependency relations

Eternal Starlight is required on the client (audited 0.9.0 hotfix). Mark other targets as optional: Collections Of Optimizations (4.6/6.5), Structurify (2.0.42), Goety Awaken (1.3.9.4), Goety Ominous (3.0.0.fix-1.20.1), FTB Quests (2001.4.22), Quest Enhance (3.13), Apotheosis (7.4.8), JEI (15.56.0.205). Forge metadata enforces the exact audited ranges when those targets are present; it does not install them.

The separate intelligent-gem data repair uses the existing Hollow Realm KubeJS/Arcane Convergence/Ars Nouveau setup. It is not part of the JAR and is not a new JAR dependency.

## Summary

Targeted compatibility and bug fixes for the Hollow Realm modpack.

## Description

HR Fixes is an independent third-party compatibility mod maintained for Hollow Realm. It fixes Eternal Starlight client attachment handling, the audited Collections Of Optimizations/Structurify overlap injection, Goety worldgen random-source access, Quest Enhance's empty quest-panel lifecycle and Apotheosis socketing display in JEI.

Install one HR Fixes JAR while Minecraft is closed, replacing the previous version. Do not install two files with mod ID `hollowrealm_performance_compat`. The code retains normal quest descriptions, progress/rewards and equipment recipes. Current target builds are deliberately bounded; other mod versions require a new audit.

This Beta was validated on the Hollow Realm client and its integrated server. Dedicated servers, long-term stability and other modpacks have not been certified. No measured FPS, TPS or memory improvement is claimed. It is not an official Eternal Starlight release.

## File changelog — copy for upload

Fixed Quest Enhance's null quest access when FTB Quests clears or refreshes a closed detail panel, while preserving normal description updates, editing, progress and rewards.

Fixed Apotheosis socketing display in JEI with valid linked equipment, gem and output examples across supported gem rarities. Actual smithing recipes, material consumption and equipment statistics are unchanged.

Retained the Eternal Starlight and Goety fixes, and verified the targeted Structurify workaround with Collections Of Optimizations 6.5 as well as 4.6. Aligned the filename and embedded version.

Tested on Minecraft 1.20.1 / Forge 47.4.16 in Hollow Realm's client and integrated server, including saving and restarting. Dedicated server: NOT_TESTED. No performance improvement is claimed.

Hollow Realm also has a separate intelligent-gem data correction which retains existing numeric values; this companion override is not bundled in the mod JAR. Existing reserved ancient-affix and Forge biome diagnostics are not claimed fixed by this release.
