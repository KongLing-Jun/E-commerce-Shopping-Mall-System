<template>
  <div class="grid gap-8 lg:grid-cols-[260px_1fr]">
    <aside class="h-fit rounded-2xl border border-[var(--line)] bg-[var(--surface)] p-5">
      <div class="flex items-center justify-between">
        <h2 class="text-2xl font-extrabold">{{ t('home.categories') }}</h2>
        <el-button text type="primary" @click="loadCategories">{{ t('common.refresh') }}</el-button>
      </div>
      <div class="mt-4 space-y-2">
        <button
          v-for="category in categories"
          :key="category.id"
          type="button"
          class="w-full rounded-xl px-3 py-2 text-left text-sm font-semibold"
          :class="activeCategoryId === category.id ? 'bg-[var(--highlight)] text-[var(--accent)]' : 'hover:bg-[var(--surface-soft)]'"
          @click="selectCategory(category)"
        >
          {{ category.name }}
        </button>
      </div>
      <el-empty v-if="!categories.length && !loading" :description="t('common.empty')" class="mt-4" />
    </aside>

    <section class="space-y-6">
      <header class="flex flex-wrap items-end justify-between gap-3">
        <div>
          <p class="text-sm text-[var(--muted)]">{{ t('nav.home') }} / {{ t('home.categories') }}</p>
          <h1 class="mt-2 text-4xl font-extrabold">
            {{ activeCategory?.name || t('home.categories') }}
          </h1>
          <p class="muted-text mt-2">{{ t('productList.subtitle') }}</p>
        </div>
        <el-button type="primary" @click="goToProducts">{{ t('home.viewAll') }}</el-button>
      </header>

      <div class="grid gap-4 sm:grid-cols-2 xl:grid-cols-4">
        <article
          v-for="product in products"
          :key="product.id"
          class="cursor-pointer overflow-hidden rounded-2xl border border-[var(--line)] bg-[var(--surface)] p-3 transition hover:-translate-y-1"
          @click="goToProduct(product.id)"
        >
          <img :src="product.coverUrl" :alt="product.name" class="h-56 w-full rounded-xl object-cover" />
          <h3 class="mt-3 line-clamp-1 text-lg font-extrabold">{{ product.name }}</h3>
          <p class="line-clamp-2 text-sm text-[var(--muted)]">{{ product.brief }}</p>
          <div class="mt-3 flex items-center justify-between">
            <strong class="text-2xl font-extrabold text-[var(--accent)]">$ {{ formatPrice(product.price) }}</strong>
            <el-button size="small" type="primary" @click.stop="goToProduct(product.id)">
              {{ t('home.view') }}
            </el-button>
          </div>
        </article>
      </div>

      <el-empty v-if="!products.length && !loading" :description="t('productList.noProducts')" />
      <el-skeleton v-if="loading" :rows="6" animated />

      <div class="flex justify-center">
        <el-pagination
          v-if="total > 0"
          background
          layout="prev, pager, next"
          :page-size="size"
          :total="total"
          :current-page="page"
          @current-change="handlePageChange"
        />
      </div>
    </section>
  </div>
</template>

<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getCategories } from '@/api/category.js'
import { searchProducts } from '@/api/product.js'
import { useI18n } from '@/i18n/index.js'

const router = useRouter()
const route = useRoute()
const categories = ref([])
const activeCategoryId = ref(null)
const products = ref([])
const loading = ref(false)
const page = ref(1)
const size = ref(8)
const total = ref(0)
const { t } = useI18n()

// 功能：根据当前选中分类生成标题展示。
const activeCategory = computed(() => categories.value.find((item) => item.id === activeCategoryId.value))

// 功能：统一价格显示格式。
const formatPrice = (value) => Number(value || 0).toFixed(2)

// 功能：从路由参数恢复选中的分类。
const syncFromRoute = () => {
  const { categoryId } = route.query
  if (categoryId !== undefined) {
    const parsed = Number(categoryId)
    activeCategoryId.value = Number.isNaN(parsed) ? null : parsed
  }
}

// 功能：将当前分类同步到地址栏，便于分享与回退。
const updateRoute = (categoryId) => {
  const query = categoryId ? { categoryId } : {}
  router.replace({ path: '/categories', query })
}

// 功能：加载分类列表并设置默认选中项。
const loadCategories = async () => {
  const res = await getCategories({ parentId: 0 })
  if (res.code === 200) {
    categories.value = res.data || []
    if (!activeCategoryId.value && categories.value.length) {
      activeCategoryId.value = categories.value[0].id
      updateRoute(activeCategoryId.value)
    }
    if (activeCategoryId.value && !categories.value.find((item) => item.id === activeCategoryId.value)) {
      activeCategoryId.value = categories.value[0]?.id || null
      updateRoute(activeCategoryId.value)
    }
  }
}

// 功能：按选中分类拉取商品列表。
const loadProducts = async () => {
  if (!activeCategoryId.value) {
    products.value = []
    total.value = 0
    return
  }
  loading.value = true
  try {
    const res = await searchProducts({
      categoryId: activeCategoryId.value,
      page: page.value - 1,
      size: size.value,
    })
    if (res.code === 200) {
      products.value = res.data.content || []
      total.value = res.data.totalElements || 0
    }
  } finally {
    loading.value = false
  }
}

// 功能：切换分类并刷新商品列表。
const selectCategory = (category) => {
  activeCategoryId.value = category.id
  page.value = 1
  updateRoute(activeCategoryId.value)
  loadProducts()
}

// 功能：分页切换后重新加载当前分类商品。
const handlePageChange = (value) => {
  page.value = value
  loadProducts()
}

// 功能：跳转到商品详情。
const goToProduct = (productId) => {
  router.push(`/product/${productId}`)
}

// 功能：跳转到完整商品列表并保留分类筛选。
const goToProducts = () => {
  if (activeCategoryId.value) {
    router.push({ path: '/products', query: { categoryId: activeCategoryId.value } })
    return
  }
  router.push('/products')
}

watch(
  () => route.query.categoryId,
  () => {
    syncFromRoute()
    loadProducts()
  },
)

onMounted(async () => {
  syncFromRoute()
  await loadCategories()
  await loadProducts()
})
</script>
