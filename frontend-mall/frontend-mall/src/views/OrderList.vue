<template>
  <div class="grid gap-6 lg:grid-cols-[270px_1fr]">
    <aside class="h-fit rounded-2xl border border-[var(--line)] bg-[var(--surface)] p-5">
      <div class="flex items-center gap-3 border-b border-[var(--line)] pb-5">
        <div class="grid h-14 w-14 place-content-center rounded-full bg-[var(--highlight)] text-2xl">U</div>
        <div>
          <div class="text-xl font-extrabold">{{ profile.username || dual('用户', 'User') }}</div>
          <div class="text-sm text-[var(--muted)]">{{ dual('金牌会员', 'Gold Member') }}</div>
        </div>
      </div>

      <div class="mt-5 space-y-2 text-base font-semibold">
        <router-link class="block rounded-xl px-4 py-3 hover:bg-[var(--surface-soft)]" to="/orders">{{ t('nav.orders') }}</router-link>
        <router-link class="block rounded-xl px-4 py-3 hover:bg-[var(--surface-soft)]" to="/profile">{{ t('nav.profile') }}</router-link>
        <router-link class="block rounded-xl px-4 py-3 hover:bg-[var(--surface-soft)]" to="/addresses">{{ t('nav.addresses') }}</router-link>
      </div>
    </aside>

    <section class="space-y-5">
      <header class="flex flex-wrap items-center justify-between gap-3">
        <div>
          <p class="text-sm text-[var(--muted)]">{{ dual('首页 / 账户', 'Home / Account') }} / {{ t('nav.orders') }}</p>
          <h1 class="mt-2 text-5xl font-extrabold">{{ dual('近期订单', 'Recent Orders') }}</h1>
        </div>
        <div class="flex gap-2">
          <el-button @click="loadOrders">{{ dual('筛选', 'Filter') }}</el-button>
          <el-button type="primary">{{ dual('追踪订单', 'Track Order') }}</el-button>
        </div>
      </header>

      <div class="flex flex-wrap gap-2">
        <button
          v-for="option in statusOptions"
          :key="String(option.value)"
          type="button"
          class="rounded-full border px-4 py-2 text-base font-semibold"
          :class="statusFilter === option.value ? 'border-slate-900 bg-slate-900 text-white' : 'border-[var(--line)] text-[var(--muted)]'"
          @click="changeStatus(option.value)"
        >
          {{ option.label }}
        </button>
      </div>

      <article
        v-for="order in orders"
        :key="order.orderNo"
        class="overflow-hidden rounded-2xl border border-[var(--line)] bg-[var(--surface)]"
      >
        <div class="flex flex-wrap items-center justify-between gap-3 border-b border-[var(--line)] bg-[var(--surface-soft)] px-5 py-4">
          <div class="grid gap-1 sm:grid-cols-3 sm:gap-8">
            <div>
              <div class="text-xs font-bold uppercase text-[var(--muted)]">{{ dual('下单时间', 'Order Placed') }}</div>
              <div class="text-lg font-semibold">{{ formatDate(order.createdAt) }}</div>
            </div>
            <div>
              <div class="text-xs font-bold uppercase text-[var(--muted)]">{{ dual('订单金额', 'Total') }}</div>
              <div class="text-lg font-semibold">$ {{ formatPrice(order.payAmount) }}</div>
            </div>
            <div>
              <div class="text-xs font-bold uppercase text-[var(--muted)]">{{ dual('订单编号', 'Order ID') }}</div>
              <div class="text-lg font-semibold">#{{ order.orderNo }}</div>
            </div>
          </div>
          <div class="flex gap-2">
            <el-button v-if="order.status === 3" @click="rebuy(order)">{{ dual('再次购买', 'Buy Again') }}</el-button>
            <el-button :disabled="order.status === 0" @click="viewInvoice(order)">{{ dual('查看发票', 'View Invoice') }}</el-button>
          </div>
        </div>

        <div class="px-5 py-5">
          <div class="flex flex-wrap gap-4">
            <img
              :src="order.items?.[0]?.image"
              :alt="order.items?.[0]?.productName || dual('商品', 'product')"
              class="h-24 w-24 rounded-xl object-cover"
            />
            <div class="min-w-0 flex-1">
              <h3 class="text-3xl font-extrabold">{{ order.items?.[0]?.productName || dual('订单商品', 'Order Items') }}</h3>
              <p class="text-[var(--muted)]">{{ order.items?.length || 0 }} {{ dual('件', 'item(s)') }}</p>
              <p class="mt-1 text-xl font-semibold" :class="statusClass(order.status)">{{ statusText(order.status) }}</p>
              <p v-if="order.expressNo" class="mt-1 text-sm text-[var(--muted)]">
                {{ dual('物流单号', 'Tracking No') }}: {{ order.expressNo }}
                <span v-if="order.expressCompany">({{ order.expressCompany }})</span>
              </p>

              <div class="mt-4 grid grid-cols-4 gap-2 text-center text-sm">
                <div :class="timelineClass(order.status, 0)">{{ dual('下单', 'Placed') }}</div>
                <div :class="timelineClass(order.status, 1)">{{ dual('已支付', 'Paid') }}</div>
                <div :class="timelineClass(order.status, 2)">{{ dual('已发货', 'Shipped') }}</div>
                <div :class="timelineClass(order.status, 3)">{{ dual('已送达', 'Delivered') }}</div>
              </div>
            </div>
            <div class="flex flex-col gap-2">
              <el-button v-if="order.status === 0" type="primary" @click="pay(order)">{{ t('orderList.pay') }}</el-button>
              <el-button v-if="order.status === 2" type="success" @click="confirm(order)">{{ t('orderList.confirm') }}</el-button>
            </div>
          </div>
        </div>
      </article>

      <el-empty v-if="!orders.length" :description="t('orderList.noOrders')" />

      <el-dialog v-model="invoiceVisible" :title="dual('订单发票', 'Order Invoice')" width="720px">
        <div v-if="invoiceLoading" class="py-6 text-center text-[var(--muted)]">{{ dual('加载中...', 'Loading...') }}</div>
        <div v-else-if="invoiceData">
          <div class="grid gap-3 sm:grid-cols-2">
            <div>
              <div class="text-xs font-bold uppercase text-[var(--muted)]">{{ dual('发票号', 'Invoice No') }}</div>
              <div class="text-lg font-semibold">{{ invoiceData.invoiceNo || '--' }}</div>
            </div>
            <div>
              <div class="text-xs font-bold uppercase text-[var(--muted)]">{{ dual('订单号', 'Order No') }}</div>
              <div class="text-lg font-semibold">#{{ invoiceData.orderNo }}</div>
            </div>
            <div>
              <div class="text-xs font-bold uppercase text-[var(--muted)]">{{ dual('下单时间', 'Created At') }}</div>
              <div class="text-lg font-semibold">{{ formatDate(invoiceData.createdAt) }}</div>
            </div>
            <div>
              <div class="text-xs font-bold uppercase text-[var(--muted)]">{{ dual('支付时间', 'Paid At') }}</div>
              <div class="text-lg font-semibold">{{ formatDate(invoiceData.paidAt) }}</div>
            </div>
          </div>

          <div class="mt-6 rounded-2xl border border-[var(--line)]">
            <div class="grid grid-cols-4 gap-2 border-b border-[var(--line)] bg-[var(--surface-soft)] px-4 py-3 text-xs font-bold uppercase text-[var(--muted)]">
              <div>{{ dual('商品', 'Item') }}</div>
              <div class="text-center">{{ dual('数量', 'Qty') }}</div>
              <div class="text-center">{{ dual('单价', 'Price') }}</div>
              <div class="text-right">{{ dual('小计', 'Subtotal') }}</div>
            </div>
            <div v-for="item in invoiceData.items || []" :key="item.productId" class="grid grid-cols-4 gap-2 border-b border-[var(--line)] px-4 py-3 last:border-b-0">
              <div class="truncate font-semibold">{{ item.productName }}</div>
              <div class="text-center">{{ item.quantity }}</div>
              <div class="text-center">$ {{ formatPrice(item.price) }}</div>
              <div class="text-right">$ {{ formatPrice(item.price * item.quantity) }}</div>
            </div>
          </div>

          <div class="mt-6 flex flex-wrap items-start justify-between gap-3 rounded-2xl border border-[var(--line)] bg-[var(--surface-soft)] px-4 py-4">
            <div class="text-sm text-[var(--muted)]">
              <div class="font-semibold text-[var(--text)]">{{ dual('收货地址', 'Shipping Address') }}</div>
              <p class="mt-1 max-w-md">{{ invoiceData.addressSnapshot || '--' }}</p>
            </div>
            <div class="text-right">
              <div class="text-xs font-bold uppercase text-[var(--muted)]">{{ dual('合计', 'Total') }}</div>
              <div class="text-2xl font-extrabold">$ {{ formatPrice(invoiceData.payAmount) }}</div>
            </div>
          </div>
        </div>
        <template #footer>
          <el-button @click="invoiceVisible = false">{{ dual('关闭', 'Close') }}</el-button>
        </template>
      </el-dialog>

      <div class="flex justify-center">
        <el-pagination
          v-if="total > 0"
          background
          layout="prev, pager, next"
          :page-size="size"
          :total="total"
          :current-page="page"
          @current-change="handlePageChange"
        />
      </div>
    </section>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRouter } from 'vue-router'
