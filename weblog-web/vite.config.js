import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: { '@': path.resolve(__dirname, 'src') }
  },
  build: {
    // 手动分包：将体积大的第三方库拆分，避免单 chunk 过大（Dashboard/ArticlePublish 曾超 500KB）
    rollupOptions: {
      output: {
        manualChunks: {
          // Vue 核心生态
          'vue-vendor': ['vue', 'vue-router', 'pinia'],
          // Element Plus 组件库
          'element-plus': ['element-plus', '@element-plus/icons-vue'],
          // 图表库（体积大，单独拆出便于缓存）
          echarts: ['echarts'],
          // Markdown 编辑器（体积大，仅文章发布页用到）
          'md-editor': ['md-editor-v3'],
          // 代码高亮 + 图片查看
          'highlight-viewer': ['highlight.js', 'viewerjs']
        }
      }
    },
    // 单 chunk 超过 800KB 才警告，避免误报
    chunkSizeWarningLimit: 800
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
