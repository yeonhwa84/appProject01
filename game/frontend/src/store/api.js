import axios from 'axios'

const api = axios.create({
  baseURL: '/api',
  timeout: 8000,
  withCredentials: true, // 세션 쿠키 포함
  headers: { 'Content-Type': 'application/json' }
})

// ── 인증 API ─────────────────────────────────────────────────────

/** 게스트 로그인 */
export const loginAsGuest = () => api.post('/auth/guest').then(r => r.data)

/** 이름 로그인 */
export const loginByName  = (userName) => api.post('/auth/login', { userName }).then(r => r.data)

/** 로그아웃 */
export const logout       = () => api.post('/auth/logout').then(r => r.data)

/** 세션 확인 */
export const getMe        = () => api.get('/auth/me').then(r => r.data)

// ── 세이브 API ───────────────────────────────────────────────────

/** 세이브 */
export const saveGame  = (sceneId, affection, currentBg) =>
  api.post('/save', { sceneId, affection, currentBg }).then(r => r.data)

/** 로드 */
export const loadGame  = () => api.get('/save').then(r => r.data)

/** 삭제 */
export const deleteSave = () => api.delete('/save').then(r => r.data)
