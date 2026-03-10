import request from './request'

export const getHello = () => request.get('/api/hello')
