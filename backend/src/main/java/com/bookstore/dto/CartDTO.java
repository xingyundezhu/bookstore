package com.bookstore.dto;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 购物车数据传输对象
 */
@Data
public class CartDTO {
    
    /**
     * 购物车ID
     */
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 图书ID
     */
    private Long bookId;
    
    /**
     * 图书标题
     */
    private String bookTitle;
    
    /**
     * 图书封面
     */
    private String bookCover;
    
    /**
     * 商品单价
     */
    private BigDecimal price;
    
    /**
     * 商品数量
     */
    private Integer quantity;
    
    /**
     * 小计金额
     */
    private BigDecimal subtotal;
    
    /**
     * 是否选中
     */
    private Integer selected;
    
    /**
     * 图书库存
     */
    private Integer stock;
}
