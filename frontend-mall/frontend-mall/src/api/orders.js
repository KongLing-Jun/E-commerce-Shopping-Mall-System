import { request } from './client'

export function createOrder(address) {
  return request('/orders', {
    method: 'POST',
    body: JSON.stringify({ address }),
  })
}

export function fetchOrders() {
  return request('/orders')
}

export function updateOrderStatus(orderId, status) {
  return request(`/orders/${orderId}/status`, {
    method: 'POST',
    body: JSON.stringify({ status }),
  })
}
