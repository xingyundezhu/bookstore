package com.bookstore.entity;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 购物车实体类
 *
 * @author bookstore
 * @since 1.0
 */
@Data
public class Cart implements Serializable {
    
    /** 购物车ID（主键） */
    private Long id;
    
    /** 用户ID */
    private Long userId;
    
    /** 图书ID */
    private Long bookId;
    
    /** 图书标题 */
    private String bookTitle;
    
    /** 图书封面 */
    private String bookCover;
    
    /** 图书价格 */
    private BigDecimal price;
    
    /** 数量 */
    private Integer quantity;
    
    /** 图书库存 */
    private Integer stock;
    
    /** 是否选中：0-否，1-是 */
    private Integer selected;
    
    /** 创建时间 */
    private LocalDateTime createTime;
    
    /** 更新时间 */
    private LocalDateTime updateTime;
}
