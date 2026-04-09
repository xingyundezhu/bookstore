package com.bookstore.controller;

import com.bookstore.dto.BookDTO;
import com.bookstore.dto.ApiResponse;
import com.bookstore.entity.User;
import com.bookstore.entity.UserBehavior;
import com.bookstore.service.RecommendationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 推荐控制器
 */
@Tag(name = "推荐系统", description = "个性化图书推荐功能")
@RestController
@RequestMapping("/recommendations")
public class RecommendationController {
    
    private final RecommendationService recommendationService;
    
    /**
     * 构造函数注入RecommendationService
     *
     * @param recommendationService 推荐服务
     */
    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }
    
    @Operation(summary = "获取个性化推荐", description = "根据用户行为获取个性化推荐图书")
    @GetMapping("/personalized")
    public ApiResponse<List<BookDTO>> getPersonalizedRecommendations(
            @AuthenticationPrincipal User user,
            @Parameter(description = "数量限制") @RequestParam(defaultValue = "10") int limit) {
        return ApiResponse.success(recommendationService.getPersonalizedRecommendations(user.getId(), limit));
    }
    
    @Operation(summary = "获取相似图书", description = "获取与指定图书相似的其他图书")
    @GetMapping("/similar/{bookId}")
    public ApiResponse<List<BookDTO>> getSimilarBooks(
            @Parameter(description = "图书ID") @PathVariable Long bookId,
            @Parameter(description = "数量限制") @RequestParam(defaultValue = "10") int limit) {
        return ApiResponse.success(recommendationService.getSimilarBooks(bookId, limit));
    }
    
    @Operation(summary = "获取热门推荐", description = "获取热门图书推荐")
    @GetMapping("/hot")
    public ApiResponse<List<BookDTO>> getHotRecommendations(
            @Parameter(description = "数量限制") @RequestParam(defaultValue = "10") int limit) {
        return ApiResponse.success(recommendationService.getHotRecommendations(limit));
    }
    
    @Operation(summary = "获取新书推荐", description = "获取新上架图书推荐")
    @GetMapping("/new")
    public ApiResponse<List<BookDTO>> getNewRecommendations(
            @Parameter(description = "数量限制") @RequestParam(defaultValue = "10") int limit) {
        return ApiResponse.success(recommendationService.getNewRecommendations(limit));
    }
    
    @Operation(summary = "记录用户行为", description = "记录用户对图书的行为（浏览、收藏、购买等）")
    @PostMapping("/behavior")
    public ApiResponse<Void> recordBehavior(
            @AuthenticationPrincipal User user,
            @Parameter(description = "图书ID") @RequestParam Long bookId,
            @Parameter(description = "行为类型：1-浏览，2-收藏，3-加购，4-购买，5-评价") @RequestParam Integer behaviorType) {
        recommendationService.recordUserBehavior(user.getId(), bookId, behaviorType);
        return ApiResponse.success("记录成功", null);
    }
    
    @Operation(summary = "浏览图书", description = "记录用户浏览图书行为")
    @PostMapping("/view/{bookId}")
    public ApiResponse<Void> viewBook(
            @AuthenticationPrincipal User user,
            @Parameter(description = "图书ID") @PathVariable Long bookId) {
        recommendationService.recordUserBehavior(user.getId(), bookId, UserBehavior.BEHAVIOR_VIEW);
        return ApiResponse.success(null);
    }
}
