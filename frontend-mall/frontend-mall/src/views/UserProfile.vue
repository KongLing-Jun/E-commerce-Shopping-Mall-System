<template>
  <div class="space-y-8 animate-fade-up">
    <section class="glass-panel p-8">
      <div class="flex flex-wrap items-center justify-between gap-4">
        <div>
          <h1 class="text-3xl font-semibold">{{ t('profile.title') }}</h1>
          <p class="mt-2 text-[var(--muted)]">{{ t('profile.subtitle') }}</p>
        </div>
        <div class="flex flex-wrap gap-3">
          <el-button @click="router.push('/orders')">{{ t('profile.viewOrders') }}</el-button>
          <el-button @click="router.push('/addresses')">{{ t('profile.manageAddresses') }}</el-button>
        </div>
      </div>
    </section>

    <section class="grid gap-4 md:grid-cols-3">
      <el-card class="border-0 bg-[var(--surface)] shadow-soft">
        <p class="text-sm text-[var(--muted)]">{{ t('profile.totalOrders') }}</p>
        <p class="mt-2 text-2xl font-semibold">{{ summary.totalOrders }}</p>
      </el-card>
      <el-card class="border-0 bg-[var(--surface)] shadow-soft">
        <p class="text-sm text-[var(--muted)]">{{ t('profile.pendingOrders') }}</p>
        <p class="mt-2 text-2xl font-semibold">{{ summary.pendingOrders }}</p>
      </el-card>
      <el-card class="border-0 bg-[var(--surface)] shadow-soft">
        <p class="text-sm text-[var(--muted)]">{{ t('profile.paidOrders') }}</p>
        <p class="mt-2 text-2xl font-semibold">{{ summary.paidOrders }}</p>
      </el-card>
      <el-card class="border-0 bg-[var(--surface)] shadow-soft">
        <p class="text-sm text-[var(--muted)]">{{ t('profile.shippedOrders') }}</p>
        <p class="mt-2 text-2xl font-semibold">{{ summary.shippedOrders }}</p>
      </el-card>
      <el-card class="border-0 bg-[var(--surface)] shadow-soft">
        <p class="text-sm text-[var(--muted)]">{{ t('profile.completedOrders') }}</p>
        <p class="mt-2 text-2xl font-semibold">{{ summary.completedOrders }}</p>
      </el-card>
      <el-card class="border-0 bg-[var(--surface)] shadow-soft">
        <p class="text-sm text-[var(--muted)]">{{ t('profile.favoriteCount') }}</p>
        <p class="mt-2 text-2xl font-semibold">{{ summary.favoriteCount }}</p>
      </el-card>
      <el-card class="border-0 bg-[var(--surface)] shadow-soft">
        <p class="text-sm text-[var(--muted)]">{{ t('profile.footprintCount') }}</p>
        <p class="mt-2 text-2xl font-semibold">{{ summary.footprintCount }}</p>
      </el-card>
    </section>

    <section class="grid gap-6 lg:grid-cols-[2fr,1fr]">
      <el-card class="border-0 bg-[var(--surface)] shadow-soft">
        <div class="flex items-center justify-between">
          <div>
            <h2 class="section-title">{{ t('profile.basicInfo') }}</h2>
            <p class="muted-text mt-1">{{ t('profile.basicHint') }}</p>
          </div>
          <el-tag type="success" v-if="profile.roleKey">{{ profile.roleKey }}</el-tag>
        </div>

        <el-form class="mt-6" label-position="top">
          <el-form-item :label="t('auth.username')">
            <el-input v-model="profile.username" />
          </el-form-item>
          <el-form-item :label="t('auth.phone')">
            <el-input v-model="profile.phone" />
          </el-form-item>
          <div class="grid gap-4 md:grid-cols-2">
            <el-form-item :label="t('profile.roleName')">
              <el-input v-model="profile.roleName" disabled />
            </el-form-item>
            <el-form-item :label="t('profile.createdAt')">
              <el-input v-model="profile.createdAt" disabled />
            </el-form-item>
          </div>
        </el-form>

        <div class="mt-6 flex justify-end">
          <el-button type="primary" :loading="saving" @click="saveProfile">
            {{ t('common.save') }}
          </el-button>
        </div>
      </el-card>

      <el-card class="border-0 bg-[var(--surface)] shadow-soft">
        <h2 class="section-title">{{ t('profile.securityTitle') }}</h2>
        <p class="muted-text mt-1">{{ t('profile.securityHint') }}</p>

        <el-form class="mt-6" label-position="top">
          <el-form-item :label="t('profile.oldPassword')">
            <el-input v-model="passwordForm.oldPassword" type="password" show-password />
          </el-form-item>
          <el-form-item :label="t('profile.newPassword')">
            <el-input v-model="passwordForm.newPassword" type="password" show-password />
          </el-form-item>
          <el-form-item :label="t('profile.confirmPassword')">
            <el-input v-model="passwordForm.confirmPassword" type="password" show-password />
          </el-form-item>
        </el-form>

        <div class="mt-6 flex justify-end">
          <el-button type="primary" :loading="changingPassword" @click="changePassword">
            {{ t('profile.updatePassword') }}
          </el-button>
        </div>
      </el-card>
    </section>

    <section class="grid gap-6 lg:grid-cols-2">
      <el-card class="border-0 bg-[var(--surface)] shadow-soft">
        <div class="flex items-center justify-between">
          <div>
            <h2 class="section-title">{{ t('profile.favoritesTitle') }}</h2>
            <p class="muted-text mt-1">{{ t('profile.favoritesHint') }}</p>
          </div>
          <el-button text @click="loadFavorites">{{ t('common.refresh') }}</el-button>
        </div>

        <div v-if="favorites.length" class="mt-6 space-y-4">
          <div
            v-for="item in favorites"
            :key="item.productId"
            class="flex items-center gap-4 rounded-2xl bg-white/70 p-3"
          >
            <el-image :src="item.coverUrl" class="h-16 w-24 rounded-xl" fit="cover" />
            <div class="flex-1">
              <div class="font-semibold">{{ item.name }}</div>
              <div class="text-sm text-[var(--muted)]">{{ item.brief }}</div>
              <div class="mt-1 text-sm text-[var(--accent)]">$ {{ item.price }}</div>
            </div>
            <div class="flex flex-col gap-2">
              <el-button size="small" @click="goToProduct(item.productId)">{{ t('profile.view') }}</el-button>
              <el-button size="small" type="danger" @click="removeFavorite(item.productId)">
                {{ t('profile.remove') }}
              </el-button>
            </div>
          </div>
        </div>
        <el-empty v-else :description="t('profile.noFavorites')" class="mt-6" />
      </el-card>

      <el-card class="border-0 bg-[var(--surface)] shadow-soft">
        <div class="flex items-center justify-between">
          <div>
            <h2 class="section-title">{{ t('profile.footprintsTitle') }}</h2>
            <p class="muted-text mt-1">{{ t('profile.footprintsHint') }}</p>
          </div>
          <el-button text @click="loadFootprints">{{ t('common.refresh') }}</el-button>
        </div>

        <div v-if="footprints.length" class="mt-6 space-y-4">
          <div
            v-for="item in footprints"
            :key="item.productId"
            class="flex items-center gap-4 rounded-2xl bg-white/70 p-3"
          >
            <el-image :src="item.coverUrl" class="h-16 w-24 rounded-xl" fit="cover" />
            <div class="flex-1">
              <div class="font-semibold">{{ item.name }}</div>
              <div class="text-sm text-[var(--muted)]">{{ item.brief }}</div>
              <div class="mt-1 text-sm text-[var(--accent)]">$ {{ item.price }}</div>
            </div>
            <div class="flex flex-col gap-2">
              <el-button size="small" @click="goToProduct(item.productId)">{{ t('profile.view') }}</el-button>
              <el-button size="small" type="danger" @click="removeFootprint(item.productId)">
                {{ t('profile.remove') }}
              </el-button>
            </div>
          </div>
        </div>
        <el-empty v-else :description="t('profile.noFootprints')" class="mt-6" />
      </el-card>
    </section>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import {
  changeUserPassword,
  fetchUserProfile,
  updateUserProfile,
} from '@/api/user.js'
import { getOrders } from '@/api/order.js'
import { useI18n } from '@/i18n/index.js'

