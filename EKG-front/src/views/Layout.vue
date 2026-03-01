<template>
  <el-container class="admin-layout">
    <el-aside :width="isCollapse ? '64px' : '260px'" class="premium-aside">
      <div class="logo-box" :class="{ 'is-collapsed': isCollapse }">
        <el-icon class="logo-icon"><i-carbon-machine-learning-model /></el-icon>
        <transition name="fade-text">
          <span v-show="!isCollapse" class="logo-text">SmartX Engine</span>
        </transition>
      </div>

      <el-menu :default-active="$route.path" router :collapse="isCollapse" :collapse-transition="false"
        background-color="transparent" text-color="var(--el-text-color-regular)" active-text-color="#3b82f6"
        class="premium-menu">
        <el-menu-item index="/sales/order">
          <el-icon><i-carbon-shopping-cart /></el-icon>
          <template #title>销售与订单网络</template>
        </el-menu-item>
        <el-menu-item index="/scm/inventory">
          <el-icon><i-carbon-delivery /></el-icon>
          <template #title>供应链与库存底座</template>
        </el-menu-item>
        <el-menu-item index="/finance/flow">
          <el-icon><i-carbon-finance /></el-icon>
          <template #title>智能财务核算中心</template>
        </el-menu-item>

        <div class="menu-divider" v-show="!isCollapse"></div>
        <div class="menu-group-title" v-show="!isCollapse">AI 引擎</div>

        <el-menu-item index="/rag/decision">
          <el-icon><i-carbon-network-4 /></el-icon>
          <template #title>图谱决策中枢 (RAG)</template>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container class="main-container">
      <el-header class="premium-header">
        <div class="header-left">
          <div class="collapse-trigger" @click="toggleCollapse">
            <el-icon :class="{ 'is-active': !isCollapse }"><i-carbon-menu /></el-icon>
          </div>
          <el-breadcrumb separator="/" class="custom-breadcrumb">
            <el-breadcrumb-item>Workspace</el-breadcrumb-item>
            <el-breadcrumb-item class="current-path">{{ $route.name || 'Dashboard' }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>

        <div class="header-right">
          <el-button circle size="default" @click="toggleTheme" class="theme-btn" style="margin-right: 20px;">
            <el-icon>
              <i-ep-moon v-if="!isDark" />
              <i-ep-sunny v-else />
            </el-icon>
          </el-button>

          <el-dropdown trigger="click" placement="bottom-end">
            <div class="user-profile">
              <div class="avatar-box">
                <el-icon><i-carbon-user-avatar /></el-icon>
              </div>
              <span class="username">Administrator</span>
              <el-icon class="arrow-down"><i-ep-arrow-down /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu class="premium-dropdown">
                <el-dropdown-item divided @click="handleLogout" style="color: #ef4444;">
                  <el-icon><i-carbon-logout /></el-icon>安全退出
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <el-main class="premium-main">
        <div class="page-wrapper">
          <router-view v-slot="{ Component }">
            <transition name="fade-slide" mode="out-in">
              <component :is="Component" />
            </transition>
          </router-view>
        </div>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = useRouter()
const isCollapse = ref(false)

// 🌟 全局持久化主题逻辑 (与 Login 同步)
const isDark = ref(false)

const applyTheme = () => {
  if (isDark.value) {
    document.documentElement.classList.add('dark')
    localStorage.setItem('smartx-theme', 'dark')
  } else {
    document.documentElement.classList.remove('dark')
    localStorage.setItem('smartx-theme', 'light')
  }
}

onMounted(() => {
  // 进入布局页时，自动读取并在整个系统应用保存的主题
  isDark.value = localStorage.getItem('smartx-theme') === 'dark'
  applyTheme()
})

const toggleTheme = () => {
  isDark.value = !isDark.value
  applyTheme()
}

const toggleCollapse = () => {
  isCollapse.value = !isCollapse.value
}

const handleLogout = () => {
  ElMessage.success('已退出工作台')
  router.push('/login')
}
</script>

<style scoped>
.admin-layout {
  height: 100vh;
  font-family: 'Inter', sans-serif;
  background-color: var(--el-bg-color-page);
}

.premium-aside {
  background-color: var(--el-bg-color);
  border-right: 1px solid var(--el-border-color-light);
  transition: width 0.3s cubic-bezier(0.2, 0, 0, 1) 0s;
  z-index: 100;
  overflow: hidden;
}

/* 🌟 完美的 Logo 盒子居中对齐 */
.logo-box {
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: flex-start;
  padding: 0 20px;
  border-bottom: 1px solid var(--el-border-color-light);
  white-space: nowrap;
  transition: all 0.3s ease;
}

.logo-box.is-collapsed {
  justify-content: center;
  /* 收缩时彻底居中 */
  padding: 0;
}

.logo-icon {
  font-size: 24px;
  color: #3b82f6;
  width: 24px;
  display: flex;
  justify-content: center;
}

.logo-text {
  font-size: 16px;
  font-weight: 700;
  margin-left: 12px;
  color: var(--el-text-color-primary);
}

.fade-text-enter-active,
.fade-text-leave-active {
  transition: opacity 0.2s ease, transform 0.2s ease;
}

.fade-text-enter-from,
.fade-text-leave-to {
  opacity: 0;
  transform: translateX(-10px);
}

.premium-menu {
  border-right: none;
  padding: 12px 0;
}

:deep(.el-menu-item) {
  margin: 4px 12px;
  border-radius: 8px;
  height: 44px;
  line-height: 44px;
}

:deep(.el-menu-item.is-active) {
  background-color: rgba(59, 130, 246, 0.1) !important;
  color: #3b82f6 !important;
  font-weight: 600;
}

:deep(.el-menu-item:hover:not(.is-active)) {
  background-color: var(--el-bg-color-page) !important;
}

.menu-divider {
  height: 1px;
  background-color: var(--el-border-color-light);
  margin: 16px 20px;
}

.menu-group-title {
  padding: 0 24px;
  font-size: 11px;
  color: var(--el-text-color-secondary);
  text-transform: uppercase;
  letter-spacing: 2px;
  margin-bottom: 8px;
}

.main-container {
  display: flex;
  flex-direction: column;
}

.premium-header {
  height: 64px;
  background-color: var(--el-bg-color-overlay);
  backdrop-filter: blur(12px);
  border-bottom: 1px solid var(--el-border-color-light);
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 24px;
  z-index: 90;
}

.header-left,
.header-right {
  display: flex;
  align-items: center;
}

.theme-btn {
  border-color: transparent;
  background-color: var(--el-fill-color-light);
}

.collapse-trigger {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 6px;
  cursor: pointer;
  color: var(--el-text-color-regular);
  transition: all 0.2s;
  margin-right: 20px;
}

.collapse-trigger:hover {
  background-color: var(--el-fill-color-light);
  color: var(--el-text-color-primary);
}

.collapse-trigger .el-icon {
  font-size: 20px;
  transition: transform 0.3s ease;
}

.collapse-trigger .el-icon.is-active {
  transform: rotate(180deg);
}

:deep(.custom-breadcrumb .el-breadcrumb__inner) {
  color: var(--el-text-color-regular);
}

:deep(.custom-breadcrumb .current-path) {
  color: var(--el-text-color-primary);
  font-weight: 600;
}

.user-profile {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 6px 12px;
  border-radius: 8px;
  transition: background-color 0.2s;
}

.user-profile:hover {
  background-color: var(--el-fill-color-light);
}

.avatar-box {
  width: 28px;
  height: 28px;
  background-color: var(--el-fill-color);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--el-text-color-regular);
  margin-right: 10px;
}

.username {
  font-size: 14px;
  color: var(--el-text-color-primary);
  font-weight: 500;
  margin-right: 8px;
}

.premium-main {
  padding: 24px;
  overflow-y: auto;
  background-color: var(--el-bg-color-page);
}

.page-wrapper {
  max-width: 1200px;
  margin: 0 auto;
}

.fade-slide-enter-active,
.fade-slide-leave-active {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.fade-slide-enter-from {
  opacity: 0;
  transform: translateY(10px);
}

.fade-slide-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}
</style>