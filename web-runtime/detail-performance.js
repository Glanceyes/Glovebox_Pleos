(()=>{
 const media=matchMedia('(prefers-reduced-motion: reduce)');
 const reduced=()=>media.matches||document.documentElement.classList.contains('reduce-motion');
 const decoded=new Map();let idle=0;const active=new Set();
 // Keep a small decoded-photo cache rather than all original full-resolution images.
 function warm(id){
  for(const n of memories[id].photos){
   if(decoded.has(n))continue;
   const img=new Image();img.decoding='async';img.src=photo(n);decoded.set(n,img);img.decode().catch(()=>{});
  }
  while(decoded.size>9)decoded.delete(decoded.keys().next().value);
 }
 function schedule(){cancelIdleCallback(idle);idle=requestIdleCallback(()=>warm(state.selected),{timeout:800})}
 function fade(el,dy=0){if(!el||reduced())return;const a=el.animate([{opacity:.35,transform:`translateY(${dy}px)`},{opacity:1,transform:'translateY(0)'}],{duration:300,easing:'cubic-bezier(.2,.7,.2,1)'});active.add(a);a.finished.then(()=>active.delete(a),()=>active.delete(a))}
 let old={view:state.view,id:state.selected,photo:state.photo};const base=render;
 render=function(){
  active.forEach(a=>a.cancel());base();
  if(state.view==='detail'){
   view.querySelectorAll('img').forEach(img=>{img.decoding='async'});
   const entry=old.view!=='detail'||old.id!==state.selected;
   if(entry){fade(view.querySelector('.detail-left'),5);fade(view.querySelector('.detail-right'),5)}
   if(entry||old.photo!==state.photo)fade(view.querySelector('.hero-picture>img'));
  }
  old={view:state.view,id:state.selected,photo:state.photo};schedule();
 };
 view.addEventListener('pointerover',e=>{const card=e.target.closest('[data-memory]');if(card)warm(Number(card.dataset.memory))},{passive:true});
 function stop(){if(reduced())active.forEach(a=>a.cancel())}
 media.addEventListener('change',stop);new MutationObserver(stop).observe(document.documentElement,{attributes:true,attributeFilter:['class']});schedule();
})();
