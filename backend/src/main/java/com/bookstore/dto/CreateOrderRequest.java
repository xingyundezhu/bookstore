package com.bookstore.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 创建订单请求DTO
 */
@Data
public class CreateOrderRequest {
    
    /**
     * 收货地址ID
     */
    @NotNull(message = "收货地址ID不能为空")
    private Long addressId;
    
    /**
     * 支付方式（默认1-支付宝）
     */
    private Integer paymentType = 1;
    
    /**
     * 订单备注
     */
    private String remark;
    
    /**
     * 购物车ID列表（可选，不传则使用选中的购物车商品）
     */
    private List<Long> cartIds;
}
