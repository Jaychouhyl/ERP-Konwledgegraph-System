import { ref, computed } from 'vue'
import { defineStore } from 'pinia'

/**
 * 🔐 全局用户状态管理
 * 职责：保存登录后的 token 和 userInfo，供全站页面响应式读取
 */
export const useUserStore = defineStore('user', () => {
  // ---- 核心状态 ----
  const userInfo = ref(null)
  const token = ref(localStorage.getItem('ekg_token') || '')

  // ---- 计算属性 ----
  const realName = computed(() => userInfo.value?.realName || '未知用户')
  const username = computed(() => userInfo.value?.username || '')
  const phone = computed(() => userInfo.value?.phone || '')
  const email = computed(() => userInfo.value?.email || '')
  const department = computed(() => userInfo.value?.department || '')
  const avatarLetter = computed(() => {
    const name = userInfo.value?.realName || ''
    return name ? name.charAt(0).toUpperCase() : 'U'
  })
  const isLoggedIn = computed(() => !!token.value)

  // ---- Actions ----
  /**
   * 登录成功后调用：同时保存 token 和 userInfo 到 Pinia + localStorage
   * @param {Object} loginResult - { token: string, userInfo: { id, username, realName, phone, email, avatar, department, status } }
   */
  function setLoginData(loginResult) {
    token.value = loginResult.token
    userInfo.value = loginResult.userInfo
    localStorage.setItem('ekg_token', loginResult.token)
    localStorage.setItem('ekg_userInfo', JSON.stringify(loginResult.userInfo))
  }

  /**
   * 页面刷新时从 localStorage 恢复用户状态（防止 F5 刷新后 Pinia 丢失）
   */
  function restoreFromStorage() {
    const savedToken = localStorage.getItem('ekg_token')
    const savedUserInfo = localStorage.getItem('ekg_userInfo')
    if (savedToken) token.value = savedToken
    if (savedUserInfo) {
      try { userInfo.value = JSON.parse(savedUserInfo) } catch (e) {
        console.warn('[UserStore] Failed to parse ekg_userInfo from localStorage, clearing it.', e)
        localStorage.removeItem('ekg_userInfo')
      }
    }
  }

  /**
   * 退出登录：清空 Pinia 状态和 localStorage
   */
  function logout() {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('ekg_token')
    localStorage.removeItem('ekg_userInfo')
  }

  /**
   * 更新个人信息：同步更新 Pinia 状态和 localStorage
   * @param {Object} profileData - { realName, phone, email, ... }
   */
  function updateUserProfile(profileData) {
    if (userInfo.value) {
      Object.assign(userInfo.value, profileData)
      localStorage.setItem('ekg_userInfo', JSON.stringify(userInfo.value))
    }
  }

  return {
    userInfo, token,
    realName, username, phone, email, department, avatarLetter, isLoggedIn,
    setLoginData, restoreFromStorage, logout, updateUserProfile
  }
})
