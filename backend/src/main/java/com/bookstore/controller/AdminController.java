package com.bookstore.controller;

import com.bookstore.dto.ApiResponse;
import com.bookstore.dto.BookDTO;
import com.bookstore.entity.Review;
import com.bookstore.mapper.UserMapper;
import com.bookstore.service.BookService;
import com.bookstore.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 系统管理控制器
 *
 * @author bookstore
 * @since 1.0
 */
@Tag(name = "系统管理", description = "管理员专用系统管理接口")
@RestController
@RequestMapping("/admin")
public class AdminController {
    
    private final UserMapper userMapper;
    private final BookService bookService;
    private final ReviewService reviewService;
    
    /**
     * 构造函数注入依赖
     *
     * @param userMapper 用户数据访问层
     * @param bookService 图书服务
     * @param reviewService 评价服务
     */
    public AdminController(UserMapper userMapper, BookService bookService, ReviewService reviewService) {
        this.userMapper = userMapper;
        this.bookService = bookService;
        this.reviewService = reviewService;
    }
    
    /**
     * 更新用户角色
     *
     * @param id 用户ID
     * @param role 角色
     * @return 操作结果
     */
    @Operation(summary = "更新用户角色", description = "设置用户为管理员或普通用户")
    @PutMapping("/users/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> updateUserRole(
            @Parameter(description = "用户ID") @PathVariable Long id,
            @Parameter(description = "角色：ADMIN-管理员，USER-普通用户") @RequestParam String role) {
        userMapper.updateRole(id, role);
        return ApiResponse.success("角色更新成功", null);
    }
    
    /**
     * 获取管理员图书列表
     *
     * @param page 页码
     * @param size 每页数量
     * @return 图书分页列表
     */
    @Operation(summary = "获取图书列表", description = "管理员获取所有图书列表")
    @GetMapping("/books")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Page<BookDTO>> getAdminBooks(
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createTime"));
        return ApiResponse.success(bookService.getAdminBooks(pageable));
    }
    
    /**
     * 更新图书销量
     *
     * @param id 图书ID
     * @param sales 销量
     * @return 操作结果
     */
    @Operation(summary = "更新图书销量", description = "管理员手动更新图书销量")
    @PutMapping("/books/{id}/sales")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> updateBookSales(
            @Parameter(description = "图书ID") @PathVariable Long id,
            @Parameter(description = "销量") @RequestParam Integer sales) {
        bookService.updateBookSales(id, sales);
        return ApiResponse.success("销量更新成功", null);
    }
    
    /**
     * 获取销量统计数据
     *
     * @return 销量统计数据
     */
    @Operation(summary = "获取销量统计", description = "获取销量统计数据")
    @GetMapping("/sales/statistics")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Object> getSalesStatistics() {
        return ApiResponse.success(bookService.getSalesStatistics());
    }
    
    /**
     * 获取销量排行榜
     *
     * @param page 页码
     * @param size 每页数量
     * @param categoryId 分类ID
     * @return 销量排行榜
     */
    @Operation(summary = "获取销量排行榜", description = "获取图书销量排行榜")
    @GetMapping("/sales/ranking")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Page<BookDTO>> getSalesRanking(
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "分类ID") @RequestParam(required = false) Long categoryId) {
        return ApiResponse.success(bookService.getSalesRanking(page, size, categoryId));
    }
    
    /**
     * 获取所有评价列表
     *
     * @param page 页码
     * @param size 每页数量
     * @return 评价分页列表
     */
    @Operation(summary = "获取所有评价", description = "管理员获取所有评价列表")
    @GetMapping("/reviews")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Page<Review>> getAllReviews(
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createTime"));
        return ApiResponse.success(reviewService.getAllReviews(pageable));
    }
    
    /**
     * 删除评价
     *
     * @param id 评价ID
     * @return 操作结果
     */
    @Operation(summary = "删除评价", description = "管理员删除评价")
    @DeleteMapping("/reviews/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> deleteReview(@Parameter(description = "评价ID") @PathVariable Long id) {
        reviewService.adminDeleteReview(id);
        return ApiResponse.success("删除成功", null);
    }
}
