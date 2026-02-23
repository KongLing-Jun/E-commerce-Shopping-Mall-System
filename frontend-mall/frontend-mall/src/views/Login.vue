<template>
  <div class="relative flex min-h-screen items-center justify-center overflow-hidden px-4 py-10">
    <div class="absolute inset-0 bg-[url('https://images.unsplash.com/photo-1497366754035-f200968a6e72?auto=format&fit=crop&w=1920&q=80')] bg-cover bg-center"></div>
    <div class="absolute inset-0 bg-slate-900/45 backdrop-blur-[3px]"></div>

    <div class="relative z-10 w-full max-w-[520px] space-y-5">
      <div class="rounded-3xl border border-white/40 bg-white/92 shadow-2xl backdrop-blur-md">
        <div class="border-b border-slate-200/80 px-10 pb-8 pt-10 text-center">
          <div class="mx-auto grid h-16 w-16 place-content-center rounded-full bg-blue-100 text-3xl font-bold text-blue-600">AP</div>
          <h1 class="mt-6 text-5xl font-extrabold tracking-tight text-slate-900">{{ t('auth.adminPortal') }}</h1>
          <p class="mt-3 text-lg text-slate-500">{{ t('auth.loginSubtitle') }}</p>
        </div>

        <el-form :model="form" label-position="top" class="space-y-2 px-10 pb-8 pt-8" @keyup.enter="handleLogin">
          <el-form-item :label="t('auth.username')">
            <el-input v-model="form.username" size="large" :placeholder="t('auth.username')" />
          </el-form-item>

          <el-form-item :label="t('auth.password')">
            <el-input
              v-model="form.password"
              size="large"
              type="password"
              show-password
              :placeholder="t('auth.password')"
            />
          </el-form-item>

          <div class="flex items-center justify-between pb-1 pt-1 text-sm">
            <el-checkbox v-model="remember">{{ t('auth.rememberMe') }}</el-checkbox>
            <button class="text-blue-600 hover:text-blue-700" type="button" @click="handleForgotPassword">
              {{ t('auth.forgotPassword') }}
            </button>
          </div>

          <el-button class="mt-2 w-full" size="large" type="primary" :loading="loading" @click="handleLogin">
            {{ t('auth.login') }}
          </el-button>
        </el-form>

        <div class="border-t border-slate-200/80 px-10 py-5 text-center text-sm text-slate-500">
          {{ t('auth.secureBy') }}
        </div>
      </div>
      <div class="text-center text-white/90">
        <button type="button" class="text-base hover:text-white" @click="router.push('/')">{{ t('auth.backToStore') }}</button>
        <div class="mt-2 text-sm">
          {{ t('auth.noAccount') }}
          <el-link class="!text-white" @click="router.push('/register')">{{ t('auth.toRegister') }}</el-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { login } from '@/api/auth.js'
import { useAuth } from '@/composables/useAuth.js'
import { useI18n } from '@/i18n/index.js'

const router = useRouter()
const loading = ref(false)
const remember = ref(false)
const form = ref({
  username: '',
  password: '',
})
const { refreshAuth } = useAuth()
const { t } = useI18n()

onMounted(() => {
  const rememberedUsername = localStorage.getItem('rememberedUsername')
  if (rememberedUsername) {
    form.value.username = rememberedUsername
    remember.value = true
  }
})

const handleForgotPassword = () => {
  ElMessage.info(t('auth.contactAdminReset'))
}

const handleLogin = async () => {
  if (!form.value.username || !form.value.password) {
    ElMessage.warning(t('auth.completeInfo'))
    return
  }

  loading.value = true
  try {
    const res = await login(form.value)
    if (res.code === 200) {
      localStorage.setItem('token', res.data.token)
      localStorage.setItem('userId', res.data.userId)
      localStorage.setItem('roleKey', res.data.roleKey)
      localStorage.setItem('menus', JSON.stringify(res.data.menus || []))
      localStorage.setItem('perms', JSON.stringify(res.data.perms || []))
      if (remember.value) {
        localStorage.setItem('rememberedUsername', form.value.username)
      } else {
        localStorage.removeItem('rememberedUsername')
      }
      refreshAuth()
      window.dispatchEvent(new Event('auth-changed'))
      ElMessage.success(t('auth.loginSuccess'))
      router.push(res.data.roleKey === 'ADMIN' ? '/admin' : '/')
    } else {
      ElMessage.error(res.message)
    }
  } catch {
    ElMessage.error(t('auth.loginFail'))
  } finally {
    loading.value = false
  }
}
</script>
