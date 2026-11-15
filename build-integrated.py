#!/usr/bin/env python3
"""Build the Glovebox handoff with Android build-tools, a JDK and Apktool."""
import argparse
import hashlib
import json
import os
from pathlib import Path
import shutil
import struct as st
import subprocess
import tempfile
from zipfile import ZipFile, ZIP_DEFLATED
ROOT=Path(__file__).resolve().parent

replacement={'0.31.11':'0.31.12','io.memorydrive.pleos':'io.memorydrive.roads','io.memorydrive.pleos.mlkitinitprovider':'io.memorydrive.roads.mlkitinitprovider','io.memorydrive.pleos.androidx-startup':'io.memorydrive.roads.androidx-startup','io.memorydrive.pleos.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION':'io.memorydrive.roads.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION','Memory Drive':'Glovebox Map'}
def rename(binary):
 b=bytearray(binary);u=lambda offset:st.unpack_from('<I',b,offset)[0]
 offset=8;assert st.unpack_from('<H',b,offset)[0]==1
 hs=st.unpack_from('<H',b,offset+2)[0];count=u(offset+8);utf8=bool(u(offset+16)&256);start=offset+u(offset+20);seen=set()
 for i in range(count):
  off=start+u(offset+hs+i*4)
  if utf8:
   off+=2 if b[off]&128 else 1
   size=b[off];off+=1
   if size&128:size=((size&127)<<8)|b[off];off+=1
   enc='utf-8'
  else:
   size=st.unpack_from('<H',b,off)[0];assert not size&0x8000;off+=2;size*=2;enc='utf-16-le'
  original=b[off:off+size].decode(enc)
  if original in replacement:
   new=replacement[original].encode(enc);assert len(new)==size; b[off:off+size]=new;seen.add(original)
 assert seen==set(replacement),seen
 return b
import sys
sys.dont_write_bytecode=True
from integration.media_manifest import media_manifest

def build(args, tmp):
    base=args.base.resolve()
    expected=json.loads((ROOT/'manifest.json').read_text())['apk_sha256']
    if hashlib.sha256(base.read_bytes()).hexdigest()!=expected:
        raise ValueError('The source handoff requires the matching Glovebox-Pleos.apk')
    java_home=os.environ.get('JAVA_HOME')
    def tool(name):
        return str(Path(java_home)/'bin'/name) if java_home else name
    def run(*parts):
        subprocess.run([str(p) for p in parts],check=True)
    classes=tmp/'classes';classes.mkdir()
    dex=tmp/'dex';dex.mkdir()
    run(tool('javac'),'-encoding','UTF-8','-source','8','-target','8','-classpath',args.android_jar,'-d',classes,*sorted((ROOT/'native-java').rglob('*.java')))
    run(tool('jar'),'cf',tmp/'design.jar','-C',classes,'.')
    run(args.tools/'d8','--min-api','29','--lib',args.android_jar,'--output',dex,tmp/'design.jar')
    archive=tmp/'third.zip'
    with ZipFile(base) as original, ZipFile(archive,'w') as only:
        only.writestr('classes.dex',original.read('classes3.dex'))
    decoded=tmp/'decoded'
    run(tool('java'),'-jar',args.apktool,'d','-f',archive,'-o',decoded)
    shutil.copytree(ROOT/'integration/smali',decoded/'smali',dirs_exist_ok=True)
    # Feed the selected phone into the original trip recorder and its existing recording preferences.
    phone_class=decoded/'smali/io/memorydrive/pleos/ConnectedPhoneMedia.smali'
    phone_source=phone_class.read_text()
    snapshot=phone_source.index('.method public final snapshot()Lio/memorydrive/pleos/PhonePlayback;')
    hook=phone_source.index('    .line 149',snapshot)
    phone_class.write_text(phone_source[:hook]+"""    invoke-static {}, Lio/memorydrive/pleos/GloveboxScreen;->iphoneJournalPlayback()Ljava/lang/Object;
    move-result-object v1
    if-eqz v1, :vehicle_media_session
    check-cast v1, Lio/memorydrive/pleos/PhonePlayback;
    return-object v1

    :vehicle_media_session
"""+phone_source[hook:])
    # The original scorer preserves personal recordings. Bundled photo albums may also
    # draw from the active listener's library, including when a public opening song exists.
    scorer=decoded/'smali/io/memorydrive/pleos/ContextualMusicIndex.smali'
    source=scorer.read_text()
    guard='    if-nez v4, :cond_17\n\n    iget-object v4, v0, Lio/memorydrive/pleos/ContextualMusicIndex;->candidates'
    assert source.count(guard)==1
    scorer.write_text(source.replace(guard,"""    if-eqz v4, :rank_visit_context
    invoke-static {v1}, Lio/memorydrive/pleos/GloveboxSamples;->isBundledVisit(Ljava/lang/Object;)Z
    move-result v4
    if-eqz v4, :cond_17

    :rank_visit_context
    iget-object v4, v0, Lio/memorydrive/pleos/ContextualMusicIndex;->candidates"""))
    run(tool('java'),'-jar',args.apktool,'b',decoded,'-o',tmp/'third-built.zip')
    with ZipFile(tmp/'third-built.zip') as patch:
        replacements={'classes3.dex':patch.read('classes.dex'),'classes5.dex':(dex/'classes.dex').read_bytes(),'res/ry.xml':(ROOT/'integration/launcher.bin').read_bytes()}
    assets={}
    for folder,prefix in [('web-runtime','assets/glovebox-web/'),('native-assets','assets/glovebox/')]:
        directory=ROOT/folder
        for path in directory.rglob('*'):
            if path.is_file() and not any(part.startswith('.') for part in path.relative_to(directory).parts):
                assets[prefix+path.relative_to(directory).as_posix()]=path
    with ZipFile(base) as orig, ZipFile(tmp/'unsigned.apk','w') as out:
        for item in orig.infolist():
            name=item.filename
            if name.startswith('META-INF/') and (name.upper().endswith(('.RSA','.SF','.DSA','.EC')) or name=='META-INF/MANIFEST.MF'):
                continue
            if name in replacements:data=replacements[name]
            elif name=='AndroidManifest.xml':data=media_manifest(rename(orig.read(item)))
            elif name in assets:data=assets.pop(name).read_bytes()
            else:data=orig.read(item)
            out.writestr(item,data)
        for name,path in sorted(assets.items()):
            out.write(path,name,compress_type=ZIP_DEFLATED)
    args.out.parent.mkdir(parents=True,exist_ok=True)
    run(args.tools/'zipalign','-f','-P','16','4',tmp/'unsigned.apk',tmp/'aligned.apk')
    if args.keystore:
        signing=['--ks',args.keystore,'--ks-key-alias',args.alias]
        if args.password_file:signing+=['--ks-pass','file:'+str(args.password_file)]
        run(args.tools/'apksigner','sign',*signing,'--v4-signing-enabled','false','--out',args.out,tmp/'aligned.apk')
        run(args.tools/'apksigner','verify',args.out)
    else:shutil.copyfile(tmp/'aligned.apk',args.out)

if __name__=='__main__':
    parser=argparse.ArgumentParser(description=__doc__)
    for arg in ['base','android-jar','tools','apktool','out']:
        parser.add_argument('--'+arg,type=Path,required=True)
    parser.add_argument('--keystore',type=Path)
    parser.add_argument('--alias')
    parser.add_argument('--password-file',type=Path)
    args=parser.parse_args()
    if args.keystore and not args.alias:parser.error('--keystore requires --alias')
    with tempfile.TemporaryDirectory() as directory:build(args,Path(directory))
