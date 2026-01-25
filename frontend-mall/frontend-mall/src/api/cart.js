import { request } from './client'

export function fetchCart() {
  return request('/cart')
}

export function addToCart(productId, quantity) {
  return request('/cart', {
    method: 'POST',
    body: JSON.stringify({ productId, quantity }),
  })
}

export function updateCart(productId, quantity) {
  return request('/cart/update', {
    method: 'POST',
    body: JSON.stringify({ productId, quantity }),
  })
}

export function removeFromCart(productId) {
  return request(`/cart?productId=${productId}`, {
    method: 'DELETE',
  })
}
