package com.bookstore.entity;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单项实体类
 *
 * @author bookstore
 * @since 1.0
 */
@Data
public class OrderItem implements Serializable {
    
    /** 订单项ID（主键） */
    private Long id;
    
    /** 订单ID */
    private Long orderId;
    
    /** 图书ID */
    private Long bookId;
    
    /** 图书标题 */
    private String bookTitle;
    
    /** 图书封面 */
    private String bookCover;
    
    /** 购买价格 */
    private BigDecimal price;
    
    /** 购买数量 */
    private Integer quantity;
    
    /** 小计金额 */
    private BigDecimal subtotal;
    
    /** 总金额（别名，兼容性字段） */
    public BigDecimal getTotalAmount() {
        return subtotal;
    }
    
    public void setTotalAmount(BigDecimal totalAmount) {
        this.subtotal = totalAmount;
    }
    
    /** 创建时间 */
    private LocalDateTime createTime;
}
