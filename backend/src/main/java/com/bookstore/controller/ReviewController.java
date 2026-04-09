package com.bookstore.controller;

import com.bookstore.dto.ApiResponse;
import com.bookstore.entity.Review;
import com.bookstore.entity.User;
import com.bookstore.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * 评价管理
 */
@Tag(name = "评价管理", description = "图书评价功能")
@RestController
@RequestMapping("/reviews")
public class ReviewController {
    
    private final ReviewService reviewService;
    
    /**
     * 构造函数注入ReviewService
     *
     * @param reviewService 评价服务
     */
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }
    
    @Operation(summary = "创建评价", description = "对图书进行评价")
    @PostMapping
    public ApiResponse<Review> createReview(
            @AuthenticationPrincipal User user,
            @Parameter(description = "图书ID") @RequestParam Long bookId,
            @Parameter(description = "订单ID（可选）") @RequestParam(required = false) Long orderId,
            @Parameter(description = "评分1-5") @RequestParam Integer rating,
            @Parameter(description = "评价内容") @RequestParam(required = false) String content,
            @Parameter(description = "评价图片") @RequestParam(required = false) String images,
            @Parameter(description = "是否匿名") @RequestParam(defaultValue = "false") Boolean isAnonymous) {
        Review review = reviewService.createReview(user.getId(), bookId, orderId, rating, content, images, isAnonymous ? 1 : 0);
        return ApiResponse.success("评价成功", review);
    }
    
    @Operation(summary = "更新评价", description = "修改已发表的评价")
    @PutMapping("/{id}")
    public ApiResponse<Review> updateReview(
            @Parameter(description = "评价ID") @PathVariable Long id,
            @Parameter(description = "评分1-5") @RequestParam(required = false) Integer rating,
            @Parameter(description = "评价内容") @RequestParam(required = false) String content,
            @Parameter(description = "评价图片") @RequestParam(required = false) String images) {
        return ApiResponse.success("更新成功", reviewService.updateReview(id, rating, content, images));
    }
    
    @Operation(summary = "删除评价", description = "删除已发表的评价")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteReview(@Parameter(description = "评价ID") @PathVariable Long id) {
        reviewService.deleteReview(id);
        return ApiResponse.success("删除成功", null);
    }
    
    @Operation(summary = "管理员删除评价", description = "管理员删除任意评价")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/admin/{id}")
    public ApiResponse<Void> adminDeleteReview(@Parameter(description = "评价ID") @PathVariable Long id) {
        reviewService.deleteReview(id);
        return ApiResponse.success("删除成功", null);
    }
    
    @Operation(summary = "管理员获取所有评价", description = "管理员获取所有评价列表")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin")
    public ApiResponse<Page<Review>> getAllReviews(
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "7") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createTime").descending());
        return ApiResponse.success(reviewService.getAllReviews(pageable));
    }
    
    @Operation(summary = "获取图书评价列表", description = "获取指定图书的所有评价")
    @GetMapping("/book/{bookId}")
    public ApiResponse<Page<Review>> getBookReviews(
            @Parameter(description = "图书ID") @PathVariable Long bookId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createTime").descending());
        return ApiResponse.success(reviewService.getReviewsByBookId(bookId, pageable));
    }
    
    @Operation(summary = "获取用户评价列表", description = "获取当前用户发表的所有评价")
    @GetMapping("/user")
    public ApiResponse<Page<Review>> getUserReviews(
            @AuthenticationPrincipal User user,
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createTime").descending());
        return ApiResponse.success(reviewService.getReviewsByUserId(user.getId(), pageable));
    }
    
    @Operation(summary = "获取图书平均评分", description = "获取指定图书的平均评分")
    @GetMapping("/book/{bookId}/rating")
    public ApiResponse<Double> getAverageRating(@Parameter(description = "图书ID") @PathVariable Long bookId) {
        return ApiResponse.success(reviewService.getAverageRating(bookId));
    }
    
    @Operation(summary = "获取图书评价数量", description = "获取指定图书的评价数量")
    @GetMapping("/book/{bookId}/count")
    public ApiResponse<Long> getReviewCount(@Parameter(description = "图书ID") @PathVariable Long bookId) {
        return ApiResponse.success(reviewService.getReviewCount(bookId));
    }
    
    @Operation(summary = "检查是否已评价", description = "检查用户是否已对某订单进行评价")
    @GetMapping("/check")
    public ApiResponse<Boolean> hasReviewed(
            @AuthenticationPrincipal User user,
            @Parameter(description = "订单ID") @RequestParam Long orderId) {
        return ApiResponse.success(reviewService.hasReviewed(user.getId(), orderId));
    }
}
