<template>
  <div class="max-w-6xl mx-auto px-5 py-8">
    <div class="flex items-center justify-between mb-6">
      <h1 class="page-title">知识库</h1>
      <span class="text-sm" style="color: var(--text-muted)">{{ flattenDirs.length }} 个目录</span>
    </div>

    <div v-if="loading" class="text-center py-20" style="color: var(--text-muted)">加载中...</div>

    <div v-else-if="directories.length === 0" class="text-center py-20" style="color: var(--text-muted)">
      知识库暂无内容
    </div>

    <div v-else>
      <!-- 目录卡片网格 -->
      <div class="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-5">
        <div
          v-for="dir in flattenDirs"
          :key="dir.id"
          class="dir-card"
        >
          <div class="dir-icon">
            <el-icon><Folder /></el-icon>
          </div>
          <div class="dir-body">
            <div class="dir-title">{{ dir.title }}</div>
            <div class="dir-meta">
              <span>{{ dir.articleCount }} 篇文章</span>
              <span v-if="dir.level > 0" class="level-tag">L{{ dir.level }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 目录树导航 -->
      <div class="tree-card mt-8">
        <h2 class="tree-title">目录结构</h2>
        <el-tree
          :data="directories"
          :props="{ label: 'title', children: 'children' }"
          node-key="id"
          default-expand-all
          class="kb-tree"
        >
          <template #default="{ data }">
            <span class="tree-node">
              <el-icon class="tree-folder"><Folder /></el-icon>
              <span class="tree-label">{{ data.title }}</span>
              <span class="tree-count">{{ data.articleCount }}</span>
            </span>
          </template>
        </el-tree>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { Folder } from '@element-plus/icons-vue'
import { frontDirectory } from '@/api/knowledge'

const directories = ref([])
const loading = ref(false)

// 扁平化目录（用于网格展示）
const flattenDirs = computed(() => {
  const result = []
  const walk = (list, level) => {
    for (const dir of list) {
      result.push({ ...dir, level })
      if (dir.children && dir.children.length) walk(dir.children, level + 1)
    }
  }
  walk(directories.value, 0)
  return result
})

onMounted(async () => {
  loading.value = true
  try {
    const res = await frontDirectory()
    directories.value = res.data || []
  } catch (e) {
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.page-title {
  font-size: 26px;
  font-weight: 800;
  color: var(--text-primary);
  margin: 0;
  letter-spacing: -0.02em;
  display: flex;
  align-items: center;
  gap: 10px;
}
.page-title::before {
  content: '';
  width: 5px;
  height: 24px;
  border-radius: 99px;
  background: var(--brand-gradient);
}

.dir-card {
  display: flex;
  align-items: flex-start;
  gap: 14px;
  padding: 20px;
  background: var(--surface);
  border: 1px solid var(--border);
  border-radius: 1rem;
  box-shadow: var(--shadow-soft);
  cursor: pointer;
  transition: transform 0.22s cubic-bezier(0.22, 1, 0.36, 1), box-shadow 0.22s ease, border-color 0.22s ease;
}
.dir-card:hover {
  transform: translateY(-3px);
  box-shadow: var(--shadow-card);
  border-color: rgba(99, 102, 241, 0.25);
}
.dir-icon {
  flex-shrink: 0;
  width: 44px;
  height: 44px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 0.75rem;
  background: var(--brand-soft);
  color: var(--brand);
  font-size: 22px;
}
.dir-title {
  font-size: 16px;
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: 6px;
}
.dir-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: var(--text-muted);
}
.level-tag {
  padding: 0 8px;
  border-radius: 99px;
  background: var(--surface-muted);
  font-size: 11px;
}

.tree-card {
  background: var(--surface);
  border: 1px solid var(--border);
  border-radius: 1rem;
  padding: 24px;
  box-shadow: var(--shadow-soft);
}
.tree-title {
  font-size: 17px;
  font-weight: 700;
  color: var(--text-primary);
  margin: 0 0 16px;
  display: flex;
  align-items: center;
  gap: 8px;
}
.tree-title::before {
  content: '';
  width: 4px;
  height: 16px;
  border-radius: 99px;
  background: var(--brand-gradient);
}
.tree-node {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
}
.tree-folder { color: var(--brand); }
.tree-label { color: var(--text-primary); }
.tree-count {
  font-size: 12px;
  color: var(--text-muted);
  background: var(--surface-muted);
  padding: 0 8px;
  border-radius: 99px;
}
</style>
