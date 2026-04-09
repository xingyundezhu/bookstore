/**
 * 收货地址API模块
 * 提供收货地址相关的接口调用，包括：
 * - 获取地址列表
 * - 添加/更新/删除地址
 * - 设置默认地址
 * 
 * @module api/address
 */
import request from './request'

/**
 * 收货地址API对象
 * @namespace addressApi
 */
export const addressApi = {
  /**
   * 获取用户地址列表
   * @returns {Promise<Array>} 地址列表
   */
  getList() {
    return request.get('/addresses')
  },

  /**
   * 根据ID获取地址详情
   * @param {number} id - 地址ID
   * @returns {Promise} 地址详情对象
   */
  getById(id) {
    return request.get(`/addresses/${id}`)
  },

  /**
   * 添加地址
   * @param {Object} data - 地址数据
   * @param {string} data.receiver - 收货人
   * @param {string} data.phone - 电话
   * @param {string} data.province - 省份
   * @param {string} data.city - 城市
   * @param {string} data.district - 区县
   * @param {string} data.detail - 详细地址
   * @returns {Promise<Object>} 添加后的地址对象
   */
  add(data) {
    return request.post('/addresses', data)
  },

  /**
   * 添加地址（别名）
   * @param {Object} data - 地址数据
   * @returns {Promise<Object>} 添加后的地址对象
   */
  create(data) {
    return request.post('/addresses', data)
  },

  /**
   * 更新地址
   * @param {number} id - 地址ID
   * @param {Object} data - 更新的地址数据
   * @returns {Promise} 更新后的地址对象
   */
  update(id, data) {
    return request.put(`/addresses/${id}`, data)
  },

  /**
   * 删除地址
   * @param {number} id - 地址ID
   * @returns {Promise} 删除结果
   */
  delete(id) {
    return request.delete(`/addresses/${id}`)
  },

  /**
   * 设置默认地址
   * @param {number} id - 地址ID
   * @returns {Promise} 设置结果
   */
  setDefault(id) {
    return request.put(`/addresses/${id}/default`)
  }
}
