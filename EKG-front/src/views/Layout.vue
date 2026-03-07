<template>
  <div class="vben-layout" :class="{ 'is-mini': isMini, 'is-hidden': isHidden, 'dark-theme': isDark }">

    <aside class="layout-sidebar">
      <div class="sidebar-logo">
        <Icon icon="mdi:cube-scan" class="logo-icon" />
        <span class="logo-title" v-show="!isMini">SmartX ERP</span>
      </div>

      <div class="sidebar-menu-scroll">
        <ul class="custom-menu">
          <template v-for="group in menuData" :key="group.key">
            <li class="menu-parent" :class="{ 'is-open': openKeys.includes(group.key) }"
              @click="handleParentClick(group.key)" @mouseenter="handleParentHover(group.key, true)"
              @mouseleave="handleParentHover(group.key, false)">
              <div class="menu-parent-left">
                <Icon :icon="group.icon" class="parent-icon" />
                <span class="parent-title" v-show="!isMini">{{ group.title }}</span>
              </div>
              <Icon icon="mdi:chevron-down" class="arrow-icon" v-show="!isMini" />

              <!-- Mini 模式下的浮层弹出菜单 -->
              <transition name="popover-fade">
                <div v-if="isMini && hoverGroupKey === group.key" class="mini-popover"
                  @mouseenter="handleParentHover(group.key, true)" @mouseleave="handleParentHover(group.key, false)">
                  <div class="popover-title">{{ group.title.replace(/ \(.+\)/, '') }}</div>
                  <div class="popover-item" v-for="child in group.children" :key="child.path"
                    :class="{ active: currentPath.includes(child.path) }" @click.stop="go(child.path)">
                    <Icon :icon="child.icon" class="popover-icon" />
                    <span>{{ child.title }}</span>
                  </div>
                </div>
              </transition>
            </li>

            <!-- 展开模式下正常显示的子菜单 -->
            <div v-if="!isMini" class="menu-children-wrapper"
              :style="{ maxHeight: openKeys.includes(group.key) ? '500px' : '0' }">
              <li class="menu-item" v-for="child in group.children" :key="child.path"
                :class="{ active: currentPath.includes(child.path) }" @click="go(child.path)" :title="child.title">
                <div class="menu-icon-wrapper">
                  <Icon :icon="child.icon" class="menu-icon" />
                </div>
                <span class="menu-title">{{ child.title }}</span>
              </li>
            </div>
          </template>
        </ul>
      </div>

      <div class="sidebar-trigger" @click="toggleMini" :title="isMini ? '展开菜单' : '收起菜单'">
        <Icon :icon="isMini ? 'mdi:chevron-double-right' : 'mdi:chevron-double-left'" class="trigger-icon" />
      </div>
    </aside>

    <div class="layout-main">
      <header class="layout-header">
        <div class="header-left">
          <div class="action-item" @click="toggleHidden" title="隐藏/显示侧边栏">
            <Icon :icon="isHidden ? 'mdi:format-indent-increase' : 'mdi:format-indent-decrease'" />
          </div>
          <div class="action-item" title="刷新页面" @click="handleRefresh">
            <Icon icon="mdi:reload" :class="{ 'spin-anim': isRefreshing }" />
          </div>
          <div class="breadcrumb">
            <span v-for="(item, index) in currentRouteInfo.breadcrumb" :key="index" class="bc-wrapper">
              <span class="bc-item" :class="{ 'bc-active': index === currentRouteInfo.breadcrumb.length - 1 }">{{ item
                }}</span>
              <span class="bc-separator" v-if="index < currentRouteInfo.breadcrumb.length - 1">/</span>
            </span>
          </div>
        </div>

        <div class="header-right">
          <!-- 🆕 系统设置按钮 -->
          <div class="action-item" title="系统设置" @click="go('/system/settings')">
            <Icon icon="mdi:cog-outline" />
          </div>

          <div class="action-item" title="主题切换" @click="toggleTheme">
            <Icon :icon="isDark ? 'mdi:weather-night' : 'mdi:weather-sunny'" />
          </div>

          <div class="action-item" title="消息通知" @click.stop="toggleMsgMenu">
            <Icon icon="mdi:bell-outline" />
            <span class="badge" v-if="unreadMsgCount > 0">{{ unreadMsgCount }}</span>
            <transition name="dropdown">
              <div v-show="showMsgMenu" class="msg-dropdown" @click.stop>
                <div class="msg-header">消息通知 ({{ unreadMsgCount }})</div>
                <div class="msg-list">
                  <div class="msg-item" v-for="msg in msgList" :key="msg.id">
                    <div class="msg-icon">
                      <Icon :icon="msg.icon" :style="{ color: msg.color }" />
                    </div>
                    <div class="msg-content">
                      <div class="msg-title">{{ msg.title }}</div>
                      <div class="msg-time">{{ msg.time }}</div>
                    </div>
                  </div>
                </div>
                <div class="msg-footer" @click="msgList = []">清空未读</div>
              </div>
            </transition>
          </div>

          <div class="action-item" title="全屏" @click="toggleFullScreen">
            <Icon :icon="isFullscreen ? 'mdi:fullscreen-exit' : 'mdi:fullscreen'" />
          </div>

          <div class="user-dropdown" @click.stop="toggleUserMenu">
            <div class="header-text-avatar">{{ userStore.avatarLetter }}</div>
            <span class="username">{{ userStore.realName }}</span>
            <Icon icon="mdi:chevron-down" style="font-size: 16px; color: var(--text-color); margin-left: 4px;" />
            <transition name="dropdown">
              <ul v-show="showUserMenu" class="dropdown-menu">
                <li @click.stop="go('/profile')">
                  <Icon icon="mdi:account-outline" /><span>个人中心</span>
                </li>
                <div class="dropdown-divider"></div>
                <li @click.stop="handleLogout" class="logout-item">
                  <Icon icon="mdi:logout" /><span>退出登录</span>
                </li>
              </ul>
            </transition>
          </div>
        </div>
      </header>

      <div class="layout-tabs">
        <div class="tabs-scroll-container">
          <div v-for="(tab, index) in tabsList" :key="tab.path" class="tab-item"
            :class="{ active: currentPath === tab.path }" @click="clickTab(tab.path)">
            <Icon :icon="tab.icon" class="tab-icon" />
            <span class="tab-title">{{ tab.title }}</span>
            <div v-if="tabsList.length > 1" class="tab-close-icon" @click.stop="closeTab(tab.path, index)">
              <Icon icon="mdi:close" />
            </div>
          </div>
        </div>
        <div class="tabs-actions" @click="closeOtherTabs" title="关闭其他">
          <Icon icon="mdi:chevron-down" />
        </div>
      </div>

      <main class="layout-content">
        <div class="content-wrapper">
          <router-view v-slot="{ Component }">
            <transition name="fade-slide" mode="out-in">
              <keep-alive>
                <component :is="Component" v-if="!isRefreshing" :key="currentPath" />
              </keep-alive>
            </transition>
          </router-view>
        </div>
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted, onUnmounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { Icon } from '@iconify/vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const isMini = ref(false)
const isHidden = ref(false)
const toggleMini = () => { isMini.value = !isMini.value; if (isMini.value) isHidden.value = false }
const toggleHidden = () => { isHidden.value = !isHidden.value }
const currentPath = computed(() => route.path)

