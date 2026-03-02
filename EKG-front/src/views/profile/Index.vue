<template>
  <div class="profile-container">
    <div class="profile-header card-base">
      <div class="profile-text-avatar">{{ userInfo.realName.charAt(0).toUpperCase() }}</div>
      <div class="profile-info">
        <h2 class="profile-name">{{ userInfo.realName }}</h2>
        <p class="profile-desc">
          <Icon icon="mdi:shield-account-outline" class="desc-icon" /> {{ userInfo.role }}
          <span class="divider">|</span>
          <Icon icon="mdi:domain" class="desc-icon" /> {{ userInfo.department }}
        </p>
      </div>
    </div>

    <div class="profile-body">

      <div class="info-card card-base">
        <div class="card-title">
          <span>基本信息</span>
          <button class="text-btn" @click="openEditModal">
            <Icon icon="mdi:square-edit-outline" /> 编辑
          </button>
        </div>
        <ul class="info-list">
          <li>
            <span class="label">登录账号</span>
            <span class="value lock-text">{{ userInfo.username }}
              <Icon icon="mdi:lock-outline" title="不可修改" />
            </span>
          </li>
          <li><span class="label">真实姓名</span><span class="value">{{ userInfo.realName }}</span></li>
          <li><span class="label">联系电话</span><span class="value">{{ userInfo.phone || '未填写' }}</span></li>
          <li><span class="label">电子邮箱</span><span class="value">{{ userInfo.email || '未填写' }}</span></li>
          <li><span class="label">注册时间</span><span class="value">{{ userInfo.regDate }}</span></li>
        </ul>
      </div>

      <div class="info-card card-base">
        <div class="card-title"><span>安全设置</span></div>
        <ul class="security-list">
          <li>
            <div class="sec-left">
              <div class="sec-name">账户密码</div>
              <div class="sec-desc">定期修改密码有助于保护账户安全</div>
            </div>
            <button class="vben-btn default" @click="openPwdModal">修改密码</button>
          </li>
          <li>
            <div class="sec-left">
              <div class="sec-name">密保手机</div>
              <div class="sec-desc">已绑定手机：{{ maskPhone(userInfo.phone) }}</div>
            </div>
            <div class="sec-status success">
              <Icon icon="mdi:check-circle" /> 已绑定
            </div>
          </li>
        </ul>
      </div>
    </div>

    <transition name="modal-fade">
      <div class="modal-overlay" v-if="showEditModal" @click.self="closeEditModal">
        <div class="modal-content">
          <div class="modal-header">
            <h3>编辑个人信息</h3>
            <Icon icon="mdi:close" class="modal-close" @click="closeEditModal" />
          </div>
          <div class="modal-body">
            <div class="form-item">
              <label>登录账号</label>
              <input type="text" :value="editForm.username" class="vben-input disabled" disabled title="登录账号不可修改" />
              <span class="form-tip">用于系统登录的唯一凭证，不可修改。</span>
            </div>
            <div class="form-item mt-4">
              <label>真实姓名 <span class="req">*</span></label>
              <input type="text" v-model="editForm.realName" class="vben-input" placeholder="请输入您在网页上显示的姓名" />
            </div>
            <div class="form-item mt-4">
              <label>联系电话</label>
              <input type="text" v-model="editForm.phone" class="vben-input" placeholder="请输入手机号" />
            </div>
            <div class="form-item mt-4">
              <label>电子邮箱</label>
              <input type="text" v-model="editForm.email" class="vben-input" placeholder="请输入邮箱地址" />
            </div>
          </div>
          <div class="modal-footer">
            <button class="vben-btn default" @click="closeEditModal">取消</button>
            <button class="vben-btn primary" @click="saveEditForm">保存更改</button>
          </div>
        </div>
      </div>
    </transition>

    <transition name="modal-fade">
      <div class="modal-overlay" v-if="showPwdModal" @click.self="closePwdModal">
        <div class="modal-content">
          <div class="modal-header">
            <h3>修改密码</h3>
            <Icon icon="mdi:close" class="modal-close" @click="closePwdModal" />
          </div>
          <div class="modal-body">
            <div class="form-item">
              <label>当前密码 <span class="req">*</span></label>
              <input type="password" v-model="pwdForm.oldPwd" class="vben-input" placeholder="请输入当前使用的密码" />
            </div>
            <div class="form-item mt-4">
              <label>新密码 <span class="req">*</span></label>
              <input type="password" v-model="pwdForm.newPwd" class="vben-input" placeholder="请输入新密码 (至少6位)" />
            </div>
            <div class="form-item mt-4">
              <label>确认新密码 <span class="req">*</span></label>
              <input type="password" v-model="pwdForm.confirmPwd" class="vben-input" placeholder="请再次输入新密码" />
            </div>
            <div v-if="pwdError" class="error-msg">
              <Icon icon="mdi:alert-circle" /> {{ pwdError }}
            </div>
          </div>
          <div class="modal-footer">
            <button class="vben-btn default" @click="closePwdModal">取消</button>
            <button class="vben-btn primary" @click="submitPwdChange">确认修改</button>
          </div>
        </div>
      </div>
    </transition>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { Icon } from '@iconify/vue'

