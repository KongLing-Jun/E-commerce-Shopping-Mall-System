import { computed, onBeforeUnmount, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'

const isLoggedIn = ref(!!localStorage.getItem('token'))
const roleKey = ref(localStorage.getItem('roleKey'))
const isAdmin = computed(() => roleKey.value === 'ADMIN')

// 功能：处理刷新auth
const refreshAuth = () => {
  isLoggedIn.value = !!localStorage.getItem('token')
  roleKey.value = localStorage.getItem('roleKey')
}

// 功能：处理guardaction
const guardAction = (action, message = 'Please log in first') => {
  if (!isLoggedIn.value) {
    ElMessage.warning(message)
    return false
  }
  if (typeof action === 'function') {
    action()
  }
  return true
}

// 功能：处理useauth
export const useAuth = () => {
  // 功能：处理handler
  const handler = () => refreshAuth()

  onMounted(() => {
    window.addEventListener('auth-changed', handler)
  })

  onBeforeUnmount(() => {
    window.removeEventListener('auth-changed', handler)
  })

  return {
    isLoggedIn,
    roleKey,
    isAdmin,
    refreshAuth,
    guardAction,
  }
}
