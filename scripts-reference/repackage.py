"""Keep original resources, three untouched DEX files and native libs byte-identical.

Windows decoding loses case-sensitive resource name pairs; never ship that output.
Only take the modified app DEX from Apktool, then patch the original ZIP directly.
"""
from pathlib import Path
import zipfile, json
root=Path(__file__).parent
with zipfile.ZipFile(root/'original.apk') as original, zipfile.ZipFile(root/'glovebox-unsigned.apk') as built, zipfile.ZipFile(root/'glovebox-preserved.apk','w') as out:
    for info in original.infolist():
        if info.filename.startswith('META-INF/') and (info.filename.endswith(('.RSA','.SF','.DSA','.EC')) or info.filename=='META-INF/MANIFEST.MF'):
            continue
        if info.filename=='classes3.dex': data=built.read(info.filename)
        elif info.filename=='res/JL.ttf': data=(root/'patch/unknown/res/JL.ttf').read_bytes()
        else: data=original.read(info.filename)
        out.writestr(info,data)
    for f in (root/'patch/assets/glovebox').iterdir():
        out.write(f,'assets/glovebox/'+f.name,compress_type=zipfile.ZIP_STORED if f.suffix=='.mp4' else zipfile.ZIP_DEFLATED)
    out.write(root/'java-dex/classes.dex','classes5.dex',compress_type=zipfile.ZIP_DEFLATED)
    for f in (root/'patch/assets/glovebox-web').rglob('*'):
        if f.is_file():out.write(f,'assets/glovebox-web/'+f.relative_to(root/'patch/assets/glovebox-web').as_posix(),compress_type=zipfile.ZIP_STORED if f.suffix=='.mp4' else zipfile.ZIP_DEFLATED)
with zipfile.ZipFile(root/'original.apk') as a, zipfile.ZipFile(root/'glovebox-preserved.apk') as b:
    an=set(a.namelist()); bn=set(b.namelist())
    changed=[n for n in sorted(an&bn) if a.getinfo(n).CRC!=b.getinfo(n).CRC]
    removed=sorted(an-bn)
    assert changed==['classes3.dex','res/JL.ttf'], changed
    assert all(n.startswith('META-INF/') for n in removed), removed
    report={'changed_entries':changed,'added':sorted(bn-an),'removed_old_signatures':removed,'original_manifest_resources_native_libraries_preserved':True}
    (root/'apk-entry-audit.json').write_text(json.dumps(report,indent=2),encoding='utf-8')
    print(json.dumps(report,indent=2))
