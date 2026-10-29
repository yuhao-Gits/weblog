<template>
  <el-card shadow="never">
    <template #header><span>博客设置</span></template>
    <el-form :model="form" label-width="100px" v-loading="loading" style="max-width: 600px">
      <el-form-item label="博客名称">
        <el-input v-model="form.blogName" />
      </el-form-item>
      <el-form-item label="作者名称">
        <el-input v-model="form.authorName" />
      </el-form-item>
      <el-form-item label="个人简介">
        <el-input v-model="form.introduction" type="textarea" :rows="3" />
      </el-form-item>
      <el-form-item label="头像 URL">
        <el-input v-model="form.avatar" placeholder="https://..." />
      </el-form-item>
      <el-form-item label="Logo URL">
        <el-input v-model="form.logo" placeholder="https://..." />
      </el-form-item>
      <el-form-item label="GitHub">
        <el-input v-model="form.githubHome" placeholder="https://github.com/..." />
      </el-form-item>
      <el-form-item label="Gitee">
        <el-input v-model="form.giteeHome" placeholder="https://gitee.com/..." />
      </el-form-item>

      <el-divider content-position="left">评论设置</el-divider>

      <el-form-item label="评论审核">
        <el-switch v-model="form.commentAudit" :active-value="1" :inactive-value="0" active-text="开启审核" />
      </el-form-item>
      <el-form-item label="敏感词">
        <el-input v-model="form.sensitiveWords" type="textarea" :rows="2" placeholder="多个敏感词用逗号分隔，如：广告,赌博" />
      </el-form-item>

      <el-divider content-position="left">评论邮件通知（选填）</el-divider>

      <el-form-item label="SMTP 服务器">
        <el-input v-model="form.emailHost" placeholder="smtp.qq.com" />
      </el-form-item>
      <el-form-item label="端口">
        <el-input-number v-model="form.emailPort" :min="1" :max="65535" />
      </el-form-item>
      <el-form-item label="发件邮箱">
        <el-input v-model="form.emailUsername" placeholder="xxx@qq.com" />
      </el-form-item>
      <el-form-item label="授权码">
        <el-input v-model="form.emailPassword" type="password" show-password placeholder="SMTP 授权码" />
      </el-form-item>

      <el-form-item>
        <el-button type="primary" :loading="saving" @click="handleSave">保存</el-button>
      </el-form-item>
    </el-form>
  </el-card>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getBlogSettings, updateBlogSettings } from '@/api/settings'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const saving = ref(false)
const form = reactive({
  blogName: '',
  authorName: '',
  introduction: '',
  avatar: '',
  logo: '',
  githubHome: '',
  giteeHome: '',
  sensitiveWords: '',
  commentAudit: 0,
  emailHost: '',
  emailPort: 465,
  emailUsername: '',
  emailPassword: '',
  emailFrom: ''
})

async function loadSettings() {
  loading.value = true
  try {
    const res = await getBlogSettings()
    Object.assign(form, res.data)
  } catch (e) {} finally {
    loading.value = false
  }
}

async function handleSave() {
  saving.value = true
  try {
    await updateBlogSettings(form)
    ElMessage.success('保存成功')
  } catch (e) {} finally {
    saving.value = false
  }
}

onMounted(loadSettings)
</script>