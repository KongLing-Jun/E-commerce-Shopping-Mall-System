import request from '../request.js'

// 功能：获取admin购物车
export const fetchAdminCarts = (params) => {
  return request.get('/api/admin/carts', { params })
}

// 功能：删除admin购物车明细
export const deleteAdminCartItem = (id) => {
  return request.delete(`/api/admin/carts/${id}`)
}
