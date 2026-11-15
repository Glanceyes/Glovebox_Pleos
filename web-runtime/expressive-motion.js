(()=>{
 const media=matchMedia('(prefers-reduced-motion: reduce)');
 const reduced=()=>media.matches||document.documentElement.classList.contains('reduce-motion');
 const active=new Set();
 function animate(el,frames,options={}){
  if(!el||reduced())return;
  const a=el.animate(frames,{duration:650,easing:'cubic-bezier(.18,.7,.2,1)',fill:'backwards',...options});
  active.add(a);a.finished.then(()=>active.delete(a),()=>active.delete(a));return a;
 }
 const rise=(selector,delay=0)=>view.querySelectorAll(selector).forEach((el,i)=>animate(el,[{opacity:0,translate:'0 12px'},{opacity:1,translate:'0 0'}],{delay:delay+Math.min(i,8)*45}));
 function decorate(){
  view.querySelectorAll('.dial').forEach(d=>{if(!d.querySelector('.instrument-orbit'))d.insertAdjacentHTML('beforeend','<i class="instrument-orbit" aria-hidden="true"></i>')});
  view.querySelectorAll('.waveform i').forEach((el,i)=>{el.style.setProperty('--wave-speed',650+(i*137)%800+'ms');el.style.setProperty('--wave-delay',-(i*173)%1000+'ms')});
 }
 function entrance(){
  if(reduced())return;
  if(state.view==='detail')return;
  rise('.home-story h1,.cover-copy h1,.preferences-heading h1,.archive-head h1',90);
  rise('.home-story>p,.story-caption,.cover-copy>p',180);
  view.querySelectorAll('.bubble').forEach((el,i)=>animate(el,[{opacity:0,scale:'.8'},{opacity:1,scale:'1'}],{delay:160+i*65,duration:550}));
  rise('.dash-info p,.dash-sub,.seasons span',250);
  view.querySelectorAll('.dial>b,.dial>small').forEach((el,i)=>animate(el,[{opacity:0,translate:'0 8px',filter:'blur(3px)'},{opacity:1,translate:'0 0',filter:'blur(0)'}],{delay:250+i*100,duration:650}));
  animate(view.querySelector('.compass svg'),[{rotate:'-22deg',opacity:.2},{rotate:'0deg',opacity:1}],{duration:1100});
  view.querySelectorAll('.album-stack img').forEach((el,i)=>animate(el,[{opacity:0,scale:'.9',translate:'0 14px'},{opacity:1,scale:'1',translate:'0 0'}],{delay:120+i*90,duration:850}));
  rise('.tile-index,.tile h3,.tile small',180);
  rise('.thumbs button,.memory-contact-sheet button,.route-film figure',180);
  rise('.preference-title,.setting-metrics button,.connection-details>div,.appearance-preview',100);
  rise('.playlist .track',100);
  const photo=view.querySelector('.hero-picture>img');
  animate(photo,[{opacity:.2,scale:'1.025'},{opacity:1,scale:'1'}],{duration:850});
  animate(view.querySelector('.music-cover'),[{opacity:.3,scale:'1.018'},{opacity:1,scale:'1'}],{duration:1000});
 }
 let signature=[state.view,state.selected,state.photo,state.filter].join(':');
 const previous=render;
 render=function(){
  const next=[state.view,state.selected,state.photo,state.filter].join(':');
  active.forEach(a=>{if(!a.effect?.target?.classList?.contains('motion-feedback'))a.cancel()});
  previous();decorate();if(next!==signature)entrance();signature=next;
 };
 // Capture before rendering replaces the clicked element. Coordinates follow the scaled app.
 document.addEventListener('click',e=>{
  const button=e.target.closest('button');if(!button||reduced())return;
  const bounds=app.getBoundingClientRect(),rect=button.getBoundingClientRect(),scale=bounds.width/app.offsetWidth;
  const x=((e.detail?e.clientX:rect.x+rect.width/2)-bounds.x)/scale;
  const y=((e.detail?e.clientY:rect.y+rect.height/2)-bounds.y)/scale;
  const ring=document.createElement('i');ring.className='motion-feedback';ring.setAttribute('aria-hidden','true');ring.style.left=x-15+'px';ring.style.top=y-15+'px';app.append(ring);
  const a=animate(ring,[{opacity:.7,scale:'.25'},{opacity:0,scale:'2.4'}],{duration:480});
  if(a)a.finished.then(()=>ring.remove(),()=>ring.remove());else ring.remove();
 },true);
 document.addEventListener('input',e=>{
  if(!e.target.matches('input[type=range]'))return;
  const output=e.target.id==='volume'?document.querySelector('#volume-value'):document.querySelector('.seek-current');
  if(output&&!output.getAnimations().length)animate(output,[{scale:'1.08',color:'#ffe3ad'},{scale:'1',color:getComputedStyle(output).color}],{duration:250});
 });
 function stop(){if(reduced()){active.forEach(a=>a.cancel());document.querySelectorAll('.motion-feedback').forEach(e=>e.remove())}}
 new MutationObserver(stop).observe(document.documentElement,{attributes:true,attributeFilter:['class']});media.addEventListener('change',stop);
 decorate();entrance();
 animate(document.querySelector('header'),[{opacity:0,translate:'0 -8px'},{opacity:1,translate:'0 0'}],{duration:700});
 animate(document.querySelector('footer'),[{opacity:0,translate:'0 8px'},{opacity:1,translate:'0 0'}],{duration:700,delay:120});
})();
