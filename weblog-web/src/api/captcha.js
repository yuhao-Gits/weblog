import request from '@/utils/request'

// 获取滑块验证码
export function getCaptcha() {
  return request.get('/captcha/generate')
}

// 校验滑块验证码
export function verifyCaptcha(token, x) {
  return request.post('/captcha/verify', null, { params: { token, x } })
}
