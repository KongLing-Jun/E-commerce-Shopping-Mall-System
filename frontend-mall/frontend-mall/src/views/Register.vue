<template>
  <div class="grid gap-10 lg:grid-cols-[0.9fr_1.1fr] lg:items-center animate-fade-up">
    <el-card class="order-2 border-0 bg-[var(--surface-strong)] shadow-soft lg:order-1">
      <template #header>
        <div>
          <h2 class="text-xl font-semibold">{{ t('auth.registerTitle') }}</h2>
          <p class="muted-text">{{ t('auth.registerSubtitle') }}</p>
        </div>
      </template>

      <el-form :model="form" label-position="top">
        <el-form-item :label="t('auth.username')">
          <el-input v-model="form.username" :placeholder="t('auth.username')" />
        </el-form-item>
        <el-form-item :label="t('auth.phone')">
          <el-input v-model="form.phone" :placeholder="t('auth.phone')" />
        </el-form-item>
        <el-form-item :label="t('auth.password')">
          <el-input v-model="form.password" type="password" :placeholder="t('auth.password')" />
        </el-form-item>
        <el-form-item :label="t('auth.confirmPassword')">
          <el-input v-model="form.confirmPassword" type="password" :placeholder="t('auth.confirmPassword')" />
        </el-form-item>
        <el-button class="w-full" type="primary" size="large" :loading="loading" @click="handleRegister">
          {{ t('auth.register') }}
        </el-button>
      </el-form>

      <div class="mt-4 text-center text-sm text-[var(--muted)]">
        {{ t('auth.hasAccount') }}
        <el-link type="primary" @click="router.push('/login')">{{ t('auth.toLogin') }}</el-link>
      </div>
    </el-card>

    <section class="order-1 glass-panel p-10 lg:order-2">
      <p class="text-xs uppercase tracking-[0.4em] text-[var(--muted)]">Join Us</p>
      <h1 class="mt-4 text-4xl font-semibold">{{ t('auth.registerTitle') }}</h1>
      <p class="mt-4 text-lg text-[var(--muted)]">
        {{ t('auth.registerSubtitle') }}
      </p>
      <div class="mt-6 flex flex-wrap gap-3">
        <el-tag type="info" effect="light">{{ t('orderList.shipped') }}</el-tag>
        <el-tag type="success" effect="light">{{ t('home.tags.curated') }}</el-tag>
        <el-tag type="warning" effect="light">{{ t('home.tags.fast') }}</el-tag>
      </div>
    </section>
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
  loading.value = true
  try {
    const res = await register(form.value)
    if (res.code === 200) {
      ElMessage.success(t('auth.registerSuccess'))
      router.push('/login')
    } else {
      ElMessage.error(res.message)
    }
  } catch (error) {
    ElMessage.error(t('auth.registerFail'))
  } finally {
    loading.value = false
  }
}
</script>

