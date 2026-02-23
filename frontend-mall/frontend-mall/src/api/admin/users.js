import request from '../request.js'

export const fetchAdminUsers = (params) => {
  return request.get('/api/admin/users', { params })
}

export const createAdminUser = (data) => {
  return request.post('/api/admin/users', data)
}

export const updateAdminUser = (id, data) => {
  return request.put(`/api/admin/users/${id}`, data)
}

export const disableAdminUser = (id) => {
  return request.put(`/api/admin/users/${id}/disable`)
}

export const resetAdminUserPassword = (id, data) => {
  return request.put(`/api/admin/users/${id}/reset-password`, data)
}

export const updateAdminUserRole = (id, data) => {
  return request.put(`/api/admin/users/${id}/role`, data)
}
