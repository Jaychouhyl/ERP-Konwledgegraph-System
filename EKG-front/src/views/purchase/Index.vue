<template>
  <div class="page-container">

    <div class="message-container">
      <transition-group name="msg-fade">
        <div v-for="msg in messages" :key="msg.id" class="custom-message" :class="msg.type">
          <Icon :icon="msg.icon" class="msg-icon" />
          <span>{{ msg.text }}</span>
        </div>
      </transition-group>
    </div>

    <div class="vben-card search-wrapper">
      <div class="search-item">
        <span class="label">采购单号：</span>
        <input type="text" v-model="searchQuery.poNumber" placeholder="如 PO-2026..." class="vben-input"
          @keyup.enter="handleSearch" />
      </div>
      <div class="search-item">
        <span class="label">供应商：</span>
        <select v-model="searchQuery.supplier" class="vben-input select" @change="handleSearch">
          <option value="">全部</option>
          <option value="TSMC台积电">TSMC台积电</option>
          <option value="京东方">京东方</option>
          <option value="某劣质外包厂">某劣质外包厂</option>
        </select>
      </div>
      <div class="search-item">
        <span class="label">状态：</span>
        <select v-model="searchQuery.status" class="vben-input select" @change="handleSearch">
          <option value="">全部</option>
          <option value="DRAFT">草稿</option>
          <option value="PENDING">待审核</option>
          <option value="APPROVED">待收货</option>
          <option value="DONE">已完成</option>
        </select>
      </div>
      <div class="search-actions">
        <button class="vben-btn default" @click="resetSearch">
          <Icon icon="mdi:refresh" /> 重置
        </button>
        <button class="vben-btn primary" @click="handleSearch">
          <Icon icon="mdi:magnify" /> 查询
        </button>
      </div>
    </div>

    <div class="vben-card table-wrapper">
      <div class="table-toolbar">
        <div class="toolbar-title">采购申请与审批列表</div>
        <div class="toolbar-actions">
          <button class="vben-btn primary" @click="openDraftModal('add')">
            <Icon icon="mdi:plus-box-outline" /> 新建采购申请
          </button>
        </div>
      </div>

      <div class="table-container">
        <table class="vben-table">
          <thead>
            <tr>
              <th>单号</th>
              <th>供应商</th>
              <th>采购物料</th>
              <th>总金额 (￥)</th>
              <th>预计交期</th>
              <th>创建人</th>
              <th>状态</th>
              <th width="180">业务操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="item in paginatedList" :key="item.id">
              <td class="font-mono">{{ item.poNumber }}</td>
              <td>{{ item.supplier }}</td>
              <td>{{ item.material }} <span class="sub-text">x{{ item.qty }}</span></td>
              <td class="font-bold text-blue">{{ item.totalAmount.toLocaleString() }}</td>
              <td>{{ item.eta }}</td>
              <td>{{ item.creator }}</td>
              <td>
                <span class="status-tag" :class="item.status.toLowerCase()">
                  {{ getStatusText(item.status) }}
                </span>
              </td>
              <td>
                <div class="action-links">
                  <template v-if="item.status === 'DRAFT'">
                    <span class="link default" @click="openDraftModal('edit', item)">
                      <Icon icon="mdi:pencil-outline" /> 编辑
                    </span>
                    <span class="divider">|</span>
                    <span class="link primary" @click="submitToReview(item)">提交审核</span>
                  </template>

                  <span v-if="item.status === 'PENDING'" class="link warning" @click="openReviewModal(item)">
                    <Icon icon="mdi:shield-check-outline" /> 智能审核
                  </span>

                  <span v-if="item.status === 'APPROVED'" class="link success" @click="urgeDelivery(item)">
                    <Icon icon="mdi:bell-ring-outline" /> 催办交货
                  </span>

                  <span v-if="item.status === 'DONE'" class="link default" @click="viewDetails(item)">查看PO单</span>
                </div>
              </td>
            </tr>
            <tr v-if="paginatedList.length === 0">
              <td colspan="8" class="empty-text">未能搜索到采购单数据</td>
            </tr>
          </tbody>
        </table>
      </div>

      <div class="pagination-wrapper" v-if="totalItems > 0">
        <span class="page-info">共 {{ totalItems }} 条数据</span>
        <div class="page-controls">
          <button class="page-btn" :disabled="currentPage === 1" @click="changePage(currentPage - 1)">上一页</button>
          <div class="page-numbers">
            <span class="page-num active">{{ currentPage }}</span>
            <span class="page-num-divider">/</span>
            <span class="page-num">{{ totalPages }}</span>
          </div>
          <button class="page-btn" :disabled="currentPage === totalPages"
            @click="changePage(currentPage + 1)">下一页</button>
        </div>
      </div>
    </div>

    <transition name="modal-fade">
      <div class="modal-overlay" v-if="showDraftModal" @click.self="closeDraftModal">
        <div class="modal-content">
          <div class="modal-header">
            <h3>{{ draftModalMode === 'add' ? '手工创建采购草稿' : '编辑采购草稿' }}</h3>
            <Icon icon="mdi:close" class="modal-close" @click="closeDraftModal" />
          </div>
          <div class="modal-body">
            <div class="form-row">
              <div class="form-item half">
                <label>选择供应商 <span class="req">*</span></label>
                <input type="text" v-model="draftForm.supplier" list="supplier-list" class="vben-input form-control"
                  placeholder="下拉选择或手动输入" />
                <datalist id="supplier-list">
                  <option value="TSMC台积电"></option>
                  <option value="京东方"></option>
                  <option value="某劣质外包厂"></option>
                  <option value="宁德时代"></option>
                  <option value="立讯精密"></option>
                </datalist>
              </div>
              <div class="form-item half">
                <label>采购物料名称 <span class="req">*</span></label>
                <input type="text" v-model="draftForm.material" list="smartx-materials" class="vben-input form-control"
                  placeholder="下拉选择或手动输入" />
                <datalist id="smartx-materials">
                  <option value="骁龙 8 Gen 3 处理器"></option>
                  <option value="OLED 2K 曲面屏"></option>
                  <option value="高密度硅负极电池 5000mAh"></option>
                  <option value="索尼 IMX989 主摄模组"></option>
                  <option value="航空级铝合金中框"></option>
                  <option value="环保包装盒组件"></option>
                </datalist>
              </div>
            </div>

            <div class="form-row mt-4">
              <div class="form-item half">
                <label>采购数量 <span class="req">*</span></label>
                <input type="number" v-model="draftForm.qty" class="vben-input form-control num-input" min="1" />
              </div>
              <div class="form-item half">
                <label>预计单价 (￥) <span class="req">*</span></label>
                <input type="number" v-model="draftForm.unitPrice" class="vben-input form-control num-input" min="0" />
              </div>
            </div>

            <div class="form-row mt-4 align-items-end">
              <div class="form-item half">
                <label>预计交货日期 <span class="req">*</span></label>
                <input type="date" v-model="draftForm.eta" class="vben-input form-control" />
              </div>
              <div class="form-item half highlight-bg">
                <label>订单总额</label>
                <div class="total-price-display">￥ {{ draftTotalAmount.toLocaleString() }}</div>
              </div>
            </div>
          </div>
          <div class="modal-footer">
            <button class="vben-btn default" @click="closeDraftModal">取消</button>
            <button class="vben-btn primary" @click="submitDraft">保存至草稿箱</button>
          </div>
        </div>
      </div>
    </transition>

    <transition name="modal-fade">
      <div class="modal-overlay" v-if="showReviewModal" @click.self="closeReviewModal">
        <div class="modal-content review-modal">
          <div class="modal-header">
            <h3>单据审批与 AI 风控检测：{{ currentOrder?.poNumber }}</h3>
            <Icon icon="mdi:close" class="modal-close" @click="closeReviewModal" />
          </div>

          <div class="modal-body flex-layout">
            <div class="order-detail-panel">
              <div class="section-title">订单明细</div>
              <div class="detail-grid">
                <div class="d-item"><span class="d-label">供应商：</span>{{ currentOrder?.supplier }}</div>
                <div class="d-item"><span class="d-label">采购物料：</span>{{ currentOrder?.material }}</div>
                <div class="d-item"><span class="d-label">采购数量：</span>{{ currentOrder?.qty }} 件</div>
                <div class="d-item"><span class="d-label">订单总额：</span><span class="text-blue font-bold">￥{{
                  currentOrder?.totalAmount.toLocaleString() }}</span></div>
                <div class="d-item"><span class="d-label">申请人：</span>{{ currentOrder?.creator }}</div>
                <div class="d-item"><span class="d-label">交货日期：</span>{{ currentOrder?.eta }}</div>
              </div>

              <div class="section-title mt-4">业务流转状态</div>
              <div class="steps-container">
                <div class="step active">1. 业务建单</div>
                <div class="step-line active"></div>
                <div class="step processing">2. 财务审批</div>
                <div class="step-line"></div>
                <div class="step disabled">3. 等待收货</div>
              </div>
            </div>

            <div class="ai-risk-panel">
              <div class="ai-header">
                <Icon icon="mdi:robot-excited-outline" class="ai-icon" />
                <span>Neo4j 图谱引擎决策建议</span>
              </div>

              <div v-if="isAiThinking" class="ai-skeleton">
                <div class="pulse-bar w-3/4"></div>
                <div class="pulse-bar w-full"></div>
                <div class="pulse-bar w-5/6"></div>
                <div class="loading-text">正在检索知识图谱与历史记录...</div>
              </div>

              <div v-else class="ai-result" :class="{ 'risk-high': hasRisk }">
                <div class="result-title">
                  <Icon :icon="hasRisk ? 'mdi:alert' : 'mdi:check-decagram'" />
                  {{ hasRisk ? '发现高危风险！' : '图谱分析通过' }}
                </div>
                <p class="result-desc" v-html="aiAnalysisText"></p>
                <div v-if="!hasRisk" class="finance-check">
                  <Icon icon="mdi:bank-check" /> 系统校验：当前账面资金充裕，可安全覆盖本次采购。
                </div>
              </div>
            </div>
          </div>

          <div class="modal-footer">
            <button class="vben-btn danger" @click="handleReject">驳回申请</button>
            <button class="vben-btn success" @click="handleApprove" :disabled="isAiThinking">
              通过并冻结资金
            </button>
          </div>
        </div>
      </div>
    </transition>
  </div>
