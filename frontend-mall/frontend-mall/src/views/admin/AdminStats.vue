<template>
  <section class="space-y-6">
    <div>
      <p class="text-xs uppercase tracking-[0.35em] text-[var(--muted)]">Admin Console</p>
      <h1 class="section-title mt-3">{{ t('admin.statsTitle') }}</h1>
      <p class="muted-text mt-2">{{ t('admin.statsSubtitle') }}</p>
    </div>

    <div class="grid gap-4 md:grid-cols-2">
      <el-card class="border-0 bg-[var(--surface)] shadow-soft">
        <div class="text-sm text-[var(--muted)]">{{ t('admin.todayOrders') }}</div>
        <div class="mt-2 text-3xl font-semibold">{{ overview.todayOrders }}</div>
      </el-card>
      <el-card class="border-0 bg-[var(--surface)] shadow-soft">
        <div class="text-sm text-[var(--muted)]">{{ t('admin.totalSales') }}</div>
        <div class="mt-2 text-3xl font-semibold">{{ formatAmount(overview.totalSales) }}</div>
      </el-card>
    </div>

    <el-card class="border-0 bg-[var(--surface)] shadow-soft">
      <div class="flex items-center justify-between">
        <div>
          <div class="text-lg font-semibold">{{ t('admin.topProducts') }}</div>
          <div class="muted-text mt-1">{{ t('admin.topProductsHint') }}</div>
        </div>
        <el-button type="primary" text @click="fetchOverview">{{ t('common.refresh') }}</el-button>
      </div>
      <div ref="chartRef" class="mt-4 h-80 w-full"></div>
    </el-card>
  </section>
</template>

<script setup>
import { onBeforeUnmount, onMounted, ref } from 'vue'
import * as echarts from 'echarts'
import { ElMessage } from 'element-plus'
import { fetchAdminStatsOverview } from '@/api/admin/stats.js'
import { useI18n } from '@/i18n/index.js'

const overview = ref({
  todayOrders: 0,
  totalSales: 0,
  topProducts: [],
})
const chartRef = ref(null)
const { t } = useI18n()
let chartInstance

const formatAmount = (value) => {
  const amount = Number(value || 0)
  return new Intl.NumberFormat('en-US', { minimumFractionDigits: 2 }).format(amount)
}

const renderChart = () => {
  if (!chartRef.value) {
    return
  }
  if (!chartInstance) {
    chartInstance = echarts.init(chartRef.value)
  }
  const products = overview.value.topProducts || []
  const names = products.map((item) => item.productName || `#${item.productId}`)
  const quantities = products.map((item) => item.totalQuantity || 0)
  chartInstance.setOption({
    grid: { left: 40, right: 20, top: 20, bottom: 40 },
    tooltip: { trigger: 'axis' },
    xAxis: {
      type: 'category',
      data: names,
      axisLabel: { color: getComputedStyle(document.documentElement).getPropertyValue('--muted') },
      axisLine: { lineStyle: { color: 'rgba(0,0,0,0.1)' } },
    },
    yAxis: {
      type: 'value',
      axisLabel: { color: getComputedStyle(document.documentElement).getPropertyValue('--muted') },
      splitLine: { lineStyle: { color: 'rgba(0,0,0,0.08)' } },
    },
    series: [
      {
        name: t('admin.topProducts'),
        type: 'bar',
        data: quantities,
        itemStyle: { color: getComputedStyle(document.documentElement).getPropertyValue('--accent') },
        barWidth: 32,
      },
    ],
  })
}

const fetchOverview = async () => {
  try {
    const res = await fetchAdminStatsOverview()
    if (res.code === 200) {
      overview.value = res.data || overview.value
      renderChart()
    } else {
      ElMessage.error(res.message || t('common.empty'))
    }
  } catch (error) {
    ElMessage.error(t('common.empty'))
  }
}

const handleResize = () => {
  if (chartInstance) {
    chartInstance.resize()
  }
}

onMounted(() => {
  fetchOverview()
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  if (chartInstance) {
    chartInstance.dispose()
    chartInstance = null
  }
})
</script>
