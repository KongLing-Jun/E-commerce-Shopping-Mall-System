import request from './request.js'

// 功能：注册数据
export const register = (data) => {
    return request.post('/api/auth/register', data)
}

// 功能：登录数据
export const login = (data) => {
    return request.post('/api/auth/login', data)
}

// 功能：退出登录数据
export const logout = () => {
    return request.post('/api/auth/logout')
}

// 功能：处理当前用户
export const me = () => {
    return request.get('/api/auth/me')
}
