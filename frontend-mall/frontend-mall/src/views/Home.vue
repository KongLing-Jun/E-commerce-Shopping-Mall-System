<template>
  <div class="space-y-10">
    <section class="grid gap-4 lg:grid-cols-2">
      <article
        v-for="(hero, index) in heroCards"
        :key="index"
        class="relative h-[320px] overflow-hidden rounded-2xl border border-[var(--line)]"
      >
        <img :src="hero.image" :alt="hero.title" class="h-full w-full object-cover" />
        <div class="absolute inset-0 bg-gradient-to-t from-slate-900/70 via-slate-900/25 to-transparent"></div>
        <div class="absolute bottom-8 left-8 max-w-[70%] text-white">
          <span class="inline-flex rounded-full bg-blue-600 px-3 py-1 text-xs font-bold uppercase tracking-wide">
            {{ hero.badge }}
          </span>
          <h2 class="mt-4 text-4xl font-extrabold leading-tight">{{ hero.title }}</h2>
          <p class="mt-2 text-base text-white/90">{{ hero.subtitle }}</p>
          <el-button class="mt-5" size="large" @click="goToProducts(hero.categoryId)">
            {{ hero.action }}
          </el-button>
        </div>
      </article>
    </section>

    <section class="space-y-4">
      <div class="flex items-center justify-between">
        <h3 class="text-4xl font-extrabold">{{ dual('热门浏览', 'Popular Picks') }}</h3>
        <el-button text type="primary" @click="goToProducts()">{{ t('home.viewAll') }}</el-button>
      </div>

      <div class="grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
        <article
          v-for="product in hotProducts.slice(0, 4)"
          :key="product.id"
          class="soft-card group cursor-pointer overflow-hidden rounded-2xl border border-[var(--line)] bg-[var(--surface)] p-3"
          @click="goToProduct(product.id)"
        >
          <div class="relative">
            <img :src="product.coverUrl" :alt="product.name" class="h-56 w-full rounded-xl object-cover" />
          </div>
          <div class="mt-3">
            <h4 class="line-clamp-1 text-lg font-bold">{{ product.name }}</h4>
            <p class="text-sm text-[var(--muted)]">{{ product.brief }}</p>
            <div class="mt-3 flex items-center justify-between">
              <strong class="text-2xl font-extrabold text-[var(--accent)]">$ {{ formatPrice(product.price) }}</strong>
              <el-button size="small" type="primary" @click.stop="goToProduct(product.id)">
                {{ t('home.view') }}
              </el-button>
            </div>
          </div>
        </article>
      </div>
    </section>

    <section class="space-y-4 pb-6">
      <h3 class="text-4xl font-extrabold">{{ dual('精选商品', 'Featured Catalog') }}</h3>
      <div class="grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
        <article
          v-for="product in recommendProducts"
          :key="`rec-${product.id}`"
          class="cursor-pointer overflow-hidden rounded-2xl border border-[var(--line)] bg-[var(--surface)] p-3 transition hover:-translate-y-1"
          @click="goToProduct(product.id)"
        >
          <img :src="product.coverUrl" :alt="product.name" class="h-60 w-full rounded-xl object-cover" />
          <h4 class="mt-3 text-lg font-bold leading-tight">{{ product.name }}</h4>
          <p class="text-base font-semibold text-[var(--accent)]">$ {{ formatPrice(product.price) }}</p>
        </article>
      </div>
      <el-empty v-if="!loading && !recommendProducts.length" :description="t('common.empty')" />
      <div v-if="promoProducts.length" class="space-y-4 pt-4">
        <h3 class="text-3xl font-extrabold">{{ dual('专题推荐', 'Curated Topics') }}</h3>
        <div class="grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
          <article
            v-for="product in promoProducts.slice(0, 4)"
            :key="`promo-${product.id}`"
            class="cursor-pointer overflow-hidden rounded-2xl border border-[var(--line)] bg-[var(--surface)] p-3 transition hover:-translate-y-1"
            @click="goToProduct(product.id)"
          >
            <img :src="product.coverUrl" :alt="product.name" class="h-60 w-full rounded-xl object-cover" />
            <h4 class="mt-3 text-lg font-bold leading-tight">{{ product.name }}</h4>
            <p class="text-base font-semibold text-[var(--accent)]">$ {{ formatPrice(product.price) }}</p>
          </article>
        </div>
      </div>
      <el-skeleton v-if="loading" :rows="4" animated />
    </section>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getBanners, getRecommend } from '@/api/home.js'
import { useI18n } from '@/i18n/index.js'

const router = useRouter()
const banners = ref([])
const categories = ref([])
const recommendProducts = ref([])
const hotProducts = ref([])
const promoProducts = ref([])
const loading = ref(true)
const { t, locale } = useI18n()
const dual = (zh, en) => (locale.value === 'zh' ? zh : en)

const fallbackHeroImages = [
  'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?auto=format&fit=crop&w=1400&q=80',
  'https://images.unsplash.com/photo-1498050108023-c5249f4df085?auto=format&fit=crop&w=1400&q=80',
]

const heroCards = computed(() => {
  const leftCategory = categories.value[0]
  const rightCategory = categories.value[1]
  return [
    {
      badge: dual('本周推荐', 'Weekly Picks'),
      title: dual('智能生活精选', 'Smart Living Picks'),
      subtitle: dual('精选3C数码与智能家居，查看完整商品信息。', 'Browse selected 3C devices and open full product details.'),
      image: banners.value[0]?.imageUrl || fallbackHeroImages[0],
      action: dual('浏览商品', 'Browse Products'),
      categoryId: leftCategory?.id,
    },
    {
      badge: dual('最新发布', 'New Arrivals'),
      title: dual('科技必备', 'Tech Essentials'),
      subtitle: dual('耳机、穿戴和办公电子设备，按分类浏览。', 'Discover audio, wearable, and office electronics by category.'),
      image: banners.value[1]?.imageUrl || fallbackHeroImages[1],
      action: dual('查看分类', 'View Category'),
      categoryId: rightCategory?.id,
    },
  ]
})

const formatPrice = (value) => Number(value || 0).toFixed(2)

const loadHomeData = async () => {
  loading.value = true
  try {
    const [bannerRes, recommendRes] = await Promise.all([getBanners(), getRecommend()])
    if (bannerRes.code === 200) {
      banners.value = bannerRes.data || []
    }
    if (recommendRes.code === 200) {
      categories.value = recommendRes.data.categories || []
      recommendProducts.value = recommendRes.data.recommendProducts || recommendRes.data.products || []
      hotProducts.value = recommendRes.data.hotProducts || recommendProducts.value.slice(0, 4)
      promoProducts.value = recommendRes.data.promoProducts || []
    }
  } catch {
    ElMessage.error(dual('首页数据加载失败', 'Failed to load home data'))
  } finally {
    loading.value = false
  }
}

const goToProduct = (productId) => {
  router.push(`/product/${productId}`)
}

const goToProducts = (categoryId) => {
  if (categoryId) {
    router.push({ path: '/products', query: { categoryId } })
    return
  }
  router.push('/products')
}

onMounted(loadHomeData)
</script>