// 🆕 Mini 模式悬停弹出控制
const hoverGroupKey = ref(null)
let hoverTimer = null

const handleParentClick = (key) => {
  if (isMini.value) return // mini 模式不切换展开
  if (openKeys.value.includes(key)) openKeys.value = openKeys.value.filter(k => k !== key)
  else openKeys.value.push(key)
}

const handleParentHover = (key, isEnter) => {
  if (!isMini.value) return
  clearTimeout(hoverTimer)
  if (isEnter) {
    hoverGroupKey.value = key
  } else {
    hoverTimer = setTimeout(() => { hoverGroupKey.value = null }, 150)
  }
}

const menuData = ref([
  {
    key: 'dashboard', title: '概览 (Dashboard)', icon: 'mdi:view-dashboard-outline',
    children: [
      { path: '/dashboard/analysis', title: '分析页', icon: 'mdi:google-analytics' },
      { path: '/dashboard/workbench', title: '工作台', icon: 'mdi:monitor-dashboard' }
    ]
  },
  {
    key: 'business', title: '业务管理', icon: 'mdi:briefcase-outline',
    children: [
      { path: '/sales', title: '销售管理', icon: 'mdi:cart-outline' },
      { path: '/purchase', title: '采购管理', icon: 'mdi:shopping-outline' },
      { path: '/inventory', title: '库存管理', icon: 'mdi:store-24-hour' }
    ]
  },
  {
    key: 'system', title: '系统管理', icon: 'mdi:cog-outline',
    children: [
      { path: '/system/user', title: '人员管理', icon: 'mdi:account-cog-outline' },
      { path: '/system/customer', title: '买家管理', icon: 'mdi:account-tie-outline' },
      { path: '/system/supplier', title: '供应商管理', icon: 'mdi:truck-delivery-outline' },
      { path: '/system/settings', title: '系统设置', icon: 'mdi:tune-vertical' }
    ]
  }
])

