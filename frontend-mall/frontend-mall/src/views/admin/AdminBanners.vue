<template>
  <section class="space-y-6">
    <div class="flex flex-wrap items-start justify-between gap-4">
      <div>
        <p class="text-xs uppercase tracking-[0.35em] text-[var(--muted)]">Admin Console</p>
        <h1 class="section-title mt-3">{{ t('admin.bannerTitle') }}</h1>
        <p class="muted-text mt-2">{{ t('admin.bannerSubtitle') }}</p>
      </div>
      <el-button type="primary" size="large" v-permission="'admin:banners:edit'" @click="openCreate">
        {{ t('admin.newBanner') }}
      </el-button>
    </div>

    <el-card class="border-0 bg-[var(--surface)] shadow-soft">
      <div class="flex flex-wrap items-end gap-3">
        <el-select v-model="filters.status" :placeholder="t('common.status')" clearable class="w-36">
          <el-option :label="t('admin.statusEnabled')" :value="1" />
          <el-option :label="t('admin.statusDisabled')" :value="0" />
        </el-select>
        <el-button type="primary" @click="fetchBanners">{{ t('common.search') }}</el-button>
        <el-button @click="resetFilters">{{ t('common.reset') }}</el-button>
      </div>

      <el-table :data="banners" class="mt-6" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column :label="t('admin.imageUrl')" width="140">
          <template #default="{ row }">
            <el-image :src="row.imageUrl" class="h-16 w-28 rounded-xl" fit="cover" />
          </template>
        </el-table-column>
        <el-table-column prop="linkType" :label="t('admin.linkType')" width="120" />
        <el-table-column prop="linkTarget" :label="t('admin.linkTarget')" min-width="200" />
        <el-table-column prop="sort" :label="t('admin.sort')" width="90" />
        <el-table-column :label="t('common.status')" width="120">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? t('admin.statusEnabled') : t('admin.statusDisabled') }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column :label="t('common.actions')" width="200">
          <template #default="{ row }">
            <el-button size="small" v-permission="'admin:banners:edit'" @click="openEdit(row)">
              {{ t('common.edit') }}
            </el-button>
            <el-button size="small" type="danger" v-permission="'admin:banners:edit'" @click="remove(row)">
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
        <el-form-item :label="t('admin.imageUrl')">
          <el-input v-model="form.imageUrl" />
          <div class="mt-2 flex items-center gap-3">
            <el-upload :show-file-list="false" :http-request="handleUpload">
              <el-button size="small">{{ t('admin.upload') }}</el-button>
            </el-upload>
            <el-image v-if="form.imageUrl" :src="form.imageUrl" class="h-12 w-20 rounded-lg" fit="cover" />
          </div>
        </el-form-item>
        <el-form-item :label="t('admin.linkType')">
          <el-select v-model="form.linkType" class="w-full">
            <el-option label="PRODUCT" value="PRODUCT" />
            <el-option label="URL" value="URL" />
          </el-select>
        </el-form-item>
        <el-form-item :label="t('admin.linkTarget')">
          <el-input v-model="form.linkTarget" />
        </el-form-item>
        <div class="grid gap-4 md:grid-cols-2">
          <el-form-item :label="t('admin.sort')">
            <el-input-number v-model="form.sort" :min="0" class="w-full" />
          </el-form-item>
          <el-form-item :label="t('common.status')">
            <el-select v-model="form.status" class="w-full">
              <el-option :label="t('admin.statusEnabled')" :value="1" />
              <el-option :label="t('admin.statusDisabled')" :value="0" />
            </el-select>
          </el-form-item>
        </div>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">{{ t('common.cancel') }}</el-button>
        <el-button
          type="primary"
          :loading="saving"
          v-permission="'admin:banners:edit'"
          @click="saveBanner"
        >
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
  createAdminBanner,
  deleteAdminBanner,
  fetchAdminBanners,
  updateAdminBanner,
} from '@/api/admin/banners.js'
import { uploadAdminFile } from '@/api/admin/upload.js'
import { useI18n } from '@/i18n/index.js'

const banners = ref([])
const total = ref(0)
const page = ref(0)
const size = ref(10)
const loading = ref(false)
const saving = ref(false)
const { t } = useI18n()

const filters = reactive({
  status: '',
})

const dialogVisible = ref(false)
const editingId = ref(null)
const form = reactive({
  imageUrl: '',
  linkType: 'PRODUCT',
  linkTarget: '',
  sort: 0,
  status: 1,
})

const dialogTitle = computed(() => (editingId.value ? t('common.edit') : t('admin.newBanner')))

const fetchBanners = async () => {
  loading.value = true
  try {
    const params = {
      status: filters.status === '' ? undefined : filters.status,
      page: page.value,
      size: size.value,
    }
    const res = await fetchAdminBanners(params)
    if (res.code === 200) {
      banners.value = res.data.content || []
      total.value = res.data.totalElements || 0
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
  filters.status = ''
  page.value = 0
  fetchBanners()
}

const changePage = (nextPage) => {
  page.value = nextPage - 1
  fetchBanners()
}

const changeSize = (nextSize) => {
  size.value = nextSize
  page.value = 0
  fetchBanners()
}

const resetForm = () => {
  form.imageUrl = ''
  form.linkType = 'PRODUCT'
  form.linkTarget = ''
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
  form.imageUrl = row.imageUrl
  form.linkType = row.linkType
  form.linkTarget = row.linkTarget
  form.sort = row.sort
  form.status = row.status
  dialogVisible.value = true
}

const saveBanner = async () => {
  if (!form.imageUrl || !form.linkTarget) {
    ElMessage.warning(t('auth.completeInfo'))
    return
  }
  saving.value = true
  try {
    const payload = {
      imageUrl: form.imageUrl,
      linkType: form.linkType,
      linkTarget: form.linkTarget,
      sort: form.sort,
      status: form.status,
    }
    const res = editingId.value
      ? await updateAdminBanner(editingId.value, payload)
      : await createAdminBanner(payload)
    if (res.code === 200) {
      ElMessage.success(t('common.save'))
      dialogVisible.value = false
      fetchBanners()
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
      form.imageUrl = res.data.url
      ElMessage.success(t('admin.uploadSuccess'))
    } else {
      ElMessage.error(res.message || t('admin.uploadFail'))
    }
  } catch (error) {
    ElMessage.error(t('admin.uploadFail'))
  }
}

const remove = async (row) => {
  try {
    await ElMessageBox.confirm(t('admin.deleteConfirm'), 'Confirm', { type: 'warning' })
    const res = await deleteAdminBanner(row.id)
    if (res.code === 200) {
      ElMessage.success(t('common.delete'))
      fetchBanners()
    } else {
      ElMessage.error(res.message || t('common.empty'))
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(t('common.empty'))
    }
  }
}

onMounted(fetchBanners)
</script>

