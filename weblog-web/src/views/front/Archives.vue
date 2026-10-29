<template>
  <div class="max-w-6xl mx-auto px-5 py-8">
    <div class="flex gap-8 items-start">
      <main class="flex-1 min-w-0">
        <div class="archive-header">
          <div>
            <h1 class="text-2xl font-bold" style="color: var(--text-primary)">归档</h1>
            <span class="text-sm" style="color: var(--text-muted)">共 {{ totalCount }} 篇文章</span>
          </div>
        </div>

        <div v-loading="loading" class="archive-card">
          <section v-for="group in groups" :key="group.year" class="archive-group">
            <h2 class="archive-year">
              <span class="archive-year-dot"></span>
              {{ group.year }}
              <span class="archive-year-count">{{ group.articles.length }}</span>
            </h2>
            <ul class="archive-list">
              <li v-for="item in group.articles" :key="item.id" class="archive-li">
                <span class="archive-date">{{ item.createTime ? item.createTime.substring(5, 10) : '' }}</span>
                <router-link :to="`/article/${item.id}`" class="archive-title">
                  {{ item.title }}
                </router-link>
                <el-icon class="archive-arrow"><ArrowRight /></el-icon>
              </li>
            </ul>
          </section>

          <div v-if="!groups.length && !loading" class="text-center py-16" style="color: var(--text-muted)">暂无归档文章</div>
        </div>
      </main>

      <aside class="w-72 flex-shrink-0 hidden lg:block">
        <BlogInfoCard />
        <div class="mt-5"><CategoryTagCard /></div>
      </aside>
    </div>

    <BackToTop />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getArchiveGroups } from '@/api/front'
import { ArrowRight } from '@element-plus/icons-vue'
import BlogInfoCard from '@/components/front/BlogInfoCard.vue'
import CategoryTagCard from '@/components/front/CategoryTagCard.vue'
import BackToTop from '@/components/front/BackToTop.vue'

const loading = ref(false)
const groups = ref([])

const totalCount = computed(() =>
  groups.value.reduce((sum, g) => sum + (g.articles ? g.articles.length : 0), 0)
)

async function loadData() {
  loading.value = true
  try {
    const res = await getArchiveGroups()
    groups.value = (res.data && res.data.archives) || []
  } catch (e) {
    groups.value = []
  } finally {
    loading.value = false
  }
}

onMounted(loadData)
</script>

<style scoped>
.archive-header {
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
  border-left: 4px solid transparent;
  border-image: var(--brand-gradient) 1;
}
.archive-card {
  background: var(--surface);
  border: 1px solid var(--border);
  border-radius: 1rem;
  padding: 28px;
  box-shadow: var(--shadow-soft);
}
.archive-group + .archive-group {
  margin-top: 12px;
}
.archive-year {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 18px;
  font-weight: 700;
  color: var(--text-primary);
  margin: 0 0 14px;
}
.archive-year-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background: var(--brand-gradient);
  box-shadow: 0 0 0 4px rgba(99, 102, 241, 0.15);
}
.archive-year-count {
  font-size: 12px;
  font-weight: 500;
  color: var(--text-muted);
  background: var(--surface-muted);
  padding: 1px 9px;
  border-radius: 99px;
}
.archive-list {
  list-style: none;
  padding: 0 0 0 20px;
  margin: 0;
  border-left: 1px solid var(--border-strong);
}
.archive-li {
  position: relative;
  padding: 8px 0 8px 16px;
  display: flex;
  align-items: center;
  gap: 12px;
  transition: background 0.15s ease;
  border-radius: 0.5rem;
}
.archive-li::before {
  content: '';
  position: absolute;
  left: -4.5px;
  top: 50%;
  transform: translateY(-50%);
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: var(--surface);
  border: 2px solid var(--brand);
}
.archive-li:hover { background: rgba(99, 102, 241, 0.05); }
.archive-date {
  font-size: 13px;
  color: var(--text-muted);
  min-width: 44px;
  font-variant-numeric: tabular-nums;
}
.archive-title {
  flex: 1;
  color: var(--text-secondary);
  font-size: 14.5px;
  text-decoration: none;
  transition: color 0.15s ease;
}
.archive-title:hover { color: var(--brand); }
.archive-arrow {
  color: var(--text-faint);
  opacity: 0;
  transform: translateX(-4px);
  transition: all 0.2s ease;
}
.archive-li:hover .archive-arrow {
  opacity: 1;
  transform: translateX(0);
  color: var(--brand);
}
</style>
