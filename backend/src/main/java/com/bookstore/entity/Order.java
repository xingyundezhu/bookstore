package com.bookstore.entity;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单实体类
 *
 * @author bookstore
 * @since 1.0
 */
@Data
public class Order implements Serializable {
    
    /** 订单ID（主键） */
    private Long id;
    
    /** 订单号 */
    private String orderNo;
    
    /** 用户ID */
    private Long userId;
    
    /** 收货人 */
    private String receiver;
    
    /** 收货电话 */
    private String receiverPhone;
    
    /** 收货地址 */
    private String receiverAddress;
    
    /** 商品总金额 */
    private BigDecimal totalAmount;
    
    /** 实付金额 */
    private BigDecimal payAmount;
    
    /** 运费 */
    private BigDecimal freight;
    
    /** 折扣金额 */
    private BigDecimal discount;
    
    /** 支付方式 */
    private Integer paymentType;
    
    /** 订单状态：0-待付款，1-待发货，2-待收货，3-已完成，4-已取消 */
    private Integer status;
    
    /** 订单备注 */
    private String remark;
    
    /** 创建时间 */
    private LocalDateTime createTime;
    
    /** 支付时间 */
    private LocalDateTime paymentTime;
    
    /** 发货时间 */
    private LocalDateTime deliveryTime;
    
    /** 收货时间 */
    private LocalDateTime receiveTime;
    
    /** 更新时间 */
    private LocalDateTime updateTime;
    
    /** 订单状态常量 - 待付款 */
    public static final int STATUS_PENDING = 0;
    
    /** 订单状态常量 - 待发货 */
    public static final int STATUS_PAID = 1;
    
    /** 订单状态常量 - 待收货 */
    public static final int STATUS_SHIPPED = 2;
    
    /** 订单状态常量 - 已完成 */
    public static final int STATUS_COMPLETED = 3;
    
    /** 订单状态常量 - 已取消 */
    public static final int STATUS_CANCELLED = 4;
    
    /**
     * 获取订单状态文本
     *
     * @return 状态文本
     */
    public String getStatusText() {
        switch (status) {
            case 0: return "待付款";
            case 1: return "待发货";
            case 2: return "待收货";
            case 3: return "已完成";
            case 4: return "已取消";
            default: return "未知";
        }
    }
}
