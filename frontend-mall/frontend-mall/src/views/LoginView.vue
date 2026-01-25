<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { login } from '../api/auth'
import { useUserStore } from '../stores/user'

const router = useRouter()
const userStore = useUserStore()
const username = ref('')
const password = ref('')
const errorMessage = ref('')

async function handleLogin() {
  errorMessage.value = ''
  try {
    const data = await login({ username: username.value, password: password.value })
    userStore.setSession(data)
    router.push('/')
  } catch (error) {
    errorMessage.value = error.message
  }
}
</script>

<template>
  <section class="auth-wrapper">
    <div class="card">
      <h2>用户登录</h2>
      <label>
        账号
        <input v-model="username" placeholder="请输入账号" />
      </label>
      <label>
        密码
        <input v-model="password" type="password" placeholder="请输入密码" />
      </label>
      <p v-if="errorMessage" class="error">{{ errorMessage }}</p>
      <button @click="handleLogin">登录</button>
      <router-link class="link" to="/register">没有账号？去注册</router-link>
    </div>
  </section>
</template>

<style scoped>
.auth-wrapper {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 70vh;
}

.card {
  background: white;
  padding: 32px;
  border-radius: 20px;
  width: 360px;
  box-shadow: 0 16px 40px rgba(15, 23, 42, 0.12);
  display: flex;
  flex-direction: column;
  gap: 16px;
}

label {
  display: flex;
  flex-direction: column;
  gap: 6px;
  font-size: 14px;
  color: #334155;
}

input {
  padding: 10px 12px;
  border-radius: 8px;
  border: 1px solid #e2e8f0;
}

button {
  background: #4f46e5;
  color: white;
  border: none;
  padding: 12px;
  border-radius: 8px;
  cursor: pointer;
  font-size: 15px;
}

.error {
  color: #dc2626;
  font-size: 14px;
}

.link {
  text-align: center;
  color: #6366f1;
}
</style>
