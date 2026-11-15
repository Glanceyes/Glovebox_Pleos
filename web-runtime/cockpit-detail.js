/* Presentation details only; actions and application state remain in glovebox.js. */
function refineCockpit(){
  const svg=(body,box='0 0 200 200')=>`<svg viewBox="${box}" aria-hidden="true">${body}</svg>`;
  document.querySelectorAll('.dial').forEach(el=>{
    let marks='';
    for(let i=0;i<=60;i++){
      const a=(-225+i*4.5)*Math.PI/180,r=i%5===0?77:82;
      marks+=`<line x1="${100+r*Math.cos(a)}" y1="${100+r*Math.sin(a)}" x2="${100+88*Math.cos(a)}" y2="${100+88*Math.sin(a)}" stroke="${i<21?'#c6ae88':'#6e8796'}" stroke-width="${i%5===0?1.4:.6}"/>`;
    }
    el.insertAdjacentHTML('afterbegin',`<div class="instrument-bezel">${svg(`<circle cx="100" cy="100" r="97" fill="none" stroke="#72818a" stroke-width=".6"/><circle cx="100" cy="100" r="92" fill="none" stroke="#07121b" stroke-width="3"/>${marks}<path d="M 28 155 A 91 91 0 0 1 20 57" fill="none" stroke="#bda580" stroke-width="2"/><path d="M 40 163 L 46 158 M 160 163 L 154 158" stroke="#bdd4e0"/><text x="100" y="181" text-anchor="middle" fill="#8da3af" font-size="6" letter-spacing="2">G L O V E B O X</text>`)}</div>`);
  });
  const compass=document.querySelector('.compass');
  if(compass)compass.innerHTML=svg('<circle cx="100" cy="100" r="87" fill="none" stroke="currentColor" stroke-width="2"/><path d="M100 25v16M100 159v16M25 100h16M159 100h16" stroke="currentColor" stroke-width="3"/><path d="M100 49l-17 68 17-10 17 10z" fill="#bca889"/><path d="M100 151l-17-34 17-10 17 10z" fill="#52788c"/><circle cx="100" cy="100" r="5" fill="#e9e5dc"/>');
  const stack=document.querySelector('.album-stack');
  if(stack){const front=stack.querySelector('img');m().photos.slice(1).forEach((n,i)=>{const img=document.createElement('img');img.src=photo(n);img.alt='';img.className='sleeve sleeve-'+i;stack.insertBefore(img,front)});}
  document.querySelectorAll('.tile').forEach((el,i)=>{el.insertAdjacentHTML('afterbegin',`<span class="tile-index">${String(i+1).padStart(2,'0')}<span>${['MEMORY','ARCHIVE','ROUTES','SOUND','DRIVE','SYSTEM'][i]}</span></span>`);el.querySelector('div').insertAdjacentHTML('beforeend','<span class="tile-arrow" aria-hidden="true">↗</span>')});
  document.querySelectorAll('.memory-card').forEach(el=>el.insertAdjacentHTML('afterbegin',`<span class="frame-index">FRAME ${String(Number(el.dataset.memory)+1).padStart(2,'0')} <i>${String(memories[Number(el.dataset.memory)]?.photoCount??memories[Number(el.dataset.memory)]?.photos.length??0).padStart(2,'0')} PHOTOS</i></span>`));
  document.querySelectorAll('.track').forEach((el,i)=>el.insertAdjacentHTML('afterbegin',`<span class="track-number">${String(i+1).padStart(2,'0')}</span>`));
  const road=document.querySelector('.road');
  if(road)road.innerHTML=svg('<defs><linearGradient id="roadLight" x2="0" y2="1"><stop stop-color="#75cfe4" stop-opacity="0"/><stop offset="1" stop-color="#75cfe4" stop-opacity=".22"/></linearGradient></defs><path d="M87 10L18 190M113 10l69 180" stroke="#658396" fill="none"/><path d="M95 10L68 190h64L105 10" fill="url(#roadLight)"/><path d="M95 20L68 190M105 20l27 170" stroke="#a8c7d5" stroke-dasharray="12 12" fill="none"/><ellipse cx="100" cy="150" rx="24" ry="33" fill="#060c13" opacity=".7"/><path d="M87 116Q100 110 113 116l5 18v34q-18 8-36 0v-34z" fill="#344c5b" stroke="#a2bac8" stroke-width="1.2"/><path d="M89 124h22l3 13H86zM87 156h26l-2 8H89z" fill="#0a1722" stroke="#7392a6"/><path d="M85 145v9M115 145v9" stroke="#95bed0"/><path d="M85 166h7M108 166h7" stroke="#dcab8a" stroke-width="2"/>');
  document.querySelectorAll('[data-action="previous"],[data-action="next"]').forEach(el=>{if(!el.classList.contains('next-memory'))el.innerHTML=svg(`<path d="${el.dataset.action==='previous'?'M16 6L8 12l8 6zM6 6v12':'M8 6l8 6-8 6zM18 6v12'}"/>`,'0 0 24 24')});
}
refineCockpit();
