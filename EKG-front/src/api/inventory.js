import request from '@/utils/request'

export function pageInventory(pageNum, pageSize, materialId) {
  return request({ url: '/scm/inventory/page', method: 'get', params: { pageNum, pageSize, materialId } })
}
export function deductInventory(materialId, quantity) {
  return request({ url: '/scm/inventory/deduct', method: 'post', params: { materialId, quantity } })
}