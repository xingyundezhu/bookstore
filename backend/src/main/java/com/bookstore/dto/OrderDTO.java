package com.bookstore.dto;

import com.bookstore.entity.OrderItem;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单数据传输对象
 */
@Data
public class OrderDTO {
    
    /**
     * 订单ID
     */
    private Long id;
    
    /**
     * 订单号
     */
    private String orderNo;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 订单总金额
     */
    private BigDecimal totalAmount;
    
    /**
     * 实付金额
     */
    private BigDecimal payAmount;
    
    /**
     * 运费
     */
    private BigDecimal freight;
    
    /**
     * 优惠金额
     */
    private BigDecimal discount;
    
    /**
     * 订单状态
     */
    private Integer status;
    
    /**
     * 订单状态文本
     */
    private String statusText;
    
    /**
     * 支付方式
     */
    private Integer paymentType;
    
    /**
     * 支付方式文本
     */
    private String paymentTypeText;
    
    /**
     * 支付时间
     */
    private LocalDateTime paymentTime;
    
    /**
     * 发货时间
     */
    private LocalDateTime deliveryTime;
    
    /**
     * 收货时间
     */
    private LocalDateTime receiveTime;
    
    /**
     * 收货人姓名
     */
    private String receiver;
    
    /**
     * 收货人电话
     */
    private String receiverPhone;
    
    /**
     * 收货地址
     */
    private String receiverAddress;
    
    /**
     * 订单备注
     */
    private String remark;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    
    /**
     * 订单项列表
     */
    private List<OrderItem> orderItems;
}
