<template>
  <div class="space-y-6 animate-fade-up">
    <el-card class="border-0 bg-white/85 shadow-soft">
      <template #header>
        <div>
          <h2 class="text-2xl font-semibold">Order confirmation</h2>
          <p class="muted-text">Review items, choose address, and submit.</p>
        </div>
      </template>

      <div class="grid gap-6 lg:grid-cols-[1.2fr_0.8fr]">
        <div class="space-y-4">
          <h3 class="text-lg font-semibold">Items</h3>
          <el-table v-if="orderItems.length" :data="orderItems" style="width: 100%">
            <el-table-column label="Product">
              <template #default="{ row }">
                <div class="flex items-center gap-3">
                  <el-image :src="row.image" class="h-14 w-14 rounded-xl" fit="cover" />
                  <div>
                    <div class="font-medium">{{ row.name }}</div>
                    <div class="text-sm text-[var(--muted)]">$ {{ formatPrice(row.price) }}</div>
                  </div>
                </div>
              </template>
            </el-table-column>
            <el-table-column label="Qty" width="120">
              <template #default="{ row }">
                x {{ row.quantity }}
              </template>
            </el-table-column>
            <el-table-column label="Subtotal" width="140">
              <template #default="{ row }">
                $ {{ formatPrice(row.price * row.quantity) }}
              </template>
            </el-table-column>
          </el-table>
          <el-empty v-else description="No items selected" />
        </div>

        <div class="space-y-4">
          <h3 class="text-lg font-semibold">Shipping address</h3>
          <el-radio-group v-model="selectedAddressId" class="w-full">
            <el-radio
              v-for="address in addresses"
              :key="address.id"
              :value="address.id"
              class="mb-2 w-full"
            >
              {{ address.receiver }} {{ address.phone }} -
              {{ address.province }} {{ address.city }} {{ address.area }} {{ address.detail }}
              <el-tag v-if="address.isDefault === 1" type="success" size="small" class="ml-2">
                Default
              </el-tag>
            </el-radio>
          </el-radio-group>
          <el-button type="primary" plain @click="router.push('/addresses')">
            Manage addresses
          </el-button>
        </div>
      </div>
    </el-card>

    <el-card class="border-0 bg-white/85 shadow-soft">
      <div class="flex flex-col items-end gap-3">
        <div class="text-lg">
          Total <span class="text-2xl font-semibold text-[var(--accent)]">$ {{ formatPrice(totalAmount) }}</span>
        </div>
        <el-button type="primary" size="large" :loading="loading" @click="submitOrder">
          Submit order
        </el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { createOrder, getOrderPre } from '@/api/order.js'

const router = useRouter()
const orderItems = ref([])
const addresses = ref([])
const totalAmount = ref(0)
const selectedAddressId = ref(null)
const loading = ref(false)

const formatPrice = (value) => {
  const numberValue = Number(value || 0)
  return numberValue.toFixed(2)
}

const loadPreOrder = async () => {
  try {
    const res = await getOrderPre()
    if (res.code === 200) {
      orderItems.value = res.data.items || []
      addresses.value = res.data.addresses || []
      totalAmount.value = Number(res.data.totalAmount || 0)
      const defaultAddress = addresses.value.find(item => item.isDefault === 1)
      selectedAddressId.value = defaultAddress ? defaultAddress.id : addresses.value[0]?.id || null
    } else {
      ElMessage.error(res.message)
    }
  } catch (error) {
    ElMessage.error('Failed to load order confirmation')
  }
}

const submitOrder = async () => {
  if (!selectedAddressId.value) {
    ElMessage.warning('Please select a shipping address')
    return
  }
  if (!orderItems.value.length) {
    ElMessage.warning('No items selected')
    return
  }
  loading.value = true
  try {
    const res = await createOrder({ addressId: selectedAddressId.value })
    if (res.code === 200) {
      ElMessage.success('Order created')
      router.push('/orders')
    } else {
      ElMessage.error(res.message)
    }
  } catch (error) {
    ElMessage.error('Failed to submit order')
  } finally {
    loading.value = false
  }
}

onMounted(loadPreOrder)
</script>
