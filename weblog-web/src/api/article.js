import request from '@/utils/request'

// 分页查询文章（后台）
export function pageArticles(params) {
  return request.post('/admin/article/list', params)
}

// 发布/更新文章
export function publishArticle(data) {
  return request.post('/admin/article/publish', data)
}

// 文章详情（后台编辑用；后端以 query 参数 articleId 接收）
export function getArticleDetail(id) {
  return request.get('/admin/article/detail', { params: { articleId: id } })
}

// 删除文章
export function deleteArticle(id) {
  return request.post('/admin/article/delete', null, { params: { articleId: id } })
}

// 更新文章置顶状态
export function updateArticleIsTop(id, isTop) {
  return request.post('/admin/article/top/update', null, { params: { articleId: id, isTop } })
}
