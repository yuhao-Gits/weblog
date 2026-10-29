<template>
  <el-container class="h-screen admin-container">
    <!-- 侧边栏 -->
    <el-aside :width="isCollapse ? '68px' : '220px'" class="transition-all duration-300 admin-aside">
      <div class="sidebar-logo">
        <div class="logo-mark" :class="{ 'logo-mark-collapsed': isCollapse }">
          <span v-if="!isCollapse">W</span>
          <span v-else>W</span>
        </div>
        <div v-if="!isCollapse" class="logo-text">
          <div class="logo-name">Weblog</div>
          <div class="logo-sub">管理后台</div>
        </div>
      </div>

      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapse"
        :collapse-transition="false"
        router
        class="admin-menu"
      >
        <el-menu-item index="/admin/dashboard">
          <el-icon><DataLine /></el-icon>
          <template #title>仪表盘</template>
        </el-menu-item>
        <el-menu-item index="/admin/article">
          <el-icon><Document /></el-icon>
          <template #title>文章管理</template>
        </el-menu-item>
        <el-menu-item index="/admin/article/publish">
          <el-icon><EditPen /></el-icon>
          <template #title>发布文章</template>
        </el-menu-item>
        <el-menu-item index="/admin/category">
          <el-icon><Folder /></el-icon>
          <template #title>分类管理</template>
        </el-menu-item>
        <el-menu-item index="/admin/tag">
          <el-icon><PriceTag /></el-icon>
          <template #title>标签管理</template>
        </el-menu-item>
        <el-menu-item index="/admin/comment">
          <el-icon><ChatDotRound /></el-icon>
          <template #title>评论管理</template>
        </el-menu-item>
        <el-menu-item index="/admin/knowledge">
          <el-icon><Collection /></el-icon>
          <template #title>知识库</template>
        </el-menu-item>
        <el-menu-item index="/admin/settings">
          <el-icon><Setting /></el-icon>
          <template #title>博客设置</template>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <!-- 主区域 -->
    <el-container class="admin-main">
      <el-header class="admin-header">
        <div class="flex items-center gap-3">
          <button class="collapse-btn" @click="isCollapse = !isCollapse">
            <el-icon><Fold v-if="!isCollapse" /><Expand v-else /></el-icon>
          </button>
          <span class="header-title">后台管理</span>
        </div>
        <div class="flex items-center gap-2">
          <el-avatar :size="32" class="user-avatar">{{ (userStore.username || 'A').charAt(0).toUpperCase() }}</el-avatar>
          <span class="user-name">{{ userStore.username }}</span>
          <el-tag size="small" round class="role-tag">{{ userStore.role === 'ROLE_ADMIN' ? '管理员' : '游客' }}</el-tag>
          <el-divider direction="vertical" />
          <el-button text @click="showPwdDialog = true">修改密码</el-button>
          <el-button text @click="goFront">前台</el-button>
          <el-button text type="danger" @click="handleLogout">退出</el-button>
        </div>
      </el-header>
      <el-main class="admin-main-body">
        <router-view v-slot="{ Component }">
          <transition name="fade-slide" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </el-main>
    </el-container>

    <!-- 修改密码对话框 -->
    <el-dialog v-model="showPwdDialog" title="修改密码" width="420px" align-center>
      <el-form :model="pwdForm" label-width="90px">
        <el-form-item label="原密码">
          <el-input v-model="pwdForm.oldPassword" type="password" show-password />
        </el-form-item>
        <el-form-item label="新密码">
          <el-input v-model="pwdForm.newPassword" type="password" show-password />
        </el-form-item>
        <el-form-item label="确认密码">
          <el-input v-model="pwdForm.confirmPassword" type="password" show-password />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showPwdDialog = false">取消</el-button>
        <el-button type="primary" :loading="pwdLoading" @click="handleChangePassword">确定</el-button>
      </template>
    </el-dialog>
  </el-container>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { updatePassword } from '@/api/auth'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const isCollapse = ref(false)

const showPwdDialog = ref(false)
const pwdLoading = ref(false)
const pwdForm = reactive({ oldPassword: '', newPassword: '', confirmPassword: '' })

