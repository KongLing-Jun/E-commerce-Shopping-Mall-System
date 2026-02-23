import request from './request.js'

export const getBanners = () => {
    return request.get('/api/home/banners')
}

export const getRecommend = () => {
    return request.get('/api/home/recommend')
}

export const getHotProducts = () => {
    return request.get('/api/home/hot')
}

export const getPromoProducts = () => {
    return request.get('/api/home/promo')
}
