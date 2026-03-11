<template>
  <div class="space-y-6">
    <header class="flex flex-wrap items-end justify-between gap-3">
      <div>
        <p class="text-sm text-[var(--muted)]">{{ t('nav.orders') }} / {{ dual('订单追踪', 'Order Tracking') }}</p>
        <h1 class="mt-2 text-4xl font-extrabold">{{ dual('订单状态追踪', 'Track Your Order') }}</h1>
        <p class="muted-text mt-2">{{ dual('输入订单号查看最新状态与物流信息。', 'Enter order number to view status and shipment info.') }}</p>
      </div>
      <el-button @click="router.push('/orders')">{{ dual('返回订单列表', 'Back to Orders') }}</el-button>
    </header>

    <section class="rounded-2xl border border-[var(--line)] bg-[var(--surface)] p-6">
      <div class="grid gap-3 md:grid-cols-[1fr_auto]">
        <el-input v-model="orderNoInput" :placeholder="dual('请输入订单号', 'Order number')" @keyup.enter="searchOrder" />
        <el-button type="primary" :loading="loading" @click="searchOrder">{{ t('common.search') }}</el-button>
      </div>
    </section>

    <section class="grid gap-6 lg:grid-cols-[1fr_320px]" v-if="order">
      <article class="rounded-2xl border border-[var(--line)] bg-[var(--surface)] p-6">
        <div class="flex items-center justify-between">
          <h2 class="text-2xl font-extrabold">{{ dual('订单信息', 'Order Summary') }}</h2>
          <el-tag :type="order.status === 0 ? 'warning' : order.status === 3 ? 'success' : 'info'">
            {{ statusText(order.status) }}
          </el-tag>
        </div>
        <div class="mt-4 grid gap-3 sm:grid-cols-2">
          <div>
            <div class="text-xs font-bold uppercase text-[var(--muted)]">{{ dual('订单号', 'Order No') }}</div>
            <div class="text-lg font-semibold">#{{ order.orderNo }}</div>
          </div>
          <div>
            <div class="text-xs font-bold uppercase text-[var(--muted)]">{{ dual('下单时间', 'Created At') }}</div>
            <div class="text-lg font-semibold">{{ formatDateTime(order.createdAt) }}</div>
          </div>
          <div>
            <div class="text-xs font-bold uppercase text-[var(--muted)]">{{ dual('支付金额', 'Paid Amount') }}</div>
            <div class="text-lg font-semibold">$ {{ formatPrice(order.payAmount) }}</div>
          </div>
          <div>
            <div class="text-xs font-bold uppercase text-[var(--muted)]">{{ dual('物流信息', 'Shipment') }}</div>
            <div class="text-lg font-semibold">
              <span v-if="order.expressNo">{{ order.expressCompany || dual('快递', 'Carrier') }} #{{ order.expressNo }}</span>
              <span v-else class="text-[var(--muted)]">{{ dual('暂无物流', 'Not shipped yet') }}</span>
            </div>
          </div>
        </div>

        <div class="mt-6 rounded-2xl border border-[var(--line)] p-4">
          <h3 class="text-lg font-bold">{{ dual('状态进度', 'Status Timeline') }}</h3>
          <div class="mt-4 grid gap-3">
            <div
              v-for="step in timeline"
              :key="step.key"
              class="flex items-center justify-between rounded-xl border px-4 py-3"
              :class="step.active ? 'border-[var(--accent)] bg-[var(--highlight)]' : 'border-[var(--line)]'"
            >
              <div class="font-semibold">{{ step.label }}</div>
              <div class="text-sm text-[var(--muted)]">{{ step.time || '--' }}</div>
            </div>
          </div>
        </div>

        <div class="mt-6 rounded-2xl border border-[var(--line)] p-4">
          <h3 class="text-lg font-bold">{{ dual('物流轨迹明细', 'Shipment History') }}</h3>
          <div v-if="trackingEvents.length" class="mt-4 space-y-3">
            <div
              v-for="(event, index) in trackingEvents"
              :key="`${event.title}-${event.eventTime}-${index}`"
              class="rounded-xl border border-[var(--line)] px-4 py-3"
            >
              <div class="flex items-center justify-between">
                <div class="font-semibold">{{ event.title }}</div>
                <div class="text-sm text-[var(--muted)]">{{ formatDateTime(event.eventTime) || '--' }}</div>
              </div>
              <div class="mt-2 text-sm text-[var(--muted)]">
                <span v-if="event.location">{{ event.location }}</span>
                <span v-if="event.location && event.description"> · </span>
                <span>{{ event.description }}</span>
              </div>
            </div>
          </div>
          <el-empty v-else :description="dual('暂无物流轨迹', 'No tracking events yet')" class="mt-4" />
        </div>
      </article>

      <aside class="rounded-2xl border border-[var(--line)] bg-[var(--surface)] p-6">
        <h3 class="text-xl font-extrabold">{{ dual('商品清单', 'Items') }}</h3>
        <div class="mt-4 space-y-3">
          <div v-for="item in order.items || []" :key="item.productId" class="flex items-center gap-3">
            <img :src="item.image" :alt="item.productName" class="h-14 w-14 rounded-lg object-cover" />
            <div class="min-w-0 flex-1">
              <div class="truncate font-semibold">{{ item.productName }}</div>
              <div class="text-sm text-[var(--muted)]">{{ dual('数量', 'Qty') }}: {{ item.quantity }}</div>
            </div>
            <div class="text-sm font-semibold">$ {{ formatPrice(item.price * item.quantity) }}</div>
          </div>
          <el-empty v-if="!(order.items || []).length" :description="t('orderConfirm.noItems')" />
        </div>
      </aside>
    </section>

    <el-empty v-else-if="!loading" :description="emptyText" />
    <el-skeleton v-else :rows="6" animated />
  </div>
