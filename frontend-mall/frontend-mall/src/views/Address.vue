<template>
  <div class="space-y-6 animate-fade-up">
    <el-card class="border-0 bg-white/85 shadow-soft">
      <template #header>
        <div class="flex items-center justify-between">
          <div>
            <h2 class="text-2xl font-semibold">收货地址</h2>
            <p class="muted-text">管理收货人信息</p>
          </div>
          <el-button type="primary" @click="openDialog()">新增地址</el-button>
        </div>
      </template>

      <el-table v-if="addresses.length" :data="addresses" style="width: 100%">
        <el-table-column label="收货人" prop="receiver" width="120" />
        <el-table-column label="手机号" prop="phone" width="140" />
        <el-table-column label="地址">
          <template #default="{ row }">
            {{ row.province }} {{ row.city }} {{ row.area }} {{ row.detail }}
          </template>
        </el-table-column>
        <el-table-column label="默认" width="80">
          <template #default="{ row }">
            <el-tag v-if="row.isDefault === 1" type="success" effect="light">默认</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160">
          <template #default="{ row }">
            <el-button text type="primary" @click="openDialog(row)">编辑</el-button>
            <el-button text type="danger" @click="removeAddress(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-empty v-else description="暂无地址" />
    </el-card>

    <el-dialog v-model="dialogVisible" title="地址信息" width="520px">
      <el-form :model="form" label-position="top">
        <el-form-item label="收货人">
          <el-input v-model="form.receiver" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="form.phone" />
        </el-form-item>
        <el-form-item label="省份">
          <el-input v-model="form.province" />
        </el-form-item>
        <el-form-item label="城市">
          <el-input v-model="form.city" />
        </el-form-item>
        <el-form-item label="区县">
          <el-input v-model="form.area" />
        </el-form-item>
        <el-form-item label="详细地址">
          <el-input v-model="form.detail" />
        </el-form-item>
        <el-form-item>
          <el-checkbox v-model="form.isDefault" :true-label="1" :false-label="0">设为默认</el-checkbox>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveAddress">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { addAddress, deleteAddress, getAddresses, updateAddress } from '@/api/address.js'

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

const loadAddresses = async () => {
  try {
    const res = await getAddresses()
    if (res.code === 200) {
      addresses.value = res.data || []
    } else {
      ElMessage.error(res.message)
    }
  } catch (error) {
    ElMessage.error('加载地址失败')
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
      ElMessage.success('保存成功')
      dialogVisible.value = false
      loadAddresses()
    } else {
      ElMessage.error(res.message)
    }
  } catch (error) {
    ElMessage.error('保存失败')
  }
}

const removeAddress = async (row) => {
  try {
    const res = await deleteAddress(row.id)
    if (res.code === 200) {
      addresses.value = addresses.value.filter(item => item.id !== row.id)
      ElMessage.success('已删除')
    } else {
      ElMessage.error(res.message)
    }
  } catch (error) {
    ElMessage.error('删除失败')
  }
}

onMounted(() => {
  loadAddresses()
})
</script>
