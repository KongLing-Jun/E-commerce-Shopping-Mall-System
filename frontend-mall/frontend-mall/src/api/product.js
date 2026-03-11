import request from './request.js'

// 功能：处理搜索商品
export const searchProducts = (params) => {
    return request.get('/api/products', { params })
}

// 功能：获取商品
export const getProduct = (productId) => {
    return request.get(`/api/products/${productId}`)
}