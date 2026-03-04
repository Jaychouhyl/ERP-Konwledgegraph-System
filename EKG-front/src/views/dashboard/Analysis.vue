<template>
  <div class="analysis-container">

    <div class="message-container">
      <transition-group name="msg-fade">
        <div v-for="msg in messages" :key="msg.id" class="custom-message" :class="msg.type">
          <Icon :icon="msg.icon" class="msg-icon" />
          <span>{{ msg.text }}</span>
        </div>
      </transition-group>
    </div>

    <div class="data-cards">
      <div class="card-item" v-for="(item, index) in summaryData" :key="index">
        <div class="card-header">
          <span class="card-title">{{ item.title }}</span>
          <span class="card-tag" :class="item.tagColor">{{ item.tag }}</span>
        </div>
        <div class="card-value" :class="{ 'text-danger': item.isAlert }">{{ item.value }}</div>
        <div class="card-footer">
          <span>{{ item.footerText }}</span>
          <Icon :icon="item.icon" class="card-icon" :style="{ color: item.iconColor }" />
        </div>
      </div>
    </div>

    <div class="middle-charts">
      <div class="chart-card trend-card">
        <div class="card-title">2026 年度营收、支出与现金流趋势</div>
        <div ref="trendChartRef" class="chart-content"></div>
      </div>
      <div class="chart-card asset-card">
        <div class="card-title">企业总资产盘点结构</div>
        <div ref="pieChartRef" class="chart-content"></div>
      </div>
    </div>

    <div class="bottom-radar">
      <div class="radar-card">
        <div class="card-title flex-between">
          <span class="with-icon">
            <Icon icon="mdi:radar" class="text-danger spin-slow" /> 高危供应链预警 (AI 图谱提供)
          </span>
          <button class="text-btn" @click="showReportModal = true">查看完整报告</button>
        </div>
        <ul class="radar-list">
          <li v-for="risk in supplierRisks" :key="risk.id" class="risk-item">
            <div class="risk-info">
              <span class="risk-name">{{ risk.name }}</span>
              <div class="risk-tags">
                <span class="risk-tag" v-for="tag in risk.tags" :key="tag">{{ tag }}</span>
              </div>
            </div>
            <div class="risk-action">
              <span class="status-indicator warning"><span class="dot"></span> 建议冻结</span>
            </div>
          </li>
        </ul>
      </div>

      <div class="radar-card">
        <div class="card-title flex-between">
          <span class="with-icon">
            <Icon icon="mdi:alert-box-outline" class="text-warning" /> 异常库存与交期警报
          </span>
        </div>
        <ul class="radar-list">
          <li v-for="alert in inventoryAlerts" :key="alert.id" class="risk-item">
            <div class="risk-info">
              <span class="risk-name font-mono">{{ alert.sku }}</span>
              <span class="risk-desc">{{ alert.desc }}</span>
            </div>
            <div class="risk-action">
              <span class="text-danger font-bold">{{ alert.value }}</span>
            </div>
          </li>
        </ul>
      </div>
    </div>

    <transition name="modal-fade">
      <div class="modal-overlay" v-if="showReportModal" @click.self="showReportModal = false">
        <div class="modal-content report-modal">
          <div class="modal-header bg-danger-header">
            <h3>
              <Icon icon="mdi:shield-search" /> AI 供应链图谱风险评估报告
            </h3>
            <Icon icon="mdi:close" class="modal-close" @click="showReportModal = false" />
          </div>
          <div class="modal-body">
            <div class="report-meta">
              <span>
                <Icon icon="mdi:clock-outline" /> 报告生成时间：2026-03-03 08:30
              </span>
              <span>
                <Icon icon="mdi:brain" /> 分析引擎：SmartX Neo4j + RAG大模型
              </span>
            </div>
            <div class="report-content">
              <h4>一、 核心风险拦截概况</h4>
              <p>本月系统共进行 <b>128</b> 次自动穿透测算，拦截高危采购动作 <span class="text-danger font-bold">3</span> 次。</p>

              <h4>二、 高危节点溯源分析</h4>
              <div class="risk-node-box">
                <div class="node-title">
                  <Icon icon="mdi:alert-circle" /> 异常节点：某劣质外包厂 (Supplier)
                </div>
                <ul>
                  <li><b>关联特征：</b>与黑名单企业存在法人交叉持股，近期涉诉 2 起。</li>
                  <li><b>履约历史：</b>近半年交货延迟率骤升至 28%，次品率达 5.2%。</li>
                  <li><b>穿透影响：</b>若发生断供，将直接导致「环保包装盒组件」停线，波及下游 3 个成品 SKU 无法发货。</li>
                </ul>
              </div>

              <h4>三、 AI 处置与优化建议</h4>
              <p>1. 已自动冻结该供应商的系统下单权限。<br />2. 建议由采购部立即启动备选供应商（推荐节点：绿点科技）的资质核验与打样。</p>
            </div>
          </div>
          <div class="modal-footer">
            <button class="vben-btn default" @click="showReportModal = false">关闭阅览</button>
            <button class="vben-btn primary" @click="downloadReport">
              <Icon icon="mdi:download" /> 导出 PDF
            </button>
          </div>
        </div>
      </div>
    </transition>

  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { Icon } from '@iconify/vue'
