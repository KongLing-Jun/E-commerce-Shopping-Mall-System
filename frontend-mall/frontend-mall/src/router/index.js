import { createRouter, createWebHistory } from 'vue-router'
import Home from '@/views/Home.vue'
import Login from '@/views/Login.vue'
import Register from '@/views/Register.vue'
import ProductList from '@/views/ProductList.vue'
import ProductDetail from '@/views/ProductDetail.vue'
import Cart from '@/views/Cart.vue'
import Address from '@/views/Address.vue'
import OrderConfirm from '@/views/OrderConfirm.vue'
import OrderList from '@/views/OrderList.vue'
import UserProfile from '@/views/UserProfile.vue'
import { fetchMyMenus } from '@/api/menus.js'

const adminRouteMap = {
  AdminUsers: () => import('@/views/admin/AdminUsers.vue'),
  AdminRoles: () => import('@/views/admin/AdminRoles.vue'),
  AdminProducts: () => import('@/views/admin/AdminProducts.vue'),
  AdminCategories: () => import('@/views/admin/AdminCategories.vue'),
  AdminCarts: () => import('@/views/admin/AdminCarts.vue'),
  AdminOrders: () => import('@/views/admin/AdminOrders.vue'),
  AdminBanners: () => import('@/views/admin/AdminBanners.vue'),
  AdminStats: () => import('@/views/admin/AdminStats.vue'),
}

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'Home',
      component: Home,
    },
    {
      path: '/login',
      name: 'Login',
      component: Login,
    },
    {
      path: '/register',
      name: 'Register',
      component: Register,
    },
    {
      path: '/products',
      name: 'ProductList',
      component: ProductList,
    },
    {
      path: '/product/:productId',
      name: 'ProductDetail',
      component: ProductDetail,
    },
    {
      path: '/cart',
      name: 'Cart',
      component: Cart,
    },
    {
      path: '/addresses',
      name: 'Address',
      component: Address,
    },
    {
      path: '/orders/confirm',
      name: 'OrderConfirm',
      component: OrderConfirm,
    },
    {
      path: '/orders',
      name: 'OrderList',
      component: OrderList,
    },
    {
      path: '/profile',
      name: 'UserProfile',
      component: UserProfile,
    },
  ],
})

const publicRoutes = ['/login', '/register']
let dynamicRoutesAdded = false

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

const addAdminRoutes = (menus) => {
  const flatMenus = flattenMenus(menus)
  const adminMenus = flatMenus.filter((menu) => menu.path && menu.path.startsWith('/admin'))
  if (!router.hasRoute('AdminRoot')) {
    const redirectPath = adminMenus[0]?.path || '/'
    router.addRoute({
      path: '/admin',
      name: 'AdminRoot',
      redirect: redirectPath,
      meta: { requiresAdmin: true },
    })
  }
  adminMenus.forEach((menu) => {
    const componentLoader = adminRouteMap[menu.component]
    if (!componentLoader) {
      return
    }
    if (!router.hasRoute(menu.component)) {
      router.addRoute({
        path: menu.path,
        name: menu.component,
        component: componentLoader,
        meta: { requiresAdmin: true },
      })
    }
  })
}

router.beforeEach(async (to) => {
  const token = localStorage.getItem('token')
  const roleKey = localStorage.getItem('roleKey')
  if (!token && !publicRoutes.includes(to.path)) {
    dynamicRoutesAdded = false
    return '/login'
  }
  if (token && publicRoutes.includes(to.path)) {
    return '/'
  }
  if (token && !dynamicRoutesAdded) {
    try {
      const res = await fetchMyMenus()
      if (res.code === 200) {
        addAdminRoutes(res.data || [])
        dynamicRoutesAdded = true
        if (to.path.startsWith('/admin')) {
          return to.fullPath
        }
      }
    } catch (error) {
      // Ignore menu loading errors and continue routing.
    }
  }
  if (to.meta.requiresAdmin && roleKey !== 'ADMIN') {
    return '/'
  }
  return true
})

export default router
