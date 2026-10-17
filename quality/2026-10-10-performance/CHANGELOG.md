# Changelog

## 1.1.2-rc.1 (development candidate, 2026-10-17 — NOT_READY)

- Validate the original bytecode location of both audited Goety worldgen random-source Redirects before enabling them. Require exactly one matching `GETFIELD` instruction with the expected owner, field name and descriptor in the target method.
- Skip targets with missing, duplicate or mismatched injection sites, or unreadable class bytes. Read raw class resources without loading target classes or invoking the transforming bytecode provider.
- Preserve all six existing Mixin implementations, optional-dependency version allowlists and the narrowly scoped CoO/Structurify workaround. No new target versions, gameplay changes or performance claims.
- Add real-target bytecode regression tests, native Forge baseline/candidate drift fixtures, focused client/server smoke checks and independent source-package rebuild verification.
- This candidate is not published or deployed. Actual Goety Ominous dedicated-server gameplay is blocked by its existing client-class loading failure, reproduced with the stable baseline. Full gameplay and multiplayer acceptance remain incomplete; see the candidate report.

## 1.1.1-rc.1 (Beta)

- Remove third-party loading dependencies and exact version constraints. Missing or unaudited optional targets skip their patches; Minecraft and Forge remain required.
- Guard Mixin application by audited versions and original target method signatures, without loading or transforming those classes early. Keep the original attachment, Goety, quest and socketing fixes.
- Limit the CoO overlap workaround to audited CoO/Structurify pairs, including Structurify 2.0.43. Keep the original structure-generation behavior.
- Verify native client and minimal dedicated-server loading with Structurify 2.0.42/2.0.43, missing Structurify, missing CoO and all patch targets absent. Full modpack dedicated-server gameplay remains NOT_TESTED.

No new rendering optimization or measured performance improvement is claimed. The companion intelligent-gem data correction is unchanged.

## 1.1.0 (Beta)

- Preserve FTB Quests' empty quest-panel lifecycle before Quest Enhance reads the description. Valid quest descriptions, persistent-data requests, editing, progress and rewards continue through their original handlers.
- Add Apotheosis socketing support to JEI's smithing display using valid, linked equipment/gem/output examples across supported gem rarities. Actual socketing recipes and equipment statistics are unchanged.
- Verify the existing Structurify overlap workaround with Collections Of Optimizations 6.5 as well as 4.6; accept these two audited builds.
- Retain the Eternal Starlight attachment and Goety worldgen random-source fixes.
- Align the JAR filename, embedded mod version and source metadata.

The separate Hollow Realm data override corrects the intelligent gem's existing Ars Nouveau mana attribute ID and removes an unregistered spellbook category. All numeric values are retained. This override is **not bundled in the mod JAR**.

Validated on Minecraft 1.20.1 / Forge 47.4.16 with Hollow Realm's current client and integrated server. Dedicated servers and long-term stability have not been tested. No FPS, TPS or memory improvement is claimed.
