(()=>{
 'use strict';
 const bridge=window.GloveboxNative;
 if(!bridge)return;
 const designMemories=memories.map(v=>({...v}));
 let demo=false,loaded=false,position=0,duration=0;
 let viewport=null;
 fit=function(){
   const w=viewport?viewport[0]/devicePixelRatio:document.documentElement.clientWidth;
   const h=viewport?viewport[1]/devicePixelRatio:document.documentElement.clientHeight;
   const scale=Math.min(w/1600,h/900);
   app.style.width=w/scale+'px';app.style.height=h/scale+'px';app.style.transform=`scale(${scale})`;app.style.left='0';app.style.top='0';
 };
 window.receiveNativeViewport=(w,h)=>{viewport=[w,h];fit();};
 addEventListener('resize',fit);fit();
 const esc=v=>String(v??'').replace(/[&<>"']/g,c=>({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[c]));
 const command=(name,id=m()?.nativeId||'')=>bridge.action(name,id);
 const originalRender=render;
 render=function(){
   if(!loaded||!memories.length)return;
   originalRender();
   document.querySelector('.status').innerHTML=`<span class="dot"></span><span>${demo?'Glovebox':'내 기록'}</span><button data-native>연결 · 가져오기</button><button data-view="settings" aria-label="설정">☷</button>`;
   document.querySelector('.footer-bottom').innerHTML='<span>Glovebox</span><button data-native>계정 · 연결 · 가져오기 →</button>';
   document.querySelectorAll('footer>.pill').forEach(p=>p.remove());
   const recorded=m()?.nativeId==='a1a05005-85b8-4946-8287-3f2488fac021';
   if(!demo||recorded){
     document.querySelectorAll('.memory-video,.video-controls').forEach(e=>e.remove());
     document.querySelectorAll('.has-memory-video').forEach(e=>e.classList.remove('has-memory-video'));
     const location=view.querySelector('.coordinates');
     if(location){
       const visit=m(),place=location.querySelector('div');
       place.replaceChildren();
       if(visit.place){const name=document.createElement('b');name.innerHTML=visit.place;place.append(name);}
       if(Number.isFinite(visit.latitude)&&Number.isFinite(visit.longitude)){
         const point=document.createElement('small');
         const coord=(v,a,b)=>(v<0?b:a)+' '+Math.abs(v).toFixed(3)+'°';
         point.textContent=coord(visit.latitude,'N','S')+'　'+coord(visit.longitude,'E','W');place.append(point);
       }
       if(!place.childElementCount)location.remove();
     }
     document.querySelectorAll('.tile-index,.frame-index').forEach(el=>el.remove());
     document.querySelectorAll('.detail-intro .label:first-child').forEach(el=>el.textContent=m().season);
     if((m()?.photoCount||0)<2)view.querySelector('.memory-contact-sheet')?.remove();
   }
   if(state.view==='home'&&(!demo||recorded)){
     const caption=view.querySelector('.story-caption');
     if(caption){caption.replaceChildren();caption.textContent=[m().season,(m().photoCount||0)+'장'].filter(Boolean).join('  ·  ');}
     const season=view.querySelector('.home-bubbles .bubble:nth-last-child(2) small');if(season)season.textContent='촬영일';
   }
   if(state.view==='home'&&window.glNearbyMemory){
     const nearby=memories.find(x=>x.nativeId===window.glNearbyMemory);
     const tile=view.querySelector('.tile.feature');
     if(nearby&&tile){
       tile.querySelector('h3').innerHTML=nearby.title;
       tile.querySelector('small').textContent='이 근처에서 찍은 사진 보기';
       tile.querySelector('img').src=photo(nearby.photos[0]);
       tile.addEventListener('click',e=>{e.stopPropagation();select(nearby.id,'detail');});
     }
   }
   if(state.view==='settings'){
     const sum=view.querySelectorAll('.profile-summary b');
     if(sum.length===3){sum[0].textContent=window.glNoAlbums?0:memories.length;sum[2].textContent=memories.reduce((n,a)=>n+a.photoCount,0);}
     view.querySelector('.local-status')?.remove();
     const panels=view.querySelector('.preferences-panels');
     if(panels){
       const cards=[['library','사진과 방문 기록','장소별 앨범과 보관한 사진'],['music','음악 보관함',window.glYouTubeConnected?'연결된 YouTube 계정 · 좋아요와 재생목록':'YouTube 계정 연결'],['journal','주행 기록','기록 설정과 지난 경로'],['account','계정과 기기','사진 가져오기와 계정 관리']];
       panels.innerHTML=cards.map(([route,title,detail])=>'<section class="preference-card"><div class="preference-title"><h2>'+title+'</h2></div><p>'+detail+'</p><button class="setting-link" data-native-route="'+route+'">'+title+' 열기　↗</button></section>').join('');
     }
   }
   if(state.view==='settings'){
     const panel=view.querySelector('button[data-native-route="music"]')?.closest('.preference-card');
     if(panel){const connection=document.createElement('button');connection.className='setting-link';connection.textContent='iPhone 음악 제어 연결';connection.onclick=()=>command('iphone-media-setup','');panel.append(connection);}
   }
   if(state.view==='music' && !(window.glMusicLibrary||[]).length){
     const p=document.createElement('button');p.className='native-settings-link';p.dataset.nativeRoute='music';p.textContent='음악 보관함 · 서비스 연결 →';view.querySelector('.playlist')?.prepend(p);
   }
   document.querySelectorAll('[data-action="rewind"],[data-action="forward"]').forEach(p=>p.remove());
   document.querySelectorAll('.tile[data-view="routes"] small').forEach(p=>p.textContent='나의 코스와 함께 나눈 길');
   app.dataset.noAlbums=String(!!window.glNoAlbums);
   if(window.glNoAlbums){
     if(['home','archive','detail'].includes(state.view)){
       view.innerHTML='<section class="gl-journal"><div class="gl-journal-empty"><h2>첫 앨범을 열어 보세요</h2><p>휴대폰에서 사진을 가져오면 같은 장소에서 찍은 사진이 한곳에 모입니다.</p><button id="gl-recording-open">용산동 앨범 열기</button> <button data-native-route="usb">사진 가져오기</button> <button data-native-route="library">내 기록 열기</button></div></section>';
     }

   }
   const recordingButton=document.getElementById('gl-recording-open');if(recordingButton)recordingButton.onclick=()=>command('open-recording','');
   const clock=document.querySelector('.clock');if(clock)clock.textContent=new Date().toLocaleTimeString('ko-KR',{hour:'2-digit',minute:'2-digit',hour12:false});
   updateTime();
 };
 let catalogSignature='';
 window.receiveNativeCatalog=data=>{
   const signature=JSON.stringify(data);
   if(loaded&&signature===catalogSignature)return;
   catalogSignature=signature;
   const selected=m()?.nativeId;
   demo=data.demo;window.glSampleMode=demo;

   window.glAccountLink=data.accountLink||{};window.glMusicSync=data.musicSync||{};window.glPhoneLink=data.phoneLink||{};
   if(window.glMusicAccountGeneration!=null&&window.glMusicAccountGeneration!==data.accountGeneration)window.glSwitchMusicAccount?.();
   window.glMusicAccountGeneration=data.accountGeneration;
   window.glMusicLibrary=data.library||[];
   window.glYouTubeConnected=!!data.accountConnected;window.glVehiclePlayers={phone:!!data.phoneAudio,music:!!data.vehicleMusic,video:!!data.vehicleYouTube};
   window.glNearbyMemory=data.nearbyMemory||'';
   memories.splice(0,memories.length,...data.memories.map((v,id)=>({id,nativeId:v.nativeId,latitude:v.latitude,longitude:v.longitude,yt:v.yt||null,photoCount:v.photos.length,title:esc(v.title),place:esc(v.place),description:esc(demo?(designMemories.find(x=>x.title===v.title)?.description||v.description):v.description),song:esc(v.song),season:esc(demo?v.season.replace('의 기억',''):v.season),photos:v.photos.length?v.photos:['assets/empty-photo.svg']})));
   window.glNoAlbums=!memories.length;
   if(window.glNoAlbums)memories.push({id:0,nativeId:'',title:'내 앨범',photoCount:0,place:'',description:'',song:'',season:'',photos:['assets/empty-photo.svg']});
   const preferred=selected||(demo?'a1a05005-85b8-4946-8287-3f2488fac021':localStorage.getItem('glActiveVisit'));
   const index=memories.findIndex(v=>v.nativeId===preferred);
   const withPhotos=memories.findIndex(v=>v.photoCount>0&&v.yt);
   const firstPhoto=withPhotos>=0?withPhotos:memories.findIndex(v=>v.photoCount>0);
   if(index>=0)state.selected=index;
   else if(demo&&memories.some(v=>v.nativeId==='a1a05005-85b8-4946-8287-3f2488fac021'))state.selected=memories.findIndex(v=>v.nativeId==='a1a05005-85b8-4946-8287-3f2488fac021');
   else if(!demo&&firstPhoto>=0)state.selected=firstPhoto;
   else state.selected=Math.min(state.selected,memories.length-1);
   state.hidden=new Set();state.saved=new Set(memories.filter(v=>data.bookmarked.includes(v.nativeId)).map(v=>v.id));loaded=true;
   document.documentElement.classList.add('native-ready');render();
 };
 let playbackPainted=null,playbackSecond=-1;
 window.receiveNativePlayback=data=>{
   state.playing=!!data.playing;position=data.position||0;duration=data.duration||0;
   if(playbackPainted!==state.playing){playbackPainted=state.playing;updatePlayback();}
   const second=Math.floor(position/1000);
   if(playbackSecond!==second){playbackSecond=second;updateTime();}
 };
 updateTime=function(){const seconds=Math.floor(position/1000);document.querySelectorAll('.elapsed').forEach(el=>el.textContent=`${Math.floor(seconds/60)}:${String(seconds%60).padStart(2,'0')}`);document.querySelectorAll('.progress span').forEach(el=>{if(!el.closest('.dashboard'))el.style.width=(duration?position/duration*100:0)+'%';});};
 play=()=>command(state.playing?'pause':'play');
 const originalSelect=select;
 select=function(id,nextView){
   originalSelect(id,nextView);
   if(loaded&&!demo&&m()?.nativeId)localStorage.setItem('glActiveVisit',m().nativeId);
   command('open');
 };
 const originalGo=go;go=function(name){if(name==='driving'){originalGo(name);command('journal','');return;}originalGo(name);if(name==='routes')command('courses','');};
 function connections(){
   const content=document.querySelector('#dialog-content');
   content.innerHTML=`<div class="native-connections"><h2>기억을 이어 주는 연결</h2><p>계정과 사진, 음악과 드라이브 기록을 한곳에서 관리하세요.</p><div class="native-connection-grid">${[
     ['account','내 계정','로그인 · 개인 기록 동기화'],['usb','사진 가져오기','USB · 휴대폰 사진 연결'],['music','음악 보관함','서비스 · 앨범 · 재생 목록'],['music-connect','음악 연결','휴대폰 · 차량 음악 서비스'],['courses','함께 나눈 길','코스 · 지도 · 작성 · 가져오기'],['menu','기록과 설정','청취 기록 · 파일 수신 · 숨긴 기억']
   ].map(([route,title,sub])=>`<button data-native-route="${route}"><b>${title}</b><span>${sub}</span><em>↗</em></button>`).join('')}</div></div>`;
   const dialog=document.querySelector('#dialog');if(!dialog.open)dialog.showModal();
 }
 document.addEventListener('click',e=>{
   const b=e.target.closest('button');if(!b)return;
   if(b.dataset.nativeRoute){e.stopImmediatePropagation();document.querySelector('#dialog').close();if(b.dataset.nativeRoute==='music'&&window.glOpenMusicShelf)window.glOpenMusicShelf();else if(b.dataset.nativeRoute==='courses')go('routes');else command('native',b.dataset.nativeRoute);return;}
   if(b.hasAttribute('data-native')){e.stopImmediatePropagation();connections();return;}
   const action=b.dataset.action;
   if(['save','hide','rewind','forward','guide','tour','reset'].includes(action)){
     e.stopImmediatePropagation();
     if(action==='save')command('bookmark');else if(action==='hide')command('hide');else command('native','menu');
   }
   if(b.dataset.filter==='hidden'){e.stopImmediatePropagation();command('native','menu');}
 },true);
 const signalReady=()=>command('ready','');
 if(document.readyState==='loading')document.addEventListener('DOMContentLoaded',signalReady,{once:true});
 else signalReady();
})();
