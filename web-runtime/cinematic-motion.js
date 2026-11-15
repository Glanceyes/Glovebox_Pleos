/* Additional decorative choreography, without changing data or media controls. */
(()=>{
 const preference=matchMedia('(prefers-reduced-motion: reduce)');
 const reduced=()=>preference.matches||document.documentElement.classList.contains('reduce-motion');
 const animations=new Set();let pointerFrame=0;
 function animate(el,keyframes,options={}){
  if(!el||reduced()||document.hidden)return;
  const a=el.animate(keyframes,{duration:700,easing:'cubic-bezier(.16,.8,.2,1)',...options});
  animations.add(a);a.finished.then(()=>animations.delete(a),()=>animations.delete(a));return a;
 }
 function stagger(selector,frames,delay=45,duration=650){view.querySelectorAll(selector).forEach((el,i)=>animate(el,frames,{delay:Math.min(i,12)*delay,duration}))}
 function decorate(){
  view.querySelectorAll('.dial').forEach(el=>{
   if(!el.querySelector('.rim-comet'))el.insertAdjacentHTML('beforeend','<i class="rim-comet" aria-hidden="true"></i><i class="rim-glint" aria-hidden="true"></i>');
  });
  view.querySelectorAll('.dashboard,.music-widget,.music-console,.preference-card,.drive-player').forEach(el=>{
   el.classList.add('motion-surface');
   if(!el.querySelector(':scope>.surface-shimmer'))el.insertAdjacentHTML('beforeend','<i class="surface-shimmer" aria-hidden="true"></i>');
  });
  view.querySelectorAll('.tile,.memory-card,.route-card,.track,.bubble').forEach((el,i)=>el.style.setProperty('--motion-order',i%8));
  view.querySelectorAll('.waveform i,.sound-meter i').forEach((el,i)=>{
   el.style.animationDuration=(1800+(i*197)%1100)+'ms';el.style.animationDelay=(-(i*283)%2400)+'ms';
  });
 }
 const reveal=[{clipPath:'inset(0 100% 0 0)'},{clipPath:'inset(0 0 0 0)'}];
 function enter(){
  if(reduced())return;
  if(state.view==='detail')return;
  stagger('.coordinates b,.place,.place-tag',reveal,45,850);
  stagger('.detail-intro .label,.archive-head .label,.preferences-heading .label,.drive-label',reveal,60,800);
  stagger('.seasons span b,.setting-metrics strong,.profile-summary b',[{opacity:0,scale:'.65'},{opacity:1,scale:'1.08',offset:.7},{opacity:1,scale:'1'}],70,800);
  stagger('.photo-picker h3,.side-panel h3,.other-memories h3,.playlist h3',[{letterSpacing:'.12em',opacity:0},{letterSpacing:'0em',opacity:1}],50,750);
  stagger('.tile-indicator i',[{opacity:.1,scaleX:'.2'},{opacity:1,scaleX:'1'}],55,500);
  stagger('.detail-controls button,.side-buttons button,.drive-notice button',[{opacity:0,translate:'12px 0'},{opacity:1,translate:'0 0'}],65,700);
  stagger('.volume-scale span,.appearance-meta span,.setting-note',[{opacity:0},{opacity:1}],70,900);
  stagger('.connection-strip,.connection-details,.saved-preview-row',reveal,60,750);
  stagger('.theme-chips i',[{scale:'.3',rotate:'-35deg'},{scale:'1.1',rotate:'8deg',offset:.7},{scale:'1',rotate:'0deg'}],100,650);
  stagger('.drive-info p',[{opacity:0,translate:'-12px 0'},{opacity:1,translate:'0 0'}],90,700);
  stagger('.other-row button img',[{clipPath:'inset(0 0 100% 0)'},{clipPath:'inset(0 0 0 0)'}],75,850);
  stagger('.thumbs button img,.hero-thumbs button img',[{filter:'brightness(.55)'},{filter:'brightness(1)'}],65,900);
  stagger('.setting-badge,.local-status',[{opacity:0,scale:'.85'},{opacity:1,scale:'1'}],100,700);
  stagger('.album-stack img',[{rotate:'-5deg'},{rotate:'1deg',offset:.75},{rotate:'0deg'}],50,1100);
  stagger('.route-card .label',[{translate:'-10px 0',opacity:0},{translate:'0 0',opacity:1}],90,900);
  animate(view.querySelector('.profile-photo'),[{backgroundPosition:'50% 35%'},{backgroundPosition:'50% 50%'}],{duration:1700});
  animate(view.querySelector('.drive-notice'),[{clipPath:'inset(0 0 100% 0 round 20px)'},{clipPath:'inset(0 0 0 0 round 20px)'}],{duration:950});
 }
 let signature=[state.view,state.selected,state.filter].join(':');
 const base=render;
 render=function(){animations.forEach(a=>a.cancel());base();decorate();const next=[state.view,state.selected,state.filter].join(':');if(next!==signature)enter();signature=next};
 // Very small card tilt; limited to hover-capable pointers and never blocks clicks.
 view.addEventListener('pointermove',e=>{
  if(e.pointerType==='touch'||reduced()||pointerFrame)return;
  const card=e.target.closest('.tile,.memory-card,.route-card');if(!card)return;
  const rect=card.getBoundingClientRect(),fraction=(e.clientX-rect.left)/rect.width-.5;
  pointerFrame=requestAnimationFrame(()=>{if(card.isConnected)card.style.rotate=(fraction*.9)+'deg';pointerFrame=0});
 },{passive:true});
 view.addEventListener('pointerout',e=>{const card=e.target.closest('.tile,.memory-card,.route-card');if(card&&!card.contains(e.relatedTarget))card.style.rotate='0deg'},{passive:true});
 document.addEventListener('pointerdown',e=>{
  const button=e.target.closest('button');
  if(button&&!button.disabled)animate(button,[{scale:'1'},{scale:'.95',offset:.4},{scale:'1'}],{duration:260});
 },{passive:true});
 document.addEventListener('focusin',e=>{if(e.target.matches('input[type=search],.search-input'))animate(e.target,[{boxShadow:'0 0 0 0 #ecc58800'},{boxShadow:'0 0 0 5px #ecc58828',offset:.5},{boxShadow:'0 0 0 0 #ecc58800'}],{duration:650})});
 document.addEventListener('click',e=>{
  const button=e.target.closest('[data-action=rewind],[data-action=forward]');
  if(button)animate(view.querySelector('.elapsed'),[{opacity:.25,translate:button.dataset.action==='forward'?'10px 0':'-10px 0'},{opacity:1,translate:'0 0'}],{duration:450});
 });
 let playing=state.playing;const playback=updatePlayback;
 updatePlayback=function(){playback();if(playing!==state.playing){
  document.querySelectorAll('.album-stack,.music-cover,.setting-track>img,#mini-cover').forEach(el=>animate(el,[{filter:'brightness(.8)'},{filter:'brightness(1.13)',offset:.45},{filter:'brightness(1)'}],{duration:700}));
  document.querySelectorAll('.waveform,.sound-meter').forEach(el=>animate(el,[{opacity:.25},{opacity:1}],{duration:600}));
  playing=state.playing;
 }};
 function sync(){
  app.classList.toggle('motion-suspended',document.hidden);
  if(reduced()||document.hidden){animations.forEach(a=>a.cancel());if(pointerFrame){cancelAnimationFrame(pointerFrame);pointerFrame=0}view.querySelectorAll('[style]').forEach(el=>{if(el.style.rotate)el.style.rotate='0deg'})}
 }
 new MutationObserver(sync).observe(document.documentElement,{attributes:true,attributeFilter:['class']});
 preference.addEventListener('change',sync);document.addEventListener('visibilitychange',sync);
 decorate();enter();sync();
})();
