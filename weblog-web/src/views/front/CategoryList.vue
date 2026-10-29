<template>
  <div class="max-w-6xl mx-auto px-5 py-8">
    <div class="flex gap-8 items-start">
      <main class="flex-1 min-w-0">
        <div class="category-header">
          <div>
            <h1 class="text-xl font-bold" style="color: var(--text-primary)">分类 · {{ categoryName }}</h1>
            <span class="text-sm" style="color: var(--text-muted)">共 {{ total }} 篇文章</span>
          </div>
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
          该分类下暂无文章
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

      <aside class="w-72 flex-shrink-0 hidden lg:block">
        <CategoryTagCard />
      </aside>
    </div>

    <BackToTop />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getArticleByCategory } from '@/api/front'
import ArticleCard from '@/components/front/ArticleCard.vue'
import CategoryTagCard from '@/components/front/CategoryTagCard.vue'
import BackToTop from '@/components/front/BackToTop.vue'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const articles = ref([])
const total = ref(0)
const query = reactive({ current: 1, size: 10 })

const categoryName = computed(() => route.params.name || '')

async function loadData() {
  loading.value = true
  try {
    const res = await getArticleByCategory({
      current: query.current,
      size: query.size,
      categoryName: categoryName.value
    })
    articles.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (e) {
    articles.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

function goDetail(id) {
  router.push(`/article/${id}`)
}

watch(() => route.params.name, () => {
  query.current = 1
  loadData()
})

onMounted(loadData)
</script>

<style scoped>
.category-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  padding: 20px 24px;
  margin-bottom: 18px;
  background: var(--surface);
  border: 1px solid var(--border);
  border-radius: 1rem;
  box-shadow: var(--shadow-soft);
  border-left: 4px solid var(--brand);
}
</style>
