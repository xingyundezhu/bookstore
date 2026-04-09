/**
 * 购物车状态管理模块
 * <p>
 * 使用Pinia管理购物车相关的全局状态，包括：
 * - 购物车商品数量
 * - 购物车商品列表
 * </p>
 * 
 * @module store/cart
 * @author bookstore
 * @since 1.0
 */
import { defineStore } from 'pinia'
import { ref } from 'vue'
import { cartApi } from '@/api/cart'

/**
 * 购物车Store
 * <p>
 * 提供购物车状态管理，包括获取数量、更新数量等功能。
 * </p>
 */
export const useCartStore = defineStore('cart', () => {
  /** @type {import('vue').Ref<number>} 购物车商品数量 */
  const cartCount = ref(0)
  
  /** @type {import('vue').Ref<Array>} 购物车商品列表 */
  const cartItems = ref([])

  /**
   * 获取购物车商品数量
   * <p>
   * 从服务器获取最新的购物车商品数量并更新本地状态。
   * </p>
   * @returns {Promise<number>} 购物车数量
   */
  async function fetchCartCount() {
    try {
      const res = await cartApi.getCount()
      if (res.code === 200) {
        cartCount.value = res.data || 0
        return cartCount.value
      }
    } catch (error) {
      console.error('获取购物车数量失败', error)
      cartCount.value = 0
    }
    return 0
  }

  /**
   * 获取购物车列表
   * <p>
   * 从服务器获取最新的购物车商品列表并更新本地状态。
   * </p>
   * @returns {Promise<Array>} 购物车商品列表
   */
  async function fetchCartItems() {
    try {
      const res = await cartApi.getList()
      if (res.code === 200) {
        cartItems.value = res.data || []
        cartCount.value = cartItems.value.length
        return cartItems.value
      }
    } catch (error) {
      console.error('获取购物车列表失败', error)
      cartItems.value = []
      cartCount.value = 0
    }
    return []
  }

  /**
   * 清空购物车状态
   * <p>
   * 清空本地购物车状态，用于用户登出时。
   * </p>
   */
  function clearCart() {
    cartCount.value = 0
    cartItems.value = []
  }

  /**
   * 更新购物车数量
   * <p>
   * 手动更新购物车数量。
   * </p>
   * @param {number} count - 新的购物车数量
   */
  function setCartCount(count) {
    cartCount.value = count
  }

  return {
    cartCount,
    cartItems,
    fetchCartCount,
    fetchCartItems,
    clearCart,
    setCartCount
  }
})