</template>

<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getOrderDetail, getOrderTracking } from '@/api/order.js'
import { useI18n } from '@/i18n/index.js'

const route = useRoute()
const router = useRouter()
const { t, locale } = useI18n()
// 功能：处理中英文切换展示。
const dual = (zh, en) => (locale.value === 'zh' ? zh : en)

const orderNoInput = ref('')
const order = ref(null)
const trackingEvents = ref([])
const loading = ref(false)
const emptyText = ref(dual('请输入订单号进行追踪', 'Please enter an order number'))

// 功能：格式化金额显示。
const formatPrice = (value) => Number(value || 0).toFixed(2)

// 功能：格式化时间显示。
const formatDateTime = (value) => {
  if (!value) return ''
  const date = new Date(value)
  return date.toLocaleString()
}

// 功能：订单状态文案映射。
const statusText = (status) => {
  if (status === 0) return t('orderList.pending')
  if (status === 1) return t('orderList.paid')
  if (status === 2) return t('orderList.shipped')
  if (status === 3) return t('orderList.completed')
  return t('common.status')
}

// 功能：生成订单状态时间轴数据。
const timeline = computed(() => {
  if (!order.value) return []
  return [
    { key: 'created', label: dual('下单成功', 'Order placed'), time: formatDateTime(order.value.createdAt), active: true },
    { key: 'paid', label: dual('支付完成', 'Payment received'), time: formatDateTime(order.value.paidAt), active: order.value.status >= 1 },
    { key: 'shipped', label: dual('已发货', 'Shipped'), time: formatDateTime(order.value.shippedAt), active: order.value.status >= 2 },
    { key: 'finished', label: dual('已签收', 'Delivered'), time: formatDateTime(order.value.finishedAt), active: order.value.status >= 3 },
  ]
})

// 功能：从路由参数同步订单号。
const syncFromRoute = () => {
  const { orderNo } = route.query
  if (orderNo) {
    orderNoInput.value = String(orderNo)
  }
}

// 功能：根据订单号拉取追踪详情。
const fetchOrder = async (orderNo) => {
  loading.value = true
  emptyText.value = ''
  try {
    const res = await getOrderDetail(orderNo)
    if (res.code === 200) {
      order.value = res.data
      await fetchTracking(orderNo)
      return
    }
    order.value = null
    trackingEvents.value = []
    emptyText.value = res.message || t('common.empty')
  } catch {
    order.value = null
    trackingEvents.value = []
    emptyText.value = dual('订单追踪失败', 'Failed to track order')
  } finally {
    loading.value = false
  }
}

// 功能：获取物流轨迹事件。
const fetchTracking = async (orderNo) => {
  try {
    const res = await getOrderTracking(orderNo)
    if (res.code === 200) {
      trackingEvents.value = res.data || []
      return
    }
  } catch {
    // ignore
  }
  trackingEvents.value = []
}

// 功能：触发查询并同步 URL。
const searchOrder = async () => {
  const orderNo = orderNoInput.value.trim()
  if (!orderNo) {
    ElMessage.warning(dual('请输入订单号', 'Please enter order number'))
    return
  }
  router.replace({ path: '/orders/track', query: { orderNo } })
  await fetchOrder(orderNo)
}

watch(
  () => route.query.orderNo,
  () => {
    syncFromRoute()
    if (orderNoInput.value) {
      fetchOrder(orderNoInput.value)
    }
  },
)

onMounted(() => {
  syncFromRoute()
  if (orderNoInput.value) {
    fetchOrder(orderNoInput.value)
  }
})
</script>
