import request from '../request.js'

// 功能：获取admin分类
export const fetchAdminCategories = (params) => {
  return request.get('/api/admin/categories', { params })
}

// 功能：创建admin分类
export const createAdminCategory = (data) => {
  return request.post('/api/admin/categories', data)
}

// 功能：更新admin分类
export const updateAdminCategory = (id, data) => {
  return request.put(`/api/admin/categories/${id}`, data)
}

// 功能：启用admin分类
export const enableAdminCategory = (id) => {
  return request.put(`/api/admin/categories/${id}/on`)
}

// 功能：禁用admin分类
export const disableAdminCategory = (id) => {
  return request.put(`/api/admin/categories/${id}/off`)
}

// 功能：删除admin分类
export const deleteAdminCategory = (id) => {
  return request.delete(`/api/admin/categories/${id}`)
}
