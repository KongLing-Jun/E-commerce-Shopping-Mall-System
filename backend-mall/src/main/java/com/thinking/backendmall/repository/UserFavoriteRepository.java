package com.thinking.backendmall.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.thinking.backendmall.entity.UserFavorite;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserFavoriteRepository extends BaseMapper<UserFavorite> {
}
