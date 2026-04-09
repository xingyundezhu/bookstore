package com.bookstore.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 订单项数据传输对象
 */
@Data
public class OrderItemDTO {
    
    private Long id;
    private Long bookId;
    private String bookTitle;
    private String bookCover;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal totalAmount;
}
