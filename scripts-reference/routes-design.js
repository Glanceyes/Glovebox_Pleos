(()=>{
 const esc=v=>String(v??'').replace(/[&<>"']/g,c=>({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[c]));
 let catalog={courses:[],connected:false},ready=false,selected=null,query='';
 const route=(name)=>window.GloveboxNative.action('courseRoute',name);
 const art=(course)=>{
   const stops=course.stops||[];
   const points=stops.map((s,i)=>`${48+i*430/Math.max(1,stops.length-1)},${75+Math.sin(i*1.9)*28}`);
   return `<svg class="gl-route-line" viewBox="0 0 530 140" aria-label="코스의 장소 순서"><path d="M${points.join(' L')}"/><g>${points.map((p,i)=>`<circle cx="${p.split(',')[0]}" cy="${p.split(',')[1]}" r="5"/><text x="${p.split(',')[0]}" y="${Number(p.split(',')[1])+26}">${String(i+1).padStart(2,'0')}</text>`).join('')}</g></svg>`;
 };
 window.receiveNativeCourses=data=>{catalog=data;ready=true;if(state.view==='routes')render();};
 const detail=course=>`<section class="gl-route-detail"><div class="gl-route-detail-head"><button class="gl-route-back" data-route-back>← 보관한 길</button><span class="gl-route-eyebrow">MY COLLECTION</span><h1>${esc(course.title)}</h1><p>${esc(course.introduction||'이 길에서 만날 장소들을 살펴보세요.')}</p></div><div class="gl-route-detail-body"><div class="gl-route-stops">${(course.stops||[]).map((s,i)=>`<article><span>${String(i+1).padStart(2,'0')}</span><div><h2>${esc(s.name)}</h2><p>${esc(s.note||'이 장소에서 잠시 머물러 보세요.')}</p>${s.music?`<small>♫ ${esc(s.music.title||s.music.track||'함께 듣는 음악')}</small>`:''}</div></article>`).join('')}</div><aside class="gl-route-detail-aside">${art(course)}<small>장소 순서 · 실제 도로 경로는 안내 화면에서 확인</small><h3>이 길을 따라가 볼까요?</h3><p>보관한 장소와 음악을 이어 보며<br>다음 드라이브를 준비하세요.</p><button class="gl-route-primary" data-route-native="course:${esc(course.id)}">코스 안내 열기 <span>↗</span></button></aside></div></section>`;
 routes=function(){
   const courses=catalog.courses||[],course=courses.find(c=>c.id===selected);
   if(course)return detail(course);
   const filtered=courses.filter(c=>(c.title+' '+c.introduction+' '+(c.stops||[]).map(s=>s.name).join(' ')).toLowerCase().includes(query.toLowerCase()));
   return `<section class="gl-routes"><div class="gl-route-hero"><img src="assets/display/photo-17.webp" alt="" class="gl-route-landscape"><div class="gl-route-hero-shade"></div><div class="gl-route-hero-copy"><span class="gl-route-eyebrow">ROUTES WE SHARED</span><h1>함께 나눈 길</h1><p>누군가의 소중한 풍경이,<br>우리의 다음 드라이브가 됩니다.</p><button class="gl-route-primary" data-route-native="explore">새로운 길 둘러보기 <span>↗</span></button></div><div class="gl-route-note"><span class="gl-route-note-line"></span><small>THE NEXT JOURNEY</small><p>좋았던 길은<br>함께 기억하고 싶어서.</p><span>장소 · 사진 · 그날의 음악</span></div><div class="gl-route-hero-foot"><span>GLOVEBOX / TOGETHER</span><span>${catalog.connected?'● 내 계정과 연결됨':'○ 계정 연결로 더 많은 길을 만나세요'}</span></div></div><div class="gl-route-lower"><section class="gl-route-collection"><header><div><span class="gl-route-eyebrow">SAVED JOURNEYS</span><h2>다음에 가고 싶은 길 <small>${ready?courses.length:'—'}</small></h2></div><button class="gl-route-text" data-route-native="shelf">보관함 관리 ↗</button></header>${courses.length?`<label class="gl-route-search">⌕ <input aria-label="보관한 코스 검색" placeholder="장소나 코스 이름으로 찾아보세요" value="${esc(query)}" data-route-search></label>`:''}<div class="gl-route-cards">${catalog.error?`<div class="gl-route-empty"><h3>잠시 길을 불러오지 못했어요</h3><p>${esc(catalog.error)}</p><button class="gl-route-text" data-route-reload>다시 불러오기 ↻</button></div>`:!ready?`<div class="gl-route-empty"><span class="gl-route-loader"></span><p>보관한 길을 불러오고 있어요.</p></div>`:!courses.length?`<div class="gl-route-empty"><span class="gl-route-empty-symbol">↗</span><div><h3>아직 보관한 길이 없어요</h3><p>마음에 드는 길을 만나면 이곳에 담아 두세요.<br>다음 여행을 떠날 때, 다시 꺼내 볼 수 있도록.</p><button class="gl-route-text" data-route-native="explore">함께 나눈 길 찾아보기 →</button></div></div>`:!filtered.length?'<div class="gl-route-empty"><h3>일치하는 길이 없어요</h3><p>다른 장소나 코스 이름으로 검색해 보세요.</p></div>':filtered.map((c,i)=>`<button class="gl-route-card" data-course-id="${esc(c.id)}"><div class="gl-route-card-art"><span>JOURNEY ${String(i+1).padStart(2,'0')}</span>${art(c)}</div><div class="gl-route-card-copy"><small>${(c.stops||[]).length}개의 장소 · ${(c.stops||[]).filter(s=>s.music).length}곡의 음악</small><h3>${esc(c.title)}</h3><p>${esc(c.introduction||'장소와 음악으로 이어지는 드라이브')}</p><span>길 살펴보기 <b>↗</b></span></div></button>`).join('')}</div></section><aside class="gl-route-create"><div class="gl-route-create-photos"><img src="assets/display/photo-04.webp" alt=""><img src="assets/display/photo-08.webp" alt=""></div><span class="gl-route-eyebrow">YOUR STORY, SHARED</span><h2>나만 알던 길을<br>함께 나눠요</h2><p>기억에 남은 장소와 음악으로<br>나만의 드라이브 코스를 만들어 보세요.</p><button class="gl-route-primary" data-route-native="compose">내 경험으로 길 만들기 <span>＋</span></button><button class="gl-route-text" data-native-route="account">${catalog.connected?'내 계정 관리':'내 계정 연결하기'} ↗</button></aside></div></section>`;
 };
 document.addEventListener('click',e=>{
   const b=e.target.closest('button');if(!b)return;
   if(b.dataset.routeNative){e.stopImmediatePropagation();route(b.dataset.routeNative);}
   else if(b.dataset.courseId){e.stopImmediatePropagation();selected=b.dataset.courseId;render();}
   else if(b.hasAttribute('data-route-back')){e.stopImmediatePropagation();selected=null;render();}
   else if(b.hasAttribute('data-route-reload')){e.stopImmediatePropagation();ready=false;render();window.GloveboxNative.action('courses','');}
 },true);
 document.addEventListener('input',e=>{
   if(!e.target.matches('[data-route-search]'))return;
   query=e.target.value;
   const position=e.target.selectionStart;render();const input=document.querySelector('[data-route-search]');input?.focus();input?.setSelectionRange(position,position);
 });
})();
