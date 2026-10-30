<template>
  <div class="max-w-6xl mx-auto px-5 py-8">
    <div class="flex gap-8 items-start">
      <!-- 正文 -->
      <main class="flex-1 min-w-0">
        <div v-loading="loading" class="article-card">
          <template v-if="article">
            <div v-if="article.categoryName" class="article-category">{{ article.categoryName }}</div>
            <h1 class="article-heading">{{ article.title }}</h1>

            <div class="article-meta">
              <span><el-icon><Calendar /></el-icon> {{ formatDate(article.createTime) }}</span>
              <span><el-icon><View /></el-icon> {{ article.readCount || 0 }} 阅读</span>
              <span v-if="wordCount"><el-icon><Timer /></el-icon> 约 {{ readMinutes }} 分钟</span>
              <span v-if="article.tagNames" class="meta-tags">
                <el-icon><PriceTag /></el-icon> {{ article.tagNames }}
              </span>
            </div>

            <div v-highlight v-viewer class="article-content" v-html="article.contentHtml"></div>

            <div class="article-footer">
              <el-button round plain @click="$router.back()">
                <el-icon><Back /></el-icon> 返回
              </el-button>
            </div>

            <!-- 评论区 -->
            <CommentList v-if="article" :article-id="route.params.id" />
          </template>

          <div v-else-if="!loading" class="text-center py-20" style="color: var(--text-muted)">文章不存在</div>
        </div>
      </main>

      <!-- 右边栏：目录 + 博主卡片 -->
      <aside class="w-72 flex-shrink-0 hidden lg:block">
        <ArticleToc v-if="!loading && article" />
        <div class="mt-5"><BlogInfoCard /></div>
      </aside>
    </div>

    <BackToTop />
  </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick, watch } from 'vue'
import { useRoute } from 'vue-router'
import { getArticleDetail } from '@/api/front'
import ArticleToc from '@/components/front/ArticleToc.vue'
import BlogInfoCard from '@/components/front/BlogInfoCard.vue'
import BackToTop from '@/components/front/BackToTop.vue'
import CommentList from '@/components/front/CommentList.vue'

const route = useRoute()
const loading = ref(false)
const article = ref(null)

const wordCount = computed(() => {
  if (!article.value) return 0
  const html = article.value.contentHtml || article.value.summary || ''
  const text = html.replace(/<[^>]+>/g, '').trim()
  return text.length
})

const readMinutes = computed(() => {
  const minutes = Math.round(wordCount.value / 300)
  return minutes < 1 ? 1 : minutes
})

async function loadDetail() {
  loading.value = true
  article.value = null
  try {
    const res = await getArticleDetail(route.params.id)
    article.value = res.data
    await nextTick()
    window.scrollTo({ top: 0 })
  } catch (e) {
    article.value = null
  } finally {
    loading.value = false
  }
}

function formatDate(dateStr) {
  return dateStr ? dateStr.substring(0, 10) : ''
}

watch(() => route.params.id, loadDetail)
onMounted(loadDetail)
</script>

<style scoped>
.article-card {
  background: var(--surface);
  border: 1px solid var(--border);
  border-radius: 1.25rem;
  padding: 40px;
  box-shadow: var(--shadow-soft);
}
.article-category {
  display: inline-block;
  padding: 4px 14px;
  font-size: 13px;
  font-weight: 600;
  color: var(--brand);
  background: rgba(99, 102, 241, 0.1);
  border-radius: 99px;
  margin-bottom: 16px;
}
.article-heading {
  font-size: 32px;
  font-weight: 800;
  color: var(--text-primary);
  line-height: 1.35;
  letter-spacing: -0.02em;
  margin: 0 0 20px;
}
.article-meta {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 20px;
  font-size: 13.5px;
  color: var(--text-muted);
  padding-bottom: 20px;
  margin-bottom: 28px;
  border-bottom: 1px solid var(--border);
}
.article-meta span {
  display: inline-flex;
  align-items: center;
  gap: 5px;
}
.meta-tags { max-width: 280px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }

.article-footer {
  margin-top: 40px;
  padding-top: 24px;
  border-top: 1px solid var(--border);
}

@media (max-width: 640px) {
  .article-card { padding: 22px; border-radius: 1rem; }
  .article-heading { font-size: 24px; }
}
</style>
