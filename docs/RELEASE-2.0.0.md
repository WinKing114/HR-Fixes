# HollowRealm Fixes 2.0.0

**Prepared update — binary publication pending.** Formerly HR Fixes. These notes describe the local 2.0.0 artifact; they do not change the contents of the existing CurseForge 1.2.0 download.

## Changed

- Unify the display name as **HollowRealm Fixes**. Retain mod ID `hollowrealm_performance_compat`, Java packages and `[HR Fixes/]` log names.
- Remove four inactive or unaudited mixins: Goety Awaken, Goety Ominous, Eternal Starlight and the old Quest Enhance 3.13/3.14 panel patch. Remove their unused `WorldgenRandom` helper.
- Retain the five mixins for L2 Hostility/KubeJS cache handling, Apotheosis/JEI socketing (two), Lodestone/AttributesLib tooltips and vanilla chat line height.
- Retain patch-specific version/fingerprint checks and the CoO 5.5 / Structurify 2.0.43 overlap workaround.
- Add a machine-readable patch ledger and packaging checks. No new Java logic is introduced by the 2.0.0 cleanup or rename.

## Upgrade and scope

Minecraft 1.20.1 / Forge; metadata allows Forge [47.4.16,48), with loading checked on 47.4.29 / Java 17. Replace the previous HR Fixes JAR while the game is closed. Never keep two files with the same mod ID.

This update includes no recipe, loot, quest, shop, economy or difficulty edits. Those belong to the planned tweak mod. No quantified performance gain is claimed.

## Verification

The renamed artifact passed metadata/JSON checks, 12-class byte-identity comparison and an isolated Forge 47.4.29 client load. Five APPLY and five MERGED decisions were observed. The pre-rename 2.0.0 also has a recorded HollowRealm instance load; that is a separate artifact.

Feature-by-feature gameplay, complete flows, player experience, dedicated servers and multiplayer remain untested. Historical 1.1.0 source and reports do not validate this binary. See [verification details](VERIFICATION-2.0.0.md).

Artifact: `HollowRealm-Fixes-2.0.0.jar` (29,856 bytes)  
SHA-256: `9dba779045e217ae8d0a800b4abbe0389b542ccd6e058c68d534f6744314bc0a`
