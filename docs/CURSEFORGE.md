# HollowRealm Fixes

**Targeted compatibility and crash fixes for the HollowRealm modpack.** Formerly **HR Fixes**. Minecraft **1.20.1**, **Forge**, Java **17**.

## 2.0.0 update preview

**2.0.0 is prepared but its JAR has not yet been uploaded. The current CurseForge download is 1.2.0.** The following describes 2.0.0; older files keep their own patch lists.

### Retained fixes

- **L2 Hostility / KubeJS:** address the listener-cache race using the attack-handler registration lock.
- **Apotheosis / JEI:** register socketing recipe support and link equipment, gem and output examples in the smithing layout (two mixins).
- **Lodestone / AttributesLib:** serialize the audited base-modifier tooltip callback.
- **Vanilla chat:** guard non-positive chat line height against division by zero.
- **CoO / Structurify:** disable only the conflicting structure-overlap mixin for CoO 5.5 with Structurify 2.0.43.

Five mixins remain, plus the version-gate plugin and CoO/Structurify coremod workaround. Checks are patch-specific: relevant mod versions, and target-class SHA-256 fingerprints for the L2 cache and tooltip patches. Unsupported combinations are skipped rather than force-applied.

### Removed in 2.0.0

Goety Awaken and Goety Ominous worldgen patches, the Eternal Starlight attachment patch, the old Quest Enhance 3.13/3.14 panel patch and the unused WorldgenRandom helper. Their targets were absent or outside the audited version in the current pack. They are not supplied by 2.0.0.

## Installation and compatibility

Close Minecraft before replacing the JAR. Keep only one file with mod ID `hollowrealm_performance_compat`; HollowRealm Fixes replaces HR Fixes and must not be installed alongside it.

Metadata allows Minecraft 1.20.1 / Forge [47.4.16,48). **2.0.0 loading was checked on Forge 47.4.29 / Java 17**; the full allowed range has not been tested. Fabric, NeoForge and other Minecraft versions are not validated.

The internal mod ID and `[HR Fixes/]` log names remain unchanged. APPLY reports a gate decision; MERGED reports mixin application. Five of each were observed in the tested client; counts vary with installed targets and side.

## Scope and verification

Compatibility fixes only. Recipes, loot, quests, shops, economy, difficulty and localisation belong to a separate planned tweak mod. The prepared 2.0.0 JAR does not include those data/configuration changes.

The renamed JAR passed file/metadata checks and an isolated Forge 47.4.29 client load; its 12 classes are identical to pre-rename 2.0.0. **Per-feature behaviour, complete gameplay flows, player experience, dedicated servers and multiplayer remain untested for this build.** No measured FPS, TPS or memory improvement is claimed.

The GitHub repository currently contains historical 1.1.0 Java source, not matching 2.0.0 source. See the version-specific notes before using historical build instructions or reports.

[GitHub](https://github.com/WinKing114/HR-Fixes) · [2.0.0 notes](https://github.com/WinKing114/HR-Fixes/blob/main/docs/RELEASE-2.0.0.md) · [Verification](https://github.com/WinKing114/HR-Fixes/blob/main/docs/VERIFICATION-2.0.0.md) · [Report an issue](https://github.com/WinKing114/HR-Fixes/issues)

Independent third-party project; not affiliated with the authors of the patched mods.
