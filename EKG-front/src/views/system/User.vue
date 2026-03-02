<template>
  <div class="page-container">
    <div class="vben-card search-wrapper">
      <div class="search-item">
        <span class="label">员工姓名 / 账号：</span>
        <input type="text" v-model="searchQuery.keyword" placeholder="请输入关键字" class="vben-input"
          @keyup.enter="handleSearch" />
      </div>
      <div class="search-item">
        <span class="label">状态：</span>
        <select v-model="searchQuery.status" class="vben-input select" @change="handleSearch">
          <option value="">全部</option>
          <option value="1">启用</option>
          <option value="0">禁用</option>
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
        <div class="toolbar-title">员工账号列表</div>
        <div class="toolbar-actions">
          <button class="vben-btn primary" @click="openModal('add')">
            <Icon icon="mdi:account-plus-outline" /> 新增员工
          </button>
        </div>
      </div>

      <div class="table-container">
        <table class="vben-table">
          <thead>
            <tr>
              <th width="60">序号</th>
              <th>账号信息</th>
              <th>所属部门</th>
              <th>系统角色</th>
              <th>联系电话</th>
              <th>状态</th>
              <th>创建时间</th>
              <th width="160">操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(user, index) in paginatedList" :key="user.id">
              <td>{{ (currentPage - 1) * pageSize + index + 1 }}</td>
              <td>
                <div class="user-cell">
                  <div class="text-avatar-small" :style="{ backgroundColor: user.avatarColor }">
                    {{ user.realName.charAt(0) }}
                  </div>
                  <div class="user-name-info">
                    <div class="real-name">{{ user.realName }}</div>
                    <div class="username-sub">账号: {{ user.username }}</div>
                  </div>
                </div>
              </td>
              <td>{{ user.department }}</td>
              <td>
                <span class="role-tag" :class="{ 'super': user.role === '超级管理员' }">{{ user.role }}</span>
              </td>
              <td>{{ user.phone }}</td>
              <td>
                <span class="status-dot" :class="user.status === 1 ? 'active' : 'disabled'"></span>
                {{ user.status === 1 ? '启用' : '禁用' }}
              </td>
              <td>{{ user.createTime }}</td>
              <td>
                <div class="action-links">
                  <span class="link edit" @click="openModal('edit', user)">编辑</span>
                  <span class="divider">|</span>
                  <span class="link edit" @click="resetPassword(user)">重置密码</span>
                  <span class="divider">|</span>
                  <span class="link delete" @click="deleteUser(user)">删除</span>
                </div>
              </td>
            </tr>
            <tr v-if="paginatedList.length === 0">
              <td colspan="8" class="empty-text">未能搜索到匹配的人员数据</td>
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
      <div class="modal-overlay" v-if="showModal" @click.self="closeModal">
        <div class="modal-content">
          <div class="modal-header">
            <h3>{{ modalType === 'add' ? '新增员工账号' : '编辑员工信息' }}</h3>
            <Icon icon="mdi:close" class="modal-close" @click="closeModal" />
          </div>
          <div class="modal-body">

            <div class="form-section-title">账号信息</div>
            <div class="form-row">
              <div class="form-item half">
                <label>登录账号 <span class="req">*</span></label>
                <input type="text" v-model="formData.username" placeholder="建议使用工号或拼音" class="vben-input" />
              </div>
              <div class="form-item half" v-if="modalType === 'add'">
                <label>登录密码 <span class="req">*</span></label>
                <input type="password" v-model="formData.password" placeholder="请设置初始密码" class="vben-input" />
              </div>
            </div>

            <div class="form-section-title mt-4">基本信息</div>
            <div class="form-row">
              <div class="form-item half">
                <label>真实姓名 <span class="req">*</span></label>
                <input type="text" v-model="formData.realName" placeholder="如：张三" class="vben-input" />
              </div>
              <div class="form-item half">
                <label>联系电话</label>
                <input type="text" v-model="formData.phone" placeholder="请输入手机号" class="vben-input" />
              </div>
            </div>

            <div class="form-row">
              <div class="form-item half">
                <label>系统角色</label>
                <select v-model="formData.role" class="vben-input select">
                  <option value="超级管理员">超级管理员</option>
                  <option value="采购专员">采购专员</option>
                  <option value="销售专员">销售专员</option>
                  <option value="仓库管理员">仓库管理员</option>
                </select>
              </div>
              <div class="form-item half">
                <label>状态</label>
                <div class="radio-group">
                  <label><input type="radio" v-model="formData.status" :value="1" /> 启用</label>
                  <label><input type="radio" v-model="formData.status" :value="0" /> 禁用</label>
                </div>
              </div>
            </div>

          </div>
          <div class="modal-footer">
            <button class="vben-btn default" @click="closeModal">取消</button>
            <button class="vben-btn primary" @click="submitForm">确定提交</button>
          </div>
        </div>
      </div>
    </transition>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { Icon } from '@iconify/vue'

