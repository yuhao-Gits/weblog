<template>
  <div class="login-page">
    <!-- 背景光晕 -->
    <div class="bg-glow glow-1"></div>
    <div class="bg-glow glow-2"></div>

    <div class="login-card">
      <div class="brand-mark">
        <span class="brand-logo">W</span>
      </div>
      <h1 class="login-title">Weblog</h1>
      <p class="login-subtitle">欢迎回来，请登录你的账号</p>

      <el-form ref="formRef" :model="form" :rules="rules" size="large" class="login-form" @submit.prevent="handleLogin">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="用户名" :prefix-icon="User" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="密码" :prefix-icon="Lock" show-password @keyup.enter="handleLogin" />
        </el-form-item>
        <el-form-item>
          <SliderCaptcha ref="captchaRef" @success="onCaptchaSuccess" />
        </el-form-item>
        <button type="button" class="gradient-btn login-btn" :disabled="loading" @click="handleLogin">
          {{ loading ? '登录中…' : '登 录' }}
        </button>
      </el-form>

      <div class="login-hint">
        测试账号：admin / 123456（管理员）· test / test（游客）
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { User, Lock } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import SliderCaptcha from '@/components/SliderCaptcha.vue'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref()
const loading = ref(false)
const captchaRef = ref()
const captchaInfo = ref(null)

const form = reactive({ username: 'admin', password: '123456' })
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

function onCaptchaSuccess(info) {
  captchaInfo.value = info
}

async function handleLogin() {
  await formRef.value.validate()
  if (!captchaInfo.value || !captchaInfo.value.passToken) {
    ElMessage.warning('请先完成滑块验证')
    return
  }
  loading.value = true
  try {
    await userStore.login({
      username: form.username,
      password: form.password,
      captchaToken: captchaInfo.value.passToken
    })
    ElMessage.success('登录成功')
    router.push('/admin/dashboard')
  } catch (e) {
    // 登录失败重置验证码
    captchaInfo.value = null
    captchaRef.value?.loadCaptcha()
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  position: relative;
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--bg);
  overflow: hidden;
}
.bg-glow {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  opacity: 0.5;
  pointer-events: none;
}
.glow-1 {
  width: 480px;
  height: 480px;
  top: -120px;
  left: -80px;
  background: radial-gradient(circle, rgba(99, 102, 241, 0.5), transparent 70%);
}
.glow-2 {
  width: 420px;
  height: 420px;
  bottom: -100px;
  right: -60px;
  background: radial-gradient(circle, rgba(217, 70, 239, 0.4), transparent 70%);
}
.login-card {
  position: relative;
  width: 400px;
  padding: 40px 36px;
  background: var(--surface);
  border: 1px solid var(--border);
  border-radius: 1.5rem;
  box-shadow: var(--shadow-card);
}
.brand-mark {
  display: flex;
  justify-content: center;
  margin-bottom: 16px;
}
.brand-logo {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 56px;
  height: 56px;
  border-radius: 1rem;
  background: var(--brand-gradient);
  color: #fff;
  font-size: 26px;
  font-weight: 800;
  box-shadow: var(--shadow-float);
}
.login-title {
  text-align: center;
  font-size: 24px;
  font-weight: 800;
  color: var(--text-primary);
  margin: 0 0 4px;
  letter-spacing: -0.02em;
}
.login-subtitle {
  text-align: center;
  font-size: 14px;
  color: var(--text-muted);
  margin: 0 0 28px;
}
.login-form {
  margin: 0;
}
.login-btn {
  width: 100%;
  height: 44px;
  font-size: 15px;
  margin-top: 4px;
}
.login-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
.login-hint {
  margin-top: 20px;
  text-align: center;
  font-size: 12px;
  color: var(--text-faint);
}
</style>
