<template>
    <div class="page-container">
        <div class="vben-card search-wrapper">
            <div class="search-item">
                <span class="label">客户名称/编码：</span>
                <input type="text" v-model="searchQuery.keyword" placeholder="请输入关键字" class="vben-input"
                    @keyup.enter="handleSearch" />
            </div>
            <div class="search-item">
                <span class="label">客户等级：</span>
                <select v-model="searchQuery.level" class="vben-input select" @change="handleSearch">
                    <option value="">全部</option>
                    <option value="VIP客户">VIP客户</option>
                    <option value="大客户">大客户</option>
                    <option value="普通客户">普通客户</option>
                </select>
            </div>
            <div class="search-item">
                <span class="label">状态：</span>
                <select v-model="searchQuery.status" class="vben-input select" @change="handleSearch">
                    <option value="">全部</option>
                    <option value="1">合作中</option>
                    <option value="0">已流失</option>
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
                <div class="toolbar-title">买家/客户列表</div>
                <div class="toolbar-actions">
                    <button class="vben-btn primary" @click="openModal('add')">
                        <Icon icon="mdi:domain-plus" /> 新增客户
                    </button>
                </div>
            </div>

            <div class="table-container">
                <table class="vben-table">
                    <thead>
                        <tr>
                            <th width="60">序号</th>
                            <th>客户名称/编码</th>
                            <th>客户等级</th>
                            <th>主要联系人</th>
                            <th>联系电话</th>
                            <th>状态</th>
                            <th>创建时间</th>
                            <th width="120">操作</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr v-for="(item, index) in paginatedList" :key="item.id">
                            <td>{{ (currentPage - 1) * pageSize + index + 1 }}</td>
                            <td>
                                <div class="company-name-info">
                                    <div class="real-name">{{ item.customerName }}</div>
                                    <div class="username-sub">编码: {{ item.customerCode }}</div>
                                </div>
                            </td>
                            <td>
                                <span class="role-tag" :class="getLevelClass(item.level)">{{ item.level }}</span>
                            </td>
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
                                <span class="status-dot" :class="item.status === 1 ? 'active' : 'disabled'"></span>
                                {{ item.status === 1 ? '合作中' : '已流失' }}
                            </td>
                            <td>{{ item.createTime }}</td>
                            <td>
                                <div class="action-links">
                                    <span class="link edit" @click="openModal('edit', item)">编辑</span>
                                    <span class="divider">|</span>
                                    <span class="link delete" @click="deleteItem(item)">删除</span>
                                </div>
                            </td>
                        </tr>
                        <tr v-if="paginatedList.length === 0">
                            <td colspan="8" class="empty-text">未能搜索到匹配的客户数据</td>
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
                        <h3>{{ modalType === 'add' ? '新增客户信息' : '编辑客户信息' }}</h3>
                        <Icon icon="mdi:close" class="modal-close" @click="closeModal" />
                    </div>
                    <div class="modal-body">
                        <div class="form-section-title">企业信息</div>
                        <div class="form-row">
                            <div class="form-item half">
                                <label>客户名称 <span class="req">*</span></label>
                                <input type="text" v-model="formData.customerName" placeholder="请输入企业全称"
                                    class="vben-input" />
                            </div>
                            <div class="form-item half">
                                <label>客户编码 <span class="req">*</span></label>
                                <input type="text" v-model="formData.customerCode" placeholder="如 CUS-2026-001"
                                    class="vben-input" :disabled="modalType === 'edit'" />
                            </div>
                        </div>
                        <div class="form-item mt-4">
                            <label>企业地址</label>
                            <input type="text" v-model="formData.address" placeholder="请输入详细地址" class="vben-input" />
                        </div>

                        <div class="form-section-title mt-4">联系与策略</div>
                        <div class="form-row">
                            <div class="form-item half">
                                <label>主要联系人 <span class="req">*</span></label>
                                <input type="text" v-model="formData.contactPerson" placeholder="姓名"
                                    class="vben-input" />
                            </div>
                            <div class="form-item half">
                                <label>联系电话 <span class="req">*</span></label>
                                <input type="text" v-model="formData.phone" placeholder="手机号或座机" class="vben-input" />
                            </div>
                        </div>
                        <div class="form-row mt-4">
                            <div class="form-item half">
                                <label>客户等级</label>
                                <select v-model="formData.level" class="vben-input select">
                                    <option value="VIP客户">VIP客户</option>
                                    <option value="大客户">大客户</option>
                                    <option value="普通客户">普通客户</option>
                                </select>
                            </div>
                            <div class="form-item half">
                                <label>合作状态</label>
                                <div class="radio-group">
                                    <label><input type="radio" v-model="formData.status" :value="1" /> 合作中</label>
                                    <label><input type="radio" v-model="formData.status" :value="0" /> 已流失</label>
                                </div>
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

