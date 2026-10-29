<template>
  <div class="max-w-6xl mx-auto px-5 py-8">
    <!-- 品牌欢迎区 -->
    <section class="hero mb-8">
      <div class="hero-glow"></div>
      <h1 class="hero-title">
        你好，我是 <span class="gradient-text">{{ settings.authorName || '博主' }}</span>
      </h1>
      <p class="hero-subtitle">{{ settings.introduction || '在这里，记录技术与生活的点滴。' }}</p>
    </section>

    <div class="flex gap-8 items-start">
      <!-- 文章列表 -->
      <main class="flex-1 min-w-0">
        <div class="flex items-center justify-between mb-5">
          <h2 class="section-title">最新文章</h2>
        </div>

        <div v-loading="loading">
          <ArticleCard
            v-for="article in articles"
            :key="article.id"
            :article="article"
            @click="goDetail(article.id)"
          />
        </div>
        <div v-if="!articles.length && !loading" class="text-center py-20 surface-card" style="color: var(--text-muted)">
          暂无文章
        </div>

        <el-pagination
          v-if="total > 0"
          v-model:current-page="query.current"
          :page-size="query.size"
          :total="total"
          layout="prev, pager, next"
          class="mt-6 justify-center"
          @current-change="loadData"
        />
      </main>

      <!-- 右边栏 -->
      <aside class="w-72 flex-shrink-0 hidden lg:block">
        <BlogInfoCard />
        <div class="mt-5"><CategoryTagCard /></div>
      </aside>
    </div>

    <BackToTop />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getArticleList } from '@/api/front'
import { useBlogSettings } from '@/composables/useBlogSettings'
import ArticleCard from '@/components/front/ArticleCard.vue'
import BlogInfoCard from '@/components/front/BlogInfoCard.vue'
import CategoryTagCard from '@/components/front/CategoryTagCard.vue'
import BackToTop from '@/components/front/BackToTop.vue'

const router = useRouter()
const { settings, loadSettings } = useBlogSettings()
const loading = ref(false)
const articles = ref([])
const total = ref(0)
const query = reactive({ current: 1, size: 10 })

async function loadData() {
  loading.value = true
  try {
    const res = await getArticleList(query)
    articles.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (e) {
    articles.value = []
  } finally {
    loading.value = false
  }
}

function goDetail(id) {
  router.push(`/article/${id}`)
}

onMounted(() => {
  loadSettings()
  loadData()
})
</script>

<style scoped>
.hero {
  position: relative;
  padding: 2.5rem 2rem;
  border-radius: 1.5rem;
  background: var(--surface);
  border: 1px solid var(--border);
  overflow: hidden;
  box-shadow: var(--shadow-soft);
}
.hero-glow {
  position: absolute;
  top: -60%;
  right: -10%;
  width: 400px;
  height: 400px;
  background: radial-gradient(circle, rgba(139, 92, 246, 0.18), transparent 70%);
  filter: blur(20px);
  pointer-events: none;
}
.hero-title {
  position: relative;
  font-size: 30px;
  font-weight: 700;
  margin: 0 0 10px;
  color: var(--text-primary);
  letter-spacing: -0.02em;
}
.hero-subtitle {
  position: relative;
  font-size: 15px;
  color: var(--text-secondary);
  margin: 0;
  line-height: 1.7;
}
.section-title {
  font-size: 18px;
  font-weight: 700;
  color: var(--text-primary);
  margin: 0;
  display: flex;
  align-items: center;
  gap: 10px;
}
.section-title::before {
  content: '';
  width: 4px;
  height: 18px;
  border-radius: 99px;
  background: var(--brand-gradient);
}
</style>
