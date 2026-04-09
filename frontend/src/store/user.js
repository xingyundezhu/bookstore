/**
 * 用户状态管理模块
 * 使用Pinia管理用户相关的全局状态，包括：
 * - 用户登录状态
 * - 用户信息
 * - Token管理
 * - 权限控制
 */
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { userApi } from '@/api/user'
import router from '@/router'

/**
 * 检查Token是否过期
 * 解析JWT Token的payload部分，检查exp字段判断是否过期。
 * @param {string} token - JWT Token
 * @returns {boolean} 是否已过期
 */
function isTokenExpired(token) {
  if (!token) return true
  try {
    const payload = JSON.parse(atob(token.split('.')[1]))
    if (!payload || !payload.exp) return true
    return payload.exp * 1000 < Date.now()
  } catch (e) {
    return true
  }
}

/**
 * 用户Store
 * 提供用户状态管理，包括登录、注册、登出等功能。
 */
export const useUserStore = defineStore('user', () => {
  /** @type {import('vue').Ref<string>} 用户Token */
  const token = ref(localStorage.getItem('token') || '')
  
  /** @type {import('vue').Ref<Object|null>} 用户信息对象 */
  const user = ref(JSON.parse(localStorage.getItem('user') || 'null'))

  /** @type {import('vue').ComputedRef<boolean>} 是否已登录 */
  const isLoggedIn = computed(() => !!token.value && !isTokenExpired(token.value))
  
  /** @type {import('vue').ComputedRef<boolean>} 是否是管理员 */
  const isAdmin = computed(() => user.value?.role === 'ADMIN')

  /**
   * 清除认证信息
   * 清除内存和localStorage中的用户信息和Token。
   */
  function clearAuth() {
    token.value = ''
    user.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('user')
  }

  /**
   * 检查Token有效性
   * 检查Token是否存在且未过期，如果过期则清除认证信息。
   * @returns {boolean} Token是否有效
   */
  function checkToken() {
    if (token.value && isTokenExpired(token.value)) {
      clearAuth()
      return false
    }
    return !!token.value
  }

  /**
   * 用户登录
   * @param {Object} credentials - 登录凭证
   * @param {string} credentials.username - 用户名
   * @param {string} credentials.password - 密码
   * @returns {Promise<boolean>} 登录是否成功
   * @throws {Error} 登录失败时抛出错误
   */
  async function login(credentials) {
    const res = await userApi.login(credentials)
    if (res.code === 200) {
      token.value = res.data.token
      user.value = {
        id: res.data.userId,
        username: res.data.username,
        email: res.data.email,
        role: res.data.role,
        avatar: res.data.avatar
      }
      localStorage.setItem('token', res.data.token)
      localStorage.setItem('user', JSON.stringify(user.value))
      return true
    }
    throw new Error(res.message)
  }

  /**
   * 用户注册
   * @param {Object} data - 注册数据
   * @returns {Promise<boolean>} 注册是否成功
   * @throws {Error} 注册失败时抛出错误
   */
  async function register(data) {
    const res = await userApi.register(data)
    if (res.code === 200) {
      return true
    }
    throw new Error(res.message)
  }

  /**
   * 获取用户信息
   * 从服务器获取最新的用户信息并更新本地状态。
   */
  async function fetchUserInfo() {
    if (!checkToken()) return
    try {
      const res = await userApi.getInfo()
      if (res.code === 200) {
        user.value = res.data
        localStorage.setItem('user', JSON.stringify(res.data))
      }
    } catch (error) {
      clearAuth()
    }
  }

  /**
   * 用户登出
   * @param {boolean} redirect - 是否跳转到首页
   */
  function logout(redirect = true) {
    clearAuth()
    if (redirect) {
      router.push('/')
    }
  }

  /**
   * 更新用户信息
   * 合并更新用户信息到本地状态。
   * @param {Object} info - 要更新的用户信息
   */
  function updateUserInfo(info) {
    user.value = { ...user.value, ...info }
    localStorage.setItem('user', JSON.stringify(user.value))
  }

  return {
    token,
    user,
    isLoggedIn,
    isAdmin,
    login,
    register,
    logout,
    fetchUserInfo,
    updateUserInfo,
    checkToken,
    clearAuth
  }
})
