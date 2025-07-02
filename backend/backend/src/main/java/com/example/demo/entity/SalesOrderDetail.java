package com.example.demo.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "Sales_order_details")
@Getter
@Setter
public class SalesOrderDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long itemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sales_order_id", nullable = false)
    private SalesOrder salesOrder;

    @Column(name = "item_sequence", nullable = false)
    private Integer itemSequence;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "specification", length = 255)
    private String specification;

    @Column(name = "quantity", nullable = false, precision = 18, scale=2)
    private BigDecimal quantity;

    @Column(name = "unit_id", nullable = false)
    private Long unitId;

    @Column(name = "unit_price", nullable = false, precision = 18, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "item_amount", nullable = false, precision = 18, scale = 2)
    private BigDecimal itemAmount;

    @Column(name = "discount_rate", precision = 18, scale =2)
    private BigDecimal discountRate;

    @Column(name = "item_net_amount", nullable = false, precision = 18, scale = 2)
    private BigDecimal itemNetAmount;

    @Column(name = "item_tax_amount", nullable = false, precision = 18, scale = 2)
    private BigDecimal itemTaxAmount;

    @Column(name = "transaction_number", nullable = true , unique = true, length = 50)
    private String transactionNumber;

    @Column(name = "remarks", columnDefinition = "TEXT")
    private String remarks;


    @Column(name = "created_at",nullable = false ,updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "created_by",nullable = false ,updatable = false)
    private Long createdBy;


    @Column(name = "updated_at",nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "updated_by",nullable = false)
    private Long updatedBy;

    // Maintain backward compatibility with code using productId
    public Long getProductId() {
        return product != null ? product.getProductId() : null;
    }


    }


