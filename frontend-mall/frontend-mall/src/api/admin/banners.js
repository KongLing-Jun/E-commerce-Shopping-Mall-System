import request from '../request.js'

// 功能：获取admin轮播图
export const fetchAdminBanners = (params) => {
  return request.get('/api/admin/banners', { params })
}

// 功能：创建admin轮播图
export const createAdminBanner = (data) => {
  return request.post('/api/admin/banners', data)
}

// 功能：更新admin轮播图
export const updateAdminBanner = (id, data) => {
  return request.put(`/api/admin/banners/${id}`, data)
}

// 功能：删除admin轮播图
export const deleteAdminBanner = (id) => {
  return request.delete(`/api/admin/banners/${id}`)
}
