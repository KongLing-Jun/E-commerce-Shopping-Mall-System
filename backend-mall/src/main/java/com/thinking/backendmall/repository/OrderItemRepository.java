package com.thinking.backendmall.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.thinking.backendmall.entity.OrderItem;
import com.thinking.backendmall.vo.TopProductView;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OrderItemRepository extends BaseMapper<OrderItem> {
    @Select("""
            SELECT
                oi.product_id AS productId,
                oi.product_name_snapshot AS productName,
                SUM(oi.quantity) AS totalQuantity,
                COALESCE(SUM(oi.quantity * oi.price_snapshot), 0) AS totalAmount
            FROM order_item oi
            INNER JOIN `order` o ON oi.order_id = o.id
            WHERE o.status >= 1
            GROUP BY oi.product_id, oi.product_name_snapshot
            ORDER BY totalQuantity DESC
            LIMIT #{limit}
            """)
    List<TopProductView> selectTopProducts(@Param("limit") int limit);
}
