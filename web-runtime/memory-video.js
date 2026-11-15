(()=>{
 const media=matchMedia('(prefers-reduced-motion: reduce)');
 let userPaused=false;
 let sceneVideo=null;
 const reduced=()=>media.matches||document.documentElement.classList.contains('reduce-motion');
 function attach(){
  if(m()?.nativeId!=='coast-2022'||!['home','detail'].includes(state.view))return;
  const host=state.view==='home'?view.querySelector('.home'):view.querySelector('.hero-picture');
  if(!host||host.querySelector('video'))return;
  // In detail, keep all three original photos available; only first scene has a video.
  if(state.view==='detail'&&state.photo!==0)return;
  host.classList.add('has-memory-video');
  const video=sceneVideo||(sceneVideo=document.createElement('video'));video.className='memory-video';if(!video.getAttribute('src'))video.src='assets/display/jumunjin-dawn.mp4';video.muted=true;video.loop=true;video.playsInline=true;video.preload='metadata';video.poster=photo(17);video.setAttribute('aria-label','주문진 새벽바다 배경 영상');
  host.prepend(video);
  const controls=document.createElement('div');controls.className='video-controls';controls.innerHTML='<span>주문진 · 새벽바다 영상</span><button type="button" data-video-toggle>영상 재생</button>';
  host.append(controls);
  const button=controls.querySelector('button');
  const label=()=>{button.textContent=video.paused?'영상 재생':'영상 일시정지';button.setAttribute('aria-pressed',String(!video.paused))};
  video.onplay=label;video.onpause=label;
  video.onerror=()=>{video.remove();controls.remove();host.classList.remove('has-memory-video');notify('영상을 불러오지 못해 원본 사진을 표시합니다.')};
  button.addEventListener('click',()=>{if(video.paused){userPaused=false;video.play().catch(label)}else{userPaused=true;video.pause()}});
  if(!userPaused&&!reduced()&&!document.hidden)video.play().catch(label);
  label();
 }
 const previous=render;
 render=function(){if(sceneVideo){sceneVideo.remove();if(m()?.nativeId!=='coast-2022'||!['home','detail'].includes(state.view)||(state.view==='detail'&&state.photo!==0))sceneVideo.pause()}previous();attach()};
 const pauseForPreference=()=>{if(reduced())document.querySelectorAll('.memory-video').forEach(v=>v.pause())};
 new MutationObserver(pauseForPreference).observe(document.documentElement,{attributes:true,attributeFilter:['class']});media.addEventListener('change',pauseForPreference);
 document.addEventListener('visibilitychange',()=>document.querySelectorAll('.memory-video').forEach(v=>{if(document.hidden)v.pause();else if(!userPaused&&!reduced())v.play().catch(()=>{})}));
 attach();
 if(new URLSearchParams(location.search).get('memory')==='0')select(0,'home');
})();
