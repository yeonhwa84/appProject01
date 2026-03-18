<template>
  <div class="game-wrap">

    <!-- 배경 이미지 -->
    <div class="bg-layer" :style="bgStyle"></div>

    <!-- 캐릭터 이미지 -->
    <transition name="char-fade">
      <div class="char-layer" v-if="scene.charImg" :key="scene.charImg">
        <img
          :src="`/images/characters/${scene.charImg}`"
          :alt="scene.speaker"
          @error="imgError = true"
        />
      </div>
    </transition>

    <!-- 캐릭터 이미지 없을 때 색상 원 대체 -->
    <div class="char-fallback" v-if="scene.charImg && imgError && speakerChar">
      <div class="char-circle" :style="{ background: speakerChar.color+'44', borderColor: speakerChar.color }">
        {{ speakerChar.name[0] }}
      </div>
    </div>

    <!-- 우상단 컨트롤 -->
    <div class="top-controls">
      <button class="ctrl-btn" @click="toggleMute" :title="isMuted ? '소리 켜기' : '소리 끄기'">
        {{ isMuted ? '🔇' : '🔊' }}
      </button>
      <input
        type="range" min="0" max="1" step="0.05"
        :value="bgmVolume" @input="e => setBgmVolume(Number(e.target.value))"
        class="vol-slider" title="BGM 볼륨"
      />
    </div>

    <!-- 엔딩 화면 -->
    <transition name="fade">
      <div class="ending-overlay" v-if="isEnding">
        <div class="ending-inner">
          <p class="ending-title">✿ END ✿</p>
          <p class="ending-greeting">{{ userGreeting }}</p>
          <p class="ending-sub">최종 호감도를 확인해요.</p>
          <div class="ending-list">
            <div class="ending-row" v-for="ch in charList" :key="ch.name">
              <span class="e-name" :style="{ color: ch.color }">{{ ch.name }}</span>
              <div class="e-track">
                <div class="e-fill" :style="{ width: ch.affection + '%', background: ch.color }"></div>
              </div>
              <span class="e-label">{{ ch.affectionLabel }} ({{ ch.affection }})</span>
            </div>
          </div>
          <div class="ending-btns">
            <button class="end-btn restart" @click="restart">🔄 처음부터</button>
            <button class="end-btn title"   @click="$emit('title')">🏠 타이틀</button>
          </div>
        </div>
      </div>
    </transition>

    <!-- 하단 UI -->
    <div class="bottom-ui" v-if="!isEnding">

      <!-- 챕터 표시 -->
      <div class="chapter-bar">
        <span class="chapter-lbl">{{ chapterLabel }}</span>
        <span class="user-lbl">{{ userGreeting }}</span>
      </div>

      <!-- 호감도 바 -->
      <div class="aff-area">
        <div class="aff-item" v-for="ch in charList" :key="ch.name">
          <span class="aff-name" :style="{ color: ch.color }">{{ ch.name }}</span>
          <div class="aff-track">
            <div class="aff-fill" :style="{ width: ch.affection + '%', background: ch.color }"></div>
          </div>
          <span class="aff-label">{{ ch.affectionLabel }}</span>
        </div>
      </div>

      <!-- 대화창 -->
      <div class="dialog-box" @click="onDialogClick">
        <div
          class="speaker"
          :class="{ narration: scene.isNarration }"
          :style="speakerChar && !scene.isNarration ? { color: speakerChar.color } : {}"
        >
          {{ scene.isNarration ? '─ 내레이션 ─' : scene.speaker }}
        </div>
        <div class="dialogue">{{ displayedText }}</div>
      </div>

      <!-- 선택지 / 다음 버튼 -->
      <transition name="fade">
        <div class="choice-area" v-if="typingDone">
          <template v-if="scene.choices?.length">
            <button
              class="choice-btn"
              v-for="c in scene.choices"
              :key="c.text"
              @click="choose(c)"
            >{{ c.text }}</button>
          </template>
          <template v-else-if="scene.next">
            <button class="choice-btn next-btn" @click="advance">다음 ▶</button>
          </template>
        </div>
      </transition>

      <!-- 툴바 -->
      <div class="toolbar">
        <button class="tool-btn" @click="save">💾 세이브</button>
        <button class="tool-btn" @click="load">📂 로드</button>
        <button class="tool-btn" @click="$emit('title')">🏠 타이틀</button>
        <span class="save-msg" :class="{ visible: saveMsg }">{{ saveMsg }}</span>
      </div>

    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import { CHARACTERS, SCENES, getAffLabel } from '../store/gameData.js'
