import request from '../request.js'

// 功能：获取admin菜单tree
export const fetchAdminMenuTree = () => {
  return request.get('/api/admin/menus/tree')
}

// 功能：创建admin菜单
export const createAdminMenu = (data) => {
  return request.post('/api/admin/menus', data)
}

// 功能：更新admin菜单
export const updateAdminMenu = (id, data) => {
  return request.put(`/api/admin/menus/${id}`, data)
}

// 功能：删除admin菜单
export const deleteAdminMenu = (id) => {
  return request.delete(`/api/admin/menus/${id}`)
}
