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
const dual = (zh, en) => (locale.value === 'zh' ? zh : en)

// 统一价格格式化，避免模板里重复处理。
const formatPrice = (value) => Number(value || 0).toFixed(2)

// 拉取当前用户购物车数据并渲染列表。
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

// 抽取购物车项更新请求，供数量与勾选复用。
const syncItem = async (item, payload) => {
  const res = await updateCartItem(item.cartItemId, payload)
  if (res.code !== 200) {
    throw new Error(res.message || t('cart.updateFail'))
  }
}

// 修改商品数量，失败时回滚到修改前。
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

// 修改勾选状态，失败时回滚勾选值。
const updateChecked = async (item) => {
  try {
    await syncItem(item, { checked: item.checked })
  } catch (error) {
    item.checked = item.checked === 1 ? 0 : 1
    ElMessage.error(error.message || t('cart.updateFail'))
  }
}

// 删除购物车项并同步刷新本地列表。
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

// 已勾选商品，用于结算金额计算。
const checkedItems = computed(() => cartItems.value.filter((item) => item.checked === 1))

// 小计、运费、税费和总额按当前勾选项实时计算。
const subtotal = computed(() => checkedItems.value.reduce((sum, item) => sum + item.price * item.quantity, 0))
const shipping = computed(() => (checkedItems.value.length ? 12 : 0))
const tax = computed(() => subtotal.value * 0.08)
const totalAmount = computed(() => subtotal.value + shipping.value + tax.value - subtotal.value * discountRate.value)

// 本地优惠码演示逻辑，后续可替换为后端校验。
const applyCoupon = () => {
  if (coupon.value.trim().toUpperCase() === 'SAVE10') {
    discountRate.value = 0.1
    ElMessage.success(dual('优惠码已生效：-10%', 'Coupon applied: -10%'))
    return
  }
  discountRate.value = 0
  ElMessage.warning(dual('优惠码无效', 'Invalid coupon code'))
}

// 仅当有勾选商品时允许进入确认订单页。
const goToCheckout = () => {
  if (!checkedItems.value.length) {
    ElMessage.warning(t('orderConfirm.noItems'))
    return
  }
  router.push('/orders/confirm')
}

// 页面初始化时加载购物车。
onMounted(loadCart)
</script>


