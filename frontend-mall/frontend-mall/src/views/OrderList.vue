<template>
  <div class="space-y-6 animate-fade-up">
    <el-card class="border-0 bg-white/85 shadow-soft">
      <template #header>
        <div class="flex flex-col gap-4 lg:flex-row lg:items-center lg:justify-between">
          <div>
            <h2 class="text-2xl font-semibold">My orders</h2>
            <p class="muted-text">Track status and actions.</p>
          </div>
          <el-select v-model="statusFilter" placeholder="Status" class="w-44" @change="loadOrders">
            <el-option label="All" :value="null" />
            <el-option label="Pending payment" :value="0" />
            <el-option label="Paid" :value="1" />
            <el-option label="Shipped" :value="2" />
            <el-option label="Completed" :value="3" />
          </el-select>
        </div>
      </template>

      <el-table v-if="orders.length" :data="orders" style="width: 100%">
        <el-table-column label="Order No" prop="orderNo" width="220" />
        <el-table-column label="Amount" width="140">
          <template #default="{ row }">
            $ {{ formatPrice(row.payAmount) }}
          </template>
        </el-table-column>
        <el-table-column label="Status" width="160">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)" effect="light">{{ statusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="Created">
          <template #default="{ row }">
            {{ row.createdAt }}
          </template>
        </el-table-column>
        <el-table-column label="Actions" width="240">
          <template #default="{ row }">
            <el-button v-if="row.status === 0" size="small" type="primary" @click="pay(row)">
              Pay
            </el-button>
            <el-button v-if="row.status === 2" size="small" type="success" @click="confirm(row)">
              Confirm receipt
            </el-button>
          </template>
        </el-table-column>
        <el-table-column type="expand">
          <template #default="{ row }">
            <div class="space-y-2">
              <div
                v-for="item in row.items"
                :key="item.productId"
                class="flex items-center gap-4 rounded-2xl bg-white/70 p-3"
              >
                <el-image :src="item.image" class="h-16 w-16 rounded-xl" fit="cover" />
                <div class="flex-1">
                  <div class="font-medium">{{ item.productName }}</div>
                  <div class="text-sm text-[var(--muted)]">
                    $ {{ formatPrice(item.price) }} x {{ item.quantity }}
                  </div>
                </div>
              </div>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <el-empty v-else description="No orders" />
    </el-card>

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
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { confirmOrder, getOrders, payOrder } from '@/api/order.js'

const orders = ref([])
const page = ref(1)
const size = ref(8)
const total = ref(0)
const statusFilter = ref(null)

const formatPrice = (value) => {
  const numberValue = Number(value || 0)
  return numberValue.toFixed(2)
}

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
  } catch (error) {
    ElMessage.error('Failed to load orders')
  }
}

const handlePageChange = (value) => {
  page.value = value
  loadOrders()
}

const pay = async (row) => {
  try {
    const res = await payOrder(row.orderNo, { payAmount: row.payAmount })
    if (res.code === 200) {
      ElMessage.success('Payment successful')
      loadOrders()
    } else {
      ElMessage.error(res.message)
    }
  } catch (error) {
    ElMessage.error('Payment failed')
  }
}

const confirm = async (row) => {
  try {
    const res = await confirmOrder(row.orderNo)
    if (res.code === 200) {
      ElMessage.success('Order completed')
      loadOrders()
    } else {
      ElMessage.error(res.message)
    }
  } catch (error) {
    ElMessage.error('Confirmation failed')
  }
}

const statusText = (status) => {
  if (status === 0) return 'Pending payment'
  if (status === 1) return 'Paid'
  if (status === 2) return 'Shipped'
  return 'Completed'
}

const statusType = (status) => {
  if (status === 0) return 'warning'
  if (status === 1) return 'info'
  if (status === 2) return 'primary'
  return 'success'
}

onMounted(loadOrders)
</script>
