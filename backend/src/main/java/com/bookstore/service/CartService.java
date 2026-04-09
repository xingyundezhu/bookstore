package com.bookstore.service;

import com.bookstore.dto.CartDTO;
import com.bookstore.entity.Cart;
import java.util.List;

/**
 * 购物车服务接口
 */
public interface CartService {
    
    /**
     * 获取用户购物车列表
     *
     * @param userId 用户ID
     * @return 购物车DTO列表
     */
    List<CartDTO> getCartByUserId(Long userId);
    
    /**
     * 添加商品到购物车
     *
     * @param userId 用户ID
     * @param bookId 图书ID
     * @param quantity 数量
     * @return 购物车实体
     */
    Cart addToCart(Long userId, Long bookId, Integer quantity);
    
    /**
     * 更新购物车商品数量
     *
     * @param userId 用户ID
     * @param bookId 图书ID
     * @param quantity 新数量
     */
    void updateQuantity(Long userId, Long bookId, Integer quantity);
    
    /**
     * 从购物车移除商品
     *
     * @param userId 用户ID
     * @param bookId 图书ID
     */
    void removeFromCart(Long userId, Long bookId);
    
    /**
     * 清空购物车
     *
     * @param userId 用户ID
     */
    void clearCart(Long userId);
    
    /**
     * 更新商品选择状态
     *
     * @param userId 用户ID
     * @param bookId 图书ID
     * @param selected 选择状态：1-选中，0-未选中
     */
    void updateSelected(Long userId, Long bookId, Integer selected);
    
    /**
     * 更新所有商品选择状态
     *
     * @param userId 用户ID
     * @param selected 选择状态：1-全选，0-全不选
     */
    void updateAllSelected(Long userId, Integer selected);
    
    /**
     * 删除选中的购物车商品
     *
     * @param userId 用户ID
     */
    void deleteSelectedItems(Long userId);
    
    /**
     * 获取购物车商品数量
     *
     * @param userId 用户ID
     * @return 商品数量
     */
    long getCartCount(Long userId);
}
