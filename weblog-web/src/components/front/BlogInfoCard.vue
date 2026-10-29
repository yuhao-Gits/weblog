<template>
  <div class="sidebar-card">
    <div class="avatar-wrap">
      <div class="avatar-ring">
        <el-avatar v-if="settings.avatar" :size="80" :src="settings.avatar" />
        <el-avatar v-else :size="80" class="avatar-fallback">{{ (settings.authorName || 'W').charAt(0) }}</el-avatar>
      </div>
    </div>

    <div class="text-center">
      <div class="author-name">{{ settings.authorName || '博主' }}</div>
      <div class="author-intro">{{ settings.introduction || '这个人很懒，什么都没留下' }}</div>
    </div>

    <div class="divider"></div>

    <div class="stats-grid">
      <div class="stat-cell">
        <div class="stat-num">{{ stat.articleTotal || 0 }}</div>
        <div class="stat-label">文章</div>
      </div>
      <div class="stat-cell">
        <div class="stat-num">{{ stat.categoryTotal || 0 }}</div>
        <div class="stat-label">分类</div>
      </div>
      <div class="stat-cell">
        <div class="stat-num">{{ stat.tagTotal || 0 }}</div>
        <div class="stat-label">标签</div>
      </div>
      <div class="stat-cell">
        <div class="stat-num">{{ stat.viewTotal || 0 }}</div>
        <div class="stat-label">访问量</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import request from '@/utils/request'
import { useBlogSettings } from '@/composables/useBlogSettings'

const { settings } = useBlogSettings()
const stat = reactive({ articleTotal: 0, categoryTotal: 0, tagTotal: 0, viewTotal: 0 })

onMounted(async () => {
  try {
    const res = await request.get('/blog/statistics')
    Object.assign(stat, res.data || {})
  } catch (e) {
    // 统计接口异常时保持 0，不影响侧边栏显示
  }
})
</script>

<style scoped>
.sidebar-card {
  background: var(--surface);
  border: 1px solid var(--border);
  border-radius: 1rem;
  padding: 24px 20px;
  box-shadow: var(--shadow-soft);
}
.avatar-wrap {
  display: flex;
  justify-content: center;
  margin-bottom: 14px;
}
.avatar-ring {
  padding: 4px;
  border-radius: 50%;
  background: var(--brand-gradient);
  box-shadow: var(--shadow-float);
}
.avatar-fallback {
  background: var(--brand-gradient);
  color: #fff;
  font-size: 30px;
  font-weight: 700;
}
.author-name {
  font-size: 17px;
  font-weight: 700;
  color: var(--text-primary);
}
.author-intro {
  margin-top: 6px;
  font-size: 13px;
  color: var(--text-muted);
  line-height: 1.6;
}
.divider {
  height: 1px;
  background: var(--border);
  margin: 18px 0;
}
.stats-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px 8px;
  text-align: center;
}
.stat-num {
  font-size: 20px;
  font-weight: 800;
  color: var(--text-primary);
  line-height: 1.2;
  font-variant-numeric: tabular-nums;
}
.stat-label {
  font-size: 12px;
  color: var(--text-muted);
  margin-top: 3px;
}
</style>
