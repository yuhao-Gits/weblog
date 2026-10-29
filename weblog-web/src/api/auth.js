import request from '@/utils/request'

// 登录
export function login(data) {
  return request.post('/admin/login', data)
}

// 获取当前用户信息
export function getUserInfo() {
  return request.get('/admin/user/info')
}

// 修改密码
export function updatePassword(data) {
  return request.put('/admin/user/password/update', data)
}