import { createRouter, createWebHistory } from 'vue-router'
import Layout from '@/views/Layout.vue'
import { ElMessage } from 'element-plus' // 🌟 引入更优雅的提示框

// 💡 施工占位组件（用于暂时没写完的页面，防止白屏报错）
const Placeholder = {
  template: `
    <div style="height: 100%; display: flex; flex-direction: column; align-items: center; justify-content: center; color: #999;">
      <div style="font-size: 64px; margin-bottom: 20px;">🚧</div>
      <h2 style="color: #666; margin-bottom: 10px;">页面正在施工中...</h2>
      <p>前端攻城狮正在日夜赶工，敬请期待！</p>
    </div>
  `
}

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/login',
      name: 'Login',
      component: () => import('@/views/Login.vue')
    },
    {
      path: '/',
      name: 'Layout',
      component: Layout,
      redirect: '/dashboard/analysis', // 默认一进来重定向到分析页
      children: [
        // ================= 概览模块 =================
        {
          path: 'dashboard/analysis',
          name: 'Analysis',
          component: () => import('@/views/dashboard/Analysis.vue')
        },
        {
          path: 'dashboard/workbench',
          name: 'Workbench',
          component: () => import('@/views/dashboard/Workbench.vue')
        },
        
        // ================= 个人中心 =================
        {
          path: 'profile',
          name: 'Profile',
          component: () => import('@/views/profile/Index.vue')
        },

        // ================= 系统管理模块 =================
        {
          path: 'system/user',
          name: 'SystemUser',
          component: () => import('@/views/system/User.vue'),
          meta: { requireSuperAdmin: true } // 👈 核心：标记该页面为“超管专属”
        },
        {
          path: 'system/customer',
          name: 'SystemCustomer',
          component: () => import('@/views/system/Customer.vue')
        },
        {
          path: 'system/supplier',
          name: 'SystemSupplier',
          component: () => import('@/views/system/Supplier.vue')
        },

        // ================= 业务管理模块 =================
        {
          path: 'sales',
          name: 'Sales',
          component: () => import('@/views/sales/Order.vue').catch(() => Placeholder)
        },
        {
          path: 'purchase',
          name: 'Purchase',
          component: () => import('@/views/purchase/Index.vue') // 已经完成的第一战页面
        },
        {
          path: 'inventory',
          name: 'Inventory',
          component: () => import('@/views/inventory/Index.vue') // 接入真实的库存页面
        }
      ]
    }
  ]
})

// ================= 🌟 真实全局路由守卫 (鉴权与角色拦截) =================
router.beforeEach((to, from, next) => {
  // 1. 获取真实登录状态 Token (由 Login.vue 登录成功后存入)
  const token = localStorage.getItem('ekg_token') 
  
  // 2. 获取当前用户角色 (目前先默认超管，后期可以从 token 解析或用户信息接口获取)
  const userRole = localStorage.getItem('userRole') || '超级管理员' 

  // 3. 核心鉴权逻辑分支
  if (to.path === '/login') {
    // 3.1 如果已经有 Token，还想访问登录页 -> 强制踢回首页
    if (token) {
      next('/')
    } else {
      next() // 没 Token，正常看登录页
    }
  } else {
    // 3.2 如果访问的是非登录页 (如 /dashboard 等业务页面)
    if (token) {
      // 🚀 拦截越权访问（员工不可见人员管理）
      if (to.meta.requireSuperAdmin && userRole !== '超级管理员') {
        ElMessage.error('🛑 权限不足：只有【超级管理员】才能访问该页面！')
        return next(from.path || '/dashboard/analysis')
      }
      
      // 正常放行，允许进入页面
      next()
    } else {
      // 没 Token 且访问受保护页面 -> 强制踢回登录页
      ElMessage.warning('请先登录系统！')
      next('/login')
    }
  }
})

export default router