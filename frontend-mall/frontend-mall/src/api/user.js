import request from './request.js'

// 功能：获取用户个人信息
export const fetchUserProfile = () => {
  return request.get('/api/users/me')
}

// 功能：更新用户个人信息
export const updateUserProfile = (data) => {
  return request.put('/api/users/me', data)
}

// 功能：修改用户密码
export const changeUserPassword = (data) => {
  return request.put('/api/users/me/password', data)
}

// 功能：获取用户汇总
export const fetchUserSummary = () => {
  return request.get('/api/users/me/summary')
}

// 功能：获取用户收藏
export const fetchUserFavorites = () => {
  return request.get('/api/users/me/favorites')
}

// 功能：新增用户收藏
export const addUserFavorite = (data) => {
  return request.post('/api/users/me/favorites', data)
}

// 功能：移除用户收藏
export const removeUserFavorite = (productId) => {
  return request.delete(`/api/users/me/favorites/${productId}`)
}

// 功能：获取用户足迹
export const fetchUserFootprints = () => {
  return request.get('/api/users/me/footprints')
}

// 功能：移除用户足迹
export const removeUserFootprint = (productId) => {
  return request.delete(`/api/users/me/footprints/${productId}`)
}
