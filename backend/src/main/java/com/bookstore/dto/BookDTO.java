package com.bookstore.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 图书数据传输对象
 */
@Data
public class BookDTO {
    
    /**
     * 图书ID
     */
    private Long id;
    
    /**
     * 书名
     */
    private String title;
    
    /**
     * 作者
     */
    private String author;
    
    /**
     * 出版社
     */
    private String publisher;
    
    /**
     * 出版日期
     */
    private LocalDate publishDate;
    
    /**
     * ISBN编号
     */
    private String isbn;
    
    /**
     * 分类ID
     */
    private Long categoryId;
    
    /**
     * 分类名称
     */
    private String categoryName;
    
    /**
     * 现价
     */
    private BigDecimal price;
    
    /**
     * 原价
     */
    private BigDecimal originalPrice;
    
    /**
     * 折扣
     */
    private BigDecimal discount;
    
    /**
     * 库存数量
     */
    private Integer stock;
    
    /**
     * 销量
     */
    private Integer sales;
    
    /**
     * 封面图片URL
     */
    private String coverImage;
    
    /**
     * 详情图片
     */
    private String images;
    
    /**
     * 图书描述
     */
    private String description;
    
    /**
     * 页数
     */
    private Integer pages;
    
    /**
     * 语言
     */
    private String language;
    
    /**
     * 装帧方式
     */
    private String binding;
    
    /**
     * 状态
     */
    private Integer status;
    
    /**
     * 是否热门
     */
    private Integer isHot;
    
    /**
     * 是否新书
     */
    private Integer isNew;
    
    /**
     * 是否推荐
     */
    private Integer isRecommend;
    
    /**
     * 平均评分
     */
    private BigDecimal avgRating;
    
    /**
     * 评价数量
     */
    private Integer ratingCount;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