const openKeys = ref(['dashboard', 'business', 'system'])

const currentRouteInfo = computed(() => {
  if (currentPath.value.includes('/profile')) return { title: '个人中心', breadcrumb: ['个人中心'], icon: 'mdi:account-outline' }
  for (const group of menuData.value) {
    for (const child of group.children) {
      if (currentPath.value.includes(child.path)) {
        return { title: child.title, breadcrumb: [group.title.replace(/ \(.+\)/, ''), child.title], icon: child.icon }
      }
    }
  }
  return { title: '新页面', breadcrumb: ['首页'], icon: 'mdi:file-document-outline' }
})

const tabsList = ref([])
watch(() => route.path, (newPath) => {
  if (newPath === '/login') return
  if (!tabsList.value.some(tab => tab.path === newPath)) {
    const info = currentRouteInfo.value
    tabsList.value.push({ title: info.title, path: newPath, icon: info.icon })
  }
}, { immediate: true })

const clickTab = (path) => { if (currentPath.value !== path) router.push(path) }
const closeTab = (path, index) => {
  if (tabsList.value.length === 1) return
  tabsList.value.splice(index, 1)
  if (currentPath.value === path) {
    const nextTab = tabsList.value[index - 1] || tabsList.value[index]
    router.push(nextTab.path)
  }
}
const closeOtherTabs = () => { tabsList.value = tabsList.value.filter(tab => tab.path === currentPath.value) }

// Header 状态与方法
const isRefreshing = ref(false)
const isDark = ref(false)
const isFullscreen = ref(false)
const showUserMenu = ref(false)
const showMsgMenu = ref(false)

const msgList = ref([
  { id: 1, title: '新采购订单待审批', time: '10分钟前', icon: 'mdi:file-document-edit', color: '#1677ff' },
  { id: 2, title: '库存不足预警: 零件A', time: '1小时前', icon: 'mdi:alert-circle', color: '#ff4d4f' }
])
const unreadMsgCount = computed(() => msgList.value.length)

const go = (path) => { router.push(path); closeAllMenus() }
const handleRefresh = () => { isRefreshing.value = true; setTimeout(() => isRefreshing.value = false, 500) }
const toggleTheme = () => {
  isDark.value = !isDark.value
  if (isDark.value) document.documentElement.classList.add('dark')
  else document.documentElement.classList.remove('dark')
}
const toggleFullScreen = () => {
  if (!document.fullscreenElement) { document.documentElement.requestFullscreen(); isFullscreen.value = true }
  else { document.exitFullscreen(); isFullscreen.value = false }
}
const toggleUserMenu = () => { showUserMenu.value = !showUserMenu.value; showMsgMenu.value = false }
const toggleMsgMenu = () => { showMsgMenu.value = !showMsgMenu.value; showUserMenu.value = false }
const closeAllMenus = () => { showUserMenu.value = false; showMsgMenu.value = false }

