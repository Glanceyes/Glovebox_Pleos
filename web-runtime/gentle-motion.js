/* Short transitions only; input, scrolling, media, and saved state stay synchronous. */
(()=>{
 const preference=matchMedia('(prefers-reduced-motion: reduce)');
 const reduced=()=>preference.matches||document.documentElement.classList.contains('reduce-motion');
 let running=[];
 const stop=()=>{running.forEach(a=>a.cancel());running=[]};
 const reveal=(el,delay=0,distance=10,duration=380)=>{
  if(!el||reduced())return;
  const a=el.animate([{opacity:0,transform:`translateY(${distance}px)`},{opacity:1,transform:'translateY(0)'}],{duration,delay,easing:'cubic-bezier(.2,.75,.25,1)',fill:'backwards'});
  running.push(a);a.finished.then(()=>{running=running.filter(x=>x!==a)},()=>{});
 };
 let prior={view:state.view,selected:state.selected,photo:state.photo,filter:state.filter};
 function enter(before){
  if(reduced())return;
  if(state.view==='detail')return; // One compositor-only detail transition owns this screen.
  if(before&&before.view===state.view&&before.selected===state.selected){
   if(before.photo!==state.photo){reveal(document.querySelector('.hero-picture>img'),0,0,260);return}
   if(before.filter===state.filter)return;
  }
  reveal(view.firstElementChild,0,0,220);
  const groups=state.view==='home'?'.home-story,.coordinates,.home-bubbles,.dashboard,.music-widget,.tile':state.view==='settings'?'.profile-panel,.preference-card':state.view==='archive'?'.memory-card':state.view==='detail'?'.detail-intro,.hero-picture,.detail-right':state.view==='routes'?'.route-card':'.music-cover,.music-console,.playlist,.drive-notice,.drive-player';
  view.querySelectorAll(groups).forEach((el,i)=>reveal(el,Math.min(i,6)*35,9,380));
 }
 const baseRender=render;
 render=function(){const before=prior;stop();baseRender();enter(before);prior={view:state.view,selected:state.selected,photo:state.photo,filter:state.filter}};
 const check=()=>{if(reduced())stop()};
 new MutationObserver(check).observe(document.documentElement,{attributes:true,attributeFilter:['class']});
 preference.addEventListener('change',check);
 enter(null);
})();
