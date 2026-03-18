import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  server: {
    port: 5173,
    proxy: {
      // 개발 중 /api 요청 → Spring Boot 8081 로 프록시
      '/api': {
        target:      'http://localhost:8081',
        changeOrigin: true,
        credentials: true
      }
    }
  },
  build: {
    // npm run build → Spring Boot static 폴더에 자동 출력
    // 운영 배포 시 Spring Boot 하나로 통합 서빙 가능
    outDir:    '../backend/src/main/resources/static',
    emptyOutDir: true
  }
})
