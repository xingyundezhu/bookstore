/**
 * 图书API模块
 * 提供图书相关的接口调用，包括：
 * - 图书列表查询
 * - 图书详情获取
 * - 图书搜索
 * - 热门图书获取
 * - 新书上架获取
 * - 图书管理（管理员）
 */
import request from './request'

/**
 * 图书API对象
 * @namespace bookApi
 */
export const bookApi = {
  /**
   * 获取图书列表
   * @param {Object} params - 查询参数
   * @param {number} params.page - 页码（从0开始）
   * @param {number} params.size - 每页数量
   * @param {string} params.sortBy - 排序字段
   * @param {string} params.sortDir - 排序方向（asc/desc）
   * @returns {Promise} 图书分页列表
   */
  getList(params) {
    return request.get('/books', { params })
  },

  /**
   * 根据ID获取图书详情
   * @param {number} id - 图书ID
   * @returns {Promise} 图书详情对象
   */
  getById(id) {
    return request.get(`/books/${id}`)
  },

  /**
   * 搜索图书
   * @param {string} keyword - 搜索关键词
   * @param {Object} params - 分页参数
   * @returns {Promise} 搜索结果列表
   */
  search(keyword, params) {
    return request.get('/books/search', { params: { keyword, ...params } })
  },

  /**
   * 根据分类获取图书列表
   * @param {number} categoryId - 分类ID
   * @param {Object} params - 分页参数
   * @returns {Promise} 图书分页列表
   */
  getByCategory(categoryId, params) {
    return request.get(`/books/category/${categoryId}`, { params })
  },

  /**
   * 获取热门图书
   * @param {number} limit - 获取数量
   * @returns {Promise<Array>} 热门图书列表
   */
  getHot(limit = 10) {
    return request.get('/books/hot', { params: { limit } })
  },

  /**
   * 获取新书上架
   * @param {number} limit - 获取数量
   * @returns {Promise<Array>} 新书列表
   */
  getNew(limit = 10) {
    return request.get('/books/new', { params: { limit } })
  },

  /**
   * 获取管理员图书列表
   * @param {Object} params - 分页参数
   * @returns {Promise} 图书分页列表
   */
  getAdminList(params) {
    return request.get('/admin/books', { params })
  },

  /**
   * 获取销量统计数据
   * @returns {Promise} 销量统计数据
   */
  getSalesStatistics() {
    return request.get('/admin/sales/statistics')
  },

  /**
   * 获取销量排行榜
   * @param {number} page - 页码
   * @param {number} size - 每页数量
   * @param {number} categoryId - 分类ID（可选）
   * @returns {Promise} 销量排行榜分页数据
   */
  getSalesRanking(page, size, categoryId) {
    const params = { page, size }
    if (categoryId) params.categoryId = categoryId
    return request.get('/admin/sales/ranking', { params })
  },

  /**
   * 更新图书销量
   * @param {number} id - 图书ID
   * @param {number} sales - 销量
   * @returns {Promise} 更新结果
   */
  updateSales(id, sales) {
    return request.put(`/admin/books/${id}/sales`, null, { params: { sales } })
  },

  /**
   * 添加图书（管理员）
   * @param {Object} data - 图书数据
   * @returns {Promise<Object>} 添加后的图书对象
   */
  create(data) {
    return request.post('/books', data)
  },

  /**
   * 更新图书（管理员）
   * @param {number} id - 图书ID
   * @param {Object} data - 更新的图书数据
   * @returns {Promise} 更新后的图书对象
   */
  update(id, data) {
    return request.put(`/books/${id}`, data)
  },

  /**
   * 删除图书（管理员）
   * @param {number} id - 图书ID
   * @returns {Promise} 删除结果
   */
  delete(id) {
    return request.delete(`/books/${id}`)
  },

  /**
   * 上传图书封面图片（管理员）
   * @param {File} file - 图片文件
   * @returns {Promise} 图片访问URL
   */
  uploadCover(file) {
    const formData = new FormData()
    formData.append('file', file)
    return request.post('/books/upload/cover', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
  }
}
