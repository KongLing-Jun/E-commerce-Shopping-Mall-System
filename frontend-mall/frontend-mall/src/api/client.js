const API_BASE = import.meta.env.VITE_API_BASE || '/api'

export async function request(path, options = {}) {
  const token = localStorage.getItem('auth_token')
  const headers = {
    'Content-Type': 'application/json',
    ...(token ? { 'X-Auth-Token': token } : {}),
    ...(options.headers || {}),
  }

  let response
  try {
    response = await fetch(`${API_BASE}${path}`, {
      ...options,
      headers,
    })
  } catch (error) {
    throw new Error('网络连接失败')
  }

  const payload = await response.json().catch(() => null)
  if (!response.ok || (payload && payload.success === false)) {
    const message = payload?.message || '请求失败'
    throw new Error(message)
  }

  return payload?.data ?? payload
}
