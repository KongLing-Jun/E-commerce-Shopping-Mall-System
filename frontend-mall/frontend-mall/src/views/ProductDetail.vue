<template>
  <div class="space-y-8">
    <el-breadcrumb separator="/">
      <el-breadcrumb-item to="/">{{ t('nav.home') }}</el-breadcrumb-item>
      <el-breadcrumb-item to="/products">{{ t('nav.products') }}</el-breadcrumb-item>
      <el-breadcrumb-item>{{ product?.name || t('productDetail.title') }}</el-breadcrumb-item>
    </el-breadcrumb>

    <section v-if="product" class="grid gap-8 lg:grid-cols-[1.05fr_0.95fr]">
      <div>
        <div class="overflow-hidden rounded-2xl border border-[var(--line)] bg-[var(--surface)] p-2">
          <img :src="activeImage" :alt="product.name" class="h-[560px] w-full rounded-xl object-cover" />
        </div>
        <div class="mt-4 grid grid-cols-4 gap-3">
          <button
            v-for="(image, index) in gallery"
            :key="`${image}-${index}`"
            type="button"
            class="overflow-hidden rounded-xl border bg-[var(--surface)] p-1"
            :class="activeImage === image ? 'border-[var(--accent)]' : 'border-[var(--line)]'"
            @click="activeImage = image"
          >
            <img :src="image" :alt="`thumb-${index}`" class="h-20 w-full rounded-lg object-cover" />
          </button>
        </div>
      </div>

      <div class="space-y-5">
        <div>
          <div class="text-sm font-semibold text-[var(--accent)]">{{ dual('4.8 分（320条评价）', '4.8 (320 reviews)') }}</div>
          <h1 class="mt-2 text-5xl font-extrabold leading-tight">{{ product.name }}</h1>
          <p class="mt-3 text-lg text-[var(--muted)]">{{ product.brief }}</p>
        </div>

        <div class="flex items-center gap-3">
          <strong class="text-5xl font-extrabold text-[var(--accent)]">$ {{ formatPrice(product.price) }}</strong>
          <span class="chip">{{ t('productList.stock') }} {{ product.stock }}</span>
        </div>

        <ul class="space-y-2 text-sm text-[var(--muted)]">
          <li>{{ dual('- 蓝牙5.2低延迟连接', '- Bluetooth 5.2 low-latency connection') }}</li>
          <li>{{ dual('- 20小时续航并支持快充', '- 20-hour battery life with fast charging') }}</li>
          <li>{{ dual('- 智能主动降噪', '- Intelligent active noise cancellation') }}</li>
        </ul>

        <div class="space-y-3">
          <div class="text-sm font-semibold text-[var(--muted)]">{{ dual('颜色', 'Color') }}</div>
          <div class="flex gap-3">
            <button
              v-for="color in colors"
              :key="color"
              type="button"
              class="h-8 w-8 rounded-full border-2"
              :class="activeColor === color ? 'border-[var(--accent)]' : 'border-transparent'"
              :style="{ backgroundColor: color }"
              @click="activeColor = color"
            />
          </div>
        </div>

        <div class="grid gap-3 sm:grid-cols-[1fr_1.6fr_auto]">
          <el-input-number v-model="quantity" :min="1" :max="Math.max(1, product.stock)" size="large" />
          <el-button type="primary" size="large" :disabled="product.stock <= 0" @click="addToCart">
            {{ t('productDetail.addToCart') }}
          </el-button>
          <el-button size="large" @click="addFavorite">Fav</el-button>
        </div>

        <el-button class="w-full" size="large" type="info" @click="buyNow">{{ dual('立即购买', 'Buy Now') }}</el-button>
      </div>
    </section>

    <section v-if="product" class="grid gap-6 lg:grid-cols-[1fr_320px]">
      <div class="space-y-4 rounded-2xl border border-[var(--line)] bg-[var(--surface)] p-6">
        <div class="flex flex-wrap gap-3 border-b border-[var(--line)] pb-4">
          <button
            v-for="tab in tabs"
            :key="tab.key"
            type="button"
            class="px-3 py-2 text-sm font-semibold"
            :class="activeTab === tab.key ? 'text-[var(--accent)]' : 'text-[var(--muted)]'"
            @click="activeTab = tab.key"
          >
            {{ dual(tab.zh, tab.en) }}
          </button>
        </div>

        <div v-if="activeTab === 'Description'" class="space-y-3">
          <h3 class="text-3xl font-extrabold">{{ dual('录音室级音质，随时随地尽享', 'Studio-Quality Sound, Anywhere You Go') }}</h3>
          <div class="text-[15px] leading-8 text-[var(--muted)]" v-html="product.detailHtml"></div>
        </div>
        <div v-else-if="activeTab === 'Specifications'" class="space-y-2 text-[var(--muted)]">
          <template v-if="specEntries.length">
            <p v-for="[key, value] in specEntries" :key="`${key}-${value}`">{{ key }}: {{ value }}</p>
          </template>
          <template v-else>
            <p>{{ dual('驱动单元：40mm 动圈', 'Driver Unit: 40mm dynamic') }}</p>
            <p>{{ dual('连接方式：蓝牙 5.2', 'Connectivity: Bluetooth 5.2') }}</p>
            <p>{{ dual('电池续航：最长20小时', 'Battery: Up to 20 hours') }}</p>
            <p>{{ dual('质保：2年', 'Warranty: 2 years') }}</p>
          </template>
        </div>
        <div v-else class="space-y-3 text-[var(--muted)]">
          <p>{{ dual('"超出预期，电池足够用一整天。"', '"Better than expected, battery lasts all day."') }}</p>
          <p>{{ dual('"声音均衡，佩戴也很舒适。"', '"Balanced sound profile and comfortable fit."') }}</p>
        </div>
      </div>

      <aside class="rounded-2xl border border-[var(--line)] bg-[var(--surface)] p-6">
        <h4 class="text-2xl font-extrabold">{{ dual('用户评分', 'Customer Reviews') }}</h4>
        <div class="mt-4 text-5xl font-extrabold">4.8</div>
        <p class="mt-1 text-sm text-[var(--muted)]">{{ dual('基于320条评论', 'Based on 320 reviews') }}</p>
        <div class="mt-5 space-y-2">
          <div v-for="score in [5, 4, 3, 2, 1]" :key="score" class="flex items-center gap-2 text-sm">
            <span class="w-3">{{ score }}</span>
            <div class="h-2 flex-1 rounded bg-[var(--surface-soft)]">
              <div class="h-2 rounded bg-[var(--accent)]" :style="{ width: `${score * 15}%` }"></div>
            </div>
          </div>
        </div>
      </aside>
    </section>

    <el-empty v-else-if="!loading" :description="errorText || t('productList.noProducts')">
      <el-button type="primary" @click="router.push('/products')">{{ t('productDetail.backToList') }}</el-button>
    </el-empty>
    <el-skeleton v-else :rows="6" animated />
  </div>
