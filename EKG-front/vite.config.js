import { fileURLToPath, URL } from 'node:url'
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// 引入图标自动导入的黑魔法插件
import Icons from 'unplugin-icons/vite'
import IconsResolver from 'unplugin-icons/resolver'
import Components from 'unplugin-vue-components/vite'

export default defineConfig({
  plugins: [
    vue(),
    // 自动注册组件，遇到 i- 开头的标签会自动去解析成图标
    Components({
      resolvers: [
        IconsResolver({
          prefix: 'i', // 设定组件前缀为 i，例如 <i-ep-user />
        }),
      ],
    }),
    // 自动安装缺少的图标集！
    Icons({
      autoInstall: true,
    }),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  },
  server: {
    port: 5173,
    // 🌟 配置跨域代理，对接你的 Spring Cloud Gateway
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})