const handleLogout = () => {
  ElMessageBox.confirm('确定要退出当前系统吗？', '提示', {
    confirmButtonText: '确定退出',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(() => {
    userStore.logout()
    ElMessage.success('已安全退出')
    router.push('/login')
  }).catch(() => { })
}

onMounted(() => {
  userStore.restoreFromStorage()
  document.addEventListener('click', closeAllMenus)
  document.addEventListener('fullscreenchange', () => { isFullscreen.value = !!document.fullscreenElement })
})
onUnmounted(() => {
  document.removeEventListener('click', closeAllMenus)
})
</script>

<style scoped>
.vben-layout {
  --sidebar-width: 210px;
  --sidebar-width-mini: 64px;
  --sidebar-width-hidden: 0px;
  --sidebar-bg: #001529;
  --sidebar-text: rgba(255, 255, 255, 0.65);
  --sidebar-text-active: #ffffff;
  --sidebar-bg-active: #1677ff;
  --header-height: 48px;
  --tabs-height: 38px;
  --layout-bg: #f0f2f5;
  --header-bg: #ffffff;
  --content-bg: #ffffff;
  --text-color: #333333;
  --text-color-secondary: #666666;
  --border-color: #f0f0f0;
  --hover-bg: rgba(0, 0, 0, 0.025);
  --dropdown-bg: #ffffff;
  --dropdown-shadow: 0 6px 16px -8px rgba(0, 0, 0, 0.08), 0 9px 28px 0 rgba(0, 0, 0, 0.05), 0 12px 48px 16px rgba(0, 0, 0, 0.03);
  display: flex;
  height: 100vh;
  width: 100vw;
  overflow: hidden;
  background-color: var(--layout-bg);
  color: var(--text-color);
  transition: background-color 0.3s;
}

.vben-layout.dark-theme {
  --sidebar-bg: #141414;
  --layout-bg: #000000;
  --header-bg: #141414;
  --content-bg: #141414;
  --text-color: #e5e6eb;
  --text-color-secondary: #a6a8ad;
  --border-color: #303030;
  --hover-bg: rgba(255, 255, 255, 0.08);
  --dropdown-bg: #1f1f1f;
}

.layout-sidebar {
  width: var(--sidebar-width);
  background-color: var(--sidebar-bg);
  color: var(--sidebar-text);
  display: flex;
  flex-direction: column;
  transition: width 0.3s cubic-bezier(0.2, 0, 0, 1) 0s, border-color 0.3s;
  z-index: 10;
  border-right: 1px solid var(--border-color);
  overflow: visible;
  white-space: nowrap;
}

.vben-layout.is-mini .layout-sidebar {
  width: var(--sidebar-width-mini);
}

.vben-layout.is-hidden .layout-sidebar {
  width: var(--sidebar-width-hidden);
  border-right-color: transparent;
}

.sidebar-logo {
  height: var(--header-height);
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: #fff;
  font-size: 16px;
  font-weight: 700;
  overflow: hidden;
  white-space: nowrap;
  background: rgba(0, 0, 0, 0.2);
  flex-shrink: 0;
}

.logo-icon {
  font-size: 24px;
  color: var(--sidebar-bg-active);
  flex-shrink: 0;
}

.sidebar-menu-scroll {
  flex: 1;
  overflow-y: auto;
  overflow-x: visible;
  padding-bottom: 20px;
}

.sidebar-menu-scroll::-webkit-scrollbar {
  width: 6px;
}

.sidebar-menu-scroll::-webkit-scrollbar-thumb {
  background: rgba(255, 255, 255, 0.2);
  border-radius: 3px;
}

.custom-menu {
  list-style: none;
  padding: 10px 0;
  margin: 0;
}

.menu-parent {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 16px;
  height: 40px;
  margin: 4px 8px;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s;
  color: rgba(255, 255, 255, 0.85);
  position: relative;
}

.menu-parent:hover {
  background-color: rgba(255, 255, 255, 0.05);
  color: #fff;
}

.menu-parent-left {
  display: flex;
  align-items: center;
}

.parent-icon {
  font-size: 18px;
  margin-right: 10px;
  width: 24px;
  text-align: center;
}

.parent-title {
  font-size: 14px;
  font-weight: 500;
}

.arrow-icon {
  font-size: 16px;
  transition: transform 0.3s;
  opacity: 0.6;
}

.menu-parent.is-open .arrow-icon {
  transform: rotate(180deg);
}

.menu-children-wrapper {
  overflow: hidden;
  transition: max-height 0.3s cubic-bezier(0.2, 0, 0, 1);
}

.menu-item {
  display: flex;
  align-items: center;
  padding: 0 16px 0 24px;
  height: 40px;
  margin: 4px 8px;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s;
  overflow: hidden;
}

.menu-item:hover {
  color: var(--sidebar-text-active);
}

.menu-item.active {
  background-color: var(--sidebar-bg-active);
  color: var(--sidebar-text-active);
}

.menu-icon-wrapper {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 24px;
  margin-right: 10px;
  flex-shrink: 0;
}

.menu-icon {
  font-size: 16px;
}

.menu-title {
  white-space: nowrap;
  font-size: 13.5px;
}

.vben-layout.is-mini .menu-parent {
  padding: 0;
  justify-content: center;
}

.vben-layout.is-mini .parent-icon {
  margin-right: 0;
  font-size: 20px;
}

/* ================= 🆕 Mini 模式浮层弹出菜单 ================= */
.mini-popover {
  position: absolute;
  left: calc(var(--sidebar-width-mini) - 8px);
  top: 0;
  min-width: 160px;
  background: var(--sidebar-bg);
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 6px;
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.3);
  padding: 6px 0;
  z-index: 999;
  white-space: nowrap;
}

.popover-title {
  padding: 8px 16px 6px;
  font-size: 12px;
  color: rgba(255, 255, 255, 0.45);
  font-weight: 500;
  border-bottom: 1px solid rgba(255, 255, 255, 0.06);
  margin-bottom: 4px;
}

.popover-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  cursor: pointer;
  color: rgba(255, 255, 255, 0.65);
  font-size: 13px;
  transition: all 0.2s;
}

