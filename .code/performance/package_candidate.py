"""Package the candidate's own sources; independently rebuild and compare bytes.

No dependencies, companion modpack overrides, accounts, saves or raw logs ship.
The existing Python/javac builder remains the authoritative build procedure.
"""
from pathlib import Path
import argparse
import hashlib
import json
import subprocess
import sys
import tempfile
import zipfile

ROOT = Path(__file__).resolve().parents[2]
PROJECT = ROOT / 'quality/2026-10-10-performance'

def sha(p):
    return hashlib.sha256(p.read_bytes()).hexdigest()

def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--libraries', type=Path, default=Path('C:/Users/WinKing/Documents/.minecraft/libraries'))
    parser.add_argument('--mods', type=Path, default=Path('C:/Users/WinKing/Documents/.minecraft/versions/Hollow Realm/mods'))
    parser.add_argument('--eternal-starlight', type=Path, default=Path('C:/Users/WinKing/Documents/.minecraft/versions/Hollow Realm/mods/EternalStarlight-0.9.0-hotfix.jar'))
    parser.add_argument('--javac', default='C:/Program Files/Zulu/zulu-17/bin/javac.exe')
    parser.add_argument('--java', default='C:/Program Files/Zulu/zulu-21/bin/java.exe')
    args = parser.parse_args()
    version = json.loads((PROJECT / 'project.json').read_text('utf-8'))['version']
    jar = PROJECT / 'release' / f'hr-fixes-{version}.jar'
    files = [ROOT / 'README.md', ROOT / 'LICENSE', ROOT / '.gitattributes']
    files += [Path(__file__).with_name(n) for n in ['build_patch.py','test_redirect_gate.py','native_smoke.py','package_candidate.py']]
    files += [f for f in Path(__file__).with_name('tests').iterdir() if f.is_file() and f.suffix in ('.java','.txt')]
    files += [PROJECT / n for n in ['project.json','LICENSE','BUILD.md','CHANGELOG.md',f'RELEASE-{version}.md']]
    for folder in ['src','resources','evidence/1.1.2-rc.1']:
        files += [f for f in (PROJECT / folder).rglob('*') if f.is_file()]
    files += [PROJECT / 'evidence/redirect-gate-1.1.2-rc.1.json']
    dest = jar.with_name(f'hr-fixes-{version}-sources.zip')
    with zipfile.ZipFile(dest,'w',zipfile.ZIP_DEFLATED) as z:
        for f in sorted(files):
            info = zipfile.ZipInfo(f.relative_to(ROOT).as_posix(), (2026,10,17,0,0,0))
            info.compress_type = zipfile.ZIP_DEFLATED
            info.external_attr = 0o644 << 16
            z.writestr(info,f.read_bytes())
    with zipfile.ZipFile(jar) as z:
        classes = [z.read(n) for n in z.namelist() if n.endswith('.class')]
        assert classes and all(int.from_bytes(b[6:8],'big') == 61 for b in classes), 'Java 17 bytecode required'
        assert f'version="{version}"'.encode() in z.read('META-INF/mods.toml')
        assert not any(n.startswith(('net/minecraft/','net/minecraftforge/','hollowrealm/qa/')) for n in z.namelist())
    with tempfile.TemporaryDirectory(prefix='hr-independent-') as directory:
        source = Path(directory) / 'source'
        with zipfile.ZipFile(dest) as z:
            z.extractall(source)
        output = Path(directory) / 'output'
        subprocess.run([sys.executable,str(source / '.code/performance/build_patch.py'),
            '--libraries',str(args.libraries),'--mods',str(args.mods),'--eternal-starlight',str(args.eternal_starlight),
            '--javac',args.javac,'--output-dir',str(output)],check=True)
        assert jar.read_bytes() == (output / jar.name).read_bytes(), 'Independent rebuild differs'
        subprocess.run([sys.executable,str(source / '.code/performance/test_redirect_gate.py'),
            '--libraries',str(args.libraries),'--mods',str(args.mods),'--javac',args.javac,
            '--java',args.java,
            '--report',str(Path(directory) / 'independent-tests.json')],check=True)
    report = {'version':version,'status':'NOT_READY','jar_sha256':sha(jar),'sources_sha256':sha(dest),
              'java_class_major':61,'class_count':len(classes),'independent_rebuild':'PASS',
              'independent_gate_tests':'PASS','published':False}
    (jar.parent / f'provenance-{version}.json').write_text(json.dumps(report,indent=2) + '\n','utf-8')
    print(json.dumps(report,indent=2))

if __name__ == '__main__':
    main()
