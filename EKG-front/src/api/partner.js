import request from '@/utils/request'

// ==================== 供应商 ====================
export function pageSupplier(pageNum, pageSize, keyword) {
  return request({ url: '/auth/partner/supplier/page', method: 'get', params: { pageNum, pageSize, keyword } })
}
export function listSuppliers() {
  return request({ url: '/auth/partner/supplier/list', method: 'get' })
}
export function getSupplierById(id) {
  return request({ url: `/auth/partner/supplier/${id}`, method: 'get' })
}
export function addSupplier(data) {
  return request({ url: '/auth/partner/supplier/add', method: 'post', data })
}
export function updateSupplier(data) {
  return request({ url: '/auth/partner/supplier/update', method: 'put', data })
}
export function deleteSupplier(id) {
  return request({ url: `/auth/partner/supplier/delete/${id}`, method: 'delete' })
}

// ==================== 客户 ====================
export function pageCustomer(pageNum, pageSize, keyword) {
  return request({ url: '/auth/partner/customer/page', method: 'get', params: { pageNum, pageSize, keyword } })
}
export function listCustomers() {
  return request({ url: '/auth/partner/customer/list', method: 'get' })
}
export function getCustomerById(id) {
  return request({ url: `/auth/partner/customer/${id}`, method: 'get' })
}
export function addCustomer(data) {
  return request({ url: '/auth/partner/customer/add', method: 'post', data })
}
export function updateCustomer(data) {
  return request({ url: '/auth/partner/customer/update', method: 'put', data })
}
export function deleteCustomer(id) {
  return request({ url: `/auth/partner/customer/delete/${id}`, method: 'delete' })
}