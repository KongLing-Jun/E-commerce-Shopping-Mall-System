<template>
  <section class="space-y-6">
    <div class="flex flex-wrap items-start justify-between gap-4">
      <div>
        <h1 class="section-title">{{ t('admin.ordersTitle') }}</h1>
        <p class="muted-text mt-2">{{ t('admin.ordersSubtitle') }}</p>
      </div>
      <div class="flex gap-2">
        <el-button>Print</el-button>
        <el-button type="primary" :loading="exporting" v-permission="'admin:orders:export'" @click="exportOrders">
          Export to CSV
        </el-button>
      </div>
    </div>

    <div class="grid gap-4 sm:grid-cols-2 xl:grid-cols-4">
      <article class="rounded-2xl border border-[var(--line)] bg-[var(--surface)] p-5">
        <p class="text-sm text-[var(--muted)]">Total Orders</p>
        <p class="mt-3 text-4xl font-extrabold">{{ totalOrders }}</p>
        <p class="mt-2 text-sm text-[var(--muted)]">Today +{{ todayOrders }}</p>
      </article>
      <article class="rounded-2xl border border-[var(--line)] bg-[var(--surface)] p-5">
        <p class="text-sm text-[var(--muted)]">Pending</p>
        <p class="mt-3 text-4xl font-extrabold">{{ pendingOrders }}</p>
      </article>
      <article class="rounded-2xl border border-[var(--line)] bg-[var(--surface)] p-5">
        <p class="text-sm text-[var(--muted)]">Shipped</p>
        <p class="mt-3 text-4xl font-extrabold">{{ shippedOrders }}</p>
      </article>
      <article class="rounded-2xl border border-[var(--line)] bg-[var(--surface)] p-5">
        <p class="text-sm text-[var(--muted)]">Revenue</p>
        <p class="mt-3 text-4xl font-extrabold">$ {{ formatPrice(totalSales) }}</p>
      </article>
    </div>

    <article class="rounded-2xl border border-[var(--line)] bg-[var(--surface)] p-5">
      <div class="mb-3 flex items-center justify-between">
        <h3 class="text-lg font-bold">Merchant Notices</h3>
        <el-button text @click="fetchNotices">Refresh</el-button>
      </div>
      <el-empty v-if="!notices.length" description="No notices" :image-size="48" />
      <div v-else class="space-y-3">
        <div
          v-for="notice in notices"
          :key="notice.id"
          class="flex items-start justify-between gap-3 rounded-xl border border-[var(--line)] px-3 py-2"
        >
          <div class="space-y-1">
            <div class="text-sm font-semibold">
              {{ notice.orderNo }}
              <el-tag v-if="notice.status === 0" size="small" type="warning">Unread</el-tag>
            </div>
            <p class="text-xs text-[var(--muted)]">{{ notice.content }}</p>
          </div>
          <el-button v-if="notice.status === 0" size="small" @click="markNoticeRead(notice)">Read</el-button>
        </div>
      </div>
    </article>

    <article class="overflow-hidden rounded-2xl border border-[var(--line)] bg-[var(--surface)]">
      <div class="flex flex-wrap items-center gap-3 border-b border-[var(--line)] px-4 py-4">
        <el-input v-model="searchKeyword" placeholder="Search by Order ID or User" class="max-w-md" clearable @keyup.enter="applySearch" />
        <el-button type="primary" @click="applySearch">{{ t('common.search') }}</el-button>
        <el-button @click="resetFilters">{{ t('common.reset') }}</el-button>
      </div>

      <div class="flex flex-wrap gap-2 border-b border-[var(--line)] px-4 py-3">
        <button
          v-for="option in statusOptions"
          :key="String(option.value)"
          type="button"
          class="rounded-xl border px-4 py-2 text-sm font-semibold"
          :class="filters.status === option.value ? 'border-[var(--accent)] bg-[var(--highlight)] text-[var(--accent)]' : 'border-[var(--line)] text-[var(--muted)]'"
          @click="changeStatus(option.value)"
        >
          {{ option.label }}
        </button>
      </div>

      <el-table :data="orders" v-loading="loading" stripe>
        <el-table-column type="expand">
          <template #default="{ row }">
            <div class="rounded-xl bg-[var(--surface-soft)] p-4">
              <div class="mb-2 text-sm font-semibold">{{ t('orderConfirm.items') }}</div>
              <el-table :data="row.items || []" size="small">
                <el-table-column prop="productId" label="Product ID" width="120" />
                <el-table-column prop="productName" :label="t('nav.products')" min-width="200" />
                <el-table-column prop="price" :label="t('common.price')" width="120" />
                <el-table-column prop="quantity" :label="t('common.qty')" width="90" />
              </el-table>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="orderNo" label="Order ID" min-width="170" />
        <el-table-column :label="t('auth.username')" min-width="180">
          <template #default="{ row }">
            <div>
              <div class="font-semibold">{{ row.username || 'N/A' }}</div>
              <div class="text-xs text-[var(--muted)]">UID: {{ row.userId }}</div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="Date" min-width="150" />
        <el-table-column :label="t('common.amount')" min-width="120">
          <template #default="{ row }">$ {{ formatPrice(row.payAmount || row.totalAmount) }}</template>
        </el-table-column>
        <el-table-column :label="t('common.status')" width="130">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)" effect="light">{{ statusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column :label="t('common.actions')" width="200">
          <template #default="{ row }">
            <el-button
              size="small"
              type="success"
              :disabled="row.status !== 1"
              v-permission="'admin:orders:ship'"
              @click="openShip(row)"
            >
              {{ t('admin.ship') }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="flex justify-end border-t border-[var(--line)] px-4 py-4">
        <el-pagination
          background
          layout="prev, pager, next, sizes"
          :total="total"
          :page-size="size"
          :current-page="page + 1"
          @current-change="changePage"
          @size-change="changeSize"
        />
      </div>
    </article>

    <el-dialog v-model="shipDialogVisible" :title="t('admin.ship')" width="460px">
      <el-form :model="shipForm" label-position="top">
        <el-form-item label="Order No">
          <el-input v-model="shipForm.orderNo" disabled />
        </el-form-item>
        <el-form-item label="Express No (optional)">
          <el-input v-model="shipForm.expressNo" placeholder="SF123456789CN" />
        </el-form-item>
        <el-form-item label="Express Company (optional)">
          <el-input v-model="shipForm.expressCompany" placeholder="SF Express" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="shipDialogVisible = false">{{ t('common.cancel') }}</el-button>
        <el-button type="primary" :loading="shipping" @click="submitShip">{{ t('admin.ship') }}</el-button>
      </template>
    </el-dialog>
  </section>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { exportAdminOrders, fetchAdminOrders, shipAdminOrder } from '@/api/admin/orders.js'
import { fetchAdminStatsOverview } from '@/api/admin/stats.js'
import { fetchAdminNotices, readAdminNotice } from '@/api/admin/notices.js'
import { useI18n } from '@/i18n/index.js'

const loading = ref(false)
const exporting = ref(false)
const shipping = ref(false)
const orders = ref([])
const notices = ref([])
const total = ref(0)
const page = ref(0)
const size = ref(10)
const searchKeyword = ref('')
const totalSales = ref(0)
const todayOrders = ref(0)
const { t } = useI18n()

const filters = reactive({
  orderNo: '',
  userId: '',
  status: null,
})
const shipDialogVisible = ref(false)
const shipForm = reactive({
  orderNo: '',
  expressNo: '',
  expressCompany: '',
})

// 后台订单状态筛选选项。
const statusOptions = computed(() => [
  { label: 'All Orders', value: null },
  { label: t('orderList.paid'), value: 1 },
  { label: t('orderList.pending'), value: 0 },
  { label: t('orderList.shipped'), value: 2 },
  { label: t('orderList.completed'), value: 3 },
])

// 统计卡片数据派生值。
const totalOrders = computed(() => total.value)
const pendingOrders = computed(() => orders.value.filter((item) => item.status === 0).length)
const shippedOrders = computed(() => orders.value.filter((item) => item.status === 2).length)

// 统一金额显示格式。
const formatPrice = (value) => Number(value || 0).toFixed(2)

// 拉取后台订单分页列表，支持订单号/用户ID/状态筛选。
const fetchOrders = async () => {
  loading.value = true
  try {
    const params = {
      orderNo: filters.orderNo || undefined,
      userId: filters.userId ? Number(filters.userId) : undefined,
      status: filters.status === null ? undefined : filters.status,
      page: page.value,
      size: size.value,
    }
    const res = await fetchAdminOrders(params)
    if (res.code === 200) {
      orders.value = res.data.content || []
      total.value = res.data.totalElements || 0
    } else {
      ElMessage.error(res.message || t('common.empty'))
    }
  } catch {
    ElMessage.error(t('common.empty'))
  } finally {
    loading.value = false
  }
}

// 拉取看板总览数据（销售额、今日订单等）。
const fetchOverview = async () => {
  try {
    const res = await fetchAdminStatsOverview()
    if (res.code === 200) {
      totalSales.value = Number(res.data.totalSales || 0)
      todayOrders.value = Number(res.data.todayOrders || 0)
    }
  } catch {
    // ignore
  }
}

// 拉取“支付成功后发送给商家”的站内通知列表。
const fetchNotices = async () => {
  try {
    const res = await fetchAdminNotices({ page: 0, size: 5 })
    if (res.code === 200) {
      notices.value = res.data.content || []
    }
  } catch {
    notices.value = []
  }
}

const markNoticeRead = async (notice) => {
  try {
    const res = await readAdminNotice(notice.id)
    if (res.code === 200) {
      notice.status = 1
    }
  } catch {
    // ignore
  }
}

// 搜索框输入规则：纯数字按用户ID，否则按订单号。
const applySearch = () => {
  const keyword = searchKeyword.value.trim()
  if (/^\d+$/.test(keyword)) {
    filters.userId = keyword
    filters.orderNo = ''
  } else {
    filters.orderNo = keyword
    filters.userId = ''
  }
  page.value = 0
  fetchOrders()
}

// 重置筛选条件并回到第一页。
const resetFilters = () => {
  filters.orderNo = ''
  filters.userId = ''
  filters.status = null
  searchKeyword.value = ''
  page.value = 0
  fetchOrders()
}

// 状态筛选切换后刷新列表。
const changeStatus = (status) => {
  filters.status = status
  page.value = 0
  fetchOrders()
}

// 分页跳转。
const changePage = (nextPage) => {
  page.value = nextPage - 1
  fetchOrders()
}

// 每页条数变化后重置页码并刷新。
const changeSize = (nextSize) => {
  size.value = nextSize
  page.value = 0
  fetchOrders()
}

// 发货操作：仅待发货订单可执行。
const openShip = (row) => {
  shipForm.orderNo = row.orderNo
  shipForm.expressNo = row.expressNo || ''
  shipForm.expressCompany = row.expressCompany || ''
  shipDialogVisible.value = true
}

const submitShip = async () => {
  shipping.value = true
  try {
    await ElMessageBox.confirm(t('admin.shipConfirm'), 'Confirm', { type: 'warning' })
    const res = await shipAdminOrder(shipForm.orderNo, {
      expressNo: shipForm.expressNo || null,
      expressCompany: shipForm.expressCompany || null,
    })
    if (res.code === 200) {
      ElMessage.success(t('admin.ship'))
      shipDialogVisible.value = false
      fetchOrders()
    } else {
      ElMessage.error(res.message || t('common.empty'))
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(t('common.empty'))
    }
  } finally {
    shipping.value = false
  }
}

// 导出当前筛选条件下的订单数据（Excel）。
const exportOrders = async () => {
  exporting.value = true
  try {
    const params = {
      orderNo: filters.orderNo || undefined,
      userId: filters.userId ? Number(filters.userId) : undefined,
      status: filters.status === null ? undefined : filters.status,
    }
    const blob = await exportAdminOrders(params)
    const url = window.URL.createObjectURL(new Blob([blob]))
    const link = document.createElement('a')
    link.href = url
    link.download = `orders-${new Date().toISOString().slice(0, 10)}.xlsx`
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)
  } catch {
    ElMessage.error(t('common.empty'))
  } finally {
    exporting.value = false
  }
}

// 后端状态码映射为可读文本。
const statusLabel = (status) => {
  if (status === 0) return t('orderList.pending')
  if (status === 1) return t('orderList.paid')
  if (status === 2) return t('orderList.shipped')
  if (status === 3) return t('orderList.completed')
  return t('common.status')
}

// 后端状态码映射为标签颜色。
const statusType = (status) => {
  if (status === 0) return 'warning'
  if (status === 1) return 'success'
  if (status === 2) return 'primary'
  if (status === 3) return 'info'
  return 'default'
}

// 页面初始化时加载订单列表和统计概览。
onMounted(() => {
  fetchOrders()
  fetchOverview()
  fetchNotices()
})
</script>



