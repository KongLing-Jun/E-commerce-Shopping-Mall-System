import request from '../request.js'

// 功能：获取admin通知
export const fetchAdminNotices = (params) => {
  return request.get('/api/admin/notices', { params })
}

// 功能：处理readadmin通知
export const readAdminNotice = (id) => {
  return request.put(`/api/admin/notices/${id}/read`)
}
