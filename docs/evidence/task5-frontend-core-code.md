# 任务5：前端核心代码片段

## 目录
- [1. 登录/注册](#1-登录注册)
- [2. 购物车](#2-购物车)
- [3. 订单](#3-订单)
- [4. 路由](#4-路由)

---

## 1. 登录/注册

### 1.1 登录页面 (Login.vue)

```vue
<template>
  <div class="relative flex min-h-screen items-center justify-center overflow-hidden px-4 py-10">
    <div class="absolute inset-0 bg-[url('https://images.unsplash.com/photo-1497366754035-f200968a6e72?auto=format&fit=crop&w=1920&q=80')] bg-cover bg-center"></div>
    <div class="absolute inset-0 bg-slate-900/45 backdrop-blur-[3px]"></div>

    <div class="relative z-10 w-full max-w-[520px] space-y-5">
      <div class="rounded-3xl border border-white/40 bg-white/92 shadow-2xl backdrop-blur-md">
        <div class="border-b border-slate-200/80 px-10 pb-8 pt-10 text-center">
          <div class="mx-auto grid h-16 w-16 place-content-center rounded-full bg-blue-100 text-3xl font-bold text-blue-600">AP</div>
          <h1 class="mt-6 text-5xl font-extrabold tracking-tight text-slate-900">{{ t('auth.adminPortal') }}</h1>
          <p class="mt-3 text-lg text-slate-500">{{ t('auth.loginSubtitle') }}</p>
        </div>

        <el-form :model="form" label-position="top" class="space-y-2 px-10 pb-8 pt-8" @keyup.enter="handleLogin">
          <el-form-item :label="t('auth.username')">
            <el-input v-model="form.username" size="large" :placeholder="t('auth.username')" />
          </el-form-item>

          <el-form-item :label="t('auth.password')">
            <el-input
              v-model="form.password"
              size="large"
              type="password"
              show-password
              :placeholder="t('auth.password')"
            />
          </el-form-item>

          <div class="flex items-center justify-between pb-1 pt-1 text-sm">
            <el-checkbox v-model="remember">{{ t('auth.rememberMe') }}</el-checkbox>
            <button class="text-blue-600 hover:text-blue-700" type="button" @click="handleForgotPassword">
              {{ t('auth.forgotPassword') }}
            </button>
          </div>

          <el-button class="mt-2 w-full" size="large" type="primary" :loading="loading" @click="handleLogin">
            {{ t('auth.login') }}
          </el-button>
        </el-form>

        <div class="border-t border-slate-200/80 px-10 py-5 text-center text-sm text-slate-500">
          {{ t('auth.secureBy') }}
        </div>
      </div>
      <div class="text-center text-white/90">
        <button type="button" class="text-base hover:text-white" @click="router.push('/')">{{ t('auth.backToStore') }}</button>
        <div class="mt-2 text-sm">
          {{ t('auth.noAccount') }}
          <el-link class="!text-white" @click="router.push('/register')">{{ t('auth.toRegister') }}</el-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { login } from '@/api/auth.js'
import { useAuth } from '@/composables/useAuth.js'
import { useI18n } from '@/i18n/index.js'

const router = useRouter()
const loading = ref(false)
const remember = ref(false)
const form = ref({
  username: '',
  password: '',
})
const { refreshAuth } = useAuth()
const { t } = useI18n()

onMounted(() => {
  const rememberedUsername = localStorage.getItem('rememberedUsername')
  if (rememberedUsername) {
    form.value.username = rememberedUsername
    remember.value = true
  }
})

// 功能：处理忘记密码
const handleForgotPassword = () => {
  ElMessage.info(t('auth.contactAdminReset'))
}

// 功能：处理登录
const handleLogin = async () => {
  if (!form.value.username || !form.value.password) {
    ElMessage.warning(t('auth.completeInfo'))
    return
  }

  loading.value = true
  try {
    const res = await login(form.value)
    if (res.code === 200) {
      localStorage.setItem('token', res.data.token)
      localStorage.setItem('userId', res.data.userId)
      localStorage.setItem('roleKey', res.data.roleKey)
      localStorage.setItem('menus', JSON.stringify(res.data.menus || []))
      localStorage.setItem('perms', JSON.stringify(res.data.perms || []))
      if (remember.value) {
        localStorage.setItem('rememberedUsername', form.value.username)
      } else {
        localStorage.removeItem('rememberedUsername')
      }
      refreshAuth()
      window.dispatchEvent(new Event('auth-changed'))
      ElMessage.success(t('auth.loginSuccess'))
      router.push(res.data.roleKey === 'ADMIN' ? '/admin' : '/')
    } else {
      ElMessage.error(res.message)
    }
  } catch {
    ElMessage.error(t('auth.loginFail'))
  } finally {
    loading.value = false
  }
}
</script>
```

### 1.2 认证API (auth.js)

```javascript
import request from './request.js'

// 功能：注册账号
export const register = (data) => {
    return request.post('/api/auth/register', data)
}

// 功能：登录系统
export const login = (data) => {
    return request.post('/api/auth/login', data)
}

// 功能：退出登录
export const logout = () => {
    return request.post('/api/auth/logout')
}

// 功能：获取当前用户信息
export const me = () => {
    return request.get('/api/auth/me')
}
```

---

## 2. 购物车

### 2.1 购物车页面 (Cart.vue)

```vue
<template>
  <div class="grid gap-8 xl:grid-cols-[1fr_380px]">
    <section class="space-y-4">
      <div class="flex items-end justify-between">
        <div>
          <p class="text-sm text-[var(--muted)]">{{ dual('首页', 'Home') }} / {{ t('cart.title') }}</p>
          <h1 class="mt-2 text-5xl font-extrabold">{{ t('cart.title') }} <span class="text-2xl text-[var(--muted)]">({{ cartItems.length }})</span></h1>
        </div>
      </div>

      <article
        v-for="item in cartItems"
        :key="item.cartItemId"
        class="rounded-2xl border border-[var(--line)] bg-[var(--surface)] p-5"
      >
        <div class="flex flex-wrap items-center gap-4 lg:flex-nowrap">
          <el-checkbox
            v-model="item.checked"
            :true-label="1"
            :false-label="0"
            @change="updateChecked(item)"
          />
          <img :src="item.image" :alt="item.name" class="h-28 w-28 rounded-xl object-cover" />
          <div class="min-w-0 flex-1">
            <h3 class="truncate text-3xl font-extrabold">{{ item.name }}</h3>
            <p class="mt-2 text-base text-[var(--muted)]">{{ dual('有库存', 'In Stock') }}</p>
            <div class="mt-2 text-4xl font-extrabold">$ {{ formatPrice(item.price) }}</div>
          </div>
          <div class="flex items-center gap-2 rounded-xl border border-[var(--line)] px-2 py-2">
            <el-button text @click="changeQuantity(item, -1)">-</el-button>
            <span class="w-8 text-center font-bold">{{ item.quantity }}</span>
            <el-button text @click="changeQuantity(item, 1)">+</el-button>
          </div>
          <el-button text type="danger" @click="removeItem(item)">{{ t('common.delete') }}</el-button>
        </div>
      </article>

      <el-empty v-if="!cartItems.length" :description="t('cart.empty')" />
      <el-button text type="primary" @click="router.push('/products')">{{ dual('继续购物', 'Back to Shopping') }}</el-button>
    </section>

    <aside class="h-fit rounded-2xl border border-[var(--line)] bg-[var(--surface)] p-6">
      <h2 class="text-4xl font-extrabold">{{ dual('订单摘要', 'Order Summary') }}</h2>
      <div class="mt-5 space-y-3 text-lg">
        <div class="flex items-center justify-between">
           <span class="text-[var(--muted)]">{{ dual('商品小计', 'Subtotal') }}</span>
          <strong>$ {{ formatPrice(subtotal) }}</strong>
        </div>
        <div class="flex items-center justify-between">
           <span class="text-[var(--muted)]">{{ dual('运费预估', 'Shipping estimate') }}</span>
          <strong>$ {{ formatPrice(shipping) }}</strong>
        </div>
        <div class="flex items-center justify-between">
           <span class="text-[var(--muted)]">{{ dual('税费预估', 'Tax estimate') }}</span>
          <strong>$ {{ formatPrice(tax) }}</strong>
        </div>
      </div>

      <div class="mt-5 flex items-center gap-2">
        <el-input v-model="coupon" :placeholder="dual('礼品卡或优惠码', 'Gift card or discount code')" />
        <el-button @click="applyCoupon">{{ dual('应用', 'Apply') }}</el-button>
      </div>

      <div class="mt-6 border-t border-[var(--line)] pt-6">
        <div class="flex items-center justify-between text-4xl font-extrabold">
          <span>{{ t('common.total') }}</span>
          <span>$ {{ formatPrice(totalAmount) }}</span>
        </div>
        <el-button
          class="mt-5 w-full"
          size="large"
          type="primary"
          :disabled="!checkedItems.length"
          @click="goToCheckout"
        >
          {{ dual('去结算', 'Proceed to Checkout') }}
        </el-button>
      </div>
    </aside>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { deleteCartItem, getCartItems, updateCartItem } from '@/api/cart.js'
import { useI18n } from '@/i18n/index.js'

const router = useRouter()
const cartItems = ref([])
const coupon = ref('')
const discountRate = ref(0)
const { t, locale } = useI18n()
// 功能：处理中英文双语
const dual = (zh, en) => (locale.value === 'zh' ? zh : en)

// 功能：统一价格格式化
const formatPrice = (value) => Number(value || 0).toFixed(2)

// 功能：拉取当前用户购物车数据并渲染列表
const loadCart = async () => {
  try {
    const res = await getCartItems()
    if (res.code === 200) {
      cartItems.value = res.data || []
    } else {
      ElMessage.error(res.message)
    }
  } catch {
    ElMessage.error(t('cart.loadFail'))
  }
}

// 功能：抽取购物车项更新请求，供数量与勾选复用
const syncItem = async (item, payload) => {
  const res = await updateCartItem(item.cartItemId, payload)
  if (res.code !== 200) {
    throw new Error(res.message || t('cart.updateFail'))
  }
}

// 功能：修改商品数量，失败时回滚到修改前
const changeQuantity = async (item, delta) => {
  const next = item.quantity + delta
  if (next < 1) {
    return
  }
  const previous = item.quantity
  item.quantity = next
  try {
    await syncItem(item, { quantity: next })
  } catch (error) {
    item.quantity = previous
    ElMessage.error(error.message || t('cart.updateFail'))
  }
}

// 功能：修改勾选状态，失败时回滚勾选值
const updateChecked = async (item) => {
  try {
    await syncItem(item, { checked: item.checked })
  } catch (error) {
    item.checked = item.checked === 1 ? 0 : 1
    ElMessage.error(error.message || t('cart.updateFail'))
  }
}

// 功能：删除购物车项并同步刷新本地列表
const removeItem = async (item) => {
  try {
    const res = await deleteCartItem(item.cartItemId)
    if (res.code === 200) {
      cartItems.value = cartItems.value.filter((cart) => cart.cartItemId !== item.cartItemId)
      ElMessage.success(t('cart.removeSuccess'))
    } else {
      ElMessage.error(res.message)
    }
  } catch {
    ElMessage.error(t('cart.removeFail'))
  }
}

// 功能：已勾选商品，用于结算金额计算
const checkedItems = computed(() => cartItems.value.filter((item) => item.checked === 1))

// 功能：小计、运费、税费和总额按当前勾选项实时计算
const subtotal = computed(() => checkedItems.value.reduce((sum, item) => sum + item.price * item.quantity, 0))
const shipping = computed(() => (checkedItems.value.length ? 12 : 0))
const tax = computed(() => subtotal.value * 0.08)
const totalAmount = computed(() => subtotal.value + shipping.value + tax.value - subtotal.value * discountRate.value)

// 功能：本地优惠码演示逻辑
const applyCoupon = () => {
  if (coupon.value.trim().toUpperCase() === 'SAVE10') {
    discountRate.value = 0.1
    ElMessage.success(dual('优惠码已生效：-10%', 'Coupon applied: -10%'))
    return
  }
  discountRate.value = 0
  ElMessage.warning(dual('优惠码无效', 'Invalid coupon code'))
}

// 功能：仅当有勾选商品时允许进入确认订单页
const goToCheckout = () => {
  if (!checkedItems.value.length) {
    ElMessage.warning(t('orderConfirm.noItems'))
    return
  }
  router.push('/orders/confirm')
}

// 功能：页面初始化时加载购物车
onMounted(loadCart)
</script>
```

### 2.2 购物车API (cart.js)

```javascript
import request from './request.js'

// 功能：新增购物车明细
export const addCartItem = (data) => {
  return request.post('/api/cart/items', data)
}

// 功能：获取购物车明细
export const getCartItems = () => {
  return request.get('/api/cart/items')
}

// 功能：更新购物车明细
export const updateCartItem = (id, data) => {
  return request.put(`/api/cart/items/${id}`, data)
}

// 功能：删除购物车明细
export const deleteCartItem = (id) => {
  return request.delete(`/api/cart/items/${id}`)
}
```

---

## 3. 订单

### 3.1 订单确认页面 (OrderConfirm.vue)

```vue
<template>
  <div class="grid gap-8 xl:grid-cols-[1fr_360px]">
    <section class="space-y-6">
      <header>
        <div class="flex items-center justify-between">
          <h1 class="text-4xl font-extrabold">{{ dual('第 2 步（共 3 步）：支付', 'Step 2 of 3: Payment') }}</h1>
          <span class="text-lg text-[var(--muted)]">{{ dual('下一步：复核', 'Next: Review') }}</span>
        </div>
        <div class="mt-5 grid grid-cols-3 gap-2 text-sm font-semibold text-[var(--muted)]">
          <div class="rounded-full bg-[var(--accent)] px-4 py-2 text-center text-white">{{ dual('配送', 'Shipping') }}</div>
          <div class="rounded-full bg-[var(--highlight)] px-4 py-2 text-center text-[var(--accent)]">{{ dual('支付', 'Payment') }}</div>
          <div class="rounded-full bg-[var(--surface-soft)] px-4 py-2 text-center">{{ dual('复核', 'Review') }}</div>
        </div>
      </header>

      <article class="rounded-2xl border border-[var(--line)] bg-[var(--surface)]">
        <div class="flex items-center justify-between border-b border-[var(--line)] px-6 py-4">
          <h2 class="text-2xl font-extrabold">{{ dual('收货地址', 'Shipping Address') }}</h2>
          <el-button text type="primary" @click="router.push('/addresses')">{{ t('common.edit') }}</el-button>
        </div>
        <div class="grid gap-4 p-6 md:grid-cols-2">
          <button
            v-for="address in addresses"
            :key="address.id"
            type="button"
            class="rounded-xl border p-4 text-left"
            :class="selectedAddressId === address.id ? 'border-[var(--accent)] bg-[var(--highlight)]' : 'border-[var(--line)]'"
            @click="selectedAddressId = address.id"
          >
            <div class="text-lg font-bold">{{ address.receiver }} - {{ address.phone }}</div>
            <p class="mt-2 text-sm text-[var(--muted)]">
              {{ address.province }} {{ address.city }} {{ address.area }} {{ address.detail }}
            </p>
            <span v-if="address.isDefault === 1" class="chip mt-3">{{ t('address.default') }}</span>
          </button>
        </div>
      </article>

      <article class="rounded-2xl border border-[var(--line)] bg-[var(--surface)] p-6">
        <h2 class="text-3xl font-extrabold">{{ dual('支付方式', 'Payment Method') }}</h2>
        <p class="muted-text mt-2">{{ dual('所有交易均已加密并安全保护。', 'All transactions are secure and encrypted.') }}</p>

        <el-radio-group v-model="paymentMethod" class="mt-5 grid gap-3">
          <el-radio value="card" border size="large">{{ dual('信用卡 / 借记卡', 'Credit or Debit Card') }}</el-radio>
          <el-radio value="wallet" border size="large">{{ dual('数字钱包', 'Digital Wallet') }}</el-radio>
        </el-radio-group>

        <div class="mt-5 grid gap-4">
          <el-input v-model="paymentForm.cardNo" :placeholder="dual('卡号', 'Card Number')" />
          <div class="grid grid-cols-2 gap-4">
            <el-input v-model="paymentForm.expire" :placeholder="dual('月 / 年', 'MM / YY')" />
            <el-input v-model="paymentForm.cvc" placeholder="CVC" />
          </div>
          <el-input v-model="paymentForm.holder" :placeholder="dual('持卡人姓名', 'Name on Card')" />
          <el-checkbox v-model="paymentForm.saveCard">{{ dual('保存该卡用于下次支付', 'Save this card for future purchases') }}</el-checkbox>
        </div>
      </article>

      <div class="flex items-center justify-between">
        <el-button @click="router.push('/cart')">{{ dual('返回购物车', 'Back to Cart') }}</el-button>
        <el-button type="primary" size="large" :loading="loading" @click="submitOrder">{{ dual('提交并复核', 'Review Order') }}</el-button>
      </div>
    </section>

    <aside class="h-fit rounded-2xl border border-[var(--line)] bg-[var(--surface)]">
      <div class="border-b border-[var(--line)] px-6 py-5">
        <h3 class="text-3xl font-extrabold">{{ dual('订单摘要', 'Order Summary') }}</h3>
      </div>
      <div class="space-y-4 px-6 py-5">
        <div v-for="item in orderItems" :key="item.cartItemId" class="flex items-center gap-3">
          <img :src="item.image" :alt="item.name" class="h-16 w-16 rounded-lg object-cover" />
          <div class="min-w-0 flex-1">
            <p class="line-clamp-1 text-base font-bold">{{ item.name }}</p>
            <p class="text-sm text-[var(--muted)]">{{ dual('数量', 'Qty') }}: {{ item.quantity }}</p>
          </div>
          <strong class="text-lg">$ {{ formatPrice(item.price * item.quantity) }}</strong>
        </div>
      </div>

      <div class="space-y-3 border-t border-[var(--line)] px-6 py-5 text-base">
        <div class="flex items-center justify-between text-[var(--muted)]">
          <span>{{ dual('商品小计', 'Subtotal') }}</span>
          <span>$ {{ formatPrice(totalAmount) }}</span>
        </div>
        <div class="flex items-center justify-between text-[var(--muted)]">
          <span>{{ dual('运费', 'Shipping') }}</span>
          <span>{{ dual('免运费', 'Free') }}</span>
        </div>
        <div class="flex items-center justify-between text-[var(--muted)]">
          <span>{{ dual('预估税费', 'Estimated Tax') }}</span>
          <span>$ {{ formatPrice(totalAmount * 0.08) }}</span>
        </div>
        <div class="flex items-center justify-between border-t border-[var(--line)] pt-4 text-3xl font-extrabold">
          <span>{{ t('common.total') }}</span>
          <span class="text-[var(--accent)]">$ {{ formatPrice(totalAmount * 1.08) }}</span>
        </div>
      </div>
    </aside>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { createOrder, getOrderPre } from '@/api/order.js'
import { useI18n } from '@/i18n/index.js'

const router = useRouter()
const orderItems = ref([])
const addresses = ref([])
const totalAmount = ref(0)
const selectedAddressId = ref(null)
const paymentMethod = ref('card')
const loading = ref(false)
const paymentForm = reactive({
  cardNo: '',
  expire: '',
  cvc: '',
  holder: '',
  saveCard: false,
})
const { t, locale } = useI18n()
// 功能：处理中英文双语
const dual = (zh, en) => (locale.value === 'zh' ? zh : en)

// 功能：统一价格格式化
const formatPrice = (value) => Number(value || 0).toFixed(2)

// 功能：读取确认页预览数据：勾选商品、地址列表、金额汇总
const loadPreOrder = async () => {
  try {
    const res = await getOrderPre()
    if (res.code === 200) {
      orderItems.value = res.data.items || []
      addresses.value = res.data.addresses || []
      totalAmount.value = Number(res.data.totalAmount || 0)
      const defaultAddress = addresses.value.find((item) => item.isDefault === 1)
      selectedAddressId.value = defaultAddress ? defaultAddress.id : addresses.value[0]?.id || null
    } else {
      ElMessage.error(res.message)
    }
  } catch {
    ElMessage.error(t('orderConfirm.loadFail'))
  }
}

// 功能：提交订单前校验地址与商品，再创建主单和子单
const submitOrder = async () => {
  if (!selectedAddressId.value) {
    ElMessage.warning(t('orderConfirm.selectAddress'))
    return
  }
  if (!orderItems.value.length) {
    ElMessage.warning(t('orderConfirm.noItems'))
    return
  }
  loading.value = true
  try {
    const res = await createOrder({ addressId: selectedAddressId.value })
    if (res.code === 200) {
      ElMessage.success(`${t('orderConfirm.created')} #${res.data.orderNo}`)
      router.push(`/orders/pay/${res.data.orderNo}`)
    } else {
      ElMessage.error(res.message)
    }
  } catch {
    ElMessage.error(t('orderConfirm.submitFail'))
  } finally {
    loading.value = false
  }
}

// 功能：页面加载时初始化确认页数据
onMounted(loadPreOrder)
</script>
```

### 3.2 订单API (order.js)

```javascript
import request from './request.js'

// 功能：获取订单预览数据
export const getOrderPre = () => {
  return request.get('/api/orders/pre')
}

// 功能：创建订单并扣减库存
export const createOrder = (data) => {
  return request.post('/api/orders', data)
}

// 功能：执行订单支付
export const payOrder = (orderNo, data) => {
  return request.post(`/api/orders/${orderNo}/pay`, data)
}

// 功能：获取订单详情
export const getOrderDetail = (orderNo) => {
  return request.get(`/api/orders/${orderNo}`)
}

// 功能：获取订单物流轨迹
export const getOrderTracking = (orderNo) => {
  return request.get(`/api/orders/${orderNo}/tracking`)
}

// 功能：获取订单分页列表
export const getOrders = (params) => {
  return request.get('/api/orders', { params })
}

// 功能：确认收货并更新订单状态
export const confirmOrder = (orderNo) => {
  return request.post(`/api/orders/${orderNo}/confirm`)
}

// 功能：获取订单发票
export const getOrderInvoice = (orderNo) => {
  return request.get(`/api/orders/${orderNo}/invoice`)
}

// 功能：处理再次购买
export const rebuyOrder = (orderNo) => {
  return request.post(`/api/orders/${orderNo}/rebuy`)
}
```

---

## 4. 路由

### 4.1 路由配置 (router/index.js)

```javascript
import { createRouter, createWebHistory } from 'vue-router'
import Home from '@/views/Home.vue'
import Login from '@/views/Login.vue'
import Register from '@/views/Register.vue'
import ProductList from '@/views/ProductList.vue'
import ProductDetail from '@/views/ProductDetail.vue'
import CategoryBrowse from '@/views/CategoryBrowse.vue'
import Cart from '@/views/Cart.vue'
import Address from '@/views/Address.vue'
import OrderConfirm from '@/views/OrderConfirm.vue'
import OrderPay from '@/views/OrderPay.vue'
import OrderList from '@/views/OrderList.vue'
import OrderTrack from '@/views/OrderTrack.vue'
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
      meta: { title: 'E-Shop Home' },
    },
    {
      path: '/login',
      name: 'Login',
      component: Login,
      meta: { title: 'Login' },
    },
    {
      path: '/register',
      name: 'Register',
      component: Register,
      meta: { title: 'Register' },
    },
    {
      path: '/products',
      name: 'ProductList',
      component: ProductList,
      meta: { title: 'Products' },
    },
    {
      path: '/categories',
      name: 'CategoryBrowse',
      component: CategoryBrowse,
      meta: { title: 'Categories' },
    },
    {
      path: '/product/:productId',
      name: 'ProductDetail',
      component: ProductDetail,
      meta: { title: 'Product Detail' },
    },
    {
      path: '/cart',
      name: 'Cart',
      component: Cart,
      meta: { requiresAuth: true, title: 'Cart' },
    },
    {
      path: '/addresses',
      name: 'Address',
      component: Address,
      meta: { requiresAuth: true, title: 'Addresses' },
    },
    {
      path: '/orders/confirm',
      name: 'OrderConfirm',
      component: OrderConfirm,
      meta: { requiresAuth: true, title: 'Order Confirm' },
    },
    {
      path: '/orders',
      name: 'OrderList',
      component: OrderList,
      meta: { requiresAuth: true, title: 'My Orders' },
    },
    {
      path: '/orders/track',
      name: 'OrderTrack',
      component: OrderTrack,
      meta: { requiresAuth: true, title: 'Order Tracking' },
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
      meta: { requiresAuth: true, title: 'Profile' },
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

// 功能：解析本地存储数组
const parseLocalArray = (key) => {
  try {
    const value = localStorage.getItem(key)
    const parsed = value ? JSON.parse(value) : []
    return Array.isArray(parsed) ? parsed : []
  } catch {
    return []
  }
}

// 功能：扁平化菜单树
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

// 功能：根据菜单动态注入后台路由
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
      // 所有后台路由都以服务端身份信息为准，防止篡改 localStorage 冒充管理员
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
```

---

## 文件清单

| 模块 | 文件路径 | 说明 |
|------|---------|------|
| 登录/注册 | `frontend-mall/frontend-mall/src/views/Login.vue` | 登录页面 |
| 登录/注册 | `frontend-mall/frontend-mall/src/api/auth.js` | 认证API |
| 购物车 | `frontend-mall/frontend-mall/src/views/Cart.vue` | 购物车页面 |
| 购物车 | `frontend-mall/frontend-mall/src/api/cart.js` | 购物车API |
| 订单 | `frontend-mall/frontend-mall/src/views/OrderConfirm.vue` | 订单确认页 |
| 订单 | `frontend-mall/frontend-mall/src/api/order.js` | 订单API |
| 路由 | `frontend-mall/frontend-mall/src/router/index.js` | 路由配置 |
