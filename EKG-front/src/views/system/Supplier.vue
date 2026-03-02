<template>
    <div class="page-container">
        <div class="vben-card search-wrapper">
            <div class="search-item">
                <span class="label">供应商名称：</span>
                <input type="text" v-model="searchQuery.keyword" placeholder="请输入关键字" class="vben-input"
                    @keyup.enter="handleSearch" />
            </div>
            <div class="search-item">
                <span class="label">评级：</span>
                <select v-model="searchQuery.rating" class="vben-input select" @change="handleSearch">
                    <option value="">全部</option>
                    <option value="A级">A级 (优秀)</option>
                    <option value="B级">B级 (良好)</option>
                    <option value="C级">C级 (合格)</option>
                    <option value="D级">D级 (淘汰)</option>
                </select>
            </div>
            <div class="search-item">
                <span class="label">状态：</span>
                <select v-model="searchQuery.status" class="vben-input select" @change="handleSearch">
                    <option value="">全部</option>
                    <option value="1">正常供货</option>
                    <option value="0">已冻结</option>
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
                <div class="toolbar-title">供应商列表</div>
                <div class="toolbar-actions">
                    <button class="vben-btn primary" @click="openModal('add')">
                        <Icon icon="mdi:truck-plus" /> 新增供应商
                    </button>
                </div>
            </div>

            <div class="table-container">
                <table class="vben-table">
                    <thead>
                        <tr>
                            <th width="60">序号</th>
                            <th>供应商全称/编码</th>
                            <th>供应品类</th>
                            <th>联系人</th>
                            <th>联系电话</th>
                            <th>质量评级</th>
                            <th>供货状态</th>
                            <th width="120">操作</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr v-for="(item, index) in paginatedList" :key="item.id">
                            <td>{{ (currentPage - 1) * pageSize + index + 1 }}</td>
                            <td>
                                <div class="company-name-info">
                                    <div class="real-name">{{ item.supplierName }}</div>
                                    <div class="username-sub">编码: {{ item.supplierCode }}</div>
                                </div>
                            </td>
                            <td>{{ item.category }}</td>
                            <td>
                                <div class="user-cell">
                                    <div class="text-avatar-small" :style="{ backgroundColor: item.avatarColor }">
                                        {{ item.contactPerson.charAt(0) }}
                                    </div>
                                    <span>{{ item.contactPerson }}</span>
                                </div>
                            </td>
                            <td>{{ item.phone }}</td>
                            <td>
                                <span class="rating-tag" :class="'rating-' + item.rating.charAt(0)">{{ item.rating
                                    }}</span>
                            </td>
                            <td>
                                <span class="status-dot" :class="item.status === 1 ? 'active' : 'disabled'"></span>
                                {{ item.status === 1 ? '正常' : '冻结' }}
                            </td>
                            <td>
                                <div class="action-links">
                                    <span class="link edit" @click="openModal('edit', item)">编辑</span>
                                    <span class="divider">|</span>
                                    <span class="link delete" @click="deleteItem(item)">删除</span>
                                </div>
                            </td>
                        </tr>
                        <tr v-if="paginatedList.length === 0">
                            <td colspan="8" class="empty-text">未能搜索到匹配的供应商数据</td>
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

        <transition name="modal-fade">
            <div class="modal-overlay" v-if="showModal" @click.self="closeModal">
                <div class="modal-content">
                    <div class="modal-header">
                        <h3>{{ modalType === 'add' ? '新增供应商' : '编辑供应商' }}</h3>
                        <Icon icon="mdi:close" class="modal-close" @click="closeModal" />
                    </div>
                    <div class="modal-body">
                        <div class="form-section-title">企业资质</div>
                        <div class="form-row">
                            <div class="form-item half">
                                <label>供应商全称 <span class="req">*</span></label>
                                <input type="text" v-model="formData.supplierName" placeholder="企业营业执照名称"
                                    class="vben-input" />
                            </div>
                            <div class="form-item half">
                                <label>供应商编码 <span class="req">*</span></label>
                                <input type="text" v-model="formData.supplierCode" placeholder="如 SUP-2026-001"
                                    class="vben-input" :disabled="modalType === 'edit'" />
                            </div>
                        </div>

                        <div class="form-row mt-4">
                            <div class="form-item half">
                                <label>主营品类</label>
                                <select v-model="formData.category" class="vben-input select">
                                    <option value="核心原物料">核心原物料</option>
                                    <option value="包装耗材">包装耗材</option>
                                    <option value="机电设备">机电设备</option>
                                    <option value="外包服务">外包服务</option>
                                </select>
                            </div>
                            <div class="form-item half">
                                <label>质量评级</label>
                                <select v-model="formData.rating" class="vben-input select">
                                    <option value="A级">A级 (优秀)</option>
                                    <option value="B级">B级 (良好)</option>
                                    <option value="C级">C级 (合格)</option>
                                    <option value="D级">D级 (淘汰)</option>
                                </select>
                            </div>
                        </div>

                        <div class="form-section-title mt-4">联络信息</div>
                        <div class="form-row">
                            <div class="form-item half">
                                <label>业务接口人 <span class="req">*</span></label>
                                <input type="text" v-model="formData.contactPerson" placeholder="接口人姓名"
                                    class="vben-input" />
                            </div>
                            <div class="form-item half">
                                <label>联系电话 <span class="req">*</span></label>
                                <input type="text" v-model="formData.phone" placeholder="手机号或座机" class="vben-input" />
                            </div>
                        </div>
                        <div class="form-item mt-4">
                            <label>合作状态</label>
                            <div class="radio-group">
                                <label><input type="radio" v-model="formData.status" :value="1" /> 正常供货</label>
                                <label><input type="radio" v-model="formData.status" :value="0" /> 冻结/终止</label>
                            </div>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button class="vben-btn default" @click="closeModal">取消</button>
                        <button class="vben-btn primary" @click="submitForm">确定保存</button>
                    </div>
                </div>
            </div>
        </transition>
    </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { Icon } from '@iconify/vue'

