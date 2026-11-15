(()=>{
 const preference=matchMedia('(prefers-reduced-motion: reduce)');
 const reduced=()=>preference.matches||document.documentElement.classList.contains('reduce-motion');
 const effects=new Set();
 function tween(el,frames,options={}){if(!el||reduced())return;const a=el.animate(frames,{duration:480,easing:'cubic-bezier(.2,.7,.2,1)',...options});effects.add(a);a.finished.then(()=>effects.delete(a),()=>effects.delete(a));return a}
 function enhance(){view.querySelectorAll('.tile,.memory-card,.route-card').forEach(card=>{if(!card.querySelector('.card-light'))card.insertAdjacentHTML('beforeend','<i class="card-light" aria-hidden="true"></i>')})}
 let selected=state.selected;
 const base=render;
 render=function(){
  const old=selected;effects.forEach(a=>a.cancel());base();enhance();
  if(old!==state.selected&&!reduced()){
   const home=view.querySelector('.home');
   if(home&&memories[old]?.photos.length){const layer=document.createElement('div');layer.className='memory-transition';layer.style.backgroundImage=`linear-gradient(#20243135,#17212b77),url('${photo(memories[old].photos[0])}')`;home.append(layer);const a=tween(layer,[{opacity:1},{opacity:0}],{duration:900});if(a)a.finished.then(()=>layer.remove(),()=>layer.remove())}
   document.querySelectorAll('#mini-cover,#next-cover').forEach(el=>tween(el,[{opacity:.1,scale:'.9'},{opacity:1,scale:'1'}],{duration:550}));
   document.querySelectorAll('#mini-title,#next-title').forEach(el=>tween(el,[{opacity:0,translate:'0 6px'},{opacity:1,translate:'0 0'}],{duration:450}));
  }
  selected=state.selected;
 };
 let pointerFrame=0;
 view.addEventListener('pointermove',e=>{
  if(reduced()||pointerFrame)return;
  const card=e.target.closest('.tile,.memory-card,.route-card');if(!card)return;
  const rect=card.getBoundingClientRect(),x=(e.clientX-rect.x)/rect.width*100,y=(e.clientY-rect.y)/rect.height*100;
  pointerFrame=requestAnimationFrame(()=>{card.style.setProperty('--light-x',x+'%');card.style.setProperty('--light-y',y+'%');pointerFrame=0});
 },{passive:true});
 const playback=updatePlayback;let wasPlaying=state.playing;
 updatePlayback=function(){playback();if(wasPlaying!==state.playing){document.querySelectorAll('[data-action=play] svg').forEach(el=>tween(el,[{opacity:.3,scale:'.65',rotate:state.playing?'-14deg':'14deg'},{opacity:1,scale:'1',rotate:'0deg'}],{duration:280}));wasPlaying=state.playing}};
 function stop(){if(reduced()){effects.forEach(a=>a.cancel());document.querySelectorAll('.memory-transition').forEach(e=>e.remove())}}
 new MutationObserver(stop).observe(document.documentElement,{attributes:true,attributeFilter:['class']});preference.addEventListener('change',stop);enhance();
})();
