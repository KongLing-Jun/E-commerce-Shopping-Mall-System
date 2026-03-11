import request from '../request.js'

// 功能：获取admin商品
export const fetchAdminProducts = (params) => {
  return request.get('/api/admin/products', { params })
}

// 功能：创建admin商品
export const createAdminProduct = (data) => {
  return request.post('/api/admin/products', data)
}

// 功能：更新admin商品
export const updateAdminProduct = (id, data) => {
  return request.put(`/api/admin/products/${id}`, data)
}

// 功能：启用admin商品
export const enableAdminProduct = (id) => {
  return request.put(`/api/admin/products/${id}/on`)
}

// 功能：禁用admin商品
export const disableAdminProduct = (id) => {
  return request.put(`/api/admin/products/${id}/off`)
}

// 功能：删除admin商品
export const deleteAdminProduct = (id) => {
  return request.delete(`/api/admin/products/${id}`)
}
