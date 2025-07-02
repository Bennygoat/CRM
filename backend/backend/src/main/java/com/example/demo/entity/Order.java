// CRM/backend/backend/src/main/java/com/example/demo/entity/Order.java
package com.example.demo.entity;

import com.example.demo.enums.OrderStatus;
import com.example.demo.enums.PaymentMethod;
import com.example.demo.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderid;

    @ManyToOne
    @JoinColumn(name = "platformid")
    private Platform platform;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private CCustomer CCustomer;

    private LocalDate orderdate;

    @Enumerated(EnumType.STRING)
    @Column(name = "paymentstatus")
    private PaymentStatus paymentStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "orderstatus")
    private OrderStatus orderStatus;

    // ====================== 【以下為新增的欄位】 ======================
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false) // 設定為不允許 null
    private PaymentMethod paymentMethod;
    // ===============================================================

    @ManyToOne
    @JoinColumn(name = "addressid")
    private CCustomerAddress CCustomerAddress;

    private LocalDateTime createat;
    private LocalDateTime updateat;

    // 新增總金額欄位
    @Column(name = "total_amount") // 請在資料庫中新增此欄位
    private Double totalAmount; // 假設訂單總金額是 Double

    // ====================== 【以下為新增的欄位】 ======================
    @Column(name = "merchant_trade_no", unique = true) // 商家交易單號，設為唯一
    private String merchantTradeNo;
    // ===============================================================

    //--------
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderDetail> orderDetails = new ArrayList<>();

    // 【重要】新增與 CustomerCoupon 的反向關聯
    @OneToOne(mappedBy = "order", fetch = FetchType.LAZY)
    private CustomerCoupon usedCoupon; // 反向關聯，用於查詢此訂單用了哪張券

    //-------------雙向關聯輔助方法
    public void addOrderDetail(OrderDetail orderDetail) {
        this.orderDetails.add(orderDetail);
        orderDetail.setOrder(this);
    }

    public void removeOrderDetail(OrderDetail orderDetail) {
        this.orderDetails.remove(orderDetail);
        orderDetail.setOrder(null);
    }
}