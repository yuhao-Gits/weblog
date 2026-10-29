<template>
  <article class="article-item" @click="$emit('click')">
    <div v-if="article.cover" class="article-cover">
      <img :src="article.cover" :alt="article.title" loading="lazy" />
    </div>

    <div class="article-body">
      <div class="article-tags">
        <span v-if="article.isTop === 1" class="top-badge">
          <el-icon><Top /></el-icon> 置顶
        </span>
        <span v-if="article.categoryName" class="category-badge">{{ article.categoryName }}</span>
      </div>

      <h2 class="article-title">{{ article.title }}</h2>
      <p class="article-summary">{{ article.summary || '' }}</p>

      <div class="article-meta">
        <span class="meta-item">
          <el-icon><Calendar /></el-icon> {{ formatDate(article.createTime) }}
        </span>
        <span v-if="article.tagNames" class="meta-item meta-tags">
          <el-icon><PriceTag /></el-icon>
          <span class="tag-text">{{ article.tagNames }}</span>
        </span>
        <span class="meta-item">
          <el-icon><View /></el-icon> {{ article.readCount || 0 }}
        </span>
        <span class="read-more">
          阅读全文
          <el-icon><ArrowRight /></el-icon>
        </span>
      </div>
    </div>
  </article>
</template>

<script setup>
defineProps({
  article: { type: Object, required: true }
})
defineEmits(['click'])

function formatDate(dateStr) {
  return dateStr ? dateStr.substring(0, 10) : ''
}
</script>

<style scoped>
.article-item {
  position: relative;
  display: flex;
  gap: 22px;
  padding: 24px;
  margin-bottom: 20px;
  background: var(--surface);
  border: 1px solid var(--border);
  border-radius: 1rem;
  cursor: pointer;
  box-shadow: var(--shadow-soft);
  overflow: hidden;
  transition: transform 0.25s cubic-bezier(0.22, 1, 0.36, 1),
    box-shadow 0.25s ease, border-color 0.25s ease;
}

/* 顶部渐变光带（悬停显现） */
.article-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: var(--brand-gradient);
  opacity: 0;
  transition: opacity 0.25s ease;
}

.article-item:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-card);
  border-color: rgba(99, 102, 241, 0.25);
}
.article-item:hover::before { opacity: 1; }

.article-cover {
  flex-shrink: 0;
  width: 200px;
  height: 138px;
  border-radius: 0.75rem;
  overflow: hidden;
}
.article-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s cubic-bezier(0.22, 1, 0.36, 1);
}
.article-item:hover .article-cover img {
  transform: scale(1.06);
}

.article-body {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
}

.article-tags {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 10px;
}
.top-badge {
  display: inline-flex;
  align-items: center;
  gap: 3px;
  padding: 2px 10px;
  font-size: 12px;
  font-weight: 600;
  color: #fff;
  background: var(--brand-gradient);
  border-radius: 99px;
  box-shadow: var(--shadow-float);
}
.category-badge {
  display: inline-flex;
  align-items: center;
  padding: 2px 10px;
  font-size: 12px;
  font-weight: 500;
  color: var(--brand);
  background: rgba(99, 102, 241, 0.1);
  border-radius: 99px;
}

.article-title {
  font-size: 20px;
  font-weight: 700;
  color: var(--text-primary);
  line-height: 1.4;
  margin: 0 0 8px;
  letter-spacing: -0.01em;
  transition: color 0.2s ease;
}
.article-item:hover .article-title { color: var(--brand-600); }

.article-summary {
  font-size: 14px;
  color: var(--text-secondary);
  line-height: 1.7;
  margin: 0 0 14px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.article-meta {
  margin-top: auto;
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 16px;
  font-size: 13px;
  color: var(--text-muted);
}
.meta-item {
  display: inline-flex;
  align-items: center;
  gap: 5px;
}
.meta-tags { max-width: 220px; }
.tag-text {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.read-more {
  margin-left: auto;
  display: inline-flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  font-weight: 600;
  color: var(--brand);
  opacity: 0;
  transform: translateX(-6px);
  transition: opacity 0.25s ease, transform 0.25s ease;
}
.article-item:hover .read-more {
  opacity: 1;
  transform: translateX(0);
}

@media (max-width: 640px) {
  .article-cover { display: none; }
}
</style>