import { saveGame, loadGame }              from '../store/api.js'
import { useBgm }                          from '../store/useBgm.js'

// ── Props / Emits ──────────────────────────────────────────────────
const props = defineProps({
  userId:    { type: String, required: true },
  userType:  { type: String, default: 'GUEST' },
  userName:  { type: String, default: null },
  initSave:  { type: Object, default: null }  // 타이틀에서 로드한 세이브 데이터
})
const emit = defineEmits(['title'])

// ── BGM ───────────────────────────────────────────────────────────
const { playBgm, playSfx, toggleMute, setBgmVolume, isMuted, bgmVolume } = useBgm()

// ── 상태 ──────────────────────────────────────────────────────────
const sceneId       = ref('start')
const affection     = ref(Object.fromEntries(Object.keys(CHARACTERS).map(n => [n, 50])))
const displayedText = ref('')
const typingDone    = ref(false)
const isEnding      = ref(false)
const saveMsg       = ref('')
const currentBg     = ref(null)
const imgError      = ref(false)
let typingTimer     = null

// ── 초기 세이브 로드 ──────────────────────────────────────────────
onMounted(() => {
  if (props.initSave) {
    sceneId.value   = props.initSave.sceneId
    affection.value = props.initSave.affection
    if (props.initSave.currentBg) currentBg.value = props.initSave.currentBg
  }
})

// ── 현재 씬 ───────────────────────────────────────────────────────
const scene = computed(() => {
  const s = SCENES[sceneId.value] || SCENES['start']
  return { ...s, isNarration: !s.speaker }
})

const speakerChar = computed(() =>
  scene.value.speaker ? CHARACTERS[scene.value.speaker] : null
)

const charList = computed(() =>
  Object.values(CHARACTERS).map(ch => ({
    ...ch,
    affection:      affection.value[ch.name] ?? 50,
    affectionLabel: getAffLabel(affection.value[ch.name] ?? 50)
  }))
)

const bgStyle = computed(() =>
  currentBg.value
    ? { backgroundImage: `url('/images/backgrounds/${currentBg.value}')` }
    : {}
)

// 챕터 레이블
const chapterLabel = computed(() => {
  const id = sceneId.value
  if (id.startsWith('ch2')) return 'CHAPTER 2 · 두 번째 날'
  return 'CHAPTER 1 · 봄날의 만남'
})

// 유저 인사말
const userGreeting = computed(() => {
  if (props.userType === 'NAMED' && props.userName) return `${props.userName}님`
  return '게스트'
})

// ── 씬 변경 감지 ──────────────────────────────────────────────────
watch(sceneId, () => {
  const s = scene.value
  if (s.bg) currentBg.value = s.bg
  if (s.bgm) playBgm(s.bgm)
  imgError.value = false
  startTyping(s.text)
  playSfx('click.mp3')
}, { immediate: true })

// ── 타이핑 효과 ───────────────────────────────────────────────────
function startTyping(text) {
  if (typingTimer) clearInterval(typingTimer)
  displayedText.value = ''
  typingDone.value    = false
  let i = 0
  typingTimer = setInterval(() => {
    if (i < text.length) displayedText.value += text[i++]
    else { clearInterval(typingTimer); typingDone.value = true }
  }, 25)
}

function onDialogClick() {
  if (!typingDone.value) {
    clearInterval(typingTimer)
    displayedText.value = scene.value.text
    typingDone.value    = true
  }
}

// ── 씬 이동 ───────────────────────────────────────────────────────
function advance() {
  const next = scene.value.next
  if (!next) { isEnding.value = true; return }
  sceneId.value = next
}

function choose(c) {
  // 호감도 변경
  if (c.target && c.affection) {
    const cur = affection.value[c.target] ?? 50
    affection.value[c.target] = Math.max(0, Math.min(100, cur + c.affection))
  }
  playSfx('select.mp3')
  if (!c.next) { isEnding.value = true; return }
  sceneId.value = c.next
}

