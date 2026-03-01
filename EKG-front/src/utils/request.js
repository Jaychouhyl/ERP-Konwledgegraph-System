import axios from 'axios'
import { ElMessage, ElNotification } from 'element-plus'

const request = axios.create({
  // 🌟 核心：直接写死网关真实地址，抛弃 Vite 代理黑盒！
  baseURL: 'http://localhost:8080',
  timeout: 10000
})

// 🌟 Vben 级别的防抖锁 (防止弹窗轰炸)
let isErrorShowing = false;
const showError = (msg, isSentinel = false) => {
  if (isErrorShowing) return;
  isErrorShowing = true;
  if (isSentinel) {
    ElNotification({ title: '🚨 系统熔断限流', message: msg, type: 'error', duration: 3500 })
  } else {
    ElMessage({ message: msg, type: 'error', duration: 3000 })
  }
  setTimeout(() => { isErrorShowing = false; }, 3000);
}

request.interceptors.response.use(res => {
  // 拦截被网关包装成 200 的 Sentinel 拦截
  if (typeof res.data === 'string' && res.data.includes('Blocked by Sentinel')) {
    showError('当前系统并发压力过大，请求已被微服务网关自动拦截！', true)
    return Promise.reject(new Error('Sentinel Flow Control'))
  }
  // Vben 标准规范：code 为 0 代表成功
  if (res.data && res.data.code === 429) {
    showError(res.data.msg || '触发微服务网关限流保护', true)
    return Promise.reject(new Error('Sentinel Flow Control'))
  }
  return res.data
}, error => {
  const status = error.response ? error.response.status : null;
  // 精准捕获 HTTP 429 状态码
  if (status === 429) {
    showError('您的手速太快或后端报错频繁，已触发 Sentinel 微服务保护！', true)
  } else {
    showError(error.response?.data?.message || error.message || '网络请求失败', false)
  }
  return Promise.reject(error)
})

export default request
