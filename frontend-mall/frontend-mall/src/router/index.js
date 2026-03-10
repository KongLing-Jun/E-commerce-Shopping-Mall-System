import { createRouter, createWebHistory } from 'vue-router'
import Home from '@/views/Home.vue'
import Login from '@/views/Login.vue'
import Register from '@/views/Register.vue'
import ProductList from '@/views/ProductList.vue'
import ProductDetail from '@/views/ProductDetail.vue'
import Cart from '@/views/Cart.vue'
import Address from '@/views/Address.vue'
import OrderConfirm from '@/views/OrderConfirm.vue'
import OrderPay from '@/views/OrderPay.vue'
import OrderList from '@/views/OrderList.vue'
import UserProfile from '@/views/UserProfile.vue'
import { fetchMyMenus } from '@/api/menus.js'
import { me } from '@/api/auth.js'

const adminRouteMap = {
  AdminUsers: () => import('@/views/admin/AdminUsers.vue'),
  AdminRoles: () => import('@/views/admin/AdminRoles.vue'),
  AdminProducts: () => import('@/views/admin/AdminProducts.vue'),
  AdminMenus: () => import('@/views/admin/AdminMenus.vue'),
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
      meta: { title: 'E E-Shop - 首页' },
    },
    {
      path: '/login',
      name: 'Login',
      component: Login,
      meta: { title: '用户登录' },
    },
    {
      path: '/register',
      name: 'Register',
      component: Register,
      meta: { title: '用户注册' },
    },
    {
      path: '/products',
      name: 'ProductList',
      component: ProductList,
      meta: { title: '商品列表' },
    },
    {
      path: '/product/:productId',
      name: 'ProductDetail',
      component: ProductDetail,
      meta: { title: '商品详情' },
    },
    {
      path: '/cart',
      name: 'Cart',
      component: Cart,
      meta: { requiresAuth: true, title: '购物车' },
    },
    {
      path: '/addresses',
      name: 'Address',
      component: Address,
      meta: { requiresAuth: true, title: '收货地址' },
    },
    {
      path: '/orders/confirm',
      name: 'OrderConfirm',
      component: OrderConfirm,
      meta: { requiresAuth: true, title: '确认订单' },
    },
    {
      path: '/orders',
      name: 'OrderList',
      component: OrderList,
      meta: { requiresAuth: true, title: '我的订单' },
    },
    {
      path: '/orders/pay/:orderNo',
      name: 'OrderPay',
      component: OrderPay,
      meta: { requiresAuth: true, title: 'Payment' },
    },
    {
      path: '/profile',
      name: 'UserProfile',
      component: UserProfile,
      meta: { requiresAuth: true, title: '用户中心' },
    },
    {
      path: '/admin',
      name: 'AdminRoot',
      redirect: '/admin/stats',
      meta: { requiresAdmin: true },
    },
    {
      path: '/admin/stats',
      name: 'AdminStats',
      component: () => import('@/views/admin/AdminStats.vue'),
      meta: { requiresAdmin: true },
    },
    {
      path: '/admin/users',
      name: 'AdminUsers',
      component: () => import('@/views/admin/AdminUsers.vue'),
      meta: { requiresAdmin: true },
    },
    {
      path: '/admin/products',
      name: 'AdminProducts',
      component: () => import('@/views/admin/AdminProducts.vue'),
      meta: { requiresAdmin: true },
    },
    {
      path: '/admin/menus',
      name: 'AdminMenus',
      component: () => import('@/views/admin/AdminMenus.vue'),
      meta: { requiresAdmin: true },
    },
    {
      path: '/admin/roles',
      name: 'AdminRoles',
      component: () => import('@/views/admin/AdminRoles.vue'),
      meta: { requiresAdmin: true },
    },
    {
      path: '/admin/categories',
      name: 'AdminCategories',
      component: () => import('@/views/admin/AdminCategories.vue'),
      meta: { requiresAdmin: true },
    },
    {
      path: '/admin/orders',
      name: 'AdminOrders',
      component: () => import('@/views/admin/AdminOrders.vue'),
      meta: { requiresAdmin: true },
    },
    {
      path: '/admin/banners',
      name: 'AdminBanners',
      component: () => import('@/views/admin/AdminBanners.vue'),
      meta: { requiresAdmin: true },
    },
    {
      path: '/admin/carts',
      name: 'AdminCarts',
      component: () => import('@/views/admin/AdminCarts.vue'),
      meta: { requiresAdmin: true },
    },
  ],
})

