package com.bookstore.mapper;

import com.bookstore.entity.Address;
import org.apache.ibatis.annotations.*;
import java.util.List;

/**
 * 地址数据访问层
 */
@Mapper
public interface AddressMapper {
    
    /**
     * 根据ID查询地址
     */
    @Select("SELECT * FROM t_address WHERE id = #{id}")
    Address findById(@Param("id") Long id);
    
    /**
     * 查询用户所有地址（默认地址优先）
     */
    @Select("SELECT * FROM t_address WHERE user_id = #{userId} ORDER BY is_default DESC, create_time DESC")
    List<Address> findByUserId(@Param("userId") Long userId);
    
    /**
     * 查询用户默认地址
     */
    @Select("SELECT * FROM t_address WHERE user_id = #{userId} AND is_default = 1")
    Address findDefaultByUserId(@Param("userId") Long userId);
    
    /**
     * 创建地址
     */
    @Insert("INSERT INTO t_address(user_id, receiver, phone, province, city, district, detail, is_default, create_time, update_time) " +
            "VALUES(#{userId}, #{receiver}, #{phone}, #{province}, #{city}, #{district}, #{detail}, #{isDefault}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Address address);
    
    /**
     * 更新地址
     */
    @Update("UPDATE t_address SET receiver=#{receiver}, phone=#{phone}, province=#{province}, city=#{city}, district=#{district}, detail=#{detail}, is_default=#{isDefault}, update_time=NOW() WHERE id=#{id}")
    int update(Address address);
    
    /**
     * 清除用户默认地址
     */
    @Update("UPDATE t_address SET is_default = 0, update_time = NOW() WHERE user_id = #{userId}")
    int clearDefault(@Param("userId") Long userId);
    
    /**
     * 设置默认地址
     */
    @Update("UPDATE t_address SET is_default = 1, update_time = NOW() WHERE id = #{id}")
    int setDefault(@Param("id") Long id);
    
    /**
     * 删除地址
     */
    @Delete("DELETE FROM t_address WHERE id = #{id}")
    int deleteById(@Param("id") Long id);
}
