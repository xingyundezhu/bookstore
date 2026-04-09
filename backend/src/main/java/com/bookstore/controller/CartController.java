package com.bookstore.controller;

import com.bookstore.dto.ApiResponse;
import com.bookstore.dto.CartDTO;
import com.bookstore.entity.Cart;
import com.bookstore.entity.User;
import com.bookstore.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 购物车管理控制器
 * 提供购物车的增删改查功能
 */
@Tag(name = "购物车管理", description = "购物车管理，包括添加、修改、删除商品")
@RestController
@RequestMapping("/cart")
public class CartController {
    
    private final CartService cartService;
    
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }
    
    /**
     * 获取当前用户的购物车
     * @param user 当前登录用户
     * @return 购物车商品列表
     */
    @Operation(summary = "获取购物车", description = "获取当前用户的购物车列表")
    @GetMapping
    public ApiResponse<List<CartDTO>> getCart(@AuthenticationPrincipal User user) {
        return ApiResponse.success(cartService.getCartByUserId(user.getId()));
    }
    
    /**
     * 添加商品到购物车
     * @param user 当前登录用户
     * @param bookId 图书ID
     * @param quantity 数量
     * @return 购物车项
     */
    @Operation(summary = "添加到购物车", description = "将商品添加到购物车")
    @PostMapping
    public ApiResponse<Cart> addToCart(
            @AuthenticationPrincipal User user,
            @Parameter(description = "图书ID") @RequestParam Long bookId,
            @Parameter(description = "数量") @RequestParam(defaultValue = "1") Integer quantity) {
        return ApiResponse.success("添加成功", cartService.addToCart(user.getId(), bookId, quantity));
    }
    
    /**
     * 更新购物车商品数量
     * @param user 当前登录用户
     * @param bookId 图书ID
     * @param quantity 新数量
     * @return 操作结果
     */
    @Operation(summary = "更新购物车数量", description = "修改购物车中商品的数量")
    @PutMapping("/{bookId}")
    public ApiResponse<Void> updateQuantity(
            @AuthenticationPrincipal User user,
            @Parameter(description = "图书ID") @PathVariable Long bookId,
            @Parameter(description = "新数量") @RequestParam Integer quantity) {
        cartService.updateQuantity(user.getId(), bookId, quantity);
        return ApiResponse.success("更新成功", null);
    }
    
    /**
     * 删除购物车商品
     * @param user 当前登录用户
     * @param bookId 图书ID
     * @return 操作结果
     */
    @Operation(summary = "删除购物车商品", description = "从购物车中删除指定商品")
    @DeleteMapping("/{bookId}")
    public ApiResponse<Void> removeFromCart(
            @AuthenticationPrincipal User user,
            @Parameter(description = "图书ID") @PathVariable Long bookId) {
        cartService.removeFromCart(user.getId(), bookId);
        return ApiResponse.success("删除成功", null);
    }
    
    /**
     * 清空购物车
     * @param user 当前登录用户
     * @return 操作结果
     */
    @Operation(summary = "清空购物车", description = "清空当前用户的购物车")
    @DeleteMapping
    public ApiResponse<Void> clearCart(@AuthenticationPrincipal User user) {
        cartService.clearCart(user.getId());
        return ApiResponse.success("清空成功", null);
    }
    
    /**
     * 设置商品选中状态
     * @param user 当前登录用户
     * @param bookId 图书ID
     * @param selected 选中状态
     * @return 操作结果
     */
    @Operation(summary = "选中/取消选中商品", description = "设置购物车中商品的选中状态")
    @PutMapping("/{bookId}/selected")
    public ApiResponse<Void> updateSelected(
            @AuthenticationPrincipal User user,
            @Parameter(description = "图书ID") @PathVariable Long bookId,
            @Parameter(description = "选中状态：1-选中，0-取消") @RequestParam Integer selected) {
        cartService.updateSelected(user.getId(), bookId, selected);
        return ApiResponse.success("更新成功", null);
    }
    
    /**
     * 全选/取消全选
     * @param user 当前登录用户
     * @param selected 选中状态
     * @return 操作结果
     */
    @Operation(summary = "全选/取消全选", description = "设置购物车所有商品的选中状态")
    @PutMapping("/selected")
    public ApiResponse<Void> updateAllSelected(
            @AuthenticationPrincipal User user,
            @Parameter(description = "选中状态：1-全选，0-取消全选") @RequestParam Integer selected) {
        cartService.updateAllSelected(user.getId(), selected);
        return ApiResponse.success("更新成功", null);
    }
    
    /**
     * 删除选中的商品
     * @param user 当前登录用户
     * @return 操作结果
     */
    @Operation(summary = "删除选中商品", description = "删除购物车中所有选中的商品")
    @DeleteMapping("/selected")
    public ApiResponse<Void> deleteSelected(@AuthenticationPrincipal User user) {
        cartService.deleteSelectedItems(user.getId());
        return ApiResponse.success("删除成功", null);
    }
    
    /**
     * 获取购物车商品数量
     * @param user 当前登录用户
     * @return 商品数量
     */
    @Operation(summary = "获取购物车数量", description = "获取购物车中商品的总数量")
    @GetMapping("/count")
    public ApiResponse<Long> getCartCount(@AuthenticationPrincipal User user) {
        return ApiResponse.success(cartService.getCartCount(user.getId()));
    }
}
