package com.thinking.backendmall.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.thinking.backendmall.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Mapper
public interface OrderRepository extends BaseMapper<Order> {
    @Select("""
            SELECT COUNT(*)
            FROM `order`
            WHERE created_at >= #{start}
              AND created_at < #{end}
            """)
    // 功能：处理数量bycreatedatbetween
    Long countByCreatedAtBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Select("""
            SELECT COALESCE(SUM(pay_amount), 0)
            FROM `order`
            WHERE status >= 1
            """)
    // 功能：处理sumpaid金额
    BigDecimal sumPaidAmount();
}
