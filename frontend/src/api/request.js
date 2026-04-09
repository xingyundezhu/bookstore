/**
 * 请求配置模块
 * 配置Axios实例，包括：
 * - 基础URL配置
 * - 请求/响应拦截器
 * - 错误处理
 * - Token自动注入
 */
import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'

/**
 * Axios实例
 * 预配置的Axios实例，自动处理认证和错误。
 */
const request = axios.create({
  baseURL: '/api',
  timeout: 10000
})

/**
 * 请求拦截器
 * 在每个请求发送前：
 * - 从localStorage获取token
 * - 将token添加到Authorization头
 */
request.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

/**
 * 响应拦截器
 * 处理服务器响应：
 * - 401错误：Token过期，清除登录状态并跳转登录页
 * - 其他错误：显示错误消息
 */
request.interceptors.response.use(
  response => {
    return response.data
  },
  error => {
    if (error.response) {
      switch (error.response.status) {
        case 401:
          localStorage.removeItem('token')
          localStorage.removeItem('user')
          ElMessage.error('登录已过期，请重新登录')
          router.push({ name: 'Login', query: { redirect: router.currentRoute.value.fullPath } })
          break
        case 403:
          ElMessage.error('没有权限访问该资源')
          break
        case 404:
          ElMessage.error('请求的资源不存在')
          break
        case 500:
          ElMessage.error('服务器内部错误')
          break
        default:
          ElMessage.error(error.response.data?.message || '请求失败')
      }
    } else {
      ElMessage.error('网络连接失败')
    }
    return Promise.reject(error)
  }
)

export default request
