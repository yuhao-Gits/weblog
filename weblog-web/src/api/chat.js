import request from '@/utils/request'

// 聊天室：历史消息
export function getChatHistory(params) {
  return request.get('/chat/history', { params })
}

// 聊天室：在线用户
export function getChatOnline() {
  return request.get('/chat/online')
}
