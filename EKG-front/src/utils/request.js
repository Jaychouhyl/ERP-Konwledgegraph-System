import axios from 'axios'
import { ElMessage, ElMessageBox, ElNotification } from 'element-plus'
import router from '@/router'
// 如果你后面引入了 Pinia 管理 User 状态，可以在这里引入
// import { useUserStore } from '@/stores/user'

// 1. 创建 Axios 实例
const request = axios.create({
  // 🌟 核心：指向我们的 Spring Cloud Gateway 网关真实地址
  // 如果你本地测试网关是 8080 端口，请确保匹配
  baseURL: 'http://localhost:8080', 
  timeout: 10000, // 请求超时时间 10s
  headers: {
    'Content-Type': 'application/json;charset=utf-8'
  }
})

// 2. 请求拦截器 (Request Interceptor)
request.interceptors.request.use(
  config => {
    // 每次发送请求之前，检查本地是否存有 Token
    const token = localStorage.getItem('ekg_token')
    if (token) {
      // 规范：后端网关 AuthGlobalFilter 通常检查 Authorization 头的 Bearer Token
      config.headers['Authorization'] = 'Bearer ' + token
    }
    return config
  },
  error => {
    console.error('请求异常:', error)
    return Promise.reject(error)
  }
)

// 3. 响应拦截器 (Response Interceptor)
request.interceptors.response.use(
  res => {
    // 如果返回的不是 JSON，比如文件下载，直接返回
    if (res.config.responseType === 'blob' || res.config.responseType === 'arraybuffer') {
      return res.data
    }

    // 后端统一封装的 Result 对象结构： { code: 200, msg: "成功", data: ... }
    const { code, msg, data } = res.data

    // 200 表示业务成功，直接脱壳返回 data
    if (code === 200 || code === 0) {
      return data
    }
    // 401 认证失败 / Token 过期
    else if (code === 401) {
      ElMessageBox.confirm('登录状态已过期，您可以继续留在该页面，或者重新登录', '系统提示', {
        confirmButtonText: '重新登录',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        localStorage.removeItem('ekg_token')
        // 跳转登录页，并携带当前想去的页面路径，方便登录后跳回
        router.push({ path: '/login', query: { redirect: router.currentRoute.value.fullPath } })
      }).catch(() => {})
      return Promise.reject(new Error(msg || '认证失败'))
    } 
    // 403 权限不足
    else if (code === 403) {
      ElMessage.error(msg || '您没有操作权限')
      return Promise.reject(new Error(msg || '权限不足'))
    } 
    // 其他业务错误
    else {
      ElMessage.error(msg || '系统未知错误')
      return Promise.reject(new Error(msg || '系统错误'))
    }
  },
  error => {
    // 处理 HTTP 状态码错误 (比如 500 服务器崩溃, 404 找不到接口, 503 网关熔断等)
    console.error('响应异常:', error)
    let message = error.message
    if (message.includes('Network Error')) {
      message = '后端接口连接异常，请检查网关是否启动'
    } else if (message.includes('timeout')) {
      message = '系统接口请求超时'
    } else if (error.response && error.response.status === 500) {
      message = '服务器内部错误 (500)'
    }
    
    ElNotification({
      title: '请求失败',
      message: message,
      type: 'error',
      duration: 5000
    })
    
    return Promise.reject(error)
  }
)

export default request