(()=>{
  'use strict';
  const panel=document.createElement('aside');
  panel.className='gl-google-link';
  panel.setAttribute('aria-label','YouTube 계정 연결');
  panel.hidden=true;
  app.append(panel);
  let last='', visibleOnShelf=false;
  window.glDrawYouTubeLink=(link,connected)=>{
    const show=!!link?.phase && !connected && (state.view==='home'||state.view==='music'&&visibleOnShelf);
    panel.hidden=!show;
    if(!show){last='';return;}
    const key=JSON.stringify(link);
    if(key===last)return;
    last=key;
    panel.replaceChildren();
    const body=document.createElement('div'),label=document.createElement('b'),instruction=document.createElement('p');
    label.textContent='내 YouTube 보관함 연결';
    body.append(label,instruction);
    const button=(caption,action)=>{
      const el=document.createElement('button');el.type='button';el.textContent=caption;
      el.onclick=()=>GloveboxNative.action(action,'');return el;
    };
    if(link.phase==='waiting'){
      const url=new URL(link.url);
      if(url.protocol==='https:'&&['www.google.com','google.com','accounts.google.com','www.youtube.com','youtube.com'].includes(url.hostname)){
        const qr=qrcode(0,'M');qr.addData(url.href);qr.make();
        const figure=document.createElement('div');figure.className='gl-google-qr';figure.innerHTML=qr.createSvgTag({cellSize:4,margin:16,scalable:true});panel.append(figure);
      }
      instruction.textContent='휴대폰으로 QR을 스캔하고 코드를 입력하세요.';
      const code=document.createElement('strong');code.textContent=link.code;body.append(code);
      const address=document.createElement('small');address.textContent=url.host+url.pathname;body.append(address);
    }else if(link.phase==='error'){
      instruction.textContent=link.message;body.append(button('다시 연결','youtube-link-start'));
    }else instruction.textContent='Google 연결 코드를 받는 중입니다.';
    panel.append(body,button('나중에','youtube-link-dismiss'));
  };
  window.glShowYouTubeLink=()=>{
    visibleOnShelf=true;
    GloveboxNative.action('youtube-link-start','');
    window.glDrawYouTubeLink(window.glAccountLink,window.glYouTubeConnected);
  };
  const original=render;
  render=function(){original();window.glDrawYouTubeLink(window.glAccountLink,window.glYouTubeConnected);};
})();