.popover-item:hover {
  color: #fff;
  background: rgba(255, 255, 255, 0.08);
}

.popover-item.active {
  color: var(--sidebar-bg-active);
  background: rgba(22, 119, 255, 0.15);
}

.popover-icon {
  font-size: 15px;
}

.popover-fade-enter-active,
.popover-fade-leave-active {
  transition: all 0.2s ease;
}

.popover-fade-enter-from,
.popover-fade-leave-to {
  opacity: 0;
  transform: translateX(-6px);
}

.sidebar-trigger {
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  cursor: pointer;
  transition: all 0.3s;
  color: var(--sidebar-text);
  background: transparent;
}

.sidebar-trigger:hover {
  color: #fff;
  background-color: rgba(255, 255, 255, 0.05);
}

.trigger-icon {
  font-size: 18px;
}

/* ================= 主区域 ================= */
.layout-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.layout-header {
  height: var(--header-height);
  background: var(--header-bg);
  border-bottom: 1px solid var(--border-color);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 16px;
  flex-shrink: 0;
  transition: background-color 0.3s;
}

.header-left,
.header-right {
  display: flex;
  align-items: center;
  gap: 4px;
}

.action-item {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s;
  font-size: 18px;
  color: var(--text-color);
  position: relative;
}

.action-item:hover {
  background-color: var(--hover-bg);
}

.badge {
  position: absolute;
  top: 4px;
  right: 4px;
  min-width: 16px;
  height: 16px;
  background: #ff4d4f;
  color: #fff;
  font-size: 10px;
  font-weight: bold;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 4px;
  line-height: 1;
}

.breadcrumb {
  display: flex;
  align-items: center;
  gap: 4px;
  margin-left: 8px;
}

.bc-wrapper {
  display: flex;
  align-items: center;
  gap: 4px;
}

.bc-item {
  font-size: 14px;
  color: var(--text-color-secondary);
}

.bc-active {
  color: var(--text-color);
  font-weight: 500;
}

.bc-separator {
  color: var(--border-color);
  font-size: 12px;
}

/* 下拉菜单 */
.user-dropdown {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 0 8px;
  height: 36px;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s;
  position: relative;
}

.user-dropdown:hover {
  background-color: var(--hover-bg);
}

.header-text-avatar {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  background: var(--sidebar-bg-active);
  color: #fff;
  font-size: 13px;
  font-weight: bold;
  display: flex;
  align-items: center;
  justify-content: center;
}

.username {
  font-size: 14px;
  color: var(--text-color);
}

.dropdown-menu {
  position: absolute;
  top: 44px;
  right: 0;
  list-style: none;
  padding: 6px 0;
  margin: 0;
  background: var(--dropdown-bg);
  border-radius: 8px;
  box-shadow: var(--dropdown-shadow);
  border: 1px solid var(--border-color);
  min-width: 140px;
  z-index: 999;
}

.dropdown-menu li {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  font-size: 14px;
  color: var(--text-color);
  cursor: pointer;
  transition: all 0.2s;
}

.dropdown-menu li:hover {
  background-color: var(--hover-bg);
}

