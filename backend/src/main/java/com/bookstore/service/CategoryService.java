package com.bookstore.service;

import com.bookstore.entity.Category;
import java.util.List;

/**
 * 分类服务接口
 */
public interface CategoryService {
    
    /**
     * 根据ID获取分类
     *
     * @param id 分类ID
     * @return 分类实体
     */
    Category getCategoryById(Long id);
    
    /**
     * 获取所有启用的分类列表
     *
     * @return 分类列表
     */
    List<Category> getActiveCategories();
    
    /**
     * 获取所有分类列表（包括禁用的）
     *
     * @return 分类列表
     */
    List<Category> getAllCategories();
    
    /**
     * 创建分类
     *
     * @param category 分类信息
     * @return 创建后的分类实体
     */
    Category createCategory(Category category);
    
    /**
     * 更新分类
     *
     * @param id 分类ID
     * @param category 更新的分类信息
     * @return 更新后的分类实体
     */
    Category updateCategory(Long id, Category category);
    
    /**
     * 启用分类
     *
     * @param id 分类ID
     */
    void enableCategory(Long id);
    
    /**
     * 禁用分类
     *
     * @param id 分类ID
     */
    void disableCategory(Long id);
    
    /**
     * 删除分类
     *
     * @param id 分类ID
     */
    void deleteCategory(Long id);
}
