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
        <span class="label">销售单号：</span>
        <input type="text" v-model="searchQuery.soNumber" placeholder="如 SO-2026..." class="vben-input"
          @keyup.enter="handleSearch" />
      </div>
      <div class="search-item">
        <span class="label">客户/买家：</span>
        <select v-model="searchQuery.customer" class="vben-input select" @change="handleSearch">
          <option value="">全部客户</option>
          <option value="某某科技（深圳）">某某科技（深圳）</option>
          <option value="华南大区总代">华南大区总代</option>
          <option value="海外渠道经销商">海外渠道经销商</option>
        </select>
      </div>
      <div class="search-item">
        <span class="label">订单状态：</span>
        <select v-model="searchQuery.status" class="vben-input select" @change="handleSearch">
          <option value="">全部</option>
          <option value="DRAFT">草稿</option>
          <option value="PENDING">待审批</option>
          <option value="TO_SHIP">待发货</option>
          <option value="SHIPPED">已发货/完成</option>
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
        <div class="toolbar-title">销售订单 (SO) 管理</div>
        <div class="toolbar-actions">
          <button class="vben-btn primary" @click="openDraftModal('add')">
            <Icon icon="mdi:cart-plus" /> 新建销售单
          </button>
        </div>
      </div>

      <div class="table-container">
        <table class="vben-table">
          <thead>
            <tr>
              <th>销售单号</th>
              <th>客户名称</th>
              <th>销售产品 (成品)</th>
              <th>订单总额 (￥)</th>
              <th>要求发货日</th>
              <th>销售员</th>
              <th>状态</th>
              <th width="190">业务操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="item in paginatedList" :key="item.id">
              <td class="font-mono">{{ item.soNumber }}</td>
              <td class="font-bold">{{ item.customer }}</td>
              <td>{{ item.product }} <span class="sub-text">x{{ item.qty }}</span></td>
              <td class="font-bold text-success">{{ item.totalAmount.toLocaleString() }}</td>
              <td>{{ item.deliveryDate }}</td>
              <td>
                <div class="user-cell">
                  <div class="text-avatar-small" :style="{ backgroundColor: item.avatarColor }">
                    {{ item.salesperson.charAt(0) }}
                  </div>
                  <span>{{ item.salesperson }}</span>
                </div>
              </td>
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
                    <span class="link primary" @click="submitToReview(item)">提交审批</span>
                  </template>

                  <span v-if="item.status === 'PENDING'" class="link warning" @click="handleApprove(item)">
                    <Icon icon="mdi:shield-check-outline" /> 销售审批
                  </span>

                  <span v-if="item.status === 'TO_SHIP'" class="link ship-color" @click="confirmShipment(item)">
                    <Icon icon="mdi:truck-fast-outline" /> 确认发货出库
                  </span>

                  <span v-if="item.status === 'SHIPPED'" class="link default" @click="viewDetails(item)">
                    <Icon icon="mdi:text-box-search-outline" /> 订单履历
                  </span>
                </div>
              </td>
            </tr>
            <tr v-if="paginatedList.length === 0">
              <td colspan="8" class="empty-text">未能搜索到销售单数据</td>
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
            <h3>{{ draftModalMode === 'add' ? '创建销售草稿单' : '编辑销售草稿' }}</h3>
            <Icon icon="mdi:close" class="modal-close" @click="closeDraftModal" />
          </div>
          <div class="modal-body">
            <div class="form-row">
              <div class="form-item half">
                <label>选择客户/买家 <span class="req">*</span></label>
                <input type="text" v-model="draftForm.customer" list="customer-list" class="vben-input form-control"
                  placeholder="下拉选择客户" />
                <datalist id="customer-list">
                  <option value="某某科技（深圳）"></option>
                  <option value="华南大区总代"></option>
                  <option value="海外渠道经销商"></option>
                  <option value="散客直销"></option>
                </datalist>
              </div>
              <div class="form-item half">
                <label>销售产品 (库存成品) <span class="req">*</span></label>
                <input type="text" v-model="draftForm.product" list="product-list" class="vben-input form-control"
                  placeholder="选择出库成品" />
                <datalist id="product-list">
                  <option value="SmartX 2026 Pro 手机 (256G)"></option>
                  <option value="SmartX 2026 Ultra 手机 (512G)"></option>
                  <option value="SmartX 无线降噪耳机"></option>
                  <option value="官方快充套装 120W"></option>
                </datalist>
              </div>
            </div>

            <div class="form-row mt-4">
              <div class="form-item half">
                <label>销售数量 <span class="req">*</span></label>
                <input type="number" v-model="draftForm.qty" class="vben-input form-control num-input" min="1" />
              </div>
              <div class="form-item half">
                <label>成交单价 (￥) <span class="req">*</span></label>
                <input type="number" v-model="draftForm.unitPrice" class="vben-input form-control num-input" min="0" />
              </div>
            </div>

            <div class="form-row mt-4 align-items-end">
              <div class="form-item half">
                <label>要求发货日期 <span class="req">*</span></label>
                <input type="date" v-model="draftForm.deliveryDate" class="vben-input form-control" />
              </div>
              <div class="form-item half highlight-bg-sales">
                <label>预期营收总额</label>
                <div class="total-price-display">￥ {{ draftTotalAmount.toLocaleString() }}</div>
              </div>
            </div>
          </div>
          <div class="modal-footer">
            <button class="vben-btn default" @click="closeDraftModal">取消</button>
            <button class="vben-btn primary" @click="submitDraft">保存至草稿</button>
          </div>
        </div>
      </div>
    </transition>
  </div>
