package com.thinking.backendmall.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.thinking.backendmall.entity.ProductImage;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProductImageRepository extends BaseMapper<ProductImage> {
}
