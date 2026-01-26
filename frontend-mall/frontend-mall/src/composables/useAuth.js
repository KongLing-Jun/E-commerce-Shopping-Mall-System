import { computed, onBeforeUnmount, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'

const isLoggedIn = ref(!!localStorage.getItem('token'))
const roleKey = ref(localStorage.getItem('roleKey'))
const isAdmin = computed(() => roleKey.value === 'ADMIN')

const refreshAuth = () => {
  isLoggedIn.value = !!localStorage.getItem('token')
  roleKey.value = localStorage.getItem('roleKey')
}

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

export const useAuth = () => {
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
