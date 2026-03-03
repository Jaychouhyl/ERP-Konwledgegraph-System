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
                <span class="label">物料编码/名称：</span>
                <input type="text" v-model="searchQuery.keyword" placeholder="如 SKU 或 屏幕" class="vben-input"
                    @keyup.enter="handleSearch" />
            </div>
            <div class="search-item">
                <span class="label">物料类型：</span>
                <select v-model="searchQuery.type" class="vben-input select" @change="handleSearch">
                    <option value="">全部类型</option>
                    <option value="RAW">原料 (Raw Material)</option>
                    <option value="FINISHED">成品 (Finished Good)</option>
                </select>
            </div>
            <div class="search-item">
                <span class="label">库存状态：</span>
                <select v-model="searchQuery.stockStatus" class="vben-input select" @change="handleSearch">
                    <option value="">全部</option>
                    <option value="NORMAL">库存健康</option>
                    <option value="WARNING">低于安全库存 (预警)</option>
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
                <div class="toolbar-title">库存总览与批次溯源 (Batch Traceability)</div>
                <div class="toolbar-actions">
                    <button class="vben-btn default" @click="handleManualCheck">
                        <Icon icon="mdi:barcode-scan" /> 手工盘点
                    </button>
                    <button class="vben-btn primary" @click="handleExport">
                        <Icon icon="mdi:export" /> 导出报表
                    </button>
                </div>
            </div>

            <div class="table-container">
                <table class="vben-table">
                    <thead>
                        <tr>
                            <th width="40"></th>
                            <th>物料编码 (SKU)</th>
                            <th>物料名称</th>
                            <th>分类</th>
                            <th>所在仓库</th>
                            <th>当前总库存</th>
                            <th>安全库存线</th>
                            <th>健康状态</th>
                            <th width="120">操作</th>
                        </tr>
                    </thead>
                    <tbody v-for="item in paginatedList" :key="item.id">
                        <tr class="main-row" :class="{ 'is-expanded': expandedKeys.includes(item.id) }">
                            <td class="expand-cell" @click="toggleExpand(item.id)">
                                <Icon
                                    :icon="expandedKeys.includes(item.id) ? 'mdi:minus-box-outline' : 'mdi:plus-box-outline'"
                                    class="expand-icon" />
                            </td>
                            <td class="font-mono text-blue">{{ item.sku }}</td>
                            <td class="font-bold">{{ item.name }}</td>
                            <td>
                                <span class="type-tag" :class="item.type === 'RAW' ? 'raw' : 'finished'">
                                    {{ item.type === 'RAW' ? '原料' : '成品' }}
                                </span>
                            </td>
                            <td>{{ item.warehouse }}</td>
                            <td class="font-mono font-bold"
                                :class="{ 'text-danger': item.currentStock < item.safetyStock }">
                                {{ item.currentStock.toLocaleString() }}
                            </td>
                            <td class="font-mono text-gray">{{ item.safetyStock.toLocaleString() }}</td>
                            <td>
                                <div v-if="item.currentStock >= item.safetyStock" class="status-indicator normal">
                                    <span class="dot"></span> 健康
                                </div>
                                <div v-else class="status-indicator warning">
                                    <span class="dot"></span> 缺货预警
                                </div>
                            </td>
                            <td>
                                <div class="action-links">
                                    <span class="link primary" @click="toggleExpand(item.id)">
                                        {{ expandedKeys.includes(item.id) ? '收起溯源' : '批次溯源' }}
                                    </span>
                                </div>
                            </td>
                        </tr>

                        <tr v-if="expandedKeys.includes(item.id)" class="sub-row">
                            <td colspan="9" class="sub-cell">
                                <div class="sub-table-wrapper">
                                    <div class="sub-header">
                                        <Icon icon="mdi:source-branch" class="sub-icon" />
                                        <span>【{{ item.name }}】入库批次流向追踪</span>
                                    </div>
                                    <table class="vben-sub-table">
                                        <thead>
                                            <tr>
                                                <th>批次流水号 (Batch No.)</th>
                                                <th>关联采购单 (PO)</th>
                                                <th>溯源供应商</th>
                                                <th>该批次当前余量</th>
                                                <th>入库时间</th>
                                                <th>操作</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <tr v-for="batch in item.batches" :key="batch.batchNo">
                                                <td class="font-mono">{{ batch.batchNo }}</td>
                                                <td class="link primary font-mono"
                                                    @click="navToPurchase(batch.poNumber)">{{ batch.poNumber }}</td>
                                                <td>{{ batch.supplier }}</td>
                                                <td class="font-mono font-bold">{{ batch.qty.toLocaleString() }}</td>
                                                <td class="text-gray">{{ batch.inDate }}</td>
                                                <td>
                                                    <span class="link warning" @click="handleBatchQC(batch)">质量抽检</span>
                                                </td>
                                            </tr>
                                            <tr v-if="!item.batches || item.batches.length === 0">
                                                <td colspan="6" class="empty-text-sub">暂无入库批次记录</td>
                                            </tr>
                                        </tbody>
                                    </table>
                                </div>
                            </td>
                        </tr>
                    </tbody>
                    <tbody v-if="paginatedList.length === 0">
                        <tr>
                            <td colspan="9" class="empty-text">未能搜索到库存数据</td>
                        </tr>
                    </tbody>
                </table>
            </div>

            <div class="pagination-wrapper" v-if="totalItems > 0">
                <span class="page-info">共 {{ totalItems }} 条数据</span>
                <div class="page-controls">
                    <button class="page-btn" :disabled="currentPage === 1"
                        @click="changePage(currentPage - 1)">上一页</button>
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
    </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { Icon } from '@iconify/vue'
