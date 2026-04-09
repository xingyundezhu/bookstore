/**
 * 购物车API模块
 * 提供购物车相关的接口调用，包括：
 * - 获取购物车列表
 * - 添加商品到购物车
 * - 更新购物车商品数量
 * - 删除购物车商品
 */
import request from './request'

/**
 * 购物车API对象
 * @namespace cartApi
 */
export const cartApi = {
  /**
   * 获取购物车列表
   * @returns {Promise<Array>} 购物车商品列表
   */
  getList() {
    return request.get('/cart')
  },

  /**
   * 添加商品到购物车
   * @param {number} bookId - 图书ID
   * @param {number} quantity - 数量
   * @returns {Promise} 添加结果
   */
  add(bookId, quantity = 1) {
    return request.post('/cart', null, { params: { bookId, quantity } })
  },

  /**
   * 更新购物车商品数量
   * @param {number} bookId - 图书ID
   * @param {number} quantity - 新数量
   * @returns {Promise} 更新结果
   */
  update(bookId, quantity) {
    return request.put(`/cart/${bookId}`, null, { params: { quantity } })
  },

  /**
   * 更新购物车商品数量（别名）
   * @param {number} bookId - 图书ID
   * @param {number} quantity - 新数量
   * @returns {Promise} 更新结果
   */
  updateQuantity(bookId, quantity) {
    return request.put(`/cart/${bookId}`, null, { params: { quantity } })
  },

  /**
   * 更新购物车商品选中状态
   * @param {number} bookId - 图书ID
   * @param {number} selected - 选中状态（0-未选中，1-选中）
   * @returns {Promise} 更新结果
   */
  updateSelected(bookId, selected) {
    return request.put(`/cart/${bookId}/selected`, null, { params: { selected } })
  },

  /**
   * 更新所有商品选中状态
   * @param {number} selected - 选中状态（0-未选中，1-选中）
   * @returns {Promise} 更新结果
   */
  updateAllSelected(selected) {
    return request.put('/cart/selected', null, { params: { selected } })
  },

  /**
   * 删除购物车商品
   * @param {number} bookId - 图书ID
   * @returns {Promise} 删除结果
   */
  remove(bookId) {
    return request.delete(`/cart/${bookId}`)
  },

  /**
   * 清空购物车
   * @returns {Promise} 清空结果
   */
  clear() {
    return request.delete('/cart')
  },

  /**
   * 获取购物车商品数量
   * @returns {Promise<number>} 商品数量
   */
  getCount() {
    return request.get('/cart/count')
  }
}
