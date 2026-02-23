package com.thinking.backendmall.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.thinking.backendmall.entity.OrderDelivery;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderDeliveryRepository extends BaseMapper<OrderDelivery> {
}
