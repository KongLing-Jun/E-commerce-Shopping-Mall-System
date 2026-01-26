<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useAuth } from '@/composables/useAuth.js'
import { logout as logoutApi } from '@/api/auth.js'
import { fetchMyMenus } from '@/api/menus.js'

const router = useRouter()
const route = useRoute()
const { isLoggedIn, isAdmin, refreshAuth } = useAuth()

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
    const res = await fetchMyMenus()
    if (res.code === 200) {
      menuTree.value = res.data || []
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
  refreshAuth()
  window.dispatchEvent(new Event('auth-changed'))
  ElMessage.success('Signed out')
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
        class="sticky top-0 z-20 border-b border-white/60 bg-white/70 backdrop-blur"
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
              <el-menu-item index="/" :disabled="!isLoggedIn">Home</el-menu-item>
              <el-menu-item index="/products" :disabled="!isLoggedIn">Products</el-menu-item>
              <el-menu-item index="/cart" :disabled="!isLoggedIn">Cart</el-menu-item>
              <el-menu-item index="/orders" :disabled="!isLoggedIn">Orders</el-menu-item>
              <el-menu-item index="/addresses" :disabled="!isLoggedIn">Addresses</el-menu-item>
              <el-sub-menu v-if="isAdmin && adminMenuItems.length" index="/admin">
                <template #title>Admin</template>
                <el-menu-item
                  v-for="menu in adminMenuItems"
                  :key="menu.path"
                  :index="menu.path"
                >
                  {{ menu.name }}
                </el-menu-item>
              </el-sub-menu>
            </el-menu>
          </div>

          <div class="flex items-center gap-3">
            <template v-if="!isLoggedIn">
              <el-button size="small" text @click="router.push('/login')">Sign in</el-button>
              <el-button size="small" type="primary" @click="router.push('/register')">
                Create account
              </el-button>
            </template>
            <template v-else>
              <el-tag type="success" round>Signed in</el-tag>
              <el-button size="small" @click="logout">Sign out</el-button>
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
