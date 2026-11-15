from pathlib import Path
import shutil,re
r=Path(__file__).parent
source=r.parent/'design/glovebox'
target=r/'patch/assets/glovebox-web'
target.mkdir(parents=True,exist_ok=True)
html=(source/'index.html').read_text(encoding='utf-8')
for name in re.findall(r'(?:src|href)="([^"?#]+\.(?:css|js))"',html):
    shutil.copy2(source/name,target/name)
for dirname in ['display','fonts']:
    shutil.copytree(source/'assets'/dirname,target/'assets'/dirname,dirs_exist_ok=True)
runtime='\n'.join(f.read_text(encoding='utf-8') for f in target.iterdir() if f.suffix in ['.js','.css'])+html
for f in (source/'assets').iterdir():
    if f.is_file() and ('assets/'+f.name) in runtime:shutil.copy2(f,target/'assets'/f.name)
for f in (target/'assets').iterdir():
    if f.is_file() and f.name!='empty-photo.svg' and ('assets/'+f.name) not in runtime:
        assert f.resolve().parent==target.joinpath('assets').resolve()
        f.unlink()
(target/'assets/upscaled-4x').mkdir(exist_ok=True)
if (source/'assets/upscaled-4x/drive-scene.png').exists():shutil.copy2(source/'assets/upscaled-4x/drive-scene.png',target/'assets/upscaled-4x/drive-scene.png')
html=html.replace('<script src="vehicle-shell-bridge.js"></script>','')
html=html.replace('</head>','<link rel="stylesheet" href="native-screen.css"></head>')
html=html.replace('</body>','<script src="native-screen.js"></script><script src="routes-design.js"></script><script src="journal-native.js"></script></body>')
html=html.replace('</head>','<link rel="stylesheet" href="routes-design.css"></head>')
html=html.replace('src="../../analysis/assets/res/QL.wav"','')
(target/'index.html').write_text(html,encoding='utf-8')
js=(target/'glovebox.js').read_text(encoding='utf-8')
js=js.replace('const photo=n=>`','const photo=n=>typeof n==="string"?n:`')
js=js.replace('%15','%memories.length').replace('+14','+memories.length-1')
js=js.replace('/15*100','/memories.length*100').replace('/ 15','/ ${memories.length}').replace('>15<','>${memories.length}<').replace("'15개의 순간'","`${memories.length}개의 순간`").replace('15곡','${memories.length}곡')
(target/'glovebox.js').write_text(js,encoding='utf-8')
shutil.copy2(r/'native-screen.js',target/'native-screen.js')
shutil.copy2(r/'native-screen.css',target/'native-screen.css')
for name in ['routes-design.js','routes-design.css','journal-native.js']:shutil.copy2(r/name,target/name)
optimized_video=r/'jumunjin-dawn-playback.mp4'
body_font=r/'patch/unknown/res/JL.ttf'
if body_font.exists():shutil.copy2(body_font,r/'patch/assets/glovebox/body.ttf')
if optimized_video.exists():
    shutil.copy2(optimized_video,target/'assets/display/jumunjin-dawn.mp4')
    shutil.copy2(optimized_video,r/'patch/assets/glovebox/jumunjin-dawn.mp4')
motion=r/'java/io/memorydrive/pleos/GloveboxMotion.java'
text=motion.read_text(encoding='utf-8')
if 'GloveboxScreen.install(activity);' not in text:text=text.replace('context=activity.getApplicationContext();','context=activity.getApplicationContext();\n        GloveboxScreen.install(activity);')
motion.write_text(text,encoding='utf-8')
print('Approved design and runtime assets prepared:',sum(f.stat().st_size for f in target.rglob('*') if f.is_file()))
