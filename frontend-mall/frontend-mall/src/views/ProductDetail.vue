<template>
  <div class="space-y-6 animate-fade-up">
    <el-breadcrumb separator="/">
      <el-breadcrumb-item to="/">首页</el-breadcrumb-item>
      <el-breadcrumb-item to="/products">商品</el-breadcrumb-item>
      <el-breadcrumb-item>{{ product?.name || '详情' }}</el-breadcrumb-item>
    </el-breadcrumb>

    <el-card v-if="product" class="border-0 bg-white/85 shadow-soft">
      <div class="grid gap-8 lg:grid-cols-[0.9fr_1.1fr]">
        <el-image
          :src="product.coverUrl"
          fit="cover"
          class="h-80 w-full rounded-3xl"
          :preview-src-list="[product.coverUrl]"
        />
        <div class="space-y-4">
          <h1 class="text-3xl font-semibold">{{ product.name }}</h1>
          <p class="text-[var(--muted)]">{{ product.brief }}</p>
          <div class="flex items-center gap-3">
            <span class="text-2xl font-semibold text-[var(--accent)]">¥{{ product.price }}</span>
            <el-tag type="info" effect="light">库存 {{ product.stock }}</el-tag>
          </div>
          <el-divider />
          <div class="space-y-2">
            <h3 class="text-lg font-semibold">商品详情</h3>
            <div class="text-sm leading-7 text-[var(--muted)]" v-html="product.detailHtml"></div>
          </div>
          <div class="pt-4">
            <el-button
              type="primary"
              size="large"
              :disabled="product.stock <= 0 || !isLoggedIn"
              @click="guardAction(addToCart)"
            >
              加入购物车
            </el-button>
          </div>
        </div>
      </div>
    </el-card>

    <el-empty v-else description="商品不存在" />
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getProduct } from '@/api/product.js'
import { addCartItem } from '@/api/cart.js'
import { useAuth } from '@/composables/useAuth.js'

const route = useRoute()
const product = ref(null)
const { isLoggedIn, guardAction } = useAuth()

const loadProduct = async () => {
  try {
    const productId = route.params.productId
    const res = await getProduct(productId)
    if (res.code === 200) {
      product.value = res.data
    }
  } catch (error) {
    console.error('加载商品详情失败', error)
  }
}

const addToCart = async () => {
  if (!product.value) {
    return
  }
  try {
    const res = await addCartItem({ productId: product.value.id, quantity: 1 })
    if (res.code === 200) {
      ElMessage.success('已加入购物车')
    } else {
      ElMessage.error(res.message)
    }
  } catch (error) {
    ElMessage.error('加入购物车失败')
  }
}

onMounted(() => {
  loadProduct()
})
</script>
