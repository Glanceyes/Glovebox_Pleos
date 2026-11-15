(()=>{
 function decorate(){
  document.querySelectorAll('.dial').forEach((dial,i)=>{
   if(dial.querySelector('.bezel-flow'))return;
   const id='vortex-gold-'+i;
   dial.insertAdjacentHTML('afterbegin',`<svg class="bezel-flow" viewBox="0 0 200 200" aria-hidden="true"><defs><linearGradient id="${id}"><stop stop-color="#f7d494" stop-opacity="0"/><stop offset=".75" stop-color="#e9b66e" stop-opacity=".6"/><stop offset="1" stop-color="#fff0cc"/></linearGradient></defs><circle cx="100" cy="100" r="94" fill="none" stroke="url(#${id})" stroke-width="2.2" stroke-dasharray="110 481" stroke-linecap="round"/><circle cx="100" cy="100" r="97" fill="none" stroke="#ffe0a2" stroke-opacity=".4" stroke-width=".8" stroke-dasharray="35 575"/></svg>`);
  });
 }
 const previous=render;render=function(){previous();decorate()};decorate();
})();
