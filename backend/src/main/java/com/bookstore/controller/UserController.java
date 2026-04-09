package com.bookstore.controller;

import com.bookstore.dto.ApiResponse;
import com.bookstore.entity.User;
import com.bookstore.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 用户管理
 */
@Tag(name = "用户管理", description = "用户列表、状态管理等管理员功能")
@RestController
@RequestMapping("/users")
public class UserController {
    
    private final UserService userService;
    
    /**
     * 构造函数注入UserService
     *
     * @param userService 用户服务
     */
    public UserController(UserService userService) {
        this.userService = userService;
    }
    
    @Operation(summary = "获取用户列表", description = "分页获取所有用户列表")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Page<User>> getUserList(
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createTime").descending());
        Page<User> users = userService.getAllUsers(pageable);
        users.forEach(u -> u.setPassword(null));
        return ApiResponse.success(users);
    }
    
    @Operation(summary = "获取用户详情", description = "根据ID获取用户详细信息")
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<User> getUserById(@Parameter(description = "用户ID") @PathVariable Long id) {
        User user = userService.getUserById(id);
        user.setPassword(null);
        return ApiResponse.success(user);
    }
    
    @Operation(summary = "更新用户状态", description = "启用或禁用用户")
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> updateUserStatus(
            @Parameter(description = "用户ID") @PathVariable Long id,
            @Parameter(description = "状态：1-启用，0-禁用") @RequestParam Integer status) {
        userService.updateUserStatus(id, status);
        return ApiResponse.success(status == 1 ? "用户已启用" : "用户已禁用", null);
    }
}
