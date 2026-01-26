package com.thinking.backendmall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.thinking.backendmall.common.BusinessException;
import com.thinking.backendmall.dto.AddressRequest;
import com.thinking.backendmall.entity.Address;
import com.thinking.backendmall.repository.AddressRepository;
import com.thinking.backendmall.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    private AddressRepository addressRepository;

    @Override
    public List<Address> listAddresses(Long userId) {
        return addressRepository.selectList(new LambdaQueryWrapper<Address>()
                .eq(Address::getUserId, userId)
                .orderByDesc(Address::getIsDefault));
    }

    @Override
    public void addAddress(Long userId, AddressRequest request) {
        Address address = new Address();
        address.setUserId(userId);
        address.setReceiver(request.getReceiver());
        address.setPhone(request.getPhone());
        address.setProvince(request.getProvince());
        address.setCity(request.getCity());
        address.setArea(request.getArea());
        address.setDetail(request.getDetail());
        address.setIsDefault(request.getIsDefault() == null ? 0 : request.getIsDefault());
        if (address.getIsDefault() == 1) {
            clearDefault(userId);
        }
        addressRepository.insert(address);
    }

    @Override
    public void updateAddress(Long userId, Long addressId, AddressRequest request) {
        Address address = addressRepository.selectById(addressId);
        if (address == null || !userId.equals(address.getUserId())) {
            throw new BusinessException(404, "Address not found");
        }
        address.setReceiver(request.getReceiver());
        address.setPhone(request.getPhone());
        address.setProvince(request.getProvince());
        address.setCity(request.getCity());
        address.setArea(request.getArea());
        address.setDetail(request.getDetail());
        address.setIsDefault(request.getIsDefault() == null ? address.getIsDefault() : request.getIsDefault());
        if (address.getIsDefault() == 1) {
            clearDefault(userId);
        }
        addressRepository.updateById(address);
    }

    @Override
    public void deleteAddress(Long userId, Long addressId) {
        Address address = addressRepository.selectById(addressId);
        if (address == null || !userId.equals(address.getUserId())) {
            throw new BusinessException(404, "Address not found");
        }
        addressRepository.deleteById(addressId);
    }

    private void clearDefault(Long userId) {
        addressRepository.update(new LambdaUpdateWrapper<Address>()
                .set(Address::getIsDefault, 0)
                .eq(Address::getUserId, userId));
    }
}
