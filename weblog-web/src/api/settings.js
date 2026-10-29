import request from '@/utils/request'

// 博客设置详情
export function getBlogSettings() {
  return request.get('/admin/blog/settings/detail')
}

// 更新博客设置
export function updateBlogSettings(data) {
  return request.post('/admin/blog/settings/update', data)
}