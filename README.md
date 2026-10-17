# HR Fixes

Targeted compatibility and bug fixes for Minecraft 1.20.1 / Forge.
Independent third-party project. Mod ID: `hollowrealm_performance_compat`.

Development candidate: **1.1.2-rc.1 — NOT_READY; not published or deployed**.
[Candidate artifacts and hashes](candidates/1.1.2-rc.1/README.md).

Stable baseline: **1.1.1-rc.1**, commit `54060e5fbd26852306a241208fec814e4a8da356`.
Runtime Java 21; build target Java 17 (class major 61).

The candidate verifies the exact raw `GETFIELD Level.f_46441_:RandomSource`
instruction count before enabling either audited Goety Redirect. Missing,
duplicate or mismatched sites safely skip the patch. Version allowlists and
the six existing Mixin implementations remain unchanged.

[Build and tests](quality/2026-10-10-performance/BUILD.md) ·
[Candidate evidence and limitations](quality/2026-10-10-performance/RELEASE-1.1.2-rc.1.md) ·
[Changelog](quality/2026-10-10-performance/CHANGELOG.md)

Both Forge 47.4.16 and 47.4.22 native clients passed the focused six-Mixin
smoke checks. Minimal Forge 47.4.16 dedicated-server loading passed. The actual
Goety Ominous dedicated-server combination fails in that mod's subscriber
registration with both baseline and candidate; its server gameplay remains
NOT_TESTED. See the report before considering any deployment.

The maintained layout remains `quality/2026-10-10-performance/` and
`.code/performance/`. The source package includes build/test tools and filtered
evidence, without dependency JARs, test mods, modpack overrides, worlds or accounts.
No measured FPS/TPS gain is claimed. MIT license.
