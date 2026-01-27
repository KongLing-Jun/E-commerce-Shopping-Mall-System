<template>
  <div class="space-y-6 animate-fade-up">
    <el-breadcrumb separator="/">
      <el-breadcrumb-item to="/">{{ t('nav.home') }}</el-breadcrumb-item>
      <el-breadcrumb-item to="/products">{{ t('nav.products') }}</el-breadcrumb-item>
      <el-breadcrumb-item>{{ product?.name || t('productDetail.title') }}</el-breadcrumb-item>
    </el-breadcrumb>

    <el-card v-if="product" class="border-0 bg-[var(--surface-strong)] shadow-soft">
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
            <span class="text-2xl font-semibold text-[var(--accent)]">$ {{ product.price }}</span>
            <el-tag type="info" effect="light">{{ t('productList.stock') }} {{ product.stock }}</el-tag>
          </div>
          <el-divider />
          <div class="space-y-2">
            <h3 class="text-lg font-semibold">{{ t('productDetail.description') }}</h3>
            <div class="text-sm leading-7 text-[var(--muted)]" v-html="product.detailHtml"></div>
          </div>
          <div class="pt-4">
            <el-button
              type="primary"
              size="large"
              :disabled="product.stock <= 0 || !isLoggedIn"
              @click="guardAction(addToCart)"
            >
              {{ t('productDetail.addToCart') }}
            </el-button>
            <el-button
              class="ml-3"
              size="large"
              :disabled="!isLoggedIn"
              @click="guardAction(addFavorite)"
            >
              {{ t('productDetail.addFavorite') }}
            </el-button>
          </div>
        </div>
      </div>
    </el-card>

    <el-empty v-else :description="t('productList.noProducts')" />
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getProduct } from '@/api/product.js'
import { addCartItem } from '@/api/cart.js'
import { addUserFavorite } from '@/api/user.js'
import { useAuth } from '@/composables/useAuth.js'
import { useI18n } from '@/i18n/index.js'

const route = useRoute()
const product = ref(null)
const { isLoggedIn, guardAction } = useAuth()
const { t } = useI18n()

const loadProduct = async () => {
  try {
    const productId = route.params.productId
    const res = await getProduct(productId)
    if (res.code === 200) {
      product.value = res.data
    }
  } catch (error) {
    // ignore
  }
}

const addToCart = async () => {
  if (!product.value) {
    return
  }
  try {
    const res = await addCartItem({ productId: product.value.id, quantity: 1 })
    if (res.code === 200) {
      ElMessage.success(t('cart.added'))
    } else {
      ElMessage.error(res.message)
    }
  } catch (error) {
    ElMessage.error(t('cart.addFail'))
  }
}

const addFavorite = async () => {
  if (!product.value) {
    return
  }
  try {
    const res = await addUserFavorite({ productId: product.value.id })
    if (res.code === 200) {
      ElMessage.success(t('productDetail.favorited'))
    } else {
      ElMessage.error(res.message || t('common.empty'))
    }
  } catch (error) {
    ElMessage.error(t('common.empty'))
  }
}

onMounted(loadProduct)
</script>

