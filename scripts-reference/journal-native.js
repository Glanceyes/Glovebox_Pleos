(()=>{
 const previous=render;
 render=function(){
  previous();
  const nav=document.querySelector('#navigation');
  if(nav&&!nav.querySelector('[data-view="driving"]'))nav.insertAdjacentHTML('beforeend',`<button data-view="driving" aria-label="주행 기록" title="주행 기록">${icon('route')}<span class="nav-caption">주행 기록</span></button>`);
  if(nav&&!nav.querySelector('[data-map-open]'))nav.insertAdjacentHTML('beforeend',`<button data-map-open aria-label="지도" title="지도"><svg class="icon" viewBox="0 0 32 32" aria-hidden="true"><path d="M4 8 12 5 20 8 28 5V24L20 27 12 24 4 27Z M12 5V24 M20 8V27"/></svg><span class="nav-caption">지도</span></button>`);
  const hero=document.querySelector('.gl-route-hero-copy');
  if(hero&&!hero.querySelector('[data-map-open]'))hero.insertAdjacentHTML('beforeend','<button class="gl-route-primary" data-map-open style="margin-top:12px">지도 · 도로 경로 보기 ↗</button>');
  document.querySelectorAll('.tile[data-view="driving"] h3').forEach(el=>el.textContent='주행 기록');
  document.querySelectorAll('.tile[data-view="driving"] small').forEach(el=>el.textContent='원래 앱의 차량 기록과 경로');
 };
 document.addEventListener('click',e=>{if(e.target.closest('[data-map-open]')){e.stopImmediatePropagation();window.GloveboxNative.action('map','');}},true);
})();
