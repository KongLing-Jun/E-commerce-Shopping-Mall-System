<template>
  <div class="grid gap-8 xl:grid-cols-[1fr_360px]">
    <section class="space-y-6">
      <header>
        <div class="flex items-center justify-between">
          <h1 class="text-4xl font-extrabold">{{ dual('第 2 步（共 3 步）：支付', 'Step 2 of 3: Payment') }}</h1>
          <span class="text-lg text-[var(--muted)]">{{ dual('下一步：复核', 'Next: Review') }}</span>
        </div>
        <div class="mt-5 grid grid-cols-3 gap-2 text-sm font-semibold text-[var(--muted)]">
          <div class="rounded-full bg-[var(--accent)] px-4 py-2 text-center text-white">{{ dual('配送', 'Shipping') }}</div>
          <div class="rounded-full bg-[var(--highlight)] px-4 py-2 text-center text-[var(--accent)]">{{ dual('支付', 'Payment') }}</div>
          <div class="rounded-full bg-[var(--surface-soft)] px-4 py-2 text-center">{{ dual('复核', 'Review') }}</div>
        </div>
      </header>

      <article class="rounded-2xl border border-[var(--line)] bg-[var(--surface)]">
        <div class="flex items-center justify-between border-b border-[var(--line)] px-6 py-4">
          <h2 class="text-2xl font-extrabold">{{ dual('收货地址', 'Shipping Address') }}</h2>
          <el-button text type="primary" @click="router.push('/addresses')">{{ t('common.edit') }}</el-button>
        </div>
        <div class="grid gap-4 p-6 md:grid-cols-2">
          <button
            v-for="address in addresses"
            :key="address.id"
            type="button"
            class="rounded-xl border p-4 text-left"
            :class="selectedAddressId === address.id ? 'border-[var(--accent)] bg-[var(--highlight)]' : 'border-[var(--line)]'"
            @click="selectedAddressId = address.id"
          >
            <div class="text-lg font-bold">{{ address.receiver }} - {{ address.phone }}</div>
            <p class="mt-2 text-sm text-[var(--muted)]">
              {{ address.province }} {{ address.city }} {{ address.area }} {{ address.detail }}
            </p>
            <span v-if="address.isDefault === 1" class="chip mt-3">{{ t('address.default') }}</span>
          </button>
        </div>
      </article>

      <article class="rounded-2xl border border-[var(--line)] bg-[var(--surface)] p-6">
        <h2 class="text-3xl font-extrabold">{{ dual('支付方式', 'Payment Method') }}</h2>
        <p class="muted-text mt-2">{{ dual('所有交易均已加密并安全保护。', 'All transactions are secure and encrypted.') }}</p>

        <el-radio-group v-model="paymentMethod" class="mt-5 grid gap-3">
          <el-radio value="card" border size="large">{{ dual('信用卡 / 借记卡', 'Credit or Debit Card') }}</el-radio>
          <el-radio value="wallet" border size="large">{{ dual('数字钱包', 'Digital Wallet') }}</el-radio>
        </el-radio-group>

        <div class="mt-5 grid gap-4">
          <el-input v-model="paymentForm.cardNo" :placeholder="dual('卡号', 'Card Number')" />
          <div class="grid grid-cols-2 gap-4">
            <el-input v-model="paymentForm.expire" :placeholder="dual('月 / 年', 'MM / YY')" />
            <el-input v-model="paymentForm.cvc" placeholder="CVC" />
          </div>
          <el-input v-model="paymentForm.holder" :placeholder="dual('持卡人姓名', 'Name on Card')" />
          <el-checkbox v-model="paymentForm.saveCard">{{ dual('保存该卡用于下次支付', 'Save this card for future purchases') }}</el-checkbox>
        </div>
      </article>

      <div class="flex items-center justify-between">
        <el-button @click="router.push('/cart')">{{ dual('返回购物车', 'Back to Cart') }}</el-button>
        <el-button type="primary" size="large" :loading="loading" @click="submitOrder">{{ dual('提交并复核', 'Review Order') }}</el-button>
      </div>
    </section>

    <aside class="h-fit rounded-2xl border border-[var(--line)] bg-[var(--surface)]">
      <div class="border-b border-[var(--line)] px-6 py-5">
        <h3 class="text-3xl font-extrabold">{{ dual('订单摘要', 'Order Summary') }}</h3>
      </div>
      <div class="space-y-4 px-6 py-5">
        <div v-for="item in orderItems" :key="item.cartItemId" class="flex items-center gap-3">
          <img :src="item.image" :alt="item.name" class="h-16 w-16 rounded-lg object-cover" />
          <div class="min-w-0 flex-1">
            <p class="line-clamp-1 text-base font-bold">{{ item.name }}</p>
            <p class="text-sm text-[var(--muted)]">{{ dual('数量', 'Qty') }}: {{ item.quantity }}</p>
          </div>
          <strong class="text-lg">$ {{ formatPrice(item.price * item.quantity) }}</strong>
        </div>
      </div>

      <div class="space-y-3 border-t border-[var(--line)] px-6 py-5 text-base">
        <div class="flex items-center justify-between text-[var(--muted)]">
          <span>{{ dual('商品小计', 'Subtotal') }}</span>
          <span>$ {{ formatPrice(totalAmount) }}</span>
        </div>
        <div class="flex items-center justify-between text-[var(--muted)]">
          <span>{{ dual('运费', 'Shipping') }}</span>
          <span>{{ dual('免运费', 'Free') }}</span>
        </div>
        <div class="flex items-center justify-between text-[var(--muted)]">
          <span>{{ dual('预估税费', 'Estimated Tax') }}</span>
          <span>$ {{ formatPrice(totalAmount * 0.08) }}</span>
        </div>
        <div class="flex items-center justify-between border-t border-[var(--line)] pt-4 text-3xl font-extrabold">
          <span>{{ t('common.total') }}</span>
          <span class="text-[var(--accent)]">$ {{ formatPrice(totalAmount * 1.08) }}</span>
        </div>
      </div>
    </aside>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { createOrder, getOrderPre } from '@/api/order.js'
