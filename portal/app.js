let catalog={subjects:[],lessons:[],videos:[]};
const $=s=>document.querySelector(s);
const state={query:'',year:0,subject:'',offset:0};

async function boot(){
  catalog=await fetch('data/catalog.json').then(r=>r.json());
  $('#lessonCount').textContent=catalog.lessons.length;
  catalog.subjects.forEach(s=>$('#subject').insertAdjacentHTML('beforeend',`<option value="${s.id}">${s.name}</option>`));
  bind(); render(); renderVideos(); renderToday(); renderMomentSimulator();
  if('serviceWorker' in navigator) navigator.serviceWorker.register('sw.js');
}
function bind(){
  $('#search').addEventListener('input',e=>{state.query=e.target.value.toLowerCase();render()});
  $('#year').addEventListener('change',e=>{state.year=+e.target.value;render()});
  $('#subject').addEventListener('change',e=>{state.subject=e.target.value;render()});
  $('#nextLesson').addEventListener('click',()=>{state.offset++;renderToday()});
  $('#lessonDialog .close').addEventListener('click',()=>$('#lessonDialog').close());
  $('#forceSlider').addEventListener('input',renderMomentSimulator);
  $('#armSlider').addEventListener('input',renderMomentSimulator);
  $('#exportPortal').addEventListener('click',exportPortalProgress);
  $('#importPortal').addEventListener('change',importPortalProgress);
  $('#resetPortal').addEventListener('click',()=>{if(confirm('Azzero tutti i progressi salvati nel portale?')){Object.keys(localStorage).filter(k=>k.startsWith('lesson:')||k.startsWith('today:')||k==='completedLessons').forEach(k=>localStorage.removeItem(k));renderToday();}});
  let deferred; window.addEventListener('beforeinstallprompt',e=>{e.preventDefault();deferred=e;$('#installBtn').hidden=false});
  $('#installBtn').addEventListener('click',async()=>{if(deferred){deferred.prompt();await deferred.userChoice;deferred=null;$('#installBtn').hidden=true}});
}
function subjectName(id){return catalog.subjects.find(s=>s.id===id)?.name||id}
function filtered(){return catalog.lessons.filter(l=>(!state.year||l.year===state.year)&&(!state.subject||l.subjectId===state.subject)&&(!state.query||`${l.title} ${l.macroarea} ${l.module} ${subjectName(l.subjectId)}`.toLowerCase().includes(state.query))).slice(0,180)}
function render(){const list=filtered();$('#resultCount').textContent=`${list.length} risultati`;$('#results').innerHTML=list.map(l=>`<article class="lesson" data-id="${l.id}"><h3>${l.title}</h3><p>${subjectName(l.subjectId)} • ${l.year}° anno • ${l.durationMinutes} min</p><span class="status">${l.status.replaceAll('_',' ')}</span></article>`).join('');document.querySelectorAll('.lesson').forEach(el=>el.onclick=()=>openLesson(el.dataset.id))}
function openLesson(id){const l=catalog.lessons.find(x=>x.id===id);if(!l)return;$('#dialogMeta').textContent=`${subjectName(l.subjectId)} • ${l.year}° anno • ${l.macroarea}`;$('#dialogTitle').textContent=l.title;$('#dialogIntro').textContent=l.introduction;$('#dialogSummary').textContent=l.summary;$('#dialogObjectives').innerHTML=(l.objectives||[]).map(x=>`<li>${x}</li>`).join('');document.querySelectorAll('[data-step]').forEach(c=>{const k=`lesson:${id}:${c.dataset.step}`;c.checked=localStorage.getItem(k)==='1';c.onchange=()=>localStorage.setItem(k,c.checked?'1':'0')});$('#lessonDialog').showModal()}
function renderVideos(){const box=$('#videos');box.innerHTML=catalog.videos.slice(0,24).map(v=>{const local=v.platform==='MECHLAB_LOCAL';return `<article class="video-card"><p class="eyebrow">${local?'ORIGINALE OFFLINE':v.platform}</p><h3>${v.title}</h3><p>${v.author} • ${v.duration}</p>${local?`<video controls preload="metadata" src="${v.url}"></video>`:`<a href="${v.url}" target="_blank" rel="noopener">Apri la fonte ufficiale ↗</a>`}<p>${v.description}</p></article>`}).join('')}
function renderToday(){const completed=new Set(JSON.parse(localStorage.getItem('completedLessons')||'[]'));const candidates=catalog.lessons.filter(l=>!completed.has(l.id));const l=candidates[state.offset%candidates.length]||catalog.lessons[0];const steps=['Teoria','Video','Esercizi','Flashcard','Quiz'];const done=steps.filter(s=>localStorage.getItem(`today:${l.id}:${s}`)==='1').length;$('#today').innerHTML=`<p class="today-title">${l.title}</p><p>${subjectName(l.subjectId)} • ${l.year}° anno</p><div class="progress"><i style="width:${done/steps.length*100}%"></i></div>${steps.map(s=>`<label><input type="checkbox" data-today="${s}" ${localStorage.getItem(`today:${l.id}:${s}`)==='1'?'checked':''}> ${s}</label>`).join('<br>')}<p><button id="openToday">Apri lezione</button></p>`;document.querySelectorAll('[data-today]').forEach(c=>c.onchange=()=>{localStorage.setItem(`today:${l.id}:${c.dataset.today}`,c.checked?'1':'0');renderToday()});$('#openToday').onclick=()=>openLesson(l.id)}

