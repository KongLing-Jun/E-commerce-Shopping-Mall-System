<template>
  <div class="space-y-6 animate-fade-up">
    <el-card class="border-0 bg-[var(--surface-strong)] shadow-soft">
      <template #header>
        <div class="flex flex-col gap-4 lg:flex-row lg:items-center lg:justify-between">
          <div>
            <h2 class="text-2xl font-semibold">{{ t('orderList.title') }}</h2>
            <p class="muted-text">{{ t('orderList.subtitle') }}</p>
          </div>
          <el-select v-model="statusFilter" :placeholder="t('common.status')" class="w-44" @change="loadOrders">
            <el-option :label="t('orderList.statusAll')" :value="null" />
            <el-option :label="t('orderList.pending')" :value="0" />
            <el-option :label="t('orderList.paid')" :value="1" />
            <el-option :label="t('orderList.shipped')" :value="2" />
            <el-option :label="t('orderList.completed')" :value="3" />
          </el-select>
        </div>
      </template>

      <el-table v-if="orders.length" :data="orders" style="width: 100%">
        <el-table-column :label="t('orderList.orderNo')" prop="orderNo" width="220" />
        <el-table-column :label="t('common.amount')" width="140">
          <template #default="{ row }">
            $ {{ formatPrice(row.payAmount) }}
          </template>
        </el-table-column>
        <el-table-column :label="t('common.status')" width="160">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)" effect="light">{{ statusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column :label="t('common.createdAt')">
          <template #default="{ row }">
            {{ row.createdAt }}
          </template>
        </el-table-column>
        <el-table-column :label="t('common.actions')" width="240">
          <template #default="{ row }">
            <el-button v-if="row.status === 0" size="small" type="primary" @click="pay(row)">
              {{ t('orderList.pay') }}
            </el-button>
            <el-button v-if="row.status === 2" size="small" type="success" @click="confirm(row)">
              {{ t('orderList.confirm') }}
            </el-button>
          </template>
        </el-table-column>
        <el-table-column type="expand">
          <template #default="{ row }">
            <div class="space-y-2">
              <div
                v-for="item in row.items"
                :key="item.productId"
                class="flex items-center gap-4 rounded-2xl bg-[var(--surface)] p-3"
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

      <el-empty v-else :description="t('orderList.noOrders')" />
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
import { ElMessage, ElMessageBox } from 'element-plus'
import { confirmOrder, getOrders, payOrder } from '@/api/order.js'
import { useI18n } from '@/i18n/index.js'

const orders = ref([])
const page = ref(1)
const size = ref(8)
const total = ref(0)
const statusFilter = ref(null)
const { t } = useI18n()

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
    ElMessage.error(t('orderList.loadFail'))
  }
}

const handlePageChange = (value) => {
  page.value = value
  loadOrders()
}

const pay = async (row) => {
  try {
    await ElMessageBox.confirm(t('orderList.payConfirm'), 'Confirm', { type: 'warning' })
    const res = await payOrder(row.orderNo, { payAmount: row.payAmount })
    if (res.code === 200) {
      ElMessage.success(t('orderList.paySuccess'))
      loadOrders()
    } else {
      ElMessage.error(res.message)
    }
  } catch (error) {
    ElMessage.error(t('orderList.payFail'))
  }
}

const confirm = async (row) => {
  try {
    await ElMessageBox.confirm(t('orderList.confirmConfirm'), 'Confirm', { type: 'warning' })
    const res = await confirmOrder(row.orderNo)
    if (res.code === 200) {
      ElMessage.success(t('orderList.confirmSuccess'))
      loadOrders()
    } else {
      ElMessage.error(res.message)
    }
  } catch (error) {
    ElMessage.error(t('orderList.confirmFail'))
  }
}

const statusText = (status) => {
  if (status === 0) return t('orderList.pending')
  if (status === 1) return t('orderList.paid')
  if (status === 2) return t('orderList.shipped')
  return t('orderList.completed')
}

const statusType = (status) => {
  if (status === 0) return 'warning'
  if (status === 1) return 'info'
  if (status === 2) return 'primary'
  return 'success'
}

onMounted(loadOrders)
</script>