</template>

<script setup>
import { computed, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getProduct } from '@/api/product.js'
import { addCartItem } from '@/api/cart.js'
import { addUserFavorite } from '@/api/user.js'
import { useI18n } from '@/i18n/index.js'

const route = useRoute()
const router = useRouter()
const product = ref(null)
const loading = ref(false)
const errorText = ref('')
const activeImage = ref('')
const quantity = ref(1)
const activeColor = ref('#111827')
const activeTab = ref('Description')
const colors = ['#111827', '#1f7ae0', '#d97706']
const tabs = [
  { key: 'Description', zh: '商品详情', en: 'Description' },
  { key: 'Reviews', zh: '用户评价', en: 'Reviews' },
  { key: 'Specifications', zh: '规格参数', en: 'Specifications' },
]
const { t, locale } = useI18n()
const dual = (zh, en) => (locale.value === 'zh' ? zh : en)

const gallery = computed(() => {
  if (!product.value) {
    return []
  }
  if (Array.isArray(product.value.images) && product.value.images.length) {
    return product.value.images
  }
  return [product.value.coverUrl, product.value.coverUrl, product.value.coverUrl]
})

const specEntries = computed(() => {
  if (!product.value?.specs || typeof product.value.specs !== 'object') {
    return []
  }
  return Object.entries(product.value.specs)
})

watch(gallery, (images) => {
  activeImage.value = images[0] || ''
})

const formatPrice = (value) => Number(value || 0).toFixed(2)

const loadProduct = async () => {
  // 按商品ID加载详情，并按业务错误码展示明确提示。
  loading.value = true
  errorText.value = ''
  product.value = null
  try {
    const productId = Number(route.params.productId)
    if (!productId) {
      errorText.value = dual('商品ID无效', 'Invalid product id')
      return
    }
    const res = await getProduct(productId)
    if (res.code === 200) {
      product.value = res.data
      quantity.value = 1
    } else if (res.code === 401) {
      errorText.value = t('productDetail.loginRequired')
    } else if (res.code === 404) {
      errorText.value = t('productDetail.notFound')
    } else if (res.code === 410) {
      errorText.value = t('productDetail.offShelf')
    } else {
      errorText.value = res.message || t('productDetail.loadFail')
    }
  } catch (error) {
    const code = error?.response?.data?.code
    if (code === 401) {
      errorText.value = t('productDetail.loginRequired')
    } else if (code === 404) {
      errorText.value = t('productDetail.notFound')
    } else if (code === 410) {
      errorText.value = t('productDetail.offShelf')
    } else {
      errorText.value = t('productDetail.loadFail')
      ElMessage.error(errorText.value)
    }
  } finally {
    loading.value = false
  }
}

const addToCart = async () => {
  // 将当前数量加入购物车，数量上限受库存约束。
  if (!product.value) {
    return
  }
  try {
    const res = await addCartItem({ productId: product.value.id, quantity: quantity.value })
    if (res.code === 200) {
      ElMessage.success(t('cart.added'))
    } else {
      ElMessage.error(res.message || t('cart.addFail'))
    }
  } catch {
    ElMessage.error(t('cart.addFail'))
  }
}

const buyNow = async () => {
  // 复用加购逻辑后直接跳转到购物车结算。
  await addToCart()
  router.push('/cart')
}

const addFavorite = async () => {
  // 收藏当前商品，重复收藏由后端做幂等处理。
  if (!product.value) {
    return
  }
  try {
    const res = await addUserFavorite({ productId: product.value.id })
    if (res.code === 200) {
      ElMessage.success(t('productDetail.favorited'))
    } else {
      ElMessage.info(res.message || t('common.empty'))
    }
  } catch {
    ElMessage.error(t('common.empty'))
  }
}

watch(
  () => route.params.productId,
  () => {
    // 同页面切换商品ID时重新拉取详情。
    loadProduct()
  },
  { immediate: true },
)
</script>
