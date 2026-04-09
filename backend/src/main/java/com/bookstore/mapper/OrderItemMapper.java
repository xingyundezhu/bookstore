package com.bookstore.mapper;

import com.bookstore.entity.OrderItem;
import org.apache.ibatis.annotations.*;
import java.util.List;

/**
 * 订单项数据访问层
 *
 * @author bookstore
 * @since 1.0
 */
@Mapper
public interface OrderItemMapper {
    
    /**
     * 根据ID查询订单项
     *
     * @param id 订单项ID
     * @return 订单项
     */
    @Select("SELECT * FROM t_order_item WHERE id = #{id}")
    OrderItem findById(@Param("id") Long id);
    
    /**
     * 根据订单ID查询订单项列表
     *
     * @param orderId 订单ID
     * @return 订单项列表
     */
    @Select("SELECT * FROM t_order_item WHERE order_id = #{orderId}")
    List<OrderItem> findByOrderId(@Param("orderId") Long orderId);
    
    /**
     * 创建订单项
     *
     * @param item 订单项
     * @return 影响行数
     */
    @Insert("INSERT INTO t_order_item(order_id, book_id, book_title, book_cover, price, quantity, total_amount, create_time) " +
            "VALUES(#{orderId}, #{bookId}, #{bookTitle}, #{bookCover}, #{price}, #{quantity}, #{subtotal}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(OrderItem item);
    
    /**
     * 根据订单ID删除订单项
     *
     * @param orderId 订单ID
     * @return 影响行数
     */
    @Delete("DELETE FROM t_order_item WHERE order_id = #{orderId}")
    int deleteByOrderId(@Param("orderId") Long orderId);
}
