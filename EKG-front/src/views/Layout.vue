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

            <li class="menu-parent" :class="{
              'is-open': openKeys.includes(group.key) && !isMini,
              'is-active': isMini && hasActiveChild(group)
            }" @click="toggleMenu(group.key)" @mouseenter="handleMouseEnter($event, group)"
              @mouseleave="handleMouseLeave">
              <div class="menu-parent-left">
                <Icon :icon="group.icon" class="parent-icon" />
                <span class="parent-title" v-show="!isMini">{{ group.title }}</span>
              </div>
              <Icon icon="mdi:chevron-down" class="arrow-icon" v-show="!isMini" />
            </li>

            <div class="menu-children-wrapper" v-show="!isMini"
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

    <transition name="fade-slide-right">
      <div v-if="hoverMenuState.visible && isMini" class="floating-popover"
        :style="{ top: hoverMenuState.top + 'px', left: '64px' }" @mouseenter="handlePopoverEnter"
        @mouseleave="handlePopoverLeave">
        <div class="floating-popover-title">{{ hoverMenuState.title }}</div>
        <div v-for="child in hoverMenuState.children" :key="child.path" class="floating-popover-item"
          :class="{ active: currentPath.includes(child.path) }" @click="go(child.path)">
          <Icon :icon="child.icon" class="floating-icon" />
          <span>{{ child.title }}</span>
        </div>
      </div>
    </transition>

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
            <div class="header-text-avatar">A</div>
            <span class="username">Admin</span>
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

const router = useRouter()
const route = useRoute()

// 侧边栏折叠状态
const isMini = ref(false)
const isHidden = ref(false)
const toggleMini = () => { isMini.value = !isMini.value; if (isMini.value) isHidden.value = false }
const toggleHidden = () => { isHidden.value = !isHidden.value }
const currentPath = computed(() => route.path)

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
      { path: '/system/supplier', title: '供应商管理', icon: 'mdi:truck-delivery-outline' }
    ]
  }
])

const openKeys = ref(['dashboard', 'business', 'system'])
const toggleMenu = (key) => {
  if (isMini.value) return // 迷你模式下禁用点击折叠
  if (openKeys.value.includes(key)) openKeys.value = openKeys.value.filter(k => k !== key)
  else openKeys.value.push(key)
}

// 检查某个父菜单是否包含当前激活的子菜单
const hasActiveChild = (group) => {
  return group.children.some(child => currentPath.value.includes(child.path))
}

// ================= 新增：悬浮窗逻辑 =================
const hoverMenuState = ref({
  visible: false,
  top: 0,
  title: '',
  children: []
})
let hoverTimer = null

const handleMouseEnter = (event, group) => {
  if (!isMini.value) return
  clearTimeout(hoverTimer)
  const rect = event.currentTarget.getBoundingClientRect()
  hoverMenuState.value = {
    visible: true,
    top: rect.top, // 精确对准图标高度
    title: group.title,
    children: group.children
  }
}

const handleMouseLeave = () => {
  if (!isMini.value) return
  hoverTimer = setTimeout(() => { hoverMenuState.value.visible = false }, 150)
}
const handlePopoverEnter = () => { clearTimeout(hoverTimer) }
const handlePopoverLeave = () => {
  hoverTimer = setTimeout(() => { hoverMenuState.value.visible = false }, 150)
}
// ==================================================

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

const isRefreshing = ref(false)
const isDark = ref(false)
const isFullscreen = ref(false)
const showUserMenu = ref(false)
const showMsgMenu = ref(false)

const msgList = ref([{ id: 1, title: '新采购订单待审批', time: '10分钟前', icon: 'mdi:file-document-edit', color: '#1677ff' }, { id: 2, title: '库存不足预警: 零件A', time: '1小时前', icon: 'mdi:alert-circle', color: '#ff4d4f' }])
const unreadMsgCount = computed(() => msgList.value.length)

const go = (path) => {
  router.push(path)
  closeAllMenus()
  hoverMenuState.value.visible = false // 点击路由后自动隐藏浮窗
}
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
const handleLogout = () => { closeAllMenus(); router.push('/login') }

onMounted(() => {
  document.addEventListener('click', closeAllMenus)
  document.addEventListener('fullscreenchange', () => { isFullscreen.value = !!document.fullscreenElement })
})
onUnmounted(() => { document.removeEventListener('click', closeAllMenus) })
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
  --dropdown-shadow: 0 6px 16px -8px rgba(0, 0, 0, 0.3), 0 9px 28px 0 rgba(0, 0, 0, 0.2), 0 12px 48px 16px rgba(0, 0, 0, 0.1);
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
  overflow: hidden;
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
  overflow-x: hidden;
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

