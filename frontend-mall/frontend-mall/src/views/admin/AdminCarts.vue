<template>
  <section class="space-y-6">
    <div>
      <p class="text-xs uppercase tracking-[0.35em] text-[var(--muted)]">Admin Console</p>
      <h1 class="section-title mt-3">Cart Oversight</h1>
      <p class="muted-text mt-2">Review active carts and remove invalid items.</p>
    </div>

    <el-card class="border-0 bg-white/80 shadow-soft">
      <div class="flex flex-wrap items-end gap-3">
        <el-input v-model="filters.userId" placeholder="User ID" clearable class="w-44" />
        <el-button type="primary" @click="fetchCarts">Search</el-button>
        <el-button @click="resetFilters">Reset</el-button>
      </div>

      <el-table :data="items" class="mt-6" v-loading="loading" stripe>
        <el-table-column prop="cartItemId" label="Cart ID" width="100" />
        <el-table-column prop="userId" label="User ID" width="100" />
        <el-table-column prop="username" label="User" min-width="120" />
        <el-table-column prop="productId" label="Product ID" width="120" />
        <el-table-column prop="productName" label="Product" min-width="180" />
        <el-table-column prop="price" label="Price" width="120" />
        <el-table-column prop="quantity" label="Qty" width="90" />
        <el-table-column label="Checked" width="110">
          <template #default="{ row }">
            <el-tag :type="row.checked === 1 ? 'success' : 'info'">
              {{ row.checked === 1 ? 'Yes' : 'No' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="Created" min-width="160" />
        <el-table-column label="Actions" width="120">
          <template #default="{ row }">
            <el-button size="small" type="danger" @click="removeItem(row)">Delete</el-button>
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
import { deleteAdminCartItem, fetchAdminCarts } from '@/api/admin/carts.js'

const loading = ref(false)
const items = ref([])
const total = ref(0)
const page = ref(0)
const size = ref(10)

const filters = reactive({
  userId: '',
})

const fetchCarts = async () => {
  loading.value = true
  try {
    const params = {
      userId: filters.userId ? Number(filters.userId) : undefined,
      page: page.value,
      size: size.value,
    }
    const res = await fetchAdminCarts(params)
    if (res.code === 200) {
      items.value = res.data.content
      total.value = res.data.totalElements
    } else {
      ElMessage.error(res.message || 'Failed to load carts')
    }
  } catch (error) {
    ElMessage.error('Failed to load carts')
  } finally {
    loading.value = false
  }
}

const resetFilters = () => {
  filters.userId = ''
  page.value = 0
  fetchCarts()
}

const changePage = (nextPage) => {
  page.value = nextPage - 1
  fetchCarts()
}

const changeSize = (nextSize) => {
  size.value = nextSize
  page.value = 0
  fetchCarts()
}

const removeItem = async (row) => {
  try {
    await ElMessageBox.confirm('Delete this cart item?', 'Confirm', { type: 'warning' })
    const res = await deleteAdminCartItem(row.cartItemId)
    if (res.code === 200) {
      ElMessage.success('Deleted')
      fetchCarts()
    } else {
      ElMessage.error(res.message || 'Delete failed')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('Delete failed')
    }
  }
}

onMounted(fetchCarts)
</script>