import { useRouter } from 'vue-router'

const router = useRouter()

// ================= 0. 全局 Message =================
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

// ================= 1. 数据模拟 (精妙的嵌套数据结构) =================
const searchQuery = ref({ keyword: '', type: '', stockStatus: '' })

// 包含批次数组的复杂实体
const dataList = ref([
    {
        id: 1, sku: 'SKU-RAW-8G3', name: '骁龙 8 Gen 3 处理器', type: 'RAW', warehouse: 'A区-精密电子仓',
        currentStock: 8500, safetyStock: 2000,
        batches: [
            { batchNo: 'BATCH-0301-A1', poNumber: 'PO-20260301-001', supplier: 'TSMC台积电', qty: 5000, inDate: '2026-03-01 14:30' },
            { batchNo: 'BATCH-0215-B2', poNumber: 'PO-20260215-022', supplier: 'TSMC台积电', qty: 3500, inDate: '2026-02-15 09:15' }
        ]
    },
    {
        id: 2, sku: 'SKU-RAW-IMX989', name: '索尼 IMX989 主摄', type: 'RAW', warehouse: 'B区-恒温光电仓',
        currentStock: 800, safetyStock: 1500, // <--- 故意设置缺货状态
        batches: [
            { batchNo: 'BATCH-0225-C1', poNumber: 'PO-20260225-088', supplier: '立讯精密', qty: 800, inDate: '2026-02-28 10:00' }
        ]
    },
    {
        id: 3, sku: 'SKU-FIN-S26PRO', name: 'SmartX 2026 Pro 手机', type: 'FINISHED', warehouse: 'F区-成品出货仓',
        currentStock: 1200, safetyStock: 500,
        batches: [
            { batchNo: 'MFG-20260302-01', poNumber: '内部生产工单-001', supplier: '自产装配线', qty: 1000, inDate: '2026-03-02 18:00' },
            { batchNo: 'MFG-20260220-05', poNumber: '内部生产工单-002', supplier: '自产装配线', qty: 200, inDate: '2026-02-20 17:30' }
        ]
    },
    {
        id: 4, sku: 'SKU-RAW-OLED2K', name: 'OLED 2K 曲面屏', type: 'RAW', warehouse: 'A区-精密电子仓',
        currentStock: 4500, safetyStock: 2000,
        batches: [
            { batchNo: 'BATCH-0210-D4', poNumber: 'PO-20260210-011', supplier: '京东方', qty: 4500, inDate: '2026-02-12 11:20' }
        ]
    },
    {
        id: 5, sku: 'SKU-RAW-BAT5K', name: '高密度硅负极电池', type: 'RAW', warehouse: 'C区-防爆危化仓',
        currentStock: 150, safetyStock: 1000, // <--- 缺货状态
        batches: [
            { batchNo: 'BATCH-0120-E1', poNumber: 'PO-20260120-009', supplier: '宁德时代', qty: 150, inDate: '2026-01-25 15:45' }
        ]
    }
])

const filteredList = computed(() => {
    return dataList.value.filter(item => {
        const matchKeyword = !searchQuery.value.keyword ||
            item.sku.includes(searchQuery.value.keyword) ||
            item.name.includes(searchQuery.value.keyword)
        const matchType = !searchQuery.value.type || item.type === searchQuery.value.type

        let matchStock = true
        if (searchQuery.value.stockStatus === 'NORMAL') matchStock = item.currentStock >= item.safetyStock
        if (searchQuery.value.stockStatus === 'WARNING') matchStock = item.currentStock < item.safetyStock

        return matchKeyword && matchType && matchStock
    })
})

const handleSearch = () => { currentPage.value = 1 }
const resetSearch = () => { searchQuery.value = { keyword: '', type: '', stockStatus: '' }; currentPage.value = 1 }

