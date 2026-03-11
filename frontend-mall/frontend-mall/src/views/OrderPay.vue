<template>
  <div class="grid gap-8 xl:grid-cols-[1fr_360px]">
    <section class="space-y-6">
      <header>
        <p class="text-sm text-[var(--muted)]">{{ dual('订单 / 支付', 'Orders / Payment') }}</p>
        <h1 class="mt-2 text-4xl font-extrabold">{{ dual('支付订单', 'Pay for Order') }}</h1>
        <p class="muted-text mt-2">{{ dual('请确认订单信息并完成支付。', 'Review your order and complete payment.') }}</p>
      </header>

      <article class="rounded-2xl border border-[var(--line)] bg-[var(--surface)] p-6">
        <div class="flex items-center justify-between">
          <h2 class="text-2xl font-extrabold">{{ dual('订单信息', 'Order Summary') }}</h2>
          <el-tag v-if="order" :type="order.status === 0 ? 'warning' : 'success'">
            {{ statusText(order?.status) }}
          </el-tag>
        </div>
        <div v-if="loading" class="py-6 text-center text-[var(--muted)]">{{ t('common.loading') }}</div>
        <div v-else-if="order" class="mt-4 space-y-2 text-base">
          <div class="flex items-center justify-between">
            <span class="text-[var(--muted)]">{{ dual('订单编号', 'Order No') }}</span>
            <span class="font-semibold">#{{ order.orderNo }}</span>
          </div>
          <div class="flex items-center justify-between">
            <span class="text-[var(--muted)]">{{ dual('下单时间', 'Created At') }}</span>
            <span class="font-semibold">{{ formatDate(order.createdAt) }}</span>
          </div>
          <div class="flex items-center justify-between">
            <span class="text-[var(--muted)]">{{ dual('应付金额', 'Amount Due') }}</span>
            <span class="text-2xl font-extrabold text-[var(--accent)]">$ {{ formatPrice(order.payAmount) }}</span>
          </div>
        </div>
        <el-empty v-else :description="errorText || t('common.empty')" class="mt-6" />
      </article>

      <article class="rounded-2xl border border-[var(--line)] bg-[var(--surface)] p-6">
        <h2 class="text-2xl font-extrabold">{{ dual('支付方式', 'Payment Method') }}</h2>
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

      <div class="flex flex-wrap items-center gap-3">
        <el-button @click="router.push('/orders')">{{ dual('返回订单', 'Back to Orders') }}</el-button>
        <el-button
          type="primary"
          size="large"
          :loading="paying"
          :disabled="!order || order.status !== 0"
          @click="payNow"
        >
          {{ t('orderList.pay') }}
        </el-button>
      </div>
    </section>

    <aside class="h-fit rounded-2xl border border-[var(--line)] bg-[var(--surface)]">
      <div class="border-b border-[var(--line)] px-6 py-5">
        <h3 class="text-2xl font-extrabold">{{ dual('商品清单', 'Items') }}</h3>
      </div>
      <div class="space-y-4 px-6 py-5">
        <div v-for="item in orderItems" :key="item.productId" class="flex items-center gap-3">
          <img :src="item.image" :alt="item.productName" class="h-16 w-16 rounded-lg object-cover" />
          <div class="min-w-0 flex-1">
            <p class="line-clamp-1 text-base font-bold">{{ item.productName }}</p>
            <p class="text-sm text-[var(--muted)]">{{ dual('数量', 'Qty') }}: {{ item.quantity }}</p>
          </div>
          <strong class="text-lg">$ {{ formatPrice(item.price * item.quantity) }}</strong>
        </div>
        <el-empty v-if="!orderItems.length && !loading" :description="t('orderConfirm.noItems')" />
      </div>
    </aside>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getOrderDetail, payOrder } from '@/api/order.js'
import { useI18n } from '@/i18n/index.js'

const route = useRoute()
const router = useRouter()
const { t, locale } = useI18n()
// 功能：处理dual
const dual = (zh, en) => (locale.value === 'zh' ? zh : en)

const order = ref(null)
const loading = ref(false)
const paying = ref(false)
const errorText = ref('')
const paymentMethod = ref('card')
const paymentForm = reactive({
  cardNo: '',
  expire: '',
  cvc: '',
  holder: '',
  saveCard: false,
})

const orderNo = computed(() => route.params.orderNo)
const orderItems = computed(() => order.value?.items || [])

// 功能：格式化价格
const formatPrice = (value) => Number(value || 0).toFixed(2)
// 功能：格式化日期
const formatDate = (value) => {
  if (!value) return '--'
  const date = new Date(value)
  return date.toLocaleDateString()
}

// 功能：订单状态文本映射
const statusText = (status) => {
  if (status === 0) return t('orderList.pending')
  if (status === 1) return t('orderList.paid')
  if (status === 2) return t('orderList.shipped')
  if (status === 3) return t('orderList.completed')
  return t('common.status')
}

// 功能：加载订单
const loadOrder = async () => {
  if (!orderNo.value) {
    errorText.value = dual('订单号无效', 'Invalid order number')
    return
  }
  loading.value = true
  errorText.value = ''
  try {
    const res = await getOrderDetail(orderNo.value)
    if (res.code === 200) {
      order.value = res.data
    } else {
      errorText.value = res.message || t('common.empty')
    }
  } catch {
    errorText.value = dual('订单加载失败', 'Failed to load order')
  } finally {
    loading.value = false
  }
}

// 功能：支付now
const payNow = async () => {
  if (!order.value) return
  if (order.value.status !== 0) {
    ElMessage.warning(dual('该订单已支付或不可支付', 'Order is already paid or cannot be paid'))
    return
  }
  paying.value = true
  try {
    const res = await payOrder(order.value.orderNo, { payAmount: order.value.payAmount })
    if (res.code === 200) {
      ElMessage.success(t('orderList.paySuccess'))
      router.push('/orders')
    } else {
      ElMessage.error(res.message || t('orderList.payFail'))
    }
  } catch {
    ElMessage.error(t('orderList.payFail'))
  } finally {
    paying.value = false
  }
}

onMounted(loadOrder)
</script>

