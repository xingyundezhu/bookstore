package com.bookstore.entity;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用户实体类
 */
@Data
public class User implements Serializable {
    
    /** 用户ID（主键） */
    private Long id;
    
    /** 用户名（唯一） */
    private String username;
    
    /** 密码（BCrypt加密） */
    private String password;
    
    /** 邮箱 */
    private String email;
    
    /** 手机号 */
    private String phone;
    
    /** 昵称 */
    private String nickname;
    
    /** 头像URL */
    private String avatar;
    
    /** 性别：0-未知，1-男，2-女 */
    private Integer gender;
    
    /** 生日 */
    private LocalDate birthday;
    
    /** 个人简介 */
    private String bio;
    
    /** 状态：0-禁用，1-启用 */
    private Integer status;
    
    /** 角色：USER-普通用户，ADMIN-管理员 */
    private String role;
    
    /** 创建时间 */
    private LocalDateTime createTime;
    
    /** 更新时间 */
    private LocalDateTime updateTime;
    
    /** 普通用户角色常量 */
    public static final String ROLE_USER = "USER";
    
    /** 管理员角色常量 */
    public static final String ROLE_ADMIN = "ADMIN";
    
    /**
     * 判断是否为管理员
     *
     * @return 是否为管理员
     */
    public boolean isAdmin() {
        return ROLE_ADMIN.equals(this.role);
    }
}