import * as echarts from 'echarts'

// ================= 全局 Message =================
const messages = ref([])
let msgId = 0
const showMessage = (text, type = 'success') => {
  const id = msgId++
  let icon = 'mdi:information'
  if (type === 'error') icon = 'mdi:close-circle'
  if (type === 'success') icon = 'mdi:check-circle'
  messages.value.push({ id, text, type, icon })
  setTimeout(() => { messages.value = messages.value.filter(m => m.id !== id) }, 3000)
}

// ================= 数据状态 =================
const summaryData = ref([
  { title: '实时可用现金流', value: '￥1,500,000', footerText: '环比上月 +12.5%', tag: '财务', tagColor: 'blue', icon: 'mdi:bank-check', iconColor: '#1677ff' },
  { title: '本月销售总额', value: '￥15,997,500', footerText: '已发货订单: 24 笔', tag: '营收', tagColor: 'green', icon: 'mdi:chart-line-variant', iconColor: '#52c41a' },
  { title: '本月采购支出', value: '￥5,580,000', footerText: '待付款账单: 2 笔', tag: '支出', tagColor: 'orange', icon: 'mdi:cart-arrow-down', iconColor: '#faad14' },
  { title: 'AI 拦截与风控项', value: '3 项警告', footerText: '包含 1 个高危供应商', tag: '风控', tagColor: 'red', icon: 'mdi:shield-alert', iconColor: '#ff4d4f', isAlert: true }
])

const supplierRisks = ref([
  { id: 1, name: '某劣质外包厂', tags: ['交期延迟 28%', '违约诉讼 x2'] },
  { id: 2, name: '华东包装材料厂', tags: ['质量抽检不合格', '资金链异常'] }
])

const inventoryAlerts = ref([
  { id: 1, sku: 'SKU-RAW-IMX989', desc: '索尼主摄物料低于安全库存', value: '缺口 700 件' },
  { id: 2, sku: 'SKU-RAW-BAT5K', desc: '高密度电池物料低于安全库存', value: '缺口 850 件' },
  { id: 3, sku: 'SO-20260305-001', desc: '销售单距要求发货日仅剩 2 天', value: '加急出库' }
])

// ================= 图表逻辑 =================
const trendChartRef = ref(null)
const pieChartRef = ref(null)
let trendChart, pieChart

const initCharts = () => {
  trendChart = echarts.init(trendChartRef.value)
  trendChart.setOption({
    tooltip: { trigger: 'axis', axisPointer: { type: 'cross' } },
    legend: { data: ['销售营收', '采购支出', '现金流结余'], bottom: 0 },
    grid: { left: '2%', right: '2%', bottom: '12%', top: '15%', containLabel: true },
    xAxis: { type: 'category', data: ['10月', '11月', '12月', '1月', '2月', '3月(至今)'] },
    yAxis: [
      { type: 'value', name: '收支金额(万)', position: 'left', splitLine: { lineStyle: { type: 'dashed', color: '#f0f0f0' } } },
      { type: 'value', name: '现金结余(万)', position: 'right', splitLine: { show: false } }
    ],
    series: [
      { name: '销售营收', type: 'bar', barWidth: '30%', itemStyle: { color: '#52c41a', borderRadius: [4, 4, 0, 0] }, data: [1200, 1350, 1800, 950, 1100, 1599] },
      { name: '采购支出', type: 'bar', barWidth: '30%', itemStyle: { color: '#faad14', borderRadius: [4, 4, 0, 0] }, data: [800, 900, 1100, 700, 850, 558] },
      { name: '现金流结余', type: 'line', yAxisIndex: 1, smooth: true, symbolSize: 8, itemStyle: { color: '#1677ff' }, lineStyle: { width: 3 }, data: [150, 180, 250, 120, 140, 150] }
    ]
  })

  pieChart = echarts.init(pieChartRef.value)
  pieChart.setOption({
    tooltip: { trigger: 'item', formatter: '{a} <br/>{b}: ￥{c}万 ({d}%)' },
    legend: { top: 'bottom', icon: 'circle' },
    series: [{
      name: '资产盘点', type: 'pie', radius: ['50%', '75%'], avoidLabelOverlap: false,
      itemStyle: { borderRadius: 6, borderColor: '#fff', borderWidth: 2 },
      label: { show: false },
      data: [
        { value: 450, name: '成品库存总值', itemStyle: { color: '#722ed1' } },
        { value: 380, name: '原料库存总值', itemStyle: { color: '#1677ff' } },
        { value: 150, name: '活期现金可用', itemStyle: { color: '#52c41a' } },
        { value: 80,  name: '已冻结/待付款', itemStyle: { color: '#ff4d4f' } }
      ]
    }]
  })
}

