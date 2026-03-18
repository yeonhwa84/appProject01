<template>
  <transition name="screen" mode="out-in">

    <!-- 로그인 화면 -->
    <LoginView
      v-if="screen === 'login'"
      key="login"
      @loggedIn="onLoggedIn"
    />

    <!-- 게임 화면 -->
    <GameView
      v-else-if="screen === 'game'"
      key="game"
      :userId="user.userId"
      :userType="user.userType"
      :userName="user.userName"
      :initSave="user.initSave"
      @title="onTitle"
    />

  </transition>
</template>

<script setup>
import { ref } from 'vue'
import LoginView from './views/LoginView.vue'
import GameView  from './views/GameView.vue'
import { loadGame, logout } from './store/api.js'

const screen = ref('login')
const user   = ref({ userId: null, userType: null, userName: null, initSave: null })

// 로그인 성공 → 세이브 있으면 로드 여부 확인
async function onLoggedIn(loginData) {
  user.value = {
    userId:   loginData.userId,
    userType: loginData.userType,
    userName: loginData.userName,
    initSave: null
  }

  // 이름 로그인 + 세이브 있으면 자동 로드
  if (loginData.hasSave) {
    try {
      const res = await loadGame()
      if (res.success && res.data) {
        user.value.initSave = res.data
      }
    } catch { /* 로드 실패시 처음부터 */ }
  }

  screen.value = 'game'
}

// 타이틀(로그인)으로 돌아가기
async function onTitle() {
  try { await logout() } catch { /* 무시 */ }
  user.value   = { userId: null, userType: null, userName: null, initSave: null }
  screen.value = 'login'
}
</script>

<style>
@import url('https://fonts.googleapis.com/css2?family=Noto+Sans+KR:wght@400;500;700&display=swap');
*, *::before, *::after { box-sizing: border-box; margin: 0; padding: 0; }
body { overflow: hidden; background: #0a0810; }

.screen-enter-active, .screen-leave-active { transition: opacity .5s ease; }
.screen-enter-from,   .screen-leave-to     { opacity: 0; }
</style>
