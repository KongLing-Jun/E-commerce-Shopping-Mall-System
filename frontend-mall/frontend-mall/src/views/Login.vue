<template>
  <div class="grid gap-10 lg:grid-cols-[1.1fr_0.9fr] lg:items-center animate-fade-up">
    <section class="glass-panel p-10">
      <p class="text-xs uppercase tracking-[0.4em] text-[var(--muted)]">Member Area</p>
      <h1 class="mt-4 text-4xl font-semibold">欢迎回来</h1>
      <p class="mt-4 text-lg text-[var(--muted)]">
        登录后即可查看订单状态、收藏清单和推荐商品。
      </p>
      <div class="mt-6 flex flex-wrap gap-3">
        <el-tag type="success" effect="light">专属推荐</el-tag>
        <el-tag type="warning" effect="light">极速下单</el-tag>
        <el-tag type="info" effect="light">安全支付</el-tag>
      </div>
    </section>

    <el-card class="border-0 bg-white/85 shadow-soft">
      <template #header>
        <div>
          <h2 class="text-xl font-semibold">账号登录</h2>
          <p class="muted-text">请输入你的用户名和密码</p>
        </div>
      </template>

      <el-form :model="form" label-position="top" @keyup.enter="handleLogin">
        <el-form-item label="用户名">
          <el-input v-model="form.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" />
        </el-form-item>
        <el-button class="w-full" type="primary" size="large" :loading="loading" @click="handleLogin">
          登录
        </el-button>
      </el-form>

      <div class="mt-4 text-center text-sm text-[var(--muted)]">
        还没有账号？
        <el-link type="primary" @click="router.push('/register')">立即注册</el-link>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { login } from '@/api/auth.js'
import { useAuth } from '@/composables/useAuth.js'

const router = useRouter()
const loading = ref(false)
const form = ref({
  username: '',
  password: '',
})
const { refreshAuth } = useAuth()

const handleLogin = async () => {
  if (!form.value.username || !form.value.password) {
    ElMessage.warning('请输入用户名和密码')
    return
  }
  loading.value = true
  try {
    const res = await login(form.value)
    if (res.code === 200) {
      localStorage.setItem('token', res.data.token)
      localStorage.setItem('userId', res.data.userId)
      localStorage.setItem('roleKey', res.data.roleKey)
      refreshAuth()
      window.dispatchEvent(new Event('auth-changed'))
      ElMessage.success('登录成功')
      router.push('/')
    } else {
      ElMessage.error(res.message)
    }
  } catch (error) {
    ElMessage.error('登录失败')
  } finally {
    loading.value = false
  }
}
</script>