</template>

<script setup>
import { ref, computed, reactive } from 'vue'
import { Icon } from '@iconify/vue'

// 全局 Message 提示系统
const messages = ref([])
let msgId = 0
const showMessage = (text, type = 'success') => {
  const id = msgId++
  let icon = 'mdi:information'
  if (type === 'error') icon = 'mdi:close-circle'
  if (type === 'success') icon = 'mdi:check-circle'
  if (type === 'warning') icon = 'mdi:alert-circle'

  messages.value.push({ id, text, type, icon })
  setTimeout(() => { messages.value = messages.value.filter(m => m.id !== id) }, 3000)
}

// 数据模拟与前端过滤
const searchQuery = ref({ poNumber: '', supplier: '', status: '' })

const dataList = ref([
  { id: 1, poNumber: 'PO-20260303-001', supplier: 'TSMC台积电', material: '骁龙 8 Gen 3 处理器', qty: 5000, totalAmount: 3500000, eta: '2026-03-15', creator: '林采购', status: 'PENDING' },
  { id: 2, poNumber: 'PO-20260303-002', supplier: '京东方', material: 'OLED 2K 曲面屏', qty: 3500, totalAmount: 1400000, eta: '2026-03-10', creator: '王专员', status: 'PENDING' },
  { id: 3, poNumber: 'PO-20260303-005', supplier: '某劣质外包厂', material: '环保包装盒组件', qty: 10000, totalAmount: 25000, eta: '2026-03-05', creator: '李助理', status: 'PENDING' },
  { id: 4, poNumber: 'PO-20260228-099', supplier: '宁德时代', material: '高密度硅负极电池', qty: 2000, totalAmount: 180000, eta: '2026-03-08', creator: '林采购', status: 'APPROVED' },
  { id: 5, poNumber: 'PO-20260225-088', supplier: '立讯精密', material: '索尼 IMX989 主摄', qty: 5000, totalAmount: 1500000, eta: '2026-03-01', creator: '赵经理', status: 'DONE' },
  { id: 6, poNumber: 'PO-20260303-101', supplier: '立讯精密', material: '航空级铝合金中框', qty: 1000, totalAmount: 85000, eta: '2026-03-20', creator: '王专员', status: 'DRAFT' },
])

