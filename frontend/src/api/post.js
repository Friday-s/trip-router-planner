import request from './request'

// 获取帖子列表
export function getPostList(params) {
  return request.get('/posts', { params })
}

// 获取帖子详情
export function getPostDetail(id) {
  return request.get(`/posts/${id}`)
}

// 发布帖子
export function createPost(data) {
  return request.post('/posts', data)
}

// 点赞
export function likePost(id) {
  return request.put(`/posts/${id}/like`)
}

// 取消点赞
export function unlikePost(id) {
  return request.delete(`/posts/${id}/like`)
}

// 收藏
export function favoritePost(id) {
  return request.put(`/posts/${id}/favorite`)
}

// 取消收藏
export function unfavoritePost(id) {
  return request.delete(`/posts/${id}/favorite`)
}

// 获取我的帖子
export function getMyPosts(params) {
  return request.get('/posts/my', { params })
}

// 获取我的收藏
export function getMyFavorites(params) {
  return request.get('/posts/favorites', { params })
}
