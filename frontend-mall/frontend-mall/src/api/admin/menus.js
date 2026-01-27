import request from '../request.js'

export const fetchAdminMenuTree = () => {
  return request.get('/api/admin/menus/tree')
}
