<template>
  <div class="relative flex min-h-screen items-center justify-center overflow-hidden px-4 py-10">
    <div class="absolute inset-0 bg-[url('https://images.unsplash.com/photo-1517048676732-d65bc937f952?auto=format&fit=crop&w=1920&q=80')] bg-cover bg-center"></div>
    <div class="absolute inset-0 bg-slate-900/45 backdrop-blur-[3px]"></div>

    <div class="relative z-10 w-full max-w-[560px] rounded-3xl border border-white/40 bg-white/92 shadow-2xl backdrop-blur-md">
      <div class="border-b border-slate-200/80 px-10 pb-8 pt-10 text-center">
        <div class="mx-auto grid h-16 w-16 place-content-center rounded-full bg-blue-100 text-3xl font-bold text-blue-600">RG</div>
        <h1 class="mt-6 text-5xl font-extrabold tracking-tight text-slate-900">{{ t('auth.registerTitle') }}</h1>
        <p class="mt-3 text-lg text-slate-500">{{ t('auth.registerSubtitle') }}</p>
      </div>

      <el-form :model="form" label-position="top" class="space-y-1 px-10 pb-8 pt-7" @keyup.enter="handleRegister">
        <el-form-item :label="t('auth.username')">
          <el-input v-model="form.username" size="large" :placeholder="t('auth.username')" />
        </el-form-item>

        <el-form-item :label="t('auth.phone')">
          <el-input v-model="form.phone" size="large" :placeholder="t('auth.phone')" />
        </el-form-item>

        <el-form-item :label="t('auth.password')">
          <el-input v-model="form.password" size="large" show-password type="password" :placeholder="t('auth.password')" />
        </el-form-item>

        <el-form-item :label="t('auth.confirmPassword')">
          <el-input
            v-model="form.confirmPassword"
            size="large"
            show-password
            type="password"
            :placeholder="t('auth.confirmPassword')"
          />
        </el-form-item>

        <el-button class="mt-2 w-full" type="primary" size="large" :loading="loading" @click="handleRegister">
          {{ t('auth.register') }}
        </el-button>
      </el-form>

      <div class="border-t border-slate-200/80 px-10 py-5 text-center text-sm text-slate-500">
        {{ t('auth.hasAccount') }}
        <el-link type="primary" @click="router.push('/login')">{{ t('auth.toLogin') }}</el-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { register } from '@/api/auth.js'
import { useI18n } from '@/i18n/index.js'

const router = useRouter()
const loading = ref(false)
const form = ref({
  username: '',
  phone: '',
  password: '',
  confirmPassword: '',
})
const { t } = useI18n()

const handleRegister = async () => {
  if (!form.value.username || !form.value.phone || !form.value.password || !form.value.confirmPassword) {
    ElMessage.warning(t('auth.completeInfo'))
    return
  }
  if (form.value.password !== form.value.confirmPassword) {
    ElMessage.warning(t('auth.confirmPassword'))
    return
  }

  loading.value = true
  try {
    const res = await register(form.value)
    if (res.code === 200) {
      ElMessage.success(t('auth.registerSuccess'))
      router.push('/login')
    } else {
      ElMessage.error(res.message)
    }
  } catch {
    ElMessage.error(t('auth.registerFail'))
  } finally {
    loading.value = false
  }
}
</script>