const searchQuery = ref({ keyword: '', level: '', status: '' })

// 模拟 35 条客户数据
const dataList = ref(Array.from({ length: 35 }, (_, i) => {
    const levels = ['VIP客户', '大客户', '普通客户']
    const names = ['赵总', '钱总', '孙经理', '李采购', '周总监']
    return {
        id: i + 1,
        customerCode: `CUS-2026-${(i + 1).toString().padStart(3, '0')}`,
        customerName: `某某科技（深圳）有限公司 - ${i + 1}部`,
        contactPerson: names[i % 5],
        phone: `1380000${(i + 1).toString().padStart(4, '0')}`,
        level: levels[i % 3],
        address: '广东省深圳市南山区科技园',
        status: i % 8 === 0 ? 0 : 1,
        createTime: '2026-03-01',
        avatarColor: ['#1677ff', '#52c41a', '#faad14', '#eb2f96', '#722ed1'][i % 5]
    }
}))

const getLevelClass = (level) => {
    if (level === 'VIP客户') return 'super' // 紫色/粉色高亮
    if (level === '大客户') return 'blue-tag'
    return ''
}

// 前端实时检索
const filteredList = computed(() => {
    return dataList.value.filter(item => {
        const matchKeyword = !searchQuery.value.keyword ||
            item.customerName.includes(searchQuery.value.keyword) ||
            item.customerCode.includes(searchQuery.value.keyword);
        const matchLevel = !searchQuery.value.level || item.level === searchQuery.value.level;
        const matchStatus = searchQuery.value.status === '' || item.status === Number(searchQuery.value.status);
        return matchKeyword && matchLevel && matchStatus;
    })
})

const handleSearch = () => { currentPage.value = 1 }
const resetSearch = () => { searchQuery.value = { keyword: '', level: '', status: '' }; currentPage.value = 1 }

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
        formData.value = { customerName: '', customerCode: `CUS-2026-${Date.now().toString().slice(-4)}`, contactPerson: '', phone: '', address: '', level: '普通客户', status: 1 }
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
            avatarColor: '#1677ff'
        })
    } else {
        const index = dataList.value.findIndex(u => u.id === formData.value.id)
        if (index > -1) dataList.value[index] = { ...formData.value }
    }
    closeModal()
}

const deleteItem = (row) => {
    if (confirm(`确定要删除客户【${row.customerName}】吗？`)) {
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

.role-tag {
    padding: 2px 8px;
    border-radius: 4px;
    font-size: 12px;
    background: var(--layout-bg);
    color: var(--text-color-secondary);
    border: 1px solid var(--border-color);
}

.role-tag.super {
    color: #eb2f96;
    border-color: #ffadd2;
    background: #fff0f6;
}

.role-tag.blue-tag {
    color: #1677ff;
    border-color: #91caff;
    background: #e6f4ff;
}

.dark-theme .role-tag.super,
.dark-theme .role-tag.blue-tag {
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
    background-color: #52c41a;
    box-shadow: 0 0 4px #52c41a;
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