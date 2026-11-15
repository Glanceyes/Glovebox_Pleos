function updateGlassLabels(){
 const fonts=document.querySelector(".appearance-preview b");if(fonts)fonts.innerHTML="아름드리 꽃나무 <span>×</span> Pretendard";
 const theme=document.querySelector('.appearance-meta span:first-child b');
 if(theme)theme.textContent='앰버 글라스';
 const note=document.querySelector('.appearance-preview small');
 if(note)note.textContent='맑은 유리 · 오렌지 골드';
}
const detailedGlassRender=refineCockpit;
refineCockpit=function(){detailedGlassRender();updateGlassLabels()};
updateGlassLabels();
guide=function(){modal('<h2>Amber Glass</h2><p>맑은 스모크 유리 위에 앰버 오렌지와 금빛 포인트를 적용했습니다. 선택 표시, 계기판과 재생 버튼에 따뜻한 색을 집중했습니다.</p><p>유리판 뒤로 풍경의 색이 은은하게 비치고, 가장자리에 얇은 반사광이 맺히도록 표현했습니다. 글자는 아름드리 꽃나무, Pretendard와 Rajdhani를 사용합니다.</p><div class="swatches">'+['#242630','#44434b','#ed9e54','#efbb69','#ffd694','#fffaf3'].map(c=>`<i style="background:${c}" title="${c}"></i>`).join('')+'</div>')};