const filteredList = computed(() => {
  return dataList.value.filter(item => {
    const matchPo = !searchQuery.value.poNumber || item.poNumber.includes(searchQuery.value.poNumber)
    const matchSup = !searchQuery.value.supplier || item.supplier.includes(searchQuery.value.supplier)
    const matchStatus = !searchQuery.value.status || item.status === searchQuery.value.status
    return matchPo && matchSup && matchStatus
  })
})

const handleSearch = () => { currentPage.value = 1 }
const resetSearch = () => { searchQuery.value = { poNumber: '', supplier: '', status: '' }; currentPage.value = 1 }

const getStatusText = (status) => {
  const map = { DRAFT: '草稿', PENDING: '待审核', APPROVED: '待收货', DONE: '已入库' }
  return map[status] || status
}

const currentPage = ref(1)
const pageSize = ref(10)
const totalItems = computed(() => filteredList.value.length)
const totalPages = computed(() => Math.max(1, Math.ceil(totalItems.value / pageSize.value)))
const paginatedList = computed(() => filteredList.value.slice((currentPage.value - 1) * pageSize.value, currentPage.value * pageSize.value))
const changePage = (page) => { if (page >= 1 && page <= totalPages.value) currentPage.value = page }


// 手工建单与草稿管理
const showDraftModal = ref(false)
const draftModalMode = ref('add')
const draftForm = reactive({ id: null, poNumber: '', supplier: '', material: '', qty: 100, unitPrice: 0, eta: '' })

