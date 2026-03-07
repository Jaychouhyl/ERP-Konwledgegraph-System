import request from '@/utils/request'

// 分页获取采购订单列表
export function getPurchaseOrderPage(params) {
  return request({
    url: '/scm/purchase/page',
    method: 'get',
    params: params
  })
}

// 新建采购订单
export function createPurchaseOrder(data) {
  return request({
    url: '/scm/purchase',
    method: 'post',
    data: data
  })
}

// 审核通过采购订单
export function approvePurchaseOrder(orderId) {
  return request({
    url: `/scm/purchase/${orderId}/approve`,
    method: 'put'
  })
}