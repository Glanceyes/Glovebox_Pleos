// Clip the source's embedded letterbox in the renderer, keeping the original intact.
function applyPhotoFraming(){
 document.querySelectorAll('[style*="photo-17.jpeg"],[style*="photo-17.webp"]').forEach(el=>{
  const scale=Math.max(el.clientWidth/3840,el.clientHeight/2022);
  el.style.backgroundSize=`${3840*scale}px ${2160*scale}px`;
  el.style.backgroundPosition='center';
 });
 const home=document.querySelector('.home');
 if(home&&m().photos[0]===17){const scale=Math.max(home.clientWidth/3840,home.clientHeight/2022);home.style.setProperty('--framed-background',`${3840*scale}px ${2160*scale}px`)}
}
function refineSettings(){
 const cards=document.querySelectorAll('.preference-card');if(!cards.length)return;
 const saved=[...state.saved].filter(id=>memories[id]);
 cards[0].querySelector('.setting-note').insertAdjacentHTML('beforebegin',`<div class="saved-preview"><div class="settings-section-label">보관한 풍경 <span>${saved.length} / 15</span></div><div class="saved-preview-row">${saved.slice(0,4).map(id=>`<button data-memory="${id}" data-target="detail" title="${memories[id].title}"><img src="${photo(memories[id].photos[0])}" alt="${memories[id].title}"><span>${memories[id].title}</span></button>`).join('')||'<span class="no-saved">마음에 드는 기억을 보관하면 여기에 나타납니다.</span>'}</div></div>`);
 cards[1].insertAdjacentHTML('beforeend',`<div class="sound-presets"><span>빠른 조절</span>${[25,50,75].map(v=>`<button data-volume-preset="${v}" aria-label="음량 ${v}퍼센트">${v}%</button>`).join('')}<button id="mute-control" aria-pressed="${audio.muted}">${audio.muted?'음소거 해제':'음소거'}</button></div><div class="audio-state"><span class="audio-state-dot"></span><span id="sound-state">${audio.paused?'재생 대기 중':'음악 재생 중'}</span><span>로컬 시연 음원</span></div>`);
 cards[2].querySelector('p').outerHTML=`<div class="connection-details"><div><span>위치 정보</span><b>수신 없음</b><i></i></div><div><span>기어 · 주행 상태</span><b>수신 없음</b><i></i></div><div><span>사진 · 음악</span><b class="available">로컬 파일 사용 가능</b><i class="available"></i></div></div><p class="connection-caption">차량 연결 없이 주행 화면을 미리 볼 수 있습니다.</p>`;
 cards[3].querySelector('.appearance-meta').insertAdjacentHTML('afterend',`<div class="motion-setting"><div><b>화면 움직임 줄이기</b><small>파형 애니메이션과 전환 효과</small></div><button id="motion-control" role="switch" aria-checked="${document.documentElement.classList.contains('reduce-motion')}" aria-label="화면 움직임 줄이기"><i></i></button></div>`);
 updateSettingSound();
}
function updateSettingSound(){
 const range=document.querySelector('#volume'),output=document.querySelector('#volume-value'),mute=document.querySelector('#mute-control'),status=document.querySelector('#sound-state');
 const v=Math.round(audio.volume*100);
 if(range){range.value=audio.volume;range.style.setProperty('--volume',v+'%')}
 if(output)output.innerHTML=`${v}<small>%</small>`;
 if(mute){mute.setAttribute('aria-pressed',String(audio.muted));mute.textContent=audio.muted?'음소거 해제':'음소거'}
 if(status)status.textContent=audio.muted?'음소거 중':audio.paused?'재생 대기 중':'음악 재생 중';
 document.querySelectorAll('[data-volume-preset]').forEach(b=>b.setAttribute('aria-pressed',String(Number(b.dataset.volumePreset)===v)));
}
document.addEventListener('click',event=>{
 const b=event.target.closest('button');if(!b)return;
 if(b.dataset.volumePreset){audio.volume=Number(b.dataset.volumePreset)/100;audio.muted=false;updateSettingSound()}
 if(b.id==='mute-control'){audio.muted=!audio.muted;updateSettingSound()}
 if(b.id==='motion-control'){const enabled=document.documentElement.classList.toggle('reduce-motion');b.setAttribute('aria-checked',String(enabled));try{localStorage.setItem('glovebox-reduced-motion',String(enabled))}catch{}}
});
try{document.documentElement.classList.toggle('reduce-motion',localStorage.getItem('glovebox-reduced-motion')==='true')}catch{}
document.querySelector('#audio').addEventListener('volumechange',updateSettingSound);
document.querySelector('#audio').addEventListener('play',updateSettingSound);
document.querySelector('#audio').addEventListener('pause',updateSettingSound);
addEventListener('resize',()=>requestAnimationFrame(applyPhotoFraming));
const baseRefineCockpit=refineCockpit;
refineCockpit=function(){baseRefineCockpit();applyPhotoFraming();refineSettings()};
applyPhotoFraming();refineSettings();
