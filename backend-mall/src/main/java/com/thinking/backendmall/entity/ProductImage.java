package com.thinking.backendmall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
@TableName("product_image")
public class ProductImage {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long productId;
    private String url;
    private Integer sort;
    // 功能：获取id
    public Long getId() {
        return id;
    }

    // 功能：设置id
    public void setId(Long id) {
        this.id = id;
    }

    // 功能：获取商品id
    public Long getProductId() {
        return productId;
    }

    // 功能：设置商品id
    public void setProductId(Long productId) {
        this.productId = productId;
    }

    // 功能：获取url
    public String getUrl() {
        return url;
    }

    // 功能：设置url
    public void setUrl(String url) {
        this.url = url;
    }

    // 功能：获取排序
    public Integer getSort() {
        return sort;
    }

    // 功能：设置排序
    public void setSort(Integer sort) {
        this.sort = sort;
    }
}
