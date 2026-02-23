import request from '../request.js'

export const fetchAdminNotices = (params) => {
  return request.get('/api/admin/notices', { params })
}

export const readAdminNotice = (id) => {
  return request.put(`/api/admin/notices/${id}/read`)
}
