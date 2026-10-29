import request from '@/utils/request'

// 标签列表（后台分页）
export function pageTags(params) {
  return request.post('/admin/tag/list', params)
}

// 添加标签
export function addTag(data) {
  return request.post('/admin/tag/add', data)
}

// 删除标签（后端以 query 参数 tagId 接收）
export function deleteTag(id) {
  return request.post('/admin/tag/delete', null, { params: { tagId: id } })
}

// 全部标签（前台侧边用，含文章数）
export function listAllTags() {
  return request.get('/tag/list')
}

// 某标签下的文章分页列表（前台）
export function getTagArticleList(params) {
  return request.post('/tag/article/list', params)
}
