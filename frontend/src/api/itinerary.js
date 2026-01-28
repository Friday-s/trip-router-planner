import request from './request'

// 生成行程
export function generateItinerary(data) {
  return request.post('/itineraries/generate', data)
}

// 获取行程详情
export function getItineraryDetail(id) {
  return request.get(`/itineraries/${id}`)
}

// 保存行程
export function saveItinerary(id, data) {
  return request.post(`/itineraries/${id}/save`, data)
}

// 获取我的行程列表
export function getMyItineraries(params) {
  return request.get('/itineraries/my', { params })
}

// 删除行程
export function deleteItinerary(id) {
  return request.delete(`/itineraries/${id}`)
}