// ── 재시작 ────────────────────────────────────────────────────────
function restart() {
  isEnding.value  = false
  sceneId.value   = 'start'
  currentBg.value = null
  affection.value = Object.fromEntries(Object.keys(CHARACTERS).map(n => [n, 50]))
}

// ── 세이브 (서버 API) ─────────────────────────────────────────────
async function save() {
  try {
    await saveGame(sceneId.value, affection.value, currentBg.value)
    showMsg('💾 저장 완료!')
  } catch {
    showMsg('저장 실패. 서버를 확인해주세요.')
  }
}

// ── 로드 (서버 API) ───────────────────────────────────────────────
async function load() {
  try {
    const res = await loadGame()
    if (!res.success || !res.data) { showMsg('저장된 데이터가 없어요.'); return }
    const d = res.data
    isEnding.value  = false
    affection.value = d.affection
    sceneId.value   = d.sceneId
    if (d.currentBg) currentBg.value = d.currentBg
    showMsg(`📂 불러오기 완료! (${d.savedAt ?? ''})`)
  } catch {
    showMsg('불러오기 실패. 서버를 확인해주세요.')
  }
}

function showMsg(msg) {
  saveMsg.value = msg
  setTimeout(() => { saveMsg.value = '' }, 3000)
}
</script>

<style scoped>
.game-wrap {
  width: 100vw; height: 100vh; position: relative; overflow: hidden;
  background: #0a0810; font-family: 'Noto Sans KR', sans-serif; color: #f0eee8;
}

/* 배경 */
.bg-layer {
  position: absolute; inset: 0; z-index: 0;
  background-size: cover; background-position: center;
  transition: background-image .5s ease;
}
.bg-layer::after {
  content: ''; position: absolute; inset: 0;
  background: linear-gradient(to bottom, rgba(10,8,20,.08) 0%, rgba(10,8,20,.72) 100%);
}

/* 캐릭터 */
.char-layer {
  position: absolute; bottom: 235px; left: 50%;
  transform: translateX(-50%); z-index: 1; pointer-events: none;
}
.char-layer img { max-height: 330px; max-width: 260px; object-fit: contain; filter: drop-shadow(0 8px 28px rgba(0,0,0,.55)); }
.char-fallback { position: absolute; bottom: 320px; left: 50%; transform: translateX(-50%); z-index: 1; }
.char-circle { width: 110px; height: 110px; border-radius: 50%; border: 3px solid; display: flex; align-items: center; justify-content: center; font-size: 36px; font-weight: 700; color: rgba(255,255,255,.85); }

