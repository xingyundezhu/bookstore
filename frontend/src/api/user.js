/**
 * 用户API模块
 * 提供用户相关的接口调用，包括：
 * - 用户登录/注册
 * - 用户信息获取/更新
 * - 密码修改
 * - 头像上传
 * - 邮箱/手机绑定
 */
import request from './request'

/**
 * 用户API对象
 * @namespace userApi
 */
export const userApi = {
  /**
   * 用户登录
   * @param {Object} data - 登录数据
   * @param {string} data.username - 用户名
   * @param {string} data.password - 密码
   * @returns {Promise} 登录结果，包含token和用户信息
   */
  login(data) {
    return request.post('/auth/login', data)
  },

  /**
   * 用户注册
   * @param {Object} data - 注册数据
   * @param {string} data.username - 用户名
   * @param {string} data.password - 密码
   * @param {string} data.email - 邮箱（可选）
   * @returns {Promise} 注册结果
   */
  register(data) {
    return request.post('/auth/register', data)
  },

  /**
   * 获取当前用户信息
   * @returns {Promise} 用户信息对象
   */
  getInfo() {
    return request.get('/auth/me')
  },

  /**
   * 更新用户信息
   * @param {Object} data - 更新的用户数据
   * @returns {Promise} 更新后的用户信息
   */
  updateInfo(data) {
    return request.put('/auth/me', data)
  },

  /**
   * 修改密码
   * @param {string} oldPassword - 原密码
   * @param {string} newPassword - 新密码
   * @returns {Promise} 修改结果
   */
  updatePassword(oldPassword, newPassword) {
    return request.put('/auth/password', null, {
      params: { oldPassword, newPassword }
    })
  },

  /**
   * 上传用户头像
   * @param {File} file - 头像图片文件
   * @returns {Promise} 头像访问URL
   */
  uploadAvatar(file) {
    const formData = new FormData()
    formData.append('file', file)
    return request.post('/auth/avatar', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
  },

  /**
   * 发送邮箱验证码
   * @param {string} email - 邮箱地址
   * @returns {Promise} 发送结果
   */
  sendEmailCode(email) {
    return request.post('/auth/email/code', null, { params: { email } })
  },

  /**
   * 发送手机验证码
   * @param {string} phone - 手机号码
   * @returns {Promise} 发送结果
   */
  sendPhoneCode(phone) {
    return request.post('/auth/phone/code', null, { params: { phone } })
  },

  /**
   * 绑定邮箱
   * @param {string} email - 邮箱地址
   * @param {string} code - 验证码
   * @returns {Promise} 绑定结果
   */
  bindEmail(email, code) {
    return request.post('/auth/email/bind', null, { params: { email, code } })
  },

  /**
   * 绑定手机
   * @param {string} phone - 手机号码
   * @param {string} code - 验证码
   * @returns {Promise} 绑定结果
   */
  bindPhone(phone, code) {
    return request.post('/auth/phone/bind', null, { params: { phone, code } })
  }
}
