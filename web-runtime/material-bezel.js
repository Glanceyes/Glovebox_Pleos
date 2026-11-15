(()=>{
 function add(){document.querySelectorAll('.dial').forEach(d=>{if(!d.querySelector('.metal-reflection'))d.insertAdjacentHTML('afterbegin','<i class="metal-reflection" aria-hidden="true"></i>')})}
 const previous=render;render=function(){previous();add()};add();
})();
