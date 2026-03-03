import { createRouter, createWebHistory } from 'vue-router'
import Layout from '@/views/Layout.vue'

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

// ================= 核心缺失部分找回：全局路由守卫 (鉴权与角色拦截) =================
router.beforeEach((to, from, next) => {
  // 1. 获取登录状态 (实际项目中是从 localStorage 或 Pinia 读取 Token)
  // 这里暂时设为 true 方便我们测试，后续对接后端改为 !!localStorage.getItem('token')
  const isAuthenticated = true 
  
  // 2. 获取当前用户角色 (实际项目中登录后存入缓存)
  // 你可以手动把这里改成 '普通员工' 来测试拦截效果
  const userRole = localStorage.getItem('userRole') || '超级管理员' 

  // 3. 拦截未登录访问
  if (to.path !== '/login' && !isAuthenticated) {
    alert('请先登录系统！')
    return next('/login')
  }

  // 4. 🚀 拦截越权访问（员工不可见人员管理）
  // 检查要去往的页面是否在 meta 里写了 requireSuperAdmin
  if (to.meta.requireSuperAdmin && userRole !== '超级管理员') {
    alert('🛑 权限不足：只有【超级管理员】才能访问人员管理页面！')
    return next(from.path || '/dashboard/analysis') // 将非超管员工一脚踢回首页或上一个页面
  }

  // 5. 正常放行，允许进入页面
  next()
})

export default router