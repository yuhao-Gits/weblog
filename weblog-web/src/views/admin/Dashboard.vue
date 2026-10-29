<template>
  <div>
    <!-- 统计卡片 -->
    <div class="grid grid-cols-1 sm:grid-cols-2 xl:grid-cols-4 gap-5 mb-6">
      <div v-for="card in cards" :key="card.label" class="stat-card">
        <div class="stat-icon" :style="{ background: card.bg }">
          <el-icon :size="24"><component :is="card.icon" /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-label">{{ card.label }}</div>
          <div class="stat-value">{{ card.value }}</div>
        </div>
      </div>
    </div>

    <!-- 图表区 -->
    <div class="grid grid-cols-1 xl:grid-cols-2 gap-5">
      <div class="chart-card">
        <div class="chart-header">
          <div class="chart-title">文章发布热度</div>
          <div class="chart-sub">近 7 天</div>
        </div>
        <div ref="hotChartRef" style="height: 300px"></div>
      </div>
      <div class="chart-card">
        <div class="chart-header">
          <div class="chart-title">浏览量统计</div>
          <div class="chart-sub">近 7 天</div>
        </div>
        <div ref="pvChartRef" style="height: 300px"></div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onBeforeUnmount, nextTick } from 'vue'
import * as echarts from 'echarts'
import { getStatistics, getArticlePublishHot, getPvStatistics } from '@/api/dashboard'
import { useTheme } from '@/composables/useTheme'

const hotChartRef = ref()
const pvChartRef = ref()
const charts = []
let resizeHandler = null

const { isDark } = useTheme()

const cards = reactive([
  { label: '文章总数', value: 0, icon: 'Document', color: '#6366f1', bg: 'linear-gradient(135deg, #6366f1, #8b5cf6)' },
  { label: '分类总数', value: 0, icon: 'Folder', color: '#22c55e', bg: 'linear-gradient(135deg, #22c55e, #10b981)' },
  { label: '标签总数', value: 0, icon: 'PriceTag', color: '#f59e0b', bg: 'linear-gradient(135deg, #f59e0b, #f97316)' },
  { label: '总浏览量', value: 0, icon: 'View', color: '#ef4444', bg: 'linear-gradient(135deg, #ef4444, #f43f5e)' }
])

// 根据主题获取 ECharts 文本颜色
function getTextColor() {
  return isDark.value ? '#a8b2c2' : '#5b6472'
}
function getAxisLineColor() {
  return isDark.value ? '#2c3444' : '#e2e6ee'
}

onMounted(async () => {
  try {
    const res = await getStatistics()
    const d = res.data
    cards[0].value = d.articleTotal || 0
    cards[1].value = d.categoryTotal || 0
    cards[2].value = d.tagTotal || 0
    cards[3].value = d.viewTotal || 0
  } catch (e) {}

  await nextTick()

  // 文章发布热度
  try {
    const res = await getArticlePublishHot()
    const data = res.data || []
    const chart = echarts.init(hotChartRef.value)
    chart.setOption({
      tooltip: { trigger: 'axis' },
      grid: { left: 40, right: 20, top: 30, bottom: 30 },
      xAxis: {
        type: 'category',
        data: data.map(i => i.date),
        axisLine: { lineStyle: { color: getAxisLineColor() } },
        axisLabel: { color: getTextColor() }
      },
      yAxis: {
        type: 'value',
        splitLine: { lineStyle: { color: getAxisLineColor(), type: 'dashed' } },
        axisLabel: { color: getTextColor() }
      },
      series: [{
        data: data.map(i => i.count),
        type: 'bar',
        barWidth: 22,
        itemStyle: {
          borderRadius: [6, 6, 0, 0],
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#8b5cf6' },
            { offset: 1, color: '#6366f1' }
          ])
        }
      }]
    })
    charts.push(chart)
  } catch (e) {}

  // PV 统计（后端返回 { dates, pv, uv }）
  try {
    const res = await getPvStatistics()
    const d = res.data || {}
    const chart = echarts.init(pvChartRef.value)
    chart.setOption({
      tooltip: { trigger: 'axis' },
      legend: { data: ['PV', 'UV'], textStyle: { color: getTextColor() }, top: 0 },
      grid: { left: 40, right: 20, top: 40, bottom: 30 },
      xAxis: {
        type: 'category',
        data: d.dates || [],
        axisLine: { lineStyle: { color: getAxisLineColor() } },
        axisLabel: { color: getTextColor() }
      },
      yAxis: {
        type: 'value',
        splitLine: { lineStyle: { color: getAxisLineColor(), type: 'dashed' } },
        axisLabel: { color: getTextColor() }
      },
      series: [
        {
          name: 'PV',
          data: d.pv || [],
          type: 'line',
          smooth: true,
          areaStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: 'rgba(99, 102, 241, 0.25)' },
              { offset: 1, color: 'rgba(99, 102, 241, 0.02)' }
            ])
          },
          itemStyle: { color: '#6366f1' },
          lineStyle: { width: 3, color: '#6366f1' }
        },
        {
          name: 'UV',
          data: d.uv || [],
          type: 'line',
          smooth: true,
          itemStyle: { color: '#d946ef' },
          lineStyle: { width: 3, color: '#d946ef' }
        }
      ]
    })
    charts.push(chart)
  } catch (e) {}

  resizeHandler = () => charts.forEach(c => c && c.resize())
  window.addEventListener('resize', resizeHandler)
})

onBeforeUnmount(() => {
  if (resizeHandler) window.removeEventListener('resize', resizeHandler)
  charts.forEach(c => c && c.dispose())
})
</script>

<style scoped>
.stat-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 22px;
  background: var(--surface);
  border: 1px solid var(--border);
  border-radius: 1rem;
  box-shadow: var(--shadow-soft);
  transition: transform 0.22s cubic-bezier(0.22, 1, 0.36, 1), box-shadow 0.22s ease;
}
.stat-card:hover {
  transform: translateY(-3px);
  box-shadow: var(--shadow-card);
}
.stat-icon {
  width: 52px;
  height: 52px;
  border-radius: 0.9rem;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: var(--shadow-float);
  flex-shrink: 0;
}
.stat-info {
  min-width: 0;
}
.stat-label {
  font-size: 13px;
  color: var(--text-muted);
  margin-bottom: 4px;
}
.stat-value {
  font-size: 28px;
  font-weight: 800;
  color: var(--text-primary);
  line-height: 1.1;
  font-variant-numeric: tabular-nums;
}

.chart-card {
  background: var(--surface);
  border: 1px solid var(--border);
  border-radius: 1rem;
  padding: 20px;
  box-shadow: var(--shadow-soft);
}
.chart-header {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  margin-bottom: 12px;
}
.chart-title {
  font-size: 16px;
  font-weight: 700;
  color: var(--text-primary);
  display: flex;
  align-items: center;
  gap: 8px;
}
.chart-title::before {
  content: '';
  width: 4px;
  height: 15px;
  border-radius: 99px;
  background: var(--brand-gradient);
}
.chart-sub {
  font-size: 12px;
  color: var(--text-muted);
}
</style>
