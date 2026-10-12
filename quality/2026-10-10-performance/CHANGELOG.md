# Changelog

## 1.1.0 (Beta)

- Preserve FTB Quests' empty quest-panel lifecycle before Quest Enhance reads the description. Valid quest descriptions, persistent-data requests, editing, progress and rewards continue through their original handlers.
- Add Apotheosis socketing support to JEI's smithing display using valid, linked equipment/gem/output examples across supported gem rarities. Actual socketing recipes and equipment statistics are unchanged.
- Verify the existing Structurify overlap workaround with Collections Of Optimizations 6.5 as well as 4.6; accept these two audited builds.
- Retain the Eternal Starlight attachment and Goety worldgen random-source fixes.
- Align the JAR filename, embedded mod version and source metadata.

The separate Hollow Realm data override corrects the intelligent gem's existing Ars Nouveau mana attribute ID and removes an unregistered spellbook category. All numeric values are retained. This override is **not bundled in the mod JAR**.

Validated on Minecraft 1.20.1 / Forge 47.4.16 with Hollow Realm's current client and integrated server. Dedicated servers and long-term stability have not been tested. No FPS, TPS or memory improvement is claimed.
