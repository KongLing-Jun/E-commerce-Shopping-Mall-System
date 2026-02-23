import axios from 'axios'
import { ElMessage } from 'element-plus'

const request = axios.create({
  baseURL: '',
  timeout: 10000,
})

// Request interceptor
request.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => Promise.reject(error),
)

// Response interceptor
request.interceptors.response.use(
  (response) => response.data,
  (error) => {
    if (error.response && error.response.status === 401) {
      localStorage.removeItem('token')
      localStorage.removeItem('userId')
      localStorage.removeItem('roleKey')
      localStorage.removeItem('menus')
      localStorage.removeItem('perms')
      window.dispatchEvent(new Event('auth-changed'))
      const path = window.location.pathname || ''
      const requiresAuthPath =
        path.startsWith('/admin') ||
        path.startsWith('/cart') ||
        path.startsWith('/orders') ||
        path.startsWith('/addresses') ||
        path.startsWith('/profile')
      if (requiresAuthPath) {
        window.location.href = '/login'
      }
    } else if (error.response && error.response.status === 403) {
      ElMessage.error('Permission denied')
    }
    return Promise.reject(error)
  },
)

export default request

