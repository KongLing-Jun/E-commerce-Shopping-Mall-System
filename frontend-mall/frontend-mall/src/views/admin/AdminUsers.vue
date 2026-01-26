<template>
  <section class="space-y-6">
    <div>
      <p class="text-xs uppercase tracking-[0.35em] text-[var(--muted)]">Admin Console</p>
      <h1 class="section-title mt-3">User Management</h1>
      <p class="muted-text mt-2">Search, disable, or reset passwords.</p>
    </div>

    <el-card class="border-0 bg-white/80 shadow-soft">
      <div class="flex flex-wrap items-end gap-3">
        <el-input v-model="filters.keyword" placeholder="Username or phone" clearable class="w-60" />
        <el-button type="primary" @click="fetchUsers">Search</el-button>
        <el-button @click="resetFilters">Reset</el-button>
      </div>

      <el-table :data="users" class="mt-6" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="90" />
        <el-table-column prop="username" label="Username" min-width="140" />
        <el-table-column prop="phone" label="Phone" min-width="140" />
        <el-table-column prop="roleKey" label="Role" width="120" />
        <el-table-column label="Status" width="120">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? 'Active' : 'Disabled' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="Created" min-width="160" />
        <el-table-column label="Actions" width="220">
          <template #default="{ row }">
            <el-button
              size="small"
              type="warning"
              :disabled="row.status === 0"
              @click="disable(row)"
            >
              Disable
            </el-button>
            <el-button size="small" @click="resetPassword(row)">Reset</el-button>
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
import { disableAdminUser, fetchAdminUsers, resetAdminUserPassword } from '@/api/admin/users.js'

const users = ref([])
const total = ref(0)
const page = ref(0)
const size = ref(10)
const loading = ref(false)

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
      ElMessage.error(res.message || 'Failed to load users')
    }
  } catch (error) {
    ElMessage.error('Failed to load users')
  } finally {
    loading.value = false
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
    await ElMessageBox.confirm('Disable this user?', 'Confirm', { type: 'warning' })
    const res = await disableAdminUser(row.id)
    if (res.code === 200) {
      ElMessage.success('User disabled')
      fetchUsers()
    } else {
      ElMessage.error(res.message || 'Disable failed')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('Disable failed')
    }
  }
}

const resetPassword = async (row) => {
  try {
    await ElMessageBox.confirm('Reset password to default (123456)?', 'Confirm', { type: 'warning' })
    const res = await resetAdminUserPassword(row.id)
    if (res.code === 200) {
      ElMessage.success('Password reset')
    } else {
      ElMessage.error(res.message || 'Reset failed')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('Reset failed')
    }
  }
}

onMounted(fetchUsers)
</script>
