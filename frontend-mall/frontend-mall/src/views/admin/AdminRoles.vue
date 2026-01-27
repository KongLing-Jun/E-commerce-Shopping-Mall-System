<template>
  <section class="space-y-6">
    <div class="flex flex-wrap items-start justify-between gap-4">
      <div>
        <p class="text-xs uppercase tracking-[0.35em] text-[var(--muted)]">Admin Console</p>
        <h1 class="section-title mt-3">{{ t('admin.rolesTitle') }}</h1>
        <p class="muted-text mt-2">{{ t('admin.rolesSubtitle') }}</p>
      </div>
      <el-button type="primary" size="large" @click="openCreate">{{ t('admin.newRole') }}</el-button>
    </div>

    <el-card class="border-0 bg-[var(--surface)] shadow-soft">
      <div class="flex flex-wrap items-end gap-3">
        <el-input v-model="filters.keyword" :placeholder="t('admin.roleSearch')" clearable class="w-60" />
        <el-button type="primary" @click="fetchRoles">{{ t('common.search') }}</el-button>
        <el-button @click="resetFilters">{{ t('common.reset') }}</el-button>
      </div>

      <el-table :data="roles" class="mt-6" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="90" />
        <el-table-column prop="roleKey" :label="t('admin.roleKey')" min-width="140" />
        <el-table-column prop="roleName" :label="t('admin.roleName')" min-width="180" />
        <el-table-column :label="t('common.actions')" width="280">
          <template #default="{ row }">
            <el-button size="small" v-permission="'admin:roles:edit'" @click="openEdit(row)">
              {{ t('common.edit') }}
            </el-button>
            <el-button size="small" v-permission="'admin:roles:edit'" @click="openMenus(row)">
              {{ t('admin.assignMenus') }}
            </el-button>
            <el-button
              size="small"
              type="danger"
              v-permission="'admin:roles:delete'"
              :disabled="isBuiltIn(row)"
              @click="removeRole(row)"
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

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="520px">
      <el-form :model="form" label-position="top">
        <el-form-item :label="t('admin.roleKey')">
          <el-input v-model="form.roleKey" :disabled="editingBuiltIn" />
        </el-form-item>
        <el-form-item :label="t('admin.roleName')">
          <el-input v-model="form.roleName" />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">{{ t('common.cancel') }}</el-button>
        <el-button type="primary" :loading="saving" @click="saveRole">
          {{ t('common.save') }}
        </el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="menuDialogVisible" :title="t('admin.assignMenus')" width="560px">
      <div class="text-sm text-[var(--muted)]">{{ t('admin.assignMenusHint') }}</div>
      <el-tree
        ref="menuTreeRef"
        class="mt-4"
        :data="menuTree"
        node-key="id"
        show-checkbox
        default-expand-all
        :props="{ label: 'name', children: 'children' }"
        :empty-text="t('common.empty')"
        v-loading="menuLoading"
      />

      <template #footer>
        <el-button @click="menuDialogVisible = false">{{ t('common.cancel') }}</el-button>
        <el-button type="primary" :loading="menuSaving" @click="saveRoleMenus">
          {{ t('common.save') }}
        </el-button>
      </template>
    </el-dialog>
  </section>
</template>

<script setup>
import { computed, nextTick, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  createAdminRole,
  deleteAdminRole,
  fetchAdminRoles,
  fetchRoleMenus,
  updateAdminRole,
  updateRoleMenus,
} from '@/api/admin/roles.js'
import { fetchAdminMenuTree } from '@/api/admin/menus.js'
import { useI18n } from '@/i18n/index.js'

const roles = ref([])
const total = ref(0)
const page = ref(0)
const size = ref(10)
const loading = ref(false)
const saving = ref(false)
const menuLoading = ref(false)
const menuSaving = ref(false)
const menuTree = ref([])
const menuDialogVisible = ref(false)
const menuRoleId = ref(null)
const menuTreeRef = ref(null)

const { t } = useI18n()

const filters = reactive({
  keyword: '',
})

const dialogVisible = ref(false)
const editingId = ref(null)
const form = reactive({
  roleKey: '',
  roleName: '',
})

