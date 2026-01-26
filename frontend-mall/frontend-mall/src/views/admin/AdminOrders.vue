<template>
  <section class="space-y-6">
    <div>
      <p class="text-xs uppercase tracking-[0.35em] text-[var(--muted)]">Admin Console</p>
      <h1 class="section-title mt-3">Order Command</h1>
      <p class="muted-text mt-2">Track order status and ship paid orders.</p>
    </div>

    <el-card class="border-0 bg-white/80 shadow-soft">
      <div class="flex flex-wrap items-end gap-3">
        <el-input v-model="filters.orderNo" placeholder="Order No" clearable class="w-56" />
        <el-input v-model="filters.userId" placeholder="User ID" clearable class="w-40" />
        <el-select v-model="filters.status" placeholder="Status" clearable class="w-36">
          <el-option label="Pending" :value="0" />
          <el-option label="Paid" :value="1" />
          <el-option label="Shipped" :value="2" />
          <el-option label="Finished" :value="3" />
        </el-select>
        <el-button type="primary" @click="fetchOrders">Search</el-button>
        <el-button @click="resetFilters">Reset</el-button>
      </div>

      <el-table :data="orders" class="mt-6" v-loading="loading" stripe>
        <el-table-column type="expand">
          <template #default="{ row }">
            <div class="rounded-2xl border border-[var(--line)] bg-white/60 p-4">
              <div class="mb-3 text-sm font-semibold text-[var(--ink)]">Items</div>
              <el-table :data="row.items" size="small">
                <el-table-column prop="productId" label="Product ID" width="120" />
                <el-table-column prop="productName" label="Name" min-width="200" />
                <el-table-column prop="price" label="Price" width="120" />
                <el-table-column prop="quantity" label="Qty" width="90" />
              </el-table>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="orderNo" label="Order No" min-width="200" />
        <el-table-column prop="userId" label="User ID" width="100" />
        <el-table-column prop="username" label="User" min-width="120" />
        <el-table-column prop="totalAmount" label="Total" width="120" />
        <el-table-column prop="payAmount" label="Pay" width="120" />
        <el-table-column label="Status" width="130">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)">
              {{ statusLabel(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="Created" min-width="170" />
        <el-table-column label="Actions" width="140">
          <template #default="{ row }">
            <el-button
              size="small"
              type="success"
              :disabled="row.status !== 1"
              @click="ship(row)"
            >
              Ship
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
import { fetchAdminOrders, shipAdminOrder } from '@/api/admin/orders.js'

const loading = ref(false)
const orders = ref([])
const total = ref(0)
const page = ref(0)
const size = ref(10)

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
      ElMessage.error(res.message || 'Failed to load orders')
    }
  } catch (error) {
    ElMessage.error('Failed to load orders')
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
    await ElMessageBox.confirm('Ship this order now?', 'Confirm', { type: 'warning' })
    const res = await shipAdminOrder(row.orderNo)
    if (res.code === 200) {
      ElMessage.success('Order shipped')
      fetchOrders()
    } else {
      ElMessage.error(res.message || 'Ship failed')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('Ship failed')
    }
  }
}

const statusLabel = (status) => {
  if (status === 0) return 'Pending'
  if (status === 1) return 'Paid'
  if (status === 2) return 'Shipped'
  if (status === 3) return 'Finished'
  return 'Unknown'
}

const statusType = (status) => {
  if (status === 1) return 'success'
  if (status === 2) return 'warning'
  if (status === 3) return 'info'
  return 'default'
}

onMounted(fetchOrders)
</script>
