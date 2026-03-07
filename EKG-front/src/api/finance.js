import request from '@/utils/request'

// 获取现金流流水列表（分页）
export function getCashFlowPage(params) {
  return request({
    url: '/finance/flow/page',
    method: 'get',
    params: params
  })
}

// 获取财务统计汇总数据（专供 Dashboard 分析页图表使用）
export function getFinanceStatistics(params) {
  return request({
    url: '/finance/flow/statistics',
    method: 'get',
    params: params
  })
}

// 新增一笔流水（采购付款、销售收款等）
export function createCashFlow(data) {
  return request({
    url: '/finance/flow',
    method: 'post',
    data: data
  })
}