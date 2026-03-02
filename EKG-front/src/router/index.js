import { createRouter, createWebHistory } from 'vue-router'
import Layout from '@/views/Layout.vue'

// 💡 绝妙的占位组件：让你点击没写完的菜单时，不再是一片空白！
const Placeholder = {
  template: `
    <div style="height: 100%; display: flex; flex-direction: column; align-items: center; justify-content: center; color: #888;">
      <div style="font-size: 48px; margin-bottom: 16px;">🚧</div>
      <h2 style="margin: 0 0 8px; color: var(--text-color, #333);">该业务模块正在施工开发中...</h2>
      <p style="margin: 0; font-size: 14px;">（你可以先去“分析页”、“工作台”或“系统管理”看看）</p>
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
      redirect: '/dashboard/analysis', // 默认进入概览分析页
      children: [
        // ================= 1. 概览模块 (已完成) =================
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

        // ================= 2. 个人中心 (已完成) =================
        {
          path: 'profile',
          name: 'Profile',
          component: () => import('@/views/profile/Index.vue')
        },

        // ================= 3. 系统管理模块 (已完成) =================
        {
          path: 'system/user',
          name: 'SystemUser',
          component: () => import('@/views/system/User.vue')
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

        // ================= 4. 业务模块 (开发中，使用占位符兜底) =================
        {
          // 销售模块：假设你写了 Order.vue 就尝试加载，没写就用占位符兜底
          path: 'sales',
          name: 'Sales',
          component: () => import('@/views/sales/Order.vue').catch(() => Placeholder)
        },
        {
          // 采购模块：暂时使用占位符
          path: 'purchase',
          name: 'Purchase',
          component: Placeholder
        },
        {
          // 库存模块：暂时使用占位符
          path: 'inventory',
          name: 'Inventory',
          component: Placeholder
        }
      ]
    },
    // 处理未匹配路由，统一跳转首页或 404
    {
      path: '/:pathMatch(.*)*',
      redirect: '/'
    }
  ]
})

export default router