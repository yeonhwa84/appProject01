<template>
  <div class="login-wrap">

    <!-- 배경 파티클 -->
    <canvas ref="canvas" class="particle-canvas"></canvas>
    <div class="login-bg"></div>

    <div class="login-card" :class="{ shake: shaking }">

      <!-- 로고 -->
      <div class="logo-area">
        <div class="logo-deco">✿</div>
        <h1 class="logo-title">봄날의 인연</h1>
        <p class="logo-sub">Spring Romance</p>
      </div>

      <!-- BGM 토글 (샘플 - 실제 파일 넣으면 재생됨) -->
      <button class="bgm-toggle" @click="toggleBgm" :title="bgmOn ? 'BGM 끄기' : 'BGM 켜기'">
        <span>{{ bgmOn ? '🎵' : '🔇' }}</span>
        <span class="bgm-label">{{ bgmOn ? 'BGM ON' : 'BGM OFF' }}</span>
      </button>

      <!-- 탭 -->
      <div class="tab-row">
        <button :class="['tab-btn', { active: tab === 'guest' }]" @click="tab = 'guest'">
          👤 게스트
        </button>
        <button :class="['tab-btn', { active: tab === 'named' }]" @click="tab = 'named'">
          ✏️ 이름으로
        </button>
      </div>

      <!-- 게스트 탭 -->
      <div class="tab-content" v-if="tab === 'guest'">
        <p class="tab-desc">
          이름 없이 바로 시작해요.<br/>
          <span class="tab-warn">⚠ 브라우저를 닫으면 이어하기가 어려워요.</span>
        </p>
        <button class="start-btn guest" @click="onGuest" :disabled="loading">
          {{ loading ? '로딩 중...' : '게스트로 시작하기 ▶' }}
        </button>
      </div>

      <!-- 이름 탭 -->
      <div class="tab-content" v-else>
        <p class="tab-desc">
          이름을 입력하면 같은 이름으로<br/>나중에 이어하기가 가능해요. 🌸
        </p>
        <input
          ref="nameInput"
          v-model="userName"
          class="name-input"
          type="text"
          placeholder="이름을 입력해요 (최대 20자)"
          maxlength="20"
          @keyup.enter="onNamed"
        />
        <p class="input-error" v-if="inputError">{{ inputError }}</p>
        <button class="start-btn named" @click="onNamed" :disabled="loading">
          {{ loading ? '로딩 중...' : '시작하기 ▶' }}
        </button>
      </div>

      <!-- 에러 메시지 -->
      <p class="login-error" v-if="errorMsg">{{ errorMsg }}</p>

    </div>

    <p class="credit">yeonhwa84 · 2025</p>

  </div>
</template>

<script setup>
import { ref, watch, onMounted, onUnmounted, nextTick } from 'vue'
import { loginAsGuest, loginByName } from '../store/api.js'

const emit = defineEmits(['loggedIn'])

const tab       = ref('guest')
const userName  = ref('')
const loading   = ref(false)
const errorMsg  = ref('')
const inputError= ref('')
const shaking   = ref(false)
const bgmOn     = ref(false)
const canvas    = ref(null)
const nameInput = ref(null)
let bgmAudio    = null
let animId      = null

// ── BGM 샘플 ─────────────────────────────────────────────────────
// public/sounds/bgm/bgm_title.mp3 파일 넣으면 자동 재생
function toggleBgm() {
  bgmOn.value = !bgmOn.value
  if (bgmOn.value) {
    try {
      bgmAudio = new Audio('/sounds/bgm/bgm_title.mp3')
      bgmAudio.loop = true
      bgmAudio.volume = 0.4
      bgmAudio.play().catch(() => {})
    } catch { bgmOn.value = false }
  } else {
    if (bgmAudio) { bgmAudio.pause(); bgmAudio = null }
  }
}

// ── 로그인 ────────────────────────────────────────────────────────
async function onGuest() {
  loading.value = true; errorMsg.value = ''
  try {
    const res = await loginAsGuest()
    if (res.success) emit('loggedIn', res.data)
    else { errorMsg.value = res.message; doShake() }
  } catch {
    errorMsg.value = '서버에 연결할 수 없어요. 잠시 후 다시 시도해요.'
    doShake()
  } finally { loading.value = false }
}

async function onNamed() {
  inputError.value = ''
  if (!userName.value.trim()) { inputError.value = '이름을 입력해주세요.'; return }
  if (userName.value.trim().length > 20) { inputError.value = '이름은 20자 이내로 입력해주세요.'; return }

  loading.value = true; errorMsg.value = ''
  try {
    const res = await loginByName(userName.value.trim())
    if (res.success) emit('loggedIn', res.data)
    else { errorMsg.value = res.message; doShake() }
  } catch {
    errorMsg.value = '서버에 연결할 수 없어요. 잠시 후 다시 시도해요.'
    doShake()
  } finally { loading.value = false }
}

function doShake() {
  shaking.value = true
  setTimeout(() => shaking.value = false, 500)
}

watch(tab, () => {
  inputError.value = ''; errorMsg.value = ''
  if (tab.value === 'named') nextTick(() => nameInput.value?.focus())
})

