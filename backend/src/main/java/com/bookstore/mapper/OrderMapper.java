package com.bookstore.mapper;

import com.bookstore.entity.Order;
import org.apache.ibatis.annotations.*;
import java.util.List;

/**
 * 订单数据访问层
 */
@Mapper
public interface OrderMapper {
    
    /**
     * 根据ID查询订单
     */
    @Select("SELECT * FROM t_order WHERE id = #{id}")
    Order findById(@Param("id") Long id);
    
    /**
     * 根据订单号查询订单
     */
    @Select("SELECT * FROM t_order WHERE order_no = #{orderNo}")
    Order findByOrderNo(@Param("orderNo") String orderNo);
    
    /**
     * 查询用户所有订单
     */
    @Select("SELECT * FROM t_order WHERE user_id = #{userId} ORDER BY create_time DESC")
    List<Order> findByUserId(@Param("userId") Long userId);
    
    /**
     * 查询用户指定状态的订单
     */
    @Select("SELECT * FROM t_order WHERE user_id = #{userId} AND status = #{status} ORDER BY create_time DESC")
    List<Order> findByUserIdAndStatus(@Param("userId") Long userId, @Param("status") Integer status);
    
    /**
     * 查询所有订单
     */
    @Select("SELECT * FROM t_order ORDER BY create_time DESC")
    List<Order> findAll();
    
    /**
     * 创建订单
     */
    @Insert("INSERT INTO t_order(order_no, user_id, total_amount, pay_amount, freight, discount, status, payment_type, receiver, receiver_phone, receiver_address, remark, create_time, update_time) " +
            "VALUES(#{orderNo}, #{userId}, #{totalAmount}, #{payAmount}, #{freight}, #{discount}, #{status}, #{paymentType}, #{receiver}, #{receiverPhone}, #{receiverAddress}, #{remark}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Order order);
    
    /**
     * 更新订单状态（含支付时间）
     */
    @Update("UPDATE t_order SET status = #{status}, payment_time = #{paymentTime}, update_time = NOW() WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status, @Param("paymentTime") java.time.LocalDateTime paymentTime);
    
    /**
     * 发货
     */
    @Update("UPDATE t_order SET status = #{status}, delivery_time = NOW(), update_time = NOW() WHERE order_no = #{orderNo}")
    int deliver(@Param("orderNo") String orderNo, @Param("status") Integer status);
    
    /**
     * 确认收货
     */
    @Update("UPDATE t_order SET status = #{status}, receive_time = NOW(), update_time = NOW() WHERE order_no = #{orderNo}")
    int receive(@Param("orderNo") String orderNo, @Param("status") Integer status);
    
    /**
     * 根据订单号更新状态
     */
    @Update("UPDATE t_order SET status = #{status}, update_time = NOW() WHERE order_no = #{orderNo}")
    int updateStatusByOrderNo(@Param("orderNo") String orderNo, @Param("status") Integer status);
    
    /**
     * 统计今日订单数
     */
    @Select("SELECT COUNT(*) FROM t_order WHERE DATE(create_time) = CURDATE()")
    long countToday();
    
    /**
     * 统计今日销售额
     */
    @Select("SELECT IFNULL(SUM(pay_amount), 0) FROM t_order WHERE DATE(create_time) = CURDATE() AND status >= 1")
    java.math.BigDecimal sumTodaySales();
    
    /**
     * 统计本月订单数
     */
    @Select("SELECT COUNT(*) FROM t_order WHERE YEAR(create_time) = YEAR(CURDATE()) AND MONTH(create_time) = MONTH(CURDATE())")
    long countMonth();
    
    /**
     * 统计本月销售额
     */
    @Select("SELECT IFNULL(SUM(pay_amount), 0) FROM t_order WHERE YEAR(create_time) = YEAR(CURDATE()) AND MONTH(create_time) = MONTH(CURDATE()) AND status >= 1")
    java.math.BigDecimal sumMonthSales();
    
    /**
     * 根据订单号删除订单
     */
    @Delete("DELETE FROM t_order WHERE order_no = #{orderNo}")
    int deleteByOrderNo(@Param("orderNo") String orderNo);
}
