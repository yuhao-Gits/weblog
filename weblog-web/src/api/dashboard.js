import request from '@/utils/request'

// 仪表盘统计
export function getStatistics() {
  return request.get('/admin/dashboard/article/category/tag/statistics')
}

// 文章发布热度
export function getArticlePublishHot() {
  return request.get('/admin/dashboard/article/publish/hot')
}

// PV 统计
export function getPvStatistics() {
  return request.get('/admin/dashboard/pv/statistics')
}