import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  // ===== 前台（统一布局：Header + Footer） =====
  {
    path: '/',
    component: () => import('@/layout/FrontLayout.vue'),
    children: [
      { path: '', name: 'Home', component: () => import('@/views/front/Home.vue') },
      { path: 'article/:id', name: 'ArticleDetail', component: () => import('@/views/front/ArticleDetail.vue') },
      { path: 'category/:name', name: 'CategoryList', component: () => import('@/views/front/CategoryList.vue') },
      { path: 'tag/:name', name: 'TagList', component: () => import('@/views/front/TagList.vue') },
      { path: 'archives', name: 'Archives', component: () => import('@/views/front/Archives.vue') },
      { path: 'knowledge', name: 'Knowledge', component: () => import('@/views/front/Knowledge.vue') },
      { path: 'knowledge/:id', name: 'KnowledgeDetail', component: () => import('@/views/front/KnowledgeDetail.vue') },
      { path: 'chat', name: 'ChatRoom', component: () => import('@/views/front/ChatRoom.vue') }
    ]
  },
  // ===== 后台 =====
  { path: '/login', name: 'Login', component: () => import('@/views/admin/Login.vue') },
  {
    path: '/admin',
    component: () => import('@/layout/AdminLayout.vue'),
    redirect: '/admin/dashboard',
    children: [
      { path: 'dashboard', name: 'Dashboard', component: () => import('@/views/admin/Dashboard.vue') },
      { path: 'article', name: 'ArticleManage', component: () => import('@/views/admin/ArticleManage.vue') },
      { path: 'article/publish', name: 'ArticlePublish', component: () => import('@/views/admin/ArticlePublish.vue') },
      { path: 'category', name: 'CategoryManage', component: () => import('@/views/admin/CategoryManage.vue') },
      { path: 'tag', name: 'TagManage', component: () => import('@/views/admin/TagManage.vue') },
      { path: 'comment', name: 'CommentManage', component: () => import('@/views/admin/CommentManage.vue') },
      { path: 'knowledge', name: 'KnowledgeManage', component: () => import('@/views/admin/KnowledgeManage.vue') },
      { path: 'settings', name: 'BlogSettings', component: () => import('@/views/admin/BlogSettings.vue') }
    ]
  },
  // ===== 404 兜底 =====
  { path: '/:pathMatch(.*)*', name: 'NotFound', component: () => import('@/views/front/NotFound.vue') }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior() {
    return { top: 0 }
  }
})

// 全局路由守卫：后台页面需登录
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('weblog_token')
  if (to.path.startsWith('/admin') && to.path !== '/login' && !token) {
    next('/login')
  } else if (to.path === '/login' && token) {
    next('/admin/dashboard')
  } else {
    next()
  }
})

// 动态设置页面标题
router.afterEach(to => {
  document.title = to.name ? `${String(to.name)} - Weblog` : 'Weblog'
})

export default router
