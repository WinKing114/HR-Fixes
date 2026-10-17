# Build HR Fixes 1.1.2-rc.1 (NOT_READY candidate)

Keep the existing source layout and Python/javac builder. A complete Hollow Realm installation, launcher accounts and world saves are unnecessary.

Requirements: Python 3, JDK 17; Minecraft 1.20.1 SRG client libraries; Forge 47.4.16 client/universal and JavaFML; Mixin 0.8.5; DataFixerUpper 6.0.8, Guava 31.1-jre, Mergetool 1.1.5 API; Forge FMLLoader 47.4.16, ForgeSPI 7.0.1, ASM/ASM Tree 9.8 and Maven Artifact 3.8.5. Supply the audited Eternal Starlight 0.9.0-hotfix, FTB Quests 2001.4.22, FTB Library 2001.2.13, Apotheosis 7.4.8, Placebo 8.6.3 and JEI 15.56.0.205 JARs as compile dependencies. No dependency JAR is embedded or downloaded by the builder.

From the source-package/repository root:

```powershell
python .code/performance/build_patch.py --libraries "C:/path/to/.minecraft/libraries" --mods "C:/path/to/compile-mods" --eternal-starlight "C:/path/to/EternalStarlight-0.9.0-hotfix.jar" --javac "C:/path/to/jdk-17/bin/javac.exe"
```

The supplied library tree follows the launcher's Maven directory layout. Use `--output-dir` to choose an output directory. The current workspace can use the defaults.

`project.json` supplies both the embedded version and `hr-fixes-<version>.jar` filename. The builder checks audited target fingerprints, compiles Java 17 bytecode and writes sorted ZIP entries with fixed timestamps. The extracted source package is independently rebuilt and compared byte-for-byte with the candidate.

Companion modpack data is not a compile input and is not included in this candidate or its source package.

Runtime verification is distinct from reproducible compilation. See `RELEASE-1.1.2-rc.1.md` and `evidence/1.1.2-rc.1/` for this candidate's evidence. Historical reports do not validate this binary. Do not use historical deployment scripts to install it.

## Regression tests

`python .code/performance/test_redirect_gate.py` compiles the production plugin and a frozen baseline plugin against real ASM/Mixin APIs. Forge discovery and resource access use test doubles. It reads hash-pinned original Goety Awaken/Ominous classes, mutates bytes only in memory and compares both gates: 70 checks, baseline 18 expected failures, candidate zero. No Git history is needed in the source ZIP. Use `--libraries`, `--mods`, `--javac` and `--java` for another installation.

`python .code/performance/package_candidate.py` packages only HR Fixes sources and evidence, extracts them into a separate temporary directory, rebuilds with the existing builder, compares the complete JAR bytes and reruns the gate tests. It accepts the same dependency path options as the builder. The output SHA-256 values are in `release/provenance-1.1.2-rc.1.json`.

## Native Forge fixtures

`python .code/performance/native_smoke.py --help` documents the native test launcher. It uses an offline test identity, Java 21, an isolated working directory and a test-only QA mod. It reads public launcher version metadata without opening account files. It never prepares a fixture from production worlds or configurations. The client needs `--world-template` pointing to `level.dat` from a fresh disposable vanilla world; the server needs an installed native Forge `--server-libraries` tree containing that version's `win_args.txt` and server JAR. This launcher currently targets Windows.

Examples from the repository/source-package root:

```powershell
python .code/performance/native_smoke.py --side client --forge 47.4.22 --case all --world-template "C:/QA/fresh-vanilla/level.dat"
python .code/performance/native_smoke.py --side client --forge 47.4.22 --case all --phase read
python .code/performance/native_smoke.py --side server --forge 47.4.16 --case none --server-libraries "C:/QA/forge-server/libraries"
python .code/performance/native_smoke.py --side server --forge 47.4.16 --case drift-missing --server-libraries "C:/QA/forge-server/libraries"
```

Use separate `--work-root` paths for baseline/candidate launches, `--jar` to select the retained baseline, and `--reuse` only to retry an unchanged prepared mod inventory. Cases `drift-missing`, `drift-duplicate` and `unknown` compile an entirely self-authored target fixture with a synthetic Goety mod ID/version; no third-party JAR is modified. Baseline drift launches are expected to fail with a Mixin injection error. `all` uses actual target JARs for six-Mixin client smoke checks; `goety` limits this to the two Redirects; `awaken` omits Ominous for a dedicated-server-compatible target test. `coo` requires explicit audited `--coo` and `--structurify` paths. The read phase reopens the same isolated world and restores native serialized trade NBT from the preceding process; it does not test natural chunk/entity persistence.

Each fixture records its exact mod inventory, filtered results and a raw-log hash. PASS requires completion markers, exit code zero and a normal world-save marker; an exit code alone is insufficient. Experimental-world prompts are accepted only in the fresh disposable fixture, and a Forge loading screen is bypassed only after confirming its error list is empty. Failed harness preparations are not runtime PASS results.
