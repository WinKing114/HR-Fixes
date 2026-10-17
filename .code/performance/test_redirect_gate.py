"""Compare baseline and candidate gates with real Goety bytes and in-memory mutations.

Discovery/resource access use test doubles. This does not replace native Forge tests.
Only local audited dependencies are read; no third-party JAR is written.
"""
from pathlib import Path
import argparse
import hashlib
import json
import os
import subprocess
import tempfile

ROOT = Path(__file__).resolve().parents[2]
PROJECT = ROOT / 'quality/2026-10-10-performance'
BASELINE = '54060e5fbd26852306a241208fec814e4a8da356'
PLUGIN = 'quality/2026-10-10-performance/src/hollowrealm/performance/HRFixesMixinPlugin.java'
STUBS = {
    'net/minecraftforge/fml/loading/LoadingModList.java': '''package net.minecraftforge.fml.loading;
import java.util.*;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;
public class LoadingModList {
 public static final Map<String,String> versions = new HashMap<>();
 public static boolean absent;
 public static LoadingModList get() { return absent ? null : new LoadingModList(); }
 public List<ModInfo> getMods() { return versions.entrySet().stream().map(e -> new ModInfo(e.getKey(),e.getValue())).toList(); }
}''',
    'net/minecraftforge/fml/loading/moddiscovery/ModInfo.java': '''package net.minecraftforge.fml.loading.moddiscovery;
public record ModInfo(String getModId, String getVersion) {}''',
    'org/spongepowered/asm/service/MixinService.java': '''package org.spongepowered.asm.service;
import java.io.*;
public class MixinService {
 public static byte[] bytes;
 public static String resource;
 public static int reads;
 public static boolean ioFailure;
 public static MixinService getService() { return new MixinService(); }
 public InputStream getResourceAsStream(String name) throws IOException {
  reads++;
  if (!name.equals(resource)) throw new AssertionError("unexpected target resource " + name);
  if (ioFailure) throw new IOException("test fixture");
  return bytes == null ? null : new ByteArrayInputStream(bytes);
 }
}''',
}

def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--libraries', type=Path, default=Path('C:/Users/WinKing/Documents/.minecraft/libraries'))
    parser.add_argument('--mods', type=Path, default=Path('C:/Users/WinKing/Documents/.minecraft/versions/Hollow Realm/mods'))
    parser.add_argument('--javac', default='C:/Program Files/Zulu/zulu-17/bin/javac.exe')
    parser.add_argument('--java', default='C:/Program Files/Zulu/zulu-21/bin/java.exe')
    parser.add_argument('--baseline-source', type=Path, help='Baseline plugin source when Git history is unavailable')
    parser.add_argument('--report', type=Path, default=PROJECT / 'evidence/redirect-gate-1.1.2-rc.1.json')
    args = parser.parse_args()
    cp = os.pathsep.join(str(args.libraries / p) for p in (
        'org/ow2/asm/asm/9.8/asm-9.8.jar', 'org/ow2/asm/asm-tree/9.8/asm-tree-9.8.jar',
        'org/spongepowered/mixin/0.8.5/mixin-0.8.5.jar'))
    meta = json.loads((PROJECT / 'project.json').read_text('utf-8'))
    targets = []
    fingerprints = {}
    for mod in ('goetyawaken', 'goetyominous'):
        record = meta['tested_optional_mods'][mod]
        path = args.mods / record['file']
        digest = hashlib.sha256(path.read_bytes()).hexdigest()
        if digest != record['sha256']:
            raise SystemExit('Unaudited target: ' + str(path))
        targets.append(str(path))
        fingerprints[mod] = digest
    baseline_file = args.baseline_source or Path(__file__).with_name('tests') / 'baseline-HRFixesMixinPlugin.java.txt'
    baseline = baseline_file.read_text('utf-8')
    report = {'baseline_commit': BASELINE, 'scope': 'production gate; real target bytes; discovery/resource test doubles; NOT a Forge launch',
              'target_sha256': fingerprints, 'runs': {}}
    for label, source in [('baseline', baseline), ('candidate', (ROOT / PLUGIN).read_text('utf-8'))]:
        with tempfile.TemporaryDirectory(prefix='hr-redirect-test-') as directory:
            work = Path(directory)
            files = []
            for name, text in {**STUBS, 'hollowrealm/performance/HRFixesMixinPlugin.java': source}.items():
                dest = work / name
                dest.parent.mkdir(parents=True, exist_ok=True)
                dest.write_text(text, 'utf-8')
                files.append(str(dest))
            subprocess.run([args.javac, '--release', '17', '-proc:none', '-cp', cp, '-d', str(work),
                            *files, str(Path(__file__).with_name('tests') / 'RedirectGateTest.java')], check=True)
            result = subprocess.run([args.java, '-cp', str(work) + os.pathsep + cp, 'RedirectGateTest', *targets],
                                    capture_output=True, text=True, encoding='utf-8', errors='replace')
            lines = [s for s in result.stdout.splitlines() if s.startswith(('PASS ', 'FAIL ', 'RESULT '))]
            report['runs'][label] = {'exit_code': result.returncode, 'checks': lines, 'summary': lines[-1] if lines else result.stderr[-2000:]}
            print(label, report['runs'][label]['summary'])
    args.report.parent.mkdir(parents=True, exist_ok=True)
    args.report.write_text(json.dumps(report, indent=2) + '\n', 'utf-8')
    assert report['runs']['baseline']['exit_code'] == 1, 'Baseline must fail the safety contract'
    assert report['runs']['candidate']['exit_code'] == 0, report['runs']['candidate']

if __name__ == '__main__':
    main()
