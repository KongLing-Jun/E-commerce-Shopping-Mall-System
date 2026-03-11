import { ref } from 'vue'

const storedTheme = typeof window === 'undefined' ? null : localStorage.getItem('theme')
const systemPrefersDark = typeof window !== 'undefined'
  ? window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches
  : false

const theme = ref(storedTheme || (systemPrefersDark ? 'dark' : 'light'))

// 功能：应用主题
const applyTheme = (value) => {
  theme.value = value
  if (typeof window !== 'undefined') {
    localStorage.setItem('theme', value)
    document.documentElement.dataset.theme = value
  }
}

// 功能：切换主题
const toggleTheme = () => {
  applyTheme(theme.value === 'dark' ? 'light' : 'dark')
}

// 功能：处理init主题
const initTheme = () => {
  applyTheme(theme.value)
}

// 功能：处理use主题
export const useTheme = () => ({
  theme,
  applyTheme,
  toggleTheme,
  initTheme,
})
