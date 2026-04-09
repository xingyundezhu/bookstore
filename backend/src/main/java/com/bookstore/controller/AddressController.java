package com.bookstore.controller;

import com.bookstore.dto.ApiResponse;
import com.bookstore.entity.Address;
import com.bookstore.entity.User;
import com.bookstore.service.AddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 地址控制器
 */
@Tag(name = "地址管理", description = "收货地址管理功能")
@RestController
@RequestMapping("/addresses")
public class AddressController {
    
    private final AddressService addressService;
    
    /**
     * 构造函数注入AddressService
     *
     * @param addressService 地址服务
     */
    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }
    
    @Operation(summary = "获取用户地址列表", description = "获取当前用户的所有收货地址")
    @GetMapping
    public ApiResponse<List<Address>> getAddresses(@AuthenticationPrincipal User user) {
        return ApiResponse.success(addressService.getAddressesByUserId(user.getId()));
    }
    
    @Operation(summary = "获取地址详情", description = "根据ID获取地址详情")
    @GetMapping("/{id}")
    public ApiResponse<Address> getAddress(@Parameter(description = "地址ID") @PathVariable Long id) {
        return ApiResponse.success(addressService.getAddressById(id));
    }
    
    @Operation(summary = "添加地址", description = "添加新的收货地址")
    @PostMapping
    public ApiResponse<Address> createAddress(
            @AuthenticationPrincipal User user,
            @RequestBody Address address) {
        return ApiResponse.success("添加成功", addressService.createAddress(user.getId(), address));
    }
    
    @Operation(summary = "更新地址", description = "更新收货地址信息")
    @PutMapping("/{id}")
    public ApiResponse<Address> updateAddress(
            @AuthenticationPrincipal User user,
            @Parameter(description = "地址ID") @PathVariable Long id,
            @RequestBody Address address) {
        return ApiResponse.success("更新成功", addressService.updateAddress(user.getId(), id, address));
    }
    
    @Operation(summary = "删除地址", description = "删除收货地址")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteAddress(
            @AuthenticationPrincipal User user,
            @Parameter(description = "地址ID") @PathVariable Long id) {
        addressService.deleteAddress(user.getId(), id);
        return ApiResponse.success("删除成功", null);
    }
    
    @Operation(summary = "设为默认地址", description = "将指定地址设为默认收货地址")
    @PutMapping("/{id}/default")
    public ApiResponse<Void> setDefaultAddress(
            @AuthenticationPrincipal User user,
            @Parameter(description = "地址ID") @PathVariable Long id) {
        addressService.setDefaultAddress(user.getId(), id);
        return ApiResponse.success("设置成功", null);
    }
}
