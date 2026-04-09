/**
 * 收藏API模块
 * 提供图书收藏相关的接口调用，包括：
 * - 添加/取消收藏
 * - 获取收藏列表
 * - 检查收藏状态
 * 
 */
import request from './request'

/**
 * 收藏API对象
 * @namespace favoriteApi
 */
export const favoriteApi = {
  /**
   * 添加收藏
   * @param {number} bookId - 图书ID
   * @returns {Promise} 添加结果
   */
  add(bookId) {
    return request.post(`/favorites/${bookId}`)
  },

  /**
   * 取消收藏
   * @param {number} bookId - 图书ID
   * @returns {Promise} 取消结果
   */
  remove(bookId) {
    return request.delete(`/favorites/${bookId}`)
  },

  /**
   * 获取收藏列表
   * @param {Object} params - 分页参数
   * @returns {Promise} 收藏的图书分页列表
   */
  getList(params) {
    return request.get('/favorites', { params })
  },

  /**
   * 检查是否已收藏
   * @param {number} bookId - 图书ID
   * @returns {Promise<boolean>} 是否已收藏
   */
  check(bookId) {
    return request.get(`/favorites/check/${bookId}`)
  },

  /**
   * 获取收藏数量
   * @returns {Promise<number>} 收藏数量
   */
  getCount() {
    return request.get('/favorites/count')
  }
}
