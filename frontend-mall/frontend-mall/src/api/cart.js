import request from './request.js'

export const addCartItem = (data) => {
  return request.post('/api/cart/items', data)
}

export const getCartItems = () => {
  return request.get('/api/cart/items')
}

export const updateCartItem = (id, data) => {
  return request.put(`/api/cart/items/${id}`, data)
}

export const deleteCartItem = (id) => {
  return request.delete(`/api/cart/items/${id}`)
}
