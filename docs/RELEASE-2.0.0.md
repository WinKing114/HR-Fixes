# HollowRealm Fixes 2.0.0

## Changes

- Rename the mod display name from HR Fixes to HollowRealm Fixes; retain the internal mod ID and log names.
- Remove inactive Goety Awaken, Goety Ominous and Eternal Starlight patches, the unaudited Quest Enhance 3.13/3.14 panel patch, and the unused WorldgenRandom helper.
- Retain five mixins for the L2 Hostility/KubeJS listener cache, Apotheosis/JEI socketing (two), Lodestone/AttributesLib tooltips and vanilla chat line height.
- Retain patch-specific compatibility gates and the CoO 5.5 / Structurify 2.0.43 overlap workaround.
- Add the machine-readable patch ledger and packaging checks. No new Java logic is introduced by the cleanup or rename.

## Compatibility

Minecraft 1.20.1 / Forge / Java 17. Loading checked on Forge 47.4.29; metadata permits [47.4.16,48). Replace the old HR Fixes JAR; never install both because they share mod ID `hollowrealm_performance_compat`.

This mod is designed for HollowRealm. Recipes, loot, quests, shops and balance edits are outside this update.

## Verification

File/metadata checks and isolated client loading passed; five APPLY and five MERGED records were observed. All 12 class files match the pre-rename 2.0.0 JAR. Per-feature gameplay, full playthroughs, dedicated servers and multiplayer remain untested for this build.

SHA-256: `9dba779045e217ae8d0a800b4abbe0389b542ccd6e058c68d534f6744314bc0a`

[CurseForge file](https://www.curseforge.com/minecraft/mc-mods/hollowrealm-fixes/files/9629391) · [Verification](VERIFICATION-2.0.0.md)
