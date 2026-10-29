<template>
  <div class="not-found">
    <div class="not-found-code">404</div>
    <h1 class="not-found-title">页面走丢了</h1>
    <p class="not-found-desc">你访问的页面不存在或已被移除</p>
    <div class="flex items-center justify-center gap-3">
      <el-button type="primary" @click="$router.push('/')">回到首页</el-button>
      <el-button @click="$router.back()">返回上一页</el-button>
    </div>
  </div>
</template>

<style scoped>
.not-found {
  min-height: 60vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
  padding: 40px 16px;
}
.not-found-code {
  font-size: 96px;
  font-weight: 800;
  line-height: 1;
  background: var(--brand-gradient);
  -webkit-background-clip: text;
  background-clip: text;
  -webkit-text-fill-color: transparent;
  letter-spacing: 4px;
}
.not-found-title {
  font-size: 22px;
  font-weight: 700;
  color: var(--text-primary);
  margin: 24px 0 8px;
}
.not-found-desc {
  font-size: 14px;
  color: var(--text-muted);
  margin-bottom: 28px;
}
</style>
