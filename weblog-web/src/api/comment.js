import request from '@/utils/request'

// 发布评论
export function publishComment(data) {
  return request.post('/comment/publish', data)
}

// 某篇文章的评论列表
export function getCommentList(articleId) {
  return request.get('/comment/list', { params: { articleId } })
}

// 后台：评论分页列表
export function pageComments(params) {
  return request.post('/admin/comment/list', params)
}

// 后台：删除评论
export function deleteComment(commentId) {
  return request.post('/admin/comment/delete', null, { params: { commentId } })
}

// 后台：审核评论
export function auditComment(commentId, status) {
  return request.post('/admin/comment/audit', null, { params: { commentId, status } })
}
