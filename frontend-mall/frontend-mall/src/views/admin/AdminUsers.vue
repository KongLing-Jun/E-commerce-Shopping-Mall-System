<template>
  <section class="space-y-6">
    <div class="flex flex-wrap items-start justify-between gap-4">
      <div>
        <h1 class="section-title">{{ t('admin.usersTitle') }}</h1>
        <p class="muted-text mt-2">{{ t('admin.usersSubtitle') }}</p>
      </div>
      <el-button type="primary" size="large" v-permission="'admin:users:create'" @click="openCreateDialog">
        + {{ t('admin.addUser') }}
      </el-button>
    </div>

    <div class="flex flex-wrap items-center gap-3">
      <el-input
        v-model="filters.keyword"
        :placeholder="t('admin.userSearchPlaceholder')"
        clearable
        class="max-w-xl flex-1"
        @keyup.enter="fetchUsers"
      />
      <el-button @click="fetchUsers">{{ t('common.search') }}</el-button>
      <el-button @click="resetFilters">{{ t('common.reset') }}</el-button>
      <el-button @click="exportCsv">{{ t('admin.exportCsv') }}</el-button>
    </div>

    <article class="overflow-hidden rounded-2xl border border-[var(--line)] bg-[var(--surface)]">
      <el-table :data="users" v-loading="loading" stripe>
        <el-table-column :label="t('auth.username')" min-width="260">
          <template #default="{ row }">
            <div class="flex items-center gap-3">
              <div class="grid h-12 w-12 place-content-center rounded-full bg-[var(--highlight)] font-bold text-[var(--accent)]">
                {{ initials(row.username) }}
              </div>
              <div>
                <div class="text-base font-bold">{{ row.username }}</div>
                <div class="text-sm text-[var(--muted)]">ID: {{ row.id }}</div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column :label="t('admin.roleName')" min-width="140">
          <template #default="{ row }">
            <span class="chip">{{ row.roleName || row.roleKey || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="phone" :label="t('auth.phone')" min-width="180" />
        <el-table-column :label="t('common.status')" width="140">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)" effect="light">{{ statusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" :label="t('common.createdAt')" min-width="180" />
        <el-table-column :label="t('common.actions')" width="360">
          <template #default="{ row }">
            <el-button size="small" v-permission="'admin:users:edit'" @click="openEditDialog(row)">
              {{ t('common.edit') }}
            </el-button>
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

      <div class="flex justify-end border-t border-[var(--line)] px-4 py-4">
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
    </article>

    <el-dialog v-model="userDialogVisible" :title="userDialogTitle" width="460px">
      <el-form label-position="top">
        <el-form-item :label="t('auth.username')" required>
          <el-input v-model="userForm.username" />
        </el-form-item>
        <el-form-item :label="t('auth.phone')" required>
          <el-input v-model="userForm.phone" />
        </el-form-item>
        <el-form-item :label="t('admin.roleName')" required>
          <el-select v-model="userForm.roleId" class="w-full">
            <el-option v-for="role in roles" :key="role.id" :label="role.roleName" :value="role.id" />
          </el-select>
        </el-form-item>
        <el-form-item :label="t('common.status')">
          <el-select v-model="userForm.status" class="w-full">
            <el-option :label="t('admin.statusEnabled')" :value="1" />
            <el-option :label="t('admin.statusDisabled')" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item :label="passwordLabel">
          <el-input v-model="userForm.password" show-password type="password" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="userDialogVisible = false">{{ t('common.cancel') }}</el-button>
        <el-button type="primary" :loading="userSaving" @click="saveUser">{{ t('common.save') }}</el-button>
      </template>
    </el-dialog>

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
        <el-button type="primary" :loading="roleSaving" @click="saveUserRole">{{ t('common.save') }}</el-button>
      </template>
    </el-dialog>
  </section>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  createAdminUser,
  disableAdminUser,
  fetchAdminUsers,
  resetAdminUserPassword,
  updateAdminUser,
  updateAdminUserRole,
} from '@/api/admin/users.js'
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
const userDialogVisible = ref(false)
const userDialogMode = ref('create')
const userSaving = ref(false)
const userForm = reactive({
  id: null,
  username: '',
  phone: '',
  roleId: null,
  status: 1,
  password: '',
})
const { t } = useI18n()

const filters = reactive({
  keyword: '',
})

const userDialogTitle = computed(() => (userDialogMode.value === 'create' ? t('admin.addUser') : t('admin.editUser')))
const passwordLabel = computed(() => (
  userDialogMode.value === 'create' ? t('auth.password') : t('admin.newPasswordOptional')
))

const initials = (name) => (name || '?').slice(0, 2).toUpperCase()
const statusType = (status) => (status === 1 ? 'success' : 'danger')
const statusText = (status) => (status === 1 ? t('admin.statusEnabled') : t('admin.statusDisabled'))

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
  } catch {
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
  } catch {
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

const openCreateDialog = () => {
  userDialogMode.value = 'create'
  userForm.id = null
  userForm.username = ''
  userForm.phone = ''
  userForm.roleId = null
  userForm.status = 1
  userForm.password = ''
  userDialogVisible.value = true
}

const openEditDialog = (row) => {
  userDialogMode.value = 'edit'
  userForm.id = row.id
  userForm.username = row.username
  userForm.phone = row.phone
  userForm.roleId = row.roleId
  userForm.status = row.status ?? 1
  userForm.password = ''
  userDialogVisible.value = true
}

const saveUser = async () => {
  if (!userForm.username || !userForm.phone || !userForm.roleId) {
    ElMessage.warning(t('auth.completeInfo'))
    return
  }
  if (userDialogMode.value === 'create' && !userForm.password) {
    ElMessage.warning(t('auth.completeInfo'))
    return
  }

  const payload = {
    username: userForm.username,
    phone: userForm.phone,
    roleId: userForm.roleId,
    status: userForm.status,
    password: userForm.password || undefined,
  }

  userSaving.value = true
  try {
    const res = userDialogMode.value === 'create'
      ? await createAdminUser(payload)
      : await updateAdminUser(userForm.id, payload)

    if (res.code === 200) {
      ElMessage.success(t('common.save'))
      userDialogVisible.value = false
      fetchUsers()
    } else {
      ElMessage.error(res.message || t('common.empty'))
    }
  } catch {
    ElMessage.error(t('common.empty'))
  } finally {
    userSaving.value = false
  }
}

const disable = async (row) => {
  try {
    await ElMessageBox.confirm(t('admin.disableConfirm'), t('common.actions'), { type: 'warning' })
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
    await ElMessageBox.confirm(t('admin.resetConfirm'), t('common.actions'), { type: 'warning' })
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
  } catch {
    ElMessage.error(t('common.empty'))
  } finally {
    roleSaving.value = false
  }
}

const exportCsv = () => {
  if (!users.value.length) {
    ElMessage.info(t('common.empty'))
    return
  }
  const header = ['id', 'username', 'phone', 'role', 'status', 'createdAt']
  const lines = users.value.map((user) => [
    user.id,
    user.username,
    user.phone,
    user.roleName || user.roleKey,
    user.status,
    user.createdAt,
  ])
  const csv = [header.join(','), ...lines.map((line) => line.join(','))].join('\n')
  const blob = new Blob([csv], { type: 'text/csv;charset=utf-8;' })
  const url = window.URL.createObjectURL(blob)
  const link = document.createElement('a')
  link.href = url
  link.download = `users-${new Date().toISOString().slice(0, 10)}.csv`
  link.click()
  window.URL.revokeObjectURL(url)
}

onMounted(() => {
  fetchUsers()
  fetchRoles()
})
</script>
