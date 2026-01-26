import request from './request.js'

export const searchProducts = (params) => {
    return request.get('/api/products', { params })
}

export const getProduct = (productId) => {
    return request.get(`/api/products/${productId}`)
}