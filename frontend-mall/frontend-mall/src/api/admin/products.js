import request from '../request.js'

export const fetchAdminProducts = (params) => {
  return request.get('/api/admin/products', { params })
}

export const createAdminProduct = (data) => {
  return request.post('/api/admin/products', data)
}

export const updateAdminProduct = (id, data) => {
  return request.put(`/api/admin/products/${id}`, data)
}

export const enableAdminProduct = (id) => {
  return request.put(`/api/admin/products/${id}/on`)
}

export const disableAdminProduct = (id) => {
  return request.put(`/api/admin/products/${id}/off`)
}

export const deleteAdminProduct = (id) => {
  return request.delete(`/api/admin/products/${id}`)
}
