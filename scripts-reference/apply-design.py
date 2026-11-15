from pathlib import Path
import shutil, json, ast
from fontTools.ttLib import TTFont
from playwright.sync_api import sync_playwright

root=Path(__file__).parent
web=root.parent/'design/glovebox'
patch=root/'patch'
code=patch/'smali_classes3/io/memorydrive/pleos'
original=root/'decoded/smali_classes3/io/memorydrive/pleos'
for baseline in original.glob('*.smali'):
    shutil.copy2(baseline,code/baseline.name)
assets=patch/'assets/glovebox'
assets.mkdir(parents=True,exist_ok=True)
for number in range(1,33):
    shutil.copy2(web/f'assets/display/photo-{number:02d}.webp',assets/f'photo-{number:02d}.webp')
webjs=(web/'glovebox.js').read_text(encoding='utf-8')
rows=ast.literal_eval(webjs.split('const memories=',1)[1].split('].map',1)[0]+']')
samples=[dict(title=r[0],place=r[1],season=r[2],song=r[3],photos=r[4:],description='목적지에 도착하기 전, 마음에 남은 풍경. 잠시 멈춘 그 길에서 그날의 시간을 다시 만납니다.') for r in rows]
(assets/'samples.json').write_text(json.dumps(samples,ensure_ascii=False),encoding='utf-8')
shutil.copy2(web/'assets/display/photo-17.webp',assets/'landscape.webp')
shutil.copy2(root/'jumunjin-dawn-playback.mp4' if (root/'jumunjin-dawn-playback.mp4').exists() else web/'assets/display/jumunjin-dawn.mp4',assets/'jumunjin-dawn.mp4')
# Keep the compiled resource table and every original resource ID intact.
font=TTFont(web/'assets/fonts/PretendardVariable.woff2'); font.flavor=None
font.save(patch/'unknown/res/JL.ttf')
font=TTFont(web/'assets/fonts/NanumAReumDeuRiGgocNaMu.woff'); font.flavor=None
font.save(assets/'flower-tree.ttf')
for name in ['Pretendard-LICENSE.txt','FlowerTree-SOURCE.txt','Allura-OFL.txt']:
    shutil.copy2(web/'assets/fonts'/name,assets/name)
with sync_playwright() as p:
    browser=p.chromium.launch(headless=True)
    page=browser.new_page(viewport={'width':950,'height':260},device_scale_factor=2)
    svg=(web/'assets/logo-allura.svg').read_text(encoding='utf-8')
    page.set_content('<style>body{margin:0;background:transparent}svg{width:950px;height:260px}</style>'+svg)
    page.screenshot(path=str(assets/'logo.png'),omit_background=True)
    browser.close()

s=(original/'MemoryScreenKt.smali').read_text(encoding='utf-8')
palette={'ffdfe9e6':'ffe7b978','ff17232a':'ff292522','ff0a1015':'ff141210','fff2f3ef':'fffaf7f0','ffabb7be':'ffc9bcae','ff364151':'ff6b5846','ff34434b':'ff57432f','ffb6c8db':'fff0c995','ff212d3d':'ff3a3028'}
for a,b in palette.items(): s=s.replace('0x'+a+'L','0x'+b+'L')
s=s.replace('0xff292522L','0xa6292522L').replace('0xff3a3028L','0x993a3028L')
s=s.replace('0xff141210L','0x00141210L')
# Only headlineLarge uses the handwritten face. Other text retains Pretendard.
needle='    move-object/from16 v69, v37\n'
assert s.count(needle)==1
s=s.replace(needle,needle+'\n    invoke-static {v9}, Lio/memorydrive/pleos/GloveboxDesign;->heading(Landroidx/compose/runtime/Composer;)Landroidx/compose/ui/text/font/FontFamily;\n\n    move-result-object v37\n')
# Capture the existing engine, never construct an alternative data store.
pos=s.index('.method public static final MemoryScreen(')
insertion=s.index('\n',s.index('    .locals ',pos))+1
s=s[:insertion]+'\n    invoke-static/range {p0 .. p0}, Lio/memorydrive/pleos/GloveboxMotion;->engine(Ljava/lang/Object;)V\n'+s[insertion:]
# Only the existing coast demo gets a native video/photo view; real memories keep their renderer.
pos=s.index('.method public static final MemoryPhoto(')
insertion=s.index('\n',s.index('    .locals ',pos))+1
s=s[:insertion]+'''
    move-object/from16 v0, p0
    move/from16 v1, p1
    move-object/from16 v2, p2
    move-object/from16 v3, p7
    invoke-static {v0, v1, v2, v3}, Lio/memorydrive/pleos/GloveboxVideo;->render(Ljava/lang/Object;ZLjava/lang/Object;Ljava/lang/Object;)Z
    move-result v0
    if-eqz v0, :glovebox_original_photo
    return-void
    :glovebox_original_photo
'''+s[insertion:]
# Background and sound dock decorators preserve the original content callbacks.
for method,mode in [('MemoryScreen',1),('JourneyDock',2)]:
    import re
    pat=re.compile(r'(\.method [^\n]* '+method+r'\([^\n]*\n)(.*?)(\.end method)',re.S)
    m=pat.search(s); assert m
    body=m.group(2)
    target='    invoke-static/range {v1 .. v13}, Landroidx/compose/material3/SurfaceKt;->Surface-T9BRK9s('
    assert body.count(target)==1
    body=body.replace(target,f'''    const/4 v2, {mode}
    invoke-static {{v1, v2}}, Lio/memorydrive/pleos/GloveboxMotion;->modifier(Ljava/lang/Object;I)Ljava/lang/Object;
    move-result-object v1
    check-cast v1, Landroidx/compose/ui/Modifier;
    const/4 v2, 0x0

'''+target)
    if mode==2:
        body=body.replace('const/16 v13, 0x7b','const/16 v13, 0x7a').replace('0x3f5c28f6','0x3f266666')
    s=s[:m.start()]+m.group(1)+body+m.group(3)+s[m.end():]
