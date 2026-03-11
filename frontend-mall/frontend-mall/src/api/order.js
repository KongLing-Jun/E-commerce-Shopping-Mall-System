import request from './request.js'

// 功能：获取订单预览数据。
export const getOrderPre = () => {
  return request.get('/api/orders/pre')
}

// 功能：创建订单并扣减库存。
export const createOrder = (data) => {
  return request.post('/api/orders', data)
}

// 功能：执行订单支付。
export const payOrder = (orderNo, data) => {
  return request.post(`/api/orders/${orderNo}/pay`, data)
}

// 功能：获取订单详情。
export const getOrderDetail = (orderNo) => {
  return request.get(`/api/orders/${orderNo}`)
}

// 功能：获取订单物流轨迹。
export const getOrderTracking = (orderNo) => {
  return request.get(`/api/orders/${orderNo}/tracking`)
}

// 功能：获取订单分页列表。
export const getOrders = (params) => {
  return request.get('/api/orders', { params })
}

// 功能：确认收货并更新订单状态。
export const confirmOrder = (orderNo) => {
  return request.post(`/api/orders/${orderNo}/confirm`)
}

// 功能：获取订单发票。
export const getOrderInvoice = (orderNo) => {
  return request.get(`/api/orders/${orderNo}/invoice`)
}

// 功能：处理再次购买。
export const rebuyOrder = (orderNo) => {
  return request.post(`/api/orders/${orderNo}/rebuy`)
}
