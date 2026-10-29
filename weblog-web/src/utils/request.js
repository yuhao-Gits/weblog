import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'

// 统一走 /api 前缀，由 Vite/Nginx 转发到后端，
// 避免与前台页面路由（/article/:id 等）冲突
const request = axios.create({
  baseURL: '/api',
  timeout: 15000
})

// 请求拦截：加 token
request.interceptors.request.use(config => {
  const token = localStorage.getItem('weblog_token')
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

// 响应拦截：统一处理
request.interceptors.response.use(
  response => {
    const res = response.data
    // 后端 code 为字符串 "10000"，统一转字符串比较
    if (String(res.code) !== '10000') {
      ElMessage.error(res.message || '请求失败')
      return Promise.reject(new Error(res.message || 'Error'))
    }
    return res
  },
  error => {
    if (error.response) {
      const { status, data } = error.response
      if (status === 401) {
        ElMessage.error('未登录或登录已过期')
        localStorage.removeItem('weblog_token')
        router.push('/login')
      } else if (status === 403) {
        ElMessage.error(data?.message || '无权限')
      } else {
        ElMessage.error(data?.message || `请求错误 (${status})`)
      }
    } else {
      ElMessage.error('网络异常，请检查后端是否启动')
    }
    return Promise.reject(error)
  }
)

export default request
