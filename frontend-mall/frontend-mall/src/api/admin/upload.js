import request from '../request.js'

// 功能：上传adminfile
export const uploadAdminFile = (file) => {
  const formData = new FormData()
  formData.append('file', file)
  return request.post('/api/admin/upload', formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  })
}
