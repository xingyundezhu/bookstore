package com.bookstore.entity;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 图书实体类
 *
 * @author bookstore
 * @since 1.0
 */
@Data
public class Book implements Serializable {
    
    /** 图书ID（主键） */
    private Long id;
    
    /** 图书标题 */
    private String title;
    
    /** 作者 */
    private String author;
    
    /** 出版社 */
    private String publisher;
    
    /** 出版日期 */
    private LocalDate publishDate;
    
    /** ISBN编号 */
    private String isbn;
    
    /** 分类ID */
    private Long categoryId;
    
    /** 分类名称 */
    private String categoryName;
    
    /** 价格 */
    private BigDecimal price;
    
    /** 原价 */
    private BigDecimal originalPrice;
    
    /** 折扣 */
    private BigDecimal discount;
    
    /** 库存数量 */
    private Integer stock;
    
    /** 销量 */
    private Integer sales;
    
    /** 封面图片URL */
    private String coverImage;
    
    /** 图书图片列表 */
    private String images;
    
    /** 图书描述 */
    private String description;
    
    /** 页数 */
    private Integer pages;
    
    /** 语言 */
    private String language;
    
    /** 装帧 */
    private String binding;
    
    /** 状态：0-下架，1-上架 */
    private Integer status;
    
    /** 是否热门：0-否，1-是 */
    private Integer isHot;
    
    /** 是否新书：0-否，1-是 */
    private Integer isNew;
    
    /** 是否推荐：0-否，1-是 */
    private Integer isRecommend;
    
    /** 创建时间 */
    private LocalDateTime createTime;
    
    /** 更新时间 */
    private LocalDateTime updateTime;
    
    /** 图书状态常量 - 下架 */
    public static final int STATUS_OFF = 0;
    
    /** 图书状态常量 - 上架 */
    public static final int STATUS_ON = 1;
}
