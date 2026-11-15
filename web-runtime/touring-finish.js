function finishTouring(){
 const x=m();
 document.querySelectorAll('#navigation button').forEach(b=>{b.insertAdjacentHTML('beforeend',`<span class="nav-caption">${b.getAttribute('aria-label')}</span>`);if(b.classList.contains('active'))b.setAttribute('aria-current','page')});
 const summary=document.querySelector('.home-story');
 if(summary)summary.insertAdjacentHTML('beforeend',`<div class="story-caption"><span>MEMORY ${String(x.id+1).padStart(2,'0')}</span><i></i><span>${x.season}의 풍경</span><i></i><span>사진 ${x.photos.length}장</span></div>`);
 const dashSub=document.querySelector('.dash-sub');
 if(dashSub)dashSub.innerHTML=`<span>${icon('bookmark')} 보관 ${state.saved.size}</span><span>${icon('album')} 사진 ${memories.reduce((sum,item)=>sum+item.photos.length,0)}</span><span>${icon('music')} 기억의 음악</span>`;
 const seasons=document.querySelectorAll('.seasons span');seasons.forEach((el,i)=>{const values=[3,4,5,2,1];el.style.setProperty('--season-fill',values[i]/5*100+'%');el.classList.toggle('current-season',['봄','여름','가을','겨울','밤'][i]===x.season)});
 const hero=document.querySelector('.hero-picture');
 if(hero){hero.insertAdjacentHTML('beforeend',`<button class="photo-step photo-step-back" data-photo="${(state.photo+x.photos.length-1)%x.photos.length}" aria-label="이전 사진">‹</button><button class="photo-step photo-step-next" data-photo="${(state.photo+1)%x.photos.length}" aria-label="다음 사진">›</button>`)}
 const side=document.querySelector('.detail-right');
 if(side)side.insertAdjacentHTML('beforeend',`<section class="memory-contact-sheet"><div class="section-eyebrow">앨범 사진 <span>${String(x.photos.length).padStart(2,'0')}</span></div><div>${x.photos.map((n,i)=>`<button data-photo="${i}" class="${i===state.photo?'selected':''}" aria-label="사진 ${i+1} 보기"><img src="${photo(n)}" alt="${x.title} 사진 ${i+1}"><span>${String(i+1).padStart(2,'0')}</span></button>`).join('')}</div></section>`);
 document.querySelectorAll('.memory-card .metadata').forEach(el=>{const id=Number(el.closest('.memory-card').dataset.memory);el.innerHTML=`<span>${icon('album')} ${memories[id].photos.length}장의 사진</span><span>${icon('music')} ${memories[id].song}</span>`});
 const archiveHead=document.querySelector('.archive-head');
 if(archiveHead)archiveHead.insertAdjacentHTML('afterend',`<div class="archive-index"><span>${state.filter==='saved'?'보관한 순간':state.filter==='hidden'?'숨긴 순간':'나의 모든 순간'} <i></i> 사진을 선택해 기억 열기</span></div>`);
 document.querySelectorAll('.route-card').forEach((card,i)=>{
  const memory=memories[Number(card.dataset.memory)];
  card.insertAdjacentHTML('afterbegin',`<span class="route-number">JOURNEY <b>${String(i+1).padStart(2,'0')}</b><small>${memory.season} / 예시 코스</small></span>`);
  const copy=card.querySelector('div');
  copy.insertAdjacentHTML('beforeend',`<div class="route-film">${memory.photos.map((n,j)=>`<figure><img src="${photo(n)}" alt="코스에 담긴 풍경 ${j+1}"><figcaption><i></i>SCENE ${String(j+1).padStart(2,'0')}</figcaption></figure>`).join('')}</div><div class="route-meta"><span>${icon('album')} 사진 ${memory.photos.length}장</span><span>${icon('music')} ${memory.song}</span></div>`);
 });
 const consoleEl=document.querySelector('.music-console');
 if(consoleEl){consoleEl.querySelector('.waveform').insertAdjacentHTML('afterend','<div class="scrubber"><label class="sr-only" for="seek-track">음악 재생 위치</label><input id="seek-track" type="range" min="0" max="100" step="0.1" value="0" disabled><div><span class="seek-current">0:00</span><span class="seek-duration"></span></div></div>');}
 const playlist=document.querySelector('.playlist');
 if(playlist)playlist.insertAdjacentHTML('beforeend',`<div class="playlist-foot"><span></span><span>${String(x.id+1).padStart(2,'0')} / 15</span></div>`);
 const driveNotice=document.querySelector('.drive-notice');
 if(driveNotice)driveNotice.insertAdjacentHTML('afterbegin',`<div class="drive-memory-stamp"><span></span><b>${String(x.id+1).padStart(2,'0')}</b></div>`);
 const drivePlayer=document.querySelector('.drive-player');
 if(drivePlayer)drivePlayer.insertAdjacentHTML('beforeend','<div class="progress drive-progress"><span></span></div><div class="drive-player-meta"><span></span><span class="elapsed">0:00</span></div>');
 const profile=document.querySelector('.profile-caption');
 if(profile)profile.insertAdjacentHTML('beforeend',`<div class="profile-location">${icon('route')} ${x.season} · 사진 ${x.photos.length}장</div>`);
 applyPhotoFraming();updateTouringProgress();
}
function updateTouringProgress(){
 if(window.glTimeline){window.glTimeline();return;}
 const seek=document.querySelector('#seek-track');if(!seek)return;
 const duration=audio.duration,time=audio.currentTime||0,valid=Number.isFinite(duration)&&duration>0;
 const format=t=>`${Math.floor(t/60)}:${String(Math.floor(t%60)).padStart(2,'0')}`;
 seek.disabled=!valid;seek.value=valid?time/duration*100:0;seek.style.setProperty('--played',seek.value+'%');
 document.querySelector('.seek-current').textContent=format(time);
 document.querySelector('.seek-duration').textContent=valid?format(duration):'';
}
document.addEventListener('input',e=>{if(!window.glTimeline&&e.target.id==='seek-track'&&Number.isFinite(audio.duration))audio.currentTime=audio.duration*Number(e.target.value)/100});
audio.addEventListener('timeupdate',updateTouringProgress);audio.addEventListener('loadedmetadata',updateTouringProgress);
const previousTouringRefine=refineCockpit;
refineCockpit=function(){previousTouringRefine();finishTouring()};
finishTouring();
