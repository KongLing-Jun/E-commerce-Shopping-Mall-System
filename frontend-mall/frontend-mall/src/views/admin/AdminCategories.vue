<template>
  <section class="space-y-6">
    <div class="flex flex-wrap items-start justify-between gap-4">
      <div>
        <p class="text-xs uppercase tracking-[0.35em] text-[var(--muted)]">Admin Console</p>
        <h1 class="section-title mt-3">{{ t('admin.categoriesTitle') }}</h1>
        <p class="muted-text mt-2">{{ t('admin.categoriesSubtitle') }}</p>
      </div>
      <el-button type="primary" size="large" @click="openCreate">{{ t('admin.newCategory') }}</el-button>
    </div>

    <el-card class="border-0 bg-[var(--surface)] shadow-soft">
      <div class="flex flex-wrap items-end gap-3">
        <el-input v-model="filters.keyword" :placeholder="t('common.search')" clearable class="w-60" />
        <el-select v-model="filters.status" :placeholder="t('common.status')" clearable class="w-40">
          <el-option :label="t('admin.statusEnabled')" :value="1" />
          <el-option :label="t('admin.statusDisabled')" :value="0" />
        </el-select>
        <el-button type="primary" @click="fetchCategories">{{ t('common.search') }}</el-button>
        <el-button @click="resetFilters">{{ t('common.reset') }}</el-button>
      </div>

      <el-table :data="categories" class="mt-6" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="90" />
        <el-table-column prop="name" :label="t('admin.categoryName')" min-width="220" />
        <el-table-column prop="parentId" :label="t('admin.parentId')" width="120" />
        <el-table-column prop="sort" :label="t('admin.sort')" width="120" />
        <el-table-column :label="t('common.status')" width="140">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? t('admin.statusEnabled') : t('admin.statusDisabled') }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column :label="t('common.actions')" width="260">
          <template #default="{ row }">
            <el-button size="small" v-permission="'admin:categories:edit'" @click="openEdit(row)">
              {{ t('common.edit') }}
            </el-button>
            <el-button
              size="small"
              type="warning"
              v-if="row.status === 1"
              v-permission="'admin:categories:edit'"
              @click="setStatus(row, 0)"
            >
              {{ t('admin.statusDisabled') }}
            </el-button>
            <el-button
              size="small"
              type="success"
              v-else
              v-permission="'admin:categories:edit'"
              @click="setStatus(row, 1)"
            >
              {{ t('admin.statusEnabled') }}
            </el-button>
            <el-button size="small" type="danger" v-permission="'admin:categories:delete'" @click="removeCategory(row)">
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

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="520px">
      <el-form :model="form" label-position="top">
        <el-form-item :label="t('admin.categoryName')">
          <el-input v-model="form.name" />
        </el-form-item>
        <div class="grid gap-4 md:grid-cols-2">
          <el-form-item :label="t('admin.parentId')">
            <el-input-number v-model="form.parentId" :min="0" class="w-full" />
          </el-form-item>
          <el-form-item :label="t('admin.sort')">
            <el-input-number v-model="form.sort" :min="0" class="w-full" />
          </el-form-item>
        </div>
        <el-form-item :label="t('common.status')">
          <el-select v-model="form.status" class="w-full">
            <el-option :label="t('admin.statusEnabled')" :value="1" />
            <el-option :label="t('admin.statusDisabled')" :value="0" />
          </el-select>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">{{ t('common.cancel') }}</el-button>
        <el-button type="primary" :loading="saving" @click="saveCategory">
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
  createAdminCategory,
  deleteAdminCategory,
  disableAdminCategory,
  enableAdminCategory,
  fetchAdminCategories,
  updateAdminCategory,
} from '@/api/admin/categories.js'
import { useI18n } from '@/i18n/index.js'

const categories = ref([])
const total = ref(0)
const page = ref(0)
const size = ref(10)
const loading = ref(false)
const saving = ref(false)
const { t } = useI18n()

const filters = reactive({
  keyword: '',
  status: '',
})

const dialogVisible = ref(false)
const editingId = ref(null)
const form = reactive({
  name: '',
  parentId: 0,
  sort: 0,
  status: 1,
})

const dialogTitle = computed(() => (editingId.value ? t('common.edit') : t('admin.newCategory')))

const fetchCategories = async () => {
  loading.value = true
  try {
    const params = {
      keyword: filters.keyword || undefined,
      status: filters.status === '' || filters.status === null ? undefined : Number(filters.status),
      page: page.value,
      size: size.value,
    }
    const res = await fetchAdminCategories(params)
    if (res.code === 200) {
      categories.value = res.data.content
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
  filters.status = ''
  page.value = 0
  fetchCategories()
}

const changePage = (nextPage) => {
  page.value = nextPage - 1
  fetchCategories()
}

const changeSize = (nextSize) => {
  size.value = nextSize
  page.value = 0
  fetchCategories()
}

const resetForm = () => {
  form.name = ''
  form.parentId = 0
  form.sort = 0
  form.status = 1
}

const openCreate = () => {
  editingId.value = null
  resetForm()
  dialogVisible.value = true
}

const openEdit = (row) => {
  editingId.value = row.id
  form.name = row.name
  form.parentId = row.parentId ?? 0
  form.sort = row.sort ?? 0
  form.status = row.status ?? 1
  dialogVisible.value = true
}

const saveCategory = async () => {
  if (!form.name) {
    ElMessage.warning(t('auth.completeInfo'))
    return
  }
  saving.value = true
  try {
    const payload = {
      name: form.name,
      parentId: form.parentId,
      sort: form.sort,
      status: form.status,
    }
    const res = editingId.value
      ? await updateAdminCategory(editingId.value, payload)
      : await createAdminCategory(payload)
    if (res.code === 200) {
      ElMessage.success(t('common.save'))
      dialogVisible.value = false
      fetchCategories()
    } else {
      ElMessage.error(res.message || t('common.empty'))
    }
  } catch (error) {
    ElMessage.error(t('common.empty'))
  } finally {
    saving.value = false
  }
}

const setStatus = async (row, status) => {
  try {
    const res = status === 1
      ? await enableAdminCategory(row.id)
      : await disableAdminCategory(row.id)
    if (res.code === 200) {
      ElMessage.success(t('common.save'))
      fetchCategories()
    } else {
      ElMessage.error(res.message || t('common.empty'))
    }
  } catch (error) {
    ElMessage.error(t('common.empty'))
  }
}

const removeCategory = async (row) => {
  try {
    await ElMessageBox.confirm(t('admin.deleteConfirm'), 'Confirm', { type: 'warning' })
    const res = await deleteAdminCategory(row.id)
    if (res.code === 200) {
      ElMessage.success(t('common.delete'))
      fetchCategories()
    } else {
      ElMessage.error(res.message || t('common.empty'))
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(t('common.empty'))
    }
  }
}

onMounted(fetchCategories)
</script>
