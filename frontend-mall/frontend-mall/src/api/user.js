import request from './request.js'

export const fetchUserProfile = () => {
  return request.get('/api/users/me')
}

export const updateUserProfile = (data) => {
  return request.put('/api/users/me', data)
}

export const changeUserPassword = (data) => {
  return request.put('/api/users/me/password', data)
}

export const fetchUserSummary = () => {
  return request.get('/api/users/me/summary')
}

export const fetchUserFavorites = () => {
  return request.get('/api/users/me/favorites')
}

export const addUserFavorite = (data) => {
  return request.post('/api/users/me/favorites', data)
}

export const removeUserFavorite = (productId) => {
  return request.delete(`/api/users/me/favorites/${productId}`)
}

export const fetchUserFootprints = () => {
  return request.get('/api/users/me/footprints')
}

export const removeUserFootprint = (productId) => {
  return request.delete(`/api/users/me/footprints/${productId}`)
}
