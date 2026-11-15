# HollowRealm Fixes

Targeted compatibility and crash fixes for the **HollowRealm** modpack, formerly **HR Fixes**.

Minecraft **1.20.1** · **Forge** · Java **17** · [CurseForge](https://www.curseforge.com/minecraft/mc-mods/hollowrealm-fixes) · [Issues](https://github.com/WinKing114/HR-Fixes/issues)

## Current version and downloads

**2.0.0 is the prepared update documented here; its renamed JAR has not yet been published to CurseForge or GitHub Releases.** CurseForge currently offers **1.2.0**. The patch list below describes **2.0.0**, not every historical download. See the [2.0.0 update notes](docs/RELEASE-2.0.0.md).

The repository URL remains `WinKing114/HR-Fixes` so existing links keep working. The project display name is **HollowRealm Fixes**; the internal mod ID remains `hollowrealm_performance_compat`.

## Fixes in 2.0.0

| Area | Targeted fix |
| --- | --- |
| L2 Hostility / KubeJS | Clear the listener cache under the attack-handler registration lock to address a cache race. |
| Apotheosis / JEI | Register the socketing category extension and link equipment, gem and output examples in the smithing layout (two mixins). |
| Lodestone / AttributesLib | Serialize the audited base-modifier tooltip callback to address a client-side race. |
| Vanilla chat | Guard non-positive chat line height against division by zero. |
| Collections of Optimizations / Structurify | Disable only the conflicting structure-overlap mixin for CoO 5.5 with Structurify 2.0.43. |

Five mixins remain, plus the version-gate plugin and the CoO/Structurify coremod workaround. Patch-specific checks use the relevant mod versions and, for the L2 cache and tooltip patches, target-class SHA-256 fingerprints. The vanilla chat patch targets Minecraft 1.20.1. Unsupported combinations are skipped rather than force-applied.

`APPLY` logs report a preparation-gate decision; `MERGED` reports mixin application. Counts depend on installed targets and side. Five of each were observed in the tested HollowRealm client; that count is not a universal installation requirement.

## What changed from HR Fixes

2.0.0 removes the Goety Awaken and Goety Ominous worldgen patches, Eternal Starlight attachment patch, Quest Enhance 3.13/3.14 panel patch and unused `WorldgenRandom` helper. Their targets were absent or outside the audited version in the current pack. These are **not fixes supplied by 2.0.0**.

The rename changes four text/resource entries. All 12 class files match the pre-rename 2.0.0 JAR byte for byte. Internal packages and `[HR Fixes/]` log names are retained.

## Installation

Use a published file appropriate for your modpack and dependency versions. Close Minecraft, back up the JAR being replaced, and keep **one** file with mod ID `hollowrealm_performance_compat` in `mods/`. A renamed HollowRealm Fixes JAR replaces the old HR Fixes JAR; do not install both.

Metadata permits Forge `[47.4.16,48)` with Minecraft `[1.20.1]`. **2.0.0 loading was checked on Forge 47.4.29 / Java 17**; the entire permitted Forge range has not been tested. Fabric, NeoForge, other Minecraft versions, dedicated servers and multiplayer are not validated for this update.

## Scope

This mod contains compatibility fixes. Modpack-specific recipes, loot, quests, shops, economy, difficulty and localisation belong to a separate planned tweak mod. The prepared 2.0.0 JAR does not bundle those data repairs or instance configuration changes.

No measured FPS, TPS or memory improvement is claimed.

## Verification and source availability

[2.0.0 verification notes](docs/VERIFICATION-2.0.0.md) distinguish file checks, loading and untested gameplay. The renamed JAR passed file/metadata checks and an isolated Forge 47.4.29 client load with five APPLY and five MERGED decisions. **Per-feature behaviour, complete gameplay flows, player experience, dedicated servers and multiplayer remain untested for this build.**

The Java source currently in [quality/2026-10-10-performance](quality/2026-10-10-performance) is historical **1.1.0**, not the source of 2.0.0. The local 2.0.0 packaging process retained class files from the 1.2.0 binary; matching 1.2.0/2.0.0 Java source is not present in this repository. The historical build instructions do not reproduce 2.0.0. Do not treat the old test reports as current-version certification.

## Documentation

- [Changelog](CHANGELOG.md)
- [2.0.0 update notes](docs/RELEASE-2.0.0.md)
- [2.0.0 verification and artifact identity](docs/VERIFICATION-2.0.0.md)
- [CurseForge description](docs/CURSEFORGE.md)
- [Historical 1.1.0 validation report](quality/2026-10-10-performance/RELEASE-1.1.0.md)

## License

[MIT](LICENSE). Independent third-party project; not affiliated with the authors of the patched mods.
