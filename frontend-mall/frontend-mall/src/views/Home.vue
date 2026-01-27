<template>
  <div class="space-y-12 animate-fade-up">
    <section class="glass-panel p-10">
      <div class="grid gap-10 lg:grid-cols-[1.15fr_0.85fr] lg:items-center">
        <div>
          <p class="text-xs uppercase tracking-[0.4em] text-[var(--muted)]">{{ t('home.discover') }}</p>
          <h1 class="mt-4 text-4xl font-semibold leading-tight lg:text-5xl">
            {{ t('home.heroTitle') }}
          </h1>
          <p class="mt-4 text-lg text-[var(--muted)]">
            {{ t('home.heroSubtitle') }}
          </p>
          <div class="mt-6 flex flex-wrap gap-3">
            <el-button
              type="primary"
              size="large"
              :disabled="!isLoggedIn"
              @click="guardAction(() => router.push('/products'))"
            >
              {{ t('home.browse') }}
            </el-button>
            <el-button size="large" plain :disabled="!isLoggedIn" @click="guardAction(scrollToRecommend)">
              {{ t('home.viewRecommend') }}
            </el-button>
          </div>
          <div class="mt-6 flex flex-wrap gap-2">
            <el-tag type="warning" effect="light">{{ t('home.tags.fast') }}</el-tag>
            <el-tag type="success" effect="light">{{ t('home.tags.secure') }}</el-tag>
            <el-tag type="info" effect="light">{{ t('home.tags.curated') }}</el-tag>
          </div>
        </div>

        <el-card class="border-0 bg-[var(--surface)] shadow-soft">
          <template #header>
            <div class="flex items-center justify-between">
              <span class="text-sm font-semibold text-[var(--muted)]">{{ t('home.bannerTitle') }}</span>
              <el-tag type="danger" round>HOT</el-tag>
            </div>
          </template>
          <el-carousel height="240px" indicator-position="outside">
            <el-carousel-item v-for="banner in banners" :key="banner.id">
              <el-image :src="banner.imageUrl" fit="cover" class="h-full w-full rounded-xl" />
            </el-carousel-item>
            <el-carousel-item v-if="loading">
              <div
                class="flex h-full w-full animate-pulse items-center justify-center rounded-xl bg-[var(--surface)] text-[var(--muted)]"
              >
                {{ t('common.loading') }}
              </div>
            </el-carousel-item>
            <el-carousel-item v-else-if="!banners.length">
              <div
                class="flex h-full items-center justify-center rounded-xl border border-dashed border-[var(--line)] text-[var(--muted)]"
              >
                {{ t('home.noBanners') }}
              </div>
            </el-carousel-item>
          </el-carousel>
        </el-card>
      </div>
    </section>

    <section>
      <div class="flex items-center justify-between">
        <h2 class="section-title">{{ t('home.categories') }}</h2>
        <el-button text :disabled="!isLoggedIn" @click="guardAction(() => router.push('/products'))">
          {{ t('home.allCategories') }}
        </el-button>
      </div>
      <div class="mt-5 grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
        <el-card
          v-for="category in categories"
          :key="category.id"
          shadow="hover"
          class="border-0 bg-[var(--surface)] transition duration-300 hover:-translate-y-1"
        >
          <div class="flex items-center justify-between">
            <div>
              <h3 class="text-lg font-semibold">{{ category.name }}</h3>
              <p class="muted-text">{{ t('home.categoryHint') }}</p>
            </div>
            <div class="rounded-2xl bg-[var(--highlight)] px-3 py-1 text-xs font-semibold">
              {{ category.id }}
            </div>
          </div>
        </el-card>
        <el-skeleton v-if="loading" :rows="3" animated />
      </div>
    </section>

    <section ref="recommendSection">
      <div class="flex items-center justify-between">
        <h2 class="section-title">{{ t('home.recommended') }}</h2>
        <el-button type="primary" plain :disabled="!isLoggedIn" @click="guardAction(() => router.push('/products'))">
          {{ t('home.viewAll') }}
        </el-button>
      </div>
      <div class="mt-6 grid gap-6 md:grid-cols-2 xl:grid-cols-3">
        <el-card
          v-for="product in products"
          :key="product.id"
          shadow="hover"
          class="group cursor-pointer border-0 bg-[var(--surface-strong)] transition duration-300 hover:-translate-y-1"
          :class="{ 'pointer-events-none opacity-60': !isLoggedIn }"
          @click="guardAction(() => goToProduct(product.id))"
        >
          <el-image :src="product.coverUrl" fit="cover" class="h-44 w-full rounded-2xl" />
          <div class="mt-4">
            <h3 class="text-lg font-semibold">{{ product.name }}</h3>
            <p class="mt-1 text-sm text-[var(--muted)]">{{ product.brief }}</p>
            <div class="mt-4 flex items-center justify-between">
              <span class="text-lg font-semibold text-[var(--accent)]">$ {{ product.price }}</span>
              <el-button size="small" type="primary" plain :disabled="!isLoggedIn">{{ t('home.view') }}</el-button>
            </div>
          </div>
        </el-card>
        <el-skeleton v-if="loading" :rows="4" animated />
      </div>
    </section>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { getBanners, getRecommend } from '@/api/home.js'
import { useAuth } from '@/composables/useAuth.js'
import { useI18n } from '@/i18n/index.js'

const router = useRouter()
const banners = ref([])
const categories = ref([])
const products = ref([])
const loading = ref(true)
const recommendSection = ref(null)
const { isLoggedIn, guardAction } = useAuth()
const { t } = useI18n()

const loadHomeData = async () => {
  loading.value = true
  try {
    const [bannerRes, recommendRes] = await Promise.all([getBanners(), getRecommend()])
    if (bannerRes.code === 200) {
      banners.value = bannerRes.data
    }
    if (recommendRes.code === 200) {
      categories.value = recommendRes.data.categories
      products.value = recommendRes.data.products
    }
  } catch (error) {
    // ignore
  } finally {
    loading.value = false
  }
}

const goToProduct = (productId) => {
  router.push(`/product/${productId}`)
}

const scrollToRecommend = () => {
  if (recommendSection.value) {
    recommendSection.value.scrollIntoView({ behavior: 'smooth' })
  }
}

onMounted(loadHomeData)
</script>

