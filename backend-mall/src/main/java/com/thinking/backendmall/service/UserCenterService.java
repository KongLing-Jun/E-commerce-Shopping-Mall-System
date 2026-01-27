package com.thinking.backendmall.service;

import com.thinking.backendmall.vo.UserFavoriteView;
import com.thinking.backendmall.vo.UserFootprintView;
import com.thinking.backendmall.vo.UserSummaryView;

import java.util.List;

public interface UserCenterService {
    UserSummaryView getSummary();

    List<UserFavoriteView> listFavorites();

    void addFavorite(Long productId);

    void removeFavorite(Long productId);

    List<UserFootprintView> listFootprints();

    void removeFootprint(Long productId);

    void recordFootprint(Long productId);
}
