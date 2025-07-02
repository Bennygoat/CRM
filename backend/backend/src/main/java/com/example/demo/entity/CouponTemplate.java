// 檔名: CouponTemplate.java
package com.example.demo.entity;

import com.example.demo.enums.CouponStatus; // 建議新增的枚舉
import com.example.demo.enums.CouponType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal; // 使用 BigDecimal 處理金額，避免精度問題
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "coupon_templates") // 表名建議用複數並能體現是模板
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name; // e.g., "新會員註冊禮 - 50元折價券"

    @Column(length = 255)
    private String description; // e.g., "全館不限金額消費即可折抵"

    // 可選，用於公開發行的兌換碼
    @Column(unique = true, length = 50)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CouponType couponType; // FIXED_AMOUNT 或 PERCENTAGE

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal discountValue; // 折扣值 (例如 50.00 或 0.90)

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal minPurchaseAmount; // 最低消費門檻

    @Column(precision = 10, scale = 2)
    private BigDecimal maxDiscountAmount; // 百分比折扣時的最高折抵金額

    @Column(nullable = false)
    private LocalDateTime validFrom; // 有效期 (開始)

    @Column(nullable = false)
    private LocalDateTime validTo; // 有效期 (結束)

    @Column(nullable = false)
    private Integer totalQuantity; // 總發行量上限

    @Column(nullable = false)
    @Builder.Default
    private Integer issuedQuantity = 0; // 已發行數量

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CouponStatus status; // 模板狀態: ACTIVE, INACTIVE

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_vip_level")
    private VIPLevel targetVipLevel; // 保留您原有的 VIP 等級限制

    // 【重要】一個模板可以生成多個用戶優惠券實例
    @OneToMany(mappedBy = "couponTemplate", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<CustomerCoupon> customerCoupons;
}