const draftTotalAmount = computed(() => (draftForm.qty * draftForm.unitPrice) || 0)

const openDraftModal = (mode, row = null) => {
  draftModalMode.value = mode
  if (mode === 'edit' && row) {
    draftForm.id = row.id
    draftForm.poNumber = row.poNumber
    draftForm.supplier = row.supplier
    draftForm.material = row.material
    draftForm.qty = row.qty
    draftForm.unitPrice = row.totalAmount / row.qty
    draftForm.eta = row.eta
  } else {
    draftForm.id = null
    draftForm.poNumber = `PO-20260303-${Math.floor(Math.random() * 1000).toString().padStart(3, '0')}`
    draftForm.supplier = ''
    draftForm.material = ''
    draftForm.qty = 100
    draftForm.unitPrice = 0
    draftForm.eta = new Date().toISOString().split('T')[0]
  }
  showDraftModal.value = true
}

const closeDraftModal = () => { showDraftModal.value = false }

const submitDraft = () => {
  if (!draftForm.supplier || !draftForm.material || draftForm.qty <= 0) {
    showMessage('提交失败：请填写完整的供应商、物料名称及数量！', 'error')
    return
  }
  if (draftModalMode.value === 'add') {
    dataList.value.unshift({
      id: Date.now(), poNumber: draftForm.poNumber, supplier: draftForm.supplier, material: draftForm.material,
      qty: Number(draftForm.qty), totalAmount: draftTotalAmount.value, eta: draftForm.eta, creator: '当前员工', status: 'DRAFT'
    })
    showMessage('建单成功！已保存至草稿箱。', 'success')
  } else {
    const index = dataList.value.findIndex(item => item.id === draftForm.id)
    if (index > -1) {
      Object.assign(dataList.value[index], { supplier: draftForm.supplier, material: draftForm.material, qty: Number(draftForm.qty), totalAmount: draftTotalAmount.value, eta: draftForm.eta })
      showMessage('草稿编辑已保存。', 'success')
    }
  }
  closeDraftModal()
}

// 采购流操作
const submitToReview = (item) => {
  item.status = 'PENDING'
  showMessage(`单据 ${item.poNumber} 已提交至财务与AI风控审核。`, 'success')
}

const urgeDelivery = (item) => {
  showMessage(`系统已自动向【${item.supplier}】发送交货催促提醒！`, 'success')
}

const viewDetails = (item) => { showMessage(`正在加载订单 ${item.poNumber} 的采购档案...`, 'info') }


// AI 智能审核风控面板
const showReviewModal = ref(false)
const currentOrder = ref(null)
const isAiThinking = ref(true)
const hasRisk = ref(false)
const aiAnalysisText = ref('')

