package com.bookstore.mapper;

import com.bookstore.entity.Category;
import org.apache.ibatis.annotations.*;
import java.util.List;

/**
 * 分类数据访问层
 */
@Mapper
public interface CategoryMapper {
    
    /**
     * 根据ID查询分类
     */
    @Select("SELECT * FROM t_category WHERE id = #{id}")
    Category findById(@Param("id") Long id);
    
    /**
     * 查询所有启用的分类
     */
    @Select("SELECT * FROM t_category WHERE status = 1 ORDER BY sort")
    List<Category> findAllActive();
    
    /**
     * 查询所有分类
     */
    @Select("SELECT * FROM t_category ORDER BY sort")
    List<Category> findAll();
    
    /**
     * 创建分类
     */
    @Insert("INSERT INTO t_category(name, sort, icon, status, create_time, update_time) VALUES(#{name}, #{sort}, #{icon}, #{status}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Category category);
    
    /**
     * 更新分类
     */
    @Update("UPDATE t_category SET name=#{name}, sort=#{sort}, icon=#{icon}, status=#{status}, update_time=NOW() WHERE id=#{id}")
    int update(Category category);
    
    /**
     * 更新分类状态
     */
    @Update("UPDATE t_category SET status=#{status}, update_time=NOW() WHERE id=#{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
    
    /**
     * 删除分类
     */
    @Delete("DELETE FROM t_category WHERE id = #{id}")
    int deleteById(@Param("id") Long id);
}
