<template>
  <section class="space-y-6">
    <div>
      <p class="text-xs uppercase tracking-[0.35em] text-[var(--muted)]">Admin Console</p>
      <h1 class="section-title mt-3">{{ t('admin.usersTitle') }}</h1>
      <p class="muted-text mt-2">{{ t('admin.usersSubtitle') }}</p>
    </div>

    <el-card class="border-0 bg-[var(--surface)] shadow-soft">
      <div class="flex flex-wrap items-end gap-3">
        <el-input v-model="filters.keyword" :placeholder="t('productList.searchPlaceholder')" clearable class="w-60" />
        <el-button type="primary" @click="fetchUsers">{{ t('common.search') }}</el-button>
        <el-button @click="resetFilters">{{ t('common.reset') }}</el-button>
      </div>

      <el-table :data="users" class="mt-6" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="90" />
        <el-table-column prop="username" :label="t('auth.username')" min-width="140" />
        <el-table-column prop="phone" :label="t('auth.phone')" min-width="140" />
        <el-table-column prop="roleName" :label="t('admin.roleName')" min-width="160">
          <template #default="{ row }">
            <span>{{ row.roleName || row.roleKey || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column :label="t('common.status')" width="120">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? t('admin.statusEnabled') : t('admin.statusDisabled') }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" :label="t('common.createdAt')" min-width="160" />
        <el-table-column :label="t('common.actions')" width="280">
          <template #default="{ row }">
            <el-button
              size="small"
              type="warning"
              :disabled="row.status === 0"
              v-permission="'admin:users:disable'"
              @click="disable(row)"
            >
              {{ t('admin.disable') }}
            </el-button>
            <el-button size="small" v-permission="'admin:users:reset'" @click="resetPassword(row)">
              {{ t('admin.resetPassword') }}
            </el-button>
            <el-button size="small" v-permission="'admin:users:role'" @click="openRoleDialog(row)">
              {{ t('admin.assignRole') }}
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

    <el-dialog v-model="roleDialogVisible" :title="t('admin.assignRole')" width="420px">
      <el-form label-position="top">
        <el-form-item :label="t('admin.roleName')">
          <el-select v-model="roleForm.roleId" class="w-full">
            <el-option v-for="role in roles" :key="role.id" :label="role.roleName" :value="role.id" />
          </el-select>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="roleDialogVisible = false">{{ t('common.cancel') }}</el-button>
        <el-button type="primary" :loading="roleSaving" @click="saveUserRole">
          {{ t('common.save') }}
        </el-button>
      </template>
    </el-dialog>
  </section>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { disableAdminUser, fetchAdminUsers, resetAdminUserPassword, updateAdminUserRole } from '@/api/admin/users.js'
import { fetchAdminRoles } from '@/api/admin/roles.js'
import { useI18n } from '@/i18n/index.js'

const users = ref([])
const total = ref(0)
const page = ref(0)
const size = ref(10)
const loading = ref(false)
const roles = ref([])
const roleDialogVisible = ref(false)
const roleSaving = ref(false)
const roleForm = reactive({
  userId: null,
  roleId: null,
})
const { t } = useI18n()

const filters = reactive({
  keyword: '',
})

const fetchUsers = async () => {
  loading.value = true
  try {
    const params = {
      keyword: filters.keyword || undefined,
      page: page.value,
      size: size.value,
    }
    const res = await fetchAdminUsers(params)
    if (res.code === 200) {
      users.value = res.data.content || []
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

const fetchRoles = async () => {
  try {
    const res = await fetchAdminRoles({ page: 0, size: 200 })
    if (res.code === 200) {
      roles.value = res.data.content || []
    }
  } catch (error) {
    // ignore
  }
}

const resetFilters = () => {
  filters.keyword = ''
  page.value = 0
  fetchUsers()
}

const changePage = (nextPage) => {
  page.value = nextPage - 1
  fetchUsers()
}

const changeSize = (nextSize) => {
  size.value = nextSize
  page.value = 0
  fetchUsers()
}

const disable = async (row) => {
  try {
    await ElMessageBox.confirm(t('admin.disableConfirm'), 'Confirm', { type: 'warning' })
    const res = await disableAdminUser(row.id)
    if (res.code === 200) {
      ElMessage.success(t('admin.disable'))
      fetchUsers()
    } else {
      ElMessage.error(res.message || t('common.empty'))
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(t('common.empty'))
    }
  }
}

const resetPassword = async (row) => {
  try {
    await ElMessageBox.confirm(t('admin.resetConfirm'), 'Confirm', { type: 'warning' })
    const res = await resetAdminUserPassword(row.id)
    if (res.code === 200) {
      ElMessage.success(t('admin.resetPassword'))
    } else {
      ElMessage.error(res.message || t('common.empty'))
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(t('common.empty'))
    }
  }
}

const openRoleDialog = (row) => {
  roleForm.userId = row.id
  roleForm.roleId = row.roleId
  roleDialogVisible.value = true
}

const saveUserRole = async () => {
  if (!roleForm.userId || !roleForm.roleId) {
    ElMessage.warning(t('auth.completeInfo'))
    return
  }
  roleSaving.value = true
  try {
    const res = await updateAdminUserRole(roleForm.userId, { roleId: roleForm.roleId })
    if (res.code === 200) {
      ElMessage.success(t('common.save'))
      roleDialogVisible.value = false
      fetchUsers()
    } else {
      ElMessage.error(res.message || t('common.empty'))
    }
  } catch (error) {
    ElMessage.error(t('common.empty'))
  } finally {
    roleSaving.value = false
  }
}

onMounted(() => {
  fetchUsers()
  fetchRoles()
})
</script>

