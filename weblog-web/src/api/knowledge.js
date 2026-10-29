import request from '@/utils/request'

// ===== 后台目录 =====
export function listDirectory() {
  return request.post('/admin/knowledge/directory/list')
}
export function saveDirectory(data) {
  return request.post('/admin/knowledge/directory/save', data)
}
export function deleteDirectory(id) {
  return request.post('/admin/knowledge/directory/delete', null, { params: { id } })
}
export function sortDirectory(data) {
  return request.post('/admin/knowledge/directory/sort', data)
}

// ===== 后台文章 =====
export function pageArticle(params) {
  return request.post('/admin/knowledge/article/list', params)
}
export function saveArticle(data) {
  return request.post('/admin/knowledge/article/save', data)
}
export function deleteArticle(id) {
  return request.post('/admin/knowledge/article/delete', null, { params: { id } })
}
export function updateArticleTop(id, isTop) {
  return request.post('/admin/knowledge/article/top', null, { params: { id, isTop } })
}
export function updateArticleStatus(id, status) {
  return request.post('/admin/knowledge/article/status', null, { params: { id, status } })
}
export function getArticleDetail(id) {
  return request.post('/admin/knowledge/article/detail', null, { params: { id } })
}

// ===== 前台 =====
export function frontDirectory() {
  return request.get('/knowledge/directory')
}
export function frontArticleDetail(id) {
  return request.get('/knowledge/article/detail', { params: { id } })
}