const searchQuery = ref({ keyword: '', rating: '', status: '' })

// 模拟 28 条供应商数据
const dataList = ref(Array.from({ length: 28 }, (_, i) => {
    const ratings = ['A级', 'B级', 'C级', 'D级']
    const categories = ['核心原物料', '包装耗材', '机电设备', '外包服务']
    const names = ['郑总', '吴厂长', '陈经理', '卫总监']
    return {
        id: i + 1,
        supplierCode: `SUP-2026-${(i + 1).toString().padStart(3, '0')}`,
        supplierName: `某某工业制造（东莞）厂 - ${i + 1}厂`,
        category: categories[i % 4],
        contactPerson: names[i % 4],
        phone: `1390000${(i + 1).toString().padStart(4, '0')}`,
        rating: ratings[i % 4],
        status: i % 7 === 0 ? 0 : 1, // 少数被冻结
        createTime: '2025-11-20',
        avatarColor: ['#1677ff', '#52c41a', '#faad14', '#eb2f96', '#722ed1'][i % 5]
    }
}))

const filteredList = computed(() => {
    return dataList.value.filter(item => {
        const matchKeyword = !searchQuery.value.keyword ||
            item.supplierName.includes(searchQuery.value.keyword) ||
            item.supplierCode.includes(searchQuery.value.keyword);
        const matchRating = !searchQuery.value.rating || item.rating === searchQuery.value.rating;
        const matchStatus = searchQuery.value.status === '' || item.status === Number(searchQuery.value.status);
        return matchKeyword && matchRating && matchStatus;
    })
})

const handleSearch = () => { currentPage.value = 1 }
const resetSearch = () => { searchQuery.value = { keyword: '', rating: '', status: '' }; currentPage.value = 1 }

// 分页逻辑
const currentPage = ref(1)
const pageSize = ref(10)
const totalItems = computed(() => filteredList.value.length)
const totalPages = computed(() => Math.max(1, Math.ceil(totalItems.value / pageSize.value)))

const paginatedList = computed(() => {
    const start = (currentPage.value - 1) * pageSize.value
    return filteredList.value.slice(start, start + pageSize.value)
})
const changePage = (page) => { if (page >= 1 && page <= totalPages.value) currentPage.value = page }

// 弹窗逻辑
const showModal = ref(false)
const modalType = ref('add')
const formData = ref({})

const openModal = (type, row = null) => {
    modalType.value = type
    if (type === 'edit' && row) {
        formData.value = { ...row }
    } else {
        formData.value = { supplierName: '', supplierCode: `SUP-2026-${Date.now().toString().slice(-4)}`, contactPerson: '', phone: '', category: '核心原物料', rating: 'B级', status: 1 }
    }
    showModal.value = true
}
const closeModal = () => { showModal.value = false }

const submitForm = () => {
    if (modalType.value === 'add') {
        dataList.value.unshift({
            id: Date.now(),
            ...formData.value,
            createTime: new Date().toISOString().split('T')[0],
            avatarColor: '#52c41a'
        })
    } else {
        const index = dataList.value.findIndex(u => u.id === formData.value.id)
        if (index > -1) dataList.value[index] = { ...formData.value }
    }
    closeModal()
}

const deleteItem = (row) => {
    if (confirm(`确定要删除供应商【${row.supplierName}】吗？这可能会影响历史采购单！`)) {
        dataList.value = dataList.value.filter(u => u.id !== row.id)
        if (paginatedList.value.length === 0 && currentPage.value > 1) currentPage.value--
    }
}
</script>

<style scoped>
/* 复用标准的 Vben 页面结构 CSS */
.page-container {
    display: flex;
    flex-direction: column;
    gap: 16px;
}

.vben-card {
    background: var(--content-bg);
    border: 1px solid var(--border-color);
    border-radius: 6px;
    padding: 16px;
}

.search-wrapper {
    display: flex;
    flex-wrap: wrap;
    gap: 20px;
    align-items: center;
}

.search-item {
    display: flex;
    align-items: center;
}

.search-item .label {
    font-size: 14px;
    color: var(--text-color);
    margin-right: 8px;
}