// 分页逻辑
const currentPage = ref(1)
const pageSize = ref(10)
const totalItems = computed(() => filteredList.value.length)
const totalPages = computed(() => Math.max(1, Math.ceil(totalItems.value / pageSize.value)))
const paginatedList = computed(() => filteredList.value.slice((currentPage.value - 1) * pageSize.value, currentPage.value * pageSize.value))
const changePage = (page) => { if (page >= 1 && page <= totalPages.value) currentPage.value = page }


// ================= 2. 主子表展开核心逻辑 =================
const expandedKeys = ref([])

const toggleExpand = (id) => {
    const index = expandedKeys.value.indexOf(id)
    if (index > -1) {
        expandedKeys.value.splice(index, 1) // 收起
    } else {
        expandedKeys.value.push(id) // 展开
    }
}

// ================= 3. 业务操作 =================
const navToPurchase = (poNumber) => {
    showMessage(`正在为您跳转至采购单：${poNumber} 的详细档案...`, 'info')
    // 实际开发中可以通过 router.push 携带参数跳回采购页
    setTimeout(() => {
        router.push({ path: '/purchase', query: { po: poNumber } })
    }, 1000)
}

const handleManualCheck = () => {
    showMessage('盘点机接口已激活，等待扫码枪数据接入...', 'warning')
}

const handleExport = () => {
    showMessage('正在生成库存与批次溯源 Excel 报表...', 'success')
}

const handleBatchQC = (batch) => {
    showMessage(`已向质检部发送指令，对批次【${batch.batchNo}】进行抽检排查。`, 'success')
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
    width: 150px;
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

.vben-table tbody tr.main-row:hover {
    background-color: var(--hover-bg);
}

.empty-text {
    text-align: center;
    padding: 40px !important;
    color: var(--text-color-secondary);
}

/* 主表特有样式 */
.main-row.is-expanded td {
    border-bottom-color: transparent;
    /* 展开时隐藏主行下边框使之连贯 */
}

.expand-cell {
    cursor: pointer;
    color: var(--text-color-secondary);
    transition: color 0.2s;
    text-align: center;
}

.expand-cell:hover {
    color: var(--sidebar-bg-active);
}

.expand-icon {
    font-size: 20px;
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

.text-gray {
    color: var(--text-color-secondary);
}

.text-danger {
    color: #ff4d4f;
}

/* 分类标签 */
.type-tag {
    padding: 2px 8px;
    border-radius: 4px;
    font-size: 12px;
    font-weight: bold;
    border: 1px solid;
}

.type-tag.raw {
    color: #1677ff;
    background: #e6f4ff;
    border-color: #91caff;
}

.type-tag.finished {
    color: #722ed1;
    background: #f9f0ff;
    border-color: #d3adf7;
}

.dark-theme .type-tag {
    background: transparent;
}

/* 库存健康指示器 */
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

.status-indicator.normal {
    color: #52c41a;
}

.status-indicator.normal .dot {
    background: #52c41a;
    box-shadow: 0 0 4px #52c41a;
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

/* ================= 批次溯源嵌套子表 ================= */
.sub-row {
    background-color: var(--layout-bg);
}

.dark-theme .sub-row {
    background-color: rgba(255, 255, 255, 0.02);
}

.sub-cell {
    padding: 0 !important;
}

.sub-table-wrapper {
    padding: 16px 24px 24px 64px;
    /* 故意左侧留白，产生缩进层级感 */
    border-bottom: 1px solid var(--border-color);
}

.sub-header {
    display: flex;
    align-items: center;
    gap: 6px;
    color: var(--sidebar-bg-active);
    font-weight: bold;
    font-size: 14px;
    margin-bottom: 12px;
}

.sub-icon {
    font-size: 18px;
}

.vben-sub-table {
    width: 100%;
    border-collapse: collapse;
    font-size: 13px;
    background: var(--content-bg);
    border-radius: 6px;
    overflow: hidden;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.vben-sub-table th,
.vben-sub-table td {
    padding: 10px 16px;
    border: 1px solid var(--border-color);
    text-align: left;
}

.vben-sub-table th {
    background-color: rgba(0, 0, 0, 0.02);
    color: var(--text-color-secondary);
    font-weight: bold;
}

.dark-theme .vben-sub-table th {
    background-color: rgba(255, 255, 255, 0.04);
}

.empty-text-sub {
    text-align: center;
    padding: 20px !important;
    color: var(--text-color-secondary);
}

/* 操作链接 */
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
    text-decoration: underline;
}

.link.primary {
    color: var(--sidebar-bg-active);
}

.link.warning {
    color: #faad14;
}

/* 分页 */
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
</style>