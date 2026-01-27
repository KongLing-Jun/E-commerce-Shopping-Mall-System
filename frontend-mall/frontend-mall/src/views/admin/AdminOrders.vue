<template>
  <section class="space-y-6">
    <div>
      <p class="text-xs uppercase tracking-[0.35em] text-[var(--muted)]">Admin Console</p>
      <h1 class="section-title mt-3">{{ t('admin.ordersTitle') }}</h1>
      <p class="muted-text mt-2">{{ t('admin.ordersSubtitle') }}</p>
    </div>

    <el-card class="border-0 bg-[var(--surface)] shadow-soft">
      <div class="flex flex-wrap items-end gap-3">
        <el-input v-model="filters.orderNo" placeholder="Order No" clearable class="w-56" />
        <el-input v-model="filters.userId" placeholder="User ID" clearable class="w-40" />
        <el-select v-model="filters.status" :placeholder="t('common.status')" clearable class="w-36">
          <el-option :label="t('orderList.pending')" :value="0" />
          <el-option :label="t('orderList.paid')" :value="1" />
          <el-option :label="t('orderList.shipped')" :value="2" />
          <el-option :label="t('orderList.completed')" :value="3" />
        </el-select>
        <el-button type="primary" @click="fetchOrders">{{ t('common.search') }}</el-button>
        <el-button @click="resetFilters">{{ t('common.reset') }}</el-button>
        <el-button type="success" :loading="exporting" v-permission="'admin:orders:export'" @click="exportOrders">
          {{ t('admin.exportOrders') }}
        </el-button>
      </div>

      <el-table :data="orders" class="mt-6" v-loading="loading" stripe>
        <el-table-column type="expand">
          <template #default="{ row }">
            <div class="rounded-2xl border border-[var(--line)] bg-[var(--surface)] p-4">
              <div class="mb-3 text-sm font-semibold text-[var(--ink)]">{{ t('orderConfirm.items') }}</div>
              <el-table :data="row.items" size="small">
                <el-table-column prop="productId" label="Product ID" width="120" />
                <el-table-column prop="productName" :label="t('nav.products')" min-width="200" />
                <el-table-column prop="price" :label="t('common.price')" width="120" />
                <el-table-column prop="quantity" :label="t('common.qty')" width="90" />
              </el-table>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="orderNo" label="Order No" min-width="200" />
        <el-table-column prop="userId" label="User ID" width="100" />
        <el-table-column prop="username" label="User" min-width="120" />
        <el-table-column prop="totalAmount" :label="t('common.amount')" width="120" />
        <el-table-column prop="payAmount" label="Pay" width="120" />
        <el-table-column :label="t('common.status')" width="130">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)">
              {{ statusLabel(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" :label="t('common.createdAt')" min-width="170" />
        <el-table-column :label="t('common.actions')" width="140">
          <template #default="{ row }">
            <el-button
              size="small"
              type="success"
              :disabled="row.status !== 1"
              v-permission="'admin:orders:ship'"
              @click="ship(row)"
            >
              {{ t('admin.ship') }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="mt-6 flex justify-end">
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
    </el-card>
  </section>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { exportAdminOrders, fetchAdminOrders, shipAdminOrder } from '@/api/admin/orders.js'
import { useI18n } from '@/i18n/index.js'

const loading = ref(false)
const exporting = ref(false)
const orders = ref([])
const total = ref(0)
const page = ref(0)
const size = ref(10)
const { t } = useI18n()

const filters = reactive({
  orderNo: '',
  userId: '',
  status: '',
})

const fetchOrders = async () => {
  loading.value = true
  try {
    const params = {
      orderNo: filters.orderNo || undefined,
      userId: filters.userId ? Number(filters.userId) : undefined,
      status: filters.status === '' ? undefined : filters.status,
      page: page.value,
      size: size.value,
    }
    const res = await fetchAdminOrders(params)
    if (res.code === 200) {
      orders.value = res.data.content
      total.value = res.data.totalElements
    } else {
      ElMessage.error(res.message || t('common.empty'))
    }
  } catch (error) {
    ElMessage.error(t('common.empty'))
  } finally {
    loading.value = false
  }
}

const resetFilters = () => {
  filters.orderNo = ''
  filters.userId = ''
  filters.status = ''
  page.value = 0
  fetchOrders()
}

const changePage = (nextPage) => {
  page.value = nextPage - 1
  fetchOrders()
}

const changeSize = (nextSize) => {
  size.value = nextSize
  page.value = 0
  fetchOrders()
}

const ship = async (row) => {
  try {
    await ElMessageBox.confirm(t('admin.shipConfirm'), 'Confirm', { type: 'warning' })
    const res = await shipAdminOrder(row.orderNo)
    if (res.code === 200) {
      ElMessage.success(t('admin.ship'))
      fetchOrders()
    } else {
      ElMessage.error(res.message || t('common.empty'))
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(t('common.empty'))
    }
  }
}

const exportOrders = async () => {
  exporting.value = true
  try {
    const params = {
      orderNo: filters.orderNo || undefined,
      userId: filters.userId ? Number(filters.userId) : undefined,
      status: filters.status === '' ? undefined : filters.status,
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
  } catch (error) {
    ElMessage.error(t('common.empty'))
  } finally {
    exporting.value = false
  }
}

const statusLabel = (status) => {
  if (status === 0) return t('orderList.pending')
  if (status === 1) return t('orderList.paid')
  if (status === 2) return t('orderList.shipped')
  if (status === 3) return t('orderList.completed')
  return t('common.status')
}

const statusType = (status) => {
  if (status === 1) return 'success'
  if (status === 2) return 'warning'
  if (status === 3) return 'info'
  return 'default'
}

onMounted(fetchOrders)
</script>

