<template>
  <div class="slider-captcha w-full select-none" :style="{ width: '320px' }">
    <!-- 背景图 + 缺口 + 滑块 -->
    <div class="relative rounded-md overflow-hidden" style="height: 160px">
      <img v-if="backgroundImage" :src="backgroundImage" class="w-full h-full block" style="object-fit: cover" draggable="false" />
      <!-- 滑块拼图 -->
      <img
        v-if="puzzleImage"
        :src="puzzleImage"
        class="absolute top-0 left-0 pointer-events-none"
        style="width: 48px; height: 48px; transition: none"
        :style="{ transform: `translateX(${sliderX}px)`, top: `${puzzleY}px` }"
        draggable="false"
      />
    </div>

    <!-- 滑轨 -->
    <div
      ref="trackRef"
      class="relative mt-2 h-10 rounded-full track cursor-pointer"
      @mousedown="onDragStart"
      @touchstart="onDragStart"
    >
      <div class="absolute left-0 top-0 h-full rounded-full track-fill" :style="{ width: `${sliderX + 24}px` }"></div>
      <div
        class="absolute top-0 left-0 h-10 w-10 rounded-full thumb"
        :style="{ transform: `translateX(${sliderX}px)` }"
      >
        <el-icon><Right /></el-icon>
      </div>
      <div v-if="!verified" class="absolute inset-0 flex items-center justify-center text-sm track-text pointer-events-none">
        向右滑动滑块完成验证
      </div>
      <div v-else class="absolute inset-0 flex items-center justify-center text-sm track-ok pointer-events-none">
        验证通过
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getCaptcha, verifyCaptcha } from '@/api/captcha'
import { ElMessage } from 'element-plus'
import { Right } from '@element-plus/icons-vue'

const emit = defineEmits(['success', 'refresh'])

const backgroundImage = ref('')
const puzzleImage = ref('')
const puzzleY = ref(0)
const token = ref('')
const sliderX = ref(0)
const verified = ref(false)
const trackRef = ref()

const TRACK_WIDTH = 320
const PUZZLE = 48
const MAX_X = TRACK_WIDTH - PUZZLE // 滑块可移动最大距离（背景宽 - 拼图宽）

async function loadCaptcha() {
  verified.value = false
  sliderX.value = 0
  try {
    const res = await getCaptcha()
    const data = res.data
    token.value = data.token
    backgroundImage.value = data.backgroundImage
    puzzleImage.value = data.puzzleImage
    puzzleY.value = data.y || 0
  } catch (e) {}
}

let dragging = false
let startX = 0
let startSliderX = 0

function onDragStart(e) {
  if (verified.value) return
  dragging = true
  startX = e.type === 'touchstart' ? e.touches[0].clientX : e.clientX
  startSliderX = sliderX.value
  document.addEventListener('mousemove', onDragMove)
  document.addEventListener('mouseup', onDragEnd)
  document.addEventListener('touchmove', onDragMove)
  document.addEventListener('touchend', onDragEnd)
}

function onDragMove(e) {
  if (!dragging) return
  const clientX = e.type === 'touchmove' ? e.touches[0].clientX : e.clientX
  const delta = clientX - startX
  let x = startSliderX + delta
  if (x < 0) x = 0
  if (x > MAX_X) x = MAX_X
  sliderX.value = x
}

async function onDragEnd() {
  if (!dragging) return
  dragging = false
  document.removeEventListener('mousemove', onDragMove)
  document.removeEventListener('mouseup', onDragEnd)
  document.removeEventListener('touchmove', onDragMove)
  document.removeEventListener('touchend', onDragEnd)

  // 松手即校验（只要拖动过一定距离）
  if (sliderX.value >= 10) {
    // 校验
    try {
      const res = await verifyCaptcha(token.value, Math.round(sliderX.value))
      verified.value = true
      ElMessage.success('验证通过')
      // 后端返回 passToken（登录时一次性消费）
      emit('success', { passToken: res.data?.passToken })
    } catch (e) {
      ElMessage.error('验证失败，请重试')
      sliderX.value = 0
      loadCaptcha()
      emit('refresh')
    }
  } else {
    // 未拖动，回弹
    sliderX.value = 0
  }
}

// 暴露给父组件刷新
defineExpose({ loadCaptcha })

onMounted(loadCaptcha)
</script>

<style scoped>
.slider-captcha {
  border-radius: 0.75rem;
  overflow: hidden;
}
.track {
  background: var(--surface-muted);
  border: 1px solid var(--border);
  display: flex;
  align-items: center;
}
.track-fill {
  background: rgba(99, 102, 241, 0.15);
}
.thumb {
  background: var(--surface);
  border: 1px solid var(--border);
  box-shadow: var(--shadow-soft);
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--brand);
}
.track-text {
  color: var(--text-muted);
}
.track-ok {
  color: #22c55e;
}
</style>
