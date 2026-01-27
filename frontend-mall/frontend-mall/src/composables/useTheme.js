import { ref } from 'vue'

const storedTheme = typeof window === 'undefined' ? null : localStorage.getItem('theme')
const systemPrefersDark = typeof window !== 'undefined'
  ? window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches
  : false

const theme = ref(storedTheme || (systemPrefersDark ? 'dark' : 'light'))

const applyTheme = (value) => {
  theme.value = value
  if (typeof window !== 'undefined') {
    localStorage.setItem('theme', value)
    document.documentElement.dataset.theme = value
  }
}

const toggleTheme = () => {
  applyTheme(theme.value === 'dark' ? 'light' : 'dark')
}

const initTheme = () => {
  applyTheme(theme.value)
}

export const useTheme = () => ({
  theme,
  applyTheme,
  toggleTheme,
  initTheme,
})
