<template>
  <div class="workbench-container">
    
    <div class="workbench-header card-base">
      <div class="user-info">
        <div class="admin-avatar">A</div>
        <div class="greet">
          <h2 class="greet-title">早安，Admin，祝你开心每一天！</h2>
          <p class="greet-desc">今日晴，20℃ - 32℃，适合处理积压的审批哦！</p>
        </div>
      </div>
      <div class="user-stats">
        <div class="stat-item">
          <div class="stat-label">待办审批</div>
          <div class="stat-value">12</div>
        </div>
        <div class="stat-divider"></div>
        <div class="stat-item">
          <div class="stat-label">进行中项目</div>
          <div class="stat-value">8</div>
        </div>
      </div>
    </div>

    <div class="workbench-main">
      
      <div class="main-left">
        <div class="card-base">
          <div class="card-title">
            <div class="title-left"><Icon icon="mdi:timeline-text-outline" class="title-icon" /> 最新动态</div>
          </div>
          <div class="dynamic-list">
            <div class="dynamic-item" v-for="item in dynamics" :key="item.id">
              <div class="text-avatar" :style="{ backgroundColor: item.color }">{{ item.avatarText }}</div>
              <div class="dynamic-content">
                <div class="dynamic-text">
                  <span class="name">{{ item.name }}</span> {{ item.action }} <span class="target">{{ item.target }}</span>
                </div>
                <div class="dynamic-time">{{ item.time }}</div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="main-right">
        
        <div class="card-base">
          <div class="card-title">
            <div class="title-left"><Icon icon="mdi:compass-outline" class="title-icon" /> 快捷导航</div>
          </div>
          <div class="quick-nav-grid">
            <div class="nav-item" v-for="(nav, index) in quickNavs" :key="index" @click="go(nav.path)">
              <Icon :icon="nav.icon" class="nav-icon" :style="{ color: nav.color }" />
              <span class="nav-text">{{ nav.title }}</span>
            </div>
          </div>
        </div>

        <div class="card-base todo-card">
          <div class="card-title todo-title">
            <div class="title-left"><Icon icon="mdi:format-list-checkbox" class="title-icon" /> 待办事项</div>
            <div class="add-btn" @click="openTodoModal" title="新增待办">
              <Icon icon="mdi:plus" />
            </div>
          </div>
          
          <div class="todo-list">
            <label class="todo-item" v-for="(todo, index) in todos" :key="index">
              <input type="checkbox" v-model="todo.done" class="todo-checkbox" />
              <span class="todo-text" :class="{ 'is-done': todo.done }">{{ todo.text }}</span>
            </label>
            <div v-if="todos.length === 0" class="empty-state">暂无待办事项，太棒了！</div>
          </div>
        </div>
        
      </div>
    </div>

    <transition name="modal-fade">
      <div class="modal-overlay" v-if="showModal" @click.self="closeTodoModal">
        <div class="modal-content">
          <div class="modal-header">
            <h3>新增待办事项</h3>
            <Icon icon="mdi:close" class="modal-close" @click="closeTodoModal" />
          </div>
          <div class="modal-body">
            <input 
              ref="todoInputRef" 
              type="text" 
              v-model="newTodoText" 
              placeholder="请输入你要处理的事项..." 
              class="modal-input" 
              @keyup.enter="addTodo" 
            />
          </div>
          <div class="modal-footer">
            <button class="btn btn-cancel" @click="closeTodoModal">取消</button>
            <button class="btn btn-primary" @click="addTodo">确定添加</button>
          </div>
        </div>
      </div>
    </transition>

  </div>
</template>

