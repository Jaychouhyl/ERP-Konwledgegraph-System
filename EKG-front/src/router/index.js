import { createRouter, createWebHistory } from 'vue-router'
import Layout from '@/views/Layout.vue'

// 💡 绝妙的占位组件：让你点击没写完的菜单时，不再是一片空白！
const Placeholder = {
  template: `
    <div style="height: 100%; display: flex; flex-direction: column; align-items: center; justify-content: center; color: #94a3b8;">
      <el-icon style="font-size: 60px; margin-bottom: 20px; color: #cbd5e1;"><i-lucide-hammer /></el-icon>
      <h2 style="margin: 0 0 10px 0; color: #475569;">页面开发中...</h2>
      <p style="margin: 0;">此微服务模块的前端页面正在火热施工中 🚀</p>
    </div>
  `
}

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/login',
      name: 'login',
      component: () => import('@/views/Login.vue')
    },
    {
      path: '/',
      name: 'layout',
      component: Layout,
      redirect: '/sales/order',
      children: [
        {
          path: 'sales/order',
          name: '销售订单管理',
          component: () => import('@/views/sales/Order.vue')
        },
        // 挂载占位符，这样点击菜单就不会没反应了
        {
          path: 'scm/inventory',
          name: '供应链管理',
          component: Placeholder
        },
        {
          path: 'finance/flow',
          name: '财务中心',
          component: Placeholder
        },
        {
          path: 'rag/decision',
          name: 'AI 决策助手',
          component: Placeholder
        }
      ]
    }
  ]
})

export default router
