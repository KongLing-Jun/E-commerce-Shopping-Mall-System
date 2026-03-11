import request from './request.js'

// 功能：新增购物车明细
export const addCartItem = (data) => {
  return request.post('/api/cart/items', data)
}

// 功能：获取购物车明细
export const getCartItems = () => {
  return request.get('/api/cart/items')
}

// 功能：更新购物车明细
export const updateCartItem = (id, data) => {
  return request.put(`/api/cart/items/${id}`, data)
}

// 功能：删除购物车明细
export const deleteCartItem = (id) => {
  return request.delete(`/api/cart/items/${id}`)
}
