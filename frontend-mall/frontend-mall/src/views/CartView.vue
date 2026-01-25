<script setup>
import { computed, onMounted, ref } from 'vue'
import { fetchCart, removeFromCart, updateCart } from '../api/cart'
import { fetchProducts } from '../api/products'
import { createOrder } from '../api/orders'

const cartItems = ref([])
const products = ref([])
const address = ref('')
const message = ref('')

const enrichedItems = computed(() =>
  cartItems.value.map((item) => {
    const product = products.value.find((p) => p.id === item.productId) || {}
    return {
      ...item,
      name: product.name,
      price: product.price,
      imageUrl: product.imageUrl,
    }
  })
)

const total = computed(() =>
  enrichedItems.value.reduce((sum, item) => sum + Number(item.price || 0) * item.quantity, 0)
)

async function loadCart() {
  try {
    const [cart, productList] = await Promise.all([fetchCart(), fetchProducts()])
    cartItems.value = cart
    products.value = productList
  } catch (error) {
    message.value = error.message
  }
}

async function changeQuantity(item, delta) {
  const nextValue = item.quantity + delta
  if (nextValue <= 0) return
  try {
    await updateCart(item.productId, nextValue)
    await loadCart()
  } catch (error) {
    message.value = error.message
  }
}

async function removeItem(productId) {
  try {
    await removeFromCart(productId)
    await loadCart()
  } catch (error) {
    message.value = error.message
  }
}

async function submitOrder() {
  try {
    const order = await createOrder(address.value)
    message.value = `订单已提交，订单号 #${order.id}`
    address.value = ''
    await loadCart()
  } catch (error) {
    message.value = error.message
  }
}

onMounted(loadCart)
</script>

<template>
  <section class="cart">
    <header>
      <h2>购物车</h2>
      <p>管理你的商品清单，确认数量后提交订单。</p>
    </header>

    <p v-if="message" class="message">{{ message }}</p>

    <div v-if="enrichedItems.length" class="items">
      <div v-for="item in enrichedItems" :key="item.id" class="item">
        <img :src="item.imageUrl" :alt="item.name" />
        <div class="detail">
          <h3>{{ item.name }}</h3>
          <p>单价：￥{{ item.price }}</p>
          <div class="quantity">
            <button @click="changeQuantity(item, -1)">-</button>
            <span>{{ item.quantity }}</span>
            <button @click="changeQuantity(item, 1)">+</button>
          </div>
        </div>
        <div class="actions">
          <span>小计：￥{{ Number(item.price || 0) * item.quantity }}</span>
          <button class="remove" @click="removeItem(item.productId)">移除</button>
        </div>
      </div>
    </div>

    <div v-else class="empty">购物车为空，快去首页挑选商品吧！</div>

    <div class="checkout">
      <div>
        <label>
          收货地址
          <input v-model="address" placeholder="请输入收货地址" />
        </label>
      </div>
      <div class="summary">
        <span>合计：￥{{ total.toFixed(2) }}</span>
        <button :disabled="!enrichedItems.length" @click="submitOrder">提交订单</button>
      </div>
    </div>
  </section>
</template>

<style scoped>
.cart header {
  margin-bottom: 20px;
}

.message {
  color: #2563eb;
  margin-bottom: 16px;
}

.items {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.item {
  display: grid;
  grid-template-columns: 120px 1fr 160px;
  gap: 16px;
  background: white;
  border-radius: 16px;
  padding: 16px;
  align-items: center;
  box-shadow: 0 6px 20px rgba(15, 23, 42, 0.08);
}

.item img {
  width: 120px;
  height: 100px;
  border-radius: 12px;
  object-fit: cover;
}

.detail h3 {
  margin: 0 0 8px;
}

.quantity {
  display: inline-flex;
  align-items: center;
  gap: 8px;
}

.quantity button {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  border: 1px solid #cbd5f5;
  background: #eef2ff;
  cursor: pointer;
}

.actions {
  display: flex;
  flex-direction: column;
  gap: 12px;
  align-items: flex-end;
}

.remove {
  border: none;
  background: #fee2e2;
  color: #dc2626;
  padding: 6px 12px;
  border-radius: 8px;
  cursor: pointer;
}

.empty {
  padding: 32px;
  background: #f8fafc;
  border-radius: 16px;
  text-align: center;
}

.checkout {
  margin-top: 24px;
  padding: 20px;
  background: white;
  border-radius: 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 8px 22px rgba(15, 23, 42, 0.08);
}

.checkout input {
  width: 320px;
  padding: 10px 12px;
  border-radius: 8px;
  border: 1px solid #e2e8f0;
}

.summary {
  display: flex;
  align-items: center;
  gap: 16px;
}

.summary button {
  background: #16a34a;
  color: white;
  border: none;
  padding: 10px 16px;
  border-radius: 8px;
  cursor: pointer;
}

.summary button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}
</style>
