<template>
  <section class="space-y-6">
    <div class="flex flex-wrap items-start justify-between gap-4">
      <div>
        <h1 class="section-title">{{ t('admin.productsTitle') }}</h1>
        <p class="muted-text mt-2">{{ t('admin.productsSubtitle') }}</p>
      </div>
      <div class="flex gap-2">
        <el-button @click="exportCsv">Export CSV</el-button>
        <el-button type="primary" size="large" @click="openCreate">+ {{ t('admin.newProduct') }}</el-button>
      </div>
    </div>

    <article class="overflow-hidden rounded-2xl border border-[var(--line)] bg-[var(--surface)]">
      <div class="flex items-center justify-between border-b border-[var(--line)] px-5 py-4">
        <h2 class="text-2xl font-extrabold">Homepage Carousel</h2>
        <el-button text type="primary" @click="router.push('/admin/banners')">Manage All</el-button>
      </div>
      <div class="grid gap-4 p-5 md:grid-cols-2 xl:grid-cols-4">
        <article
          v-for="banner in banners.slice(0, 4)"
          :key="banner.id"
          class="relative overflow-hidden rounded-xl border border-[var(--line)]"
        >
          <img :src="banner.imageUrl" :alt="`banner-${banner.id}`" class="h-36 w-full object-cover" />
          <span class="absolute bottom-2 left-2 chip">{{ banner.status === 1 ? 'Active' : 'Draft' }}</span>
        </article>
        <button
          type="button"
          class="grid h-36 place-content-center rounded-xl border border-dashed border-[var(--line)] text-[var(--muted)]"
          @click="router.push('/admin/banners')"
        >
          Upload Banner
        </button>
      </div>
    </article>

    <div class="grid gap-6 xl:grid-cols-[260px_1fr]">
      <aside class="space-y-4 rounded-2xl border border-[var(--line)] bg-[var(--surface)] p-4">
        <div>
          <div class="flex items-center justify-between">
            <h3 class="text-xl font-extrabold">Categories</h3>
            <el-button text @click="clearCategory">Clear</el-button>
          </div>
          <div class="mt-3 space-y-2">
            <label
              v-for="category in categories"
              :key="category.id"
              class="flex cursor-pointer items-center justify-between rounded-lg px-2 py-2 hover:bg-[var(--surface-soft)]"
            >
              <span class="flex items-center gap-2">
                <el-radio v-model="filters.categoryId" :label="category.id" />
                <span>{{ category.name }}</span>
              </span>
            </label>
          </div>
        </div>

        <div class="border-t border-[var(--line)] pt-4">
          <h3 class="text-xl font-extrabold">Status</h3>
          <div class="mt-3 space-y-2">
            <el-radio-group v-model="filters.status" class="grid gap-2">
              <el-radio label="">All</el-radio>
              <el-radio label="ON">In Stock</el-radio>
              <el-radio label="OFF">Disabled</el-radio>
            </el-radio-group>
          </div>
        </div>
      </aside>

      <section class="space-y-4">
        <div class="flex flex-wrap items-center justify-between gap-3">
          <div class="flex flex-wrap items-center gap-2">
            <el-input
              v-model="filters.keyword"
              :placeholder="t('productList.searchPlaceholder')"
              clearable
              class="w-72"
              @keyup.enter="fetchProducts"
            />
            <el-button type="primary" @click="fetchProducts">{{ t('common.search') }}</el-button>
            <el-button @click="resetFilters">{{ t('common.reset') }}</el-button>
          </div>
          <el-select v-model="sortMode" class="w-44" @change="applySort">
            <el-option label="Newest Added" value="newest" />
            <el-option label="Price High" value="priceDesc" />
            <el-option label="Price Low" value="priceAsc" />
          </el-select>
        </div>

        <div class="grid gap-4 sm:grid-cols-2 xl:grid-cols-3">
          <article
            v-for="product in displayProducts"
            :key="product.id"
            class="overflow-hidden rounded-2xl border border-[var(--line)] bg-[var(--surface)] p-3"
          >
            <img :src="product.coverUrl" :alt="product.name" class="h-48 w-full rounded-xl object-cover" />
            <div class="mt-3 flex items-center justify-between">
              <span class="chip">{{ product.status === 'ON' ? 'In Stock' : 'Disabled' }}</span>
              <span class="text-sm text-[var(--muted)]">{{ t('productList.stock') }} {{ product.stock }}</span>
            </div>
            <h3 class="mt-3 line-clamp-2 text-2xl font-extrabold">{{ product.name }}</h3>
            <p class="line-clamp-2 text-sm text-[var(--muted)]">{{ product.brief }}</p>
            <div class="mt-3 text-3xl font-extrabold text-[var(--accent)]">$ {{ formatPrice(product.price) }}</div>

            <div class="mt-4 grid grid-cols-2 gap-2">
              <el-button @click="openEdit(product)">{{ t('common.edit') }}</el-button>
              <el-button
                v-if="product.status === 'ON'"
                type="warning"
                v-permission="'admin:products:off'"
                @click="setStatus(product, 'OFF')"
              >
                Off
              </el-button>
              <el-button
                v-else
                type="success"
                v-permission="'admin:products:on'"
                @click="setStatus(product, 'ON')"
              >
                On
              </el-button>
              <el-button
                type="danger"
                class="col-span-2"
                v-permission="'admin:products:delete'"
                @click="removeProduct(product)"
              >
                {{ t('common.delete') }}
              </el-button>
            </div>
          </article>
        </div>

        <div class="flex justify-center">
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
      </section>
    </div>

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
          <el-input v-model="form.brief" type="textarea" :rows="2" />
        </el-form-item>
        <el-form-item :label="t('admin.coverUrl')">
          <el-input v-model="form.coverUrl" />
          <div class="mt-2 flex items-center gap-3">
            <el-upload :show-file-list="false" :http-request="handleUpload">
              <el-button size="small">{{ t('admin.upload') }}</el-button>
            </el-upload>
            <img v-if="form.coverUrl" :src="form.coverUrl" class="h-12 w-20 rounded-lg object-cover" />
          </div>
        </el-form-item>
        <el-form-item label="Gallery URLs (one per line)">
          <el-input
            v-model="form.imageUrlsText"
            type="textarea"
            :rows="3"
            placeholder="https://.../image1.jpg&#10;https://.../image2.jpg"
          />
        </el-form-item>
        <el-form-item :label="t('admin.detailHtml')">
          <el-input v-model="form.detailHtml" type="textarea" :rows="4" />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">{{ t('common.cancel') }}</el-button>
        <el-button type="primary" :loading="saving" @click="saveProduct">{{ t('common.save') }}</el-button>
      </template>
    </el-dialog>
  </section>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
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
import { fetchAdminBanners } from '@/api/admin/banners.js'
import { uploadAdminFile } from '@/api/admin/upload.js'
import { useI18n } from '@/i18n/index.js'

