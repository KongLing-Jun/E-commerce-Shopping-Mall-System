<template>
  <div class="grid gap-10 lg:grid-cols-[1.1fr_0.9fr] lg:items-center animate-fade-up">
    <section class="glass-panel p-10">
      <p class="text-xs uppercase tracking-[0.4em] text-[var(--muted)]">{{ t('home.discover') }}</p>
      <h1 class="mt-4 text-4xl font-semibold">{{ t('auth.loginTitle') }}</h1>
      <p class="mt-4 text-lg text-[var(--muted)]">
        {{ t('auth.loginSubtitle') }}
      </p>
      <div class="mt-6 flex flex-wrap gap-3">
        <el-tag type="success" effect="light">{{ t('home.tags.secure') }}</el-tag>
        <el-tag type="warning" effect="light">{{ t('home.tags.fast') }}</el-tag>
        <el-tag type="info" effect="light">{{ t('home.tags.curated') }}</el-tag>
      </div>
    </section>

    <el-card class="border-0 bg-[var(--surface-strong)] shadow-soft">
      <template #header>
        <div>
          <h2 class="text-xl font-semibold">{{ t('auth.loginTitle') }}</h2>
          <p class="muted-text">{{ t('auth.loginSubtitle') }}</p>
        </div>
      </template>

      <el-form :model="form" label-position="top" @keyup.enter="handleLogin">
        <el-form-item :label="t('auth.username')">
          <el-input v-model="form.username" :placeholder="t('auth.username')" />
        </el-form-item>
        <el-form-item :label="t('auth.password')">
          <el-input v-model="form.password" type="password" :placeholder="t('auth.password')" />
        </el-form-item>
        <el-button class="w-full" type="primary" size="large" :loading="loading" @click="handleLogin">
          {{ t('auth.login') }}
        </el-button>
      </el-form>

      <div class="mt-4 text-center text-sm text-[var(--muted)]">
        {{ t('auth.noAccount') }}
        <el-link type="primary" @click="router.push('/register')">{{ t('auth.toRegister') }}</el-link>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { login } from '@/api/auth.js'
import { fetchMyPerms } from '@/api/menus.js'
import { useAuth } from '@/composables/useAuth.js'
import { useI18n } from '@/i18n/index.js'

const router = useRouter()
const loading = ref(false)
const form = ref({
  username: '',
  password: '',
})
const { refreshAuth } = useAuth()
const { t } = useI18n()

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
      try {
        const permRes = await fetchMyPerms()
        if (permRes.code === 200) {
          localStorage.setItem('perms', JSON.stringify(permRes.data || []))
        }
      } catch (error) {
        localStorage.setItem('perms', JSON.stringify([]))
      }
      refreshAuth()
      window.dispatchEvent(new Event('auth-changed'))
      ElMessage.success(t('auth.loginSuccess'))
      router.push('/')
    } else {
      ElMessage.error(res.message)
    }
  } catch (error) {
    ElMessage.error(t('auth.loginFail'))
  } finally {
    loading.value = false
  }
}
</script>

