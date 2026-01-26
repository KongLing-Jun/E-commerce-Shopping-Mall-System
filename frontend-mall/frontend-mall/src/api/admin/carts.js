import request from '../request.js'

export const fetchAdminCarts = (params) => {
  return request.get('/api/admin/carts', { params })
}

export const deleteAdminCartItem = (id) => {
  return request.delete(`/api/admin/carts/${id}`)
}
