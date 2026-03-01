<template>
  <div class="vben-login-container">
    <div class="theme-switch">
      <el-button circle size="large" @click="toggleTheme" class="theme-btn">
        <el-icon size="18">
          <i-ep-moon v-if="!isDark" />
          <i-ep-sunny v-else />
        </el-icon>
      </el-button>
    </div>

    <div class="login-left">
      <div class="blob shape-1"></div>
      <div class="blob shape-2"></div>

      <svg class="wave-separator" viewBox="0 0 100 100" preserveAspectRatio="none">
        <path d="M100,0 C20,30 30,70 100,100 L100,0 Z" class="wave-soft-1" />
        <path d="M100,0 C50,40 -10,60 100,100 L100,0 Z" class="wave-soft-2" />
      </svg>

      <div class="left-content">
        <div class="logo enter-x-left" style="animation-delay: 0.1s">
          <el-icon class="logo-icon"><i-carbon-machine-learning-model /></el-icon>
          <span class="logo-text">SmartX Engine</span>
        </div>
      </div>
    </div>

    <div class="login-right">
      <div class="glass-form-wrapper">
        <div class="form-header enter-x-right" style="animation-delay: 0.1s">
          <h2>Sign In</h2>
        </div>

        <div class="form-body">
          <div class="premium-input-group enter-x-right" style="animation-delay: 0.2s">
            <el-icon class="input-icon"><i-carbon-user-avatar /></el-icon>
            <input type="text" v-model="username" class="premium-input" placeholder="Username / 账号" />
            <span class="focus-border"></span>
          </div>

          <div class="premium-input-group enter-x-right" style="animation-delay: 0.3s">
            <el-icon class="input-icon"><i-carbon-locked /></el-icon>
            <input type="password" v-model="password" class="premium-input" placeholder="Password / 密码"
              @keyup.enter="handleLogin" />
            <span class="focus-border"></span>
          </div>

          <div class="enter-x-right" style="animation-delay: 0.4s">
            <button class="premium-btn" @click="handleLogin" :disabled="loading">
              <span v-if="!loading">登 录 系 统</span>
              <el-icon v-else class="is-loading"><i-ep-loading /></el-icon>
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = useRouter()
const username = ref('admin')
const password = ref('123456')
const loading = ref(false)

// 🌟 全局持久化主题逻辑
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
  // 页面加载时读取之前的设置
  isDark.value = localStorage.getItem('smartx-theme') === 'dark'
  applyTheme()
})

const toggleTheme = () => {
  isDark.value = !isDark.value
  applyTheme()
}

const handleLogin = () => {
  if (!username.value || !password.value) return ElMessage.warning('请输入凭证')
  loading.value = true
  setTimeout(() => {
    ElMessage.success('登录成功')
    router.push('/')
  }, 600)
}
</script>

<style scoped>
/* 定义左右两侧的自适应颜色变量 */
:root {
  --left-bg-color: #f1f5f9;
  --left-text-color: #0f172a;
}

html.dark {
  --left-bg-color: #020617;
  /* 黑夜模式下极深的蓝黑 */
  --left-text-color: #ffffff;
}

.vben-login-container {
  display: flex;
  height: 100vh;
  background-color: var(--el-bg-color);
  color: var(--el-text-color-primary);
  overflow: hidden;
  transition: background-color 0.4s ease;
  font-family: 'Inter', sans-serif;
}

.theme-switch {
  position: absolute;
  top: 30px;
  right: 40px;
  z-index: 100;
}

.theme-btn {
  background-color: var(--el-bg-color-overlay);
  border: 1px solid var(--el-border-color);
  color: var(--el-text-color-primary);
  transition: all 0.3s;
}

.theme-btn:hover {
  transform: rotate(30deg);
}

.login-left {
  position: relative;
  flex: 1.1;
  background-color: var(--left-bg-color);
  color: var(--left-text-color);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 10;
  transition: background-color 0.4s ease, color 0.4s ease;
}

/* 🌟 极致柔和的波浪分界线 */
.wave-separator {
  position: absolute;
  right: 0;
  top: 0;
  width: 150px;
  height: 100%;
  z-index: 20;
  filter: blur(8px);
  /* 高斯模糊让边缘彻底柔和 */
  transform: scaleY(1.1);
  /* 轻微拉伸消除顶部截断感 */
}

.wave-soft-1 {
  fill: var(--el-bg-color);
  opacity: 0.4;
  transition: fill 0.4s ease;
}

.wave-soft-2 {
  fill: var(--el-bg-color);
  opacity: 0.9;
  transition: fill 0.4s ease;
}

