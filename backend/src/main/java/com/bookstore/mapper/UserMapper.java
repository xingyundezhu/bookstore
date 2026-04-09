package com.bookstore.mapper;

import com.bookstore.entity.User;
import org.apache.ibatis.annotations.*;
import java.util.List;

/**
 * 用户数据访问层
 */
@Mapper
public interface UserMapper {
    
    /**
     * 根据ID查询用户
     */
    @Select("SELECT * FROM t_user WHERE id = #{id}")
    User findById(@Param("id") Long id);
    
    /**
     * 根据用户名查询用户
     */
    @Select("SELECT * FROM t_user WHERE username = #{username}")
    User findByUsername(@Param("username") String username);
    
    /**
     * 根据邮箱查询用户
     */
    @Select("SELECT * FROM t_user WHERE email = #{email}")
    User findByEmail(@Param("email") String email);
    
    /**
     * 根据手机号查询用户
     */
    @Select("SELECT * FROM t_user WHERE phone = #{phone}")
    User findByPhone(@Param("phone") String phone);
    
    /**
     * 插入新用户
     */
    @Insert("INSERT INTO t_user(username, password, email, phone, nickname, avatar, gender, birthday, status, role, create_time, update_time) " +
            "VALUES(#{username}, #{password}, #{email}, #{phone}, #{nickname}, #{avatar}, #{gender}, #{birthday}, #{status}, #{role}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);
    
    /**
     * 更新用户基本信息
     */
    @Update("UPDATE t_user SET nickname=#{nickname}, phone=#{phone}, email=#{email}, avatar=#{avatar}, gender=#{gender}, birthday=#{birthday}, bio=#{bio}, update_time=NOW() WHERE id=#{id}")
    int update(User user);
    
    /**
     * 更新用户密码
     */
    @Update("UPDATE t_user SET password=#{password}, update_time=NOW() WHERE id=#{id}")
    int updatePassword(@Param("id") Long id, @Param("password") String password);
    
    /**
     * 更新用户头像
     */
    @Update("UPDATE t_user SET avatar=#{avatar}, update_time=NOW() WHERE id=#{id}")
    int updateAvatar(@Param("id") Long id, @Param("avatar") String avatar);
    
    /**
     * 更新用户角色
     */
    @Update("UPDATE t_user SET role=#{role}, update_time=NOW() WHERE id=#{id}")
    int updateRole(@Param("id") Long id, @Param("role") String role);
    
    /**
     * 查询所有用户
     */
    @Select("SELECT * FROM t_user ORDER BY create_time DESC")
    List<User> findAll();
    
    /**
     * 根据状态查询用户
     */
    @Select("SELECT * FROM t_user WHERE status = #{status} ORDER BY create_time DESC")
    List<User> findByStatus(@Param("status") Integer status);
    
    /**
     * 更新用户状态
     */
    @Update("UPDATE t_user SET status=#{status}, update_time=NOW() WHERE id=#{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
    
    /**
     * 统计用户总数
     */
    @Select("SELECT COUNT(*) FROM t_user")
    long count();
}