(code/'MemoryScreenKt.smali').write_text(s,encoding='utf-8')
main=(original/'MainActivity.smali').read_text(encoding='utf-8')
needle='    invoke-super {p0, p1}, Landroidx/activity/ComponentActivity;->onCreate(Landroid/os/Bundle;)V'
assert main.count(needle)==1
main=main.replace(needle,needle+'\n\n    invoke-static/range {p0 .. p0}, Lio/memorydrive/pleos/GloveboxMotion;->install(Landroid/app/Activity;)V')
assert '.method public onUserInteraction()V' not in main
main+='''
.method public onUserInteraction()V
    .locals 0
    invoke-super {p0}, Landroidx/activity/ComponentActivity;->onUserInteraction()V
    invoke-static {}, Lio/memorydrive/pleos/GloveboxMotion;->interact()V
    return-void
.end method
'''
(code/'MainActivity.smali').write_text(main,encoding='utf-8')
memories=(original/'MemoryKt.smali').read_text(encoding='utf-8')
start=memories.index('.method public static final getDemoMemories()')
end=memories.index('.end method',start)
section=memories[start:end]
section=section.replace('    return-object v0','    invoke-static {v0}, Lio/memorydrive/pleos/GloveboxSamples;->list(Ljava/util/List;)Ljava/util/List;\n    move-result-object v0\n    return-object v0')
memories=memories[:start]+section+memories[end:]
(code/'MemoryKt.smali').write_text(memories,encoding='utf-8')
file='MemoryScreenKt$MemoryScreen$13$1.smali'
s=(original/file).read_text(encoding='utf-8')
start=s.index('    const-string v12, "Memory Drive"')
call=s.index('    invoke-static/range {v1 .. v25}, Landroidx/compose/material3/TextKt;',start)
end=s.index('\n',call)
s=s[:call]+'''    move-object/from16 v3, v22
    invoke-static {v2, v3}, Lio/memorydrive/pleos/GloveboxDesign;->brand(Landroidx/compose/ui/Modifier;Landroidx/compose/runtime/Composer;)V'''+s[end:]
(code/file).write_text(s,encoding='utf-8')
shutil.copy2(root/'GloveboxDesign.smali',code/'GloveboxDesign.smali')
atmos=(original/'MemoryAtmosphereKt.smali').read_text(encoding='utf-8')
atmos=atmos.replace('0xff34576aL','0xff735139L').replace('0xff4b4263L','0xff5e4938L')
(code/'MemoryAtmosphereKt.smali').write_text(atmos,encoding='utf-8')
for f in code.glob('*.smali'):
    if f.name=='GloveboxDesign.smali': continue
    source=f.read_text(encoding='utf-8')
    replaced=source.replace('Landroidx/compose/material3/SurfaceKt;->Surface-T9BRK9s(', 'Lio/memorydrive/pleos/GloveboxDesign;->surface(').replace('Landroidx/compose/material3/SurfaceKt;->Surface-o_FOJdg(', 'Lio/memorydrive/pleos/GloveboxDesign;->clickableSurface(')
    replaced=replaced.replace('Landroidx/compose/material3/TextKt;->Text--4IGK_g(', 'Lio/memorydrive/pleos/GloveboxDesign;->text(')
    if source!=replaced: f.write_text(replaced,encoding='utf-8')
(root/'design-changes.json').write_text(json.dumps({'palette':palette,'logo':'existing Allura SVG rendered at 2x','body':'Pretendard variable','headlineLarge':'Nanum Flower Tree','unchanged':'all original data, navigation, platform and media services'},indent=2),encoding='utf-8')
# Keep the data's imported flag intact; only the photo renderer recognizes bundled demo photos.
photo_renderer=code/'MemoryScreenKt$MemoryPhoto$9.smali'
source=photo_renderer.read_text(encoding='utf-8')
source,count=re.subn(r'invoke-virtual (\{[^}]+\}), Lio/memorydrive/pleos/Memory;->getImported\(\)Z',r'invoke-static \1, Lio/memorydrive/pleos/GloveboxSamples;->hasDisplayPhotos(Ljava/lang/Object;)Z',source)
assert count==2, count
photo_renderer.write_text(source,encoding='utf-8')
library=code/'VehicleLibraryHomeKt$VehicleLibraryHome$15.smali'
source=library.read_text(encoding='utf-8')
start=source.index('.method private static final invoke$lambda$5(')
position=source.index('\n',source.index('    .locals ',start))+1
source=source[:position]+'''
    move-object/from16 v0, p1
    move-object/from16 v1, p10
    move-object/from16 v2, p22
    invoke-static {v0, v1, v2}, Lio/memorydrive/pleos/GloveboxFeatures;->journal(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Z
    move-result v0
    if-eqz v0, :glovebox_normal_library
    sget-object v0, Lkotlin/Unit;->INSTANCE:Lkotlin/Unit;
    return-object v0
    :glovebox_normal_library
'''+source[position:]
library.write_text(source,encoding='utf-8')
print('Native theme applied; original drive journal connected; demo photos use assigned files')