.logout-item:hover {
  color: #ff4d4f;
}

.dropdown-divider {
  height: 1px;
  background: var(--border-color);
  margin: 4px 8px;
}

.dropdown-enter-active,
.dropdown-leave-active {
  transition: all 0.25s ease;
}

.dropdown-enter-from,
.dropdown-leave-to {
  opacity: 0;
  transform: translateY(-6px);
}

/* 消息通知下拉 */
.msg-dropdown {
  position: absolute;
  top: 44px;
  right: -40px;
  width: 280px;
  background: var(--dropdown-bg);
  border-radius: 8px;
  box-shadow: var(--dropdown-shadow);
  border: 1px solid var(--border-color);
  z-index: 999;
  overflow: hidden;
}

.msg-header {
  padding: 12px 16px;
  font-size: 14px;
  font-weight: bold;
  color: var(--text-color);
  border-bottom: 1px solid var(--border-color);
}

.msg-list {
  max-height: 250px;
  overflow-y: auto;
}

.msg-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 12px 16px;
  border-bottom: 1px solid var(--border-color);
  transition: background 0.2s;
}

.msg-item:last-child {
  border-bottom: none;
}

.msg-item:hover {
  background-color: var(--hover-bg);
}

.msg-icon {
  font-size: 20px;
  padding-top: 2px;
}

.msg-content {
  flex: 1;
}

.msg-title {
  font-size: 13px;
  color: var(--text-color);
  margin-bottom: 4px;
}

.msg-time {
  font-size: 12px;
  color: var(--text-color-secondary);
}

.msg-footer {
  text-align: center;
  padding: 10px;
  font-size: 13px;
  color: var(--sidebar-bg-active);
  cursor: pointer;
  border-top: 1px solid var(--border-color);
  transition: background 0.2s;
}

.msg-footer:hover {
  background: var(--hover-bg);
}

/* Tab 栏 */
.layout-tabs {
  height: var(--tabs-height);
  background: var(--header-bg);
  border-bottom: 1px solid var(--border-color);
  display: flex;
  align-items: center;
  padding: 0 8px;
  flex-shrink: 0;
  transition: background-color 0.3s;
}

.tabs-scroll-container {
  flex: 1;
  display: flex;
  overflow-x: auto;
  gap: 6px;
  padding: 0 4px;
}

.tabs-scroll-container::-webkit-scrollbar {
  height: 0;
}

.tab-item {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 4px 12px;
  border-radius: 4px;
  font-size: 13px;
  color: var(--text-color-secondary);
  cursor: pointer;
  white-space: nowrap;
  transition: all 0.2s;
  border: 1px solid transparent;
}

.tab-item:hover {
  color: var(--text-color);
  background-color: var(--hover-bg);
}

.tab-item.active {
  color: var(--sidebar-bg-active);
  background: rgba(22, 119, 255, 0.08);
  border-color: rgba(22, 119, 255, 0.3);
}

.tab-icon {
  font-size: 14px;
}

.tab-close-icon {
  font-size: 14px;
  opacity: 0;
  transition: opacity 0.2s;
  display: flex;
  align-items: center;
  margin-left: 2px;
}

.tab-item:hover .tab-close-icon {
  opacity: 0.6;
}

.tab-close-icon:hover {
  opacity: 1;
  color: #ff4d4f;
}

.tabs-actions {
  width: 32px;
  height: 28px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  color: var(--text-color-secondary);
  font-size: 16px;
  border-left: 1px solid var(--border-color);
  margin-left: 4px;
}

.tabs-actions:hover {
  color: var(--text-color);
}

/* 内容区 */
.layout-content {
  flex: 1;
  overflow-y: auto;
  overflow-x: hidden;
}

.content-wrapper {
  padding: 16px;
}

/* 刷新旋转 */
.spin-anim {
  animation: spin 0.5s linear;
}

@keyframes spin {
  from {
    transform: rotate(0deg);
  }

  to {
    transform: rotate(360deg);
  }
}

/* 页面切换 */
.fade-slide-enter-active {
  transition: all 0.3s ease;
}

.fade-slide-leave-active {
  transition: all 0.2s ease;
}

.fade-slide-enter-from {
  opacity: 0;
  transform: translateX(10px);
}

.fade-slide-leave-to {
  opacity: 0;
}
</style>