(()=>{
 'use strict';
 const bridge=window.GloveboxNative;if(!bridge||!window.L)return;
 const esc=v=>String(v??'').replace(/[&<>"']/g,c=>({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[c]));
 const send=(name,arg='')=>bridge.action(name,arg);
 const img=v=>typeof v==='string'&&/^data:image\/(jpeg|png|webp);base64,[A-Za-z0-9+/=]+$/.test(v)?v:'';
 const valid=(lat,lon)=>Number.isFinite(lat)&&Number.isFinite(lon)&&Math.abs(lat)<=85&&Math.abs(lon)<=180;
 const coords=s=>[s?.coordinate?.latitude,s?.coordinate?.longitude];
 const clock=t=>new Date(t).toLocaleTimeString('ko-KR',{hour:'2-digit',minute:'2-digit',hour12:false});
 const day=t=>new Date(t).toLocaleDateString('ko-KR',{month:'long',day:'numeric',weekday:'short'});
 const sendTrack=(id,idx)=>{const n=memories.findIndex(m=>m.nativeId===id);if(n<0)return;select(n,'detail');state.photo=idx;render();};
 let journal={trips:[],photos:[]},saved=[],publicCourses=[],selectedTrip='',activeCourse='',stopIndex=0,expanded=false;
 let map=null,positions={},lastKey='',picking=false,checked=new Set(),received=false,scope='all',photoVisit='',selectionSource='';
 const metro=(lat,lon,a,b)=>Math.hypot((lat-a)*111195,(lon-b)*111195*Math.cos((lat+a)*Math.PI/360));
 function projections(photo,trip){
  const candidates=[];
  for(let i=0;i<trip.points.length;i++){
   const b=trip.points[i];if(!valid(b.latitude,b.longitude))continue;
   candidates.push({distance:metro(photo.lat,photo.lon,b.latitude,b.longitude),time:b.atMs,limit:i===trip.points.length-1?1800000:600000});
   const a=trip.points[i-1];if(!a||!valid(a.latitude,a.longitude)||b.atMs<=a.atMs||b.atMs-a.atMs>180000)continue;
   const east=111195*Math.cos(photo.lat*Math.PI/180);
   const x=(b.longitude-a.longitude)*east,y=(b.latitude-a.latitude)*111195,len=x*x+y*y;
   if(!len||len>2500*2500)continue;
   const t=Math.max(0,Math.min(1,((photo.lon-a.longitude)*east*x+(photo.lat-a.latitude)*111195*y)/len));
   candidates.push({distance:metro(photo.lat,photo.lon,a.latitude+t*(b.latitude-a.latitude),a.longitude+t*(b.longitude-a.longitude)),time:a.atMs+t*(b.atMs-a.atMs),limit:600000});
  }
  return candidates;
 }
 function associate(data){
  const byTrip=new Map(data.trips.map(t=>[t.id,[]])),revisited=new Map(data.trips.map(t=>[t.id,[]])),unassigned=[];
  for(const photo of data.photos){
   let chosen=photo.chosen,best=Infinity;
   if(chosen===undefined&&valid(photo.lat,photo.lon)&&Number.isFinite(photo.at)){
    for(const trip of data.trips){
     if(photo.excluded===trip.id||photo.at<trip.startedAtMs-600000||photo.at>trip.endedAtMs+1800000)continue;
     for(const point of projections(photo,trip)){
      const dt=Math.abs(photo.at-point.time),score=point.distance+dt/3000;
      if(point.distance<=250&&dt<=point.limit&&score<best){chosen=trip.id;best=score;}
     }
    }
   }
   if(byTrip.has(chosen))byTrip.get(chosen).push(photo);else unassigned.push(photo);
   if(!Number.isFinite(photo.at)||!valid(photo.lat,photo.lon))continue;
   for(const trip of data.trips){
    if(chosen===trip.id||photo.excluded===trip.id||new Date(photo.at).toDateString()===new Date(trip.startedAtMs).toDateString()||photo.at>=trip.startedAtMs)continue;
    if(projections(photo,trip).some(point=>point.distance<=250))revisited.get(trip.id).push(photo);
   }
  }
  return {byTrip,revisited,unassigned};
 }
 let matches=associate(journal);
 const trips=()=>journal.trips;
 const trip=()=>trips().find(t=>t.id===selectedTrip)||trips()[0];
 const albums=()=>Array.from(new Set(matches.unassigned.map(p=>p.album)));
 const visit=()=>mapsPhotos().filter(p=>p.album===photoVisit);
 const mapsPhotos=()=>matches.unassigned;
 const setTrip=t=>{photoVisit='';selectedTrip=t;};
 const tripPhotos=()=>photoVisit?visit():(matches.byTrip.get(trip()?.id)||[]);
 const allCourses=()=>{const ids=new Set(saved.map(c=>c.id));return [...saved,...publicCourses.filter(c=>!ids.has(c.id))].map(c=>{const live=publicCourses.find(p=>p.id===c.id);return live?{...c,...live,roadPreview:c.roadPreview||live.roadPreview,experience:{...c.experience,...live.experience}}:c;});};
 function shown(){
  const rows=allCourses();
  if(scope==='saved')return rows.filter(c=>saved.some(s=>s.id===c.id));
  if(scope==='nearby')return rows.filter(c=>Number.isFinite(c.suggestionScore)).sort((a,b)=>b.suggestionScore-a.suggestionScore);
  return rows;
 }
 const course=()=>shown().find(c=>c.id===activeCourse)||(scope==='nearby'&&shown()[0])||shown().find(c=>c.stops?.[0]&&cover(c,c.stops[0])&&img(c.experience?.avatar))||shown().find(c=>img(c.experience?.avatar))||shown()[0];
 const stop=()=>course()?.stops?.[stopIndex]||course()?.stops?.[0];
 function meta(c){return c?.experience||{};}
 function cover(c,s){
  const embedded=img(meta(c).photos?.[s?.id]);if(embedded)return embedded;
  const remote=(s?.photos||[]).find(id=>typeof id==='string'&&/^[a-zA-Z0-9_-]{1,128}$/.test(id));
  return remote&&c.publicId?'https://glovebox.local/community-photo/'+encodeURIComponent(c.publicId)+'/'+remote:'';
 }
 const initials=c=>(meta(c).name||'').trim().slice(0,1);
 const person=(c)=>img(meta(c).avatar)?'<img src="'+img(meta(c).avatar)+'" alt="">':'<span>'+esc(initials(c)||'⌖')+'</span>';
 const distance=t=>{let n=0;for(let i=1;i<(t?.points||[]).length;i++){const a=t.points[i-1],b=t.points[i];if(valid(a.latitude,a.longitude)&&valid(b.latitude,b.longitude))n+=metro(a.latitude,a.longitude,b.latitude,b.longitude);}return n;};
 const km=n=>n>=1000?(n/1000).toFixed(1)+' km':Math.round(n)+' m';
 const btn=(a,v,label,cls='')=>'<button type="button" class="'+cls+'" data-ja="'+a+'" data-value="'+esc(v)+'">'+label+'</button>';
 function mapBox(label){return '<div class="ja-map-wrap"><div id="journey-map" aria-label="'+label+'"></div><div class="ja-map-tools">'+btn('fit','','전체 보기')+btn('zoom','1','＋')+btn('zoom','-1','−')+'</div></div>';}
 function journalPage(){
  const t=trip(),ps=tripPhotos(),v=photoVisit?ps[0]:null,past=photoVisit?[]:(matches.revisited.get(t?.id)||[]);
  const nearby=t&&ps.find(p=>valid(p.lat,p.lon)&&t.points.some(f=>valid(f.latitude,f.longitude)&&metro(p.lat,p.lon,f.latitude,f.longitude)<250));
  const title=v?v.place:(nearby?.place|| (t?day(t.startedAtMs)+' 드라이브':'내가 달린 길'));
  const metrics=v?(v.at?day(v.at)+' · ':'')+'사진 '+ps.length+'장':t?clock(t.startedAtMs)+' — '+clock(t.endedAtMs)+'　·　'+km(distance(t))+'　·　'+Math.max(1,Math.round((t.endedAtMs-t.startedAtMs)/60000))+'분':'';
  const tripItems=trips().map(x=>btn('trip',x.id,'<b>'+esc(day(x.startedAtMs))+'</b><span>'+clock(x.startedAtMs)+'　'+km(distance(x))+'</span>',!photoVisit&&t?.id===x.id?'selected':'')).join('');
  const visitItems=albums().map(id=>{const p=mapsPhotos().find(x=>x.album===id);return btn('visit',id,'<img src="'+esc(p.src)+'" alt=""><b>'+esc(p.place||p.title)+'</b>',photoVisit===id?'selected':'');}).join('');
  const thumbnails=(rows,previous)=>rows.map(p=>btn('photo',p.key,'<img src="'+esc(p.src)+'" alt="'+esc(p.place)+'">'+(p.at?'<span>'+(previous?new Date(p.at).toLocaleDateString('ko-KR',{month:'numeric',day:'numeric'}):clock(p.at))+'</span>':''),'ja-photo')).join('');
  const strip=thumbnails(ps,false);
  const earlier=past.length?'<div class="ja-section-title"><h3>이 길에서 다시 만난 사진 <em>'+past.length+'</em></h3></div><div class="ja-photos">'+thumbnails(past,true)+'</div>':'';
  return '<section class="ja-page"><div class="ja-head"><h1>내가 달린 길</h1>'+btn('journal-settings','','기록 설정 ↗')+'</div><div class="ja-layout">'+mapBox('주행 경로와 사진 위치')+'<aside class="ja-pane"><div class="ja-panel-head"><h2>'+esc(title)+'</h2><p>'+esc(metrics)+'</p></div><div class="ja-body"><div class="ja-section-title"><h3>'+(past.length&&!ps.length?'사진':'이날의 사진')+(ps.length?' <em>'+ps.length+'</em>':'')+'</h3>'+(t&&!v?btn('link','','사진 연결 ＋'):'')+'</div>'+(strip?'<div class="ja-photos">'+strip+'</div>':(past.length?'':'<p class="ja-muted">이 주행에 함께 남길 사진을 골라 보세요.</p>'))+earlier+'<div class="ja-section-title"><h3>주행 기록</h3></div><div class="ja-trips">'+(tripItems||'<p>첫 주행을 마치면 이곳에 기록됩니다.</p>')+'</div>'+(visitItems?'<div class="ja-section-title"><h3>사진으로 남은 방문</h3></div><div class="ja-visits">'+visitItems+'</div>':'')+'</div></aside></div></section>';
 }
 let peopleOpen=false,authorCourse='';
 const authorKey=c=>JSON.stringify([String(meta(c).name||'').trim(),meta(c).avatar||'']);
 function authorCard(rows){
  const c=rows[0],photoStop=c.stops?.find(s=>cover(c,s)),pic=cover(c,photoStop);
  const placeNames=[...new Set(rows.flatMap(r=>(r.stops||[]).map(s=>s.name)).filter(Boolean))];
  return '<article class="ja-driver-card">'+(pic?'<img class="ja-driver-cover" alt="" src="'+esc(pic)+'">':'')+
   btn('profile',c.id,'<i class="ja-avatar">'+person(c)+'</i><span><b>'+esc(meta(c).name)+'</b><small>코스 '+rows.length+'개</small></span><strong aria-hidden="true">›</strong>','ja-driver-person')+
   '<div class="ja-driver-writeup"><b>'+esc(c.title)+'</b><p>'+esc(placeNames.slice(0,3).join(' · '))+'</p></div></article>';
 }
 function peoplePage(){
  const groups=new Map();
  for(const c of allCourses()){
   if(!String(meta(c).name||'').trim())continue;
   const key=authorKey(c);if(!groups.has(key))groups.set(key,[]);groups.get(key).push(c);
  }
  const selected=allCourses().find(c=>c.id===authorCourse),rows=selected?groups.get(authorKey(selected)):null;
  const top='<div class="ja-head"><h1>'+(rows?esc(meta(selected).name):'운전자 둘러보기')+'</h1><div class="ja-actions">'+
   (rows?btn('people','','모든 운전자'):'')+btn('to-map','','공유 지도')+btn('explore','','공개 코스')+btn('compose','','코스 만들기 ＋','ja-primary')+'</div></div>';
  if(!rows)return '<section class="ja-page ja-community">'+top+'<div class="ja-driver-grid">'+
   ([...groups.values()].map(authorCard).join('')||'<div class="ja-empty-drivers">아직 작성자가 표시된 코스가 없습니다.</div>')+'</div></section>';
  const uniqueStops=new Set(rows.flatMap(r=>(r.stops||[]).map(s=>JSON.stringify(coords(s)))));
  const header='<div class="ja-profile-lead"><i class="ja-avatar">'+person(selected)+'</i><div><h2>'+esc(meta(selected).name)+'</h2><p>공유한 코스 '+rows.length+'개　·　경유지 '+uniqueStops.size+'곳</p></div></div>';
  const cards=rows.map(c=>{
   const first=c.stops?.find(s=>cover(c,s))||c.stops?.[0],pic=cover(c,first);
   const memo=(c.stops||[]).find(s=>s.note)?.note||c.introduction||'';
   return btn('profile-course',c.id,(pic?'<img src="'+esc(pic)+'" alt="">':'')+'<span><b>'+esc(c.title)+'</b><small>'+esc((c.stops||[]).map(s=>s.name).join(' → '))+'</small>'+(memo?'<p>'+esc(memo)+'</p>':'')+'<em>지도에서 열기　↗</em></span>','ja-profile-route');
  });
  return '<section class="ja-page ja-community">'+top+'<div class="ja-profile-body">'+header+'<div class="ja-profile-routes">'+cards.join('')+'</div></div></section>';
 }
 function storyPage(){
  if(peopleOpen)return peoplePage();
  const list=shown(),c=course(),s=stop();const stops=c?.stops||[],pic=cover(c,s),author=meta(c).name;
  const coastLink=list.length>0&&!list.some(x=>x.experience?.avatar)?btn('coast','','제주 해안 코스'):'';
  const topline='<div class="ja-head"><h1>함께 나눈 길</h1><div class="ja-actions">'+btn('people','','운전자')+btn('scope','all','전체','all'===scope?'on':'')+btn('scope','nearby','추천',scope==='nearby'?'on':'')+btn('scope','saved','보관함','saved'===scope?'on':'')+coastLink+btn('map-tools','','지도 도구')+btn('compose','','코스 만들기 ＋','ja-primary')+'</div></div>';
  const entries=list.map(x=>btn('course',x.id,'<i class="ja-avatar">'+person(x)+'</i><span><b>'+esc(x.title)+'</b><small>'+esc(meta(x).name||'보관한 코스')+' · '+x.stops.length+'곳</small></span>',c?.id===x.id?'selected':'')).join('');
  const cards=stops.map((x,i)=>btn('stop',i,'<em>'+String(i+1).padStart(2,'0')+'</em><b>'+esc(x.name)+'</b>',i===stopIndex?'selected':'')).join('');
  const authors=new Map();for(const item of list){if(meta(item).name&&!authors.has(meta(item).name))authors.set(meta(item).name,item);}
  const people=authors.size?'<div class="ja-people">'+Array.from(authors.values(),x=>btn('author',x.id,'<i class="ja-avatar">'+person(x)+'</i><span>'+esc(meta(x).name)+'</span>',c?.id===x.id?'selected':'')).join('')+'</div>':'';
  const road=c?.meters?' · '+km(c.meters)+' · 약 '+Math.max(1,Math.round(c.seconds/60))+'분':'';
  const info=c?'<div class="ja-story-lead '+(pic?'has-picture':'')+'">'+(pic?'<img class="ja-scene" src="'+esc(pic)+'" alt="'+esc(s?.name)+'">':'')+'<div>'+(author?btn('profile',c.id,'<i class="ja-avatar">'+person(c)+'</i>'+esc(author)+'　›','ja-by ja-by-link'):'')+'<h2>'+esc(c.title)+'</h2><p>'+stops.length+'곳'+road+'</p>'+(scope==='nearby'?'<p>'+esc(c.suggestionReason)+'</p>':'')+'</div></div><div class="ja-body"><div class="ja-stops">'+cards+'</div><h3 class="ja-place">'+esc(s?.name||c.title)+'</h3><p class="ja-note">'+esc(s?.note||c.introduction)+'</p>'+(s?.music?.title?(s.music.provider==='YouTube Music'&&saved.some(x=>x.id===c.id)?btn('song',s.id,'▶　'+esc(s.music.title),'ja-song'):'<p class="ja-track">♫　'+esc(s.music.title)+'</p>'):'')+'<div class="ja-actions">'+(saved.some(x=>x.id===c.id)?btn('guide',c.id,'이 코스 열기 ↗','ja-primary'):btn('keep',c.publicId,'내 보관함에 담기','ja-primary'))+(saved.some(x=>x.id===c.id)?btn('forward',c.id,'코스 보내기')+btn('share-card',c.id,'내 카드 만들기'):'')+(!c.roadPreview?.length&&stops.length>1&&saved.some(x=>x.id===c.id)?btn('road',c.id,'도로 경로 보기'):'')+'</div><div class="ja-section-title"><h3>코스 목록 <em>'+list.length+'</em></h3>'+btn('shelf','','보관함 관리 ↗')+'</div><div class="ja-entries">'+entries+'</div></div>':scope==='nearby'?'<div class="ja-body"><h2>주변 추천 코스</h2><p>현재 위치에 맞는 코스가 아직 없어요.</p>'+btn('scope','all','전체 코스 보기')+'</div>':'<div class="ja-body"><h2>'+(received?'아직 담아 둔 길이 없어요':'코스를 불러오는 중')+'</h2>'+btn('coast','','제주 해안 코스 열기','ja-primary')+btn('explore','','공유 코스 둘러보기 ↗')+'</div>';
  return '<section class="ja-page">'+topline+'<div class="ja-layout">'+mapBox('공유한 장소 지도').replace('<div class="ja-map-tools">',people+'<div class="ja-map-tools">')+'<aside class="ja-pane">'+info+'</aside></div></section>';
 }
 window.receiveNativeJournal=value=>{journal=value;matches=associate(value);if(photoVisit&&!albums().includes(photoVisit))photoVisit='';if(state.view==='driving'||state.view==='detail')render();};
 window.receiveNativeCourses=value=>{saved=value.courses||[];received=true;if(state.view==='routes')render();};
 window.glCommunityFeed=value=>{publicCourses=value.courses||[];if(state.view==='routes')render();};
 window.glRoadForStory=(id,line,meters,seconds)=>{const c=saved.find(x=>x.id===id);if(c){c.roadPreview=line;c.meters=meters;c.seconds=seconds;}if(state.view==='routes')render();};
 window.glStoryRoadError=()=>notify('도로 경로를 불러오지 못했습니다.');
 driving=journalPage;routes=storyPage;
 function openStoryEditor(){
  const c=course(),pictures=memories.flatMap(m=>(m.nativeId?m.photos.slice(0,m.photoCount):[]).map((src,i)=>({src,title:m.place+' · '+(i+1)})));
  const select=(key)=>'<select data-card-photo="'+esc(key)+'"><option value="">선택 안 함</option>'+pictures.map((p,i)=>'<option value="'+i+'">'+esc(p.title)+'</option>').join('')+'</select>';
  const content=document.querySelector('#dialog-content');
  content.innerHTML='<section class="ja-picker ja-editor"><h2>공유 카드 만들기</h2><p>'+esc(c.title)+'</p><label>지도에 표시할 이름<input id="card-by" maxlength="40" required placeholder="이름 또는 별명"></label><label>프로필 사진'+select('avatar')+'</label>'+c.stops.map(s=>'<label>'+esc(s.name)+' 사진'+select(s.id)+'</label>').join('')+'<p class="ja-feedback" role="status"></p>'+btn('export-card','','파일로 저장','ja-primary')+'</section>';
  content._pictures=pictures;document.querySelector('#dialog').showModal();
 }
 async function photoData(src,size){
  const i=new Image();i.src=src;await i.decode();const canvas=document.createElement('canvas');
  const ratio=Math.min(1,size/Math.max(i.naturalWidth,i.naturalHeight));canvas.width=Math.max(1,Math.round(i.naturalWidth*ratio));canvas.height=Math.max(1,Math.round(i.naturalHeight*ratio));canvas.getContext('2d').drawImage(i,0,0,canvas.width,canvas.height);return canvas.toDataURL('image/jpeg',.77);
 }
 async function saveStoryCard(button){
  const host=document.querySelector('#dialog-content'),name=host.querySelector('#card-by').value.trim(),feedback=host.querySelector('.ja-feedback');
  if(!name){feedback.textContent='지도에 표시할 이름을 입력해 주세요.';return;}
  button.disabled=true;try{
   const c=course(),experience={name,avatar:'',photos:{},artists:meta(c).artists||{}};
   for(const select of host.querySelectorAll('[data-card-photo]')){
    const key=select.dataset.cardPhoto,avatar=key==='avatar';
    const value=select.value===''?'':await photoData(host._pictures[Number(select.value)].src,avatar?120:440);
    if(avatar)experience.avatar=value;else if(value)experience.photos[key]=value;
   }
   const payload=JSON.stringify({id:c.id,experience});
   if(new Blob([payload]).size>235000){throw Error('사진 용량이 큽니다. 더 적은 사진을 선택해 주세요.');}
   send('export-story',payload);
  }catch(error){feedback.textContent=error.message;button.disabled=false;}
 }
 window.glCardSaved=(id,filename)=>{document.querySelector('#dialog').close();activeCourse=id;stopIndex=0;expanded=false;notify('다운로드에 '+filename+' 저장');send('courses');};
 window.glCardFailed=()=>{const form=document.querySelector('.ja-editor');if(form){form.querySelector('button').disabled=false;form.querySelector('.ja-feedback').textContent='파일을 저장하지 못했습니다. 다시 시도해 주세요.';}};
 function openPicker(){
  if(!trip())return;checked=new Set(tripPhotos().map(p=>p.key));picking=true;
  const content=document.querySelector('#dialog-content');content.innerHTML='<section class="ja-picker"><h2>이 주행에 연결할 사진</h2><div class="ja-pick-grid">'+journal.photos.map(p=>'<label><input type="checkbox" data-pick="'+esc(p.key)+'" '+(checked.has(p.key)?'checked':'')+'><img src="'+esc(p.src)+'" alt=""><b>'+esc(p.place||p.title)+'</b>'+(p.at?'<small>'+esc(day(p.at))+' '+clock(p.at)+'</small>':'')+'</label>').join('')+'</div>'+btn('commit','','선택 저장','ja-primary')+'</section>';document.querySelector('#dialog').showModal();
 }
 function disposeMap(){if(map){if(map.hasJourneyPoints)positions[lastKey]={center:map.getCenter(),zoom:map.getZoom()};map.remove();map=null;}}
 function mapKey(){return state.view==='routes'?'routes:'+scope:'journal:'+(photoVisit||trip()?.id||'empty');}
 function redrawMap(){
  const el=document.getElementById('journey-map');if(!el||!el.isConnected)return;
  lastKey=mapKey();map=L.map(el,{zoomControl:false,attributionControl:false,zoomAnimation:false,fadeAnimation:false,markerZoomAnimation:false,scrollWheelZoom:true});
  const street=L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png',{maxZoom:19,keepBuffer:1}).addTo(map);
  L.control.attribution({prefix:false,position:'bottomleft'}).addTo(map).addAttribution('© OpenStreetMap contributors');
  let bounds=[],markers=[];
  function marker(pos,content,select,label,kind,active){
   if(!valid(...pos))return;bounds.push(pos);
   const pin=L.marker(pos,{title:label,alt:label,zIndexOffset:active?800:0,icon:L.divIcon({className:'ja-marker '+kind+(active?' current':''),html:content,iconSize:[46,52],iconAnchor:[23,49]})}).addTo(map);
   pin.on('click',select);markers.push(pin);return pin;
  }
  if(state.view==='routes'){
   const focus=course();
   if(focus?.roadPreview?.length>1)L.polyline(focus.roadPreview.map(([lon,lat])=>[lat,lon]),{color:'#73451c',weight:7,opacity:.95}).addTo(map),L.polyline(focus.roadPreview.map(([lon,lat])=>[lat,lon]),{color:'#ffca79',weight:3}).addTo(map);
   const groups=new Map();
   for(const c of shown())for(const [i,s] of (c.stops||[]).entries()){
    if(i>0&&c.id!==focus?.id)continue;const pos=coords(s);if(!valid(...pos))continue;const k=pos.map(x=>x.toFixed(5)).join(':');if(!groups.has(k))groups.set(k,[]);groups.get(k).push({c,s,i,pos});
   }
   groups.forEach(items=>{
    const entry=items.find(x=>x.c.id===focus?.id&&x.i===stopIndex)||items[0],{c,s,i,pos}=entry,active=c.id===focus?.id&&i===stopIndex;
    const pin=marker(pos,'<i>'+person(c)+'</i>'+(items.length>1?'<sup>'+items.length+'</sup>':''),()=>{const n=active&&items.length>1?items[(items.indexOf(entry)+1)%items.length]:entry;activeCourse=n.c.id;stopIndex=n.i;expanded=true;render();},s.name,'story-pin',active);
    if(active&&pin)pin.bindTooltip('<b>'+esc(s.name)+'</b>'+(expanded&&s.note?'<span>'+esc(s.note)+'</span>':''),{permanent:true,direction:'top',interactive:true,offset:[0,-46],className:'ja-bubble'}).openTooltip().getTooltip().on('click',()=>{activeCourse=c.id;stopIndex=i;expanded=true;render();});
    if(!active&&groups.size<=4&&items[0]&&i===0&&map.getContainer().clientWidth>610)pin?.bindTooltip(esc(s.name),{permanent:true,direction:'top',offset:[0,-45],className:'ja-bubble ja-secondary'}).openTooltip();
   });
  }else{
   const t=trip(),points=photoVisit?[]:(t?.points||[]).filter(p=>valid(p.latitude,p.longitude)).map(p=>[p.latitude,p.longitude]);
   if(points.length){bounds.push(...points);L.polyline(points,{color:'#fff5e7',weight:9,opacity:.98}).addTo(map);L.polyline(points,{color:'#a46539',weight:5}).addTo(map);
    [[0,'출발'],[points.length-1,'도착']].forEach(([i,label])=>{L.circleMarker(points[i],{color:'#fff',weight:2,radius:6,fillColor:label==='도착'?'#603729':'#c3894f',fillOpacity:1}).bindTooltip(label).addTo(map);});}
   for(const p of [...tripPhotos(),...(photoVisit?[]:(matches.revisited.get(t?.id)||[]))].filter(p=>valid(p.lat,p.lon))){
    // An explicitly linked photograph may be from far away; preserve its real coordinates.
    const prior=bounds.length;marker([p.lat,p.lon],'<i><img src="'+esc(p.src)+'" alt=""></i>',()=>sendTrack(p.album,p.index),p.place,'camera-pin',false);
    if(points.length&&points.every(x=>metro(p.lat,p.lon,...x)>3000))bounds.length=prior;
   }
  }
  function fit(){if(bounds.length){map.fitBounds(L.latLngBounds(bounds),{padding:[68,76],maxZoom:17,animate:false});}else map.setView([36.3,127.5],7);}
  map.hasJourneyPoints=bounds.length>0;map.fitJourney=fit;const remembered=positions[lastKey];if(remembered)map.setView(remembered.center,remembered.zoom);else fit();
 }
 const previous=render;render=function(){
  disposeMap();previous();
  if(state.view==='detail'&&m()?.nativeId){
   const hasPhoto=ps=>(ps||[]).some(p=>p.album===m().nativeId&&p.index===state.photo);
   const ts=journal.trips.filter(t=>hasPhoto(matches.byTrip.get(t.id))),again=journal.trips.filter(t=>hasPhoto(matches.revisited.get(t.id)));
   const parent=document.querySelector('.detail-right .side-panel:nth-child(2)');
   const links=rows=>rows.map(t=>btn('open-trip',t.id,esc(day(t.startedAtMs))+'　'+clock(t.startedAtMs)+' ↗')).join('');
   if(parent&&(ts.length||again.length))parent.innerHTML=(ts.length?'<h3>이 사진과 함께한 주행</h3>'+links(ts):'')+(again.length?'<h3>이곳을 다시 지난 날</h3>'+links(again):'');
  }
  if(['routes','driving'].includes(state.view))redrawMap();
 };
 const goBase=go;go=function(name){goBase(name);if(name==='detail')send('journal');};
 document.addEventListener('click',e=>{
  const b=e.target.closest('[data-ja]');if(!b)return;e.stopImmediatePropagation();const a=b.dataset.ja,v=b.dataset.value;
  if(a==='fit'){map?.fitJourney();return;}if(a==='zoom'){map?.setZoom(map.getZoom()+Number(v));return;}
  if(a==='photo'){const p=journal.photos.find(p=>p.key===v);if(p)sendTrack(p.album,p.index);return;}
  if(a==='link'){openPicker();return;}
  if(a==='commit'){const next=new Set([...document.querySelectorAll('[data-pick]:checked')].map(n=>n.dataset.pick));document.querySelector('#dialog').close();picking=false;send('journal-edit',JSON.stringify({trip:trip().id,add:[...next].filter(k=>!checked.has(k)),remove:[...checked].filter(k=>!next.has(k))}));return;}
  if(a==='scope'){scope=v;if(scope==='nearby')send('courses');activeCourse='';stopIndex=0;expanded=false;}
  if(a==='course'||a==='stop'){activeCourse=a==='course'?v:course().id;stopIndex=a==='stop'?Number(v):0;expanded=true;const s=stop();if(s&&map){const pos=coords(s);if(valid(...pos))map.setView(pos,Math.max(14,map.getZoom()));}}
  if(a==='trip'||a==='open-trip'){setTrip(v);if(a==='open-trip'){go('driving');return;}}
  if(a==='visit')photoVisit=v;
  if(a==='people'||a==='author'||a==='profile'){peopleOpen=true;authorCourse=a==='people'?'':v;render();return;}
  if(a==='to-map'){peopleOpen=false;authorCourse='';render();return;}
  if(a==='profile-course'){scope='all';peopleOpen=false;activeCourse=v;stopIndex=0;expanded=true;render();const pt=coords(stop());if(map&&valid(...pt))map.setView(pt,Math.max(14,map.getZoom()));return;}
  if(a==='forward'){send('share-saved-story',v);return;}
  if(a==='song'){send('story-song',JSON.stringify({course:course().id,stop:v}));return;}
  if(a==='coast'){send('coast');return;}
  if(a==='keep'){send('keep-story',v);return;}
  if(a==='guide'){send('courseRoute','course:'+v);return;}
  if(['compose','explore','shelf'].includes(a)){send('courseRoute',a);return;}
  if(a==='share-card'){openStoryEditor();return;}
  if(a==='export-card'){saveStoryCard(b);return;}
  if(a==='road'){send('story-road',v);return;}
  if(a==='map-tools'){send('map');return;}
  if(a==='journal-settings'){send('native','library');return;}
  render();
 },true);
})();
