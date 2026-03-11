package com.thinking.backendmall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName("address")
public class Address {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long userId;
    private String receiver;
    private String phone;
    private String province;
    private String city;
    private String area;
    private String detail;
    private Integer isDefault; // 1默认 0否

    // 功能：获取id
    public Long getId() {
        return id;
    }

    // 功能：设置id
    public void setId(Long id) {
        this.id = id;
    }

    // 功能：获取用户id
    public Long getUserId() {
        return userId;
    }

    // 功能：设置用户id
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    // 功能：获取receiver
    public String getReceiver() {
        return receiver;
    }

    // 功能：设置receiver
    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    // 功能：获取手机号
    public String getPhone() {
        return phone;
    }

    // 功能：设置手机号
    public void setPhone(String phone) {
        this.phone = phone;
    }

    // 功能：获取province
    public String getProvince() {
        return province;
    }

    // 功能：设置province
    public void setProvince(String province) {
        this.province = province;
    }

    // 功能：获取city
    public String getCity() {
        return city;
    }

    // 功能：设置city
    public void setCity(String city) {
        this.city = city;
    }

    // 功能：获取area
    public String getArea() {
        return area;
    }

    // 功能：设置area
    public void setArea(String area) {
        this.area = area;
    }

    // 功能：获取详情
    public String getDetail() {
        return detail;
    }

    // 功能：设置详情
    public void setDetail(String detail) {
        this.detail = detail;
    }

    // 功能：获取isdefault
    public Integer getIsDefault() {
        return isDefault;
    }

    // 功能：设置isdefault
    public void setIsDefault(Integer isDefault) {
        this.isDefault = isDefault;
    }
}
