"""Reproducible Java 17 build, JUnit 5 verification, and packaging for HollowRealm Fixes 2.0.1."""
from __future__ import annotations

import hashlib
import json
import os
import shutil
import struct
import subprocess
import sys
import zipfile
from pathlib import Path

VERSION = "2.0.1"
ARTIFACT_NAME = f"HollowRealm-Fixes-{VERSION}.jar"
FIXED_ZIP_TIME = (2026, 11, 19, 12, 0, 0)

REPO_ROOT = Path(__file__).resolve().parent
MODPACK_CODE_DIR = REPO_ROOT.parent
DIST_DIR = MODPACK_CODE_DIR / "dist"
BUILD_DIR = REPO_ROOT / "build"
MAIN_CLASSES = BUILD_DIR / "classes" / "main"
TEST_CLASSES = BUILD_DIR / "classes" / "test"
LIBS_OUT = BUILD_DIR / "libs"
EVIDENCE_DIR = MODPACK_CODE_DIR / "evidence" / VERSION

JAVA_HOME = Path(os.environ.get("JAVA_HOME", r"C:\Program Files\Zulu\zulu-17"))
JAVAC = JAVA_HOME / "bin" / "javac.exe"
JAVA = JAVA_HOME / "bin" / "java.exe"
JAVAP = JAVA_HOME / "bin" / "javap.exe"

MC_LIBS = Path(r"C:\Users\WinKing\Documents\.minecraft\libraries")
INSTANCE_MODS = Path(r"C:\Users\WinKing\Documents\.minecraft\versions\HollowRealm\mods")
GRADLE_CACHE = Path(r"C:\Users\WinKing\.gradle\caches\modules-2\files-2.1")

FORBIDDEN_REMOVED_STRINGS = (
    b"goetyawaken",
    b"goetyominous",
    b"eternal_starlight",
    b"quest_enhance",
    b"WorldgenRandom",
    b"AttachmentClientMixin",
    b"SorcererWorldgenRandomMixin",
    b"ZealotWorldgenRandomMixin",
    b"QuestPanelLifecycleMixin",
)


def sha256_bytes(data: bytes) -> str:
    return hashlib.sha256(data).hexdigest()


def resolve_compile_classpath() -> tuple[list[Path], Path]:
    client_srg = MC_LIBS / "net/minecraft/client/1.20.1-20230612.114412/client-1.20.1-20230612.114412-srg.jar"
    relative_libs = [
        client_srg,
        MC_LIBS / "net/minecraftforge/forge/1.20.1-47.4.29/forge-1.20.1-47.4.29-client.jar",
        MC_LIBS / "net/minecraftforge/forge/1.20.1-47.4.29/forge-1.20.1-47.4.29-universal.jar",
        MC_LIBS / "net/minecraftforge/javafmllanguage/1.20.1-47.4.29/javafmllanguage-1.20.1-47.4.29.jar",
        MC_LIBS / "net/minecraftforge/fmlloader/1.20.1-47.4.29/fmlloader-1.20.1-47.4.29.jar",
        MC_LIBS / "net/minecraftforge/fmlcore/1.20.1-47.4.29/fmlcore-1.20.1-47.4.29.jar",
        MC_LIBS / "net/minecraftforge/forgespi/7.0.1/forgespi-7.0.1.jar",
        MC_LIBS / "net/minecraftforge/eventbus/6.2.33/eventbus-6.2.33.jar",
        MC_LIBS / "org/spongepowered/mixin/0.8.5/mixin-0.8.5.jar",
        MC_LIBS / "org/ow2/asm/asm/9.9.1/asm-9.9.1.jar",
        MC_LIBS / "org/ow2/asm/asm-commons/9.9.1/asm-commons-9.9.1.jar",
        MC_LIBS / "org/ow2/asm/asm-tree/9.9.1/asm-tree-9.9.1.jar",
        MC_LIBS / "org/apache/logging/log4j/log4j-api/2.19.0/log4j-api-2.19.0.jar",
        MC_LIBS / "org/apache/logging/log4j/log4j-core/2.19.0/log4j-core-2.19.0.jar",
        MC_LIBS / "org/apache/logging/log4j/log4j-slf4j2-impl/2.19.0/log4j-slf4j2-impl-2.19.0.jar",
        MC_LIBS / "org/slf4j/slf4j-api/2.0.1/slf4j-api-2.0.1.jar",
        MC_LIBS / "org/apache/maven/maven-artifact/3.8.5/maven-artifact-3.8.5.jar",
        MC_LIBS / "com/mojang/datafixerupper/6.0.8/datafixerupper-6.0.8.jar",
        MC_LIBS / "com/google/code/gson/gson/2.10/gson-2.10.jar",
        MC_LIBS / "com/google/guava/failureaccess/1.0.1/failureaccess-1.0.1.jar",
        MC_LIBS / "com/google/guava/guava/31.1-jre/guava-31.1-jre.jar",
        MC_LIBS / "it/unimi/dsi/fastutil/8.5.9/fastutil-8.5.9.jar",
        MC_LIBS / "org/apache/commons/commons-lang3/3.12.0/commons-lang3-3.12.0.jar",
        MC_LIBS / "net/minecraftforge/mergetool/1.1.5/mergetool-1.1.5-api.jar",
        INSTANCE_MODS / "[神化] Apotheosis-1.20.1-7.4.8.jar",
        INSTANCE_MODS / "Placebo-1.20.1-8.6.3.jar",
        INSTANCE_MODS / "[JEI物品管理器] jei-1.20.1-forge-15.56.0.205.jar",
    ]
    missing = [str(p) for p in relative_libs if not p.is_file()]
    if missing:
        raise FileNotFoundError("Missing required build dependencies:\n" + "\n".join(missing))
    return relative_libs, client_srg


