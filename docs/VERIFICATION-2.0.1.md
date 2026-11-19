# 2.0.1 Verification & Artifact Identity

Verification log for **HollowRealm Fixes `2.0.1`** (`HollowRealm-Fixes-2.0.1.jar`).

- **Artifact**: `HollowRealm-Fixes-2.0.1.jar`
- **Size**: `31,692` bytes (`25` archive entries, `17` classes)
- **SHA-256**: `dc5b26e974ce35a9429c2d25e6279e9a9341eb8e8f49dd2c75ba25608d52095b`

## Build & Verification Layers

| Layer | Status | Scope & Notes |
| :--- | :--- | :--- |
| **Java 17 Compilation & Bytecode** | **PASS** | Compiled 10 main Java sources with Zulu JDK 17 (`--release 17`, major version 61), zero warnings, zero dead 1.x mod references in `HRFixesMixinPlugin.class`, `ApotheosisJeiMixin.class` verified to guard `enableAdventure`, and `mixins.json` ↔ `hr-fixes-manifest.json` ↔ `.class` verified 1:1. |
| **JUnit 5 Unit & Real-Bytecode Suite** | **PASS (8/8)** | `ChatLineHeightGuardTest`, `HRFixesMixinPluginTest` (verified against real installed bytecode for `ApothicAttributes 1.3.7`, `Lodestone 1.6.4.1`, `Apotheosis 7.4.8`, `JEI 15.56.0.205`, `L2Hostility 2.5.19`, `L2DamageTracker 0.4.4`, and `Minecraft 1.20.1` SRG `ChatComponent`), and `SocketingJeiCacheBenchmarkTest`. |
| **Same-Environment Benchmark** | **PASS** | `SocketingJeiCacheBenchmarkTest`: 20,000 iterations × 240 entries, legacy per-slot `List.copyOf` + stream (`108.745 ms`) vs `2.0.1` `SnapshotCache` (`4.311 ms`) = **25.23x speedup**. |
| **Isolated Dedicated Server (Co-Installed with Tweaks 0.2.1)** | **PASS** | Forge `1.20.1-47.4.29` dedicated server (`21` mods, `24.2 s`) with `HollowRealm-Fixes-2.0.1.jar` + `HollowRealm-Tweaks-0.2.1.jar` + target server mods: clean `APPLY`/`MERGED` for `KubeJsAttackCacheMixin`, zero crashes/attributable errors, probe PASS across initial + reload. |
| **Live 282-Mod Client GUI & Multiplayer** | **Pending** | Client-side Mixins (`ApotheosisJeiMixin`, `SocketingJeiLayoutMixin`, `LodestoneTooltipMixin`, `ChatLineHeightGuardMixin`) are verified by JUnit real-bytecode gate/ASM tests; live full-pack client GUI rendering (`Apotheosis` JEI socketing, `Lodestone` tooltips, chat line spacing, `CoO × Structurify` overlap) and multiplayer remain to be verified after instance deployment. |
