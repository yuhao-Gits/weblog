import { ref } from 'vue'
import { getBlogSettings } from '@/api/front'

// 前台博客设置：模块级单例缓存，多个页面共享同一次请求结果
const settings = ref({})
let loading = false

export function useBlogSettings() {
  async function loadSettings(force = false) {
    if (loading) return
    if (!force && settings.value && Object.keys(settings.value).length > 0) return
    loading = true
    try {
      const res = await getBlogSettings()
      settings.value = res.data || {}
    } catch (e) {
      // 前台接口异常时降级为默认值，不阻塞页面渲染
      settings.value = settings.value || {}
    } finally {
      loading = false
    }
  }

  return { settings, loadSettings }
}