// ── 파티클 ────────────────────────────────────────────────────────
onMounted(() => {
  const cvs = canvas.value
  if (!cvs) return
  const ctx = cvs.getContext('2d')
  cvs.width = window.innerWidth; cvs.height = window.innerHeight
  const pts = Array.from({ length: 45 }, () => ({
    x: Math.random() * cvs.width, y: Math.random() * cvs.height,
    r: Math.random() * 2 + 0.5, vy: -(Math.random() * 0.5 + 0.15),
    vx: (Math.random() - 0.5) * 0.25,
    alpha: Math.random() * 0.5 + 0.2,
    color: ['#f9a8c9','#c5b8f5','#93ddc8'][Math.floor(Math.random()*3)]
  }))
  function draw() {
    ctx.clearRect(0, 0, cvs.width, cvs.height)
    pts.forEach(p => {
      ctx.beginPath(); ctx.arc(p.x, p.y, p.r, 0, Math.PI*2)
      ctx.fillStyle = p.color + Math.round(p.alpha*255).toString(16).padStart(2,'0')
      ctx.fill()
      p.x += p.vx; p.y += p.vy
      if (p.y < -10) { p.y = cvs.height+10; p.x = Math.random()*cvs.width }
    })
    animId = requestAnimationFrame(draw)
  }
  draw()
  window.addEventListener('resize', () => { cvs.width = window.innerWidth; cvs.height = window.innerHeight })
})

onUnmounted(() => {
  if (animId) cancelAnimationFrame(animId)
  if (bgmAudio) { bgmAudio.pause(); bgmAudio = null }
})
</script>

<style scoped>
.login-wrap {
  width: 100vw; height: 100vh;
  display: flex; flex-direction: column;
  align-items: center; justify-content: center;
  font-family: 'Noto Sans KR', sans-serif;
  position: relative; overflow: hidden;
}
.login-bg {
  position: absolute; inset: 0;
  background: radial-gradient(ellipse at 30% 40%, rgba(197,184,245,.2) 0%, transparent 55%),
              radial-gradient(ellipse at 70% 70%, rgba(249,168,201,.16) 0%, transparent 50%),
              #0a0810;
}
.particle-canvas { position: absolute; inset: 0; z-index: 1; pointer-events: none; }

/* 카드 */
.login-card {
  position: relative; z-index: 2;
  background: rgba(20,14,34,.88);
  border: 1px solid rgba(197,184,245,.18);
  border-radius: 22px;
  padding: 36px 32px 28px;
  width: 360px;
  backdrop-filter: blur(18px);
  transition: transform .15s;
}
.login-card.shake { animation: shake .45s ease; }
@keyframes shake {
  0%,100%{transform:translateX(0)}
  20%{transform:translateX(-8px)}
  40%{transform:translateX(8px)}
  60%{transform:translateX(-5px)}
  80%{transform:translateX(5px)}
}

/* 로고 */
.logo-area { text-align: center; margin-bottom: 22px; }
.logo-deco { font-size: 32px; display: block; margin-bottom: 6px; animation: spin 8s linear infinite; }
@keyframes spin { 0%{transform:rotate(0)} 100%{transform:rotate(360deg)} }
.logo-title { font-size: 26px; font-weight: 700; color: #f0eee8; letter-spacing: -.02em; margin-bottom: 4px; }
.logo-sub { font-size: 12px; color: #a09aaf; letter-spacing: .2em; text-transform: uppercase; }

/* BGM 토글 */
.bgm-toggle {
  display: flex; align-items: center; gap: 6px;
  margin: 0 auto 16px;
  background: rgba(50,35,70,.6); border: 1px solid rgba(255,255,255,.08);
  border-radius: 20px; padding: 4px 14px;
  color: #a09aaf; font-size: 12px; font-family: inherit;
  cursor: pointer; transition: color .2s, border-color .2s;
}
.bgm-toggle:hover { color: #f0eee8; border-color: rgba(197,184,245,.3); }
.bgm-label { font-size: 11px; }

/* 탭 */
.tab-row { display: flex; gap: 6px; margin-bottom: 18px; }
.tab-btn {
  flex: 1; padding: 9px;
  background: rgba(40,28,58,.7); border: 1px solid rgba(255,255,255,.07);
  border-radius: 10px; color: #a09aaf;
  font-size: 13px; font-family: inherit; cursor: pointer;
  transition: all .18s;
}
.tab-btn.active {
  background: rgba(80,55,115,.9); border-color: rgba(197,184,245,.3);
  color: #f0eee8;
}

/* 탭 콘텐츠 */
.tab-content { display: flex; flex-direction: column; gap: 12px; }
.tab-desc { font-size: 13px; color: #a09aaf; line-height: 1.7; text-align: center; }
.tab-warn { font-size: 11px; color: rgba(249,168,201,.7); }

/* 이름 입력 */
.name-input {
  width: 100%; padding: 11px 14px;
  background: rgba(40,28,58,.8); border: 1px solid rgba(255,255,255,.1);
  border-radius: 10px; color: #f0eee8;
  font-size: 14px; font-family: inherit;
  outline: none; transition: border-color .18s;
}
.name-input:focus { border-color: rgba(197,184,245,.5); }
.name-input::placeholder { color: rgba(160,154,175,.5); }
.input-error { font-size: 12px; color: #f9a8c9; }

/* 시작 버튼 */
.start-btn {
  width: 100%; padding: 13px;
  border: none; border-radius: 12px;
  font-size: 15px; font-weight: 700; font-family: inherit;
  cursor: pointer; transition: opacity .2s, transform .2s;
}
.start-btn:disabled { opacity: .5; cursor: not-allowed; }
.start-btn:not(:disabled):hover { opacity: .88; transform: translateY(-1px); }
.start-btn.guest { background: rgba(80,55,115,.9); color: #f0eee8; border: 1px solid rgba(197,184,245,.25); }
.start-btn.named { background: linear-gradient(135deg, #c5b8f5, #f9a8c9); color: #1e1828; }

.login-error { font-size: 12px; color: #f9a8c9; text-align: center; margin-top: 8px; }
.credit { position: relative; z-index: 2; font-size: 11px; color: rgba(160,154,175,.35); margin-top: 20px; }
</style>
