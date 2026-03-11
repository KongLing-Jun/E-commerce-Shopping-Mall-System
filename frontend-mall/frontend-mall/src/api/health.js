import request from './request'

// 功能：健康检查
export const getHello = () => request.get('/api/hello')
