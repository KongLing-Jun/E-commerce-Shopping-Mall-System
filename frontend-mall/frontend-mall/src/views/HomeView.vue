<script setup>
import { onMounted, ref } from 'vue'
import { fetchProducts } from '../api/products'
import { addToCart } from '../api/cart'
import { useUserStore } from '../stores/user'

const products = ref([])
const keyword = ref('')
const loading = ref(false)
const errorMessage = ref('')
const successMessage = ref('')
const userStore = useUserStore()

async function loadProducts() {
  loading.value = true
  errorMessage.value = ''
  try {
    products.value = await fetchProducts(keyword.value)
  } catch (error) {
    errorMessage.value = error.message
  } finally {
    loading.value = false
  }
}

async function handleAddToCart(productId) {
  if (!userStore.token) {
    errorMessage.value = '请先登录再加入购物车'
    return
  }
  try {
    await addToCart(productId, 1)
    successMessage.value = '已加入购物车'
    setTimeout(() => {
      successMessage.value = ''
    }, 2000)
  } catch (error) {
    errorMessage.value = error.message
  }
}

onMounted(loadProducts)
</script>

<template>
  <section class="hero">
    <div>
      <p class="eyebrow">3C 电子商城</p>
      <h1>精选数码好物，一站式下单体验</h1>
      <p class="sub">覆盖手机、电脑、配件热销榜，立即开启购物体验。</p>
    </div>
    <div class="search-card">
      <h3>快捷搜索</h3>
      <div class="search-row">
        <input v-model="keyword" placeholder="搜索商品关键词" />
        <button @click="loadProducts">搜索</button>
      </div>
      <p class="hint">支持关键词模糊搜索</p>
    </div>
  </section>

  <section class="status" v-if="errorMessage || successMessage">
    <p v-if="errorMessage" class="error">{{ errorMessage }}</p>
    <p v-if="successMessage" class="success">{{ successMessage }}</p>
  </section>

  <section class="product-list">
    <header>
      <h2>推荐商品</h2>
      <span v-if="loading">加载中...</span>
    </header>
    <div v-if="products.length" class="grid">
      <article v-for="product in products" :key="product.id" class="card">
        <img :src="product.imageUrl" :alt="product.name" />
        <div class="card-body">
          <h3>{{ product.name }}</h3>
          <p>{{ product.description }}</p>
          <div class="meta">
            <span class="price">￥{{ product.price }}</span>
            <span class="stock">库存 {{ product.stock }}</span>
          </div>
          <button @click="handleAddToCart(product.id)">加入购物车</button>
        </div>
      </article>
    </div>
    <div v-else class="empty">暂无商品数据，请确认后端服务已启动。</div>
  </section>
</template>

<style scoped>
.hero {
  display: flex;
  gap: 32px;
  justify-content: space-between;
  background: linear-gradient(120deg, #eef2ff, #fdf2f8);
  padding: 32px;
  border-radius: 24px;
  margin-bottom: 32px;
}

.hero h1 {
  font-size: 32px;
  margin: 12px 0;
}

.eyebrow {
  font-weight: 600;
  color: #6366f1;
}

.sub {
  color: #475569;
  max-width: 420px;
}

.search-card {
  background: white;
  border-radius: 16px;
  padding: 20px;
  min-width: 260px;
  box-shadow: 0 10px 30px rgba(15, 23, 42, 0.1);
}

.search-row {
  display: flex;
  gap: 8px;
  margin-top: 12px;
}

.search-row input {
  flex: 1;
  padding: 10px 12px;
  border-radius: 8px;
  border: 1px solid #e2e8f0;
}

.search-row button {
  background: #4f46e5;
  border: none;
  color: white;
  padding: 10px 16px;
  border-radius: 8px;
  cursor: pointer;
}

.hint {
  font-size: 12px;
  color: #94a3b8;
  margin-top: 8px;
}

.status {
  margin-bottom: 16px;
}

.error {
  color: #dc2626;
}

.success {
  color: #16a34a;
}

.product-list header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 20px;
}

.card {
  background: white;
  border-radius: 18px;
  overflow: hidden;
  box-shadow: 0 8px 24px rgba(15, 23, 42, 0.08);
  display: flex;
  flex-direction: column;
}

.card img {
  width: 100%;
  height: 160px;
  object-fit: cover;
}

.card-body {
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.meta {
  display: flex;
  justify-content: space-between;
  color: #64748b;
}

.price {
  font-weight: 700;
  color: #f97316;
}

.card button {
  border: none;
  background: #111827;
  color: white;
  padding: 10px;
  border-radius: 8px;
  cursor: pointer;
}

.empty {
  padding: 32px;
  background: #f8fafc;
  border-radius: 16px;
  text-align: center;
  color: #475569;
}
</style>
