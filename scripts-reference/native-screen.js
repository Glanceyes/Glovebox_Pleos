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
   document.querySelector('.status').innerHTML=`<span class="dot"></span><span>${demo?'가상 기록 체험':'내 기록'}</span><button data-native>연결 · 가져오기</button><button data-view="settings" aria-label="설정">☷</button>`;
   document.querySelector('.footer-bottom').innerHTML='<span>Glovebox</span><button data-native>계정 · 연결 · 가져오기 →</button>';
   document.querySelectorAll('footer>.pill').forEach((p,i)=>p.textContent=i===0?'Memory Drive 연동':demo?'시연 기록':'개인 기록');
   if(!demo){
     document.querySelectorAll('.memory-video,.video-controls').forEach(e=>e.remove());
     document.querySelector('.coordinates')?.replaceChildren();
   }
   if(state.view==='settings'){
     const p=document.createElement('button');p.className='native-settings-link';p.dataset.native='';p.textContent='계정 · 사진 · 음악 · 드라이브 기록 관리 →';view.prepend(p);
   }
   if(state.view==='music'){
     const p=document.createElement('button');p.className='native-settings-link';p.dataset.nativeRoute='music';p.textContent='음악 보관함 · 서비스 연결 →';view.querySelector('.playlist')?.prepend(p);
   }
   document.querySelectorAll('[data-action="rewind"],[data-action="forward"]').forEach(p=>p.remove());
   document.querySelectorAll('.tile[data-view="routes"] small').forEach(p=>p.textContent='나의 코스와 함께 나눈 길');
   updateTime();
 };
 let catalogSignature='';
 window.receiveNativeCatalog=data=>{
   const signature=JSON.stringify(data);
   if(loaded&&signature===catalogSignature)return;
   catalogSignature=signature;
   const selected=m()?.nativeId;
   demo=data.demo;
   if(!data.memories.length){loaded=false;command('native','');return;}
   memories.splice(0,memories.length,...data.memories.map((v,id)=>({id,nativeId:v.nativeId,title:esc(v.title),place:esc(v.place),description:esc(demo?(designMemories.find(x=>x.title===v.title)?.description||v.description):v.description),song:esc(v.song),season:esc(demo?v.season.replace('의 기억',''):v.season),photos:v.photos.length?v.photos:['assets/empty-photo.svg']})));
   const index=memories.findIndex(v=>v.nativeId===selected);state.selected=index>=0?index:Math.min(state.selected,memories.length-1);state.hidden=new Set();state.saved=new Set(memories.filter(v=>data.bookmarked.includes(v.nativeId)).map(v=>v.id));loaded=true;
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
 const originalSelect=select;select=function(id,nextView){originalSelect(id,nextView);command('open');};
 const originalGo=go;go=function(name){if(name==='driving'){command('native','journal');return;}originalGo(name);if(name==='routes')command('courses','');};
 function connections(){
   const content=document.querySelector('#dialog-content');
   content.innerHTML=`<div class="native-connections"><span class="label">GLOVEBOX · CONNECTIONS</span><h2>기억을 이어 주는 연결</h2><p>계정과 사진, 음악과 드라이브 기록을 한곳에서 관리하세요.</p><div class="native-connection-grid">${[
     ['account','내 계정','로그인 · 개인 기록 동기화'],['usb','사진 가져오기','USB · 휴대폰 사진 연결'],['music','음악 보관함','서비스 · 앨범 · 재생 목록'],['music-connect','음악 연결','휴대폰 · 차량 음악 서비스'],['courses','함께 나눈 길','코스 · 지도 · 작성 · 가져오기'],['menu','기록과 설정','청취 기록 · 파일 수신 · 숨긴 기억']
   ].map(([route,title,sub])=>`<button data-native-route="${route}"><b>${title}</b><span>${sub}</span><em>↗</em></button>`).join('')}</div></div>`;
   const dialog=document.querySelector('#dialog');if(!dialog.open)dialog.showModal();
 }
 document.addEventListener('click',e=>{
   const b=e.target.closest('button');if(!b)return;
   if(b.dataset.nativeRoute){e.stopImmediatePropagation();document.querySelector('#dialog').close();if(b.dataset.nativeRoute==='courses')go('routes');else command('native',b.dataset.nativeRoute);return;}
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
