<template>
  <section class="space-y-6">
    <div class="flex flex-wrap items-start justify-between gap-4">
      <div>
        <p class="text-xs uppercase tracking-[0.35em] text-[var(--muted)]">Admin Console</p>
        <h1 class="section-title mt-3">Product Control</h1>
        <p class="muted-text mt-2">Manage catalog, pricing, and inventory in one place.</p>
      </div>
      <el-button type="primary" size="large" @click="openCreate">New Product</el-button>
    </div>

    <el-card class="border-0 bg-white/80 shadow-soft">
      <div class="flex flex-wrap items-end gap-3">
        <el-input v-model="filters.keyword" placeholder="Search name" clearable class="w-60" />
        <el-input v-model="filters.categoryId" placeholder="Category ID" clearable class="w-40" />
        <el-select v-model="filters.status" placeholder="Status" clearable class="w-36">
          <el-option label="ON" value="ON" />
          <el-option label="OFF" value="OFF" />
        </el-select>
        <el-button type="primary" @click="fetchProducts">Search</el-button>
        <el-button @click="resetFilters">Reset</el-button>
      </div>

      <el-table :data="products" class="mt-6" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="90" />
        <el-table-column prop="name" label="Name" min-width="200" />
        <el-table-column prop="price" label="Price" width="120" />
        <el-table-column prop="stock" label="Stock" width="120" />
        <el-table-column label="Status" width="120">
          <template #default="{ row }">
            <el-tag :type="row.status === 'ON' ? 'success' : 'info'">
              {{ row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="Created" min-width="160" />
        <el-table-column label="Actions" width="220">
          <template #default="{ row }">
            <el-button size="small" @click="openEdit(row)">Edit</el-button>
            <el-button
              size="small"
              type="warning"
              v-if="row.status === 'ON'"
              @click="setStatus(row, 'OFF')"
            >
              Off
            </el-button>
            <el-button
              size="small"
              type="success"
              v-else
              @click="setStatus(row, 'ON')"
            >
              On
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

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="560px">
      <el-form :model="form" label-position="top">
        <el-form-item label="Name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="Category ID">
          <el-input-number v-model="form.categoryId" :min="1" class="w-full" />
        </el-form-item>
        <div class="grid gap-4 md:grid-cols-2">
          <el-form-item label="Price">
            <el-input-number v-model="form.price" :min="0" :step="0.1" class="w-full" />
          </el-form-item>
          <el-form-item label="Stock">
            <el-input-number v-model="form.stock" :min="0" class="w-full" />
          </el-form-item>
        </div>
        <el-form-item label="Status">
          <el-select v-model="form.status" class="w-full">
            <el-option label="ON" value="ON" />
            <el-option label="OFF" value="OFF" />
          </el-select>
        </el-form-item>
        <el-form-item label="Brief">
          <el-input v-model="form.brief" />
        </el-form-item>
        <el-form-item label="Cover URL">
          <el-input v-model="form.coverUrl" />
        </el-form-item>
        <el-form-item label="Detail HTML">
          <el-input v-model="form.detailHtml" type="textarea" :rows="4" />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">Cancel</el-button>
        <el-button type="primary" :loading="saving" @click="saveProduct">
          Save
        </el-button>
      </template>
    </el-dialog>
  </section>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import {
  createAdminProduct,
  disableAdminProduct,
  enableAdminProduct,
  fetchAdminProducts,
  updateAdminProduct,
} from '@/api/admin/products.js'

const loading = ref(false)
const saving = ref(false)
const products = ref([])
const total = ref(0)
const page = ref(0)
const size = ref(10)

const filters = reactive({
  keyword: '',
  categoryId: '',
  status: '',
})

const dialogVisible = ref(false)
const editingId = ref(null)
const form = reactive({
  name: '',
  categoryId: null,
  price: 0,
  stock: 0,
  status: 'ON',
  brief: '',
  coverUrl: '',
  detailHtml: '',
})

const dialogTitle = computed(() => (editingId.value ? 'Edit Product' : 'Create Product'))

const fetchProducts = async () => {
  loading.value = true
  try {
    const params = {
      keyword: filters.keyword || undefined,
      categoryId: filters.categoryId ? Number(filters.categoryId) : undefined,
      status: filters.status || undefined,
      page: page.value,
      size: size.value,
    }
    const res = await fetchAdminProducts(params)
    if (res.code === 200) {
      products.value = res.data.content
      total.value = res.data.totalElements
    } else {
      ElMessage.error(res.message || 'Failed to load products')
    }
  } catch (error) {
    ElMessage.error('Failed to load products')
  } finally {
    loading.value = false
  }
}

const resetFilters = () => {
  filters.keyword = ''
  filters.categoryId = ''
  filters.status = ''
  page.value = 0
  fetchProducts()
}

const changePage = (nextPage) => {
  page.value = nextPage - 1
  fetchProducts()
}

const changeSize = (nextSize) => {
  size.value = nextSize
  page.value = 0
  fetchProducts()
}

const resetForm = () => {
  form.name = ''
  form.categoryId = null
  form.price = 0
  form.stock = 0
  form.status = 'ON'
  form.brief = ''
  form.coverUrl = ''
  form.detailHtml = ''
}

const openCreate = () => {
  editingId.value = null
  resetForm()
  dialogVisible.value = true
}

const openEdit = (row) => {
  editingId.value = row.id
  form.name = row.name
  form.categoryId = row.categoryId
  form.price = row.price
  form.stock = row.stock
  form.status = row.status || 'ON'
  form.brief = row.brief || ''
  form.coverUrl = row.coverUrl || ''
  form.detailHtml = row.detailHtml || ''
  dialogVisible.value = true
}

const saveProduct = async () => {
  if (!form.name || !form.categoryId) {
    ElMessage.warning('Name and category are required')
    return
  }
  saving.value = true
  try {
    const payload = {
      name: form.name,
      categoryId: form.categoryId,
      price: form.price,
      stock: form.stock,
      status: form.status,
      brief: form.brief,
      coverUrl: form.coverUrl,
      detailHtml: form.detailHtml,
    }
    const res = editingId.value
      ? await updateAdminProduct(editingId.value, payload)
      : await createAdminProduct(payload)
    if (res.code === 200) {
      ElMessage.success('Saved')
      dialogVisible.value = false
      fetchProducts()
    } else {
      ElMessage.error(res.message || 'Save failed')
    }
  } catch (error) {
    ElMessage.error('Save failed')
  } finally {
    saving.value = false
  }
}

const setStatus = async (row, status) => {
  try {
    const res = status === 'ON'
      ? await enableAdminProduct(row.id)
      : await disableAdminProduct(row.id)
    if (res.code === 200) {
      ElMessage.success('Status updated')
      fetchProducts()
    } else {
      ElMessage.error(res.message || 'Update failed')
    }
  } catch (error) {
    ElMessage.error('Update failed')
  }
}

onMounted(fetchProducts)
</script>
