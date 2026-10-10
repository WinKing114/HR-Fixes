# HR Fixes

Targeted compatibility and bug fixes for the Hollow Realm modpack.

An independent, third-party project for **Minecraft 1.20.1 / Forge 47.4.16**. It is not an official Eternal Starlight release.

## Current fix

Restore Eternal Starlight Forge client attachment synchronization when its Forge packet wrapper passes a null player reference. HR Fixes supplies the current local player on the client main thread and preserves the original entity lookup and attachment decoding. Packets without a local player are discarded, with no cross-world replay queue.

Requires **EternalStarlight-0.9.0-hotfix.jar**, internal mod version **0.9.0**. Other binaries sharing that version require a new compatibility review. Servers do not need HR Fixes.

## Build

The existing source layout and Python/javac build are retained. Compile dependencies are supplied locally; no third-party JARs or assets are bundled.

```powershell
python .code/performance/build_patch.py --libraries "C:/path/to/.minecraft/libraries" --eternal-starlight "C:/path/to/EternalStarlight-0.9.0-hotfix.jar" --javac "C:/path/to/jdk-17/bin/javac.exe"
```

See [build instructions](quality/2026-10-10-performance/BUILD.md), [source](quality/2026-10-10-performance/src/hollowrealm/performance/mixin/AttachmentClientMixin.java), and [candidate metadata](quality/2026-10-10-performance/project.json).

## Validation and release status

The **1.0.1-rc.1** candidate was tested in an isolated Hollow Realm client with the original 264 JARs. Real attachment updates and deletion, death/respawn, dimension changes, full client restart and same-process world reload passed. Independent source rebuilding produced the same candidate JAR.

These checks do not establish long-term stability, complete Eternal Starlight feature coverage, or external dedicated-server reconnect behavior. No FPS, TPS or memory improvement is claimed.

See the [audit](quality/2026-10-10-performance/AUDIT.md) and [validation record](quality/2026-10-10-performance/evidence/hr-fixes-review.json). Historical deployment scripts, diagnostic scripts, test worlds and raw logs are not included in this standalone source repository.

## Installation

Install one HR Fixes JAR in the client's `mods` folder and restart Minecraft. Replace any earlier private 1.0.0 copy, including `HR Fixes-1.0.0.jar` or `hollowrealm-performance-compat-1.0.0.jar`: all use the stable mod ID `hollowrealm_performance_compat` and must not coexist.

Close Minecraft before removal. HR Fixes adds no world content and performs no save migration. Removing it with the affected dependency restores the original synchronization failure.

## Publishing

CurseForge project **1736729** has been created by the owner. Candidate files have not been publicly uploaded by this workflow; public submission remains subject to the owner's confirmation and project artwork/field review. [Publication materials](quality/2026-10-10-performance/CURSEFORGE.md).

## License

[MIT](LICENSE), Copyright (c) 2026 Hollow Realm contributors. Eternal Starlight remains a separate dependency; its code and assets are not distributed here.