<script setup>
import { ref, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { Icon } from '@iconify/vue'

const router = useRouter()
const go = (path) => router.push(path)

// ========== 1. 最新动态数据 (优化了头像配色与文字) ========== 
const dynamics = ref([
  { id: 1, name: '林主任', avatarText: '林', color: '#1677ff', action: '新建了采购订单', target: 'PO-20260301-001', time: '刚刚' },
  { id: 2, name: '王仓管', avatarText: '王', color: '#52c41a', action: '更新了库存盘点状态', target: 'A区电子元件库', time: '1小时前' },
  { id: 3, name: '李销售', avatarText: '李', color: '#faad14', action: '完成了销售合同归档', target: 'SO-2026-VIP', time: '2小时前' },
  { id: 4, name: '系统管理员', avatarText: '管', color: '#722ed1', action: '修改了系统权限', target: '财务角色组', time: '昨天 14:20' },
  { id: 5, name: '陈经理', avatarText: '陈', color: '#eb2f96', action: '审批驳回了请款单', target: '差旅报销-2026', time: '昨天 09:30' }
])

// ========== 2. 快捷导航数据 ========== 
const quickNavs = ref([
  { title: '分析页', icon: 'mdi:google-analytics', color: '#1677ff', path: '/dashboard/analysis' },
  { title: '销售单', icon: 'mdi:cart-outline', color: '#52c41a', path: '/sales/order' },
  { title: '采购单', icon: 'mdi:shopping-outline', color: '#faad14', path: '/purchase' },
  { title: '库存管理', icon: 'mdi:store-24-hour', color: '#722ed1', path: '/inventory' },
  { title: '个人中心', icon: 'mdi:account-outline', color: '#eb2f96', path: '/profile' },
  { title: '系统设置', icon: 'mdi:cog-outline', color: '#13c2c2', path: '/' }
])

// ========== 3. 待办事项功能逻辑 ========== 
const todos = ref([
  { text: '审核由林主任提交的采购单', done: false },
  { text: '更新本月度财务报表', done: false },
  { text: '参加下午2点的产品需求评审会议', done: true }
])

const showModal = ref(false)
const newTodoText = ref('')
const todoInputRef = ref(null)

const openTodoModal = () => {
  showModal.value = true
  newTodoText.value = ''
  // 弹窗出现后自动聚焦输入框 
  nextTick(() => {
    todoInputRef.value?.focus()
  })
}

const closeTodoModal = () => {
  showModal.value = false
}

const addTodo = () => {
  if (!newTodoText.value.trim()) return 
  // 将新待办加到列表最前面 
  todos.value.unshift({ text: newTodoText.value.trim(), done: false })
  closeTodoModal()
}
</script>

<style scoped>
.workbench-container {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

/* 基础卡片样式 */
.card-base {
  background: var(--content-bg);
  border: 1px solid var(--border-color);
  border-radius: 8px;
  padding: 20px;
  transition: all 0.3s;
}

/* ================= 头部样式 ================= */
.workbench-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.user-info { display: flex; align-items: center; gap: 20px; }
.admin-avatar {
  width: 72px; height: 72px; border-radius: 50%;
  background: #1677ff; color: #fff; font-size: 32px; font-weight: bold;
  display: flex; align-items: center; justify-content: center;
  box-shadow: 0 4px 12px rgba(22, 119, 255, 0.3);
}
.greet-title { margin: 0 0 8px 0; font-size: 20px; font-weight: bold; color: var(--text-color); }
.greet-desc { margin: 0; font-size: 14px; color: var(--text-color-secondary); }

.user-stats { display: flex; align-items: center; gap: 24px; }
.stat-item { text-align: right; }
.stat-label { font-size: 14px; color: var(--text-color-secondary); margin-bottom: 4px; }
.stat-value { font-size: 24px; font-weight: bold; color: var(--text-color); }
.stat-divider { width: 1px; height: 40px; background-color: var(--border-color); }

/* ================= 左右布局 ================= */
.workbench-main {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 16px;
}

/* 统一的卡片标题栏 */
.card-title {
  font-size: 16px; font-weight: bold; color: var(--text-color);
  margin-bottom: 20px; padding-bottom: 12px;
  border-bottom: 1px solid var(--border-color);
  display: flex; justify-content: space-between; align-items: center;
}
.title-left { display: flex; align-items: center; gap: 8px; }
.title-icon { font-size: 20px; color: var(--sidebar-bg-active); }

/* 最新动态列表 */
.dynamic-list { display: flex; flex-direction: column; }
.dynamic-item { display: flex; padding: 16px 0; border-bottom: 1px solid var(--border-color); gap: 16px; align-items: flex-start; }
.dynamic-item:last-child { border-bottom: none; padding-bottom: 0; }
/* Vben风格文字头像 */
.text-avatar {
  width: 40px; height: 40px; border-radius: 50%;
  color: #fff; font-size: 16px; font-weight: 500;
  display: flex; align-items: center; justify-content: center;
  flex-shrink: 0;
}
.dynamic-content { flex: 1; }
.dynamic-text { font-size: 14px; color: var(--text-color); margin-bottom: 6px; line-height: 1.5; }
.dynamic-text .name { color: var(--text-color); font-weight: 500; margin-right: 4px; }
.dynamic-text .target { color: var(--sidebar-bg-active); cursor: pointer; transition: opacity 0.3s; }
.dynamic-text .target:hover { opacity: 0.8; }
.dynamic-time { font-size: 13px; color: var(--text-color-secondary); }

/* 快捷导航 */
.quick-nav-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 12px; }
.nav-item {
  display: flex; flex-direction: column; align-items: center; justify-content: center;
  padding: 16px 0; border-radius: 6px; cursor: pointer; transition: all 0.3s; border: 1px solid transparent;
}
.nav-item:hover { background-color: var(--hover-bg); border-color: var(--border-color); box-shadow: 0 2px 8px rgba(0,0,0,0.04); }
.nav-icon { font-size: 28px; margin-bottom: 8px; }
.nav-text { font-size: 13px; color: var(--text-color); }

/* ================= 待办事项卡片 ================= */
.todo-card { margin-top: 16px; }
.todo-title { margin-bottom: 12px; }
.add-btn {
  width: 28px; height: 28px; border-radius: 4px;
  display: flex; align-items: center; justify-content: center;
  color: var(--sidebar-bg-active); font-size: 20px; cursor: pointer;
  transition: all 0.2s; background: rgba(22, 119, 255, 0.1);
}
.add-btn:hover { background: var(--sidebar-bg-active); color: #fff; }

.todo-list { display: flex; flex-direction: column; gap: 8px; }
.todo-item {
  display: flex; align-items: flex-start; padding: 10px 12px;
  border-radius: 6px; cursor: pointer; transition: all 0.2s;
  background: var(--layout-bg); border: 1px solid transparent;
}
.todo-item:hover { border-color: var(--border-color); }
.todo-checkbox {
  margin-top: 3px; margin-right: 12px; width: 16px; height: 16px;
  cursor: pointer; accent-color: var(--sidebar-bg-active);
}
.todo-text { font-size: 14px; color: var(--text-color); flex: 1; line-height: 1.5; transition: all 0.3s; }
.todo-text.is-done { color: var(--text-color-secondary); text-decoration: line-through; opacity: 0.6; }
.empty-state { text-align: center; padding: 20px; color: var(--text-color-secondary); font-size: 13px; }

/* ================= 弹窗 Modal 样式 ================= */
.modal-overlay {
  position: fixed; top: 0; left: 0; width: 100vw; height: 100vh;
  background: rgba(0, 0, 0, 0.45); backdrop-filter: blur(2px);
  display: flex; align-items: center; justify-content: center;
  z-index: 9999; /* 保证在最顶层 */
}
.modal-content {
  background: var(--content-bg); width: 400px; border-radius: 8px;
  box-shadow: 0 10px 30px rgba(0,0,0,0.1); overflow: hidden;
  border: 1px solid var(--border-color);
}
.modal-header {
  display: flex; justify-content: space-between; align-items: center;
  padding: 16px 24px; border-bottom: 1px solid var(--border-color);
}
.modal-header h3 { margin: 0; font-size: 16px; color: var(--text-color); font-weight: bold; }
.modal-close { font-size: 20px; color: var(--text-color-secondary); cursor: pointer; transition: color 0.2s; }
.modal-close:hover { color: #ff4d4f; }
.modal-body { padding: 24px; }
.modal-input {
  width: 100%; box-sizing: border-box; padding: 10px 14px;
  border: 1px solid var(--border-color); border-radius: 6px;
  background: var(--layout-bg); color: var(--text-color);
  font-size: 14px; outline: none; transition: border-color 0.3s;
}
.modal-input:focus { border-color: var(--sidebar-bg-active); box-shadow: 0 0 0 2px rgba(22, 119, 255, 0.1); }
.modal-footer {
  display: flex; justify-content: flex-end; gap: 12px;
  padding: 12px 24px; border-top: 1px solid var(--border-color);
  background: var(--layout-bg);
}
.btn { padding: 6px 16px; border-radius: 6px; font-size: 14px; cursor: pointer; border: none; transition: all 0.2s; }
.btn-cancel { background: transparent; color: var(--text-color); border: 1px solid var(--border-color); }
.btn-cancel:hover { border-color: var(--sidebar-bg-active); color: var(--sidebar-bg-active); }
.btn-primary { background: var(--sidebar-bg-active); color: #fff; }
.btn-primary:hover { opacity: 0.85; }

/* 弹窗动画 */
.modal-fade-enter-active, .modal-fade-leave-active { transition: all 0.3s cubic-bezier(0.2, 0, 0, 1); }
.modal-fade-enter-from, .modal-fade-leave-to { opacity: 0; transform: scale(0.95); }
</style>