const dialogTitle = computed(() => (editingId.value ? t('common.edit') : t('admin.newRole')))
const editingBuiltIn = computed(() => {
  if (!editingId.value) {
    return false
  }
  return isBuiltIn({ roleKey: form.roleKey })
})

const isBuiltIn = (row) => {
  return ['ADMIN', 'USER'].includes((row.roleKey || '').toUpperCase())
}

const fetchRoles = async () => {
  loading.value = true
  try {
    const params = {
      keyword: filters.keyword || undefined,
      page: page.value,
      size: size.value,
    }
    const res = await fetchAdminRoles(params)
    if (res.code === 200) {
      roles.value = res.data.content || []
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
  filters.keyword = ''
  page.value = 0
  fetchRoles()
}

const changePage = (nextPage) => {
  page.value = nextPage - 1
  fetchRoles()
}

const changeSize = (nextSize) => {
  size.value = nextSize
  page.value = 0
  fetchRoles()
}

const resetForm = () => {
  form.roleKey = ''
  form.roleName = ''
}

const openCreate = () => {
  editingId.value = null
  resetForm()
  dialogVisible.value = true
}

const openEdit = (row) => {
  editingId.value = row.id
  form.roleKey = row.roleKey
  form.roleName = row.roleName
  dialogVisible.value = true
}

const saveRole = async () => {
  if (!form.roleKey || !form.roleName) {
    ElMessage.warning(t('auth.completeInfo'))
    return
  }
  saving.value = true
  try {
    const payload = {
      roleKey: form.roleKey,
      roleName: form.roleName,
    }
    const res = editingId.value
      ? await updateAdminRole(editingId.value, payload)
      : await createAdminRole(payload)
    if (res.code === 200) {
      ElMessage.success(t('common.save'))
      dialogVisible.value = false
      fetchRoles()
    } else {
      ElMessage.error(res.message || t('common.empty'))
    }
  } catch (error) {
    ElMessage.error(t('common.empty'))
  } finally {
    saving.value = false
  }
}

const removeRole = async (row) => {
  try {
    await ElMessageBox.confirm(t('admin.deleteRoleConfirm'), 'Confirm', { type: 'warning' })
    const res = await deleteAdminRole(row.id)
    if (res.code === 200) {
      ElMessage.success(t('common.delete'))
      fetchRoles()
    } else {
      ElMessage.error(res.message || t('common.empty'))
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(t('common.empty'))
    }
  }
}

const loadMenuTree = async () => {
  if (menuTree.value.length) {
    return
  }
  menuLoading.value = true
  try {
    const res = await fetchAdminMenuTree()
    if (res.code === 200) {
      menuTree.value = res.data || []
    } else {
      ElMessage.error(res.message || t('common.empty'))
    }
  } catch (error) {
    ElMessage.error(t('common.empty'))
  } finally {
    menuLoading.value = false
  }
}

const openMenus = async (row) => {
  menuRoleId.value = row.id
  menuDialogVisible.value = true
  await loadMenuTree()
  nextTick(() => {
    menuTreeRef.value?.setCheckedKeys([])
  })
  try {
    const res = await fetchRoleMenus(row.id)
    if (res.code === 200) {
      const ids = res.data || []
      nextTick(() => {
        menuTreeRef.value?.setCheckedKeys(ids)
      })
    } else {
      ElMessage.error(res.message || t('common.empty'))
    }
  } catch (error) {
    ElMessage.error(t('common.empty'))
  }
}

const saveRoleMenus = async () => {
  const checked = menuTreeRef.value?.getCheckedKeys() || []
  const halfChecked = menuTreeRef.value?.getHalfCheckedKeys() || []
  const menuIds = Array.from(new Set([...checked, ...halfChecked]))
  menuSaving.value = true
  try {
    const res = await updateRoleMenus(menuRoleId.value, { menuIds })
    if (res.code === 200) {
      ElMessage.success(t('common.save'))
      menuDialogVisible.value = false
    } else {
      ElMessage.error(res.message || t('common.empty'))
    }
  } catch (error) {
    ElMessage.error(t('common.empty'))
  } finally {
    menuSaving.value = false
  }
}

onMounted(fetchRoles)
</script>
