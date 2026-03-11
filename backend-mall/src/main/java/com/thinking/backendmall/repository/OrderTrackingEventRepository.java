package com.thinking.backendmall.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.thinking.backendmall.entity.OrderTrackingEvent;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderTrackingEventRepository extends BaseMapper<OrderTrackingEvent> {
}
