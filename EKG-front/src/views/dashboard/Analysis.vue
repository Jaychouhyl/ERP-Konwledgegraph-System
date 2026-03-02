<template>
  <div class="analysis-container">
    <div class="data-cards">
      <div class="card-item" v-for="(item, index) in summaryData" :key="index">
        <div class="card-header">
          <span class="card-title">{{ item.title }}</span>
          <span class="card-tag" :class="item.tagColor">{{ item.tag }}</span>
        </div>
        <div class="card-value">{{ item.value }}</div>
        <div class="card-footer">
          <span>总计: {{ item.total }}</span>
          <Icon :icon="item.icon" class="card-icon" :style="{ color: item.iconColor }" />
        </div>
      </div>
    </div>

    <div class="chart-card large">
      <div class="card-title">全年销售与采购趋势</div>
      <div ref="trendChartRef" class="chart-content"></div>
    </div>

    <div class="bottom-charts">
      <div class="chart-card">
        <div class="card-title">产品销售占比 (TOP 5)</div>
        <div ref="pieChartRef" class="chart-content"></div>
      </div>
      <div class="chart-card">
        <div class="card-title">各仓库库存预警</div>
        <div ref="barChartRef" class="chart-content"></div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { Icon } from '@iconify/vue'
import * as echarts from 'echarts'

// 顶部卡片假数据
const summaryData = ref([
  { title: '总销售额', value: '￥126,560', total: '￥3,456,700', tag: '日', tagColor: 'blue', icon: 'mdi:currency-cny', iconColor: '#1677ff' },
  { title: '新增订单', value: '3,240', total: '124,500', tag: '周', tagColor: 'green', icon: 'mdi:cart-outline', iconColor: '#52c41a' },
  { title: '库存周转', value: '18 天', total: '24 天', tag: '月', tagColor: 'orange', icon: 'mdi:store-24-hour', iconColor: '#faad14' },
  { title: '活跃客户', value: '8,846', total: '65,120', tag: '年', tagColor: 'purple', icon: 'mdi:account-group', iconColor: '#722ed1' }
])

const trendChartRef = ref(null)
const pieChartRef = ref(null)
const barChartRef = ref(null)
let trendChart, pieChart, barChart

// 初始化图表
const initCharts = () => {
  // 1. 趋势折线图
  trendChart = echarts.init(trendChartRef.value)
  trendChart.setOption({
    tooltip: { trigger: 'axis' },
    legend: { data: ['销售额', '采购额'], bottom: 0 },
    grid: { left: '3%', right: '4%', bottom: '10%', top: '5%', containLabel: true },
    xAxis: { type: 'category', boundaryGap: false, data: ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月'] },
    yAxis: { type: 'value' },
    series: [
      { name: '销售额', type: 'line', smooth: true, itemStyle: { color: '#1677ff' }, areaStyle: { color: 'rgba(22,119,255,0.1)' }, data: [120, 132, 101, 134, 90, 230, 210, 301, 334, 290, 330, 320] },
      { name: '采购额', type: 'line', smooth: true, itemStyle: { color: '#52c41a' }, data: [220, 182, 191, 234, 290, 330, 310, 201, 154, 190, 330, 410] }
    ]
  })

  // 2. 占比饼图
  pieChart = echarts.init(pieChartRef.value)
  pieChart.setOption({
    tooltip: { trigger: 'item' },
    legend: { top: 'bottom' },
    series: [{
      type: 'pie', radius: ['40%', '70%'], avoidLabelOverlap: false,
      itemStyle: { borderRadius: 10, borderColor: '#fff', borderWidth: 2 },
      label: { show: false, position: 'center' },
      data: [
        { value: 1048, name: '电子元件', itemStyle: { color: '#1677ff' } },
        { value: 735, name: '机械配件', itemStyle: { color: '#52c41a' } },
        { value: 580, name: '包装耗材', itemStyle: { color: '#faad14' } },
        { value: 484, name: '成品主机', itemStyle: { color: '#722ed1' } },
        { value: 300, name: '其他', itemStyle: { color: '#eb2f96' } }
      ]
    }]
  })

  // 3. 预警柱状图
  barChart = echarts.init(barChartRef.value)
  barChart.setOption({
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
    grid: { left: '3%', right: '4%', bottom: '5%', top: '5%', containLabel: true },
    xAxis: { type: 'value' },
    yAxis: { type: 'category', data: ['北京仓', '上海仓', '广州仓', '深圳仓', '成都仓'] },
    series: [{
      name: '预警SKU数量', type: 'bar', barWidth: '50%',
      itemStyle: { color: '#ff4d4f', borderRadius: [0, 4, 4, 0] },
      data: [10, 52, 200, 334, 390]
    }]
  })
}

// 监听窗口大小变化以适配图表
const resizeCharts = () => {
  trendChart?.resize(); pieChart?.resize(); barChart?.resize()
}

onMounted(() => {
  setTimeout(initCharts, 100) // 延迟确保DOM挂载完毕
  window.addEventListener('resize', resizeCharts)
})

onUnmounted(() => {
  window.removeEventListener('resize', resizeCharts)
  trendChart?.dispose(); pieChart?.dispose(); barChart?.dispose()
})
</script>

<style scoped>
.analysis-container {
  display: flex;
  flex-direction: column;
  gap: 16px;
  /* 移除外边距，因为 layout-content 已经有 padding 了 */
}

/* 通用卡片样式：完全继承暗黑模式变量 */
.chart-card, .card-item {
  background: var(--content-bg);
  border: 1px solid var(--border-color);
  border-radius: 8px;
  padding: 20px;
  transition: all 0.3s;
}
.chart-card:hover, .card-item:hover {
  box-shadow: 0 4px 12px rgba(0,0,0,0.05);
}

/* 顶部四个数据卡片 */
.data-cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: var(--text-color-secondary);
  font-size: 14px;
}
.card-tag {
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
  border: 1px solid transparent;
}
.card-tag.blue { color: #1677ff; border-color: #91caff; background: #e6f4ff; }
.card-tag.green { color: #52c41a; border-color: #b7eb8f; background: #f6ffed; }
.card-tag.orange { color: #faad14; border-color: #ffe58f; background: #fffbe6; }
.card-tag.purple { color: #722ed1; border-color: #d3adf7; background: #f9f0ff; }
/* 暗黑模式修正 Tag 背景 */
.dark-theme .card-tag { background: transparent; }

.card-value {
  font-size: 28px;
  font-weight: bold;
  color: var(--text-color);
  margin: 12px 0;
}
.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 14px;
  color: var(--text-color-secondary);
  border-top: 1px solid var(--border-color);
  padding-top: 12px;
}
.card-icon {
  font-size: 20px;
}

/* 图表区域 */
.card-title {
  font-size: 16px;
  font-weight: bold;
  color: var(--text-color);
  margin-bottom: 16px;
}
.chart-content {
  width: 100%;
  height: 300px;
}
.chart-card.large .chart-content {
  height: 350px;
}

/* 底部两列图表 */
.bottom-charts {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}
</style>