<template>
  <div class="space-y-6 animate-fade-up">
    <el-card class="border-0 bg-[var(--surface-strong)] shadow-soft">
      <template #header>
        <div class="flex items-center justify-between">
          <div>
            <h2 class="text-2xl font-semibold">{{ t('cart.title') }}</h2>
            <p class="muted-text">{{ t('cart.subtitle') }}</p>
          </div>
          <el-button type="primary" :disabled="!checkedItems.length" @click="goToCheckout">
            {{ t('cart.checkout') }}
          </el-button>
        </div>
      </template>

      <el-table v-if="cartItems.length" :data="cartItems" style="width: 100%">
        <el-table-column :label="t('common.status')" width="80">
          <template #default="{ row }">
            <el-checkbox v-model="row.checked" :true-label="1" :false-label="0" @change="updateChecked(row)" />
          </template>
        </el-table-column>
        <el-table-column :label="t('nav.products')">
          <template #default="{ row }">
            <div class="flex items-center gap-3">
              <el-image :src="row.image" class="h-16 w-16 rounded-xl" fit="cover" />
              <div>
                <div class="font-medium">{{ row.name }}</div>
                <div class="text-sm text-[var(--muted)]">$ {{ row.price }}</div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column :label="t('cart.quantity')" width="160">
          <template #default="{ row }">
            <el-input-number v-model="row.quantity" :min="1" @change="updateQuantity(row)" />
          </template>
        </el-table-column>
        <el-table-column :label="t('common.amount')" width="140">
          <template #default="{ row }">
            $ {{ (row.price * row.quantity).toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column :label="t('common.actions')" width="120">
          <template #default="{ row }">
            <el-button text type="danger" @click="removeItem(row)">{{ t('cart.remove') }}</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-empty v-else :description="t('cart.empty')" />
    </el-card>

    <el-card class="border-0 bg-[var(--surface-strong)] shadow-soft">
      <div class="flex items-center justify-between">
        <span class="text-lg font-semibold">{{ t('cart.selectedTotal') }}</span>
        <span class="text-2xl font-semibold text-[var(--accent)]">$ {{ totalAmount }}</span>
      </div>
    </el-card>
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
const { t } = useI18n()

const loadCart = async () => {
  try {
    const res = await getCartItems()
    if (res.code === 200) {
      cartItems.value = res.data || []
    } else {
      ElMessage.error(res.message)
    }
  } catch (error) {
    ElMessage.error(t('cart.loadFail'))
  }
}

const updateQuantity = async (row) => {
  try {
    const res = await updateCartItem(row.cartItemId, { quantity: row.quantity })
    if (res.code !== 200) {
      ElMessage.error(res.message)
    }
  } catch (error) {
    ElMessage.error(t('cart.updateFail'))
  }
}

const updateChecked = async (row) => {
  try {
    const res = await updateCartItem(row.cartItemId, { checked: row.checked })
    if (res.code !== 200) {
      ElMessage.error(res.message)
    }
  } catch (error) {
    ElMessage.error(t('cart.updateFail'))
  }
}

const removeItem = async (row) => {
  try {
    const res = await deleteCartItem(row.cartItemId)
    if (res.code === 200) {
      cartItems.value = cartItems.value.filter(item => item.cartItemId !== row.cartItemId)
      ElMessage.success(t('cart.removeSuccess'))
    } else {
      ElMessage.error(res.message)
    }
  } catch (error) {
    ElMessage.error(t('cart.removeFail'))
  }
}

const checkedItems = computed(() => cartItems.value.filter(item => item.checked === 1))

const totalAmount = computed(() => {
  return checkedItems.value
    .reduce((sum, item) => sum + item.price * item.quantity, 0)
    .toFixed(2)
})

const goToCheckout = () => {
  if (!checkedItems.value.length) {
    ElMessage.warning(t('orderConfirm.noItems'))
    return
  }
  router.push('/orders/confirm')
}

onMounted(loadCart)
</script>

