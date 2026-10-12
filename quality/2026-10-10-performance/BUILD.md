# Build HR Fixes 1.1.0

Keep the existing source layout and Python/javac builder. A complete Hollow Realm installation, launcher accounts and world saves are unnecessary.

Requirements: Python 3, JDK 17; Minecraft 1.20.1 SRG client libraries; Forge 47.4.16 client/universal and JavaFML; Mixin 0.8.5; DataFixerUpper 6.0.8, Guava 31.1-jre, Mergetool 1.1.5 API. Supply the audited Eternal Starlight 0.9.0-hotfix, FTB Quests 2001.4.22, FTB Library 2001.2.13, Apotheosis 7.4.8, Placebo 8.6.3 and JEI 15.56.0.205 JARs as compile dependencies. No dependency JAR is embedded or downloaded by the builder.

From the source-package/repository root:

```powershell
python .code/performance/build_patch.py --libraries "C:/path/to/.minecraft/libraries" --mods "C:/path/to/compile-mods" --eternal-starlight "C:/path/to/EternalStarlight-0.9.0-hotfix.jar" --javac "C:/path/to/jdk-17/bin/javac.exe"
```

The supplied library tree follows the launcher's Maven directory layout. Use `--output-dir` to choose an output directory. The current workspace can use the defaults.

`project.json` supplies both the embedded version and `hr-fixes-<version>.jar` filename. The builder checks audited target fingerprints, compiles Java 17 bytecode and writes sorted ZIP entries with fixed timestamps. The extracted source package is independently rebuilt and compared byte-for-byte with the candidate.

The intelligent-gem JSON in `overrides/` is installed separately into Hollow Realm's KubeJS data directory. It is not a compile input and is not placed in the JAR. Review its target mods before copying it to another instance.

Runtime verification is distinct from reproducible compilation. See `RELEASE-1.1.0.md` and `evidence/compat-20261012.json` for the tested environment and limitations. Do not use historical deployment scripts to install this version.
