package com.bookstore.service.impl;

import com.bookstore.entity.Address;
import com.bookstore.mapper.AddressMapper;
import com.bookstore.service.AddressService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 地址服务实现类
 */
@Service
public class AddressServiceImpl implements AddressService {
    
    private final AddressMapper addressMapper;
    
    /**
     * 构造函数注入依赖
     *
     * @param addressMapper 地址数据访问层
     */
    public AddressServiceImpl(AddressMapper addressMapper) {
        this.addressMapper = addressMapper;
    }
    
    @Override
    public Address getAddressById(Long id) {
        Address address = addressMapper.findById(id);
        if (address == null) {
            throw new RuntimeException("地址不存在");
        }
        return address;
    }
    
    @Override
    public List<Address> getAddressesByUserId(Long userId) {
        return addressMapper.findByUserId(userId);
    }
    
    @Override
    @Transactional
    public Address createAddress(Long userId, Address address) {
        address.setUserId(userId);
        if (address.getIsDefault() == null) {
            address.setIsDefault(false);
        }
        if (Boolean.TRUE.equals(address.getIsDefault())) {
            addressMapper.clearDefault(userId);
        }
        addressMapper.insert(address);
        return address;
    }
    
    @Override
    @Transactional
    public Address updateAddress(Long userId, Long id, Address address) {
        Address existingAddress = getAddressById(id);
        if (!existingAddress.getUserId().equals(userId)) {
            throw new RuntimeException("无权修改此地址");
        }
        address.setId(id);
        address.setUserId(userId);
        if (Boolean.TRUE.equals(address.getIsDefault())) {
            addressMapper.clearDefault(userId);
        }
        addressMapper.update(address);
        return address;
    }
    
    @Override
    @Transactional
    public void deleteAddress(Long userId, Long id) {
        Address address = getAddressById(id);
        if (!address.getUserId().equals(userId)) {
            throw new RuntimeException("无权删除此地址");
        }
        addressMapper.deleteById(id);
    }
    
    @Override
    @Transactional
    public void setDefaultAddress(Long userId, Long id) {
        Address address = getAddressById(id);
        if (!address.getUserId().equals(userId)) {
            throw new RuntimeException("无权操作此地址");
        }
        addressMapper.clearDefault(userId);
        addressMapper.setDefault(id);
    }
}
