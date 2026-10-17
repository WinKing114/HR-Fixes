# HR Fixes 1.1.2-rc.1 — NOT_READY

This is a development candidate uploaded for review, not an approved release.
Do not deploy it to a production instance. No GitHub Release or tag is created.

The binary and independently rebuildable source ZIP are the unchanged artifacts
from local source commit `5a6d876960766da3c985fe0f5d1f1125e8b21d05`, based on
`54060e5fbd26852306a241208fec814e4a8da356` (1.1.1-rc.1).
The surrounding repository also contains this upload's documentation and artifacts.

- [Candidate JAR](hr-fixes-1.1.2-rc.1.jar)
- [Independent source ZIP](hr-fixes-1.1.2-rc.1-sources.zip)
- [Build provenance and hashes](provenance-1.1.2-rc.1.json)
- [SHA-256 manifest](SHA256SUMS.txt)
- [Retained stable rollback JAR](rollback/hr-fixes-1.1.1-rc.1.jar)
- [Validation report](../../quality/2026-10-10-performance/RELEASE-1.1.2-rc.1.md)
- [English changelog](../../quality/2026-10-10-performance/CHANGELOG.md)

Both native client versions (Forge 47.4.16 and 47.4.22 / Java 21) passed focused
six-Mixin smoke checks. Raw-target gate tests passed 70/70, native synthetic drift
fixtures reproduced baseline injection failures and candidate safe skips, and
independent source rebuilding produced the identical Java 17-compatible JAR.
Minimal dedicated-server loading and the actual Awaken-only server case passed.

Actual Goety Ominous dedicated-server loading fails in that mod's automatic
subscriber registration with both baseline and candidate (`LocalPlayer` on the
server). Its target gameplay and the report's remaining full-gameplay cases are
NOT_TESTED. The candidate remains NOT_READY. Historical statements about not
uploading refer to the development/validation session before this authorized upload;
they do not imply that this review branch is an approved publication.

Rollback in a test instance: stop the client/server, remove the candidate and
restore the retained 1.1.1-rc.1 JAR, verifying the manifest. Keep exactly one JAR
with mod ID `hollowrealm_performance_compat`; do not change worlds or modpack data.
