<template>
  <el-dialog
    v-model="visible"
    title="站内搜索"
    width="560px"
    :show-close="true"
    class="search-dialog"
    @closed="reset"
  >
    <div class="search-box">
      <el-input
        v-model="keyword"
        placeholder="输入关键词搜索文章…"
        size="large"
        clearable
        @keyup.enter="doSearch"
        @input="onInput"
      >
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>
      <el-button type="primary" size="large" :loading="loading" @click="doSearch">搜索</el-button>
    </div>

    <div v-loading="loading" class="search-result mt-4">
      <template v-if="!loading">
        <div v-if="searched && results.length === 0" class="empty">未找到相关文章</div>
        <ul v-if="results.length" class="result-list">
          <li v-for="item in results" :key="item.id" class="result-item" @click="goDetail(item.id)">
            <div class="result-title" v-html="item.title"></div>
            <div class="result-highlight" v-html="item.highlight"></div>
          </li>
        </ul>
      </template>
    </div>
  </el-dialog>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { Search } from '@element-plus/icons-vue'
import { searchArticle } from '@/api/front'
import { ElMessage } from 'element-plus'

const router = useRouter()
const visible = ref(false)
const keyword = ref('')
const results = ref([])
const loading = ref(false)
const searched = ref(false)

function open() {
  visible.value = true
  keyword.value = ''
  results.value = []
  searched.value = false
}

function close() {
  visible.value = false
}

function reset() {
  results.value = []
  searched.value = false
}

function onInput() {
  if (!keyword.value.trim()) {
    results.value = []
    searched.value = false
  }
}

async function doSearch() {
  const kw = keyword.value.trim()
  if (!kw) {
    ElMessage.warning('请输入搜索关键词')
    return
  }
  loading.value = true
  try {
    const res = await searchArticle(kw)
    results.value = res.data || []
    searched.value = true
    if (results.value.length === 0) {
      ElMessage.info('未找到相关文章')
    }
  } catch (e) {
  } finally {
    loading.value = false
  }
}

function goDetail(id) {
  close()
  router.push(`/article/${id}`)
}

defineExpose({ open, close })
</script>

<style scoped>
.search-box {
  display: flex;
  gap: 8px;
}
.search-result {
  min-height: 80px;
  max-height: 360px;
  overflow-y: auto;
}
.empty {
  text-align: center;
  color: var(--text-muted);
  padding: 24px 0;
  font-size: 14px;
}
.result-list {
  list-style: none;
  margin: 0;
  padding: 0;
}
.result-item {
  padding: 12px 10px;
  border-bottom: 1px solid var(--border);
  cursor: pointer;
  border-radius: 8px;
  transition: background 0.15s;
}
.result-item:hover {
  background: rgba(99, 102, 241, 0.06);
}
.result-title {
  font-size: 15px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 6px;
}
.result-highlight {
  font-size: 13px;
  color: var(--text-secondary);
  line-height: 1.6;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}
.result-highlight :deep(em) {
  color: #ef4444;
  font-style: normal;
  font-weight: 600;
  background: rgba(239, 68, 68, 0.08);
  padding: 0 2px;
  border-radius: 3px;
}
</style>
