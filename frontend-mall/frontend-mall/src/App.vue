<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useAuth } from '@/composables/useAuth.js'
import { logout as logoutApi } from '@/api/auth.js'
import { fetchMyMenus, fetchMyPerms } from '@/api/menus.js'
import { useI18n } from '@/i18n/index.js'
import { useTheme } from '@/composables/useTheme.js'

const router = useRouter()
const route = useRoute()
const { isLoggedIn, isAdmin, refreshAuth } = useAuth()
const { locale, setLocale, t, availableLocales } = useI18n()
const { theme, applyTheme, initTheme } = useTheme()
const dual = (zh, en) => (locale.value === 'zh' ? zh : en)

const menuTree = ref([])
const storeSearch = ref('')

const flattenMenus = (menus, result = []) => {
  if (!Array.isArray(menus)) {
    return result
  }
  menus.forEach((menu) => {
    result.push(menu)
    if (menu.children && menu.children.length) {
      flattenMenus(menu.children, result)
    }
  })
  return result
}

const adminMenuItems = computed(() => {
  const flat = flattenMenus(menuTree.value, [])
  return flat.filter((menu) => menu.path && menu.path.startsWith('/admin') && menu.type === 'MENU')
})

const menuLabelMap = {
  AdminUsers: 'nav.adminUsers',
  AdminRoles: 'nav.adminRoles',
  AdminProducts: 'nav.adminProducts',
  AdminCategories: 'nav.adminCategories',
  AdminOrders: 'nav.adminOrders',
  AdminBanners: 'nav.adminBanners',
  AdminCarts: 'nav.adminCarts',
  AdminStats: 'nav.adminStats',
}

const resolveMenuLabel = (menu) => {
  const key = menuLabelMap[menu.component]
  return key ? t(key) : menu.name
}

const isAuthPage = computed(() => ['/login', '/register'].includes(route.path))
const isAdminArea = computed(() => route.path.startsWith('/admin'))

const storeNavItems = computed(() => [
  { path: '/', label: t('nav.home') },
  { path: '/products', label: t('nav.products') },
  { path: '/orders', label: t('nav.orders') },
  { path: '/cart', label: t('nav.cart') },
])

const storeNavItemsForRoute = computed(() => {
  // 首页只保留浏览入口，隐藏可直接触发购买流程的导航。
  if (route.path === '/') {
    return storeNavItems.value.filter((item) => item.path === '/' || item.path === '/products')
  }
  return storeNavItems.value
})

const loadMenus = async () => {
  if (!isLoggedIn.value) {
    menuTree.value = []
    return
  }
  const cachedMenus = localStorage.getItem('menus')
  const cachedPerms = localStorage.getItem('perms')
  if (cachedMenus) {
    try {
      menuTree.value = JSON.parse(cachedMenus) || []
    } catch {
      menuTree.value = []
    }
  }
  if (cachedPerms) {
    try {
      JSON.parse(cachedPerms)
    } catch {
      localStorage.removeItem('perms')
    }
  }
  try {
    // menus 用于动态侧边栏，perms 用于按钮级权限控制。
    const [menuRes, permRes] = await Promise.all([fetchMyMenus(), fetchMyPerms()])
    if (menuRes.code === 200) {
      menuTree.value = menuRes.data || []
      localStorage.setItem('menus', JSON.stringify(menuRes.data || []))
    }
    if (permRes.code === 200) {
      localStorage.setItem('perms', JSON.stringify(permRes.data || []))
    }
  } catch {
    menuTree.value = []
  }
}

watch(isLoggedIn, (value) => {
  if (value) {
    loadMenus()
  } else {
    menuTree.value = []
  }
})

onMounted(() => {
  // 首次加载时恢复主题与语言偏好。
  initTheme()
  setLocale(locale.value)
  if (isLoggedIn.value) {
    loadMenus()
  }
})

const handleStoreSearch = () => {
  // 统一跳转到商品列表页并携带关键字查询参数。
  if (!storeSearch.value) {
    router.push('/products')
    return
  }
  router.push({ path: '/products', query: { keyword: storeSearch.value } })
}

const logout = async () => {
  try {
    await logoutApi()
  } catch {
    // Ignore logout API failures and clear local state anyway.
  }
  localStorage.removeItem('token')
  localStorage.removeItem('userId')
  localStorage.removeItem('roleKey')
  localStorage.removeItem('menus')
  localStorage.removeItem('perms')
  // 通知权限守卫和状态管理：当前登录态已失效。
  refreshAuth()
  window.dispatchEvent(new Event('auth-changed'))
  ElMessage.success(t('auth.logoutSuccess'))
  router.push('/login')
}
</script>

