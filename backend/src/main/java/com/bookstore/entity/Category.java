package com.bookstore.entity;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 分类实体类
 */
@Data
public class Category implements Serializable {
    
    /** 分类ID（主键） */
    private Long id;
    
    /** 分类名称 */
    private String name;
    
    /** 排序号 */
    private Integer sort;
    
    /** 分类图标 */
    private String icon;
    
    /** 状态：0-禁用，1-启用 */
    private Integer status;
    
    /** 创建时间 */
    private LocalDateTime createTime;
    
    /** 更新时间 */
    private LocalDateTime updateTime;
}
