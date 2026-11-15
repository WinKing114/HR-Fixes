# 2.0.0 verification and artifact identity

Documentation checked on 2026-11-15. This is a summary of existing local evidence, not a new gameplay test or a published binary release.

| Artifact | Bytes | SHA-256 |
| --- | ---: | --- |
| HR Fixes 2.0.0, before rename | 29760 | `23114c991c80cf98d7b98316371f6cb74340b631dcd308e4f60f32d6e849dfa7` |
| HollowRealm Fixes 2.0.0, renamed | 29856 | `9dba779045e217ae8d0a800b4abbe0389b542ccd6e058c68d534f6744314bc0a` |

The renamed JAR contains 20 entries and 12 class files. All classes are identical to the pre-rename artifact. Four text/resource entries differ: `META-INF/mods.toml`, `pack.mcmeta`, `coremods/structurify_overlap_gate.js` and `hr-fixes-manifest.json`.

## Evidence layers

| Layer | Evidence and limit |
| --- | --- |
| File / metadata | Read-only verification rerun on 2026-11-15: equal entry names, 12 identical classes, parsed TOML/JSON, expected mod ID/name/version and SHA-256. |
| Renamed artifact loading | Existing isolated Minecraft 1.20.1 / Forge 47.4.29 / Java 17 client log dated 2026-11-15: all five APPLY and five MERGED decisions, coremod overlap gate active. This is loading evidence. |
| Pre-rename artifact loading | Existing 2026-11-15 HollowRealm instance loading record for the 29,760-byte artifact. Does not establish deployment of the renamed JAR. |
| Per-feature gameplay | Not validated for this build. |
| Complete flows / player experience | Not validated. |
| Dedicated server / multiplayer | Not validated. |

The local loading log records the five targets as:
```text
APPLY KubeJsAttackCacheMixin -> dev.xkmc.l2hostility.compat.kubejs.LHKJSPlugin
APPLY ApotheosisJeiMixin -> dev.shadowsoffire.apotheosis.adventure.compat.AdventureJEIPlugin
APPLY SocketingJeiLayoutMixin -> mezz.jei.library.plugins.vanilla.anvil.SmithingRecipeCategory
APPLY LodestoneTooltipMixin -> dev.shadowsoffire.attributeslib.client.AttributesLibClient
APPLY ChatLineHeightGuardMixin -> net.minecraft.client.gui.components.ChatComponent
```
Each has a corresponding MERGED record. Preparation decisions and merges alone do not prove that the original symptoms were reproduced and fixed.

## Availability and reproducibility

The renamed 2.0.0 artifact is prepared locally; no 2.0.0 binary is uploaded by this documentation update. CurseForge's existing 1.2.0 download retains its own identity and history.

The repository contains historical 1.1.0 Java source. The local 2.0.0 packaging copied class files from the 1.2.0 binary and did not recompile Java. Matching Java source for those retained classes is not available here. No claim is made that the old source or historical build script reproduces 2.0.0.

The metadata-allowed Forge range [47.4.16,48) is wider than the verified 47.4.29 environment. Optional patch targets and fingerprints are checked per patch; fingerprints are not used for every patch. A patch being skipped is not proof the unsupported combination is otherwise safe.
