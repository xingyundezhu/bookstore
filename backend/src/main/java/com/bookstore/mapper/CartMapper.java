package com.bookstore.mapper;

import com.bookstore.entity.Cart;
import org.apache.ibatis.annotations.*;
import java.util.List;

/**
 * 购物车数据访问层
 */
@Mapper
public interface CartMapper {
    
    /**
     * 查询用户购物车列表（关联图书信息）
     */
    @Select("SELECT c.*, b.title as book_title, b.cover_image as book_cover, b.price, b.stock, (c.quantity * b.price) as subtotal " +
            "FROM t_cart c LEFT JOIN t_book b ON c.book_id = b.id WHERE c.user_id = #{userId} ORDER BY c.create_time DESC")
    List<Cart> findByUserId(@Param("userId") Long userId);
    
    /**
     * 查询用户选中的购物车商品
     */
    @Select("SELECT c.*, b.title as book_title, b.cover_image as book_cover, b.price, b.stock, (c.quantity * b.price) as subtotal " +
            "FROM t_cart c LEFT JOIN t_book b ON c.book_id = b.id WHERE c.user_id = #{userId} AND c.selected = 1 ORDER BY c.create_time DESC")
    List<Cart> findSelectedByUserId(@Param("userId") Long userId);
    
    /**
     * 查询用户购物车中指定图书
     */
    @Select("SELECT c.*, b.title as book_title, b.cover_image as book_cover, b.price, b.stock " +
            "FROM t_cart c LEFT JOIN t_book b ON c.book_id = b.id WHERE c.user_id = #{userId} AND c.book_id = #{bookId}")
    Cart findByUserIdAndBookId(@Param("userId") Long userId, @Param("bookId") Long bookId);
    
    /**
     * 添加商品到购物车
     */
    @Insert("INSERT INTO t_cart(user_id, book_id, quantity, selected, create_time, update_time) VALUES(#{userId}, #{bookId}, #{quantity}, #{selected}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Cart cart);
    
    /**
     * 更新购物车商品数量
     */
    @Update("UPDATE t_cart SET quantity = #{quantity}, update_time = NOW() WHERE user_id = #{userId} AND book_id = #{bookId}")
    int updateQuantity(@Param("userId") Long userId, @Param("bookId") Long bookId, @Param("quantity") Integer quantity);
    
    /**
     * 更新单个商品选择状态
     */
    @Update("UPDATE t_cart SET selected = #{selected}, update_time = NOW() WHERE user_id = #{userId} AND book_id = #{bookId}")
    int updateSelected(@Param("userId") Long userId, @Param("bookId") Long bookId, @Param("selected") Integer selected);
    
    /**
     * 更新所有商品选择状态
     */
    @Update("UPDATE t_cart SET selected = #{selected}, update_time = NOW() WHERE user_id = #{userId}")
    int updateAllSelected(@Param("userId") Long userId, @Param("selected") Integer selected);
    
    /**
     * 删除购物车中指定商品
     */
    @Delete("DELETE FROM t_cart WHERE user_id = #{userId} AND book_id = #{bookId}")
    int deleteByUserIdAndBookId(@Param("userId") Long userId, @Param("bookId") Long bookId);
    
    /**
     * 清空用户购物车
     */
    @Delete("DELETE FROM t_cart WHERE user_id = #{userId}")
    int deleteByUserId(@Param("userId") Long userId);
    
    /**
     * 删除用户选中的购物车商品
     */
    @Delete("DELETE FROM t_cart WHERE user_id = #{userId} AND selected = 1")
    int deleteSelectedByUserId(@Param("userId") Long userId);
    
    /**
     * 统计用户购物车商品数量
     */
    @Select("SELECT COUNT(*) FROM t_cart WHERE user_id = #{userId}")
    long countByUserId(@Param("userId") Long userId);
}