</template>

<script setup>
import { ref, computed, reactive } from 'vue'
import { Icon } from '@iconify/vue'

// ================= 全局 Message =================
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

// ================= 数据模拟 =================
const searchQuery = ref({ soNumber: '', customer: '', status: '' })

const dataList = ref([
  { id: 1, soNumber: 'SO-20260305-001', customer: '华南大区总代', product: 'SmartX 2026 Pro 手机 (256G)', qty: 2000, totalAmount: 11998000, deliveryDate: '2026-03-10', salesperson: '李销售', status: 'PENDING', avatarColor: '#1677ff' },
  { id: 2, soNumber: 'SO-20260304-015', customer: '海外渠道经销商', product: 'SmartX 2026 Ultra 手机 (512G)', qty: 500, totalAmount: 3999500, deliveryDate: '2026-03-15', salesperson: '王经理', status: 'TO_SHIP', avatarColor: '#52c41a' },
  { id: 3, soNumber: 'SO-20260302-088', customer: '某某科技（深圳）', product: '官方快充套装 120W', qty: 1000, totalAmount: 199000, deliveryDate: '2026-03-05', salesperson: '赵销售', status: 'SHIPPED', avatarColor: '#faad14' },
  { id: 4, soNumber: 'SO-20260306-002', customer: '散客直销', product: 'SmartX 无线降噪耳机', qty: 50, totalAmount: 24950, deliveryDate: '2026-03-08', salesperson: '李销售', status: 'DRAFT', avatarColor: '#1677ff' },
])

const filteredList = computed(() => {
  return dataList.value.filter(item => {
    const matchSo = !searchQuery.value.soNumber || item.soNumber.includes(searchQuery.value.soNumber)
    const matchCus = !searchQuery.value.customer || item.customer.includes(searchQuery.value.customer)
    const matchStatus = !searchQuery.value.status || item.status === searchQuery.value.status
    return matchSo && matchCus && matchStatus
  })
})

const handleSearch = () => { currentPage.value = 1 }
const resetSearch = () => { searchQuery.value = { soNumber: '', customer: '', status: '' }; currentPage.value = 1 }

const getStatusText = (status) => {
  const map = { DRAFT: '草稿', PENDING: '待审批', TO_SHIP: '待发货', SHIPPED: '已发货' }
  return map[status] || status
}

// 分页逻辑
const currentPage = ref(1)
const pageSize = ref(10)
const totalItems = computed(() => filteredList.value.length)
const totalPages = computed(() => Math.max(1, Math.ceil(totalItems.value / pageSize.value)))
const paginatedList = computed(() => filteredList.value.slice((currentPage.value - 1) * pageSize.value, currentPage.value * pageSize.value))
const changePage = (page) => { if (page >= 1 && page <= totalPages.value) currentPage.value = page }


// ================= 手工建单与草稿管理 =================
const showDraftModal = ref(false)
const draftModalMode = ref('add')
const draftForm = reactive({ id: null, soNumber: '', customer: '', product: '', qty: 10, unitPrice: 5999, deliveryDate: '' })

const draftTotalAmount = computed(() => (draftForm.qty * draftForm.unitPrice) || 0)

