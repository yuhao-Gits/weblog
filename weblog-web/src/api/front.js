import request from '@/utils/request'

// 前台文章分页列表
export function getArticleList(params) {
  return request.post('/article/list', params)
}

// 前台文章详情
export function getArticleDetail(articleId) {
  return request.get('/article/detail', { params: { articleId } })
}

// 归档：按年份分组（全量）
export function getArchiveGroups() {
  return request.post('/article/archives')
}

// 归档：分页
export function getArchiveList(params) {
  return request.post('/article/archive', params)
}

// 某分类下的文章分页
export function getArticleByCategory(params) {
  return request.post('/category/article/list', params)
}

// 某标签下的文章分页
export function getArticleByTag(params) {
  return request.post('/tag/article/list', params)
}

// 博客设置（前台公开）
export function getBlogSettings() {
  return request.get('/blog/settings/detail')
}

// 站内搜索
export function searchArticle(keyword) {
  return request.post('/search/article', { keyword })
}
