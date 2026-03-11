package com.thinking.backendmall.service;

import com.thinking.backendmall.vo.UserFavoriteView;
import com.thinking.backendmall.vo.UserFootprintView;
import com.thinking.backendmall.vo.UserSummaryView;

import java.util.List;

public interface UserCenterService {
    // 功能：获取汇总
    UserSummaryView getSummary();

    // 功能：查询收藏
    List<UserFavoriteView> listFavorites();

    // 功能：新增收藏
    void addFavorite(Long productId);

    // 功能：移除收藏
    void removeFavorite(Long productId);

    // 功能：查询足迹
    List<UserFootprintView> listFootprints();

    // 功能：移除足迹
    void removeFootprint(Long productId);

    // 功能：记录足迹
    void recordFootprint(Long productId);
}
