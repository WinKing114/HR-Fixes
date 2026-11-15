# Changelog

Version-specific changes for **HollowRealm Fixes**, formerly **HR Fixes**. Historical entries were backfilled on 2026-11-15 from published JAR metadata/resources and artifact differences; no old binary was modified or retested.

## 2.0.0 — 2026-11-15 (Beta)

- Rename the mod display name from HR Fixes to HollowRealm Fixes; retain the internal mod ID and log names.
- Remove inactive Goety Awaken, Goety Ominous and Eternal Starlight patches, the unaudited Quest Enhance 3.13/3.14 panel patch, and the unused WorldgenRandom helper.
- Retain five mixins for the L2 Hostility/KubeJS listener cache, Apotheosis/JEI socketing (two), Lodestone/AttributesLib tooltips and vanilla chat line height.
- Retain patch-specific compatibility gates and the CoO 5.5 / Structurify 2.0.43 overlap workaround.
- Add the machine-readable patch ledger and packaging checks. No new Java logic is introduced by the cleanup or rename.


[Download / CurseForge file](https://www.curseforge.com/minecraft/mc-mods/hollowrealm-fixes/files/9629391) · [Full notes](docs/RELEASE-2.0.0.md)

## 1.2.0

- Add the L2 Hostility/KubeJS listener-cache race fix.
- Add the vanilla chat line-height divide-by-zero guard.
- Extend patch gating for the new targets; retain the earlier compatibility patches.


[Original file](https://www.curseforge.com/minecraft/mc-mods/hollowrealm-fixes/files/9423453)

## 1.1.4

- Update compatibility gating with structured gate/decision handling and diagnostic logging.
- Update the Lodestone/AttributesLib tooltip callback and JEI socketing example implementation.
- Retain the same seven registered mixins and the CoO/Structurify workaround.


[Original file](https://www.curseforge.com/minecraft/mc-mods/hollowrealm-fixes/files/9387542)

## 1.1.3-rc.1

- Add the Lodestone/AttributesLib tooltip concurrency patch.
- Synchronize the affected base-modifier callback while retaining tooltip behaviour.
- Retain the six earlier mixins and CoO/Structurify workaround.


Full tooltip/FastJEI interaction testing and the separate Goety Ominous dedicated-server issue remain outside the validated scope.

[Original file](https://www.curseforge.com/minecraft/mc-mods/hollowrealm-fixes/files/9307690)

## 1.1.2-rc.1

- Update the compatibility-gate implementation.
- Retain the six mixins for Eternal Starlight, Goety Awaken/Goety Ominous, quest panels and Apotheosis/JEI socketing.
- No new patch mixin is added relative to 1.1.1-rc.1.


[Original file](https://www.curseforge.com/minecraft/mc-mods/hollowrealm-fixes/files/9237950)

## 1.1.1-rc.1

- Introduce the Mixin compatibility-gate plugin for optional, version-specific targets.
- Update Eternal Starlight attachment handling and the guarded CoO/Structurify coremod.
- Keep the six registered patch mixins; change Forge metadata from exact 47.4.16 to [47.4.16,48).


Known limitation: the CoO 4.8 / Structurify 2.0.43 conflict is not addressed.

[Original file](https://www.curseforge.com/minecraft/mc-mods/hollowrealm-fixes/files/9159195)

## 1.1.0

- Add the FTB Quests/Quest Enhance empty-panel lifecycle patch.
- Add Apotheosis socketing support and linked examples in JEI.
- Retain Eternal Starlight attachments and Goety worldgen random-source fixes; extend the CoO/Structurify workaround to the audited CoO 4.6/6.5 builds.


[Original file](https://www.curseforge.com/minecraft/mc-mods/hollowrealm-fixes/files/9148335)

## 1.0.0-rc.1

- Initial published candidate with Eternal Starlight attachment handling, Goety Awaken/Goety Ominous worldgen random-source patches and the CoO/Structurify overlap workaround.
- Historical version mismatch: the uploaded filename is hr-fixes-1.0.0-rc.1.jar, but its embedded version is 1.1.0-rc.1. The original binary is preserved.


[Original file](https://www.curseforge.com/minecraft/mc-mods/hollowrealm-fixes/files/9127662)

## Installation and validation

Minecraft 1.20.1 / Forge / Java 17. Keep one JAR with mod ID `hollowrealm_performance_compat`. Dependency gates and permitted Forge versions vary by build. Current-version loading evidence does not certify historical files; dedicated-server, multiplayer and long-term support are not claimed. See [artifact identities](docs/HISTORICAL-ARTIFACTS.json) and [2.0.0 verification](docs/VERIFICATION-2.0.0.md).
