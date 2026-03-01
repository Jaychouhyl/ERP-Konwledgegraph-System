import { createApp } from 'vue'
import App from './App.vue'
import router from './router'

import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import 'element-plus/theme-chalk/dark/css-vars.css' // 🌟 新增这行：激活官方暗黑模式

const app = createApp(App)

app.use(router)
app.use(ElementPlus)
app.mount('#app')