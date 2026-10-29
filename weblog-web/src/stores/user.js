import { defineStore } from 'pinia'
import { login as loginApi, getUserInfo } from '@/api/auth'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: localStorage.getItem('weblog_token') || '',
    username: localStorage.getItem('weblog_username') || '',
    role: localStorage.getItem('weblog_role') || ''
  }),
  actions: {
    async login({ username, password, captchaToken }) {
      const res = await loginApi({ username, password, captchaToken })
      this.token = res.data.token
      this.username = res.data.username
      this.role = res.data.role
      localStorage.setItem('weblog_token', this.token)
      localStorage.setItem('weblog_username', this.username)
      localStorage.setItem('weblog_role', this.role)
      return res
    },
    async fetchUserInfo() {
      const res = await getUserInfo()
      this.username = res.data.username
      this.role = res.data.role
      return res.data
    },
    logout() {
      this.token = ''
      this.username = ''
      this.role = ''
      localStorage.removeItem('weblog_token')
      localStorage.removeItem('weblog_username')
      localStorage.removeItem('weblog_role')
    }
  }
})