<template>
  <div class="space-y-8">
    <section class="soft-card p-6">
      <div class="flex flex-col gap-5 xl:flex-row xl:items-end xl:justify-between">
        <div>
          <h1 class="section-title">{{ t('productList.title') }}</h1>
          <p class="muted-text mt-2">{{ t('productList.subtitle') }}</p>
        </div>
        <div class="grid gap-3 sm:grid-cols-2 lg:grid-cols-4">
          <el-input
            v-model="keyword"
            :placeholder="t('productList.searchPlaceholder')"
            clearable
            @keyup.enter="search"
          />
          <el-select v-model="categoryId" :placeholder="t('productList.categoryPlaceholder')" clearable>
            <el-option v-for="category in categories" :key="category.id" :label="category.name" :value="category.id" />
          </el-select>
          <el-button type="primary" @click="search">{{ t('common.search') }}</el-button>
          <el-button @click="resetFilters">{{ t('common.reset') }}</el-button>
        </div>
      </div>
    </section>

    <section>
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
            <strong class="text-2xl font-extrabold text-[var(--accent)]">$ {{ product.price }}</strong>
            <div class="flex items-center gap-2">
              <span class="chip">{{ t('productList.stock') }} {{ product.stock }}</span>
              <el-button size="small" type="primary" @click.stop="goToProduct(product.id)">
                {{ t('home.view') }}
              </el-button>
            </div>
          </div>
        </article>
      </div>

      <el-empty v-if="!products.length && !loading" :description="t('productList.noProducts')" class="mt-10" />
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
        @current-change="handlePageChange"
      />
    </section>
  </div>
</template>

<script setup>
import { onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { searchProducts } from '@/api/product.js'
import { getRecommend } from '@/api/home.js'
import { useI18n } from '@/i18n/index.js'

const router = useRouter()
const route = useRoute()
const keyword = ref('')
const categoryId = ref('')
const page = ref(1)
const size = ref(8)
const total = ref(0)
const products = ref([])
const categories = ref([])
const loading = ref(false)
const { t } = useI18n()

const syncFromRoute = () => {
  // 以 URL 参数为准恢复筛选条件，保证刷新和分享链接一致。
  keyword.value = route.query.keyword || ''
  categoryId.value = route.query.categoryId ? Number(route.query.categoryId) : ''
}

const search = async () => {
  loading.value = true
  try {
    const params = {
      keyword: keyword.value || undefined,
      categoryId: categoryId.value || undefined,
      // 后端分页从 0 开始，前端页码从 1 开始。
      page: page.value - 1,
      size: size.value,
    }
    const res = await searchProducts(params)
    if (res.code === 200) {
      products.value = res.data.content || []
      total.value = res.data.totalElements || 0
      router.replace({
        path: '/products',
        // 将当前筛选条件同步回 URL，便于回退与分享。
        query: {
          keyword: keyword.value || undefined,
          categoryId: categoryId.value || undefined,
        },
      })
    }
  } finally {
    loading.value = false
  }
}

const resetFilters = () => {
  keyword.value = ''
  categoryId.value = ''
  page.value = 1
  search()
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
    // 复用首页推荐接口返回的分类数据，减少额外请求。
    const res = await getRecommend()
    if (res.code === 200) {
      categories.value = res.data.categories || []
    }
  } catch {
    // ignore
  }
}

watch(
  () => route.query,
  () => {
    syncFromRoute()
  },
)

onMounted(() => {
  syncFromRoute()
  loadCategories()
  search()
})
</script>


