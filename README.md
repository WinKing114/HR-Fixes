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

The latest local
