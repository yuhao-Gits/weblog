import request from '@/utils/request'

// 分类列表（后台分页）
export function pageCategories(params) {
  return request.post('/admin/category/list', params)
}

// 添加分类
export function addCategory(data) {
  return request.post('/admin/category/add', data)
}

// 删除分类（后端以 query 参数 categoryId 接收）
export function deleteCategory(id) {
  return request.post('/admin/category/delete', null, { params: { categoryId: id } })
}

// 全部分类（前台下拉/侧边用，含文章数）
export function listAllCategories() {
  return request.get('/category/list')
}

// 某分类下的文章分页列表（前台）
export function getCategoryArticleList(params) {
  return request.post('/category/article/list', params)
}
