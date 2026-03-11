package com.thinking.backendmall.service;

import com.thinking.backendmall.dto.AddressRequest;
import com.thinking.backendmall.entity.Address;

import java.util.List;

public interface AddressService {

    // 功能：查询地址
    List<Address> listAddresses(Long userId);

    // 功能：新增地址
    void addAddress(Long userId, AddressRequest request);

    // 功能：更新地址
    void updateAddress(Long userId, Long addressId, AddressRequest request);

    // 功能：删除地址
    void deleteAddress(Long userId, Long addressId);
}
