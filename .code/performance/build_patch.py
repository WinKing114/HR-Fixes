"""Build the existing HR Fixes project from local, unmodified dependencies."""
from pathlib import Path
import argparse
import hashlib
import json
import subprocess
import tempfile
import zipfile

ROOT = Path(__file__).resolve().parents[2]
PROJECT = ROOT / 'quality/2026-10-10-performance'

def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--libraries', type=Path, default=Path('C:/Users/WinKing/Documents/.minecraft/libraries'))
    parser.add_argument('--eternal-starlight', type=Path, default=Path('C:/Users/WinKing/Documents/.minecraft/versions/Hollow Realm/mods/EternalStarlight-0.9.0-hotfix.jar'))
    parser.add_argument('--mods', type=Path, default=Path('C:/Users/WinKing/Documents/.minecraft/versions/Hollow Realm/mods'))
    parser.add_argument('--javac', default='C:/Program Files/Zulu/zulu-17/bin/javac.exe')
    parser.add_argument('--output-dir', type=Path, default=PROJECT / 'release')
    args = parser.parse_args()
    meta = json.loads((PROJECT / 'project.json').read_text('utf-8'))
    target_hash = hashlib.sha256(args.eternal_starlight.read_bytes()).hexdigest()
    if target_hash != meta['target_sha256']:
        raise SystemExit('Unsupported Eternal Starlight binary; audit the new build before changing the boundary.')
    relative_libs = [
        'net/minecraft/client/1.20.1-20230612.114412/client-1.20.1-20230612.114412-srg.jar',
        'net/minecraftforge/forge/1.20.1-47.4.16/forge-1.20.1-47.4.16-client.jar',
        'net/minecraftforge/javafmllanguage/1.20.1-47.4.16/javafmllanguage-1.20.1-47.4.16.jar',
        'net/minecraftforge/fmlloader/1.20.1-47.4.16/fmlloader-1.20.1-47.4.16.jar',
        'org/ow2/asm/asm-tree/9.5/asm-tree-9.5.jar',
        'org/spongepowered/mixin/0.8.5/mixin-0.8.5.jar',
        'net/minecraftforge/forge/1.20.1-47.4.16/forge-1.20.1-47.4.16-universal.jar',
        'com/mojang/datafixerupper/6.0.8/datafixerupper-6.0.8.jar',
        'com/google/guava/guava/31.1-jre/guava-31.1-jre.jar',
        'net/minecraftforge/mergetool/1.1.5/mergetool-1.1.5-api.jar',
    ]
    libs = [args.libraries / p for p in relative_libs] + [args.eternal_starlight]
    for pattern in ('*ftb-quests-forge-2001.4.22.jar', '*ftb-library-forge-2001.2.13.jar',
                    '*Apotheosis-1.20.1-7.4.8.jar', '*Placebo-1.20.1-8.6.3.jar', '*jei-1.20.1-forge-15.56.0.205.jar'):
        matches = list(args.mods.glob(pattern))
        if len(matches) != 1:
            raise SystemExit('Expected one audited compile dependency: ' + pattern)
        libs.extend(matches)
    missing = [str(p) for p in libs if not p.is_file()]
    if missing:
        raise SystemExit('Missing local compile dependencies: ' + ', '.join(missing))
    audited = {item['file']: item['sha256'] for item in meta['tested_optional_mods'].values()}
    for dependency in libs:
        expected = audited.get(dependency.name)
        if expected and hashlib.sha256(dependency.read_bytes()).hexdigest() != expected:
            raise SystemExit('Unsupported compile dependency: ' + dependency.name)
    build_root = ROOT / '.runtime/performance-20261010/build'
    build_root.mkdir(parents=True, exist_ok=True)
    sources = sorted((PROJECT / 'src').rglob('*.java'))
    with tempfile.TemporaryDirectory(prefix='hr-fixes-', dir=build_root) as directory:
        build = Path(directory)
        subprocess.run([args.javac, '--release', '17', '-proc:none', '-cp', ';'.join(map(str, libs)),
                        '-d', str(build), *map(str, sources)], check=True)
        resources = {p.relative_to(PROJECT / 'resources').as_posix(): p.read_bytes()
                     for p in (PROJECT / 'resources').rglob('*') if p.is_file()}
        resources['META-INF/mods.toml'] = resources['META-INF/mods.toml'].replace(b'@VERSION@', meta['version'].encode())
        resources['LICENSE'] = (PROJECT / 'LICENSE').read_bytes()
        resources.update({p.relative_to(build).as_posix(): p.read_bytes() for p in build.rglob('*.class')})
        args.output_dir.mkdir(parents=True, exist_ok=True)
        jar = args.output_dir / f"hr-fixes-{meta['version']}.jar"
        with zipfile.ZipFile(jar, 'w', zipfile.ZIP_DEFLATED) as z:
            for name, data in sorted(resources.items()):
                info = zipfile.ZipInfo(name, (2026, 10, 10, 0, 0, 0))
                info.compress_type = zipfile.ZIP_DEFLATED
                info.external_attr = 0o644 << 16
                z.writestr(info, data)
    print(json.dumps({'jar': str(jar), 'sha256': hashlib.sha256(jar.read_bytes()).hexdigest(),
                      'eternal_starlight_sha256': target_hash}))

if __name__ == '__main__':
    main()