function renderMomentSimulator(){
  const force=Number($('#forceSlider').value);
  const arm=Number($('#armSlider').value);
  const moment=force*arm;
  $('#forceValue').textContent=`${force} N`;
  $('#armValue').textContent=`${arm.toLocaleString('it-IT',{minimumFractionDigits:2,maximumFractionDigits:2})} m`;
  $('#momentValue').textContent=`${moment.toLocaleString('it-IT',{minimumFractionDigits:1,maximumFractionDigits:1})} N·m`;
  $('#momentHint').textContent=moment<40?'Momento contenuto: prova ad aumentare forza o braccio.':moment<150?'Momento intermedio: osserva quanto pesa il contributo del braccio.':'Momento elevato: nella realtà verifica sempre resistenza, serraggio e sicurezza.';
  const canvas=$('#momentCanvas'),ctx=canvas.getContext('2d');
  const w=canvas.width,h=canvas.height,pivotX=w*0.72,pivotY=h*0.70;
  ctx.clearRect(0,0,w,h); ctx.fillStyle='#101820';ctx.fillRect(0,0,w,h);
  ctx.strokeStyle='#90a4ae';ctx.lineWidth=18;ctx.lineCap='round';
  const startX=pivotX-Math.min(arm,1)*w*0.58;
  ctx.beginPath();ctx.moveTo(startX,pivotY-45);ctx.lineTo(pivotX,pivotY);ctx.stroke();
  ctx.fillStyle='#ffb300';ctx.beginPath();ctx.arc(pivotX,pivotY,18,0,Math.PI*2);ctx.fill();
  const arrowTop=Math.max(40,pivotY-70-force/500*190);
  ctx.strokeStyle='#42a5f5';ctx.lineWidth=8;ctx.beginPath();ctx.moveTo(startX,arrowTop);ctx.lineTo(startX,pivotY-55);ctx.stroke();
  ctx.beginPath();ctx.moveTo(startX-14,pivotY-78);ctx.lineTo(startX,pivotY-55);ctx.lineTo(startX+14,pivotY-78);ctx.stroke();
  ctx.font='bold 28px system-ui';ctx.fillStyle='#42a5f5';ctx.fillText(`F = ${force} N`,Math.max(20,startX-70),arrowTop-12);
  ctx.strokeStyle='#ef5350';ctx.lineWidth=3;ctx.beginPath();ctx.moveTo(startX,pivotY+25);ctx.lineTo(pivotX,pivotY+25);ctx.stroke();
  ctx.fillStyle='#ef5350';ctx.fillText(`b = ${arm.toFixed(2)} m`,(startX+pivotX)/2-65,pivotY+65);
  ctx.fillStyle='#fff';ctx.font='bold 34px system-ui';ctx.fillText(`M = ${moment.toFixed(1)} N·m`,w-290,65);
}
function portalProgressObject(){const data={version:1,exportedAt:new Date().toISOString(),items:{}};Object.keys(localStorage).filter(k=>k.startsWith('lesson:')||k.startsWith('today:')||k==='completedLessons').forEach(k=>data.items[k]=localStorage.getItem(k));return data}
function exportPortalProgress(){const blob=new Blob([JSON.stringify(portalProgressObject(),null,2)],{type:'application/json'});const a=document.createElement('a');a.href=URL.createObjectURL(blob);a.download=`mechlab-portal-backup-${new Date().toISOString().slice(0,10)}.json`;a.click();URL.revokeObjectURL(a.href)}
async function importPortalProgress(event){const file=event.target.files?.[0];if(!file)return;try{const data=JSON.parse(await file.text());if(!data.items||typeof data.items!=='object')throw new Error('Formato non valido');Object.entries(data.items).forEach(([k,v])=>localStorage.setItem(k,String(v)));renderToday();alert('Progressi del portale ripristinati.')}catch(error){alert(`Importazione non riuscita: ${error.message}`)}finally{event.target.value=''}}

boot().catch(err=>{document.body.insertAdjacentHTML('beforeend',`<pre>${err}</pre>`)})
