// 檔名: CustomerCoupon.java (新檔案，取代 CouponUsage.java)
package com.example.demo.entity;

import com.example.demo.enums.CustomerCouponStatus; // 建議新增的枚舉
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "customer_coupons") // 代表用戶持有的券
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerCoupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 【重要】多張 CustomerCoupon 屬於一個用戶
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private CCustomer customer;

    // 【重要】多張 CustomerCoupon 源自同一個模板
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_template_id", nullable = false)
    private CouponTemplate couponTemplate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CustomerCouponStatus status; // 這張券本身的狀態: UNUSED, USED, EXPIRED

    @Column(nullable = false)
    private LocalDateTime receivedAt; // 領取時間

    private LocalDateTime usedAt; // 使用時間 (使用前為 null)

    // 【重要】一張被使用的券，對應到一張訂單
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", unique = true) // 確保一張券只能用在一個訂單
    private Order order;

    @PrePersist
    public void onPrePersist() {
        this.receivedAt = LocalDateTime.now();
        this.status = CustomerCouponStatus.UNUSED;
    }
}