async function handleChangePassword() {
  if (!pwdForm.oldPassword || !pwdForm.newPassword) {
    ElMessage.warning('请填写完整密码信息')
    return
  }
  if (pwdForm.newPassword.length < 6) {
    ElMessage.warning('新密码至少 6 位')
    return
  }
  if (pwdForm.newPassword !== pwdForm.confirmPassword) {
    ElMessage.warning('两次输入的新密码不一致')
    return
  }
  pwdLoading.value = true
  try {
    await updatePassword({ password: pwdForm.oldPassword, newPassword: pwdForm.newPassword })
    ElMessage.success('密码修改成功，请重新登录')
    showPwdDialog.value = false
    userStore.logout()
    router.push('/login')
  } catch (e) {
  } finally {
    pwdLoading.value = false
  }
}

const activeMenu = computed(() => route.path)

function goFront() {
  router.push('/')
}

function handleLogout() {
  userStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.admin-container {
  background: var(--bg);
}

/* 侧边栏 */
.admin-aside {
  background: var(--surface);
  border-right: 1px solid var(--border);
  box-shadow: var(--shadow-soft);
}
.sidebar-logo {
  height: 64px;
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 0 18px;
  border-bottom: 1px solid var(--border);
}
.logo-mark {
  width: 36px;
  height: 36px;
  border-radius: 0.75rem;
  background: var(--brand-gradient);
  color: #fff;
  font-weight: 800;
  font-size: 18px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: var(--shadow-float);
  flex-shrink: 0;
  transition: all 0.3s ease;
}
.logo-text {
  line-height: 1.2;
}
.logo-name {
  font-size: 16px;
  font-weight: 700;
  color: var(--text-primary);
}
.logo-sub {
  font-size: 11px;
  color: var(--text-muted);
  margin-top: 2px;
}

/* 菜单 */
.admin-menu {
  border-right: none;
  background: transparent;
  padding: 12px 8px;
}
.admin-menu :deep(.el-menu-item) {
  height: 44px;
  line-height: 44px;
  margin: 4px 0;
  border-radius: 0.6rem;
  color: var(--text-secondary);
  font-weight: 500;
  transition: all 0.2s ease;
}
.admin-menu :deep(.el-menu-item:hover) {
  background: rgba(99, 102, 241, 0.08);
  color: var(--brand);
}
.admin-menu :deep(.el-menu-item.is-active) {
  background: var(--brand-gradient);
  color: #fff;
  box-shadow: var(--shadow-float);
}
.admin-menu :deep(.el-menu-item.is-active .el-icon) {
  color: #fff;
}

/* 主区域 */
.admin-main {
  background: var(--bg);
}
.admin-header {
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: var(--glass-bg);
  backdrop-filter: saturate(180%) blur(16px);
  -webkit-backdrop-filter: saturate(180%) blur(16px);
  border-bottom: 1px solid var(--border);
  padding: 0 20px;
}
.collapse-btn {
  width: 36px;
  height: 36px;
  border: none;
  border-radius: 0.6rem;
  background: transparent;
  color: var(--text-secondary);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  transition: all 0.2s ease;
}
.collapse-btn:hover {
  background: rgba(99, 102, 241, 0.08);
  color: var(--brand);
}
.header-title {
  font-size: 15px;
  font-weight: 600;
  color: var(--text-primary);
}
.user-avatar {
  background: var(--brand-gradient);
  color: #fff;
  font-weight: 700;
}
.user-name {
  font-size: 14px;
  font-weight: 500;
  color: var(--text-primary);
}
.role-tag {
  border: none;
}

.admin-main-body {
  padding: 20px;
  background: var(--bg);
}

.fade-slide-enter-active,
.fade-slide-leave-active {
  transition: opacity 0.2s ease, transform 0.2s ease;
}
.fade-slide-enter-from {
  opacity: 0;
  transform: translateY(8px);
}
.fade-slide-leave-to {
  opacity: 0;
  transform: translateY(-8px);
}
</style>