const router = useRouter()
const { t } = useI18n()

const saving = ref(false)
const changingPassword = ref(false)

const profile = reactive({
  id: null,
  username: '',
  phone: '',
  roleKey: '',
  roleName: '',
  createdAt: '',
})

const summary = reactive({
  totalOrders: 0,
  pendingOrders: 0,
  paidOrders: 0,
  shippedOrders: 0,
  completedOrders: 0,
  favoriteCount: 0,
  footprintCount: 0,
})

const favorites = ref([])
const footprints = ref([])

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: '',
})

const getFavoritesFromStorage = () => {
  try {
    const stored = localStorage.getItem('userFavorites')
    return stored ? JSON.parse(stored) : []
  } catch {
    return []
  }
}

const saveFavoritesToStorage = (favoritesList) => {
  localStorage.setItem('userFavorites', JSON.stringify(favoritesList))
}

const getFootprintsFromStorage = () => {
  try {
    const stored = localStorage.getItem('userFootprints')
    return stored ? JSON.parse(stored) : []
  } catch {
    return []
  }
}

const saveFootprintsToStorage = (footprintsList) => {
  localStorage.setItem('userFootprints', JSON.stringify(footprintsList))
}

const loadProfile = async () => {
  try {
    const res = await fetchUserProfile()
    console.log('Profile API Response:', res)
    if (res.code === 200 && res.data) {
      profile.id = res.data.id
      profile.username = res.data.username
      profile.phone = res.data.phone
      profile.roleKey = res.data.roleKey
      profile.roleName = res.data.roleName
      profile.createdAt = res.data.createdAt
    } else {
      ElMessage.error(res.message || t('common.empty'))
    }
  } catch (error) {
    console.error('Load profile error:', error)
    ElMessage.error(t('common.empty'))
  }
}

