import request from '../request.js'

export const fetchAdminMenuTree = () => {
  return request.get('/api/admin/menus/tree')
}

export const createAdminMenu = (data) => {
  return request.post('/api/admin/menus', data)
}

export const updateAdminMenu = (id, data) => {
  return request.put(`/api/admin/menus/${id}`, data)
}

export const deleteAdminMenu = (id) => {
  return request.delete(`/api/admin/menus/${id}`)
}
