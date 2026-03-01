<template>
  <div style="padding: 24px;">
    <div class="enter-y" style="animation-delay: 0.1s">
      <el-card shadow="hover" style="margin-bottom: 20px; border-radius: 12px; border: none;">
        <template #header>
          <div style="display: flex; justify-content: space-between; align-items: center;">
            <span style="font-size: 18px; font-weight: bold;">节点状态监控</span>
            <el-tag type="success" effect="dark">Gateway Online</el-tag>
          </div>
        </template>
        <el-alert
          title="分布式链路就绪"
          type="success"
          description="轻量级前端已成功绕过复杂架构，与后端 Spring Cloud Gateway (8080) 建立直连。请狂点下方按钮测试 Sentinel！"
          show-icon
          :closable="false"
        />
      </el-card>
    </div>

    <div class="enter-y" style="animation-delay: 0.2s">
      <el-card shadow="hover" style="border-radius: 12px; border: none; text-align: center; padding: 40px 0;">
        <h2 style="margin-bottom: 10px; color: #333;">高并发熔断模拟器</h2>
        <p style="color: #999; margin-bottom: 30px;">请在 1 秒内连续点击该按钮超过 3 次，触发 Sentinel 全局限流</p>
        
        <el-button
          type="primary"
          size="large"
          style="width: 260px; height: 50px; font-size: 16px; box-shadow: 0 8px 20px rgba(64,158,255,0.3);"
          @click="handleTestCreateOrder"
          :loading="loading"
        >
          模拟创建真实订单
        </el-button>

        <div v-if="responseData" class="enter-y" style="margin-top: 30px; text-align: left; background: #f8f9fa; padding: 20px; border-radius: 8px;">
          <span style="font-weight: bold; color: #666;">网关直连返回:</span>
          <pre style="color: #67c23a; margin-top: 10px; font-family: monospace;">{{ responseData }}</pre>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import request from '@/utils/request' // 引入我们刚才重写的纯净版 request.js

const loading = ref(false)
const responseData = ref(null)

const handleTestCreateOrder = async () => {
  loading.value = true
  responseData.value = null
  try {
    // 纯粹的直连，没有任何多余前缀！
    const res = await request.get('/sales/order/create?productId=1')
    responseData.value = res
  } catch (error) {
    console.error('请求被拦截:', error)
  } finally {
    loading.value = false
  }
}
</script>
