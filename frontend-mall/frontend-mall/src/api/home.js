import request from './request.js'

export const getBanners = () => {
    return request.get('/api/home/banners')
}

export const getRecommend = () => {
    return request.get('/api/home/recommend')
}