/**
 * 推荐API模块
 * 提供个性化推荐相关的接口调用，包括：
 * - 获取个性化推荐
 * - 记录用户行为
 */
import request from './request'

/**
 * 推荐API对象
 * @namespace recommendationApi
 */
export const recommendationApi = {
  /**
   * 获取个性化推荐图书
   * @param {number} limit - 获取数量
   * @returns {Promise<Array>} 推荐图书列表
   */
  getPersonalized(limit = 10) {
    return request.get('/recommendations/personalized', { params: { limit } })
  },

  /**
   * 记录浏览行为
   * 用于推荐算法分析用户兴趣。
   * @param {number} bookId - 图书ID
   * @returns {Promise} 记录结果
   */
  viewBook(bookId) {
    return request.post(`/recommendations/view/${bookId}`)
  }
}