const openReviewModal = (item) => {
  currentOrder.value = item
  showReviewModal.value = true
  isAiThinking.value = true
  hasRisk.value = false
  aiAnalysisText.value = ''

  setTimeout(() => {
    isAiThinking.value = false
    if (item.supplier.includes('劣质')) {
      hasRisk.value = true
      aiAnalysisText.value = `
        <span style="color:#ff4d4f;font-weight:bold;">警告：</span>Neo4j 知识图谱检测到供应商节点（${item.supplier}）存在严重问题：<br/><br/>
        1. 历史交货延迟率高达 <span style="color:#ff4d4f;font-weight:bold;font-size:16px;">28%</span>。<br/>
        2. 关联法人存在 <span style="color:#ff4d4f;font-weight:bold;">2起</span> 违约诉讼。<br/><br/>
        <b>AI决策建议：</b>强烈建议<span style="color:#ff4d4f;font-weight:bold;text-decoration:underline;">驳回</span>此单，系统已匹配备选优质供应商【绿点科技】。
      `
    } else {
      hasRisk.value = false
      aiAnalysisText.value = `
        供应商节点（${item.supplier}）系统信用分级为 <b style="color:#52c41a">A级</b>。<br/><br/>
        基于图谱链路追溯，其历史履约率为 <b>99.2%</b>，未发现财务穿透风险。<br/><br/>
        <b>AI决策建议：</b>订单健康度良好，建议通过，预测该批次物料可按时抵达。
      `
    }
  }, 1200)
}

const closeReviewModal = () => { showReviewModal.value = false }

const handleReject = () => {
  currentOrder.value.status = 'DRAFT'
  showMessage(`已执行风控拦截，单据打回草稿箱。`, 'warning')
  closeReviewModal()
}

const handleApprove = () => {
  if (hasRisk.value && !confirm('🚨 大模型提示存在高危供应链风险，您确定要强行通过吗？')) return
  currentOrder.value.status = 'APPROVED'
  showMessage(`审批通过！资金已冻结，订单流转至仓库待收货。`, 'success')
  closeReviewModal()
}
</script>

<style scoped>
.page-container {
  display: flex;
  flex-direction: column;
  gap: 16px;
  position: relative;
}

.vben-card {
  background: var(--content-bg);
  border: 1px solid var(--border-color);
  border-radius: 6px;
  padding: 16px;
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

.custom-message.warning {
  border-left-color: #faad14;
  color: #faad14;
}

.custom-message.info {
  border-left-color: #1677ff;
  color: #1677ff;
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

/* 搜索区 */
.search-wrapper {
  display: flex;
  flex-wrap: nowrap;
  gap: 16px;
  align-items: center;
  overflow-x: auto;
  padding-bottom: 8px;
  margin-bottom: -8px;
}

.search-wrapper::-webkit-scrollbar {
  height: 4px;
}

.search-item {
  display: flex;
  align-items: center;
  flex-shrink: 0;
}

.search-item .label {
  font-size: 14px;
  color: var(--text-color);
  margin-right: 8px;
}

.vben-input {
  border: 1px solid var(--border-color);
  border-radius: 6px;
  background: var(--layout-bg);
  color: var(--text-color);
  outline: none;
  transition: all 0.3s;
}

.vben-input:focus {
  border-color: var(--sidebar-bg-active);
  box-shadow: 0 0 0 2px rgba(22, 119, 255, 0.1);
}

.search-item .vben-input {
  width: 140px;
  height: 34px;
  padding: 0 12px;
  font-size: 14px;
  box-sizing: border-box;
}

.search-actions {
  margin-left: auto;
  display: flex;
  gap: 10px;
  flex-shrink: 0;
}

/* 按钮通用 */
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

.vben-btn.danger {
  background: #ff4d4f;
  color: #fff;
}

.vben-btn.danger:hover {
  opacity: 0.85;
}

.vben-btn.success {
  background: #52c41a;
  color: #fff;
}

.vben-btn.success:hover {
  opacity: 0.85;
}

.vben-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

/* 表格区 */
.table-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.toolbar-title {
  font-size: 16px;
  font-weight: bold;
  color: var(--text-color);
}

.table-container {
  overflow-x: auto;
}

.vben-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 14px;
}

.vben-table th,
.vben-table td {
  padding: 12px 16px;
  border-bottom: 1px solid var(--border-color);
  text-align: left;
  color: var(--text-color);
}

.vben-table th {
  background-color: var(--layout-bg);
  color: var(--text-color-secondary);
  font-weight: 500;
}

.vben-table tbody tr:hover {
  background-color: var(--hover-bg);
}

.empty-text {
  text-align: center;
  padding: 40px !important;
  color: var(--text-color-secondary);
}

.font-mono {
  font-family: monospace;
  letter-spacing: 0.5px;
}

.font-bold {
  font-weight: bold;
}

.text-blue {
  color: var(--sidebar-bg-active);
}

.sub-text {
  color: var(--text-color-secondary);
  font-size: 12px;
  margin-left: 4px;
}

.status-tag {
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
}

.status-tag.draft {
  background: #f5f5f5;
  color: #595959;
  border: 1px solid #d9d9d9;
}

.status-tag.pending {
  background: #fff7e6;
  color: #fa8c16;
  border: 1px solid #ffd591;
}

.status-tag.approved {
  background: #e6f4ff;
  color: #1677ff;
  border: 1px solid #91caff;
}

.status-tag.done {
  background: #f6ffed;
  color: #52c41a;
  border: 1px solid #b7eb8f;
}

.dark-theme .status-tag {
  background: transparent;
}

.action-links {
  display: flex;
  align-items: center;
  gap: 12px;
}

.link {
  cursor: pointer;
  font-size: 13px;
  font-weight: 500;
  transition: opacity 0.2s;
  display: flex;
  align-items: center;
  gap: 2px;
}

.link:hover {
  opacity: 0.7;
}

.link.primary {
  color: var(--sidebar-bg-active);
}

.link.warning {
  color: #faad14;
}

.link.success {
  color: #52c41a;
}

.link.default {
  color: var(--text-color-secondary);
}

.divider {
  color: var(--border-color);
  margin: 0 -4px;
}

.pagination-wrapper {
  margin-top: 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: var(--text-color-secondary);
  font-size: 13px;
  border-top: 1px solid var(--border-color);
  padding-top: 16px;
}

.page-controls {
  display: flex;
  align-items: center;
  gap: 12px;
}

.page-btn {
  padding: 4px 12px;
  border: 1px solid var(--border-color);
  background: var(--content-bg);
  color: var(--text-color);
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.2s;
  font-size: 13px;
}

.page-btn:hover:not(:disabled) {
  border-color: var(--sidebar-bg-active);
  color: var(--sidebar-bg-active);
}

.page-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
  background: var(--layout-bg);
}

