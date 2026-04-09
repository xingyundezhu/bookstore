package com.bookstore.entity;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 收货地址实体类
 */
@Data
public class Address implements Serializable {
    
    /** 地址ID（主键） */
    private Long id;
    
    /** 用户ID */
    private Long userId;
    
    /** 收货人 */
    private String receiver;
    
    /** 联系电话 */
    private String phone;
    
    /** 省份 */
    private String province;
    
    /** 城市 */
    private String city;
    
    /** 区县 */
    private String district;
    
    /** 详细地址 */
    private String detail;
    
    /** 是否默认地址 */
    private Boolean isDefault;
    
    /** 创建时间 */
    private LocalDateTime createTime;
    
    /** 更新时间 */
    private LocalDateTime updateTime;
}