/* 收起时的父菜单激活样式 */
.vben-layout.is-mini .menu-parent.is-active {
  background-color: var(--sidebar-bg-active);
  color: #fff;
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
  color: var(--sidebar-text-active);
  background: rgba(255, 255, 255, 0.05);
}

.trigger-icon {
  font-size: 22px;
}

/* ================= 悬浮框样式 ================= */
.floating-popover {
  position: fixed;
  z-index: 9999;
  width: 180px;
  background: var(--dropdown-bg);
  border: 1px solid var(--border-color);
  border-radius: 6px;
  box-shadow: var(--dropdown-shadow);
  padding: 8px 0;
  overflow: hidden;
}

.floating-popover-title {
  padding: 4px 16px 8px;
  font-size: 13px;
  font-weight: bold;
  color: var(--text-color-secondary);
  border-bottom: 1px solid var(--border-color);
  margin-bottom: 4px;
}

.floating-popover-item {
  padding: 10px 16px;
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  font-size: 14px;
  color: var(--text-color);
  transition: all 0.2s;
}

.floating-popover-item:hover {
  background: var(--hover-bg);
  color: var(--sidebar-bg-active);
}

.floating-popover-item.active {
  color: var(--sidebar-bg-active);
  font-weight: bold;
  background: rgba(22, 119, 255, 0.1);
}

.floating-icon {
  font-size: 16px;
}

.layout-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.layout-header {
  height: var(--header-height);
  background: var(--header-bg);
  display: flex;
  align-items: center;
  justify-content: space-between;
  z-index: 9;
  padding: 0 16px 0 0;
  transition: background-color 0.3s, border-color 0.3s;
}

.action-item {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  padding: 0 12px;
  cursor: pointer;
  font-size: 20px;
  color: var(--text-color);
  transition: all 0.3s;
  position: relative;
}

.action-item:hover {
  background-color: var(--hover-bg);
}

.header-left {
  display: flex;
  align-items: center;
  height: 100%;
}

.breadcrumb {
  margin-left: 8px;
  display: flex;
  align-items: center;
  font-size: 14px;
}

.bc-wrapper {
  display: flex;
  align-items: center;
}

.bc-item {
  color: var(--text-color-secondary);
  transition: color 0.3s;
}

.bc-item.bc-active {
  color: var(--text-color);
  font-weight: 500;
}

.bc-separator {
  margin: 0 8px;
  color: var(--text-color-secondary);
  opacity: 0.5;
  font-size: 12px;
}

.header-right {
  display: flex;
  align-items: center;
  height: 100%;
  gap: 4px;
  padding-right: 16px;
}

.spin-anim {
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from {
    transform: rotate(0deg);
  }

  to {
    transform: rotate(360deg);
  }
}

.badge {
  position: absolute;
  top: 10px;
  right: 6px;
  background-color: #ff4d4f;
  color: #fff;
  font-size: 12px;
  line-height: 14px;
  padding: 0 4px;
  border-radius: 8px;
  font-weight: bold;
  transform: scale(0.85);
}

.msg-dropdown {
  position: absolute;
  top: 48px;
  right: -60px;
  width: 300px;
  background: var(--dropdown-bg);
  box-shadow: var(--dropdown-shadow);
  border: 1px solid var(--border-color);
  border-radius: 6px;
  overflow: hidden;
  z-index: 100;
  cursor: default;
}

.msg-header {
  padding: 12px 16px;
  border-bottom: 1px solid var(--border-color);
  font-weight: 600;
  font-size: 14px;
  display: flex;
  justify-content: space-between;
}

.msg-list {
  max-height: 300px;
  overflow-y: auto;
}

.msg-item {
  display: flex;
  padding: 12px 16px;
  border-bottom: 1px solid var(--border-color);
  cursor: pointer;
  transition: background 0.3s;
}

.msg-item:hover {
  background: var(--hover-bg);
}

.msg-icon {
  width: 32px;
  height: 32px;
  background: var(--layout-bg);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  margin-right: 12px;
}

.msg-content {
  flex: 1;
  overflow: hidden;
}

