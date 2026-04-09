package com.bookstore.controller;

import com.bookstore.dto.ApiResponse;
import com.bookstore.dto.CreateOrderRequest;
import com.bookstore.dto.OrderDTO;
import com.bookstore.entity.Order;
import com.bookstore.entity.User;
import com.bookstore.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 订单管理控制器
 * 提供订单创建、支付、查询等功能
 */
@Tag(name = "订单管理", description = "订单创建、支付、查询等功能")
@RestController
@RequestMapping("/orders")
public class OrderController {
    
    private final OrderService orderService;
    
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }
    
    /**
     * 创建订单
     * @param user 当前登录用户
     * @param request 订单创建请求
     * @return 创建的订单
     */
    @Operation(summary = "创建订单", description = "从购物车创建新订单")
    @PostMapping
    public ApiResponse<Order> createOrder(
            @AuthenticationPrincipal User user,
            @RequestBody CreateOrderRequest request) {
        return ApiResponse.success("下单成功", orderService.createOrder(user.getId(), request));
    }
    
    /**
     * 支付订单
     * @param user 当前登录用户
     * @param orderNo 订单号
     * @return 支付后的订单
     */
    @Operation(summary = "支付订单", description = "支付指定订单")
    @PostMapping("/{orderNo}/pay")
    public ApiResponse<Order> payOrder(
            @AuthenticationPrincipal User user,
            @Parameter(description = "订单号") @PathVariable String orderNo) {
        return ApiResponse.success("支付成功", orderService.payOrder(user.getId(), orderNo));
    }
    
    /**
     * 取消订单
     * @param user 当前登录用户
     * @param orderNo 订单号
     * @return 操作结果
     */
    @Operation(summary = "取消订单", description = "取消未支付的订单")
    @PostMapping("/{orderNo}/cancel")
    public ApiResponse<Void> cancelOrder(
            @AuthenticationPrincipal User user,
            @Parameter(description = "订单号") @PathVariable String orderNo) {
        orderService.cancelOrder(user.getId(), orderNo);
        return ApiResponse.success("取消成功", null);
    }
    
    /**
     * 确认收货
     * @param user 当前登录用户
     * @param orderNo 订单号
     * @return 操作结果
     */
    @Operation(summary = "确认收货", description = "确认已发货的订单")
    @PostMapping("/{orderNo}/receive")
    public ApiResponse<Void> receiveOrder(
            @AuthenticationPrincipal User user,
            @Parameter(description = "订单号") @PathVariable String orderNo) {
        orderService.receiveOrder(user.getId(), orderNo);
        return ApiResponse.success("确认收货成功", null);
    }
    
    /**
     * 获取订单详情
     * @param orderNo 订单号
     * @return 订单详情
     */
    @Operation(summary = "获取订单详情", description = "根据订单号获取订单详情")
    @GetMapping("/{orderNo}")
    public ApiResponse<OrderDTO> getOrder(@Parameter(description = "订单号") @PathVariable String orderNo) {
        Order order = orderService.getOrderByOrderNo(orderNo);
        return ApiResponse.success(orderService.getUserOrders(order.getUserId(), null, PageRequest.of(0, 1))
            .getContent().stream().findFirst().orElse(null));
    }
    
    /**
     * 获取用户订单列表
     * @param user 当前登录用户
     * @param status 订单状态
     * @param page 页码
     * @param size 每页数量
     * @return 订单分页列表
     */
    @Operation(summary = "获取用户订单列表", description = "获取当前用户的订单列表")
    @GetMapping
    public ApiResponse<Page<OrderDTO>> getUserOrders(
            @AuthenticationPrincipal User user,
            @Parameter(description = "订单状态") @RequestParam(required = false) Integer status,
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createTime").descending());
        return ApiResponse.success(orderService.getUserOrders(user.getId(), status, pageable));
    }
    
    /**
     * 获取所有订单（管理员）
     * @param page 页码
     * @param size 每页数量
     * @return 订单分页列表
     */
    @Operation(summary = "获取所有订单", description = "管理员获取所有订单列表")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin")
    public ApiResponse<Page<OrderDTO>> getAllOrders(
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createTime").descending());
        return ApiResponse.success(orderService.getAllOrders(pageable));
    }
    
    /**
     * 发货（管理员）
     * @param orderNo 订单号
     * @return 操作结果
     */
    @Operation(summary = "发货", description = "管理员发货")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{orderNo}/deliver")
    public ApiResponse<Void> deliverOrder(@Parameter(description = "订单号") @PathVariable String orderNo) {
        orderService.deliverOrder(orderNo);
        return ApiResponse.success("发货成功", null);
    }
    
    /**
     * 更新订单状态（管理员）
     * @param orderNo 订单号
     * @param status 新状态
     * @return 操作结果
     */
    @Operation(summary = "更新订单状态", description = "管理员更新订单状态")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{orderNo}/status")
    public ApiResponse<Void> updateOrderStatus(
            @Parameter(description = "订单号") @PathVariable String orderNo,
            @Parameter(description = "新状态") @RequestParam Integer status) {
        orderService.updateOrderStatus(orderNo, status);
        return ApiResponse.success("更新成功", null);
    }
    
    /**
     * 获取订单统计（管理员）
     * @return 订单统计数据
     */
    @Operation(summary = "获取订单统计", description = "管理员获取订单统计数据")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/statistics")
    public ApiResponse<Map<String, Object>> getOrderStatistics() {
        return ApiResponse.success(orderService.getOrderStatistics());
    }
    
    /**
     * 删除已完成订单（管理员）
     * @param orderNo 订单号
     * @return 操作结果
     */
    @Operation(summary = "删除已完成订单", description = "管理员删除已完成的订单")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{orderNo}")
    public ApiResponse<Void> deleteCompletedOrder(@Parameter(description = "订单号") @PathVariable String orderNo) {
        orderService.deleteCompletedOrder(orderNo);
        return ApiResponse.success("删除成功", null);
    }
}
