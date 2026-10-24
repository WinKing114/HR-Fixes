# HR Fixes

**Targeted compatibility and bug fixes for Hollow Realm**

HR Fixes is a lightweight Minecraft Forge mod developed specifically for the **Hollow Realm** modpack.

It addresses reproducible crashes, mod compatibility conflicts, and gameplay bugs that are best resolved through Java or Mixin patches.

The project focuses on small, evidence-based fixes rather than broad, unverified optimizations.

## Project Information

- **Minecraft:** 1.20.1
- **Mod Loader:** Forge
- **Mod ID:** `hollowrealm_performance_compat`
- **Java Bytecode Target:** Java 17
- **Development Candidate:** `1.1.3-rc.1`
- **Release Status:** Ready to publish with documented limits; not yet publicly released

## Compatibility Fixes

HR Fixes currently maintains targeted fixes for:

- **Lodestone × AttributesLib:** Addresses concurrent cache access causing tooltip null-pointer errors and incorrect base-attribute detection.
- **Goety Awaken / Goety Ominous:** Improves random-source handling and validates Redirect injection sites before applying compatibility patches.
- **FTB Quests × Quest Enhance:** Prevents a specific quest-panel lifecycle null-pointer error.
- **Apotheosis × JEI:** Fixes socketing recipe display compatibility.
- **Eternal Starlight:** Maintains a targeted attachment-handling compatibility fix.
- **Collections of Optimizations × Structurify:** Provides guarded compatibility handling for audited mod versions.

Patches are scoped to their supported targets. Missing or unsupported optional mods should not force unrelated patches to load.

HR Fixes does not redesign quests, replace gameplay configurations, or automatically resolve every third-party incompatibility.

## Latest Development — 1.1.3-rc.1

The latest local candidate addresses a concurrency issue involving Lodestone and AttributesLib.

The fix synchronizes a short affected callback while preserving the existing tooltip behavior.

**Recorded validation results:**

| Verification | Result |
| --- | --- |
| Concurrent calls | 192,000 |
| Previous candidate: null-pointer errors | 329 |
| New candidate: null-pointer errors | 0 |
| Previous candidate: attribute misclassifications | 596 |
| New candidate: attribute misclassifications | 0 |
| New checks | 31/31 passed |
| Existing compatibility-gate checks | 70/70 passed |
| Independent source rebuild | Byte-identical JAR |

These results are based on the local candidate's recorded validation. They do not represent complete real-game or long-term acceptance.

## Compatibility and Known Limitations

HR Fixes is designed for Minecraft 1.20.1 on Forge.

Previous candidates completed focused client checks on Forge 47.4.16 and 47.4.22. These results do not automatically qualify every new candidate on both versions.

For `1.1.3-rc.1`, the following remain unverified:

- Complete FastJEI interaction and in-game tooltip behavior.
- New-candidate runtime testing on Forge 47.4.16.
- Dedicated-server compatibility and long-term gameplay stability.
- Complete Hollow Realm modpack compatibility.

A previously identified Goety Ominous dedicated-server failure involving client-only class loading is a separate third-party defect and is not claimed to be fixed by HR Fixes.

**Full Hollow Realm dedicated-server support is not currently qualified.**

## Development Principles

HR Fixes follows a focused maintenance approach:

1. Investigate real and reproducible problems.
2. Confirm the root cause before introducing patches.
3. Prefer the smallest safe fix.
4. Preserve existing functionality and optional-mod compatibility.
5. Test affected behavior and relevant regressions.
6. Document unsupported configurations and remaining limitations.

Configuration, recipe, data-pack, and KubeJS fixes remain in their appropriate maintenance layers unless there is a clear technical reason to move them into HR Fixes.

## Installation and Releases

Only use builds published or explicitly provided by the project maintainer.

Before replacing a build, close the Minecraft client or server and ensure that only one HR Fixes JAR is installed.

Development candidates, source archives, and rollback copies are not automatically public releases.

**Repository:** https://github.com/WinKing114/HR-Fixes

---

HR Fixes is an independent compatibility project maintained for the Hollow Realm modpack. Compatibility is verified for documented environments only; broader support is not implied.