.msg-title {
  font-size: 14px;
  color: var(--text-color);
  margin-bottom: 4px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.msg-time {
  font-size: 12px;
  color: var(--text-color-secondary);
}

.msg-footer {
  padding: 10px 0;
  text-align: center;
  color: var(--sidebar-bg-active);
  font-size: 14px;
  cursor: pointer;
  transition: background 0.3s;
}

.msg-footer:hover {
  background: var(--hover-bg);
}

.user-dropdown {
  display: flex;
  align-items: center;
  height: 100%;
  padding: 0 12px;
  cursor: pointer;
  transition: background-color 0.3s;
  position: relative;
}

.user-dropdown:hover {
  background-color: var(--hover-bg);
}

.header-text-avatar {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  background-color: var(--sidebar-bg-active);
  color: #ffffff;
  font-size: 14px;
  font-weight: bold;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 8px;
  flex-shrink: 0;
}

.username {
  font-size: 14px;
  margin-right: 4px;
  color: var(--text-color);
}

.dropdown-menu {
  position: absolute;
  top: 48px;
  right: 0;
  background: var(--dropdown-bg);
  min-width: 140px;
  box-shadow: var(--dropdown-shadow);
  border: 1px solid var(--border-color);
  border-radius: 6px;
  padding: 4px 0;
  margin: 0;
  list-style: none;
  z-index: 100;
}

.dropdown-menu li {
  padding: 10px 16px;
  font-size: 14px;
  color: var(--text-color);
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  transition: background 0.3s;
}

.dropdown-menu li:hover {
  background: var(--hover-bg);
  color: var(--sidebar-bg-active);
}

.dropdown-menu .logout-item:hover {
  color: #ff4d4f;
}

.dropdown-divider {
  height: 1px;
  background: var(--border-color);
  margin: 4px 0;
}

.layout-tabs {
  height: var(--tabs-height);
  background: var(--header-bg);
  display: flex;
  align-items: center;
  z-index: 8;
  border-top: 1px solid var(--border-color);
  border-bottom: 1px solid var(--border-color);
  transition: background-color 0.3s, border-color 0.3s;
}

.tabs-scroll-container {
  display: flex;
  align-items: center;
  height: 100%;
  flex: 1;
  overflow-x: auto;
}

.tabs-scroll-container::-webkit-scrollbar {
  display: none;
}

.tab-item {
  height: 100%;
  padding: 0 14px;
  background: transparent;
  border-right: 1px solid var(--border-color);
  display: flex;
  align-items: center;
  gap: 6px;
  cursor: pointer;
  color: var(--text-color-secondary);
  transition: all 0.2s;
  font-size: 13px;
  position: relative;
}

.tab-item:hover {
  color: var(--text-color);
  background-color: var(--hover-bg);
}

.tab-item.active {
  background: var(--content-bg);
  color: var(--sidebar-bg-active);
  font-weight: 500;
}

.tab-item.active::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 2px;
  background-color: var(--sidebar-bg-active);
}

.tab-icon {
  font-size: 15px;
}

.tab-title {
  white-space: nowrap;
  margin-right: 4px;
}

.tab-close-icon {
  font-size: 14px;
  width: 18px;
  height: 18px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  color: var(--text-color-secondary);
  transition: all 0.2s;
}

.tab-close-icon:hover {
  background: rgba(0, 0, 0, 0.08);
  color: var(--text-color);
}

.vben-layout.dark-theme .tab-close-icon:hover {
  background: rgba(255, 255, 255, 0.15);
}

.tabs-actions {
  width: 38px;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  background: var(--header-bg);
  border-left: 1px solid var(--border-color);
  color: var(--text-color-secondary);
  transition: all 0.2s;
}

.tabs-actions:hover {
  color: var(--sidebar-bg-active);
  background-color: var(--hover-bg);
}

.layout-content {
  flex: 1;
  padding: 16px;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
}

.content-wrapper {
  flex: 1;
}

.fade-slide-enter-active,
.fade-slide-leave-active {
  transition: all 0.2s cubic-bezier(0.2, 0, 0, 1);
}

.fade-slide-enter-from {
  opacity: 0;
  transform: translateX(10px);
}

.fade-slide-leave-to {
  opacity: 0;
  transform: translateX(-10px);
}

.fade-slide-right-enter-active,
.fade-slide-right-leave-active {
  transition: all 0.2s cubic-bezier(0.2, 0, 0, 1);
}

.fade-slide-right-enter-from,
.fade-slide-right-leave-to {
  opacity: 0;
  transform: translateX(-10px);
}
</style>