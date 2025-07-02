package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set; // 改用 Set

@Entity
@Table(name = "customer")
@DiscriminatorValue("B2C")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class CCustomer extends CustomerBase {

    @Column(nullable = false, unique = true)
    private String account;
    @Column(nullable = false)
    private String password;
    private LocalDate birthday;
    private LocalDateTime lastLogin;
    private LocalDateTime accessStartTime;
    private LocalDateTime accessEndTime;

//    @Column(name = "is_active", nullable = false)
//    @Builder.Default
//    private boolean isActive = true;
//
//    @Column(name = "is_deleted", nullable = false)
//    @Builder.Default
//    private Boolean isDeleted = false;


//    private LocalDateTime createdAt;
//    private LocalDateTime updatedAt;

//    @CreatedDate // Use annotation
//    @Column(name = "created_at", nullable = false, updatable = false)
//    private LocalDateTime createdAt; //TODO(joshkuei): Add for test.
//
//    @LastModifiedDate // Use annotation
//    @Column(name = "updated_at", nullable = false)
//    private LocalDateTime updatedAt; //TODO(joshkuei): Add for test.test

//    @PrePersist
//    public void onCreate() {
//        this.createdAt = LocalDateTime.now();
//        this.updatedAt = LocalDateTime.now();
//        if (this.isDeleted == null) this.isDeleted = false;
//        if (this.isActive == null) this.isActive = true;
//
//    }
//
//    @PreUpdate
//    public void onUpdate() {
//        this.updatedAt = LocalDateTime.now();
//    }


    @OneToOne(mappedBy = "CCustomer")
    private Cart cart;

    @OneToMany(mappedBy = "CCustomer")
    @Builder.Default
    private List<CCustomerAddress> CCustomerAddress = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "vip_level")
    private VIPLevel vipLevel;

    // 【重要】移除舊的多對多關聯
    // @ManyToMany(...)
    // private List<Coupon> coupons;

    // 【重要】新增與 CustomerCoupon 的一對多關聯
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<CustomerCoupon> customerCoupons;
}
