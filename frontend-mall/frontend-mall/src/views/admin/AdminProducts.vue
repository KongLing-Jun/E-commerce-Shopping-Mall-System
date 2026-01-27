<template>
  <section class="space-y-6">
    <div class="flex flex-wrap items-start justify-between gap-4">
      <div>
        <p class="text-xs uppercase tracking-[0.35em] text-[var(--muted)]">Admin Console</p>
        <h1 class="section-title mt-3">{{ t('admin.productsTitle') }}</h1>
        <p class="muted-text mt-2">{{ t('admin.productsSubtitle') }}</p>
      </div>
      <el-button type="primary" size="large" @click="openCreate">{{ t('admin.newProduct') }}</el-button>
    </div>

    <el-card class="border-0 bg-[var(--surface)] shadow-soft">
      <div class="flex flex-wrap items-end gap-3">
        <el-input v-model="filters.keyword" :placeholder="t('productList.searchPlaceholder')" clearable class="w-60" />
        <el-select v-model="filters.categoryId" :placeholder="t('productList.categoryPlaceholder')" clearable class="w-48">
          <el-option
            v-for="category in categories"
            :key="category.id"
            :label="categoryLabel(category)"
            :value="category.id"
          />
        </el-select>
        <el-select v-model="filters.status" :placeholder="t('common.status')" clearable class="w-36">
          <el-option label="ON" value="ON" />
          <el-option label="OFF" value="OFF" />
        </el-select>
        <el-button type="primary" @click="fetchProducts">{{ t('common.search') }}</el-button>
        <el-button @click="resetFilters">{{ t('common.reset') }}</el-button>
      </div>

      <el-table :data="products" class="mt-6" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="90" />
        <el-table-column prop="name" :label="t('nav.products')" min-width="200" />
        <el-table-column prop="price" :label="t('common.price')" width="120" />
        <el-table-column prop="stock" :label="t('productList.stock')" width="120" />
        <el-table-column :label="t('common.status')" width="120">
          <template #default="{ row }">
            <el-tag :type="row.status === 'ON' ? 'success' : 'info'">
              {{ row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" :label="t('common.createdAt')" min-width="160" />
        <el-table-column :label="t('common.actions')" width="260">
          <template #default="{ row }">
            <el-button size="small" @click="openEdit(row)">{{ t('common.edit') }}</el-button>
            <el-button
              size="small"
              type="warning"
              v-if="row.status === 'ON'"
              v-permission="'admin:products:off'"
              @click="setStatus(row, 'OFF')"
            >
              Off
            </el-button>
            <el-button
              size="small"
              type="success"
              v-else
              v-permission="'admin:products:on'"
              @click="setStatus(row, 'ON')"
            >
              On
            </el-button>
            <el-button
              size="small"
              type="danger"
              v-permission="'admin:products:delete'"
              @click="removeProduct(row)"
            >
              {{ t('common.delete') }}
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
        <el-form-item :label="t('nav.products')">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item :label="t('productList.categoryPlaceholder')">
          <el-select v-model="form.categoryId" class="w-full">
            <el-option
              v-for="category in categories"
              :key="category.id"
              :label="categoryLabel(category)"
              :value="category.id"
            />
          </el-select>
        </el-form-item>
        <div class="grid gap-4 md:grid-cols-2">
          <el-form-item :label="t('common.price')">
            <el-input-number v-model="form.price" :min="0" :step="0.1" class="w-full" />
          </el-form-item>
          <el-form-item :label="t('productList.stock')">
            <el-input-number v-model="form.stock" :min="0" class="w-full" />
          </el-form-item>
        </div>
        <el-form-item :label="t('common.status')">
          <el-select v-model="form.status" class="w-full">
            <el-option label="ON" value="ON" />
            <el-option label="OFF" value="OFF" />
          </el-select>
        </el-form-item>
        <el-form-item :label="t('admin.brief')">
          <el-input v-model="form.brief" />
        </el-form-item>
        <el-form-item :label="t('admin.coverUrl')">
          <el-input v-model="form.coverUrl" />
          <div class="mt-2 flex items-center gap-3">
            <el-upload :show-file-list="false" :http-request="handleUpload">
              <el-button size="small">{{ t('admin.upload') }}</el-button>
            </el-upload>
            <el-image v-if="form.coverUrl" :src="form.coverUrl" class="h-12 w-20 rounded-lg" fit="cover" />
          </div>
        </el-form-item>
        <el-form-item :label="t('admin.detailHtml')">
          <el-input v-model="form.detailHtml" type="textarea" :rows="4" />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">{{ t('common.cancel') }}</el-button>
        <el-button type="primary" :loading="saving" @click="saveProduct">
          {{ t('common.save') }}
        </el-button>
      </template>
    </el-dialog>
  </section>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  createAdminProduct,
  deleteAdminProduct,
  disableAdminProduct,
  enableAdminProduct,
  fetchAdminProducts,
  updateAdminProduct,
} from '@/api/admin/products.js'
import { fetchAdminCategories } from '@/api/admin/categories.js'
import { uploadAdminFile } from '@/api/admin/upload.js'
import { useI18n } from '@/i18n/index.js'

const loading = ref(false)
const saving = ref(false)
const products = ref([])
const categories = ref([])
const total = ref(0)
const page = ref(0)
const size = ref(10)
const { t } = useI18n()

const filters = reactive({
  keyword: '',
  categoryId: null,
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

const dialogTitle = computed(() => (editingId.value ? t('common.edit') : t('admin.newProduct')))

const fetchProducts = async () => {
  loading.value = true
  try {
    const params = {
      keyword: filters.keyword || undefined,
      categoryId: filters.categoryId || undefined,
      status: filters.status || undefined,
      page: page.value,
      size: size.value,
    }
    const res = await fetchAdminProducts(params)
    if (res.code === 200) {
      products.value = res.data.content
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
  filters.keyword = ''
  filters.categoryId = null
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
    ElMessage.warning(t('auth.completeInfo'))
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
      ElMessage.success(t('common.save'))
      dialogVisible.value = false
      fetchProducts()
    } else {
      ElMessage.error(res.message || t('common.empty'))
    }
  } catch (error) {
    ElMessage.error(t('common.empty'))
  } finally {
    saving.value = false
  }
}

const handleUpload = async (options) => {
  try {
    const res = await uploadAdminFile(options.file)
    if (res.code === 200) {
      form.coverUrl = res.data.url
      ElMessage.success(t('admin.uploadSuccess'))
    } else {
      ElMessage.error(res.message || t('admin.uploadFail'))
    }
  } catch (error) {
    ElMessage.error(t('admin.uploadFail'))
  }
}

const fetchCategories = async () => {
  try {
    const res = await fetchAdminCategories({ page: 0, size: 200 })
    if (res.code === 200) {
      categories.value = res.data.content || []
    }
  } catch (error) {
    // ignore
  }
}

const categoryLabel = (category) => {
  if (category?.status === 0) {
    return `${category.name} (${t('admin.statusDisabled')})`
  }
  return category?.name || ''
}

const setStatus = async (row, status) => {
  try {
    const res = status === 'ON'
      ? await enableAdminProduct(row.id)
      : await disableAdminProduct(row.id)
    if (res.code === 200) {
      ElMessage.success(t('common.save'))
      fetchProducts()
    } else {
      ElMessage.error(res.message || t('common.empty'))
    }
  } catch (error) {
    ElMessage.error(t('common.empty'))
  }
}

const removeProduct = async (row) => {
  try {
    await ElMessageBox.confirm(t('admin.deleteConfirm'), 'Confirm', { type: 'warning' })
    const res = await deleteAdminProduct(row.id)
    if (res.code === 200) {
      ElMessage.success(t('common.delete'))
      fetchProducts()
    } else {
      ElMessage.error(res.message || t('common.empty'))
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(t('common.empty'))
    }
  }
}

onMounted(() => {
  fetchProducts()
  fetchCategories()
})
</script>

