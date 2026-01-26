<template>
  <div class="space-y-6 animate-fade-up">
    <el-card class="border-0 bg-white/85 shadow-soft">
      <template #header>
        <div class="flex items-center justify-between">
          <div>
            <h2 class="text-2xl font-semibold">购物车</h2>
            <p class="muted-text">勾选商品后进入结算</p>
          </div>
          <el-button type="primary" :disabled="!checkedItems.length" @click="goToCheckout">
            去结算
          </el-button>
        </div>
      </template>

      <el-table v-if="cartItems.length" :data="cartItems" style="width: 100%">
        <el-table-column label="选择" width="80">
          <template #default="{ row }">
            <el-checkbox v-model="row.checked" :true-label="1" :false-label="0" @change="updateChecked(row)" />
          </template>
        </el-table-column>
        <el-table-column label="商品">
          <template #default="{ row }">
            <div class="flex items-center gap-3">
              <el-image :src="row.image" class="h-16 w-16 rounded-xl" fit="cover" />
              <div>
                <div class="font-medium">{{ row.name }}</div>
                <div class="text-sm text-[var(--muted)]">¥{{ row.price }}</div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="数量" width="160">
          <template #default="{ row }">
            <el-input-number v-model="row.quantity" :min="1" @change="updateQuantity(row)" />
          </template>
        </el-table-column>
        <el-table-column label="小计" width="140">
          <template #default="{ row }">
            ¥{{ (row.price * row.quantity).toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-button text type="danger" @click="removeItem(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-empty v-else description="购物车暂无商品" />
    </el-card>

    <el-card class="border-0 bg-white/85 shadow-soft">
      <div class="flex items-center justify-between">
        <span class="text-lg font-semibold">合计金额</span>
        <span class="text-2xl font-semibold text-[var(--accent)]">¥{{ totalAmount }}</span>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { deleteCartItem, getCartItems, updateCartItem } from '@/api/cart.js'

const router = useRouter()
const cartItems = ref([])

const loadCart = async () => {
  try {
    const res = await getCartItems()
    if (res.code === 200) {
      cartItems.value = res.data || []
    } else {
      ElMessage.error(res.message)
    }
  } catch (error) {
    ElMessage.error('加载购物车失败')
  }
}

const updateQuantity = async (row) => {
  try {
    const res = await updateCartItem(row.cartItemId, { quantity: row.quantity })
    if (res.code !== 200) {
      ElMessage.error(res.message)
    }
  } catch (error) {
    ElMessage.error('更新数量失败')
  }
}

const updateChecked = async (row) => {
  try {
    const res = await updateCartItem(row.cartItemId, { checked: row.checked })
    if (res.code !== 200) {
      ElMessage.error(res.message)
    }
  } catch (error) {
    ElMessage.error('更新勾选失败')
  }
}

const removeItem = async (row) => {
  try {
    const res = await deleteCartItem(row.cartItemId)
    if (res.code === 200) {
      cartItems.value = cartItems.value.filter(item => item.cartItemId !== row.cartItemId)
      ElMessage.success('已删除')
    } else {
      ElMessage.error(res.message)
    }
  } catch (error) {
    ElMessage.error('删除失败')
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
    ElMessage.warning('请选择商品')
    return
  }
  router.push('/orders/confirm')
}

onMounted(() => {
  loadCart()
})
</script>