// ================= 1. 用户基础数据 =================
const userInfo = reactive({
  username: 'admin',
  realName: 'Admin',
  role: '超级管理员',
  phone: '13812345678',
  email: 'admin@smartx.com',
  department: '核心开发团队',
  regDate: '2026-01-01'
})

// 脱敏手机号工具函数
const maskPhone = (phone) => {
  if (!phone || phone.length !== 11) return '未绑定'
  return phone.substring(0, 3) + '****' + phone.substring(7)
}

// ================= 2. 编辑信息逻辑 =================
const showEditModal = ref(false)
const editForm = reactive({})

const openEditModal = () => {
  Object.assign(editForm, userInfo)
  showEditModal.value = true
}
const closeEditModal = () => { showEditModal.value = false }

const saveEditForm = () => {
  if (!editForm.realName.trim()) {
    alert('真实姓名不能为空！')
    return
  }
  // 保存时，确保 username 不被恶意覆盖
  userInfo.realName = editForm.realName
  userInfo.phone = editForm.phone
  userInfo.email = editForm.email
  closeEditModal()
}

// ================= 3. 修改密码逻辑 =================
const showPwdModal = ref(false)
const pwdForm = reactive({ oldPwd: '', newPwd: '', confirmPwd: '' })
const pwdError = ref('')

const openPwdModal = () => {
  pwdForm.oldPwd = ''
  pwdForm.newPwd = ''
  pwdForm.confirmPwd = ''
  pwdError.value = ''
  showPwdModal.value = true
}
const closePwdModal = () => { showPwdModal.value = false }

const submitPwdChange = () => {
  pwdError.value = ''
  if (!pwdForm.oldPwd) return pwdError.value = '请输入当前密码'
  if (!pwdForm.newPwd || pwdForm.newPwd.length < 6) return pwdError.value = '新密码不能少于6个字符'
  if (pwdForm.newPwd !== pwdForm.confirmPwd) return pwdError.value = '两次输入的新密码不一致'

  // 模拟接口请求
  setTimeout(() => {
    alert('密码修改成功，请妥善保管！(实际应用中可能需要重新登录)')
    closePwdModal()
  }, 300)
}
</script>

<style scoped>
.profile-container {
  max-width: 1000px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.card-base {
  background: var(--content-bg);
  border: 1px solid var(--border-color);
  border-radius: 8px;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.02);
  transition: all 0.3s;
}

/* ================= 头部样式 ================= */
.profile-header {
  display: flex;
  align-items: center;
  padding: 30px 40px;
}

.profile-text-avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background: var(--sidebar-bg-active);
  color: #fff;
  font-size: 36px;
  font-weight: bold;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 24px;
  box-shadow: 0 4px 12px rgba(22, 119, 255, 0.3);
  flex-shrink: 0;
}

