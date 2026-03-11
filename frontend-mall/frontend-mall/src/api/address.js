import request from './request.js'

// 功能：获取地址
export const getAddresses = () => {
  return request.get('/api/addresses')
}

// 功能：新增地址
export const addAddress = (data) => {
  return request.post('/api/addresses', data)
}

// 功能：更新地址
export const updateAddress = (id, data) => {
  return request.put(`/api/addresses/${id}`, data)
}

// 功能：删除地址
export const deleteAddress = (id) => {
  return request.delete(`/api/addresses/${id}`)
}
