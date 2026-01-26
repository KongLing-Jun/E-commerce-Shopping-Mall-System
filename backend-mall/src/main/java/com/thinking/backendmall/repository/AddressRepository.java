package com.thinking.backendmall.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.thinking.backendmall.entity.Address;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AddressRepository extends BaseMapper<Address> {
}
