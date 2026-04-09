package com.bookstore.service;

import com.bookstore.entity.Address;

import java.util.List;

/**
 * 地址服务接口
 */
public interface AddressService {
    
    /**
     * 创建收货地址
     *
     * @param userId 用户ID
     * @param address 地址信息
     * @return 创建后的地址实体
     */
    Address createAddress(Long userId, Address address);
    
    /**
     * 更新收货地址
     *
     * @param userId 用户ID
     * @param id 地址ID
     * @param address 更新的地址信息
     * @return 更新后的地址实体
     */
    Address updateAddress(Long userId, Long id, Address address);
    
    /**
     * 删除收货地址
     *
     * @param userId 用户ID
     * @param id 地址ID
     */
    void deleteAddress(Long userId, Long id);
    
    /**
     * 根据ID获取地址
     *
     * @param id 地址ID
     * @return 地址实体
     */
    Address getAddressById(Long id);
    
    /**
     * 获取用户所有地址
     *
     * @param userId 用户ID
     * @return 地址列表
     */
    List<Address> getAddressesByUserId(Long userId);
    
    /**
     * 设置默认地址
     *
     * @param userId 用户ID
     * @param id 地址ID
     */
    void setDefaultAddress(Long userId, Long id);
}
