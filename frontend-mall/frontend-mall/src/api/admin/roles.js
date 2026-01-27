import request from '../request.js'

export const fetchAdminRoles = (params) => {
  return request.get('/api/admin/roles', { params })
}

export const createAdminRole = (data) => {
  return request.post('/api/admin/roles', data)
}

export const updateAdminRole = (id, data) => {
  return request.put(`/api/admin/roles/${id}`, data)
}

export const deleteAdminRole = (id) => {
  return request.delete(`/api/admin/roles/${id}`)
}

export const fetchRoleMenus = (id) => {
  return request.get(`/api/admin/roles/${id}/menus`)
}

export const updateRoleMenus = (id, data) => {
  return request.put(`/api/admin/roles/${id}/menus`, data)
}