import { useI18n } from '@/i18n/index.js'

const router = useRouter()
const orderItems = ref([])
const addresses = ref([])
const totalAmount = ref(0)
const selectedAddressId = ref(null)
const paymentMethod = ref('card')
const loading = ref(false)
const paymentForm = reactive({
  cardNo: '',
  expire: '',
  cvc: '',
  holder: '',
  saveCard: false,
})
const { t, locale } = useI18n()
const dual = (zh, en) => (locale.value === 'zh' ? zh : en)

// 统一价格格式化，避免金额显示精度不一致。
const formatPrice = (value) => Number(value || 0).toFixed(2)

// 读取确认页预览数据：勾选商品、地址列表、金额汇总。
const loadPreOrder = async () => {
  try {
    const res = await getOrderPre()
    if (res.code === 200) {
      orderItems.value = res.data.items || []
      addresses.value = res.data.addresses || []
      totalAmount.value = Number(res.data.totalAmount || 0)
      const defaultAddress = addresses.value.find((item) => item.isDefault === 1)
      selectedAddressId.value = defaultAddress ? defaultAddress.id : addresses.value[0]?.id || null
    } else {
      ElMessage.error(res.message)
    }
  } catch {
    ElMessage.error(t('orderConfirm.loadFail'))
  }
}

// 提交订单前校验地址与商品，再创建主单和子单。
const submitOrder = async () => {
  if (!selectedAddressId.value) {
    ElMessage.warning(t('orderConfirm.selectAddress'))
    return
  }
  if (!orderItems.value.length) {
    ElMessage.warning(t('orderConfirm.noItems'))
    return
  }
  loading.value = true
  try {
    const res = await createOrder({ addressId: selectedAddressId.value })
    if (res.code === 200) {
      ElMessage.success(`${t('orderConfirm.created')} #${res.data.orderNo}`)
      router.push('/orders')
    } else {
      ElMessage.error(res.message)
    }
  } catch {
    ElMessage.error(t('orderConfirm.submitFail'))
  } finally {
    loading.value = false
  }
}

// 页面加载时初始化确认页数据。
onMounted(loadPreOrder)
</script>


