import request from './request'

// 获取 POI 列表
export function getPoiList(params) {
  return request.get('/pois', { params })
}

// 获取 POI 详情
export function getPoiDetail(id) {
  return request.get(`/pois/${id}`)
}

// 获取区域列表
export function getAreas(city = '大理') {
  return request.get('/pois/areas', { params: { city } })
}

// 获取标签列表
export function getTags() {
  return request.get('/pois/tags')
}
