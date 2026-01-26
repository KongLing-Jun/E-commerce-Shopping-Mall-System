import request from './request.js'

export const getAddresses = () => {
  return request.get('/api/addresses')
}

export const addAddress = (data) => {
  return request.post('/api/addresses', data)
}

export const updateAddress = (id, data) => {
  return request.put(`/api/addresses/${id}`, data)
}

export const deleteAddress = (id) => {
  return request.delete(`/api/addresses/${id}`)
}