def resolve_junit_jars() -> list[Path]:
    needed = [
        ("org.apiguardian", "apiguardian-api-1.1.2.jar"),
        ("org.opentest4j", "opentest4j-1.3.0.jar"),
        ("org.junit.jupiter", "junit-jupiter-api-5.10.3.jar"),
        ("org.junit.jupiter", "junit-jupiter-engine-5.10.3.jar"),
        ("org.junit.jupiter", "junit-jupiter-params-5.10.3.jar"),
        ("org.junit.platform", "junit-platform-commons-1.10.3.jar"),
        ("org.junit.platform", "junit-platform-engine-1.10.3.jar"),
        ("org.junit.platform", "junit-platform-launcher-1.10.3.jar"),
    ]
    found: list[Path] = []
    for group, jar_name in needed:
        matches = list((GRADLE_CACHE / group).rglob(jar_name))
        if not matches:
            raise FileNotFoundError(f"Missing JUnit jar {group}/{jar_name} under {GRADLE_CACHE}")
        found.append(matches[0])
    return found


def run_checked(cmd: list[str], cwd: Path | None = None) -> subprocess.CompletedProcess[str]:
    proc = subprocess.run(
        cmd,
        cwd=cwd or REPO_ROOT,
        capture_output=True,
        text=True,
        encoding="utf-8",
        errors="replace",
    )
    if proc.returncode != 0:
        raise RuntimeError(
            f"Command failed ({proc.returncode}): {' '.join(cmd)}\nSTDOUT:\n{proc.stdout}\nSTDERR:\n{proc.stderr}"
        )
    return proc


def compile_main(compile_cp: list[Path]) -> list[Path]:
    if BUILD_DIR.exists():
        shutil.rmtree(BUILD_DIR)
    MAIN_CLASSES.mkdir(parents=True, exist_ok=True)
    sources = sorted((REPO_ROOT / "src/main/java").rglob("*.java"))
    if not sources:
        raise RuntimeError("No main Java sources found")
    run_checked([
        str(JAVAC),
        "--release", "17",
        "-encoding", "UTF-8",
        "-proc:none",
        "-Xlint:unchecked",
        "-cp", os.pathsep.join(map(str, compile_cp)),
        "-d", str(MAIN_CLASSES),
        *map(str, sources),
    ])
    return sources