const resizeCharts = () => { trendChart?.resize(); pieChart?.resize() }

// ================= 修复：真正生成并导出 PDF =================
const showReportModal = ref(false)

const downloadReport = () => {
  showMessage('正在调用浏览器引擎渲染 A4 报告，请在弹出的窗口中选择“另存为 PDF”...', 'success')
  
  // 创建一个隐藏的打印窗口
  const printWindow = window.open('', '_blank', 'width=800,height=900')
  
  // 注入精心排版的报告 HTML
  const reportHTML = `
    <!DOCTYPE html>
    <html lang="zh-CN">
    <head>
      <meta charset="UTF-8">
      <title>AI 供应链图谱风险评估报告</title>
      <style>
        body { font-family: 'Helvetica Neue', Arial, sans-serif; padding: 40px 60px; color: #333; line-height: 1.8; }
        .header { border-bottom: 3px solid #ff4d4f; padding-bottom: 20px; margin-bottom: 30px; }
        h1 { color: #ff4d4f; font-size: 28px; margin: 0; display: flex; align-items: center; }
        .meta { color: #666; font-size: 13px; margin-top: 10px; }
        h3 { color: #1677ff; border-left: 4px solid #1677ff; padding-left: 10px; margin-top: 40px; }
        .danger-box { background: #fff1f0; border: 1px solid #ffccc7; padding: 20px; border-radius: 6px; margin: 20px 0; }
        .danger-box h4 { color: #ff4d4f; margin-top: 0; font-size: 16px; }
        ul { padding-left: 20px; }
        li { margin-bottom: 10px; }
        .footer { margin-top: 80px; text-align: center; color: #999; font-size: 12px; border-top: 1px solid #eee; padding-top: 20px; }
      </style>
    </head>
    <body>
      <div class="header">
        <h1>SmartX ERP - AI 供应链风险评估报告</h1>
        <div class="meta">
          <span>报告生成时间：${new Date().toLocaleString()}</span><br/>
          <span>分析引擎：SmartX Neo4j 知识图谱 + RAG 大语言模型</span>
        </div>
      </div>

      <h3>一、 核心风险拦截概况</h3>
      <p>本月系统自动监听供应链流转数据，共进行 <b>128</b> 次深层节点穿透测算，拦截高危采购动作 <span style="color:#ff4d4f;font-weight:bold;">3</span> 次，成功规避潜在资金损失预估达 <b>￥850,000</b>。</p>

      <h3>二、 高危节点溯源分析</h3>
      <div class="danger-box">
        <h4>异常节点拦截记录：某劣质外包厂 (Supplier Entity)</h4>
        <ul>
          <li><b>关联特征：</b>通过图谱多跳检索发现，该企业与系统黑名单企业存在法人交叉持股现象，且近期外部司法节点显示涉诉 2 起。</li>
          <li><b>履约历史：</b>近半年历史交货延迟率骤升至 28%，来料质检次品率达 5.2%。</li>
          <li><b>穿透影响：</b>若发生断供，将直接导致「环保包装盒组件」停线，进而波及下游 3 个成品 SKU 无法按期发货。</li>
        </ul>
      </div>

      <h3>三、 AI 处置与优化建议</h3>
      <p>1. 系统已自动冻结该供应商的下单权限，相关待审单据已被驳回打回草稿箱。</p>
      <p>2. 建议采购部立即启动备选供应商的资质核验与打样测试。</p>
      <p><b>系统智能推荐备选节点：</b>【绿点科技】（信用评级 A，历史履约率 99.5%）</p>

      <div class="footer">
        此报告由 SmartX 智能决策系统自动生成，未经授权严禁对外泄露。
      </div>
    </body>
    </html>
  `
  
  // 写入并触发打印
  printWindow.document.write(reportHTML)
  printWindow.document.close()
  
  // 延迟半秒确保样式渲染完毕后唤起打印窗口
  setTimeout(() => {
    printWindow.print()
    showReportModal.value = false // 打印唤起后自动关闭当前弹窗
  }, 500)
}

