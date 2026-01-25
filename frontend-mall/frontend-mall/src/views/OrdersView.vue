<script setup>
import { onMounted, ref } from 'vue'
import { fetchOrders, updateOrderStatus } from '../api/orders'

const orders = ref([])
const message = ref('')
const statusLabels = {
  PENDING_PAYMENT: '待支付',
  PAID: '待发货',
  SHIPPED: '已发货',
  COMPLETED: '已完成',
}

async function loadOrders() {
  try {
    orders.value = await fetchOrders()
  } catch (error) {
    message.value = error.message
  }
}

async function confirmOrder(orderId) {
  try {
    await updateOrderStatus(orderId, 'COMPLETED')
    await loadOrders()
  } catch (error) {
    message.value = error.message
  }
}

function formatStatus(status) {
  return statusLabels[status] || status
}

onMounted(loadOrders)
</script>

<template>
  <section>
    <header class="header">
      <div>
        <h2>我的订单</h2>
        <p>查看订单状态，确认收货后即可完成。</p>
      </div>
      <button @click="loadOrders">刷新</button>
    </header>

    <p v-if="message" class="message">{{ message }}</p>

    <div v-if="orders.length" class="orders">
      <article v-for="order in orders" :key="order.id" class="order">
        <header>
          <div>
            <h3>订单号 #{{ order.id }}</h3>
            <p>下单时间：{{ order.createdAt }}</p>
          </div>
          <span class="status">{{ formatStatus(order.status) }}</span>
        </header>
        <ul>
          <li v-for="item in order.items" :key="item.id">
            <span>{{ item.productName }} × {{ item.quantity }}</span>
            <span>￥{{ item.price }}</span>
          </li>
        </ul>
        <footer>
          <span>总计：￥{{ order.total }}</span>
          <button v-if="order.status === 'SHIPPED'" @click="confirmOrder(order.id)">
            确认收货
          </button>
        </footer>
      </article>
    </div>

    <div v-else class="empty">暂无订单记录</div>
  </section>
</template>

<style scoped>
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.header button {
  border: 1px solid #e2e8f0;
  padding: 8px 14px;
  border-radius: 8px;
  background: white;
  cursor: pointer;
}

.message {
  color: #2563eb;
  margin-bottom: 12px;
}

.orders {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.order {
  background: white;
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 8px 22px rgba(15, 23, 42, 0.08);
}

.order header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 12px;
}

.status {
  color: #0f766e;
  font-weight: 600;
}

ul {
  list-style: none;
  padding: 0;
  margin: 0 0 12px;
}

li {
  display: flex;
  justify-content: space-between;
  padding: 6px 0;
  border-bottom: 1px dashed #e2e8f0;
}

footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

footer button {
  border: none;
  background: #4f46e5;
  color: white;
  padding: 8px 12px;
  border-radius: 8px;
  cursor: pointer;
}

.empty {
  padding: 32px;
  background: #f8fafc;
  border-radius: 16px;
  text-align: center;
}
</style>
