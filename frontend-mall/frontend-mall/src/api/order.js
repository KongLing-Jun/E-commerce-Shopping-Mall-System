import request from './request.js'

export const getOrderPre = () => {
  return request.get('/api/orders/pre')
}

export const createOrder = (data) => {
  return request.post('/api/orders', data)
}

export const payOrder = (orderNo, data) => {
  return request.post(`/api/orders/${orderNo}/pay`, data)
}

export const getOrderDetail = (orderNo) => {
  return request.get(`/api/orders/${orderNo}`)
}

export const getOrders = (params) => {
  return request.get('/api/orders', { params })
}

export const confirmOrder = (orderNo) => {
  return request.post(`/api/orders/${orderNo}/confirm`)
}

export const getOrderInvoice = (orderNo) => {
  return request.get(`/api/orders/${orderNo}/invoice`)
}

export const rebuyOrder = (orderNo) => {
  return request.post(`/api/orders/${orderNo}/rebuy`)
}
