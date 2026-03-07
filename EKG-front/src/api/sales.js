import request from '@/utils/request'

// 获取销售订单分页
export function getSalesOrderPage(params) {
  return request({
    url: '/sales/order/page',
    method: 'get',
    params: params
  })
}

// 创建销售订单
export function createSalesOrder(data) {
  return request({
    url: '/sales/order',
    method: 'post',
    data: data
  })
}