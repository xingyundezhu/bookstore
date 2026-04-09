package com.bookstore.entity;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 评价实体类
 *
 * @author bookstore
 * @since 1.0
 */
@Data
public class Review implements Serializable {
    
    /** 评价ID（主键） */
    private Long id;
    
    /** 用户ID */
    private Long userId;
    
    /** 图书ID */
    private Long bookId;
    
    /** 订单ID */
    private Long orderId;
    
    /** 评分（1-5） */
    private Integer rating;
    
    /** 评价内容 */
    private String content;
    
    /** 评价图片 */
    private String images;
    
    /** 是否匿名 */
    private Boolean isAnonymous;
    
    /** 状态：0-待审核，1-已审核 */
    private Integer status;
    
    /** 创建时间 */
    private LocalDateTime createTime;
    
    /** 更新时间 */
    private LocalDateTime updateTime;
    
    /** 图书标题（关联查询） */
    private String bookTitle;
    
    /** 图书封面（关联查询） */
    private String bookCover;
    
    /** 用户昵称（关联查询） */
    private String userNickname;
    
    /** 用户头像（关联查询） */
    private String userAvatar;
}
