# HR Fixes

**Targeted compatibility and bug fixes for the Hollow Realm modpack**

[![Release](https://img.shields.io/badge/release-1.2.0-blue)](https://github.com/WinKing114/HR-Fixes/releases)
[![Minecraft](https://img.shields.io/badge/minecraft-1.20.1-green)](https://www.minecraft.net/)
[![Forge](https://img.shields.io/badge/forge-47.4.16%2B-orange)](https://files.minecraftforge.net/)
[![License](https://img.shields.io/badge/license-MIT-lightgrey)](LICENSE)

HR Fixes is a small Forge mod maintained for the **Hollow Realm** modpack. It fixes reproducible
conflicts, crashes and lifecycle bugs in third-party mods with narrowly scoped Mixin patches,
instead of changing gameplay values, recipes, quests or world data.

- **Minecraft:** 1.20.1
- **Forge:** `[47.4.16, 48)`
- **Mod ID:** `hollowrealm_performance_compat`
- **Java:** 17 bytecode
- **Current release:** `1.2.0`

## Patches

Every patch is optional and version-gated: it only runs on the exact third-party build it was
audited against, and it fails closed (logs why and skips) when a target is missing or changed.

| Patch | Target | Problem it fixes |
| --- | --- | --- |
| `AttachmentClientMixin` | Eternal Starlight 0.9.0 | Client attachment packet handles a null sender |
| `ZealotWorldgenRandomMixin` | Goety Ominous 3.0.0.fix-1.20.1 | Off-thread `Level.random` use during worldgen |
| `SorcererWorldgenRandomMixin` | Goety Awaken 1.3.9.4 | Same class of bug (mod not installed in the current pack) |
| `QuestPanelLifecycleMixin` | FTB Quests 2001.4.22 + Quest Enhance 3.13/3.14 | Null quest dereference in an empty quest panel |
| `ApotheosisJeiMixin` | Apotheosis 7.4.8 + JEI 15.56.0.205 | Missing socketing category extension |
| `SocketingJeiLayoutMixin` | JEI 15.56.0.205 | Focus links for socketing recipes in the smithing display |
| `LodestoneTooltipMixin` | Lodestone 1.20.1-1.6.4.1 + AttributesLib 1.3.7 | Concurrent tooltip callback race |
| `KubeJsAttackCacheMixin` | L2 Hostility 2.5.19 + L2 Damage Tracker 0.4.4 | `ConcurrentModificationException` aborts mod loading |
| `ChatLineHeightGuardMixin` | Minecraft 1.20.1 | Chat panel divides by zero every frame |
| `structurify_overlap_gate` (CoreMod) | CoO 4.6/5.5/6.5 + Structurify 2.0.42/2.0.43 | CoO's Structurify overlap Mixin on incompatible builds |

## How patches are gated

Each patch checks, at Mixin preparation time:

1. the installed mod version is in the audited list,
2. the original target class bytes have the expected shape (method, field, local-variable layout
   or SHA-256 fingerprint), and
3. the audited fix still applies to that shape.

If any check fails the patch is skipped with a logged reason — a missing or newer third-party mod
never blocks loading, and a changed target never crashes the game. `latest.log` reports `APPLY` /
`SKIP` for gate decisions and `MERGED` when this mod's `postApply` actually ran for a target.

## Installation

1. Close Minecraft.
2. Put **one** HR Fixes JAR into `mods/` — never two files with mod ID `hollowrealm_performance_compat`.
3. Start the game and check `latest.log` for `HR Fixes loaded` plus the `APPLY`/`SKIP` lines.

No world, NBT, KubeJS or other mod files are modified. To roll back, delete the JAR and restore the
previous one.

## Compatibility

Audited optional targets: Eternal Starlight 0.9.0, Goety Ominous 3.0.0.fix-1.20.1, Goety Awaken
1.3.9.4, FTB Quests 2001.4.22, Quest Enhance 1.20.1-forge-3.13/3.14, Apotheosis 7.4.8, JEI
15.56.0.205, Lodestone 1.20.1-1.6.4.1, AttributesLib 1.3.7, Collections Of Optimizations
4.6/5.5/6.5, Structurify 2.0.42/2.0.43, L2 Hostility 2.5.19, L2 Damage Tracker 0.4.4.

Forge metadata declares only Minecraft and Forge as required dependencies. Optional targets are
checked at runtime; a missing or newer mod only disables that one patch.

## Verification

Release 1.2.0 was validated with:

| Check | Result |
| --- | --- |
| Binary gate assertions (real target bytes + real installed metadata) | 74 / 74 |
| Release JAR structure (classes, resources, mixin sides, CoreMod wiring) | 26 / 26 |
| Broken-JAR negative controls | 6 / 6 |
| JVM race assertions on the real L2 classes | 7 / 7 |
| Isolated real Forge 47.4.22 client transformation checks | 8 / 8 |
| Independent rebuild from the published source package | identical SHA-256 |

**Not verified:** full-pack gameplay, in-game quest/JEI interaction, dedicated servers, multiplayer
and long-term stability. No FPS, TPS or memory improvement is claimed.

## Project layout

```
quality/<date>-<version>/   release source tree, report, evidence and metadata per version
.code/performance/          build, gate, race, packaging and native transformation tools
docs/archive/               historical README snapshots
CHANGELOG.md                release history
```

Each version directory contains its own `README.md`, `RELEASE-*.md`, `COMPATIBILITY.md`, `AUDIT.md`,
`BUILD.md` and `evidence/` so a published build can be reproduced and audited later.

## Links

- [Changelog](CHANGELOG.md)
- [1.2.0 release notes](quality/2026-11-03-1.2.0/RELEASE-1.2.0.md)
- [Compatibility and version boundaries](quality/2026-11-03-1.2.0/COMPATIBILITY.md)
- [Bytecode audit](quality/2026-11-03-1.2.0/AUDIT.md)
- [Build and verification](quality/2026-11-03-1.2.0/BUILD.md)
- [October 24, 2026 README snapshot](docs/archive/README-2026-10-24-1.1.3-rc.1.md)

## License

MIT — see [LICENSE](LICENSE).

HR Fixes is an independent third-party project. It is not affiliated with Eternal Starlight, FTB,
Apotheosis, JEI, Lodestone, L2 Hostility or the Hollow Realm modpack authors.
