import { request } from './client'

export function fetchProducts(keyword = '') {
  const query = keyword ? `?keyword=${encodeURIComponent(keyword)}` : ''
  return request(`/products${query}`)
}

export function fetchProduct(id) {
  return request(`/products/${id}`)
}
