import request from '../request.js'

export const fetchAdminBanners = (params) => {
  return request.get('/api/admin/banners', { params })
}

export const createAdminBanner = (data) => {
  return request.post('/api/admin/banners', data)
}

export const updateAdminBanner = (id, data) => {
  return request.put(`/api/admin/banners/${id}`, data)
}

export const deleteAdminBanner = (id) => {
  return request.delete(`/api/admin/banners/${id}`)
}
