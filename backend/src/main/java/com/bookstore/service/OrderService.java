package com.bookstore.service;

import com.bookstore.dto.CreateOrderRequest;
import com.bookstore.dto.OrderDTO;
import com.bookstore.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Map;

/**
 * 订单服务接口
 */
public interface OrderService {
    
    /**
     * 创建订单
     *
     * @param userId 用户ID
     * @param request 创建订单请求
     * @return 创建的订单实体
     */
    Order createOrder(Long userId, CreateOrderRequest request);
    
    /**
     * 支付订单
     *
     * @param userId 用户ID
     * @param orderNo 订单号
     * @return 支付后的订单实体
     */
    Order payOrder(Long userId, String orderNo);
    
    /**
     * 取消订单
     *
     * @param userId 用户ID
     * @param orderNo 订单号
     */
    void cancelOrder(Long userId, String orderNo);
    
    /**
     * 发货
     *
     * @param orderNo 订单号
     */
    void deliverOrder(String orderNo);
    
    /**
     * 确认收货
     *
     * @param userId 用户ID
     * @param orderNo 订单号
     */
    void receiveOrder(Long userId, String orderNo);
    
    /**
     * 根据订单号获取订单
     *
     * @param orderNo 订单号
     * @return 订单实体
     */
    Order getOrderByOrderNo(String orderNo);
    
    /**
     * 获取用户订单列表
     *
     * @param userId 用户ID
     * @param status 订单状态（可选）
     * @param pageable 分页参数
     * @return 订单DTO分页列表
     */
    Page<OrderDTO> getUserOrders(Long userId, Integer status, Pageable pageable);
    
    /**
     * 获取所有订单列表（管理员）
     *
     * @param pageable 分页参数
     * @return 订单DTO分页列表
     */
    Page<OrderDTO> getAllOrders(Pageable pageable);
    
    /**
     * 更新订单状态
     *
     * @param orderNo 订单号
     * @param status 新状态
     */
    void updateOrderStatus(String orderNo, Integer status);
    
    /**
     * 获取订单统计数据
     *
     * @return 统计数据Map
     */
    Map<String, Object> getOrderStatistics();
    
    /**
     * 删除已完成订单（管理员）
     *
     * @param orderNo 订单号
     */
    void deleteCompletedOrder(String orderNo);
}
