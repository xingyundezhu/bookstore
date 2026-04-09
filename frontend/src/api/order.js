/**
 * 订单API模块
 * 提供订单相关的接口调用，包括：
 * - 创建订单
 * - 订单支付/取消/确认收货
 * - 订单列表查询
 * - 订单管理（管理员）
 */
import request from './request'

/**
 * 订单API对象
 * @namespace orderApi
 */
export const orderApi = {
  /**
   * 创建订单
   * @param {Object} data - 订单数据
   * @param {Array} data.items - 订单项列表
   * @param {number} data.addressId - 收货地址ID
   * @returns {Promise} 创建后的订单对象
   */
  create(data) {
    return request.post('/orders', data)
  },

  /**
   * 支付订单
   * @param {string} orderNo - 订单号
   * @returns {Promise} 支付结果
   */
  pay(orderNo) {
    return request.post(`/orders/${orderNo}/pay`)
  },

  /**
   * 取消订单
   * @param {string} orderNo - 订单号
   * @returns {Promise} 取消结果
   */
  cancel(orderNo) {
    return request.post(`/orders/${orderNo}/cancel`)
  },

  /**
   * 确认收货
   * @param {string} orderNo - 订单号
   * @returns {Promise} 确认结果
   */
  receive(orderNo) {
    return request.post(`/orders/${orderNo}/receive`)
  },

  /**
   * 根据订单号获取订单详情
   * @param {string} orderNo - 订单号
   * @returns {Promise} 订单详情对象
   */
  getByOrderNo(orderNo) {
    return request.get(`/orders/${orderNo}`)
  },

  /**
   * 获取用户订单列表
   * @param {Object} params - 查询参数
   * @returns {Promise} 订单分页列表
   */
  getUserList(params) {
    return request.get('/orders', { params })
  },

  /**
   * 获取所有订单列表（管理员）
   * @param {Object} params - 查询参数
   * @returns {Promise} 订单分页列表
   */
  getAdminList(params) {
    return request.get('/orders/admin', { params })
  },

  /**
   * 发货（管理员）
   * @param {string} orderNo - 订单号
   * @returns {Promise} 发货结果
   */
  deliver(orderNo) {
    return request.post(`/orders/${orderNo}/deliver`)
  },

  /**
   * 更新订单状态（管理员）
   * @param {string} orderNo - 订单号
   * @param {number} status - 新状态
   * @returns {Promise} 更新结果
   */
  updateStatus(orderNo, status) {
    return request.put(`/orders/${orderNo}/status`, null, { params: { status } })
  },

  /**
   * 获取订单统计数据（管理员）
   * @returns {Promise<Object>} 统计数据
   */
  getStatistics() {
    return request.get('/orders/statistics')
  },

  /**
   * 删除已完成订单（管理员）
   * @param {string} orderNo - 订单号
   * @returns {Promise} 删除结果
   */
  delete(orderNo) {
    return request.delete(`/orders/${orderNo}`)
  }
}