.blob {
  position: absolute;
  filter: blur(90px);
  z-index: 1;
  opacity: 0.4;
  animation: float 10s infinite ease-in-out alternate;
}

.shape-1 {
  width: 500px;
  height: 500px;
  background: linear-gradient(135deg, #3b82f6, #8b5cf6);
  top: -100px;
  left: -100px;
  border-radius: 40%;
}

.shape-2 {
  width: 400px;
  height: 400px;
  background: linear-gradient(135deg, #10b981, #3b82f6);
  bottom: -50px;
  right: 100px;
  border-radius: 60%;
  animation-delay: -5s;
}

@keyframes float {
  0% {
    transform: translateY(0) scale(1) rotate(0deg);
  }

  100% {
    transform: translateY(-40px) scale(1.1) rotate(20deg);
  }
}

.left-content {
  position: relative;
  z-index: 30;
}

.logo {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
}

.logo-icon {
  font-size: 80px;
  margin-bottom: 20px;
  filter: drop-shadow(0 0 20px rgba(59, 130, 246, 0.4));
  color: #3b82f6;
  transition: color 0.4s;
}

html.dark .logo-icon {
  color: #ffffff;
}

.logo-text {
  font-size: 38px;
  font-weight: 700;
  letter-spacing: 4px;
}

.login-right {
  flex: 0.9;
  display: flex;
  align-items: center;
  justify-content: center;
  padding-left: 20px;
}

.glass-form-wrapper {
  width: 100%;
  max-width: 400px;
  padding: 40px;
  background: var(--el-bg-color-overlay);
  border: 1px solid var(--el-border-color-light);
  border-radius: 20px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.05);
  transition: all 0.4s ease;
}

.form-header h2 {
  font-size: 32px;
  font-weight: 700;
  margin-bottom: 40px;
  letter-spacing: 1px;
  color: var(--el-text-color-primary);
}

.premium-input-group {
  position: relative;
  display: flex;
  align-items: center;
  margin-bottom: 35px;
}

.input-icon {
  position: absolute;
  left: 0;
  color: var(--el-text-color-placeholder);
  font-size: 20px;
  transition: color 0.3s ease;
}

.premium-input {
  width: 100%;
  background: transparent;
  border: none;
  border-bottom: 1px solid var(--el-border-color);
  padding: 10px 10px 10px 35px;
  color: var(--el-text-color-primary);
  font-size: 15px;
  letter-spacing: 1px;
  outline: none;
  transition: all 0.3s ease;
}

.premium-input::placeholder {
  color: var(--el-text-color-placeholder);
  transition: all 0.3s ease;
}

.focus-border {
  position: absolute;
  bottom: 0;
  left: 50%;
  width: 0;
  height: 2px;
  background-color: #3b82f6;
  transition: all 0.4s cubic-bezier(0.25, 0.8, 0.25, 1);
  transform: translateX(-50%);
}

.premium-input:focus~.focus-border {
  width: 100%;
}

.premium-input:focus {
  border-bottom-color: transparent;
}

.premium-input:focus~.input-icon {
  color: #3b82f6;
}

.premium-input:focus::placeholder {
  opacity: 0;
  transform: translateX(10px);
}

.premium-btn {
  width: 100%;
  height: 50px;
  border-radius: 12px;
  background: linear-gradient(135deg, #2563eb 0%, #3b82f6 100%);
  color: #ffffff;
  font-size: 15px;
  font-weight: 600;
  letter-spacing: 2px;
  border: none;
  cursor: pointer;
  box-shadow: 0 4px 15px rgba(59, 130, 246, 0.3);
  transition: all 0.3s ease;
}

.premium-btn:hover {
  background: linear-gradient(135deg, #1d4ed8 0%, #2563eb 100%);
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(59, 130, 246, 0.4);
}

.enter-x-left {
  opacity: 0;
  animation: enter-x-left-anim 1s cubic-bezier(0.2, 0.8, 0.2, 1) forwards;
}

@keyframes enter-x-left-anim {
  0% {
    opacity: 0;
    transform: translateX(-60px) scale(0.95);
  }

  100% {
    opacity: 1;
    transform: translateX(0) scale(1);
  }
}

.enter-x-right {
  opacity: 0;
  animation: enter-x-right-anim 1s cubic-bezier(0.2, 0.8, 0.2, 1) forwards;
}

@keyframes enter-x-right-anim {
  0% {
    opacity: 0;
    transform: translateX(60px);
  }

  100% {
    opacity: 1;
    transform: translateX(0);
  }
}
</style>