package com.thinking.backendmall.controller;

import com.thinking.backendmall.common.BusinessException;
import com.thinking.backendmall.common.Result;
import com.thinking.backendmall.config.security.AuthContext;
import com.thinking.backendmall.dto.AddressRequest;
import com.thinking.backendmall.entity.Address;
import com.thinking.backendmall.service.AddressService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
public class AddressController {

    @Autowired
    private AddressService addressService;

    @GetMapping
    public Result<List<Address>> listAddresses() {
        return Result.success(addressService.listAddresses(requireUserId()));
    }

    @PostMapping
    public Result<Void> addAddress(@Valid @RequestBody AddressRequest request) {
        addressService.addAddress(requireUserId(), request);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<Void> updateAddress(@PathVariable Long id, @Valid @RequestBody AddressRequest request) {
        addressService.updateAddress(requireUserId(), id, request);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteAddress(@PathVariable Long id) {
        addressService.deleteAddress(requireUserId(), id);
        return Result.success();
    }

    private Long requireUserId() {
        Long userId = AuthContext.getUserId();
        if (userId == null) {
            throw new BusinessException(401, "Unauthorized");
        }
        return userId;
    }
}