const saveProfile = async () => {
  if (!profile.username || !profile.phone) {
    ElMessage.warning(t('auth.completeInfo'))
    return
  }
  saving.value = true
  try {
    const res = await updateUserProfile({
      username: profile.username,
      phone: profile.phone,
    })
    if (res.code === 200 && res.data) {
      profile.username = res.data.username
      profile.phone = res.data.phone
      profile.roleKey = res.data.roleKey
      profile.roleName = res.data.roleName
      profile.createdAt = res.data.createdAt
      if (res.data.token) {
        localStorage.setItem('token', res.data.token)
        window.dispatchEvent(new Event('auth-changed'))
      }
      ElMessage.success(t('common.save'))
    } else {
      ElMessage.error(res.message || t('common.empty'))
    }
  } catch (error) {
    ElMessage.error(t('common.empty'))
  } finally {
    saving.value = false
  }
}

const loadSummary = async () => {
  try {
    const params = { page: 0, size: 100 }
    const res = await getOrders(params)
    console.log('Orders API Response for summary:', res)
    if (res.code === 200 && res.data) {
      const orders = res.data.content || []
      summary.totalOrders = res.data.totalElements || orders.length || 0
      summary.pendingOrders = orders.filter(o => o.status === 0).length
      summary.paidOrders = orders.filter(o => o.status === 1).length
      summary.shippedOrders = orders.filter(o => o.status === 2).length
      summary.completedOrders = orders.filter(o => o.status === 3).length
    }
  } catch (error) {
    console.error('Load summary error:', error)
  }
  // 从localStorage获取收藏和足迹数量
  const favList = getFavoritesFromStorage()
  summary.favoriteCount = favList.length
  const footprintList = getFootprintsFromStorage()
  summary.footprintCount = footprintList.length
}

const loadFavorites = async () => {
  try {
    favorites.value = getFavoritesFromStorage()
    summary.favoriteCount = favorites.value.length
  } catch (error) {
    console.error('Load favorites error:', error)
    favorites.value = []
  }
}

const loadFootprints = async () => {
  try {
    footprints.value = getFootprintsFromStorage()
    summary.footprintCount = footprints.value.length
  } catch (error) {
    console.error('Load footprints error:', error)
    footprints.value = []
  }
}

const removeFootprint = async (productId) => {
  try {
    const footprintList = getFootprintsFromStorage()
    const newFootprintList = footprintList.filter(item => item.productId !== productId)
    saveFootprintsToStorage(newFootprintList)
    footprints.value = newFootprintList
    summary.footprintCount = newFootprintList.length
    ElMessage.success('已移除足迹')
  } catch (error) {
    console.error('Remove footprint error:', error)
    ElMessage.error('移除失败')
  }
}

const removeFavorite = async (productId) => {
  try {
    const favList = getFavoritesFromStorage()
    const newFavList = favList.filter(item => item.productId !== productId)
    saveFavoritesToStorage(newFavList)
    favorites.value = newFavList
    summary.favoriteCount = newFavList.length
    ElMessage.success('已移除收藏')
  } catch (error) {
    console.error('Remove favorite error:', error)
    ElMessage.error('移除失败')
  }
}

const goToProduct = (productId) => {
  router.push(`/product/${productId}`)
}

const changePassword = async () => {
  if (!passwordForm.oldPassword || !passwordForm.newPassword || !passwordForm.confirmPassword) {
    ElMessage.warning(t('auth.completeInfo'))
    return
  }
  changingPassword.value = true
  try {
    const res = await changeUserPassword({
      oldPassword: passwordForm.oldPassword,
      newPassword: passwordForm.newPassword,
      confirmPassword: passwordForm.confirmPassword,
    })
    if (res.code === 200) {
      ElMessage.success(t('profile.passwordUpdated'))
      passwordForm.oldPassword = ''
      passwordForm.newPassword = ''
      passwordForm.confirmPassword = ''
    } else {
      ElMessage.error(res.message || t('common.empty'))
    }
  } catch (error) {
    ElMessage.error(t('common.empty'))
  } finally {
    changingPassword.value = false
  }
}

onMounted(() => {
  loadProfile()
  loadSummary()
  loadFavorites()
  loadFootprints()
})
</script>
