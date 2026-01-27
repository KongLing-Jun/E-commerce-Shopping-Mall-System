import request from '../request.js'

export const fetchAdminStatsOverview = () => {
  return request.get('/api/admin/stats/overview')
}