.page-numbers {
  display: flex;
  align-items: center;
  gap: 6px;
}

.page-num.active {
  color: var(--sidebar-bg-active);
  font-weight: bold;
}

/* 弹窗通用样式 */
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
  z-index: 999;
}

.modal-content {
  width: 500px;
  background: var(--content-bg);
  border-radius: 8px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
  border: 1px solid var(--border-color);
  overflow: hidden;
}

.review-modal {
  width: 850px;
}

.modal-header {
  padding: 16px 24px;
  display: flex;
  justify-content: space-between;
  border-bottom: 1px solid var(--border-color);
  background: var(--header-bg);
}

.modal-header h3 {
  margin: 0;
  font-size: 16px;
  color: var(--text-color);
  font-weight: bold;
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

/* 建单表单高度对齐定制 */
.form-row {
  display: flex;
  gap: 20px;
}

.form-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-item.half {
  flex: 1;
}

.form-item label {
  font-size: 13px;
  color: var(--text-color);
  font-weight: 500;
}

.form-item .req {
  color: #ff4d4f;
  margin-left: 2px;
}

.align-items-end {
  align-items: flex-end;
}

.mt-4 {
  margin-top: 16px;
}

/* 强制下拉框和输入框的视觉尺寸 100% 一致 */
.form-control {
  width: 100%;
  height: 36px;
  padding: 0 12px;
  font-size: 14px;
  box-sizing: border-box;
}

.num-input {
  font-family: monospace;
  font-size: 15px;
}

.highlight-bg {
  background: var(--layout-bg);
  padding: 8px 12px;
  border-radius: 6px;
  border: 1px dashed var(--sidebar-bg-active);
}

.total-price-display {
  font-size: 18px;
  font-weight: bold;
  color: var(--sidebar-bg-active);
  font-family: monospace;
  line-height: 1;
  margin-top: 4px;
}

/* 左右分栏布局 (用于 AI 审核) */
.flex-layout {
  display: flex;
  height: 320px;
  padding: 0;
}

.order-detail-panel {
  flex: 1;
  padding: 24px;
  border-right: 1px solid var(--border-color);
}

.ai-risk-panel {
  width: 360px;
  padding: 24px;
  background: rgba(22, 119, 255, 0.03);
  display: flex;
  flex-direction: column;
}

.dark-theme .ai-risk-panel {
  background: rgba(22, 119, 255, 0.08);
}

.section-title {
  font-size: 14px;
  font-weight: bold;
  color: var(--text-color);
  margin-bottom: 16px;
  border-left: 3px solid var(--sidebar-bg-active);
  padding-left: 8px;
}

.detail-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px 8px;
  font-size: 14px;
  color: var(--text-color);
}

