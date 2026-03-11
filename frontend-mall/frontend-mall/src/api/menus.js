import request from './request.js'

// 功能：获取我的菜单
export const fetchMyMenus = () => {
  return request.get('/api/menus/my')
}

// 功能：获取我的权限
export const fetchMyPerms = () => {
  return request.get('/api/menus/perms')
}
