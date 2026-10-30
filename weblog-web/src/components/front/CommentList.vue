<template>
  <div class="comment-section">
    <h3 class="comment-title">评论（{{ total }}）</h3>

    <!-- 发布评论 -->
    <div class="comment-form">
      <div class="form-row">
        <el-input v-model="form.nickname" placeholder="昵称" class="form-item" />
        <el-input v-model="form.email" placeholder="邮箱（选填）" class="form-item" />
        <el-input v-model="form.qq" placeholder="QQ（选填，用于头像）" class="form-item" />
      </div>
      <el-input
        v-model="form.content"
        type="textarea"
        :rows="3"
        placeholder="写下你的评论…"
        class="form-textarea"
      />
      <div class="form-actions">
        <span v-if="replyTarget" class="reply-tip">回复 @{{ replyTarget.nickname }}</span>
        <el-button v-if="replyTarget" size="small" @click="cancelReply">取消回复</el-button>
        <el-button type="primary" size="small" :loading="submitting" @click="submit">发表评论</el-button>
      </div>
    </div>

    <!-- 评论列表 -->
    <div v-loading="loading" class="comment-list">
      <div v-if="!loading && comments.length === 0" class="comment-empty">暂无评论，来抢沙发～</div>
      <div v-for="c in comments" :key="c.id" class="comment-item">
        <div class="comment-avatar">
          <el-avatar :size="40" :src="c.avatar">
            <span>{{ c.nickname?.charAt(0) }}</span>
          </el-avatar>
        </div>
        <div class="comment-body">
          <div class="comment-meta">
            <span class="comment-nickname">{{ c.nickname }}</span>
            <span class="comment-time">{{ c.createTime }}</span>
          </div>
          <div class="comment-content">{{ c.content }}</div>
          <div class="comment-actions">
            <el-button text size="small" @click="setReply(c)">回复</el-button>
          </div>
          <!-- 子评论 -->
          <div v-if="c.children && c.children.length" class="comment-children">
            <div v-for="child in c.children" :key="child.id" class="comment-item child-item">
              <div class="comment-avatar">
                <el-avatar :size="32" :src="child.avatar">
                  <span>{{ child.nickname?.charAt(0) }}</span>
                </el-avatar>
              </div>
              <div class="comment-body">
                <div class="comment-meta">
                  <span class="comment-nickname">{{ child.nickname }}</span>
                  <span v-if="child.replyUserName" class="reply-to">回复 @{{ child.replyUserName }}</span>
                  <span class="comment-time">{{ child.createTime }}</span>
                </div>
                <div class="comment-content">{{ child.content }}</div>
                <div class="comment-actions">
                  <el-button text size="small" @click="setReply(child)">回复</el-button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { publishComment, getCommentList } from '@/api/comment'
import { ElMessage } from 'element-plus'

const props = defineProps({
  articleId: { type: String, required: true }
})

const comments = ref([])
const total = ref(0)
const loading = ref(false)
const submitting = ref(false)
const replyTarget = ref(null)

const form = reactive({ nickname: '', email: '', qq: '', content: '' })

async function load() {
  loading.value = true
  try {
    const res = await getCommentList(props.articleId)
    comments.value = res.data || []
    total.value = countAll(comments.value)
  } catch (e) {} finally {
    loading.value = false
  }
}

function countAll(list) {
  let n = 0
  for (const c of list) {
    n += 1
    if (c.children) n += c.children.length
  }
  return n
}

function setReply(c) {
  replyTarget.value = c
}

function cancelReply() {
  replyTarget.value = null
}

async function submit() {
  if (!form.nickname.trim()) { ElMessage.warning('请输入昵称'); return }
  if (!form.content.trim()) { ElMessage.warning('请输入评论内容'); return }
  submitting.value = true
  try {
    const payload = {
      articleId: props.articleId,
      nickname: form.nickname,
      email: form.email,
      qq: form.qq,
      content: form.content,
      parentId: replyTarget.value ? replyTarget.value.id : 0,
      replyUserName: replyTarget.value ? replyTarget.value.nickname : undefined
    }
    await publishComment(payload)
    ElMessage.success('评论成功')
    form.content = ''
    replyTarget.value = null
    load()
  } catch (e) {} finally {
    submitting.value = false
  }
}

onMounted(load)
</script>

<style scoped>
.comment-section {
  margin-top: 40px;
}
.comment-title {
  font-size: 18px;
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: 16px;
  display: flex;
  align-items: center;
  gap: 8px;
}
.comment-title::before {
  content: '';
  width: 4px;
  height: 16px;
  border-radius: 99px;
  background: var(--brand-gradient);
}
.comment-form {
  background: var(--surface-muted);
  border-radius: 0.75rem;
  padding: 16px;
  margin-bottom: 20px;
}
.form-row {
  display: flex;
  gap: 12px;
  margin-bottom: 12px;
}
.form-item {
  flex: 1;
}
.form-textarea {
  margin-bottom: 12px;
}
.form-actions {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 12px;
}
.reply-tip {
  color: var(--brand);
  font-size: 13px;
  margin-right: auto;
}
.comment-list {
  min-height: 60px;
}
.comment-empty {
  text-align: center;
  color: var(--text-muted);
  padding: 24px 0;
}
.comment-item {
  display: flex;
  gap: 12px;
  padding: 14px 0;
  border-bottom: 1px solid var(--border);
}
.comment-avatar {
  flex-shrink: 0;
}
.comment-body {
  flex: 1;
  min-width: 0;
}
.comment-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 4px;
}
.comment-nickname {
  font-weight: 600;
  color: var(--text-primary);
  font-size: 14px;
}
.reply-to {
  color: var(--brand);
  font-size: 13px;
}
.comment-time {
  color: var(--text-muted);
  font-size: 12px;
}
.comment-content {
  color: var(--text-secondary);
  font-size: 14px;
  line-height: 1.6;
  word-break: break-word;
}
.comment-actions {
  margin-top: 4px;
}
.comment-children {
  margin-top: 8px;
  background: var(--surface-muted);
  border-radius: 0.5rem;
  padding: 4px 12px;
}
.child-item {
  border-bottom: 1px solid var(--border);
}
.child-item:last-child {
  border-bottom: none;
}
</style>
