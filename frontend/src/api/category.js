/**
 * 分类API模块
 * 提供图书分类相关的接口调用，包括：
 * - 获取分类列表
 * - 分类管理（管理员）
 * - 分类启用/禁用
 */
import request from './request'

/**
 * 分类API对象
 * @namespace categoryApi
 */
export const categoryApi = {
  /**
   * 获取启用的分类列表
   * 只返回状态为启用的分类，用于前台展示。
   * @returns {Promise<Array>} 启用的分类列表
   */
  getActive() {
    return request.get('/categories')
  },

  /**
   * 获取所有分类列表（包括禁用的）
   * 返回所有分类，包括禁用状态的，用于后台管理。
   * @returns {Promise<Array>} 所有分类列表
   */
  getAll() {
    return request.get('/categories/all')
  },

  /**
   * 根据ID获取分类详情
   * @param {number} id - 分类ID
   * @returns {Promise} 分类详情对象
   */
  getById(id) {
    return request.get(`/categories/${id}`)
  },

  /**
   * 创建分类（管理员）
   * @param {Object} data - 分类数据
   * @param {string} data.name - 分类名称
   * @param {number} data.sort - 排序号
   * @returns {Promise} 创建后的分类对象
   */
  create(data) {
    return request.post('/categories', data)
  },

  /**
   * 更新分类（管理员）
   * @param {number} id - 分类ID
   * @param {Object} data - 更新的分类数据
   * @returns {Promise} 更新后的分类对象
   */
  update(id, data) {
    return request.put(`/categories/${id}`, data)
  },

  /**
   * 启用分类（管理员）
   * @param {number} id - 分类ID
   * @returns {Promise} 操作结果
   */
  enable(id) {
    return request.put(`/categories/${id}/enable`)
  },

  /**
   * 禁用分类（管理员）
   * 如果分类下有上架书籍则无法禁用。
   * @param {number} id - 分类ID
   * @returns {Promise} 操作结果
   */
  disable(id) {
    return request.put(`/categories/${id}/disable`)
  },

  /**
   * 删除分类（管理员）
   * 如果分类下有上架书籍则无法删除。
   * @param {number} id - 分类ID
   * @returns {Promise} 删除结果
   */
  delete(id) {
    return request.delete(`/categories/${id}`)
  }
}
