package com.bookstore.service;

import com.bookstore.dto.LoginRequest;
import com.bookstore.dto.LoginResponse;
import com.bookstore.dto.RegisterRequest;
import com.bookstore.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 用户服务接口
 */
public interface UserService {
    
    /**
     * 用户注册
     *
     * @param request 注册请求信息
     * @return 注册成功的用户实体
     */
    User register(RegisterRequest request);
    
    /**
     * 用户登录
     *
     * @param request 登录请求信息
     * @return 登录响应，包含JWT令牌
     */
    LoginResponse login(LoginRequest request);
    
    /**
     * 根据ID获取用户
     *
     * @param id 用户ID
     * @return 用户实体
     */
    User getUserById(Long id);
    
    /**
     * 根据用户名获取用户
     *
     * @param username 用户名
     * @return 用户实体
     */
    User getUserByUsername(String username);
    
    /**
     * 更新用户信息
     *
     * @param id 用户ID
     * @param user 更新的用户信息
     * @return 更新后的用户实体
     */
    User updateUser(Long id, User user);
    
    /**
     * 修改密码
     *
     * @param id 用户ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     */
    void updatePassword(Long id, String oldPassword, String newPassword);
    
    /**
     * 更新头像
     *
     * @param id 用户ID
     * @param avatar 头像URL
     */
    void updateAvatar(Long id, String avatar);
    
    /**
     * 分页获取所有用户
     *
     * @param pageable 分页参数
     * @return 用户分页列表
     */
    Page<User> getAllUsers(Pageable pageable);
    
    /**
     * 更新用户状态
     *
     * @param id 用户ID
     * @param status 状态：1-启用，0-禁用
     */
    void updateUserStatus(Long id, Integer status);
    
    /**
     * 发送邮箱验证码
     *
     * @param email 邮箱地址
     */
    void sendEmailCode(String email);
    
    /**
     * 发送手机验证码
     *
     * @param phone 手机号码
     */
    void sendPhoneCode(String phone);
    
    /**
     * 绑定邮箱
     *
     * @param userId 用户ID
     * @param email 邮箱地址
     * @param code 验证码
     * @return 更新后的用户实体
     */
    User bindEmail(Long userId, String email, String code);
    
    /**
     * 绑定手机
     *
     * @param userId 用户ID
     * @param phone 手机号码
     * @param code 验证码
     * @return 更新后的用户实体
     */
    User bindPhone(Long userId, String phone, String code);
}