def compile_and_run_tests(compile_cp: list[Path], junit_jars: list[Path], client_srg: Path) -> str:
    TEST_CLASSES.mkdir(parents=True, exist_ok=True)
    test_resources = REPO_ROOT / "src/test/resources"
    if test_resources.exists():
        for res in test_resources.rglob("*"):
            if res.is_file():
                dst = TEST_CLASSES / res.relative_to(test_resources)
                dst.parent.mkdir(parents=True, exist_ok=True)
                shutil.copy2(res, dst)
    test_sources = sorted((REPO_ROOT / "src/test/java").rglob("*.java"))
    runner_src = REPO_ROOT / "tooling/TestSuiteRunner.java"
    all_test_cp = [MAIN_CLASSES, *compile_cp, *junit_jars]
    run_checked([
        str(JAVAC),
        "--release", "17",
        "-encoding", "UTF-8",
        "-proc:none",
        "-cp", os.pathsep.join(map(str, all_test_cp)),
        "-d", str(TEST_CLASSES),
        str(runner_src),
        *map(str, test_sources),
    ])
    runtime_cp = [TEST_CLASSES, *all_test_cp]
    proc = run_checked([
        str(JAVA),
        f"-Dhrfixes.mods={INSTANCE_MODS.as_posix()}",
        f"-Dhrfixes.srg={client_srg.as_posix()}",
        "-cp", os.pathsep.join(map(str, runtime_cp)),
        "TestSuiteRunner",
        "hollowrealm.performance",
    ])
    return proc.stdout


