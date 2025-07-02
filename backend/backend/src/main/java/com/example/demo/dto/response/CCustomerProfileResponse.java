package com.example.demo.dto.response;

import com.example.demo.entity.CouponTemplate;
import com.example.demo.entity.CustomerCoupon;
import com.example.demo.entity.VIPLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
public class CCustomerProfileResponse {
    private String account;
    private String customerName;
    private String email;
    private String address;
    private LocalDate birthday;

    // 【重要修改】將欄位名稱和型別都更新
    private List<CustomerCouponDto> coupons;

    private Long spending;
    private VIPLevel vipLevel;
}
