<template>
  <div class="space-y-6 animate-fade-up">
    <el-card class="border-0 bg-[var(--surface-strong)] shadow-soft">
      <template #header>
        <div class="flex items-center justify-between">
          <div>
            <h2 class="text-2xl font-semibold">{{ t('address.title') }}</h2>
            <p class="muted-text">{{ t('address.manage') }}</p>
          </div>
          <el-button type="primary" @click="openDialog()">{{ t('address.add') }}</el-button>
        </div>
      </template>

      <el-table v-if="addresses.length" :data="addresses" style="width: 100%">
        <el-table-column :label="t('address.receiver')" prop="receiver" width="120" />
        <el-table-column :label="t('address.phone')" prop="phone" width="140" />
        <el-table-column :label="t('address.title')">
          <template #default="{ row }">
            {{ row.province }} {{ row.city }} {{ row.area }} {{ row.detail }}
          </template>
        </el-table-column>
        <el-table-column :label="t('address.default')" width="80">
          <template #default="{ row }">
            <el-tag v-if="row.isDefault === 1" type="success" effect="light">{{ t('address.default') }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column :label="t('common.actions')" width="160">
          <template #default="{ row }">
            <el-button text type="primary" @click="openDialog(row)">{{ t('common.edit') }}</el-button>
            <el-button text type="danger" @click="removeAddress(row)">{{ t('common.delete') }}</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-empty v-else :description="t('address.empty')" />
    </el-card>

    <el-dialog v-model="dialogVisible" :title="t('address.title')" width="520px">
      <el-form :model="form" label-position="top">
        <el-form-item :label="t('address.receiver')">
          <el-input v-model="form.receiver" />
        </el-form-item>
        <el-form-item :label="t('address.phone')">
          <el-input v-model="form.phone" />
        </el-form-item>
        <el-form-item :label="t('address.province')">
          <el-input v-model="form.province" />
        </el-form-item>
        <el-form-item :label="t('address.city')">
          <el-input v-model="form.city" />
        </el-form-item>
        <el-form-item :label="t('address.area')">
          <el-input v-model="form.area" />
        </el-form-item>
        <el-form-item :label="t('address.detail')">
          <el-input v-model="form.detail" />
        </el-form-item>
        <el-form-item>
          <el-checkbox v-model="form.isDefault" :true-label="1" :false-label="0">
            {{ t('address.setDefault') }}
          </el-checkbox>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">{{ t('common.cancel') }}</el-button>
        <el-button type="primary" @click="saveAddress">{{ t('address.save') }}</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { addAddress, deleteAddress, getAddresses, updateAddress } from '@/api/address.js'
import { useI18n } from '@/i18n/index.js'

const addresses = ref([])
const dialogVisible = ref(false)
const editingId = ref(null)
const form = ref({
  receiver: '',
  phone: '',
  province: '',
  city: '',
  area: '',
  detail: '',
  isDefault: 0,
})
const { t } = useI18n()

const loadAddresses = async () => {
  try {
    const res = await getAddresses()
    if (res.code === 200) {
      addresses.value = res.data || []
    } else {
      ElMessage.error(res.message)
    }
  } catch (error) {
    ElMessage.error(t('address.empty'))
  }
}

const openDialog = (row) => {
  if (row) {
    editingId.value = row.id
    form.value = { ...row }
  } else {
    editingId.value = null
    form.value = {
      receiver: '',
      phone: '',
      province: '',
      city: '',
      area: '',
      detail: '',
      isDefault: 0,
    }
  }
  dialogVisible.value = true
}

const saveAddress = async () => {
  try {
    let res
    if (editingId.value) {
      res = await updateAddress(editingId.value, form.value)
    } else {
      res = await addAddress(form.value)
    }
    if (res.code === 200) {
      ElMessage.success(t('address.save'))
      dialogVisible.value = false
      loadAddresses()
    } else {
      ElMessage.error(res.message)
    }
  } catch (error) {
    ElMessage.error(t('address.save'))
  }
}

const removeAddress = async (row) => {
  try {
    const res = await deleteAddress(row.id)
    if (res.code === 200) {
      addresses.value = addresses.value.filter(item => item.id !== row.id)
      ElMessage.success(t('common.delete'))
    } else {
      ElMessage.error(res.message)
    }
  } catch (error) {
    ElMessage.error(t('common.delete'))
  }
}

onMounted(loadAddresses)
</script>

