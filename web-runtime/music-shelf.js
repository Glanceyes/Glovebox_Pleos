(()=>{
 'use strict';
 const bridge=window.GloveboxNative;
 if(!bridge)return;
 const send=(action,id='')=>bridge.action(action,id);
 const esc=value=>String(value??'').replace(/[&<>"']/g,c=>({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[c]));
 const art=id=>'https://glovebox.local/youtube-cover/'+encodeURIComponent(id);
 const number=n=>Number(n||0).toLocaleString('ko-KR');
 const date=(t,full=true)=>t>0?new Intl.DateTimeFormat('ko-KR',{month:'long',day:'numeric',...(full?{hour:'numeric',minute:'2-digit',hour12:true}:{})}).format(new Date(t)):'';
 const glyphs={youtube:'<rect x="2" y="5" width="20" height="14" rx="5"/><path d="m10 9 6 3-6 3z"/>',wheel:'<circle cx="12" cy="12" r="9"/><circle cx="12" cy="12" r="2"/><path d="m4 8 6 3m10-3-6 3m-2 3v7"/>',sync:'<path d="M20 10a8 8 0 0 0-14-3L3 10m0-6v6h6m-5 4a8 8 0 0 0 14 3l3-3m0 6v-6h-6"/>',music:'<path d="M9 18V6l11-2v12M9 9l11-2"/><ellipse cx="6" cy="18" rx="3" ry="2"/><ellipse cx="17" cy="16" rx="3" ry="2"/>',arrow:'<path d="m9 5 7 7-7 7"/>',back:'<path d="m15 5-7 7 7 7"/>',play:'<path d="m8 4 12 8-12 8z"/>',clock:'<circle cx="12" cy="12" r="9"/><path d="M12 6v6l4 2"/>',link:'<path d="m9 15 6-6m-7 4-2 2a3 3 0 0 0 4 4l3-3m-2-8 3-3a3 3 0 0 1 4 4l-2 2"/>'};
 function glyph(name){return '<svg viewBox="0 0 24 24" aria-hidden="true" fill="none" stroke="currentColor" stroke-width="1.7" stroke-linecap="round" stroke-linejoin="round">'+glyphs[name]+'</svg>';}
 function cover(track,cls=''){return '<span class="sleeve '+cls+'">'+glyph('music')+(track?.id?'<img loading="lazy" alt="" src="'+art(track.id)+'">':'')+'</span>';}
 function btn(action,label,symbol='',style=''){return '<button type="button" data-ms="'+action+'" class="ms-button '+style+'">'+(symbol?glyph(symbol):'')+esc(label)+'</button>';}
 const lists=()=>window.glMusicLibrary||[];
 let open=false,section='lists',selection=-1,detail=-1,events=[],readStatus='loading',remember=false,showSettings=false;
 const originalGo=go;
 go=function(name){
   open=name==='music-shelf';
   if(open){window.glStopShelfPlayer();section='lists';selection=detail=-1;showSettings=false;readStatus='loading';events=[];}
   originalGo(open?'music':name);
   if(open)send('shelf-history');
 };
 window.glOpenMusicShelf=()=>go('music-shelf');
 const oldReturn=window.glSurfaceReturn;
 window.glSurfaceReturn=()=>{oldReturn?.();if(open){events=[];detail=-1;readStatus='loading';send('shelf-history');render();}};
 window.glShelfHistory=(trips,album,saving)=>{
   if(!open)return;
   remember=!!saving;
   const previous=events[detail];
   events=[...(trips||[]),...(album||[])].sort((a,b)=>b.at-a.at);
   detail=previous?events.findIndex(e=>e.kind===previous.kind&&e.at===previous.at&&e.title===previous.title):-1;
   readStatus=album===null?'error':'ready';render();
 };
 function panels(){
    const sync=window.glMusicSync||{},library=lists(),connected=!!window.glYouTubeConnected;
    const covers=[], seen=new Set();
    for(let offset=0;offset<4&&covers.length<3;offset++)for(const list of library){const t=list.tracks[offset];if(t&&!seen.has(t.id)){covers.push(t);seen.add(t.id);}if(covers.length===3)break;}
    return '<div class="ms-panels"><section class="ms-account"><div class="ms-panel-copy"><div class="ms-kicker">'+glyph('youtube')+'YouTube 보관함</div><h2>'+(connected?number(sync.count)+'<small>곡</small>':'계정 연결')+'</h2><p>'+(connected?(sync.last?'최근 동기화　'+esc(date(sync.last)):'동기화 대기 중'):'좋아요와 재생목록 가져오기')+'</p><div class="ms-actions">'+(connected?'<button type="button" class="ms-button" data-ms="sync" '+(sync.busy?'disabled':'')+'>'+glyph('sync')+(sync.busy?'동기화 중':'지금 동기화')+'</button>':'')+btn('manage',connected?'계정 관리':'YouTube 연결',connected?'':'link','quiet')+'</div></div><div class="ms-stack" aria-hidden="true">'+covers.slice().reverse().map(t=>cover(t)).join('')+'</div></section>'+
    '<section class="ms-car"><div class="ms-panel-copy"><div class="ms-kicker">'+glyph('wheel')+'차량 청취 기록</div><h2>'+(!events.length?'청취 기록 없음':esc(events[0].title))+'</h2><p>'+(events.length?esc(date(events[0].at))+'　·　'+(events[0].kind==='drive'?'주행 중':'차량 앨범'):(sync.record?'주행 기록 켜짐':'주행 기록 꺼짐'))+'</p><div class="ms-actions">'+btn('history','기록 보기','clock')+btn('settings','기록 설정','','quiet')+'</div></div><div class="ms-wheel" aria-hidden="true">'+glyph('wheel')+'</div></section></div>';
 }
 function listGrid(){
    if(!lists().length)return '<div class="ms-empty">'+glyph('music')+'<p>'+(window.glYouTubeConnected?'가져온 재생목록이 없습니다.':'YouTube를 연결하면 재생목록이 표시됩니다.')+'</p></div>';
    return '<div class="ms-collections">'+lists().map((l,i)=>'<button data-ms="list" data-i="'+i+'" class="ms-collection"><div class="ms-pair">'+cover(l.tracks[0])+ (l.tracks[1]?cover(l.tracks[1]):'')+'</div><span><b>'+esc(l.name)+'</b><small>'+number(l.tracks.length)+'곡</small></span>'+glyph('arrow')+'</button>').join('')+'</div>';
 }
 function history(){
    if(!events.length)return '<div class="ms-empty">'+glyph('clock')+'<p>'+(readStatus==='loading'?'청취 기록을 불러오는 중':readStatus==='error'?'앨범 청취 기록을 불러오지 못했습니다.':'차량에 저장된 청취 기록이 없습니다.')+'</p>'+(readStatus==='error'?btn('retry','다시 불러오기'):'')+'</div>';
    return '<div class="ms-events">'+events.map((e,i)=>'<button data-ms="event" data-i="'+i+'"><span class="ms-event-icon">'+(e.video?cover({id:e.video}):glyph('wheel'))+'</span><span><b>'+esc(e.title)+'</b><small>'+esc(date(e.at))+'　·　'+(e.kind==='drive'?'주행 중':'차량 앨범')+'</small></span>'+glyph('arrow')+'</button>').join('')+'</div>';
 }
 function listDetail(list){
   return '<header class="ms-subhead">'+btn('back','보관함','back')+'<h1>'+esc(list.name)+'</h1><span>'+number(list.tracks.length)+'곡</span></header><div class="ms-tracks">'+list.tracks.map((t,i)=>'<button data-ms="play" data-i="'+i+'" aria-label="'+esc(t.title)+' 재생">'+cover(t)+'<span><b>'+esc(t.title)+'</b><small>'+esc(t.artist)+'</small></span>'+glyph('play')+'</button>').join('')+'</div>';
 }
 function eventDetail(e){
   const known=e.video&&lists().flatMap(l=>l.tracks).find(t=>t.id===e.video);
   return '<header class="ms-subhead">'+btn('back','청취 기록','back')+'<h1>청취 상세</h1></header><section class="ms-event-detail">'+(e.video?cover({id:e.video}):'<div class="ms-detail-wheel">'+glyph('wheel')+'</div>')+'<div><p>'+(e.kind==='drive'?'주행 중':'차량 앨범')+'</p><h2>'+esc(e.title)+'</h2>'+(e.artist?'<p>'+esc(e.artist)+'</p>':'')+'<dl><dt>들은 시각</dt><dd>'+esc(date(e.at))+'</dd>'+(e.provider?'<dt>음악 서비스</dt><dd>'+esc(e.provider)+'</dd>':'')+'</dl><div class="ms-actions">'+(e.trip?'<button class="ms-button" data-ja="open-trip" data-value="'+esc(e.trip)+'">'+glyph('wheel')+'이 주행 열기</button>':'')+(known?btn('again','다시 듣기','play'):'')+'</div></div></section>';
 }
 function picks(){
   const rows=[],seen=new Set(),names=new Set();
   for(let k=0;k<20&&rows.length<5;k++)for(const list of lists()){
     const t=list.tracks[k];if(!t||seen.has(t.id)||names.has(t.artist)||rows.length===5)continue;
     rows.push(t);seen.add(t.id);names.add(t.artist);
   }
   return rows;
 }
 function musicStrip(){
   const items=picks();if(!items.length)return '';
   return '<section class="ms-featured"><h2>보관함의 곡</h2><div>'+items.map(t=>'<button type="button" data-ms="pick" data-id="'+esc(t.id)+'" aria-label="'+esc(t.title)+' 재생">'+cover(t)+'<b>'+esc(t.title)+'</b><small>'+esc(t.artist)+'</small></button>').join('')+'</div></section>';
 }
 function content(){
   if(showSettings)return '<header class="ms-subhead">'+btn('back','보관함','back')+'<h1>청취 기록 설정</h1></header><div class="ms-options"><section>'+glyph('music')+'<div><h2>앨범에서 들은 곡</h2><p>Glovebox 앨범에서 재생한 곡과 들은 시각을 이 차량에 저장합니다.</p></div><button class="ms-switch" role="switch" aria-label="앨범 청취 기록" aria-checked="'+remember+'" data-ms="remember">'+(remember?'켜짐':'꺼짐')+'</button></section><section>'+glyph('wheel')+'<div><h2>주행 중 들은 곡</h2><p>주행 기록이 켜져 있으면 차량이 받은 곡 정보를 그때 달린 경로와 함께 저장합니다.</p></div>'+btn('journey-settings','주행 기록 관리','arrow')+'</section></div>';
   if(detail>=0&&events[detail])return eventDetail(events[detail]);
   if(selection>=0&&lists()[selection])return listDetail(lists()[selection]);
   return '<header class="ms-heading"><h1>음악 보관함</h1>'+btn('player','재생 화면','music','quiet')+'</header>'+panels()+'<div class="ms-tabs" role="group" aria-label="보관함 보기"><button data-ms="lists" aria-pressed="'+(section==='lists')+'">'+(window.glYouTubeConnected?'내 재생목록':'재생목록')+' <small>'+lists().length+'</small></button><button data-ms="history" aria-pressed="'+(section==='history')+'">청취 기록</button><button data-ms="phone" class="ms-phone">'+glyph('link')+'iPhone 연결</button></div>'+(section==='lists'?listGrid()+musicStrip():history());
 }
 const priorRender=render;
 render=function(){
   if(!(open&&state.view==='music')){priorRender();return;}
   const old=view.querySelector('.ms-body'),scroll=old?.scrollTop||0,active=document.activeElement?.dataset;
   priorRender();
   view.innerHTML='<section class="ms-body" aria-label="음악 보관함과 청취 기록">'+content()+'</section>';
   const root=view.firstElementChild; root.scrollTop=scroll;
   if(active?.ms){const matches=Array.from(root.querySelectorAll('[data-ms]')).filter(b=>b.dataset.ms===active.ms&&b.dataset.i===active.i);matches[0]?.focus({preventScroll:true});}
 };
 document.addEventListener('error',e=>{if(e.target.matches?.('.sleeve img'))e.target.hidden=true;},true);
 document.addEventListener('click',e=>{
   const b=e.target.closest('[data-ms]');if(!b||!open)return;
   e.stopImmediatePropagation();
   const action=b.dataset.ms,idx=Number(b.dataset.i);
   if(action==='sync'){send('shelf-sync');return;}
   if(action==='retry'){readStatus='loading';send('shelf-history');}
   if(action==='manage'){if(window.glYouTubeConnected)send('shelf-manage');else window.glShowYouTubeLink();return;}
   if(action==='settings'){showSettings=true;render();return;}
   if(action==='journey-settings'){send('native','journal');return;}
   if(action==='remember'){send('shelf-album-setting',remember?'off':'on');return;}
   if(action==='phone'){send('iphone-media-setup');return;}
   if(action==='player'){go('music');return;}
   if(action==='list'){selection=idx;detail=-1;}
   if(action==='event'){detail=idx;selection=-1;}
   if(action==='pick'){const t=picks().find(x=>x.id===b.dataset.id);if(t)window.glPlayStoryTrack(t);return;}
   if(action==='play'){const t=lists()[selection]?.tracks[idx];if(t)window.glPlayStoryTrack(t);return;}
   if(action==='again'){const t=lists().flatMap(l=>l.tracks).find(t=>t.id===events[detail]?.video);if(t)window.glPlayStoryTrack(t);return;}
   if(action==='lists'||action==='history'){section=action;selection=detail=-1;}
   if(action==='back'){selection=detail=-1;showSettings=false;}
   render();const root=view.querySelector('.ms-body');if(root)root.scrollTop=0;
 },true);
})();