onMounted(() => {
  setTimeout(initCharts, 150)
  window.addEventListener('resize', resizeCharts)
})

onUnmounted(() => {
  window.removeEventListener('resize', resizeCharts)
  trendChart?.dispose(); pieChart?.dispose()
})
</script>

<style scoped>
.analysis-container {
  display: flex;
  flex-direction: column;
  gap: 16px;
  position: relative;
}

/* 全局 Toast */
.message-container {
  position: fixed;
  top: 20px;
  left: 50%;
  transform: translateX(-50%);
  z-index: 10000;
  display: flex;
  flex-direction: column;
  gap: 10px;
  pointer-events: none;
}

.custom-message {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 500;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
  background: var(--content-bg);
  border-left: 4px solid transparent;
  pointer-events: auto;
}

.custom-message.success {
  border-left-color: #52c41a;
  color: #52c41a;
}

.custom-message.error {
  border-left-color: #ff4d4f;
  color: #ff4d4f;
}

.msg-icon {
  font-size: 18px;
}

.msg-fade-enter-active,
.msg-fade-leave-active {
  transition: all 0.3s cubic-bezier(0.2, 0, 0, 1);
}

.msg-fade-enter-from {
  opacity: 0;
  transform: translateY(-20px);
}

.msg-fade-leave-to {
  opacity: 0;
  transform: translateY(-20px);
}

/* 基础卡片 */
.chart-card,
.card-item,
.radar-card {
  background: var(--content-bg);
  border: 1px solid var(--border-color);
  border-radius: 8px;
  padding: 20px;
  transition: all 0.3s;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.02);
}

.chart-card:hover,
.card-item:hover,
.radar-card:hover {
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.05);
}

.card-title {
  font-size: 15px;
  font-weight: bold;
  color: var(--text-color);
  margin-bottom: 16px;
}

/* 1. 顶部指标 */
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

.card-tag.blue {
  color: #1677ff;
  border-color: #91caff;
  background: #e6f4ff;
}

.card-tag.green {
  color: #52c41a;
  border-color: #b7eb8f;
  background: #f6ffed;
}

.card-tag.orange {
  color: #faad14;
  border-color: #ffe58f;
  background: #fffbe6;
}

.card-tag.red {
  color: #ff4d4f;
  border-color: #ffccc7;
  background: #fff2f0;
}

.dark-theme .card-tag {
  background: transparent;
}

.card-value {
  font-size: 26px;
  font-weight: bold;
  color: var(--text-color);
  margin: 12px 0;
  font-family: monospace;
}

.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 13px;
  color: var(--text-color-secondary);
  border-top: 1px dashed var(--border-color);
  padding-top: 12px;
}

.card-icon {
  font-size: 22px;
}

.text-danger {
  color: #ff4d4f !important;
}

.text-warning {
  color: #faad14 !important;
}

.font-mono {
  font-family: monospace;
}

.font-bold {
  font-weight: bold;
}

/* 2. 中部图表 */
.middle-charts {
  display: flex;
  gap: 16px;
}

.trend-card {
  flex: 2;
}

.asset-card {
  flex: 1;
}

.chart-content {
  width: 100%;
  height: 320px;
}

/* 3. 底部雷达 */
.bottom-radar {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

.flex-between {
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid var(--border-color);
  padding-bottom: 12px;
}

.with-icon {
  display: flex;
  align-items: center;
  gap: 6px;
}

.text-btn {
  background: transparent;
  border: none;
  color: var(--sidebar-bg-active);
  cursor: pointer;
  font-size: 13px;
  transition: opacity 0.2s;
}

.text-btn:hover {
  opacity: 0.7;
  text-decoration: underline;
}

.spin-slow {
  animation: radar-spin 3s linear infinite;
}

@keyframes radar-spin {
  100% {
    transform: rotate(360deg);
  }
}

.radar-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.risk-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 0;
  border-bottom: 1px dashed var(--border-color);
}

.risk-item:last-child {
  border-bottom: none;
  padding-bottom: 0;
}

