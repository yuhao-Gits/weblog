<template>
  <div>
    <!-- 分类卡片 -->
    <div class="sidebar-card">
      <h3 class="sidebar-title">分类</h3>
      <ul class="sidebar-list">
        <li v-for="c in categories" :key="c.id">
          <router-link :to="`/category/${encodeURIComponent(c.name)}`" class="sidebar-row">
            <span class="row-label">
              <span class="row-dot"></span>
              <span class="truncate">{{ c.name }}</span>
            </span>
            <span class="sidebar-count">{{ c.articlesTotal || 0 }}</span>
          </router-link>
        </li>
        <li v-if="!categories.length" class="text-xs py-2" style="color: var(--text-muted)">暂无分类</li>
      </ul>
    </div>

    <!-- 标签卡片 -->
    <div class="sidebar-card mt-5">
      <h3 class="sidebar-title">标签</h3>
      <div class="flex flex-wrap gap-2">
        <router-link
          v-for="t in tags"
          :key="t.id"
          :to="`/tag/${encodeURIComponent(t.name)}`"
          class="tag-chip"
        >
          {{ t.name }}
          <span class="opacity-60">{{ t.articlesTotal || 0 }}</span>
        </router-link>
        <span v-if="!tags.length" class="text-xs" style="color: var(--text-muted)">暂无标签</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { listAllCategories } from '@/api/category'
import { listAllTags } from '@/api/tag'

const categories = ref([])
const tags = ref([])

onMounted(async () => {
  try {
    const [cRes, tRes] = await Promise.all([listAllCategories(), listAllTags()])
    categories.value = cRes.data || []
    tags.value = tRes.data || []
  } catch (e) {
    // 侧边栏数据获取失败不影响正文
  }
})
</script>

<style scoped>
.sidebar-card {
  background: var(--surface);
  border: 1px solid var(--border);
  border-radius: 1rem;
  padding: 18px;
  box-shadow: var(--shadow-soft);
}
.sidebar-title {
  font-size: 15px;
  font-weight: 700;
  color: var(--text-primary);
  margin: 0 0 14px;
  padding-bottom: 10px;
  border-bottom: 1px solid var(--border);
  display: flex;
  align-items: center;
  gap: 8px;
}
.sidebar-title::before {
  content: '';
  width: 4px;
  height: 14px;
  border-radius: 99px;
  background: var(--brand-gradient);
}
.sidebar-list {
  list-style: none;
  padding: 0;
  margin: 0;
}
.sidebar-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
  padding: 8px 6px;
  color: var(--text-secondary);
  font-size: 14px;
  text-decoration: none;
  border-radius: 0.5rem;
  transition: all 0.15s ease;
}
.sidebar-row:hover {
  color: var(--brand);
  background: rgba(99, 102, 241, 0.06);
}
.row-label {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  min-width: 0;
}
.row-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: var(--brand-gradient);
  flex-shrink: 0;
}
.sidebar-count {
  font-size: 12px;
  color: var(--text-muted);
  background: var(--surface-muted);
  border-radius: 99px;
  padding: 1px 9px;
  font-variant-numeric: tabular-nums;
}
.tag-chip {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 4px 12px;
  background: rgba(99, 102, 241, 0.08);
  color: var(--brand);
  border-radius: 99px;
  font-size: 12.5px;
  text-decoration: none;
  transition: all 0.18s ease;
}
.tag-chip:hover {
  background: var(--brand-gradient);
  color: #fff;
  box-shadow: var(--shadow-float);
}
</style>
