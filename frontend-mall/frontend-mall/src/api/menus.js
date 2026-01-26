import request from './request.js'

export const fetchMyMenus = () => {
  return request.get('/api/menus/my')
}
