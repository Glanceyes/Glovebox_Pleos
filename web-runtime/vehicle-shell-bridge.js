(()=>{
 if(parent===window)return;
 const report=()=>parent.postMessage({type:'glovebox-status',view:state.view,song:m().song,playing:state.playing},'*');
 const previous=render;render=function(){previous();report()};
 const playback=updatePlayback;updatePlayback=function(){playback();report()};
 window.addEventListener('message',e=>{
  if(e.source!==parent||e.data?.type!=='glovebox-shell')return;
  if(e.data.command==='navigate'&&['home','archive','routes','music','settings'].includes(e.data.value))go(e.data.value);
  if(e.data.command==='play')play();
 });report();
})();
