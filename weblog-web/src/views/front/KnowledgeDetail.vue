<template>
  <div class="max-w-6xl mx-auto px-5 py-8">
    <div class="flex gap-8 items-start">
      <main class="flex-1 min-w-0">
        <div v-if="loading" class="text-center py-20" style="color: var(--text-muted)">加载中...</div>

        <div v-else-if="!article" class="text-center py-20" style="color: var(--text-muted)">文章不存在或未发布</div>

        <div v-else class="kb-card">
          <div class="kb-breadcrumb">
            <el-icon><Folder /></el-icon>
            <span>知识库</span>
            <el-icon class="sep"><ArrowRight /></el-icon>
            <span class="current">{{ article.directoryTitle || '文章' }}</span>
          </div>

          <h1 class="kb-title">{{ article.title }}</h1>

          <div class="kb-meta">
            <span><el-icon><Calendar /></el-icon> {{ article.createTime }}</span>
            <span v-if="article.summary" class="summary">· {{ article.summary }}</span>
          </div>

          <!-- 正文渲染 -->
          <div class="knowledge-content" v-html="article.contentHtml || article.content"></div>

          <!-- 上下篇导航 -->
          <div class="kb-pager">
            <router-link v-if="article.prevId" :to="`/knowledge/${article.prevId}`" class="pager-btn">
              <el-icon><ArrowLeft /></el-icon> 上一篇
            </router-link>
            <span v-else></span>
            <router-link v-if="article.nextId" :to="`/knowledge/${article.nextId}`" class="pager-btn">
              下一篇 <el-icon><ArrowRight /></el-icon>
            </router-link>
          </div>
        </div>
      </main>

      <aside class="w-72 flex-shrink-0 hidden lg:block">
        <BlogInfoCard />
      </aside>
    </div>

    <BackToTop />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { frontArticleDetail } from '@/api/knowledge'
import { Folder, ArrowRight, ArrowLeft, Calendar } from '@element-plus/icons-vue'
import BlogInfoCard from '@/components/front/BlogInfoCard.vue'
import BackToTop from '@/components/front/BackToTop.vue'

const route = useRoute()
const article = ref(null)
const loading = ref(false)

async function loadArticle() {
  loading.value = true
  try {
    const res = await frontArticleDetail(route.params.id)
    article.value = res.data
  } catch (e) {
    article.value = null
  } finally {
    loading.value = false
  }
}

onMounted(loadArticle)
</script>

<style scoped>
.kb-card {
  background: var(--surface);
  border: 1px solid var(--border);
  border-radius: 1.25rem;
  padding: 40px;
  box-shadow: var(--shadow-soft);
}
.kb-breadcrumb {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: var(--text-muted);
  margin-bottom: 18px;
}
.kb-breadcrumb .sep { margin: 0 2px; }
.kb-breadcrumb .current { color: var(--brand); font-weight: 600; }
.kb-title {
  font-size: 32px;
  font-weight: 800;
  color: var(--text-primary);
  letter-spacing: -0.02em;
  line-height: 1.35;
  margin: 0 0 20px;
}
.kb-meta {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13.5px;
  color: var(--text-muted);
  padding-bottom: 20px;
  margin-bottom: 28px;
  border-bottom: 1px solid var(--border);
}
.kb-meta span { display: inline-flex; align-items: center; gap: 5px; }
.kb-pager {
  display: flex;
  justify-content: space-between;
  margin-top: 40px;
  padding-top: 24px;
  border-top: 1px solid var(--border);
}
.pager-btn {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  border-radius: 99px;
  background: rgba(99, 102, 241, 0.08);
  color: var(--brand);
  font-size: 14px;
  font-weight: 600;
  text-decoration: none;
  transition: all 0.18s ease;
}
.pager-btn:hover {
  background: var(--brand-gradient);
  color: #fff;
  box-shadow: var(--shadow-float);
}

@media (max-width: 640px) {
  .kb-card { padding: 22px; }
  .kb-title { font-size: 24px; }
}
</style>
