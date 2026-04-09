package com.bookstore.entity;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户行为实体类
 *
 * @author bookstore
 * @since 1.0
 */
@Data
public class UserBehavior implements Serializable {
    
    /** 行为ID（主键） */
    private Long id;
    
    /** 用户ID */
    private Long userId;
    
    /** 图书ID */
    private Long bookId;
    
    /** 行为类型：1-浏览，2-收藏，3-购买，4-加购，5-评价 */
    private Integer behaviorType;
    
    /** 行为分数 */
    private Double score;
    
    /** 创建时间 */
    private LocalDateTime createTime;
    
    /** 行为类型常量 - 浏览 */
    public static final int BEHAVIOR_VIEW = 1;
    
    /** 行为类型常量 - 收藏 */
    public static final int BEHAVIOR_FAVORITE = 2;
    
    /** 行为类型常量 - 购买 */
    public static final int BEHAVIOR_PURCHASE = 3;
    
    /** 行为类型常量 - 加购 */
    public static final int BEHAVIOR_CART = 4;
    
    /** 行为类型常量 - 评价 */
    public static final int BEHAVIOR_REVIEW = 5;
}
