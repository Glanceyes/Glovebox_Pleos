/* Motion follows state changes; it never delays application actions. */
(()=>{
 const media=matchMedia('(prefers-reduced-motion: reduce)');
 const reduced=()=>media.matches||document.documentElement.classList.contains('reduce-motion');
 const running=new Set();
 let observer=null;
 function move(el,frames,options={}){
  if(!el||reduced()||document.hidden)return;
  const animation=el.animate(frames,{duration:540,easing:'cubic-bezier(.16,.75,.2,1)',...options});
  running.add(animation);
  animation.finished.then(()=>running.delete(animation),()=>running.delete(animation));
  return animation;
 }
 function snapshot(){return {screen:state.view,photo:state.photo,id:state.selected,saved:state.saved.has(state.selected)}}
 let before=snapshot();
 function decorate(){
  observer?.disconnect();
  if(reduced())return;
  observer=new IntersectionObserver(entries=>entries.forEach(entry=>{
   if(!entry.isIntersecting)return;
   const el=entry.target;observer.unobserve(el);
   // Only newly scrolled-in content; existing entrance animations handle initial cards.
   if(el.dataset.motionWaiting==='true')move(el,[{opacity:.2,translate:'0 16px'},{opacity:1,translate:'0 0'}],{duration:580});
  }),{threshold:.15});
  view.querySelectorAll('.memory-card,.track,.route-card').forEach(el=>{
   const r=el.getBoundingClientRect();
   el.dataset.motionWaiting=String(r.top>innerHeight-60||r.bottom<0);
   observer.observe(el);
  });
 }
 const base=render;
 render=function(){
  const prior=before;
  const nav=document.querySelector('#navigation .active')?.getBoundingClientRect();
  running.forEach(a=>a.cancel());
  base();
  const next=snapshot();
  if(prior.screen!==next.screen){
   const active=document.querySelector('#navigation .active');
   if(active&&nav){const rect=active.getBoundingClientRect(),scale=app.getBoundingClientRect().width/app.offsetWidth;
    move(active,[{translate:`${(nav.x-rect.x)/scale}px 0`,filter:'brightness(1.5)'},{translate:'0 0',filter:'brightness(1)'}],{duration:420});
   }
   move(view.querySelector('.dashboard .progress span'),[{transform:'scaleX(0)'},{transform:'scaleX(1)'}],{duration:1000,delay:200});
   view.querySelectorAll('.profile-summary>div,.drive-steps>span').forEach((el,i)=>move(el,[{clipPath:'inset(100% 0 0 0)'},{clipPath:'inset(0 0 0 0)'}],{delay:100+i*70,duration:650}));
  }
  if(prior.screen===next.screen&&prior.id===next.id&&prior.photo!==next.photo){
   // The detail transition owns the large photo; avoid full-image clip-path repaints.
   move(view.querySelector('.photo-count'),[{translate:'0 8px',opacity:0},{translate:'0 0',opacity:1}],{duration:320});
   view.querySelectorAll('[data-photo].active').forEach(el=>move(el,[{scale:'.91'},{scale:'1.04',offset:.65},{scale:'1'}],{duration:440}));
  }
  if(prior.id===next.id&&prior.saved!==next.saved){
   move(view.querySelector('[data-action=save]'),[{scale:'.96'},{scale:'1.025',offset:.55},{scale:'1'}],{duration:550});
   move(view.querySelector('.dashboard .progress span'),[{transform:'scaleX(.85)'},{transform:'scaleX(1)'}],{duration:650});
  }
  before=next;decorate();
 };
 // Dialog and confirmation messages have their own entrances, independent of navigation.
 const dialog=document.querySelector('#dialog'),toast=document.querySelector('#toast');
 new MutationObserver(()=>{if(dialog.open)move(dialog,[{opacity:0,translate:'0 18px',scale:'.97'},{opacity:1,translate:'0 0',scale:'1'}],{duration:420})}).observe(dialog,{attributes:true,attributeFilter:['open']});
 new MutationObserver(()=>{if(!toast.hidden){toast.getAnimations().forEach(a=>a.cancel());move(toast,[{opacity:0,translate:'0 12px',scale:'.97'},{opacity:1,translate:'0 0',scale:'1'}],{duration:350})}}).observe(toast,{attributes:true,attributeFilter:['hidden'],childList:true});
 document.addEventListener('change',event=>{
  const input=event.target;
  if(input.matches('input[type=checkbox],input[type=radio],select')){
   const row=input.closest('label,.motion-setting,.setting-row,.preference-card');
   move(row,[{filter:'brightness(1.16)'},{filter:'brightness(1)'}],{duration:480});
  }
 });
 function stop(){if(reduced()||document.hidden){running.forEach(a=>a.cancel());observer?.disconnect()}}
 new MutationObserver(()=>{stop();if(!reduced())decorate()}).observe(document.documentElement,{attributes:true,attributeFilter:['class']});
 media.addEventListener('change',()=>{stop();if(!reduced())decorate()});
 document.addEventListener('visibilitychange',()=>{stop();if(!document.hidden)decorate()});
 decorate();
})();