/* 우상단 컨트롤 */
.top-controls {
  position: absolute; top: 12px; right: 14px; z-index: 5;
  display: flex; align-items: center; gap: 8px;
  background: rgba(10,8,20,.6); border-radius: 20px; padding: 4px 10px;
  backdrop-filter: blur(8px);
}
.ctrl-btn { background: none; border: none; color: #f0eee8; font-size: 16px; cursor: pointer; padding: 2px 4px; }
.vol-slider { width: 70px; height: 4px; accent-color: #c5b8f5; cursor: pointer; }

.bottom-ui { position: absolute; bottom: 0; left: 0; right: 0; z-index: 2; }

/* 챕터 바 */
.chapter-bar {
  background: rgba(8,5,16,.65); padding: 3px 20px;
  display: flex; justify-content: space-between; align-items: center;
}
.chapter-lbl { font-size: 10px; letter-spacing: .12em; text-transform: uppercase; color: rgba(197,184,245,.5); font-weight: 600; }
.user-lbl { font-size: 10px; color: rgba(249,168,201,.6); font-weight: 600; }

/* 호감도 */
.aff-area { display: flex; gap: 16px; padding: 7px 22px; background: rgba(8,5,16,.9); flex-wrap: wrap; }
.aff-item { display: flex; align-items: center; gap: 7px; flex: 1; min-width: 130px; }
.aff-name { font-size: 11px; font-weight: 700; min-width: 28px; }
.aff-track { flex: 1; height: 5px; background: rgba(255,255,255,.1); border-radius: 99px; overflow: hidden; }
.aff-fill { height: 100%; border-radius: 99px; transition: width .6s cubic-bezier(.4,0,.2,1); }
.aff-label { font-size: 10px; color: #a09aaf; min-width: 56px; text-align: right; }

/* 대화창 */
.dialog-box { background: rgba(12,8,22,.93); backdrop-filter: blur(14px); border-top: 1px solid rgba(255,255,255,.07); padding: 14px 22px 8px; cursor: pointer; }
.speaker { font-size: 15px; font-weight: 700; color: #c5b8f5; margin-bottom: 6px; min-height: 22px; }
.speaker.narration { color: #a09aaf; font-weight: 400; }
.dialogue { font-size: 15px; line-height: 1.75; color: #f0eee8; min-height: 54px; white-space: pre-line; }

/* 선택지 */
.choice-area { background: rgba(12,8,22,.93); backdrop-filter: blur(14px); padding: 7px 22px 8px; display: flex; flex-direction: column; gap: 6px; }
.choice-btn { width: 100%; padding: 10px 18px; background: rgba(50,35,70,.92); border: 1px solid rgba(255,255,255,.08); border-radius: 10px; color: #f0eee8; font-size: 14px; font-family: inherit; cursor: pointer; text-align: left; transition: background .18s, transform .18s, border-color .18s; }
.choice-btn:hover { background: rgba(85,60,115,.97); border-color: rgba(255,255,255,.18); transform: translateX(4px); }
.next-btn { text-align: right; color: #a09aaf; font-size: 13px; }
.next-btn:hover { color: #f0eee8; }

/* 툴바 */
.toolbar { background: rgba(8,5,16,.96); padding: 6px 22px; display: flex; align-items: center; gap: 8px; }
.tool-btn { background: rgba(50,35,70,.7); border: 1px solid rgba(255,255,255,.08); border-radius: 8px; color: #a09aaf; font-size: 11px; font-family: inherit; padding: 4px 12px; cursor: pointer; transition: color .18s, border-color .18s; }
.tool-btn:hover { color: #f0eee8; border-color: rgba(255,255,255,.2); }
.save-msg { font-size: 11px; color: #c5b8f5; margin-left: auto; opacity: 0; transition: opacity .3s; }
.save-msg.visible { opacity: 1; }

/* 엔딩 */
.ending-overlay { position: absolute; inset: 0; z-index: 10; background: rgba(8,5,16,.93); backdrop-filter: blur(16px); display: flex; align-items: center; justify-content: center; }
.ending-inner { display: flex; flex-direction: column; align-items: center; gap: 16px; }
.ending-title { font-size: 30px; font-weight: 700; color: #c5b8f5; letter-spacing: -.02em; }
.ending-greeting { font-size: 15px; color: #f9a8c9; font-weight: 600; }
.ending-sub { font-size: 13px; color: #a09aaf; }
.ending-list { display: flex; flex-direction: column; gap: 12px; width: 340px; }
.ending-row { display: flex; align-items: center; gap: 10px; }
.e-name { font-size: 14px; font-weight: 700; min-width: 32px; }
.e-track { flex: 1; height: 8px; background: rgba(255,255,255,.1); border-radius: 99px; overflow: hidden; }
.e-fill { height: 100%; border-radius: 99px; transition: width 1.2s cubic-bezier(.4,0,.2,1); }
.e-label { font-size: 12px; color: #a09aaf; min-width: 110px; text-align: right; }
.ending-btns { display: flex; gap: 10px; margin-top: 8px; }
.end-btn { padding: 11px 24px; border: none; border-radius: 12px; font-size: 14px; font-weight: 700; font-family: inherit; cursor: pointer; transition: opacity .2s, transform .2s; }
.end-btn:hover { opacity: .88; transform: scale(1.03); }
.end-btn.restart { background: linear-gradient(135deg, #c5b8f5, #f9a8c9); color: #1e1828; }
.end-btn.title   { background: rgba(50,35,70,.9); color: #f0eee8; border: 1px solid rgba(255,255,255,.12); }

/* 트랜지션 */
.char-fade-enter-active, .char-fade-leave-active { transition: opacity .4s; }
.char-fade-enter-from,   .char-fade-leave-to     { opacity: 0; }
.fade-enter-active, .fade-leave-active { transition: opacity .3s; }
.fade-enter-from,   .fade-leave-to     { opacity: 0; }
</style>
