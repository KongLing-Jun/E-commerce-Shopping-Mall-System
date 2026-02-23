package com.thinking.backendmall.service;

import com.thinking.backendmall.dto.AddressRequest;
import com.thinking.backendmall.entity.Address;

import java.util.List;

public interface AddressService {

    List<Address> listAddresses(Long userId);

    void addAddress(Long userId, AddressRequest request);

    void updateAddress(Long userId, Long addressId, AddressRequest request);

    void deleteAddress(Long userId, Long addressId);
}
