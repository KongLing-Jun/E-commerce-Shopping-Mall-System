import request from '../request.js'

export const fetchAdminOrders = (params) => {
  return request.get('/api/admin/orders', { params })
}

export const shipAdminOrder = (orderNo) => {
  return request.post(`/api/admin/orders/${orderNo}/ship`)
}

export const exportAdminOrders = (params) => {
  return request.get('/api/admin/orders/export', {
    params,
    responseType: 'blob',
  })
}
