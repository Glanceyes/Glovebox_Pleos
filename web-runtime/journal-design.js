(()=>{
 let data=null,selected=null;
 const esc=v=>String(v??'').replace(/[&<>"']/g,c=>({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[c]));
 const date=t=>new Date(t).toLocaleString('ko-KR',{month:'long',day:'numeric',hour:'2-digit',minute:'2-digit'});
 const coord=p=>`${Number(p.latitude).toFixed(5)}°, ${Number(p.longitude).toFixed(5)}°`;
 function map(points,trip){
   return points.length ? '<div class="gl-road-slot" data-map-kind="trip" data-map-id="'+esc(trip.id)+'" role="region" aria-label="주행 경로 지도"></div>' : '<p>저장된 위치가 없어요.</p>';
 }
 window.receiveNativeJournal=value=>{data=value;if(state.view==='driving')render();};
 driving=function(){
   const trips=data?.trips||[],trip=trips.find(t=>t.id===selected)||trips[0];
   const points=(trip?.points||[]).filter(p=>Number.isFinite(p.latitude)&&Number.isFinite(p.longitude));
   return `<section class="gl-journal"><div class="gl-journal-heading"><div><small>MY DRIVE JOURNAL</small><h1>내가 달린 길</h1><p>${esc(data?.status||'주행 기록을 불러오고 있어요.')}</p></div><button data-native-route="library">주행 기록 설정 ↗</button></div>${trip?`<div class="gl-journal-layout"><aside>${trips.map(t=>`<button data-trip="${esc(t.id)}" class="${t.id===trip.id?'selected':''}"><b>${date(t.startedAtMs)}</b><span>${Math.max(0,Math.round((t.endedAtMs-t.startedAtMs)/60000))}분의 드라이브 · ${t.points.length}개 위치</span></button>`).join('')}</aside><article><div class="gl-journal-endpoints"><div><small>출발 · ${date(trip.startedAtMs)}</small><b>${points.length?coord(points[0]):'위치 없음'}</b></div><span>→</span><div><small>도착 · ${date(trip.endedAtMs)}</small><b>${points.length?coord(points[points.length-1]):'위치 없음'}</b></div></div>${map(points,trip)}<p class="gl-journal-caption">© OpenStreetMap contributors</p></article></div>`:`<div class="gl-journal-empty"><span>↗</span><h2>${data?'아직 기록된 드라이브가 없어요':'달린 길을 불러오는 중이에요'}</h2><p>계정을 연결하고 원래 앱의 차량 기록을 켜면,<br>주행이 끝난 뒤 출발·도착 위치와 이동 경로가 여기에 남습니다.</p><button data-native-route="library">기록 설정 열기 →</button></div>`}</section>`;
 };
 const previous=render;render=function(){previous();const nav=document.querySelector('#navigation');if(nav&&!nav.querySelector('[data-view="driving"]'))nav.insertAdjacentHTML('beforeend','<button data-view="driving" aria-label="주행 기록" title="주행 기록" class="'+(state.view==='driving'?'active':'')+'">'+icon('route')+'<span style="display:block;font-size:10px;margin-top:3px">Drive</span></button>');if(state.view==='driving'){view.classList.add('light');document.querySelector('#gear').textContent='P';document.title='Glovebox · 주행 기록';}};
 document.addEventListener('click',e=>{const b=e.target.closest('[data-trip]');if(b){e.stopImmediatePropagation();selected=b.dataset.trip;render();}},true);
})();
