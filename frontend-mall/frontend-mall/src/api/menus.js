import request from './request.js'

export const fetchMyMenus = () => {
  return request.get('/api/menus/my')
}

export const fetchMyPerms = () => {
  return request.get('/api/menus/perms')
}
