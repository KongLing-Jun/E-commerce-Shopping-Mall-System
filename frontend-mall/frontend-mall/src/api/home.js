import request from './request.js'

// 功能：获取轮播图
export const getBanners = () => {
    return request.get('/api/home/banners')
}

// 功能：获取推荐
export const getRecommend = () => {
    return request.get('/api/home/recommend')
}

// 功能：获取热销商品
export const getHotProducts = () => {
    return request.get('/api/home/hot')
}

// 功能：获取促销商品
export const getPromoProducts = () => {
    return request.get('/api/home/promo')
}