.vben-input {
    box-sizing: border-box;
    padding: 6px 12px;
    border: 1px solid var(--border-color);
    border-radius: 6px;
    background: var(--layout-bg);
    color: var(--text-color);
    font-size: 14px;
    outline: none;
    transition: all 0.3s;
    min-width: 180px;
}

.vben-input:focus {
    border-color: var(--sidebar-bg-active);
}

.vben-input.select {
    cursor: pointer;
}

.search-actions {
    margin-left: auto;
    display: flex;
    gap: 10px;
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
}

.vben-table th {
    background-color: var(--layout-bg);
    color: var(--text-color-secondary);
    font-weight: 500;
}

.vben-table tbody tr:hover {
    background-color: var(--hover-bg);
}

.company-name-info .real-name {
    color: var(--text-color);
    font-weight: 500;
}

.company-name-info .username-sub {
    color: var(--text-color-secondary);
    font-size: 12px;
    margin-top: 2px;
}

.user-cell {
    display: flex;
    align-items: center;
    gap: 8px;
    color: var(--text-color);
}

.text-avatar-small {
    width: 28px;
    height: 28px;
    border-radius: 50%;
    color: #fff;
    font-weight: bold;
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;
    font-size: 12px;
}

/* 评级标签定制颜色 */
.rating-tag {
    padding: 2px 8px;
    border-radius: 4px;
    font-size: 12px;
    font-weight: bold;
}

.rating-A {
    color: #52c41a;
    background: #f6ffed;
    border: 1px solid #b7eb8f;
}

.rating-B {
    color: #1677ff;
    background: #e6f4ff;
    border: 1px solid #91caff;
}

.rating-C {
    color: #faad14;
    background: #fffbe6;
    border: 1px solid #ffe58f;
}

.rating-D {
    color: #ff4d4f;
    background: #fff2f0;
    border: 1px solid #ffccc7;
}

.dark-theme .rating-tag {
    background: transparent;
}

.status-dot {
    display: inline-block;
    width: 6px;
    height: 6px;
    border-radius: 50%;
    margin-right: 6px;
    vertical-align: middle;
}

.status-dot.active {
    background-color: #1677ff;
    box-shadow: 0 0 4px #1677ff;
}

.status-dot.disabled {
    background-color: #ff4d4f;
}

.action-links {
    display: flex;
    align-items: center;
    gap: 8px;
}

.link {
    cursor: pointer;
    font-size: 13px;
    transition: opacity 0.2s;
}

.link:hover {
    opacity: 0.7;
}

.link.edit {
    color: var(--sidebar-bg-active);
}

.link.delete {
    color: #ff4d4f;
}

.divider {
    color: var(--border-color);
}

.empty-text {
    text-align: center;
    color: var(--text-color-secondary);
    padding: 30px !important;
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

.modal-overlay {
    position: fixed;
    top: 0;
    left: 0;
    width: 100vw;
    height: 100vh;
    background: rgba(0, 0, 0, 0.45);
    backdrop-filter: blur(2px);
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 9999;
}

.modal-content {
    background: var(--content-bg);
    width: 600px;
    border-radius: 8px;
    border: 1px solid var(--border-color);
    box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
}

.modal-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 16px 24px;
    border-bottom: 1px solid var(--border-color);
}

.modal-header h3 {
    margin: 0;
    font-size: 16px;
    color: var(--text-color);
}

.modal-close {
    font-size: 20px;
    color: var(--text-color-secondary);
    cursor: pointer;
    transition: color 0.2s;
}

.modal-close:hover {
    color: #ff4d4f;
}

.modal-body {
    padding: 24px;
    display: flex;
    flex-direction: column;
    gap: 16px;
}

.form-section-title {
    font-size: 13px;
    font-weight: bold;
    color: var(--text-color-secondary);
    border-left: 3px solid var(--sidebar-bg-active);
    padding-left: 8px;
    margin-bottom: 4px;
}

.mt-4 {
    margin-top: 8px;
}

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
}

.form-item .req {
    color: #ff4d4f;
    margin-left: 2px;
}

.form-item .vben-input {
    width: 100%;
}

.form-item .vben-input:disabled {
    background-color: var(--layout-bg);
    cursor: not-allowed;
    opacity: 0.7;
}

.radio-group {
    display: flex;
    gap: 16px;
    color: var(--text-color);
    font-size: 14px;
    align-items: center;
    height: 32px;
}

.radio-group label {
    cursor: pointer;
    display: flex;
    align-items: center;
    gap: 4px;
}

.radio-group input {
    accent-color: var(--sidebar-bg-active);
}

.modal-footer {
    display: flex;
    justify-content: flex-end;
    gap: 12px;
    padding: 12px 24px;
    border-top: 1px solid var(--border-color);
    background: var(--layout-bg);
    border-radius: 0 0 8px 8px;
}

.modal-fade-enter-active,
.modal-fade-leave-active {
    transition: all 0.3s cubic-bezier(0.2, 0, 0, 1);
}

.modal-fade-enter-from,
.modal-fade-leave-to {
    opacity: 0;
    transform: scale(0.95);
}
</style>