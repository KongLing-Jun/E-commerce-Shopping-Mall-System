import request from '../request.js'

// 功能：获取admin用户
export const fetchAdminUsers = (params) => {
  return request.get('/api/admin/users', { params })
}

// 功能：创建admin用户
export const createAdminUser = (data) => {
  return request.post('/api/admin/users', data)
}

// 功能：更新admin用户
export const updateAdminUser = (id, data) => {
  return request.put(`/api/admin/users/${id}`, data)
}

// 功能：禁用admin用户
export const disableAdminUser = (id) => {
  return request.put(`/api/admin/users/${id}/disable`)
}

// 功能：重置admin用户密码
export const resetAdminUserPassword = (id, data) => {
  return request.put(`/api/admin/users/${id}/reset-password`, data)
}

// 功能：更新admin用户角色
export const updateAdminUserRole = (id, data) => {
  return request.put(`/api/admin/users/${id}/role`, data)
}
