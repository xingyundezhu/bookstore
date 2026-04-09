package com.bookstore.entity;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 图书评分实体类
 */
@Data
public class BookRating implements Serializable {
    
    /** 评分ID（主键） */
    private Long id;
    
    /** 图书ID */
    private Long bookId;
    
    /** 平均评分 */
    private BigDecimal avgRating;
    
    /** 评价数量 */
    private Integer reviewCount;
    
    /** 更新时间 */
    private LocalDateTime updateTime;
}
