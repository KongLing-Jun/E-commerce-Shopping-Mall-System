<template>
  <section class="space-y-6">
    <div class="flex flex-wrap items-start justify-between gap-4">
      <div>
        <p class="text-xs uppercase tracking-[0.35em] text-[var(--muted)]">Admin Console</p>
        <h1 class="section-title mt-3">{{ t('admin.menusTitle') }}</h1>
        <p class="muted-text mt-2">{{ t('admin.menusSubtitle') }}</p>
      </div>
      <el-button type="primary" size="large" @click="openCreate">
        {{ t('common.create') }}
      </el-button>
    </div>

    <el-card class="border-0 bg-[var(--surface)] shadow-soft">
      <el-table
        :data="menus"
        row-key="id"
        default-expand-all
        :tree-props="{ children: 'children' }"
        v-loading="loading"
        stripe
      >
        <el-table-column prop="name" :label="t('admin.menuName')" min-width="200" />
        <el-table-column prop="type" :label="t('admin.menuType')" width="120" />
        <el-table-column prop="path" :label="t('admin.menuPath')" min-width="180" />
        <el-table-column prop="component" :label="t('admin.menuComponent')" min-width="160" />
        <el-table-column prop="permCode" :label="t('admin.permCode')" min-width="160" />
        <el-table-column prop="sort" :label="t('admin.sort')" width="90" />
        <el-table-column :label="t('admin.visible')" width="100">
          <template #default="{ row }">
            <el-tag :type="row.visible === 1 ? 'success' : 'info'">
              {{ row.visible === 1 ? t('common.yes') : t('common.no') }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column :label="t('common.actions')" width="220">
          <template #default="{ row }">
            <el-button size="small" @click="openEdit(row)">{{ t('common.edit') }}</el-button>
            <el-button size="small" @click="openChild(row)">{{ t('common.create') }}</el-button>
            <el-button size="small" type="danger" @click="remove(row)">{{ t('common.delete') }}</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="560px">
      <el-form :model="form" label-position="top">
        <el-form-item :label="t('admin.parentMenu')">
          <el-select v-model="form.parentId" class="w-full">
            <el-option :value="0" :label="dual('???', 'Root')" />
            <el-option
              v-for="option in menuOptions"
              :key="option.id"
              :value="option.id"
              :label="option.label"
            />
          </el-select>
        </el-form-item>
        <el-form-item :label="t('admin.menuName')">
          <el-input v-model="form.name" />
        </el-form-item>
        <div class="grid gap-4 md:grid-cols-2">
          <el-form-item :label="t('admin.menuType')">
            <el-select v-model="form.type" class="w-full">
              <el-option :label="t('admin.menuTypeMenu')" value="MENU" />
              <el-option :label="t('admin.menuTypeButton')" value="BUTTON" />
            </el-select>
          </el-form-item>
          <el-form-item :label="t('admin.sort')">
            <el-input-number v-model="form.sort" :min="0" class="w-full" />
          </el-form-item>
        </div>
        <el-form-item :label="t('admin.menuPath')">
          <el-input v-model="form.path" />
        </el-form-item>
        <el-form-item :label="t('admin.menuComponent')">
          <el-input v-model="form.component" />
        </el-form-item>
        <el-form-item :label="t('admin.permCode')">
          <el-input v-model="form.permCode" />
        </el-form-item>
        <el-form-item :label="t('admin.visible')">
          <el-select v-model="form.visible" class="w-full">
            <el-option :label="t('common.yes')" :value="1" />
            <el-option :label="t('common.no')" :value="0" />
          </el-select>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">{{ t('common.cancel') }}</el-button>
        <el-button type="primary" :loading="saving" @click="saveMenu">{{ t('common.save') }}</el-button>
      </template>
    </el-dialog>
  </section>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  createAdminMenu,
  deleteAdminMenu,
  fetchAdminMenuTree,
  updateAdminMenu,
} from '@/api/admin/menus.js'
import { useI18n } from '@/i18n/index.js'

const menus = ref([])
const loading = ref(false)
const saving = ref(false)
const dialogVisible = ref(false)
const editingId = ref(null)
const { t, locale } = useI18n()
const dual = (zh, en) => (locale.value === 'zh' ? zh : en)

const form = reactive({
  parentId: 0,
  name: '',
  type: 'MENU',
  path: '',
  component: '',
  permCode: '',
  sort: 0,
  visible: 1,
})

const dialogTitle = computed(() => (editingId.value ? t('common.edit') : t('common.create')))

const flattenMenus = (items, level = 0, result = []) => {
  items.forEach((item) => {
    result.push({
      id: item.id,
      label: `${'--'.repeat(level)} ${item.name}`.trim(),
    })
    if (item.children && item.children.length) {
      flattenMenus(item.children, level + 1, result)
    }
  })
  return result
}

const menuOptions = computed(() => flattenMenus(menus.value))

const fetchMenus = async () => {
  loading.value = true
  try {
    const res = await fetchAdminMenuTree()
    if (res.code === 200) {
      menus.value = res.data || []
    } else {
      ElMessage.error(res.message || t('common.empty'))
    }
  } catch {
    ElMessage.error(t('common.empty'))
  } finally {
    loading.value = false
  }
}

const resetForm = () => {
  form.parentId = 0
  form.name = ''
  form.type = 'MENU'
  form.path = ''
  form.component = ''
  form.permCode = ''
  form.sort = 0
  form.visible = 1
}

const openCreate = () => {
  editingId.value = null
  resetForm()
  dialogVisible.value = true
}

const openEdit = (row) => {
  editingId.value = row.id
  form.parentId = row.parentId ?? 0
  form.name = row.name || ''
  form.type = row.type || 'MENU'
  form.path = row.path || ''
  form.component = row.component || ''
  form.permCode = row.permCode || ''
  form.sort = row.sort || 0
  form.visible = row.visible ?? 1
  dialogVisible.value = true
}

const openChild = (row) => {
  editingId.value = null
  resetForm()
  form.parentId = row.id
  dialogVisible.value = true
}

const saveMenu = async () => {
  if (!form.name || !form.type) {
    ElMessage.warning(t('auth.completeInfo'))
    return
  }
  if (form.type === 'MENU' && !form.path) {
    ElMessage.warning(t('admin.menuPathRequired'))
    return
  }
  saving.value = true
  try {
    const payload = {
      parentId: form.parentId,
      name: form.name,
      type: form.type,
      path: form.path || null,
      component: form.component || null,
      permCode: form.permCode || null,
      sort: form.sort,
      visible: form.visible,
    }
    const res = editingId.value
      ? await updateAdminMenu(editingId.value, payload)
      : await createAdminMenu(payload)
    if (res.code === 200) {
      ElMessage.success(t('common.save'))
      dialogVisible.value = false
      fetchMenus()
    } else {
      ElMessage.error(res.message || t('common.empty'))
    }
  } catch {
    ElMessage.error(t('common.empty'))
  } finally {
    saving.value = false
  }
}

const remove = async (row) => {
  try {
    await ElMessageBox.confirm(t('admin.deleteConfirm'), 'Confirm', { type: 'warning' })
    const res = await deleteAdminMenu(row.id)
    if (res.code === 200) {
      ElMessage.success(t('common.delete'))
      fetchMenus()
    } else {
      ElMessage.error(res.message || t('common.empty'))
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(t('common.empty'))
    }
  }
}

onMounted(fetchMenus)
</script>
