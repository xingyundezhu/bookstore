/**
 * 评价API模块
 * 提供图书评价相关的接口调用，包括：
 * - 添加评价
 * - 获取评价列表
 * - 评价管理
 */
import request from './request'

/**
 * 评价API对象
 * @namespace reviewApi
 */
export const reviewApi = {
  /**
   * 添加评价
   * @param {Object} data - 评价数据
   * @param {number} data.bookId - 图书ID
   * @param {number} data.orderId - 订单ID（可选）
   * @param {number} data.rating - 评分（1-5）
   * @param {string} data.content - 评价内容
   * @param {boolean} data.isAnonymous - 是否匿名
   * @returns {Promise} 添加结果
   */
  add(data) {
    const params = new URLSearchParams()
    params.append('bookId', data.bookId)
    if (data.orderId) params.append('orderId', data.orderId)
    params.append('rating', data.rating)
    if (data.content) params.append('content', data.content)
    if (data.images) params.append('images', data.images)
    params.append('isAnonymous', data.isAnonymous || false)
    return request.post('/reviews', null, { params })
  },

  /**
   * 添加评价（别名）
   * @param {Object} data - 评价数据
   * @returns {Promise} 添加结果
   */
  create(data) {
    return this.add(data)
  },

  /**
   * 获取图书评价列表
   * @param {number} bookId - 图书ID
   * @param {Object} params - 分页参数
   * @returns {Promise} 评价分页列表
   */
  getByBook(bookId, params) {
    return request.get(`/reviews/book/${bookId}`, { params })
  },

  /**
   * 获取图书评价列表（别名）
   * @param {number} bookId - 图书ID
   * @param {Object} params - 分页参数
   * @returns {Promise} 评价分页列表
   */
  getBookReviews(bookId, params) {
    return this.getByBook(bookId, params)
  },

  /**
   * 获取用户评价列表
   * @param {Object} params - 分页参数
   * @returns {Promise} 评价分页列表
   */
  getUserReviews(params) {
    return request.get('/reviews/user', { params })
  },

  /**
   * 获取所有评价列表（管理员）
   * @param {Object} params - 分页参数
   * @returns {Promise} 评价分页列表
   */
  getAllReviews(params) {
    return request.get('/admin/reviews', { params })
  },

  /**
   * 更新评价
   * @param {number} id - 评价ID
   * @param {Object} data - 更新的评价数据
   * @returns {Promise} 更新结果
   */
  update(id, data) {
    const params = new URLSearchParams()
    if (data.rating) params.append('rating', data.rating)
    if (data.content) params.append('content', data.content)
    if (data.images) params.append('images', data.images)
    return request.put(`/reviews/${id}`, null, { params })
  },

  /**
   * 删除评价
   * @param {number} id - 评价ID
   * @returns {Promise} 删除结果
   */
  delete(id) {
    return request.delete(`/reviews/${id}`)
  },

  /**
   * 管理员删除评价
   * @param {number} id - 评价ID
   * @returns {Promise} 删除结果
   */
  adminDeleteReview(id) {
    return request.delete(`/admin/reviews/${id}`)
  },

  /**
   * 获取图书平均评分
   * @param {number} bookId - 图书ID
   * @returns {Promise} 平均评分
   */
  getAverageRating(bookId) {
    return request.get(`/reviews/book/${bookId}/rating`)
  },

  /**
   * 获取图书评价数量
   * @param {number} bookId - 图书ID
   * @returns {Promise} 评价数量
   */
  getReviewCount(bookId) {
    return request.get(`/reviews/book/${bookId}/count`)
  },

  /**
   * 检查是否已评价
   * @param {number} orderId - 订单ID
   * @returns {Promise} 是否已评价
   */
  checkReviewed(orderId) {
    return request.get('/reviews/check', { params: { orderId } })
  }
}
