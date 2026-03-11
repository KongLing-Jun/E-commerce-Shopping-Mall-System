import request from '../request.js'

// 功能：获取后台订单列表。
export const fetchAdminOrders = (params) => {
  return request.get('/api/admin/orders', { params })
}

// 功能：发货后台订单。
export const shipAdminOrder = (orderNo, payload = {}) => {
  return request.post(`/api/admin/orders/${orderNo}/ship`, payload)
}

// 功能：新增订单物流轨迹。
export const addAdminOrderTracking = (orderNo, payload = {}) => {
  return request.post(`/api/admin/orders/${orderNo}/tracking`, payload)
}

// 功能：导出后台订单。
export const exportAdminOrders = (params) => {
  return request.get('/api/admin/orders/export', {
    params,
    responseType: 'blob',
  })
}
