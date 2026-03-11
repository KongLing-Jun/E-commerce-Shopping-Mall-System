package com.thinking.backendmall.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class AddressRequest {
    @NotBlank(message = "Receiver is required")
    private String receiver;

    @NotBlank(message = "Phone is required")
    private String phone;

    @NotBlank(message = "Province is required")
    private String province;

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "Area is required")
    private String area;

    @NotBlank(message = "Detail is required")
    private String detail;

    @Min(value = 0, message = "IsDefault must be 0 or 1")
    @Max(value = 1, message = "IsDefault must be 0 or 1")
    private Integer isDefault;

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
