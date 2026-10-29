<template>
  <div v-if="titles.length" class="toc-card">
    <h3 class="toc-title">目录</h3>
    <ul class="toc-list">
      <li
        v-for="(item, index) in titles"
        :key="index"
        :class="['toc-item', `toc-${item.level}`, { active: activeIndex === index }]"
        @click="scrollTo(index)"
      >
        {{ item.text }}
      </li>
    </ul>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, nextTick } from 'vue'

const props = defineProps({
  /** 正文容器选择器，用于提取标题 */
  container: { type: String, default: '.article-content' }
})

const titles = ref([])
const activeIndex = ref(0)
let anchors = []
let scrollHandler = null

function parseTitles() {
  const el = document.querySelector(props.container)
  if (!el) return
  const nodes = el.querySelectorAll('h1, h2, h3')
  anchors = Array.from(nodes)
  titles.value = anchors.map(node => ({
    text: node.textContent.trim(),
    level: Number(node.tagName.charAt(1))
  }))
  anchors.forEach((node, i) => {
    node.setAttribute('data-toc-index', String(i))
    node.scrollMarginTop = '80px'
  })
}

function scrollTo(index) {
  const node = anchors[index]
  if (!node) return
  node.scrollIntoView({ behavior: 'smooth', block: 'start' })
  activeIndex.value = index
}

function onScroll() {
  if (!anchors.length) return
  let current = 0
  anchors.forEach((node, i) => {
    if (node.getBoundingClientRect().top <= 100) {
      current = i
    }
  })
  activeIndex.value = current
}

onMounted(async () => {
  await nextTick()
  parseTitles()
  scrollHandler = () => onScroll()
  window.addEventListener('scroll', scrollHandler, { passive: true })
  onScroll()
})

onBeforeUnmount(() => {
  if (scrollHandler) window.removeEventListener('scroll', scrollHandler)
})
</script>

<style scoped>
.toc-card {
  background: var(--surface);
  border: 1px solid var(--border);
  border-radius: 1rem;
  padding: 18px;
  box-shadow: var(--shadow-soft);
  position: sticky;
  top: 80px;
  max-height: calc(100vh - 150px);
  overflow-y: auto;
}
.toc-title {
  font-size: 15px;
  font-weight: 700;
  color: var(--text-primary);
  margin: 0 0 12px;
  padding-bottom: 10px;
  border-bottom: 1px solid var(--border);
  display: flex;
  align-items: center;
  gap: 8px;
}
.toc-title::before {
  content: '';
  width: 4px;
  height: 14px;
  border-radius: 99px;
  background: var(--brand-gradient);
}
.toc-list {
  list-style: none;
  padding: 0;
  margin: 0;
}
.toc-item {
  font-size: 13px;
  color: var(--text-secondary);
  line-height: 1.6;
  padding: 5px 10px;
  border-radius: 6px;
  cursor: pointer;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  border-left: 2px solid transparent;
  transition: all 0.15s ease;
}
.toc-item:hover {
  color: var(--brand);
  background: rgba(99, 102, 241, 0.06);
}
.toc-2 { padding-left: 18px; }
.toc-3 { padding-left: 30px; }
.toc-item.active {
  color: var(--brand);
  background: rgba(99, 102, 241, 0.1);
  border-left-color: var(--brand);
  font-weight: 600;
}
</style>
