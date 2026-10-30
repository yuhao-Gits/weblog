<template>
  <div class="min-h-screen flex flex-col" style="background: var(--bg)">
    <!-- 公共 Header：玻璃拟态粘性顶栏 -->
    <header class="glass-header sticky top-0 z-50">
      <div class="max-w-6xl mx-auto px-5 h-16 flex items-center justify-between">
        <router-link to="/" class="flex items-center gap-2.5 no-underline group">
          <div v-if="settings.logo" class="w-8 h-8 rounded-xl overflow-hidden shadow-soft">
            <img :src="settings.logo" alt="logo" class="w-full h-full object-cover" />
          </div>
          <div v-else class="w-8 h-8 rounded-xl bg-brand-gradient flex items-center justify-center text-white font-bold text-lg shadow-float">
            {{ (settings.blogName || 'W').charAt(0) }}
          </div>
          <span class="text-lg font-bold tracking-tight" style="color: var(--text-primary)">{{ settings.blogName || 'Weblog' }}</span>
        </router-link>

        <nav class="flex items-center gap-1 text-sm">
          <router-link to="/" class="nav-link">首页</router-link>
          <router-link to="/archives" class="nav-link">归档</router-link>
          <router-link to="/knowledge" class="nav-link">知识库</router-link>
          <router-link to="/chat" class="nav-link">聊天室</router-link>

          <button class="icon-btn" title="搜索" @click="searchDialogRef?.open()">
            <el-icon><Search /></el-icon>
          </button>
          <button class="icon-btn" :title="isDark ? '切换到白天' : '切换到黑夜'" @click="toggleTheme">
            <el-icon><Sunny v-if="isDark" /><Moon v-else /></el-icon>
          </button>

          <template v-if="token">
            <router-link to="/admin/dashboard" class="nav-link">后台</router-link>
            <button class="text-link" @click="handleLogout">退出</button>
          </template>
          <router-link v-else to="/login" class="login-pill">登录</router-link>
        </nav>
      </div>
    </header>

    <!-- 站内搜索弹框 -->
    <SearchDialog ref="searchDialogRef" />

    <!-- 页面主体 -->
    <main class="flex-1">
      <router-view v-slot="{ Component }">
        <transition name="fade-slide" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </main>

    <!-- 公共 Footer -->
    <footer class="mt-12 border-t" style="border-color: var(--border)">
      <div class="max-w-6xl mx-auto px-5 py-8 text-center">
        <div v-if="settings.blogName" class="text-base font-semibold mb-2" style="color: var(--text-primary)">
          {{ settings.blogName }}
        </div>
        <div class="text-sm mb-3" style="color: var(--text-muted)">
          © {{ year }} {{ settings.authorName || '' }} · 用心记录，分享热爱
        </div>
        <div class="flex items-center justify-center gap-5 text-sm">
          <a v-if="settings.githubHome" :href="settings.githubHome" target="_blank" rel="noopener" class="footer-link">GitHub</a>
          <a v-if="settings.giteeHome" :href="settings.giteeHome" target="_blank" rel="noopener" class="footer-link">Gitee</a>
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useBlogSettings } from '@/composables/useBlogSettings'
import { useTheme } from '@/composables/useTheme'
import { ElMessageBox, ElMessage } from 'element-plus'
import { Search, Sunny, Moon } from '@element-plus/icons-vue'
import SearchDialog from '@/components/front/SearchDialog.vue'

const router = useRouter()
const { settings, loadSettings } = useBlogSettings()
const { isDark, toggle: toggleTheme } = useTheme()
const token = ref(localStorage.getItem('weblog_token'))
const year = computed(() => new Date().getFullYear())
const searchDialogRef = ref()

function handleLogout() {
  ElMessageBox.confirm('确定退出登录吗？', '提示', { type: 'warning' })
    .then(() => {
      localStorage.removeItem('weblog_token')
      token.value = null
      ElMessage.success('已退出登录')
      router.push('/')
    })
    .catch(() => {})
}

onMounted(() => {
  loadSettings()
})
</script>

<style scoped>
.glass-header {
  background: var(--glass-bg);
  backdrop-filter: saturate(180%) blur(16px);
  -webkit-backdrop-filter: saturate(180%) blur(16px);
  border-bottom: 1px solid var(--border);
}

.nav-link {
  position: relative;
  color: var(--text-secondary);
  text-decoration: none;
  padding: 0.4rem 0.75rem;
  border-radius: 0.6rem;
  font-weight: 500;
  transition: color 0.18s ease, background-color 0.18s ease;
}
.nav-link:hover {
  color: var(--brand);
  background: rgba(99, 102, 241, 0.07);
}
.nav-link.router-link-exact-active {
  color: var(--brand);
  font-weight: 600;
}
.nav-link.router-link-exact-active::after {
  content: '';
  position: absolute;
  left: 50%;
  bottom: 2px;
  transform: translateX(-50%);
  width: 16px;
  height: 3px;
  border-radius: 99px;
  background: var(--brand-gradient);
}

.icon-btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 34px;
  height: 34px;
  border: none;
  border-radius: 0.6rem;
  background: transparent;
  color: var(--text-secondary);
  cursor: pointer;
  transition: all 0.18s ease;
}
.icon-btn:hover {
  background: rgba(99, 102, 241, 0.08);
  color: var(--brand);
}

.login-pill {
  margin-left: 0.5rem;
  padding: 0.4rem 1.1rem;
  border-radius: 99px;
  background: var(--brand-gradient);
  color: #fff !important;
  font-weight: 600;
  text-decoration: none;
  box-shadow: var(--shadow-float);
  transition: transform 0.18s ease, filter 0.18s ease;
}
.login-pill:hover {
  transform: translateY(-1px);
  filter: brightness(1.06);
}

.text-link {
  color: var(--text-secondary);
  background: none;
  border: none;
  cursor: pointer;
  font-size: 14px;
  padding: 0.4rem 0.75rem;
  border-radius: 0.6rem;
  transition: color 0.18s ease;
}
.text-link:hover { color: #ef4444; }

.footer-link {
  color: var(--text-muted);
  text-decoration: none;
  transition: color 0.18s ease;
}
.footer-link:hover { color: var(--brand); }

.fade-slide-enter-active,
.fade-slide-leave-active {
  transition: opacity 0.25s ease, transform 0.25s ease;
}
.fade-slide-enter-from {
  opacity: 0;
  transform: translateY(10px);
}
.fade-slide-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}
</style>