const publicRoutes = ['/login', '/register']
let dynamicRoutesAdded = false

const parseLocalArray = (key) => {
  try {
    const value = localStorage.getItem(key)
    const parsed = value ? JSON.parse(value) : []
    return Array.isArray(parsed) ? parsed : []
  } catch {
    return []
  }
}

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
        meta: { requiresAdmin: true, perm: menu.permCode || null },
      })
    }
  })
}

router.beforeEach(async (to) => {
  // 设置页面标题
  if (to.meta.title) {
    document.title = to.meta.title
  }

  const token = localStorage.getItem('token')
  let roleKey = localStorage.getItem('roleKey')
  let perms = parseLocalArray('perms')
  if (!token) {
    dynamicRoutesAdded = false
  }
  if (!token && to.meta.requiresAuth) {
    return '/login'
  }
  if (token && publicRoutes.includes(to.path)) {
    return '/'
  }
  if (token && to.path.startsWith('/admin')) {
    try {
      // 所有后台路由都以服务端身份信息为准，防止篡改 localStorage 冒充管理员。
      const meRes = await me()
      if (meRes.code !== 200 || meRes.data.roleKey !== 'ADMIN') {
        return '/'
      }
      roleKey = meRes.data.roleKey
      perms = Array.isArray(meRes.data.perms) ? meRes.data.perms : []
      localStorage.setItem('userId', meRes.data.userId)
      localStorage.setItem('roleKey', meRes.data.roleKey)
      localStorage.setItem('menus', JSON.stringify(meRes.data.menus || []))
      localStorage.setItem('perms', JSON.stringify(perms))
    } catch (error) {
      return '/login'
    }
  }
  if (token && !dynamicRoutesAdded) {
    try {
      if (!roleKey) {
        const meRes = await me()
        if (meRes.code === 200) {
          roleKey = meRes.data.roleKey
          localStorage.setItem('userId', meRes.data.userId)
          localStorage.setItem('roleKey', meRes.data.roleKey)
          localStorage.setItem('menus', JSON.stringify(meRes.data.menus || []))
          localStorage.setItem('perms', JSON.stringify(meRes.data.perms || []))
          perms = Array.isArray(meRes.data.perms) ? meRes.data.perms : []
        }
      }
      const cachedMenus = parseLocalArray('menus')
      if (cachedMenus.length > 0) {
        addAdminRoutes(cachedMenus)
        dynamicRoutesAdded = true
      } else {
        const res = await fetchMyMenus()
        if (res.code === 200) {
          const menus = res.data || []
          localStorage.setItem('menus', JSON.stringify(menus))
          addAdminRoutes(menus)
          dynamicRoutesAdded = true
        }
      }
      if (to.path.startsWith('/admin')) {
        return to.fullPath
      }
    } catch (error) {
      localStorage.removeItem('token')
      localStorage.removeItem('userId')
      localStorage.removeItem('roleKey')
      localStorage.removeItem('menus')
      localStorage.removeItem('perms')
      return '/login'
    }
  }
  if (to.path.startsWith('/admin') && roleKey !== 'ADMIN') {
    return '/'
  }
  if (to.meta.requiresAdmin && roleKey !== 'ADMIN') {
    return '/'
  }
  if (to.meta.perm && !perms.includes(to.meta.perm)) {
    return '/'
  }
  return true
})

export default router
