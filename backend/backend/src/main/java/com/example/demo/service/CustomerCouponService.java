package com.example.demo.service;

import com.example.demo.entity.CCustomer;
import com.example.demo.entity.CouponTemplate;
import com.example.demo.entity.CustomerCoupon;
import com.example.demo.repository.CouponTemplateRepository;
import com.example.demo.repository.CustomerCouponRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomerCouponService {

    private final CustomerCouponRepository customerCouponRepository;
    private final CouponTemplateRepository couponTemplateRepository;

    @Transactional
    public CustomerCoupon grantCouponToCustomer(CCustomer customer, CouponTemplate template) {
        // 建立用戶優惠券實例
        CustomerCoupon customerCoupon = CustomerCoupon.builder()
                .customer(customer)
                .couponTemplate(template)
                .build();

        // 儲存用戶優惠券
        CustomerCoupon savedCustomerCoupon = customerCouponRepository.save(customerCoupon);

        // 更新模板的已發行數量
        template.setIssuedQuantity(template.getIssuedQuantity() + 1);
        couponTemplateRepository.save(template);

        return savedCustomerCoupon;
    }
}