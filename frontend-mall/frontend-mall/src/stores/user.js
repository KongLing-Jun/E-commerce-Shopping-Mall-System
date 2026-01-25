import { defineStore } from 'pinia'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: localStorage.getItem('auth_token') || '',
    userId: localStorage.getItem('user_id') || '',
    username: localStorage.getItem('username') || '',
  }),
  actions: {
    setSession({ token, userId, username }) {
      this.token = token
      this.userId = userId
      this.username = username
      localStorage.setItem('auth_token', token)
      localStorage.setItem('user_id', userId)
      localStorage.setItem('username', username)
    },
    clearSession() {
      this.token = ''
      this.userId = ''
      this.username = ''
      localStorage.removeItem('auth_token')
      localStorage.removeItem('user_id')
      localStorage.removeItem('username')
    },
  },
})
