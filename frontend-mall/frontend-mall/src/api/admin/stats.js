import request from '../request.js'

// 功能：获取admin统计概览
export const fetchAdminStatsOverview = () => {
  return request.get('/api/admin/stats/overview')
}
