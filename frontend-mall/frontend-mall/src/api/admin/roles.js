import request from '../request.js'

// 功能：获取admin角色
export const fetchAdminRoles = (params) => {
  return request.get('/api/admin/roles', { params })
}

// 功能：创建admin角色
export const createAdminRole = (data) => {
  return request.post('/api/admin/roles', data)
}

// 功能：更新admin角色
export const updateAdminRole = (id, data) => {
  return request.put(`/api/admin/roles/${id}`, data)
}

// 功能：删除admin角色
export const deleteAdminRole = (id) => {
  return request.delete(`/api/admin/roles/${id}`)
}

// 功能：获取角色菜单
export const fetchRoleMenus = (id) => {
  return request.get(`/api/admin/roles/${id}/menus`)
}

// 功能：更新角色菜单
export const updateRoleMenus = (id, data) => {
  return request.put(`/api/admin/roles/${id}/menus`, data)
}
