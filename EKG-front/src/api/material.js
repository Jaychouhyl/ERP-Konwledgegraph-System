import request from '@/utils/request'

export function pageMaterial(pageNum, pageSize, keyword) {
  return request({ url: '/scm/material/page', method: 'get', params: { pageNum, pageSize, keyword } })
}
export function listMaterials() {
  return request({ url: '/scm/material/list', method: 'get' })
}
export function addMaterial(data) {
  return request({ url: '/scm/material/add', method: 'post', data })
}
export function updateMaterial(data) {
  return request({ url: '/scm/material/update', method: 'put', data })
}
export function deleteMaterial(id) {
  return request({ url: `/scm/material/delete/${id}`, method: 'delete' })
}