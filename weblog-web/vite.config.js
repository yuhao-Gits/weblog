import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: { '@': path.resolve(__dirname, 'src') }
  },
  server: {
    port: 5173,
    proxy: {
      // API 统一前缀：转发时去掉 /api
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        rewrite: p => p.replace(/^\/api/, ''),
        ws: true
      },
      // 本地上传文件静态资源
      '/files': { target: 'http://localhost:8080', changeOrigin: true },
      // H2 控制台（开发调试用）
      '/h2-console': { target: 'http://localhost:8080', changeOrigin: true }
    }
  }
})
