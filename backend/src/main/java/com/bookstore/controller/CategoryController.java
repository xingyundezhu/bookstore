package com.bookstore.controller;

import com.bookstore.dto.ApiResponse;
import com.bookstore.entity.Category;
import com.bookstore.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 分类控制器
 */
@Tag(name = "分类管理", description = "图书分类浏览功能")
@RestController
@RequestMapping("/categories")
public class CategoryController {
    
    private final CategoryService categoryService;
    
    /**
     * 构造函数注入CategoryService
     *
     * @param categoryService 分类服务
     */
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }
    
    /**
     * 获取启用的分类列表
     *
     * @return 启用的分类列表
     */
    @Operation(summary = "获取启用的分类列表", description = "获取所有启用状态的分类列表")
    @GetMapping
    public ApiResponse<List<Category>> getActiveCategories() {
        return ApiResponse.success(categoryService.getActiveCategories());
    }
    
    /**
     * 获取所有分类
     *
     * @return 所有分类列表
     */
    @Operation(summary = "获取所有分类", description = "获取所有分类列表（包括禁用的）")
    @GetMapping("/all")
    public ApiResponse<List<Category>> getAllCategories() {
        return ApiResponse.success(categoryService.getAllCategories());
    }
    
    /**
     * 获取分类详情
     *
     * @param id 分类ID
     * @return 分类详情
     */
    @Operation(summary = "获取分类详情", description = "根据ID获取分类详情")
    @GetMapping("/{id}")
    public ApiResponse<Category> getCategory(@Parameter(description = "分类ID") @PathVariable Long id) {
        return ApiResponse.success(categoryService.getCategoryById(id));
    }
    
    /**
     * 添加分类
     *
     * @param category 分类信息
     * @return 添加后的分类
     */
    @Operation(summary = "添加分类", description = "添加新分类（管理员）")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ApiResponse<Category> createCategory(@RequestBody Category category) {
        return ApiResponse.success("添加成功", categoryService.createCategory(category));
    }
    
    /**
     * 更新分类
     *
     * @param id 分类ID
     * @param category 更新的分类信息
     * @return 更新后的分类
     */
    @Operation(summary = "更新分类", description = "更新分类信息（管理员）")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ApiResponse<Category> updateCategory(
            @Parameter(description = "分类ID") @PathVariable Long id, 
            @RequestBody Category category) {
        return ApiResponse.success("更新成功", categoryService.updateCategory(id, category));
    }
    
    /**
     * 启用分类
     *
     * @param id 分类ID
     * @return 操作结果
     */
    @Operation(summary = "启用分类", description = "启用分类（管理员）")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/enable")
    public ApiResponse<Void> enableCategory(@Parameter(description = "分类ID") @PathVariable Long id) {
        categoryService.enableCategory(id);
        return ApiResponse.success("启用成功", null);
    }
    
    /**
     * 禁用分类
     *
     * @param id 分类ID
     * @return 操作结果
     */
    @Operation(summary = "禁用分类", description = "禁用分类，如果分类下有上架书籍则无法禁用（管理员）")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/disable")
    public ApiResponse<Void> disableCategory(@Parameter(description = "分类ID") @PathVariable Long id) {
        categoryService.disableCategory(id);
        return ApiResponse.success("禁用成功", null);
    }
    
    /**
     * 删除分类
     *
     * @param id 分类ID
     * @return 操作结果
     */
    @Operation(summary = "删除分类", description = "删除分类，如果分类下有上架书籍则无法删除（管理员）")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteCategory(@Parameter(description = "分类ID") @PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ApiResponse.success("删除成功", null);
    }
}