const searchQuery = ref({ keyword: '', status: '' })

// 基础数据 (25条)
const userList = ref(Array.from({ length: 25 }, (_, i) => ({
  id: i + 1,
  username: `user_${i + 1}`,
  realName: i === 0 ? '系统超管' : `员工${i + 1}`,
  department: i % 3 === 0 ? '技术部' : '业务部',
  role: i === 0 ? '超级管理员' : (i % 2 === 0 ? '销售专员' : '采购专员'),
  phone: `1380000${(i + 1).toString().padStart(4, '0')}`,
  status: i % 5 === 0 ? 0 : 1,
  createTime: '2026-03-01',
  avatarColor: ['#1677ff', '#52c41a', '#faad14', '#eb2f96', '#722ed1'][i % 5]
})))

// ================= 修复：前端纯响应式搜索拦截 =================
const filteredList = computed(() => {
  return userList.value.filter(user => {
    // 关键字匹配 (姓名或账号)
    const matchKeyword = !searchQuery.value.keyword ||
      user.username.includes(searchQuery.value.keyword) ||
      user.realName.includes(searchQuery.value.keyword);
    // 状态匹配
    const matchStatus = searchQuery.value.status === '' ||
      user.status === Number(searchQuery.value.status);

    return matchKeyword && matchStatus;
  })
})

const handleSearch = () => {
  currentPage.value = 1 // 搜索后重置到第一页
}

const resetSearch = () => {
  searchQuery.value = { keyword: '', status: '' }
  currentPage.value = 1
}

// 分页逻辑基于过滤后的数据
const currentPage = ref(1)
const pageSize = ref(10)
const totalItems = computed(() => filteredList.value.length)
const totalPages = computed(() => Math.max(1, Math.ceil(totalItems.value / pageSize.value)))

const paginatedList = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  return filteredList.value.slice(start, start + pageSize.value)
})

const changePage = (page) => {
  if (page >= 1 && page <= totalPages.value) {
    currentPage.value = page
  }
}

// 弹窗与增删改查
const showModal = ref(false)
const modalType = ref('add')
const formData = ref({ username: '', realName: '', password: '', phone: '', role: '采购专员', status: 1 })

const openModal = (type, row = null) => {
  modalType.value = type
  if (type === 'edit' && row) {
    formData.value = { ...row }
  } else {
    formData.value = { username: '', realName: '', password: '', phone: '', role: '采购专员', status: 1 }
  }
  showModal.value = true
}
const closeModal = () => { showModal.value = false }

const submitForm = () => {
  if (modalType.value === 'add') {
    userList.value.unshift({
      id: Date.now(),
      ...formData.value,
      createTime: new Date().toISOString().split('T')[0],
      avatarColor: '#1677ff',
      department: '新分配部门'
    })
  } else {
    const index = userList.value.findIndex(u => u.id === formData.value.id)
    if (index > -1) userList.value[index] = { ...formData.value }
  }
  closeModal()
}

const resetPassword = (row) => {
  alert(`已将用户【${row.username}】的密码重置为默认密码：123456`)
}

const deleteUser = (row) => {
  if (confirm(`确定要删除账号【${row.username}】吗？`)) {
    userList.value = userList.value.filter(u => u.id !== row.id)
    if (paginatedList.value.length === 0 && currentPage.value > 1) {
      currentPage.value--
    }
  }
}
</script>

<style scoped>
/* 保持原有 User.vue 样式不变 */
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
  min-width: 200px;
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

.user-cell {
  display: flex;
  align-items: center;
  gap: 10px;
}

.text-avatar-small {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  color: #fff;
  font-weight: bold;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.user-name-info .real-name {
  color: var(--text-color);
  font-weight: 500;
}

.user-name-info .username-sub {
  color: var(--text-color-secondary);
  font-size: 12px;
  margin-top: 2px;
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

.dark-theme .role-tag.super {
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