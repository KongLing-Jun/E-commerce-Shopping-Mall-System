import request from '../request.js'

export const fetchAdminCategories = (params) => {
  return request.get('/api/admin/categories', { params })
}

export const createAdminCategory = (data) => {
  return request.post('/api/admin/categories', data)
}

export const updateAdminCategory = (id, data) => {
  return request.put(`/api/admin/categories/${id}`, data)
}

export const enableAdminCategory = (id) => {
  return request.put(`/api/admin/categories/${id}/on`)
}

export const disableAdminCategory = (id) => {
  return request.put(`/api/admin/categories/${id}/off`)
}

export const deleteAdminCategory = (id) => {
  return request.delete(`/api/admin/categories/${id}`)
}
