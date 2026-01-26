<template>
  <div class="space-y-8 animate-fade-up">
    <section class="glass-panel p-8">
      <div class="flex flex-col gap-6 lg:flex-row lg:items-center lg:justify-between">
        <div>
          <h1 class="text-3xl font-semibold">商品探索</h1>
          <p class="mt-2 text-[var(--muted)]">筛选分类与关键词，快速找到心仪商品。</p>
        </div>
        <div class="flex flex-wrap gap-3">
          <el-input
            v-model="keyword"
            placeholder="搜索商品关键词"
            clearable
            class="w-56"
            @keyup.enter="search"
          />
          <el-select v-model="categoryId" placeholder="选择分类" clearable class="w-48">
            <el-option v-for="category in categories" :key="category.id" :label="category.name" :value="category.id" />
          </el-select>
          <el-button type="primary" :disabled="!isLoggedIn" @click="guardAction(search)">搜索</el-button>
        </div>
      </div>
    </section>

    <section>
      <div class="grid gap-6 md:grid-cols-2 xl:grid-cols-3">
        <el-card
          v-for="product in products"
          :key="product.id"
          shadow="hover"
          class="group cursor-pointer border-0 bg-white/85 transition duration-300 hover:-translate-y-1"
          :class="{ 'pointer-events-none opacity-60': !isLoggedIn }"
          @click="guardAction(() => goToProduct(product.id))"
        >
          <el-image :src="product.coverUrl" fit="cover" class="h-48 w-full rounded-2xl" />
          <div class="mt-4 space-y-2">
            <h3 class="text-lg font-semibold">{{ product.name }}</h3>
            <p class="text-sm text-[var(--muted)]">{{ product.brief }}</p>
            <div class="flex items-center justify-between pt-2">
              <span class="text-lg font-semibold text-[var(--accent)]">¥{{ product.price }}</span>
              <el-tag type="info" effect="light">库存 {{ product.stock }}</el-tag>
            </div>
          </div>
        </el-card>
      </div>

      <el-empty v-if="!products.length && !loading" description="暂无商品" class="mt-10" />
      <el-skeleton v-if="loading" :rows="6" animated class="mt-6" />
    </section>

    <section class="flex justify-center">
      <el-pagination
        v-if="total > 0"
        background
        layout="prev, pager, next"
        :page-size="size"
        :total="total"
        :current-page="page"
        :disabled="!isLoggedIn"
        @current-change="handlePageChange"
      />
    </section>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { searchProducts } from '@/api/product.js'
import { getRecommend } from '@/api/home.js'
import { useAuth } from '@/composables/useAuth.js'

const router = useRouter()
const keyword = ref('')
const categoryId = ref('')
const page = ref(1)
const size = ref(9)
const total = ref(0)
const products = ref([])
const categories = ref([])
const loading = ref(false)
const { isLoggedIn, guardAction } = useAuth()

const search = async () => {
  loading.value = true
  try {
    const params = {
      keyword: keyword.value || undefined,
      categoryId: categoryId.value || undefined,
      page: page.value - 1,
      size: size.value,
    }
    const res = await searchProducts(params)
    if (res.code === 200) {
      products.value = res.data.content
      total.value = res.data.totalElements
    }
  } catch (error) {
    console.error('搜索商品失败', error)
  } finally {
    loading.value = false
  }
}

const handlePageChange = (value) => {
  page.value = value
  search()
}

const goToProduct = (productId) => {
  router.push(`/product/${productId}`)
}

const loadCategories = async () => {
  try {
    const res = await getRecommend()
    if (res.code === 200) {
      categories.value = res.data.categories
    }
  } catch (error) {
    console.error('加载分类失败', error)
  }
}

onMounted(() => {
  loadCategories()
  search()
})
</script>
