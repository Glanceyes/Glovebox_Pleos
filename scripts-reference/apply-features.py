"""Adapt every original Compose feature, including its nested/error/dialog states.

Only presentation call sites are rewritten. Original callback arguments and their
compiled method bodies are retained. Run after apply-design.py.
"""
from pathlib import Path
import re,json
r=Path(__file__).parent
code=r/'patch/smali_classes3/io/memorydrive/pleos'
source=r/'decoded/smali_classes3/io/memorydrive/pleos'
classes=('ButtonKt','OutlinedTextFieldKt','AndroidAlertDialog_androidKt')
pattern=re.compile(r'Landroidx/compose/material3/('+ '|'.join(classes)+r');->([^\s(]+)(\([^\n]+\)V)')
specs={}
for f in source.glob('*.smali'):
    for m in pattern.finditer(f.read_text('utf-8')):specs[m.group(0)]=m.groups()
wrapper=['.class public final Lio/memorydrive/pleos/GloveboxControls;','.super Ljava/lang/Object;','']
replacements={}
for old,(owner,name,sig) in sorted(specs.items()):
    new=name.replace('-','_')
    args=re.findall(r'\[*L[^;]+;|\[*[ZBSCIJFD]',sig[1:sig.index(')')])
    regs=[];n=0
    for a in args:regs.append(n);n+=2 if a in ('J','D') else 1
    mod=args.index('Landroidx/compose/ui/Modifier;');shape=args.index('Landroidx/compose/ui/graphics/Shape;')
    mode=4 if owner=='ButtonKt' else 5 if owner=='OutlinedTextFieldKt' else 6
    mask=(1<<mod)|(1<<shape)
    wrapper += [f'.method public static {new}{sig}','    .locals 2',
        f'    move-object/from16 v0, p{regs[mod]}',f'    const/4 v1, {mode}',
        '    invoke-static {v0, v1}, Lio/memorydrive/pleos/GloveboxFeatures;->control(Ljava/lang/Object;I)Ljava/lang/Object;',
        '    move-result-object v0','    check-cast v0, Landroidx/compose/ui/Modifier;',f'    move-object/16 p{regs[mod]}, v0',
        '    invoke-static {}, Lio/memorydrive/pleos/GloveboxFeatures;->shape()Ljava/lang/Object;',
        '    move-result-object v0','    check-cast v0, Landroidx/compose/ui/graphics/Shape;',f'    move-object/16 p{regs[shape]}, v0',
        f'    move/from16 v0, p{n-1}',f'    const v1, {~mask}', '    and-int/2addr v0, v1',f'    move/16 p{n-1}, v0',
        f'    invoke-static/range {{p0 .. p{n-1}}}, {old}','    return-void','.end method','']
    replacements[old]=f'Lio/memorydrive/pleos/GloveboxControls;->{new}{sig}'
wrapper += ['.method public static tag(Landroidx/compose/ui/Modifier;Ljava/lang/String;)Landroidx/compose/ui/Modifier;',
    '    .locals 0','    invoke-static {p0, p1}, Lio/memorydrive/pleos/GloveboxFeatures;->tag(Ljava/lang/Object;Ljava/lang/String;)Ljava/lang/Object;',
    '    move-result-object p0','    check-cast p0, Landroidx/compose/ui/Modifier;','    return-object p0','.end method']
replacements['Landroidx/compose/ui/platform/TestTagKt;->testTag(']='Lio/memorydrive/pleos/GloveboxControls;->tag('
counts={}
for f in code.glob('*.smali'):
    if f.name.startswith('Glovebox'):continue
    s=f.read_text('utf-8');hits={}
    for old,new in replacements.items():
        count=s.count(old)
        if count:hits[old.split(';->')[1].split('(')[0]]=count;s=s.replace(old,new)
    if hits:counts[f.name]=hits
    f.write_text(s,encoding='utf-8')
(code/'GloveboxControls.smali').write_text('\n'.join(wrapper)+'\n',encoding='utf-8')
# Keep a reference to the original navigation state, instead of inventing a second router.
f=code/'MemoryScreenKt$MemoryScreen$13$1.smali';s=f.read_text('utf-8')
needle='.method public final invoke(Landroidx/compose/foundation/layout/BoxWithConstraintsScope;Landroidx/compose/runtime/Composer;I)V'
assert needle in s
pos=s.index('\n',s.index('    .locals ',s.index(needle)))+1
s=s[:pos]+'\n    invoke-static/range {p0 .. p0}, Lio/memorydrive/pleos/GloveboxFeatures;->capture(Ljava/lang/Object;)V\n'+s[pos:]
f.write_text(s,encoding='utf-8')
# Account form uses the available panel width; no abandoned right half on a vehicle display.
f=code/'VehiclePlatformLibraryKt.smali';s=f.read_text('utf-8')
s=s.replace('0x44700000','0x44c80000') # 960dp -> 1600dp upper bound
s=s.replace('const/16 v2, 0x3c0','const/16 v2, 0x640')
f.write_text(s,encoding='utf-8')
f=code/'MemoryScreenKt.smali';s=f.read_text('utf-8')
needle='.method private static final MemoryTopTab('
pos=s.index('\n',s.index('    .locals ',s.index(needle)))+1
s=s[:pos]+'''\n    move-object/from16 v0, p0
    move-object/from16 v1, p3
    invoke-static {v0, v1}, Lio/memorydrive/pleos/GloveboxFeatures;->tab(Ljava/lang/String;Ljava/lang/Object;)Ljava/lang/Object;
    move-result-object v0
    check-cast v0, Lkotlin/jvm/functions/Function0;
    move-object/16 p3, v0
'''+s[pos:]
f.write_text(s,encoding='utf-8')
(r/'feature-presentation-coverage.json').write_text(json.dumps(counts,ensure_ascii=False,indent=2),encoding='utf-8')
f=code/'VehicleCoursesKt.smali';s=f.read_text('utf-8')
needle='    invoke-static {v15}, Lio/memorydrive/pleos/VehicleCoursesKt;->VehicleCourses$lambda$20(Landroidx/compose/runtime/MutableState;)Z'
assert s.count(needle)==1
s=s.replace(needle,'    invoke-static {v4, v5, v15}, Lio/memorydrive/pleos/GloveboxRoutes;->capture(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)V\n\n'+needle)
f.write_text(s,encoding='utf-8')
print('Native feature presentation:',len(counts),'classes;',sum(sum(x.values()) for x in counts.values()),'call sites; callbacks preserved')