const router = useRouter()
const loading = ref(false)
const saving = ref(false)
const products = ref([])
const categories = ref([])
const banners = ref([])
const total = ref(0)
const page = ref(0)
const size = ref(12)
const sortMode = ref('newest')
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
  imageUrlsText: '',
  detailHtml: '',
})

const dialogTitle = computed(() => (editingId.value ? t('common.edit') : t('admin.newProduct')))

const displayProducts = computed(() => {
  const data = [...products.value]
  if (sortMode.value === 'priceAsc') {
    data.sort((a, b) => Number(a.price) - Number(b.price))
  } else if (sortMode.value === 'priceDesc') {
    data.sort((a, b) => Number(b.price) - Number(a.price))
  } else {
    data.sort((a, b) => new Date(b.createdAt || 0) - new Date(a.createdAt || 0))
  }
  return data
})

const formatPrice = (value) => Number(value || 0).toFixed(2)

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
      products.value = res.data.content || []
      total.value = res.data.totalElements || 0
    } else {
      ElMessage.error(res.message || t('common.empty'))
    }
  } catch {
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

const clearCategory = () => {
  filters.categoryId = null
  fetchProducts()
}

const applySort = () => {
  // Sorting is computed locally.
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
  form.imageUrlsText = ''
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
  form.imageUrlsText = row.coverUrl || ''
  form.detailHtml = row.detailHtml || ''
  dialogVisible.value = true
}

const parseImageUrls = (rawText) => {
  return String(rawText || '')
    .split('\n')
    .map((line) => line.trim())
    .filter((line) => line.length > 0)
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
      imageUrls: parseImageUrls(form.imageUrlsText),
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
  } catch {
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
      if (!parseImageUrls(form.imageUrlsText).includes(res.data.url)) {
        form.imageUrlsText = form.imageUrlsText
          ? `${form.imageUrlsText}\n${res.data.url}`
          : res.data.url
      }
      ElMessage.success(t('admin.uploadSuccess'))
    } else {
      ElMessage.error(res.message || t('admin.uploadFail'))
    }
  } catch {
    ElMessage.error(t('admin.uploadFail'))
  }
}

const fetchCategories = async () => {
  try {
    const res = await fetchAdminCategories({ page: 0, size: 200 })
    if (res.code === 200) {
      categories.value = res.data.content || []
    }
  } catch {
    // ignore
  }
}

const fetchBanners = async () => {
  try {
    const res = await fetchAdminBanners({ page: 0, size: 20 })
    if (res.code === 200) {
      banners.value = res.data.content || []
    }
  } catch {
    banners.value = []
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
    const res = status === 'ON' ? await enableAdminProduct(row.id) : await disableAdminProduct(row.id)
    if (res.code === 200) {
      ElMessage.success(t('common.save'))
      fetchProducts()
    } else {
      ElMessage.error(res.message || t('common.empty'))
    }
  } catch {
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

const exportCsv = () => {
  if (!displayProducts.value.length) {
    ElMessage.info('No products to export')
    return
  }
  const header = ['id', 'name', 'categoryId', 'price', 'stock', 'status']
  const rows = displayProducts.value.map((item) => [item.id, item.name, item.categoryId, item.price, item.stock, item.status])
  const csv = [header.join(','), ...rows.map((row) => row.join(','))].join('\n')
  const blob = new Blob([csv], { type: 'text/csv;charset=utf-8;' })
  const url = window.URL.createObjectURL(blob)
  const link = document.createElement('a')
  link.href = url
  link.download = `products-${new Date().toISOString().slice(0, 10)}.csv`
  link.click()
  window.URL.revokeObjectURL(url)
}

onMounted(() => {
  fetchProducts()
  fetchCategories()
  fetchBanners()
})
</script>



