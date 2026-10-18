# HR Fixes 1.1.2-rc.1 (Beta)

Targeted compatibility fixes for Minecraft 1.20.1 / Forge. Qualification: **READY_WITH_DOCUMENTED_LIMITS** for the tested environments below. This does not qualify a complete Hollow Realm dedicated server.

## Changes

- Harden the safety gate for the existing Goety Awaken and Goety Ominous worldgen random-source Redirects. Before enabling either patch, inspect the original target method and require exactly one matching `GETFIELD` instruction with the audited owner, field name and descriptor.
- Safely skip missing, duplicate, mismatched or unreadable injection sites without loading the target class or requesting transformed bytecode during target registration. Keep the strict Mixin injection requirements.
- Preserve all six existing Mixin implementations, optional-dependency version allowlists and the narrowly scoped CoO/Structurify gate. This release does not add support for CoO 4.8.

The original audited Goety binaries already have valid injection sites. The corrected defect is a reproducible safety-gating gap: native Forge fixtures with missing or duplicate sites cause the stable gate to enable an invalid Redirect; the candidate skips those fixtures safely. This is not a claim that the original audited Goety JARs fail with the same injection error.

## Tested environment and dependencies

| Component | Verified scope |
| --- | --- |
| Minecraft / loader | Minecraft 1.20.1, Forge |
| Runtime / bytecode | Java 21 runtime; Java 17 bytecode (class major 61) |
| Client | Forge 47.4.16 and 47.4.22; focused six-Mixin smoke checks |
| Dedicated server | Forge 47.4.16; minimal loading, documented Awaken-only case, and the CoO 6.5 / Structurify 2.0.43 gate |
| Forge 47.4.22 dedicated server | **NOT_TESTED** |

Only Minecraft and Forge are mandatory loading dependencies. Third-party patches activate only for their audited targets; absent or unaudited optional targets skip their patch. The loader's accepted version range is broader than the tested matrix and is not a compatibility guarantee for every Forge build or mod combination.

## Known unsupported server combination

**Goety Ominous 3.0.0.fix-1.20.1 + Goety 2.5.58.4 + Curios 5.14.1 on a Forge 47.4.16 / Java 21 dedicated server is excluded from this release's validated support scope.** The failure also occurs with the recorded Awaken/GeckoLib additions.

Ominous's shared `CroneRobeEvents` subscriber contains a client-tick implementation that causes JVM verification to resolve the client-only `LocalPlayer` class during server subscriber registration. The same failure reproduces without HR Fixes, with 1.1.1-rc.1 and with this candidate. Removing Ominous permits the minimal positive-control servers to start, save and stop. This is a confirmed third-party defect independent of HR Fixes; installing or rolling back HR Fixes does not repair it. Other Ominous dedicated-server combinations have not been qualified here.

**A complete Hollow Realm dedicated server remains NOT_READY.**

## Verification and remaining limits

- Candidate safety-gate checks: 70/70; the frozen stable gate retains 18 expected safety-contract failures.
- Native missing/duplicate-site fixtures: stable injection failures versus candidate safe skips.
- Focused native clients on Forge 47.4.16 / 47.4.22 and the documented minimal server cases passed. These are targeted smoke checks, not full gameplay acceptance.
- The independently extracted source archive rebuilt the complete candidate JAR byte-for-byte. The binary is unchanged after the final server-attribution review.

**NOT_TESTED:** Forge 47.4.22 dedicated server, Ominous dedicated-server gameplay, full modpack gameplay, multiplayer, long-term stability, production personal saves, natural entity chunk persistence, real attachment networking, complete quest/reward UI flows, and full native smithing consumption / rarity-focus interactions.

No measured FPS, TPS or memory improvement is claimed. No third-party JAR, gameplay configuration, quest content or saved-world format is changed by this update.

## Files, source and checksums

```text
0a10e204ec257e92bc0f2184e6d1e07bdc3389de281b221e1d7d7d4d9786e3a6  hr-fixes-1.1.2-rc.1.jar
4cf5dccdab82afa6a462ac25d50fd6fba8da13a2d26bff5ee79b3d25aeac530c  hr-fixes-1.1.2-rc.1-sources.zip
```

The original source ZIP preserves implementation commit `5a6d876960766da3c985fe0f5d1f1125e8b21d05` and historical task-001 NOT_READY documentation. The 2026-10-18 task-002 qualification supersedes that general readiness label only for the documented supported scope; no binary was replaced or silently repacked. These release notes carry that final scope alongside the frozen source snapshot.

Extract the source ZIP into a separate folder. Follow `quality/2026-10-10-performance/BUILD.md` and run `.code/performance/build_patch.py` with Python 3, JDK 17, the documented local Forge/Minecraft/Mixin libraries and audited compile-mod JARs. Use an explicit output directory and compare the complete rebuilt JAR SHA-256 above. The builder downloads or embeds no dependency JARs. Java 17 bytecode does not constitute a tested Java 17 runtime claim.

## Install or roll back

Close the game/server before replacing the previous HR Fixes JAR. Keep only one `hollowrealm_performance_compat` mod ID. Do not deploy this candidate into the excluded Ominous server combination expecting the third-party defect to be fixed.

To roll back, close the process, remove 1.1.2-rc.1 and restore the retained `hr-fixes-1.1.1-rc.1.jar`:

```text
0b272bbf80fcd5a0ac99ba90f156752cb362bc5ce07432531ad0829b32bedd8f  hr-fixes-1.1.1-rc.1.jar
```

No save, entity-NBT, KubeJS or third-party-mod edits are required for this rollback. It does not resolve Ominous's independent server failure.