.profile-name {
  margin: 0 0 10px 0;
  font-size: 26px;
  color: var(--text-color);
  font-weight: bold;
}

.profile-desc {
  margin: 0;
  color: var(--text-color-secondary);
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.desc-icon {
  font-size: 16px;
  opacity: 0.8;
}

.divider {
  color: var(--border-color);
}

/* ================= 主体内容 ================= */
.profile-body {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.info-card {
  padding: 24px 30px;
}

.card-title {
  font-size: 16px;
  font-weight: bold;
  color: var(--text-color);
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid var(--border-color);
  display: flex;
  justify-content: space-between;
  align-items: center;
}

/* 基础信息列表 */
.info-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.info-list li {
  margin-bottom: 20px;
  font-size: 14px;
  display: flex;
  align-items: center;
}

.info-list li:last-child {
  margin-bottom: 0;
}

.label {
  color: var(--text-color-secondary);
  width: 100px;
  flex-shrink: 0;
}

.value {
  color: var(--text-color);
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 6px;
}

.lock-text {
  color: var(--text-color-secondary);
  font-weight: normal;
}

/* 安全设置列表 */
.security-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.security-list li {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 20px;
  margin-bottom: 20px;
  border-bottom: 1px dashed var(--border-color);
}

.security-list li:last-child {
  border-bottom: none;
  margin-bottom: 0;
  padding-bottom: 0;
}

.sec-name {
  font-size: 14px;
  font-weight: bold;
  color: var(--text-color);
  margin-bottom: 6px;
}

.sec-desc {
  font-size: 13px;
  color: var(--text-color-secondary);
}

.sec-status.success {
  font-size: 13px;
  color: #52c41a;
  display: flex;
  align-items: center;
  gap: 4px;
}

/* 按钮通用 */
.vben-btn {
  display: flex;
  align-items: center;
  justify-content: center;
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
  box-shadow: 0 2px 6px rgba(22, 119, 255, 0.2);
}

.text-btn {
  background: transparent;
  color: var(--sidebar-bg-active);
  border: none;
  cursor: pointer;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 0;
}

.text-btn:hover {
  opacity: 0.8;
}

/* ================= 弹窗系统 ================= */
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
  width: 460px;
  border-radius: 8px;
  border: 1px solid var(--border-color);
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.15);
  overflow: hidden;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 18px 24px;
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
  transition: color 0.2s;
}

.modal-close:hover {
  color: #ff4d4f;
}

.modal-body {
  padding: 24px;
}

.form-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-item label {
  font-size: 13.5px;
  color: var(--text-color);
  font-weight: 500;
}

.form-item .req {
  color: #ff4d4f;
  margin-left: 2px;
}

.form-tip {
  font-size: 12px;
  color: var(--text-color-secondary);
  margin-top: 2px;
}

.vben-input {
  box-sizing: border-box;
  padding: 8px 12px;
  border: 1px solid var(--border-color);
  border-radius: 6px;
  background: var(--layout-bg);
  color: var(--text-color);
  font-size: 14px;
  outline: none;
  transition: all 0.3s;
  width: 100%;
}

.vben-input:focus {
  border-color: var(--sidebar-bg-active);
  box-shadow: 0 0 0 2px rgba(22, 119, 255, 0.1);
}

.vben-input.disabled {
  background-color: var(--hover-bg);
  cursor: not-allowed;
  color: var(--text-color-secondary);
  border-color: transparent;
}

.error-msg {
  margin-top: 16px;
  color: #ff4d4f;
  font-size: 13px;
  display: flex;
  align-items: center;
  gap: 4px;
  background: #fff2f0;
  padding: 8px 12px;
  border-radius: 6px;
  border: 1px solid #ffccc7;
}

.dark-theme .error-msg {
  background: rgba(255, 77, 79, 0.1);
  border-color: rgba(255, 77, 79, 0.2);
}

.mt-4 {
  margin-top: 16px;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 16px 24px;
  border-top: 1px solid var(--border-color);
  background: var(--layout-bg);
}

/* 弹窗动画 */
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