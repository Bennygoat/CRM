package com.example.demo.repository;

import com.example.demo.entity.CouponTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CouponTemplateRepository extends JpaRepository<CouponTemplate, Long> {
    // 我們需要一個方法來透過「代碼」找到優惠券模板
    Optional<CouponTemplate> findByCode(String code);
}