package com.bookstore.service.impl;

import com.bookstore.entity.Category;
import com.bookstore.mapper.CategoryMapper;
import com.bookstore.mapper.BookMapper;
import com.bookstore.service.CategoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 分类服务实现类
 */
@Service
public class CategoryServiceImpl implements CategoryService {
    
    private final CategoryMapper categoryMapper;
    private final BookMapper bookMapper;
    
    /**
     * 构造函数注入依赖
     *
     * @param categoryMapper 分类数据访问层
     * @param bookMapper 图书数据访问层
     */
    public CategoryServiceImpl(CategoryMapper categoryMapper, BookMapper bookMapper) {
        this.categoryMapper = categoryMapper;
        this.bookMapper = bookMapper;
    }
    
    @Override
    public Category getCategoryById(Long id) {
        Category category = categoryMapper.findById(id);
        if (category == null) {
            throw new RuntimeException("分类不存在");
        }
        return category;
    }
    
    @Override
    public List<Category> getActiveCategories() {
        return categoryMapper.findAllActive();
    }
    
    @Override
    public List<Category> getAllCategories() {
        return categoryMapper.findAll();
    }
    
    @Override
    @Transactional
    public Category createCategory(Category category) {
        if (category.getSort() == null) {
            category.setSort(0);
        }
        if (category.getStatus() == null) {
            category.setStatus(1);
        }
        categoryMapper.insert(category);
        return category;
    }
    
    @Override
    @Transactional
    public Category updateCategory(Long id, Category category) {
        getCategoryById(id);
        category.setId(id);
        categoryMapper.update(category);
        return category;
    }
    
    @Override
    @Transactional
    public void enableCategory(Long id) {
        getCategoryById(id);
        categoryMapper.updateStatus(id, 1);
    }
    
    @Override
    @Transactional
    public void disableCategory(Long id) {
        getCategoryById(id);
        
        long activeBookCount = bookMapper.countActiveByCategory(id);
        if (activeBookCount > 0) {
            throw new RuntimeException("该分类下有 " + activeBookCount + " 本上架书籍，无法禁用");
        }
        
        categoryMapper.updateStatus(id, 0);
    }
    
    @Override
    @Transactional
    public void deleteCategory(Long id) {
        getCategoryById(id);
        
        long activeBookCount = bookMapper.countActiveByCategory(id);
        if (activeBookCount > 0) {
            throw new RuntimeException("该分类下有 " + activeBookCount + " 本上架书籍，无法删除");
        }
        
        categoryMapper.deleteById(id);
    }
}
