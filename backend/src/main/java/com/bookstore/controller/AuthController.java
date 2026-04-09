package com.bookstore.controller;

import com.bookstore.dto.ApiResponse;
import com.bookstore.dto.LoginRequest;
import com.bookstore.dto.LoginResponse;
import com.bookstore.dto.RegisterRequest;
import com.bookstore.entity.User;
import com.bookstore.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * 认证管理控制器
 *
 * @author bookstore
 * @since 1.0
 */
@Tag(name = "认证管理", description = "用户注册、登录、个人信息管理")
@RestController
@RequestMapping("/auth")
public class AuthController {
    
    private final UserService userService;
    
    /**
     * 构造函数注入UserService
     *
     * @param userService 用户服务
     */
    public AuthController(UserService userService) {
        this.userService = userService;
    }
    
    /**
     * 用户注册
     *
     * @param request 注册请求信息
     * @return 注册成功的用户信息
     */
    @Operation(summary = "用户注册", description = "新用户注册账号")
    @PostMapping("/register")
    public ApiResponse<User> register(@Valid @RequestBody RegisterRequest request) {
        User user = userService.register(request);
        user.setPassword(null);
        return ApiResponse.success("注册成功", user);
    }
    
    /**
     * 用户登录
     *
     * @param request 登录请求信息
     * @return 登录响应（包含token和用户信息）
     */
    @Operation(summary = "用户登录", description = "用户登录获取token")
    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = userService.login(request);
        return ApiResponse.success("登录成功", response);
    }
    
    /**
     * 获取当前用户信息
     *
     * @param user 当前登录用户
     * @return 用户信息
     */
    @Operation(summary = "获取用户信息", description = "获取当前登录用户的个人信息")
    @GetMapping("/me")
    public ApiResponse<User> getCurrentUser(@AuthenticationPrincipal User user) {
        user.setPassword(null);
        return ApiResponse.success(user);
    }
    
    /**
     * 更新用户信息
     *
     * @param user 当前登录用户
     * @param updateUser 更新的用户信息
     * @return 更新后的用户信息
     */
    @Operation(summary = "更新用户信息", description = "更新当前用户的个人信息")
    @PutMapping("/me")
    public ApiResponse<User> updateCurrentUser(@AuthenticationPrincipal User user, @RequestBody User updateUser) {
        User updatedUser = userService.updateUser(user.getId(), updateUser);
        updatedUser.setPassword(null);
        return ApiResponse.success("更新成功", updatedUser);
    }
    
    /**
     * 修改密码
     *
     * @param user 当前登录用户
     * @param oldPassword 原密码
     * @param newPassword 新密码
     * @return 操作结果
     */
    @Operation(summary = "修改密码", description = "修改当前用户密码")
    @PutMapping("/password")
    public ApiResponse<Void> updatePassword(
            @AuthenticationPrincipal User user,
            @Parameter(description = "原密码") @RequestParam String oldPassword,
            @Parameter(description = "新密码") @RequestParam String newPassword) {
        userService.updatePassword(user.getId(), oldPassword, newPassword);
        return ApiResponse.success("密码修改成功", null);
    }
    
    /**
     * 上传用户头像
     *
     * @param user 当前登录用户
     * @param file 头像图片文件
     * @return 头像访问URL
     */
    @Operation(summary = "上传头像", description = "上传用户头像图片")
    @PostMapping("/avatar")
    public ApiResponse<String> uploadAvatar(
            @AuthenticationPrincipal User user,
            @Parameter(description = "头像图片文件") @RequestParam("file") MultipartFile file) throws IOException {
        String avatarUrl = uploadFile(file, "avatars");
        userService.updateAvatar(user.getId(), avatarUrl);
        return ApiResponse.success("头像上传成功", avatarUrl);
    }
    
    /**
     * 上传文件到指定目录
     *
     * @param file 上传的文件
     * @param directory 目标目录
     * @return 文件访问URL
     */
    private String uploadFile(MultipartFile file, String directory) throws IOException {
        String uploadDir = "uploads/" + directory + "/";
        Path dirPath = Paths.get(uploadDir);
        if (!Files.exists(dirPath)) {
            Files.createDirectories(dirPath);
        }
        
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null && originalFilename.contains(".") 
                ? originalFilename.substring(originalFilename.lastIndexOf(".")) 
                : ".jpg";
        String filename = UUID.randomUUID().toString() + extension;
        
        Path filePath = dirPath.resolve(filename);
        file.transferTo(filePath.toFile());
        
        return "/uploads/" + directory + "/" + filename;
    }
    
    /**
     * 发送邮箱验证码
     *
     * @param email 邮箱地址
     * @return 发送结果
     */
    @Operation(summary = "发送邮箱验证码", description = "发送验证码到指定邮箱")
    @PostMapping("/email/code")
    public ApiResponse<Void> sendEmailCode(@Parameter(description = "邮箱地址") @RequestParam String email) {
        userService.sendEmailCode(email);
        return ApiResponse.success("验证码已发送", null);
    }
    
    /**
     * 发送手机验证码
     *
     * @param phone 手机号码
     * @return 发送结果
     */
    @Operation(summary = "发送手机验证码", description = "发送验证码到指定手机")
    @PostMapping("/phone/code")
    public ApiResponse<Void> sendPhoneCode(@Parameter(description = "手机号码") @RequestParam String phone) {
        userService.sendPhoneCode(phone);
        return ApiResponse.success("验证码已发送", null);
    }
    
    /**
     * 绑定邮箱
     *
     * @param user 当前登录用户
     * @param email 邮箱地址
     * @param code 验证码
     * @return 绑定结果
     */
    @Operation(summary = "绑定邮箱", description = "绑定用户邮箱")
    @PostMapping("/email/bind")
    public ApiResponse<Void> bindEmail(
            @AuthenticationPrincipal User user,
            @Parameter(description = "邮箱地址") @RequestParam String email,
            @Parameter(description = "验证码") @RequestParam String code) {
        userService.bindEmail(user.getId(), email, code);
        return ApiResponse.success("邮箱绑定成功", null);
    }
    
    /**
     * 绑定手机
     *
     * @param user 当前登录用户
     * @param phone 手机号码
     * @param code 验证码
     * @return 绑定结果
     */
    @Operation(summary = "绑定手机", description = "绑定用户手机")
    @PostMapping("/phone/bind")
    public ApiResponse<Void> bindPhone(
            @AuthenticationPrincipal User user,
            @Parameter(description = "手机号码") @RequestParam String phone,
            @Parameter(description = "验证码") @RequestParam String code) {
        userService.bindPhone(user.getId(), phone, code);
        return ApiResponse.success("手机绑定成功", null);
    }
}
