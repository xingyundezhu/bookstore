package com.bookstore.controller;

import com.bookstore.dto.ApiResponse;
import com.bookstore.dto.BookDTO;
import com.bookstore.entity.User;
import com.bookstore.service.FavoriteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * 收藏控制器
 *
 * @author bookstore
 * @since 1.0
 */
@Tag(name = "收藏管理", description = "图书收藏功能")
@RestController
@RequestMapping("/favorites")
public class FavoriteController {
    
    private final FavoriteService favoriteService;
    
    /**
     * 构造函数注入FavoriteService
     *
     * @param favoriteService 收藏服务
     */
    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }
    
    /**
     * 添加收藏
     *
     * @param user 当前用户
     * @param bookId 图书ID
     * @return 操作结果
     */
    @Operation(summary = "添加收藏", description = "收藏指定图书")
    @PostMapping("/{bookId}")
    public ApiResponse<Void> addFavorite(
            @AuthenticationPrincipal User user,
            @Parameter(description = "图书ID") @PathVariable Long bookId) {
        favoriteService.addFavorite(user.getId(), bookId);
        return ApiResponse.success("收藏成功", null);
    }
    
    /**
     * 取消收藏
     *
     * @param user 当前用户
     * @param bookId 图书ID
     * @return 操作结果
     */
    @Operation(summary = "取消收藏", description = "取消收藏指定图书")
    @DeleteMapping("/{bookId}")
    public ApiResponse<Void> removeFavorite(
            @AuthenticationPrincipal User user,
            @Parameter(description = "图书ID") @PathVariable Long bookId) {
        favoriteService.removeFavorite(user.getId(), bookId);
        return ApiResponse.success("已取消收藏", null);
    }
    
    /**
     * 获取收藏列表
     *
     * @param user 当前用户
     * @param page 页码
     * @param size 每页数量
     * @return 收藏的图书列表
     */
    @Operation(summary = "获取收藏列表", description = "获取用户收藏的图书列表")
    @GetMapping
    public ApiResponse<Page<BookDTO>> getFavorites(
            @AuthenticationPrincipal User user,
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "12") int size) {
        return ApiResponse.success(favoriteService.getUserFavorites(user.getId(), page, size));
    }
    
    /**
     * 检查收藏状态
     *
     * @param user 当前用户
     * @param bookId 图书ID
     * @return 是否已收藏
     */
    @Operation(summary = "检查收藏状态", description = "检查是否已收藏某图书")
    @GetMapping("/check/{bookId}")
    public ApiResponse<Boolean> checkFavorite(
            @AuthenticationPrincipal User user,
            @Parameter(description = "图书ID") @PathVariable Long bookId) {
        return ApiResponse.success(favoriteService.isFavorited(user.getId(), bookId));
    }
    
    /**
     * 获取收藏数量
     *
     * @param user 当前用户
     * @return 收藏数量
     */
    @Operation(summary = "获取收藏数量", description = "获取用户收藏的图书数量")
    @GetMapping("/count")
    public ApiResponse<Long> getFavoriteCount(@AuthenticationPrincipal User user) {
        return ApiResponse.success(favoriteService.getFavoriteCount(user.getId()));
    }
}
