<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from './stores/user'

const router = useRouter()
const userStore = useUserStore()

const isLoggedIn = computed(() => !!userStore.token)

function logout() {
  userStore.clearSession()
  router.push('/login')
}
</script>

<template>
  <div class="app">
    <header class="navbar">
      <div class="brand" @click="router.push('/')">Mall System</div>
      <nav>
        <router-link to="/">首页</router-link>
        <router-link to="/cart">购物车</router-link>
        <router-link to="/orders">订单</router-link>
      </nav>
      <div class="auth">
        <template v-if="isLoggedIn">
          <span>Hi, {{ userStore.username }}</span>
          <button @click="logout">退出</button>
        </template>
        <template v-else>
          <router-link to="/login">登录</router-link>
          <router-link to="/register">注册</router-link>
        </template>
      </div>
    </header>

    <main class="content">
      <router-view />
    </main>
  </div>
</template>

<style scoped>
.app {
  min-height: 100vh;
  background: #f8fafc;
  color: #0f172a;
}

.navbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 32px;
  background: white;
  box-shadow: 0 4px 20px rgba(15, 23, 42, 0.08);
  position: sticky;
  top: 0;
  z-index: 10;
}

.brand {
  font-weight: 700;
  font-size: 18px;
  cursor: pointer;
}

nav {
  display: flex;
  gap: 16px;
}

nav a {
  color: #334155;
  text-decoration: none;
  font-weight: 500;
}

nav a.router-link-active {
  color: #4f46e5;
}

.auth {
  display: flex;
  gap: 12px;
  align-items: center;
}

.auth button {
  border: 1px solid #e2e8f0;
  background: white;
  padding: 6px 12px;
  border-radius: 8px;
  cursor: pointer;
}

.content {
  padding: 32px;
}
</style>
