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

const menuTree = ref([])

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
  return flat.filter((menu) => menu.path && menu.path.startsWith('/admin'))
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

const adminDefaultPath = computed(() => adminMenuItems.value[0]?.path || '/admin')

const activePath = computed(() => {
  if (route.path.startsWith('/product')) {
    return '/products'
  }
  if (route.path.startsWith('/orders/confirm')) {
    return '/orders'
  }
  if (route.path.startsWith('/admin')) {
    return adminDefaultPath.value
  }
  return route.path
})

const loadMenus = async () => {
  if (!isLoggedIn.value) {
    menuTree.value = []
    return
  }
  try {
    const [menuRes, permRes] = await Promise.all([fetchMyMenus(), fetchMyPerms()])
    if (menuRes.code === 200) {
      menuTree.value = menuRes.data || []
    }
    if (permRes.code === 200) {
      localStorage.setItem('perms', JSON.stringify(permRes.data || []))
    }
  } catch (error) {
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
  initTheme()
  setLocale(locale.value)
  if (isLoggedIn.value) {
    loadMenus()
  }
})

const logout = async () => {
  try {
    await logoutApi()
  } catch (error) {
    // Ignore logout API failures and clear local state anyway.
  }
  localStorage.removeItem('token')
  localStorage.removeItem('userId')
  localStorage.removeItem('roleKey')
  localStorage.removeItem('perms')
  refreshAuth()
  window.dispatchEvent(new Event('auth-changed'))
  ElMessage.success(t('auth.logoutSuccess'))
  router.push('/login')
}
</script>

<template>
  <div class="relative min-h-screen">
    <div
      class="pointer-events-none fixed -top-24 -right-10 h-80 w-80 rounded-full bg-[radial-gradient(circle,#ffd8a8,transparent_60%)] opacity-70 blur-3xl"
    ></div>
    <div
      class="pointer-events-none fixed bottom-0 -left-16 h-72 w-72 rounded-full bg-[radial-gradient(circle,#9ad0ec,transparent_60%)] opacity-70 blur-3xl"
    ></div>

    <el-container class="min-h-screen">
      <el-header
        height="72px"
        class="sticky top-0 z-20 border-b border-white/60 bg-[var(--surface)] backdrop-blur"
      >
        <div class="mx-auto flex h-full w-full max-w-6xl items-center justify-between px-6">
          <div class="flex items-center gap-6">
            <router-link to="/" class="flex items-center gap-3">
              <div
                class="flex h-10 w-10 items-center justify-center rounded-2xl bg-[var(--accent)] text-lg font-semibold text-white shadow-soft"
              >
                M
              </div>
              <div>
                <div class="text-xl font-semibold">Mall Studio</div>
                <div class="text-[11px] uppercase tracking-[0.35em] text-[var(--muted)]">
                  shopping lab
                </div>
              </div>
            </router-link>
            <el-menu
              :default-active="activePath"
              mode="horizontal"
              router
              class="border-0 bg-transparent"
            >
              <el-menu-item index="/" :disabled="!isLoggedIn">{{ t('nav.home') }}</el-menu-item>
              <el-menu-item index="/products" :disabled="!isLoggedIn">{{ t('nav.products') }}</el-menu-item>
              <el-menu-item index="/cart" :disabled="!isLoggedIn">{{ t('nav.cart') }}</el-menu-item>
              <el-menu-item index="/orders" :disabled="!isLoggedIn">{{ t('nav.orders') }}</el-menu-item>
              <el-menu-item index="/profile" :disabled="!isLoggedIn">{{ t('nav.profile') }}</el-menu-item>
              <el-menu-item index="/addresses" :disabled="!isLoggedIn">{{ t('nav.addresses') }}</el-menu-item>
              <el-sub-menu v-if="isAdmin && adminMenuItems.length" index="/admin">
                <template #title>{{ t('nav.admin') }}</template>
                <el-menu-item
                  v-for="menu in adminMenuItems"
                  :key="menu.path"
                  :index="menu.path"
                >
                  {{ resolveMenuLabel(menu) }}
                </el-menu-item>
              </el-sub-menu>
            </el-menu>
          </div>

          <div class="flex items-center gap-3">
            <el-select
              v-model="locale"
              size="small"
              class="w-24"
              @change="setLocale"
            >
              <el-option
                v-for="option in availableLocales"
                :key="option.value"
                :label="option.label"
                :value="option.value"
              />
            </el-select>
            <el-switch
              v-model="theme"
              active-value="dark"
              inactive-value="light"
              :active-text="t('theme.dark')"
              :inactive-text="t('theme.light')"
              @change="applyTheme"
            />
            <template v-if="!isLoggedIn">
              <el-button size="small" text @click="router.push('/login')">{{ t('nav.signIn') }}</el-button>
              <el-button size="small" type="primary" @click="router.push('/register')">
                {{ t('nav.createAccount') }}
              </el-button>
            </template>
            <template v-else>
              <el-tag type="success" round>{{ t('nav.signedIn') }}</el-tag>
              <el-button size="small" @click="logout">{{ t('nav.signOut') }}</el-button>
            </template>
          </div>
        </div>
      </el-header>

      <el-main class="px-6 pb-16 pt-8">
        <div class="mx-auto w-full max-w-6xl">
          <router-view />
        </div>
      </el-main>
    </el-container>
  </div>
</template>

