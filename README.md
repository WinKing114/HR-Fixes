# HR Fixes

A Minecraft Forge mod with targeted compatibility and bug fixes for the Hollow Realm modpack.

## Project status

The source currently available in this repository is **HR Fixes 1.1.0**, targeting **Minecraft 1.20.1 and Forge 47.4.16**. Its mod ID is `hollowrealm_performance_compat`. There is currently no published GitHub release.

An earlier version of this README described 1.2.0, but its referenced source and reports are not present on the current default branch. The earlier text remains available in [Git history](https://github.com/WinKing114/HR-Fixes/blob/d0b21db540a5be088cd4ef6069a0d9e373802b81/README.md). Do not assume its version, patch list or test results describe the 1.1.0 source.

## Fixes

The 1.1.0 source contains targeted patches for:

- Eternal Starlight attachment synchronization.
- Structurify and Collections of Optimizations compatibility.
- Goety world-generation random access.
- The FTB Quests and Quest Enhance empty quest panel.
- Apotheosis socketing displays in JEI.

These patches are intended for specific dependency versions. See the [1.1.0 project metadata](quality/2026-10-10-performance/project.json) and [validation report](quality/2026-10-10-performance/RELEASE-1.1.0.md) before use. A separate KubeJS data correction is included under `overrides/`; it is not part of the mod JAR.

## Installation

Use only a build appropriate for the target modpack and dependency versions. Close Minecraft before replacing the JAR, and keep only one JAR with the `hollowrealm_performance_compat` mod ID in `mods/`. Check the [validation report](quality/2026-10-10-performance/RELEASE-1.1.0.md) for version-specific installation and rollback limits.

## Building

See the [build instructions](quality/2026-10-10-performance/BUILD.md) for the required JDK, libraries and audited compile dependencies.

## Verification

The 1.1.0 report records targeted checks in an isolated single-player client and integrated server, including quest, JEI and attachment interactions. It also records an independent source rebuild. Dedicated servers, multiplayer, long-term play and the author's existing world were not tested. These historical results do not establish compatibility for other builds or mod combinations.

See the [validation report](quality/2026-10-10-performance/RELEASE-1.1.0.md) and [evidence](quality/2026-10-10-performance/evidence/compat-20261012.json) for the tested environment and limitations. No FPS, TPS or memory improvement is claimed.

## Documentation

- [Changelog](quality/2026-10-10-performance/CHANGELOG.md)
- [Compatibility notes](quality/2026-10-10-performance/COMPATIBILITY.md)
- [Build instructions](quality/2026-10-10-performance/BUILD.md)
- [1.1.0 validation report](quality/2026-10-10-performance/RELEASE-1.1.0.md)
- [Historical README snapshot](docs/archive/README-2026-10-24-1.1.3-rc.1.md)

## License

[MIT](LICENSE). HR Fixes is an independent third-party project and is not affiliated with the authors of its target mods.
