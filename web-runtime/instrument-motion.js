(()=>{
 const bars=()=>'<span class="sound-meter" aria-hidden="true">'+ '<i></i>'.repeat(7)+'</span>';
 function enhance(){
  document.querySelectorAll('.dial').forEach(d=>{
   if(d.querySelector('.instrument-trace'))return;
   d.insertAdjacentHTML('afterbegin','<svg class="instrument-trace" viewBox="0 0 200 200" aria-hidden="true"><path pathLength="1" d="M 36 164 A 90 90 0 1 1 164 164"/></svg>');
   d.querySelectorAll('.instrument-bezel line').forEach((line,i)=>line.style.setProperty('--tick-index',i));
  });
  const widget=document.querySelector('.music-widget');
  if(widget&&!widget.querySelector('.sound-meter'))widget.querySelector('.progress').insertAdjacentHTML('beforebegin',bars());
  const footer=document.querySelector('footer .wave');
  if(footer&&!footer.querySelector('.sound-meter')){footer.innerHTML=bars();footer.setAttribute('aria-hidden','true')}
 }
 const base=render;
 render=function(){base();enhance()};
 audio.addEventListener('volumechange',()=>app.classList.toggle('sound-muted',audio.muted||audio.volume===0));
 enhance();app.classList.toggle('sound-muted',audio.muted||audio.volume===0);
})();
