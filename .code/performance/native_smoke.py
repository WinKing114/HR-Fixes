"""Launch a fresh, offline Forge fixture; never copy production saves or accounts.

Requires an installed client and/or a native Forge server library tree. Each case
uses a separate directory. The read phase restarts the write phase's world.
"""
from pathlib import Path
import argparse
import hashlib
import json
import os
import shlex
import shutil
import subprocess
import sys
import time
import uuid
import zipfile

ROOT = Path(__file__).resolve().parents[2]
PROJECT = ROOT / 'quality/2026-10-10-performance'
sys.stdout.reconfigure(encoding='utf-8', errors='replace')

def sha(path):
    return hashlib.sha256(path.read_bytes()).hexdigest()

def allowed(rules):
    result = not rules
    features = {'has_custom_resolution': True, 'has_quick_plays_support': True, 'is_quick_play_singleplayer': True}
    for rule in rules or []:
        system = rule.get('os', {})
        match = system.get('name', 'windows') == 'windows' and system.get('arch', 'amd64') in ('amd64', 'x86_64')
        match = match and all(features.get(k, False) == v for k,v in rule.get('features', {}).items())
        if match:
            result = rule['action'] == 'allow'
    return result

def main():
    p = argparse.ArgumentParser(description=__doc__)
    p.add_argument('--libraries', type=Path, default=Path('C:/Users/WinKing/Documents/.minecraft/libraries'))
    p.add_argument('--installed-client', type=Path, default=Path('C:/Users/WinKing/Documents/.minecraft/versions/Hollow Realm'))
    p.add_argument('--server-libraries', type=Path)
    p.add_argument('--world-template', type=Path, help='level.dat from a fresh disposable vanilla QA world, never a production world')
    p.add_argument('--coo', type=Path)
    p.add_argument('--structurify', type=Path)
    p.add_argument('--work-root', type=Path, default=ROOT / '.runtime/native-112')
    p.add_argument('--jar', type=Path, default=PROJECT / 'release/hr-fixes-1.1.2-rc.1.jar')
    p.add_argument('--side', choices=['client','server'], required=True)
    p.add_argument('--case', choices=['none','goety','awaken','coo','all','drift-missing','drift-duplicate','unknown'], default='none')
    p.add_argument('--forge', choices=['47.4.16','47.4.22'], required=True)
    p.add_argument('--phase', choices=['write','read'], default='write')
    p.add_argument('--reuse', action='store_true', help='Retry a prepared fixture with an unchanged mod inventory')
    p.add_argument('--java', default='C:/Program Files/Zulu/zulu-21/bin/java.exe')
    p.add_argument('--javac', default='C:/Program Files/Zulu/zulu-17/bin/javac.exe')
    p.add_argument('--timeout', type=int, default=240)
    args = p.parse_args()
    args.work_root = args.work_root.resolve()
    test = args.work_root / f'{args.side}-{args.forge}-{args.case}'
    if args.phase == 'write' and not args.reuse:
        test.mkdir(parents=True, exist_ok=False)
        (test / 'mods').mkdir()
        candidate = args.jar.resolve()
        shutil.copy2(candidate, test / 'mods')
        compile_libs = [args.libraries / n for n in [
            'net/minecraft/client/1.20.1-20230612.114412/client-1.20.1-20230612.114412-srg.jar',
            'net/minecraftforge/forge/1.20.1-47.4.16/forge-1.20.1-47.4.16-client.jar',
            'net/minecraftforge/forge/1.20.1-47.4.16/forge-1.20.1-47.4.16-universal.jar',
            'net/minecraftforge/javafmllanguage/1.20.1-47.4.16/javafmllanguage-1.20.1-47.4.16.jar',
            'net/minecraftforge/fmlloader/1.20.1-47.4.16/fmlloader-1.20.1-47.4.16.jar',
            'net/minecraftforge/eventbus/6.2.33/eventbus-6.2.33.jar',
            'net/minecraftforge/mergetool/1.1.5/mergetool-1.1.5-api.jar',
            'com/mojang/datafixerupper/6.0.8/datafixerupper-6.0.8.jar',
            'com/mojang/brigadier/1.1.8/brigadier-1.1.8.jar',
            'com/google/guava/guava/31.1-jre/guava-31.1-jre.jar']]
        classes = test / 'probe-classes'; classes.mkdir()
        subprocess.run([args.javac, '--release', '17', '-proc:none', '-cp', os.pathsep.join(map(str, [candidate, *compile_libs])),
            '-d', str(classes), *map(str, Path(__file__).with_name('tests').glob('Native*.java'))], check=True)
        with zipfile.ZipFile(test / 'mods/hr-native-qa.jar', 'w', zipfile.ZIP_DEFLATED) as z:
            for f in classes.rglob('*.class'):
                z.writestr(f.relative_to(classes).as_posix(), f.read_bytes())
            z.writestr('META-INF/mods.toml', 'modLoader="javafml"\nloaderVersion="[47,)"\nlicense="MIT"\n[[mods]]\nmodId="hr_native_qa"\nversion="1"\ndisplayName="Disposable HR native QA"\n')
        patterns = []
        if args.case in ('goety','awaken','all'):
            patterns = ['*goety-2.5.58.4.jar','*goetyawaken-1.3.9.4.jar','goetyominous-3.0.0.fix-1.20.1.jar',
                        'curios-forge-5.14.1+1.20.1.jar','geckolib-forge-1.20.1-4.8.4.jar']
            if args.case == 'awaken':
                patterns.remove('goetyominous-3.0.0.fix-1.20.1.jar')
        if args.case == 'all':
            patterns += ['EternalStarlight-0.9.0-hotfix.jar','*ftb-quests-forge-2001.4.22.jar','ftb-library-forge-2001.2.13.jar',
                'quest_enhance-1.20.1-forge-3.13.jar','architectury-9.2.14-forge.jar','*ftb-teams-forge-2001.3.2.jar',
                '*Apotheosis-1.20.1-7.4.8.jar','Placebo-1.20.1-8.6.3.jar','ApothicAttributes-1.20.1-1.3.7.jar',
                '*jei-1.20.1-forge-15.56.0.205.jar']
        for pattern in patterns:
            found = list((args.installed_client / 'mods').glob(pattern))
            if len(found) != 1:
                raise SystemExit('Need exactly one native fixture dependency: ' + pattern)
            shutil.copy2(found[0], test / 'mods')
        if args.case in ('drift-missing','drift-duplicate','unknown'):
            fixture = test / 'fixture-classes'; fixture.mkdir()
            source = test / 'SorcererServant.java'
            # Entirely self-authored fixture: no third-party bytecode/JAR is changed.
            body = '' if args.case != 'drift-duplicate' else 'Object a = level.f_46441_; Object b = level.f_46441_;'
            source.write_text('package com.k1sak1.goetyawaken.common.entities.ally.illager;\n'
                '@net.minecraftforge.fml.common.Mod("goetyawaken")\n'
                'public class SorcererServant { private net.minecraft.world.level.Level level;\n'
                'public void generateOffers() { ' + body + ' } }\n','utf-8')
            subprocess.run([args.javac,'--release','17','-proc:none','-cp',os.pathsep.join(map(str,compile_libs)),
                            '-d',str(fixture),str(source)],check=True)
            version = '999.0' if args.case == 'unknown' else '1.3.9.4'
            with zipfile.ZipFile(test / 'mods/self-authored-target-fixture.jar','w',zipfile.ZIP_DEFLATED) as z:
                for f in fixture.rglob('*.class'):
                    z.writestr(f.relative_to(fixture).as_posix(),f.read_bytes())
                z.writestr('META-INF/mods.toml','modLoader="javafml"\nloaderVersion="[47,)"\nlicense="MIT"\n'
                    '[[mods]]\nmodId="goetyawaken"\nversion="' + version + '"\ndisplayName="Self-authored bytecode drift fixture (NOT Goety)"\n')
        if args.case == 'coo':
            if not args.coo or not args.structurify:
                raise SystemExit('CoO fixture requires explicit audited --coo and --structurify paths')
            for f in [args.coo, args.structurify]:
                shutil.copy2(f, test / 'mods')
            if args.side == 'client':
                f = next((args.installed_client / 'mods').glob('yet_another_config_lib_v3-*.jar'))
                shutil.copy2(f, test / 'mods')
        if args.side == 'client':
            if not args.world_template:
                raise SystemExit('Client requires a fresh QA --world-template level.dat')
            world = test / 'saves/hr112_qa'; world.mkdir(parents=True)
            shutil.copy2(args.world_template, world / 'level.dat')
            shutil.copytree(args.installed_client / 'Hollow Realm-natives', test / 'natives')
            (test / 'options.txt').write_text('pauseOnLostFocus:false\nrenderDistance:2\nsimulationDistance:5\nmaxFps:30\nfullscreen:false\nonboardAccessibility:false\n', 'utf-8')
        else:
            (test / 'eula.txt').write_text('eula=true\n', 'utf-8')
            (test / 'server.properties').write_text('server-ip=127.0.0.1\nserver-port=0\nonline-mode=false\nlevel-type=minecraft:flat\ngenerate-structures=false\nview-distance=2\nsimulation-distance=5\n', 'utf-8')
        (test / 'inventory.json').write_text(json.dumps({f.name:sha(f) for f in (test / 'mods').glob('*.jar')},indent=2), 'utf-8')
    else:
        assert test.is_dir() and (test / 'inventory.json').is_file(), 'Read phase must restart a prepared fixture'
        assert json.loads((test / 'inventory.json').read_text('utf-8')) == {f.name:sha(f) for f in (test / 'mods').glob('*.jar')}
    jvm = ['-Xmx3G', '-Dfile.encoding=UTF-8', '-Dmixin.debug.export=true', '-Dhrqa.case=' + args.case, '-Dhrqa.phase=' + args.phase]
    if args.side == 'server':
        if not args.server_libraries:
            raise SystemExit('Native server requires --server-libraries')
        server_libs = args.server_libraries.resolve()
        argfile = server_libs / f'net/minecraftforge/forge/1.20.1-{args.forge}/win_args.txt'
        tokens = shlex.split(argfile.read_text('utf-8'))
        tokens = [t.replace('libraries/', server_libs.as_posix() + '/') for t in tokens]
        tokens = ['-DlibraryDirectory=' + str(server_libs) if t.startswith('-DlibraryDirectory=') else t for t in tokens]
        launch = jvm + tokens + ['nogui']
    else:
        # Read only public version metadata; no launcher-account files are opened.
        meta = json.loads((args.installed_client / 'Hollow Realm.json').read_text('utf-8'))
        cp = []
        for lib in meta['libraries']:
            if not allowed(lib.get('rules')):
                continue
            path = lib.get('downloads', {}).get('artifact', {}).get('path')
            if not path:
                group, artifact, version, *classifier = lib['name'].split(':')
                suffix = '-' + classifier[0] if classifier else ''
                path = f'{group.replace(".","/")}/{artifact}/{version}/{artifact}-{version}{suffix}.jar'
            path = path.replace('1.20.1-47.4.22', '1.20.1-' + args.forge)
            f = args.libraries / path
            if not f.is_file():
                raise SystemExit('Missing native client library: ' + str(f))
            cp.append(str(f))
        cp.append(str(args.installed_client / 'Hollow Realm.jar'))
        values = {'auth_player_name':'HR112QA','version_name':'Hollow Realm','game_directory':str(test),
            'assets_root':str(args.installed_client.parents[1] / 'assets'), 'assets_index_name':meta['assetIndex']['id'],
            'auth_uuid':uuid.uuid3(uuid.NAMESPACE_DNS,'OfflinePlayer:HR112QA').hex,'auth_access_token':'0','clientid':'0','auth_xuid':'0',
            'user_type':'legacy','version_type':'HR112-QA','resolution_width':'854','resolution_height':'480',
            'quickPlayPath':'quickPlay/qa.json','quickPlaySingleplayer':'hr112_qa','natives_directory':str(test / 'natives'),
            'launcher_name':'HR112QA','launcher_version':'1','classpath':os.pathsep.join(cp),
            'library_directory':str(args.libraries),'classpath_separator':os.pathsep}
        def expand(entries):
            result = []
            for entry in entries:
                if isinstance(entry, dict):
                    if not allowed(entry.get('rules')):
                        continue
                    entry = entry['value']
                for value in entry if isinstance(entry, list) else [entry]:
                    for k,v in values.items():
                        value = value.replace('${' + k + '}',v)
                    if '${' in value:
                        raise SystemExit('Unexpanded native launch argument: ' + value)
                    result.append(value)
            return result
        game = expand(meta['arguments']['game'])
        game[game.index('--fml.forgeVersion') + 1] = args.forge
        launch = jvm + expand(meta['arguments']['jvm']) + [meta['mainClass']] + game
    launch_file = test / ('launch-' + args.phase + '.args')
    launch_file.write_text('\n'.join(json.dumps(s,ensure_ascii=False) for s in launch), 'mbcs')
    log = test / ('launcher-' + args.phase + '.log')
    with log.open('wb') as output:
        process = subprocess.Popen([args.java, '@' + str(launch_file)], cwd=test, stdout=output, stderr=subprocess.STDOUT,
                                   creationflags=subprocess.CREATE_NO_WINDOW)
    print(json.dumps({'pid':process.pid, 'fixture':str(test), 'phase':args.phase}), flush=True)
    expired = False
    try:
        process.wait(timeout=args.timeout)
    except subprocess.TimeoutExpired:
        expired = True
        process.terminate()
        process.wait(timeout=30)
    raw = log.read_text('utf-8',errors='replace')
    markers = [s for s in raw.splitlines() if '[HR112]' in s or 'HR Fixes' in s]
    saved = 'All dimensions are saved' in raw
    success = process.returncode == 0 and not expired and '[HR112] COMPLETE ' + args.phase in raw and '[HR112] FAIL' not in raw and saved
    if args.case == 'all':
        success = success and '[HR112] CLIENT_REGRESSION_COMPLETE' in raw
    report = {'side':args.side,'forge':args.forge,'java':'21','case':args.case,'phase':args.phase,
              'status':'PASS' if success else 'FAIL','exit_code':process.returncode,'timeout':expired,'normal_world_save':saved,
              'markers':markers,'log_sha256':sha(log),'inventory':json.loads((test / 'inventory.json').read_text('utf-8'))}
    (test / ('result-' + args.phase + '.json')).write_text(json.dumps(report,indent=2) + '\n','utf-8')
    print(json.dumps(report,indent=2),flush=True)
    if not success:
        print(raw[-7000:], flush=True)
        raise SystemExit(1)

if __name__ == '__main__':
    main()
