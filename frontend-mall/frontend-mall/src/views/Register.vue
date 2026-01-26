<template>
  <div class="grid gap-10 lg:grid-cols-[0.9fr_1.1fr] lg:items-center animate-fade-up">
    <el-card class="order-2 border-0 bg-white/85 shadow-soft lg:order-1">
      <template #header>
        <div>
          <h2 class="text-xl font-semibold">注册新账号</h2>
          <p class="muted-text">几步即可完成注册</p>
        </div>
      </template>

      <el-form :model="form" label-position="top">
        <el-form-item label="用户名">
          <el-input v-model="form.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="form.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" />
        </el-form-item>
        <el-form-item label="确认密码">
          <el-input v-model="form.confirmPassword" type="password" placeholder="再次输入密码" />
        </el-form-item>
        <el-button class="w-full" type="primary" size="large" :loading="loading" @click="handleRegister">
          注册
        </el-button>
      </el-form>

      <div class="mt-4 text-center text-sm text-[var(--muted)]">
        已有账号？
        <el-link type="primary" @click="router.push('/login')">返回登录</el-link>
      </div>
    </el-card>

    <section class="order-1 glass-panel p-10 lg:order-2">
      <p class="text-xs uppercase tracking-[0.4em] text-[var(--muted)]">Join Us</p>
      <h1 class="mt-4 text-4xl font-semibold">开启你的购物清单</h1>
      <p class="mt-4 text-lg text-[var(--muted)]">
        注册后可以管理收货地址、收藏心仪商品、查看订单进度。
      </p>
      <div class="mt-6 flex flex-wrap gap-3">
        <el-tag type="info" effect="light">订单追踪</el-tag>
        <el-tag type="success" effect="light">收藏夹</el-tag>
        <el-tag type="warning" effect="light">新品推送</el-tag>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { register } from '@/api/auth.js'

const router = useRouter()
const loading = ref(false)
const form = ref({
  username: '',
  phone: '',
  password: '',
  confirmPassword: '',
})

const handleRegister = async () => {
  if (!form.value.username || !form.value.phone || !form.value.password || !form.value.confirmPassword) {
    ElMessage.warning('请完整填写注册信息')
    return
  }
  loading.value = true
  try {
    const res = await register(form.value)
    if (res.code === 200) {
      ElMessage.success('注册成功，请登录')
      router.push('/login')
    } else {
      ElMessage.error(res.message)
    }
  } catch (error) {
    ElMessage.error('注册失败')
  } finally {
    loading.value = false
  }
}
</script>
