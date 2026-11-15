(()=>{
 'use strict';
 const bridge=window.GloveboxNative;if(!bridge)return;
 const send=(name,id='')=>bridge.action(name,id);
 const baseRender=render,basePlay=play,basePlayback=window.receiveNativePlayback;
 let publishedTrack=null,waitingId='',unavailable='',phoneId='';
 let output=localStorage.getItem('glAlbumPlayback')||'auto';
 send('output-mode',output);
 let failure=false,localTab=false,permitted=false,deviceFiles=[],vehicleSound={},recentSound="";
 let trackDuration=0,editingSeek=false,buffering=false;
 let song=null,owner='',listName='',signature='',started=false,time=0,isPlaying=false,tab='',activeId='',pending=false;
 const safe=v=>String(v??'').replace(/[&<>"']/g,k=>({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[k]));
 const thumbnail=id=>'https://glovebox.local/youtube-cover/'+encodeURIComponent(id);
 function track(){
  if(owner===m()?.nativeId&&song)return song;
  if(m()?.yt)return m().yt;
  if(window.glNoAlbums&&state.view==='music'){
   const lists=window.glMusicLibrary||[];
   return (lists.find(p=>p.name===listName)||lists[0])?.tracks[0];
  }
 }
 function connected(){return !!(window.glMusicLibrary||[]).length;}
 function clearPlayer(){buffering=false;trackDuration=0;editingSeek=false;document.getElementById('gl-album-player')?.remove();if(started){state.playing=false;updatePlayback();}failure=false;started=false;pending=false;time=0;isPlaying=false;signature='';send('inset-close');}
 window.glSwitchMusicAccount=()=>{
   clearPlayer();song=null;owner='';listName='';activeId='';waitingId='';phoneId='';publishedTrack=null;
 };
 function sync(){
  const map=document.querySelector('.gl-road-slot');
  const slot=map||document.querySelector('.gl-player-slot');const t=track();
  if(!slot||document.querySelector('dialog[open]')||(!map&&!t)){if(signature){signature='';send('inset-close');}return;}
  const r=slot.getBoundingClientRect(),scale=devicePixelRatio;
  const parent=slot.closest('.detail-right,.gl-journal,.gl-route-detail-body');
  if(parent){const p=parent.getBoundingClientRect();if(r.top<p.top-3||r.bottom>p.bottom+3){if(signature){signature='';send('inset-close');}return;}}
  const msg={kind:map?map.dataset.mapKind:'track',id:map?map.dataset.mapId:t.id,x:Math.round(r.left*scale),y:Math.round(r.top*scale),w:Math.round(r.width*scale),h:Math.round(r.height*scale)};
  const s=JSON.stringify(msg);if(s!==signature||pending){signature=s;if(pending&&!map)msg.start=true;pending=false;send('inset',JSON.stringify(msg));}
 }
 const nextFrame=()=>requestAnimationFrame(()=>requestAnimationFrame(sync));
 function stamp(){const seconds=Math.floor(time);return Math.floor(seconds/60)+':'+String(seconds%60).padStart(2,'0');}
 const clockLabel=t=>Math.floor(t/60)+':'+String(Math.floor(t)%60).padStart(2,'0');
 function timeline(){
  const bar=document.querySelector('#seek-track');if(!bar)return;
  const remote=vehicleSound.track,source=started||remote;
  const total=started?trackDuration:Number(remote?.duration||0)/1000;
  const at=started?time:Number(vehicleSound.position??-1)/1000;
  const canSeek=total>0&&(started||vehicleSound.canSeek);
  bar.disabled=!canSeek;
  if(!editingSeek)bar.value=total>0&&at>=0?Math.min(100,at/total*100):0;
  bar.style.setProperty('--played',bar.value+'%');
  document.querySelector('.seek-current').textContent=editingSeek?clockLabel(total*bar.value/100):source&&at>=0?clockLabel(at):'';
  document.querySelector('.seek-duration').textContent=total>0?clockLabel(total):'';
 }
 window.glTimeline=timeline;
 window.glTrackDuration=seconds=>{if(started){trackDuration=seconds;timeline();}};
 document.addEventListener('input',e=>{if(e.target.id==='seek-track'&&!e.target.disabled){editingSeek=true;timeline();}});
 document.addEventListener('change',e=>{
  if(e.target.id!=='seek-track'||e.target.disabled)return;
  const ms=started?trackDuration*1000:Number(vehicleSound.track?.duration||0);
  editingSeek=false;
  if(ms>0)send(started?'inset-seek':'audio-seek',String(Math.round(ms*e.target.value/100)));
 });
 document.addEventListener('pointercancel',()=>{if(editingSeek){editingSeek=false;timeline();}});
 function paint(){
  if(!started)return;
  const t=track();if(!t)return;
  state.playing=isPlaying||buffering;updatePlayback();timeline();
  const control=document.querySelector('.gl-inset-control');if(control){control.textContent=state.playing?'Ⅱ':'▶';control.setAttribute('aria-label',state.playing?'앨범 음악 일시정지':'앨범 음악 이어 듣기');}
  document.querySelectorAll('.elapsed').forEach(n=>n.textContent=stamp());
  const mini=document.querySelector('#mini-title');if(mini)mini.textContent=t.title;
  document.querySelectorAll('.music-console .progress span,.side-panel .progress span').forEach(n=>n.style.width=(trackDuration>0?Math.min(100,time/trackDuration*100):0)+'%');
 }
 window.glTrackState=(status,seconds,error)=>{time=seconds;isPlaying=status===1;buffering=status===3;failure=error>0;const x=document.querySelector('.gl-player-caption');if(x)x.hidden=!failure;paint();};
 window.glSurfaceReturn=()=>{if(document.querySelector('dialog[open] .gl-phone-link'))document.querySelector('dialog').close();clearPlayer();if(m()&&['detail','music','driving','routes'].includes(state.view))render();};
 window.receiveNativePlayback=(data)=>{
  vehicleSound=started?{}:(data.vehicleAudio||{});timeline();
  if(vehicleSound.track){
   if(!phoneId||vehicleSound.track.id==='external:'+phoneId){waitingId='';unavailable='';}const t=vehicleSound.track;state.playing=vehicleSound.playing;updatePlayback();
   setLabel('#mini-title',t.title);setLabel('.detail-right .side-track b',t.title);setLabel('.detail-right .side-track small',t.artist||'차량 음악');
   setLabel('.cover-copy h1',t.title);setLabel('.console-heading small','차량 음악');setLabel('.music-console .dial span','차량 음악');
   const pos=Math.floor(vehicleSound.position/1000);document.querySelectorAll('.elapsed').forEach(n=>n.textContent=pos>=0?Math.floor(pos/60)+':'+String(pos%60).padStart(2,'0'):'');
   document.querySelectorAll('.music-console .progress span,.side-panel .progress span').forEach(n=>n.style.width=(pos>=0&&t.duration>0?Math.min(100,100*vehicleSound.position/t.duration):0)+'%');
   recentSound=t.id;
  }else if(!started&&output==='phone'){
   if(recentSound){recentSound='';render();}
   state.playing=false;updatePlayback();
   document.querySelectorAll('.elapsed').forEach(n=>n.textContent='');
   document.querySelectorAll('.music-console .progress span,.side-panel .progress span').forEach(n=>n.style.width='0%');
   const waiting=window.glPhoneLink?.pending||'휴대폰 재생 정보 대기';
   setLabel('.detail-right .side-track small',waiting);setLabel('.console-heading small',waiting);
  }else{if(recentSound){recentSound='';render();}if(!started)basePlayback(data);else paint();if(waitingId&&waitingId===track()?.id)setLabel('.detail-right .side-track small',data.musicWait||'곡을 불러오는 중');}
 };
 window.glDeviceFiles=(files,access)=>{deviceFiles=files;permitted=access;if(state.view==='music'&&localTab)render();};
 function chooseLocal(t){clearPlayer();song=null;owner='';activeId='';vehicleSound={track:t,playing:false,position:0};send('audio-choose',t.id);render();}
 function audioSources(){
  const box=document.querySelector('.playlist');if(!box)return;
  const nav=document.createElement('div');nav.className='ja-source-tabs';
  for(const [key,label] of [['youtube','내 보관함'],['local','차량 음악']]){const b=document.createElement('button');b.textContent=label;b.className=(key==='local')===localTab?'on':'';b.onclick=()=>{localTab=key==='local';render();if(localTab)send('audio-files');};nav.append(b);}
  if(localTab){
   box.replaceChildren(nav);const h=document.createElement('h3');h.textContent='차에 담은 음악';box.append(h);
   const rows=document.createElement('div');rows.className='tracks';box.append(rows);
   for(const t of deviceFiles){const b=document.createElement('button');b.className='track'+(vehicleSound.track?.id===t.id?' selected':'');b.innerHTML='<i class="ja-note-icon">♫<img alt="" src="https://glovebox.local/device-cover/'+encodeURIComponent(t.id)+'" onerror="this.remove()"></i><div><b>'+safe(t.title)+'</b><small>'+safe(t.artist)+'</small></div><span>▸</span>';b.onclick=()=>chooseLocal(t);rows.append(b);}
   if(!deviceFiles.length){const p=document.createElement('p');p.textContent=permitted?'차량의 음악 폴더에 저장한 곡이 여기에 표시됩니다.':'차량에 저장된 음악을 연결해 주행 중에도 이어 들으세요.';rows.append(p);const b=document.createElement('button');b.className='native-settings-link';b.textContent=permitted?'다시 불러오기':'차량 음악 연결';b.onclick=()=>send(permitted?'audio-files':'audio-access');rows.append(b);}
  }else box.prepend(nav);
 }
 function phoneLink(t){
   if(!t||!/^[-_a-zA-Z0-9]{11}$/.test(t.id))return;
   const qr=qrcode(0,'M');qr.addData('https://music.youtube.com/watch?v='+t.id);qr.make();
   modal('<div class="gl-phone-link"><h2>iPhone에서 듣기</h2><b>'+safe(t.title)+'</b><small>'+safe(t.artist)+'</small>'+qr.createSvgTag({cellSize:5,margin:20,scalable:true})+'<p>iPhone 카메라로 코드를 스캔하면 YouTube Music에서 이 곡을 열 수 있습니다.</p></div>');
 }
 window.glMusicUnavailable=(id,why)=>{if(waitingId!==id)return;waitingId='';unavailable=why;render();};
 window.glNativeOpened=(id,provider)=>{if(waitingId!==id)return;waitingId='';unavailable='';render();};
 window.glPlayEmbedded=id=>{if(waitingId!==id||track()?.id!==id||!['music','detail'].includes(state.view))return;waitingId='';tune(track(),true);};
 function destination(){
  if(output!=='auto')return output;
  return window.glVehiclePlayers?.phone?'phone':window.glVehiclePlayers?.music?'music':'popup';
 }
 function tune(t,embedded=destination()==='popup'){
  const target=destination();
  if(!t||!t.id)return;
  if(vehicleSound.track){if(!(target==='phone'&&vehicleSound.phone))send('audio-stop');vehicleSound={};recentSound='';}
  if(!['detail','music'].includes(state.view)){go('detail');}
  if(started&&activeId===t.id&&document.querySelector('.gl-player-slot')){send(isPlaying||buffering?'inset-pause':'inset-play');return;}
  if(!embedded&&target==='phone'){
   clearPlayer();song=t;owner=m()?.nativeId;activeId=t.id;phoneId=t.id;waitingId=t.id;unavailable='';
   send('album-phone',t.id);render();return;
  }
  phoneId='';
  if(!embedded){
   if(waitingId===t.id)return;
   clearPlayer();song=t;owner=m()?.nativeId;activeId=t.id;waitingId=t.id;
   unavailable='';send(output==='auto'&&target==='music'?'album-system-audio':'album-'+target,t.id);render();return;
  }
  waitingId='';unavailable='';send('audio-stop');
  if(activeId!==t.id){time=0;trackDuration=0;editingSeek=false;isPlaying=false;}
  failure=false;song=t;owner=m()?.nativeId;activeId=t.id;started=true;pending=true;render();nextFrame();
 }
 play=()=>{if(vehicleSound.phone&&vehicleSound.track){send('audio-toggle');return;}const candidate=track();if(vehicleSound.track&&state.view==='detail'&&candidate&&vehicleSound.track.id!=='external:'+candidate.id){tune(candidate);return;}if(vehicleSound.track){send('audio-toggle');return;}const t=track();if(t)tune(t);else if(window.glSampleMode)basePlay();else send("native","music");};
 window.glPlayStoryTrack=t=>{localTab=false;go("music");tune(t);};
 window.glStopShelfPlayer=()=>{if(started)clearPlayer();};
 function libraryPanel(){
  const box=document.querySelector('.playlist'),lists=window.glMusicLibrary||[];if(!box||!lists.length)return;
  const list=lists.find(p=>p.name===listName)||lists.reduce((a,b)=>a.tracks.length>=b.tracks.length?a:b);
  listName=list.name;const head=box.querySelector('h3'),rows=box.querySelector('.tracks');if(!head||!rows)return;
  const header=document.createElement('div');header.className='gl-library-heading';head.before(header);header.append(head);
  head.textContent='내 음악';const selector=document.createElement('select');selector.className='gl-library-pick';selector.setAttribute('aria-label','재생목록 선택');
  for(const p of lists){const opt=new Option(p.name+' · '+p.tracks.length,p.name);opt.selected=p.name===list.name;selector.add(opt);}
  selector.onchange=()=>{listName=selector.value;render();};header.append(selector);
  const id=track()?.id;
  rows.innerHTML=list.tracks.map((t,i)=>`<button class="track ${id===t.id?'selected':''}" data-library-track="${i}"><img loading="lazy" src="${thumbnail(t.id)}" alt=""><div><b>${safe(t.title)}</b><small>${safe(t.artist)}</small></div><span class="track-play">${id===t.id?'▥':'▸'}</span></button>`).join('');
  const footer=box.querySelector('.playlist-foot');if(footer)footer.lastElementChild.textContent=list.tracks.length+'곡';
  const connection=document.createElement('button');connection.className='gl-route-text';connection.textContent='보관함 · 청취 기록　↗';connection.addEventListener('click',()=>window.glOpenMusicShelf());box.append(connection);
 }
 function setLabel(s,text){const el=document.querySelector(s);if(el)el.textContent=text;}
 render=function(){
  baseRender();if(!m())return;
  if(tab!==state.view){if(!(['music','detail'].includes(tab)&&['music','detail'].includes(state.view))&&started)clearPlayer();tab=state.view;}
  if(owner&&owner!==m().nativeId){clearPlayer();waitingId='';owner='';song=null;activeId='';}
  const t=track();
  const box=state.view==='detail'?document.querySelector('.detail-right .side-panel'):state.view==='music'?document.querySelector('.music-console'):null;
  if(box&&t){
    const line=document.createElement('div');line.className='gl-play-options';
    const outputButton=document.createElement('button');outputButton.type='button';
    outputButton.textContent=({auto:'자동 선택',popup:'앨범 플레이어',phone:'휴대폰 음악',music:'YouTube Music 앱',video:'차량용 YouTube 앱'}[output])+'　⌄';outputButton.setAttribute('aria-label','재생 방법 선택');
    outputButton.onclick=()=>{modal('<h2>재생 방법</h2><div class="gl-output-list"></div>');
      const list=document.querySelector('.gl-output-list');
      for(const [k,name] of [['auto','연결된 오디오 자동 선택'],['popup','앨범 안에서 영상 보기'],['phone','연결된 휴대폰으로 듣기'],['music','YouTube Music 앱에서 열기'],['video','차량용 YouTube 앱에서 열기']]){
       const b=document.createElement('button');b.textContent=name;b.setAttribute('aria-pressed',String(k===output));b.onclick=()=>{if(output!==k){send('audio-stop');clearPlayer();vehicleSound={};activeId='';waitingId='';unavailable='';}output=k;localStorage.setItem('glAlbumPlayback',k);send('output-mode',k);document.querySelector('#dialog').close();render();};list.append(b);
      }
      const iphone=document.createElement('button');iphone.textContent='iPhone 음악 제어 연결';
      iphone.onclick=()=>{document.querySelector('#dialog').close();send('iphone-media-setup');};list.append(iphone);
    };line.append(outputButton);
    if(destination()==='phone'){
      const link=document.createElement('button');link.type='button';link.textContent=window.glPhoneLink?.device?window.glPhoneLink.device+'에서 열기':'iPhone에서 곡 열기';link.onclick=()=>window.glPhoneLink?.device?send('iphone-open-track',t.id):phoneLink(t);line.append(link);
    }
    if(destination()==='phone'&&window.glPhoneLink?.device&&window.glPhoneLink.notice&&!vehicleSound.track){
      const note=document.createElement('small');note.setAttribute('role','status');note.textContent=window.glPhoneLink.notice;line.append(note);
    }
    if(unavailable){const n=document.createElement('small');n.setAttribute('role','status');n.textContent=unavailable;line.append(n);}
    box.append(line);
    if(state.view==='detail'&&m()?.yt&&t.id===m().yt.id){
       const rec=m().yt,why=String(rec.reason||'').split(/\s*·\s*(?:당시|이 장소와의)/)[0];
       if(why){const hint=document.createElement('p');hint.className='gl-rec-reason';hint.textContent=why;line.before(hint);}
    }
  }
  const selection=JSON.stringify({album:m()?.nativeId||'',track:t?.id||''});
  if(selection!==publishedTrack){publishedTrack=selection;send('recommendation',selection);}
  if(state.view==='music'&&connected()){
   libraryPanel();
   if(t){setLabel('.cover-copy h1',t.title);setLabel('.console-heading small','YouTube');setLabel('.music-console .dial span','YouTube');}
  }
  if(state.view==='music')audioSources();
  if(state.view==='home'&&m()?.yt){
    const rec=m().yt;setLabel('.music-widget h3',rec.albumMusic?'앨범에 담긴 곡':'이 장소의 추천곡');setLabel('.music-widget p',rec.title);
    setLabel('.music-widget>small',rec.artist||'YouTube');
    const art=document.querySelector('.music-widget .album-stack img');if(art){art.src=thumbnail(rec.id);art.alt=rec.title;}
  }
  if(state.view==='detail'&&t){
    const history=['temporal_history','period_history'].includes(t.association);
    setLabel('.detail-right .side-panel h3',owner===m().nativeId&&song?.id!==m()?.yt?.id?'선택한 음악':history?'방문 당시의 음악':t.albumMusic?'앨범에 담긴 곡':'이 장소의 추천곡');
    setLabel('.detail-right .side-track b',t.title);setLabel('.detail-right .side-track small',waitingId===t.id?'곡을 불러오는 중':(t.artist||'YouTube'));
    const disc=document.querySelector('.detail-right .side-track .record');if(disc){disc.style.backgroundImage='linear-gradient(#0003,#0005),url('+thumbnail(t.id)+')';disc.classList.add('gl-album-art');}
  }
  if(started&&t&&['detail','music'].includes(state.view)){
    let popup=document.getElementById('gl-album-player');
    if(!popup){
      popup=document.createElement('section');popup.id='gl-album-player';popup.setAttribute('aria-label','앨범 플레이어');
      popup.innerHTML='<header><div><strong></strong><small></small></div><button type="button" class="gl-inset-control" aria-label="앨범 음악 이어 듣기">▶</button><button type="button" class="gl-inset-close" aria-label="플레이어 닫기">×</button></header><div class="gl-player-slot" aria-label="YouTube 플레이어"></div><p class="gl-player-caption" role="status" hidden>이 곡은 앨범에서 재생할 수 없습니다. 다른 곡을 선택해 주세요.</p>';
      popup.querySelector('.gl-inset-control').onclick=()=>send(isPlaying||buffering?'inset-pause':'inset-play');
      popup.querySelector('.gl-inset-close').onclick=()=>{clearPlayer();activeId='';render();};
      document.body.append(popup);
    }
    popup.querySelector('strong').textContent=t.title;popup.querySelector('small').textContent=t.artist||'';
    popup.querySelector('.gl-player-caption').hidden=!failure;
    paint();
  }
  nextFrame();
 };
 document.addEventListener('click',event=>{const b=event.target.closest('[data-library-track]');if(!b)return;event.stopImmediatePropagation();const p=window.glMusicLibrary.find(x=>x.name===listName);if(p)tune(p.tracks[Number(b.dataset.libraryTrack)]);},true);
 addEventListener('scroll',sync,true);addEventListener('resize',nextFrame);
 new MutationObserver(nextFrame).observe(document.querySelector('dialog'),{attributes:true,attributeFilter:['open']});
})();
