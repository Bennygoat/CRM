package com.example.demo.service;

import com.example.demo.entity.CouponTemplate;
import com.example.demo.repository.CouponTemplateRepository;
import com.example.demo.exception.ResourceNotFoundException; // 假設您有這個自訂例外
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CouponTemplateService {

    private final CouponTemplateRepository couponTemplateRepository;

    public CouponTemplate findTemplateByCode(String code) {
        return couponTemplateRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("CouponTemplate with code " + code + " not found."));
    }
}