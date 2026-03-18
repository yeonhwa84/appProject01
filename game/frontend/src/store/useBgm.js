/**
 * BGM / 효과음 컴포저블
 * 파일 위치:
 *   public/sounds/bgm/   ← 배경음악 (loop)
 *   public/sounds/sfx/   ← 효과음 (1회)
 *
 * 씬 데이터의 bgm 필드에 파일명을 넣으면 자동 재생돼요.
 * bgm: null  → 이전 BGM 유지
 * bgm: 'stop' → BGM 정지
 */
import { ref } from 'vue'

let bgmAudio    = null
let currentFile = ''
const isMuted   = ref(false)
const bgmVolume = ref(0.4)
const sfxVolume = ref(0.7)

export function useBgm() {

  function playBgm(filename) {
    if (!filename || filename === 'stop') {
      if (filename === 'stop') stopBgm()
      return
    }
    if (filename === currentFile) return  // 같은 파일이면 유지
    stopBgm()
    try {
      bgmAudio = new Audio(`/sounds/bgm/${filename}`)
      bgmAudio.loop   = true
      bgmAudio.volume = isMuted.value ? 0 : bgmVolume.value
      currentFile     = filename
      bgmAudio.play().catch(() => {
        // 브라우저 정책상 첫 사용자 상호작용 후 재생 가능
        console.info('[BGM] 사용자 상호작용 후 재생됩니다.')
      })
    } catch (e) {
      console.warn('[BGM] 파일 없음:', filename)
    }
  }

  function stopBgm() {
    if (bgmAudio) { bgmAudio.pause(); bgmAudio = null; currentFile = '' }
  }

  function playSfx(filename) {
    if (!filename || isMuted.value) return
    try {
      const sfx = new Audio(`/sounds/sfx/${filename}`)
      sfx.volume = sfxVolume.value
      sfx.play().catch(() => {})
    } catch { /* 파일 없으면 무시 */ }
  }

  function toggleMute() {
    isMuted.value = !isMuted.value
    if (bgmAudio) bgmAudio.volume = isMuted.value ? 0 : bgmVolume.value
  }

  function setBgmVolume(v) {
    bgmVolume.value = Math.max(0, Math.min(1, v))
    if (bgmAudio && !isMuted.value) bgmAudio.volume = bgmVolume.value
  }

  return { playBgm, stopBgm, playSfx, toggleMute, setBgmVolume, isMuted, bgmVolume }
}