.d-label {
  color: var(--text-color-secondary);
}

/* 简易横向步骤条 */
.steps-container {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 16px;
  font-size: 13px;
  color: var(--text-color-secondary);
}

.step {
  padding: 6px 12px;
  border-radius: 20px;
  background: var(--layout-bg);
  border: 1px solid var(--border-color);
}

.step.active {
  background: #e6f4ff;
  color: #1677ff;
  border-color: #1677ff;
  font-weight: bold;
}

.step.processing {
  background: #fffbe6;
  color: #faad14;
  border-color: #faad14;
  font-weight: bold;
  box-shadow: 0 0 8px rgba(250, 173, 20, 0.3);
}

.step.disabled {
  opacity: 0.5;
}

.dark-theme .step.active {
  background: rgba(22, 119, 255, 0.2);
}

.dark-theme .step.processing {
  background: rgba(250, 173, 20, 0.2);
}

.step-line {
  flex: 1;
  height: 1px;
  background: var(--border-color);
  margin: 0 10px;
}

.step-line.active {
  background: #1677ff;
}

/* AI 面板内容 */
.ai-header {
  display: flex;
  align-items: center;
  gap: 6px;
  font-weight: bold;
  color: var(--sidebar-bg-active);
  font-size: 15px;
  margin-bottom: 20px;
}

.ai-icon {
  font-size: 20px;
  animation: float 2s ease-in-out infinite;
}

@keyframes float {
  0% {
    transform: translateY(0px);
  }

  50% {
    transform: translateY(-3px);
  }

  100% {
    transform: translateY(0px);
  }
}

/* AI 骨架屏 */
.ai-skeleton {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 16px;
  justify-content: center;
}

.pulse-bar {
  height: 12px;
  background-color: var(--border-color);
  border-radius: 6px;
  animation: pulse-opacity 1.2s infinite ease-in-out;
}

.w-3\/4 {
  width: 75%;
}

.w-full {
  width: 100%;
}

.w-5\/6 {
  width: 83%;
}

@keyframes pulse-opacity {

  0%,
  100% {
    opacity: 1;
  }

  50% {
    opacity: 0.3;
  }
}

.loading-text {
  margin-top: 16px;
  font-size: 13px;
  color: var(--sidebar-bg-active);
  text-align: center;
  font-weight: bold;
  letter-spacing: 1px;
  animation: pulse-opacity 1.2s infinite ease-in-out;
}

/* AI 结果 */
.ai-result {
  flex: 1;
  background: var(--content-bg);
  border: 1px solid #b7eb8f;
  border-radius: 8px;
  padding: 16px;
  font-size: 13px;
  line-height: 1.6;
  color: var(--text-color);
  box-shadow: 0 4px 12px rgba(82, 196, 26, 0.1);
  overflow-y: auto;
}

.ai-result.risk-high {
  border-color: #ffccc7;
  box-shadow: 0 4px 12px rgba(255, 77, 79, 0.15);
  background: #fff1f0;
}

.dark-theme .ai-result.risk-high {
  background: rgba(255, 77, 79, 0.05);
  border-color: rgba(255, 77, 79, 0.3);
}

.result-title {
  font-size: 15px;
  font-weight: bold;
  margin-bottom: 12px;
  display: flex;
  align-items: center;
  gap: 6px;
  color: #52c41a;
}

.risk-high .result-title {
  color: #ff4d4f;
}

.finance-check {
  margin-top: 16px;
  padding-top: 12px;
  border-top: 1px dashed var(--border-color);
  color: #1677ff;
  display: flex;
  align-items: center;
  gap: 4px;
  font-weight: 500;
}

.modal-fade-enter-active,
.modal-fade-leave-active {
  transition: all 0.3s;
}

.modal-fade-enter-from,
.modal-fade-leave-to {
  opacity: 0;
  transform: scale(0.98);
}
</style>