def package_and_verify() -> tuple[Path, dict]:
    resources_dir = REPO_ROOT / "src/main/resources"
    entries: dict[str, bytes] = {}
    for p in resources_dir.rglob("*"):
        if p.is_file():
            entries[p.relative_to(resources_dir).as_posix()] = p.read_bytes()
    entries["LICENSE"] = (REPO_ROOT / "LICENSE").read_bytes()
    for p in MAIN_CLASSES.rglob("*.class"):
        entries[p.relative_to(MAIN_CLASSES).as_posix()] = p.read_bytes()

    # Self-checks before writing final JAR
    class_entries = {k: v for k, v in entries.items() if k.endswith(".class")}
    for name, data in class_entries.items():
        if data[:4] != b"\xca\xfe\xba\xbe":
            raise AssertionError(f"Invalid class header in {name}")
        major = struct.unpack(">H", data[6:8])[0]
        if major != 61:
            raise AssertionError(f"Expected Java 17 class major version 61 in {name}, got {major}")

    mixins_cfg = json.loads(entries["hollowrealm-performance.mixins.json"].decode("utf-8"))
    declared_mixins = set(mixins_cfg.get("mixins", [])) | set(mixins_cfg.get("client", []))
    actual_mixins = {
        Path(k).stem
        for k in class_entries
        if k.startswith("hollowrealm/performance/mixin/") and "$" not in Path(k).name
    }
    if declared_mixins != actual_mixins:
        raise AssertionError(f"Mixin mismatch: declared={sorted(declared_mixins)} actual={sorted(actual_mixins)}")

    manifest_ledger = json.loads(entries["hr-fixes-manifest.json"].decode("utf-8"))
    if manifest_ledger.get("version") != VERSION:
        raise AssertionError(f"Manifest ledger version mismatch: {manifest_ledger.get('version')}")
    ledger_mixins = {p["mixin"] for p in manifest_ledger.get("livePatches", [])}
    if ledger_mixins != declared_mixins:
        raise AssertionError(f"Ledger mixin mismatch: ledger={sorted(ledger_mixins)} mixins.json={sorted(declared_mixins)}")

    mods_toml = entries["META-INF/mods.toml"].decode("utf-8")
    for expected in (
        f'version="{VERSION}"',
        'modId="hollowrealm_performance_compat"',
        'displayName="HollowRealm Fixes"',
        'license="MIT"',
        'displayTest="IGNORE_SERVER_VERSION"',
    ):
        if expected not in mods_toml:
            raise AssertionError(f"Missing {expected!r} in META-INF/mods.toml")

    plugin_bytes = entries["hollowrealm/performance/HRFixesMixinPlugin.class"]
    for forbidden in FORBIDDEN_REMOVED_STRINGS:
        if forbidden in plugin_bytes:
            raise AssertionError(f"Forbidden dead reference {forbidden!r} found in HRFixesMixinPlugin.class")

    apoth_mixin_bytes = entries["hollowrealm/performance/mixin/ApotheosisJeiMixin.class"]
    if b"enableAdventure" not in apoth_mixin_bytes:
        raise AssertionError("ApotheosisJeiMixin.class missing Apotheosis.enableAdventure guard")

    coremod_js = entries["coremods/structurify_overlap_gate.js"].decode("utf-8")
    if "hollowrealm/performance/HRFixesMixinPlugin" not in coremod_js or "shouldDisableStructurifyOverlap" not in coremod_js:
        raise AssertionError("Coremod JS missing expected gate method reference")

    javap_out = run_checked([str(JAVAP), "-p", str(MAIN_CLASSES / "hollowrealm/performance/HRFixesMixinPlugin.class")]).stdout
    if "public static boolean shouldDisableStructurifyOverlap()" not in javap_out:
        raise AssertionError("javap did not find public static boolean shouldDisableStructurifyOverlap()")

    LIBS_OUT.mkdir(parents=True, exist_ok=True)
    jar_path = LIBS_OUT / ARTIFACT_NAME
    with zipfile.ZipFile(jar_path, "w", zipfile.ZIP_DEFLATED) as zf:
        for name in sorted(entries):
            info = zipfile.ZipInfo(name, FIXED_ZIP_TIME)
            info.compress_type = zipfile.ZIP_DEFLATED
            info.external_attr = 0o644 << 16
            zf.writestr(info, entries[name])

    jar_bytes = jar_path.read_bytes()
    jar_sha = sha256_bytes(jar_bytes)

    DIST_DIR.mkdir(parents=True, exist_ok=True)
    dist_jar = DIST_DIR / ARTIFACT_NAME
    shutil.copy2(jar_path, dist_jar)
    (DIST_DIR / f"SHA256SUMS-{VERSION}.txt").write_text(f"{jar_sha} *{ARTIFACT_NAME}\n", encoding="utf-8")

    build_manifest = {
        "version": VERSION,
        "artifact": ARTIFACT_NAME,
        "bytes": len(jar_bytes),
        "sha256": jar_sha,
        "target": {
            "minecraft": "1.20.1",
            "forge_range": "[47.4.16,48)",
            "compiled_against_forge": "47.4.29",
            "java": 17,
        },
        "classes": {k: len(v) for k, v in sorted(class_entries.items())},
        "live_mixins": sorted(declared_mixins),
        "entry_count": len(entries),
    }
    (DIST_DIR / f"BUILD-MANIFEST-{VERSION}.json").write_text(
        json.dumps(build_manifest, indent=2, ensure_ascii=False) + "\n", encoding="utf-8"
    )
    return dist_jar, build_manifest


def main() -> int:
    for tool in (JAVAC, JAVA, JAVAP):
        if not tool.is_file():
            raise FileNotFoundError(f"Required JDK tool missing: {tool}")
    compile_cp, client_srg = resolve_compile_classpath()
    junit_jars = resolve_junit_jars()
    sources = compile_main(compile_cp)
    test_output = compile_and_run_tests(compile_cp, junit_jars, client_srg)
    dist_jar, manifest = package_and_verify()

    EVIDENCE_DIR.mkdir(parents=True, exist_ok=True)
    (EVIDENCE_DIR / "junit-output.txt").write_text(test_output, encoding="utf-8")
    (EVIDENCE_DIR / "build-manifest.json").write_text(
        json.dumps(manifest, indent=2, ensure_ascii=False) + "\n", encoding="utf-8"
    )

    print(test_output.rstrip())
    print(f"[BUILD] Compiled {len(sources)} main sources -> {dist_jar}")
    print(f"[BUILD] Size={manifest['bytes']} B SHA-256={manifest['sha256']}")
    print(f"[BUILD] SELF_CHECKS=PASS entries={manifest['entry_count']} classes={len(manifest['classes'])}")
    return 0


if __name__ == "__main__":
    sys.exit(main())
