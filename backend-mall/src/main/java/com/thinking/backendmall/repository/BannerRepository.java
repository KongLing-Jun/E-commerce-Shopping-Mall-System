package com.thinking.backendmall.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.thinking.backendmall.entity.Banner;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BannerRepository extends BaseMapper<Banner> {
}