import { confirmOrder, getOrderInvoice, getOrders, rebuyOrder } from '@/api/order.js'
import { fetchUserProfile } from '@/api/user.js'
import { useI18n } from '@/i18n/index.js'

const router = useRouter()
const orders = ref([])
const profile = ref({})
const page = ref(1)
const size = ref(6)
const total = ref(0)
const statusFilter = ref(null)
const invoiceVisible = ref(false)
const invoiceLoading = ref(false)
const invoiceData = ref(null)
const { t, locale } = useI18n()
const dual = (zh, en) => (locale.value === 'zh' ? zh : en)

// 订单状态筛选项，和后端状态码保持一致。
const statusOptions = computed(() => [
  { label: t('orderList.statusAll'), value: null },
  { label: t('orderList.pending'), value: 0 },
  { label: t('orderList.paid'), value: 1 },
  { label: t('orderList.shipped'), value: 2 },
  { label: t('orderList.completed'), value: 3 },
])

// 金额与日期展示格式化工具。
const formatPrice = (value) => Number(value || 0).toFixed(2)
const formatDate = (value) => {
  if (!value) {
    return '--'
  }
  const date = new Date(value)
  return date.toLocaleDateString()
}

// 按分页与状态筛选加载我的订单列表。
const loadOrders = async () => {
  try {
    const params = {
      page: page.value - 1,
      size: size.value,
      status: statusFilter.value === null ? undefined : statusFilter.value,
    }
    const res = await getOrders(params)
    if (res.code === 200) {
      orders.value = res.data.content || []
      total.value = res.data.totalElements || 0
    } else {
      ElMessage.error(res.message)
    }
  } catch {
    ElMessage.error(t('orderList.loadFail'))
  }
}

