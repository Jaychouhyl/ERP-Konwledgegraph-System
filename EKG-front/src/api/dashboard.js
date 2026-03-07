import request from '@/utils/request'

/** 财务大盘聚合数据 */
export function getDashboardStats() {
  return request({ url: '/finance/flow/dashboard/stats', method: 'get' })
}

/** 库存预警列表（低于安全库存的） */
export function getInventoryWarnings(pageNum = 1, pageSize = 10) {
  return request({ url: '/scm/inventory/page', method: 'get', params: { pageNum, pageSize } })
}