<template>
  <div class="min-h-screen text-[var(--ink)]">
    <router-view v-if="isAuthPage" />

    <template v-else-if="isAdminArea && isAdmin">
      <div class="grid min-h-screen lg:grid-cols-[250px_1fr]">
        <aside class="hidden border-r border-[var(--line)] bg-[var(--surface)] px-5 py-6 lg:flex lg:flex-col">
          <router-link to="/" class="flex items-center gap-3 rounded-xl px-2 py-1">
            <div class="grid h-11 w-11 place-content-center rounded-xl bg-[var(--highlight)] text-xl font-bold text-[var(--accent)]">
              A
            </div>
            <div>
              <div class="text-xl font-extrabold">{{ dual('后台面板', 'Admin Panel') }}</div>
              <div class="text-xs tracking-wide text-[var(--muted)]">{{ dual('管理控制台', 'Management Console') }}</div>
            </div>
          </router-link>

          <div class="mt-8 space-y-2">
            <router-link
              v-for="menu in adminMenuItems"
              :key="menu.path"
              :to="menu.path"
              class="flex items-center rounded-xl px-4 py-3 text-sm font-semibold text-[var(--muted)] transition"
              :class="route.path === menu.path ? 'bg-[var(--highlight)] text-[var(--accent)]' : 'hover:bg-[var(--surface-soft)]'"
            >
              {{ resolveMenuLabel(menu) }}
            </router-link>
          </div>

          <div class="mt-auto space-y-3 border-t border-[var(--line)] pt-6">
            <el-select v-model="locale" size="small" class="w-full" @change="setLocale">
              <el-option
                v-for="option in availableLocales"
                :key="option.value"
                :label="option.label"
                :value="option.value"
              />
            </el-select>
            <el-switch
              v-model="theme"
              inline-prompt
              active-value="dark"
              inactive-value="light"
              active-text="D"
              inactive-text="L"
              @change="applyTheme"
            />
            <el-button class="w-full" @click="logout">{{ t('nav.signOut') }}</el-button>
          </div>
        </aside>

        <div class="min-h-screen">
          <header class="sticky top-0 z-20 border-b border-[var(--line)] bg-[var(--surface)]/95 px-5 py-4 backdrop-blur">
            <div class="mx-auto flex w-full max-w-7xl items-center justify-between gap-4">
              <div>
                <h1 class="text-lg font-bold">{{ t('nav.admin') }}</h1>
                <p class="text-xs text-[var(--muted)]">{{ dual('RBAC与运营操作', 'RBAC + Operations') }}</p>
              </div>
              <div class="flex items-center gap-2">
                <el-select v-model="locale" size="small" class="w-24 lg:hidden" @change="setLocale">
                  <el-option
                    v-for="option in availableLocales"
                    :key="option.value"
                    :label="option.label"
                    :value="option.value"
                  />
                </el-select>
                <el-button @click="router.push('/')">{{ dual('商城', 'Store') }}</el-button>
              </div>
            </div>
          </header>

          <main class="mx-auto w-full max-w-7xl px-5 py-8">
            <router-view />
          </main>
        </div>
      </div>
    </template>

    <template v-else>
      <div class="min-h-screen">
        <header class="sticky top-0 z-20 border-b border-[var(--line)] bg-[var(--surface)]/95 backdrop-blur">
          <div class="mx-auto flex h-20 w-full max-w-7xl items-center justify-between gap-5 px-4">
            <div class="flex items-center gap-8">
              <router-link to="/" class="flex items-center gap-3">
                <div class="grid h-10 w-10 place-content-center rounded-xl bg-[var(--highlight)] font-bold text-[var(--accent)]">
                  E
                </div>
                <div class="text-2xl font-extrabold">E-Shop</div>
              </router-link>
              <nav class="hidden items-center gap-1 md:flex">
                <router-link
                  v-for="item in storeNavItemsForRoute"
                  :key="item.path"
                  :to="item.path"
                  class="rounded-xl px-4 py-2 text-sm font-semibold transition"
                  :class="route.path === item.path ? 'bg-[var(--highlight)] text-[var(--accent)]' : 'text-[var(--muted)] hover:bg-[var(--surface-soft)]'"
                >
                  {{ item.label }}
                </router-link>
              </nav>
            </div>

            <div class="flex items-center gap-2">
              <el-input
                v-model="storeSearch"
                  :placeholder="t('productList.searchPlaceholder')"
                  class="hidden w-60 lg:block"
                  @keyup.enter="handleStoreSearch"
                />
              <el-button @click="handleStoreSearch">{{ t('common.search') }}</el-button>
              <el-select v-model="locale" size="small" class="w-24" @change="setLocale">
                <el-option
                  v-for="option in availableLocales"
                  :key="option.value"
                  :label="option.label"
                  :value="option.value"
                />
              </el-select>
              <el-switch
                v-model="theme"
                inline-prompt
                active-value="dark"
                inactive-value="light"
                active-text="D"
                inactive-text="L"
                @change="applyTheme"
              />
              <template v-if="isLoggedIn">
                <el-button @click="router.push('/profile')">{{ t('nav.profile') }}</el-button>
                <el-button v-if="isAdmin" @click="router.push('/admin')">{{ t('nav.admin') }}</el-button>
                <el-button type="primary" @click="logout">{{ t('nav.signOut') }}</el-button>
              </template>
              <template v-else>
                <el-button @click="router.push('/login')">{{ t('nav.signIn') }}</el-button>
                <el-button type="primary" @click="router.push('/register')">{{ t('nav.createAccount') }}</el-button>
              </template>
            </div>
          </div>
        </header>

        <main class="mx-auto w-full max-w-7xl px-4 py-8">
          <router-view />
        </main>
      </div>
    </template>
  </div>
</template>