// 读取个人信息，展示在侧边栏区域。
const loadProfile = async () => {
  try {
    const res = await fetchUserProfile()
    if (res.code === 200) {
      profile.value = res.data || {}
    }
  } catch {
    // ignore
  }
}

// 分页切换后重新查询订单。
const handlePageChange = (value) => {
  page.value = value
  loadOrders()
}

// 切换状态标签时重置页码并刷新列表。
const changeStatus = (status) => {
  statusFilter.value = status
  page.value = 1
  loadOrders()
}

// Redirect to payment page
const pay = async (order) => {
  if (!order?.orderNo) {
    return
  }
  router.push(`/orders/pay/${order.orderNo}`)
}

const confirm = async (order) => {
  try {
    await ElMessageBox.confirm(t('orderList.confirmConfirm'), 'Confirm', { type: 'warning' })
    const res = await confirmOrder(order.orderNo)
    if (res.code === 200) {
      ElMessage.success(t('orderList.confirmSuccess'))
      loadOrders()
    } else {
      ElMessage.error(res.message)
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(t('orderList.confirmFail'))
    }
  }
}

// 打开发票弹窗并加载发票数据（仅已支付订单）。
const viewInvoice = async (order) => {
  invoiceVisible.value = true
  invoiceLoading.value = true
  invoiceData.value = null
  try {
    const res = await getOrderInvoice(order.orderNo)
    if (res.code === 200) {
      invoiceData.value = res.data
    } else {
      ElMessage.error(res.message)
      invoiceVisible.value = false
    }
  } catch {
    ElMessage.error(dual('发票获取失败', 'Failed to load invoice'))
    invoiceVisible.value = false
  } finally {
    invoiceLoading.value = false
  }
}

// 再次购买：将历史订单商品重新加入购物车。
const rebuy = async (order) => {
  try {
    const res = await rebuyOrder(order.orderNo)
    if (res.code === 200) {
      const added = res.data?.addedItems?.length || 0
      const skipped = res.data?.skippedItems?.length || 0
      if (skipped > 0) {
        ElMessage.warning(
          dual(`已加入 ${added} 件，${skipped} 件因库存不足跳过`, `Added ${added} item(s), ${skipped} skipped due to stock`)
        )
      } else {
        ElMessage.success(dual('已加入购物车', 'Added to cart'))
      }
      window.location.href = '/cart'
    } else {
      ElMessage.error(res.message)
    }
  } catch {
    ElMessage.error(dual('再次购买失败', 'Rebuy failed'))
  }
}

// 订单状态文本映射。
const statusText = (status) => {
  if (status === 0) return t('orderList.pending')
  if (status === 1) return t('orderList.paid')
  if (status === 2) return t('orderList.shipped')
  return t('orderList.completed')
}

// 订单状态颜色映射。
const statusClass = (status) => {
  if (status === 0) return 'text-amber-500'
  if (status === 1) return 'text-blue-500'
  if (status === 2) return 'text-violet-500'
  return 'text-green-600'
}

// 物流进度条样式：当前状态之前节点为激活态。
const timelineClass = (status, step) => {
  const active = status >= step
  return active
    ? 'rounded-lg bg-[var(--highlight)] px-2 py-2 font-semibold text-[var(--accent)]'
    : 'rounded-lg bg-[var(--surface-soft)] px-2 py-2 text-[var(--muted)]'
}

// 页面初始化时并行加载订单与用户信息。
onMounted(() => {
  loadOrders()
  loadProfile()
})
</script>