const openDraftModal = (mode, row = null) => {
  draftModalMode.value = mode
  if (mode === 'edit' && row) {
    draftForm.id = row.id
    draftForm.soNumber = row.soNumber
    draftForm.customer = row.customer
    draftForm.product = row.product
    draftForm.qty = row.qty
    draftForm.unitPrice = row.totalAmount / row.qty
    draftForm.deliveryDate = row.deliveryDate
  } else {
    draftForm.id = null
    draftForm.soNumber = `SO-20260306-${Math.floor(Math.random() * 1000).toString().padStart(3, '0')}`
    draftForm.customer = ''
    draftForm.product = ''
    draftForm.qty = 10
    draftForm.unitPrice = 5999
    draftForm.deliveryDate = new Date().toISOString().split('T')[0]
  }
  showDraftModal.value = true
}

const closeDraftModal = () => { showDraftModal.value = false }

const submitDraft = () => {
  if (!draftForm.customer || !draftForm.product || draftForm.qty <= 0) {
    showMessage('提交失败：请填写完整的客户、产品名称及数量！', 'error')
    return
  }
  if (draftModalMode.value === 'add') {
    dataList.value.unshift({
      id: Date.now(), soNumber: draftForm.soNumber, customer: draftForm.customer, product: draftForm.product,
      qty: Number(draftForm.qty), totalAmount: draftTotalAmount.value, deliveryDate: draftForm.deliveryDate,
      salesperson: 'Admin', status: 'DRAFT', avatarColor: '#eb2f96'
    })
    showMessage('销售单创建成功！已保存至草稿箱。', 'success')
  } else {
    const index = dataList.value.findIndex(item => item.id === draftForm.id)
    if (index > -1) {
      Object.assign(dataList.value[index], { customer: draftForm.customer, product: draftForm.product, qty: Number(draftForm.qty), totalAmount: draftTotalAmount.value, deliveryDate: draftForm.deliveryDate })
      showMessage('草稿修改已保存。', 'success')
    }
  }
  closeDraftModal()
}

// ================= 业务流转操作 =================
const submitToReview = (item) => {
  item.status = 'PENDING'
  showMessage(`单据 ${item.soNumber} 已提交至销售主管审批。`, 'success')
}

const handleApprove = (item) => {
  if (confirm(`【主管审批】确认客户【${item.customer}】的订单合规吗？\n确认后将流转至仓库安排发货！`)) {
    item.status = 'TO_SHIP'
    showMessage(`审批通过！已通知仓库准备发货。`, 'success')
  }
}

// 极其重要的一步：真正触发扣减库存的业务流
const confirmShipment = (item) => {
  if (confirm(`【仓库作业】是否确认已将 ${item.qty}件 [${item.product}] 装车发往 [${item.customer}]？\n警告：点击确认将立即扣减成品库存！`)) {
    item.status = 'SHIPPED'
    showMessage(`📦 发货成功！已扣减库存，物流跟踪单号已生成。营收增加：￥${item.totalAmount.toLocaleString()}`, 'success')
  }
}

const viewDetails = (item) => { showMessage(`正在加载订单 ${item.soNumber} 的履历档案...`, 'info') }

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
  white-space: nowrap;
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

.text-success {
  color: #52c41a;
}

/* 营收变绿 */
.sub-text {
  color: var(--text-color-secondary);
  font-size: 12px;
  margin-left: 4px;
}

/* 销售员头像列 */
.user-cell {
  display: flex;
  align-items: center;
  gap: 8px;
  color: var(--text-color);
}

.text-avatar-small {
  width: 26px;
  height: 26px;
  border-radius: 50%;
  color: #fff;
  font-weight: bold;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  font-size: 12px;
}

/* 状态 Tag (销售特调色) */
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

.status-tag.to_ship {
  background: #e6f4ff;
  color: #1677ff;
  border: 1px solid #91caff;
}

.status-tag.shipped {
  background: #f6ffed;
  color: #52c41a;
  border: 1px solid #b7eb8f;
}

.dark-theme .status-tag {
  background: transparent;
}

/* 操作按钮 */
.action-links {
  display: flex;
  align-items: center;
  gap: 10px;
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

.link.ship-color {
  color: #722ed1;
}

/* 独立的发货颜色 */
.link.default {
  color: var(--text-color-secondary);
}

.divider {
  color: var(--border-color);
  margin: 0 -4px;
}

/* 分页控件 */
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

/* 销售总额高亮 */
.highlight-bg-sales {
  background: var(--layout-bg);
  padding: 8px 12px;
  border-radius: 6px;
  border: 1px dashed #52c41a;
}

.total-price-display {
  font-size: 18px;
  font-weight: bold;
  color: #52c41a;
  font-family: monospace;
  line-height: 1;
  margin-top: 4px;
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