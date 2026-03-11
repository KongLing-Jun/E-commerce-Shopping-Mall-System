import request from './request.js'

// 功能：获取可浏览的分类列表（支持按父级筛选）。
export const getCategories = (params) => {
  return request.get('/api/categories', { params })
}
