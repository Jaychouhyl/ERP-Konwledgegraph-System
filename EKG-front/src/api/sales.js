import request from '@/utils/request'

export function createSalesOrder(data) {
  return request({ url: '/sales/order/create', method: 'post', data })
}
export function pageSalesOrder(pageNum, pageSize, orderNo, customerId) {
  return request({ url: '/sales/order/page', method: 'get', params: { pageNum, pageSize, orderNo, customerId } })
}
export function getSalesDetail(id) {
  return request({ url: `/sales/order/detail/${id}`, method: 'get' })
}