.risk-info {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.risk-name {
  font-size: 14px;
  font-weight: bold;
  color: var(--text-color);
}

.risk-desc {
  font-size: 12px;
  color: var(--text-color-secondary);
}

.risk-tags {
  display: flex;
  gap: 8px;
}

.risk-tag {
  font-size: 11px;
  padding: 2px 6px;
  background: #fff2f0;
  color: #ff4d4f;
  border: 1px solid #ffccc7;
  border-radius: 4px;
}

.dark-theme .risk-tag {
  background: rgba(255, 77, 79, 0.1);
  border-color: rgba(255, 77, 79, 0.3);
}

.status-indicator {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  font-weight: 500;
}

.status-indicator .dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
}

.status-indicator.warning {
  color: #ff4d4f;
}

.status-indicator.warning .dot {
  background: #ff4d4f;
  box-shadow: 0 0 6px #ff4d4f;
  animation: blink 1s infinite;
}

@keyframes blink {

  0%,
  100% {
    opacity: 1;
  }

  50% {
    opacity: 0.4;
  }
}

/* ================= 报告弹窗特调样式 ================= */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: rgba(0, 0, 0, 0.5);
  backdrop-filter: blur(3px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
}

.report-modal {
  width: 700px;
  background: var(--content-bg);
  border-radius: 8px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
  border: 1px solid var(--border-color);
  overflow: hidden;
}

.bg-danger-header {
  background: #fff1f0;
  border-bottom: 1px solid #ffccc7;
}

.dark-theme .bg-danger-header {
  background: rgba(255, 77, 79, 0.1);
  border-color: rgba(255, 77, 79, 0.2);
}

.bg-danger-header h3 {
  margin: 0;
  font-size: 16px;
  color: #ff4d4f;
  font-weight: bold;
  display: flex;
  align-items: center;
  gap: 6px;
}

.modal-header {
  padding: 16px 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.modal-close {
  font-size: 20px;
  color: var(--text-color-secondary);
  cursor: pointer;
}

.modal-body {
  padding: 24px;
}

.modal-footer {
  padding: 16px 24px;
  border-top: 1px solid var(--border-color);
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  background: var(--header-bg);
}

.vben-btn {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 6px 16px;
  font-size: 14px;
  border-radius: 6px;
  cursor: pointer;
  border: 1px solid transparent;
  transition: all 0.2s;
  height: 34px;
  box-sizing: border-box;
}

.vben-btn.default {
  background: transparent;
  border-color: var(--border-color);
  color: var(--text-color);
}

.vben-btn.default:hover {
  border-color: var(--sidebar-bg-active);
  color: var(--sidebar-bg-active);
}

.vben-btn.primary {
  background: var(--sidebar-bg-active);
  color: #fff;
}

.vben-btn.primary:hover {
  opacity: 0.85;
}

/* 报告内部排版 */
.report-meta {
  display: flex;
  justify-content: space-between;
  font-size: 13px;
  color: var(--text-color-secondary);
  padding-bottom: 16px;
  border-bottom: 1px dashed var(--border-color);
  margin-bottom: 16px;
}

.report-meta span {
  display: flex;
  align-items: center;
  gap: 4px;
}

.report-content {
  color: var(--text-color);
  line-height: 1.6;
  font-size: 14px;
}

.report-content h4 {
  font-size: 15px;
  color: var(--text-color);
  margin: 20px 0 10px 0;
  border-left: 3px solid var(--sidebar-bg-active);
  padding-left: 8px;
}

.report-content h4:first-child {
  margin-top: 0;
}

.risk-node-box {
  background: var(--layout-bg);
  border: 1px solid #ffccc7;
  border-radius: 6px;
  padding: 16px;
  margin: 12px 0;
}

.dark-theme .risk-node-box {
  border-color: rgba(255, 77, 79, 0.3);
}

.node-title {
  font-weight: bold;
  color: #ff4d4f;
  font-size: 15px;
  margin-bottom: 12px;
  display: flex;
  align-items: center;
  gap: 6px;
}

.risk-node-box ul {
  margin: 0;
  padding-left: 20px;
  color: var(--text-color);
}

.risk-node-box li {
  margin-bottom: 8px;
}

.modal-fade-enter-active,
.modal-fade-leave-active {
  transition: all 0.3s cubic-bezier(0.2, 0, 0, 1);
}

.modal-fade-enter-from,
.modal-fade-leave-to {
  opacity: 0;
  transform: scale(0.98);
}
</style>