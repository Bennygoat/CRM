package com.example.demo.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "purchase_order_details")
@Getter
@Setter
public class PurchaseOrderDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long itemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_order_id", nullable = false)
    @JsonBackReference
    private PurchaseOrder purchaseOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "quantity", nullable = false, precision = 18, scale = 2)
    private BigDecimal quantity;

    @Column(name = "unit_price", nullable = false, precision = 18, scale = 2)
    private BigDecimal unitPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    @Column(name = "item_amount", nullable = false, precision = 18, scale = 2)
    private BigDecimal itemAmount;

    @Column(name = "item_tax_amount", nullable = false, precision = 18, scale = 2)
    private BigDecimal itemTaxAmount;

    @Column(name = "item_net_amount", nullable = false, precision = 18, scale = 2)
    private BigDecimal itemNetAmount;

    @Column(name = "is_gift", nullable = false)
    private boolean isGift = false;

    @Column(name = "created_by", nullable = false, updatable = false)
    private Long createdBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_by", nullable = false)
    private Long updatedBy;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public String getProductName() {
        return product != null ? product.getName() : null;
    }

    // Maintain backward compatibility with code using productId
    public Long getProductId() {
        return product != null ? product.getProductId() : null;
    }

//    public void setProductId(Long productId) {
//        // This method intentionally left empty to maintain compatibility
//        // The product relationship should be set directly instead
//    }

    // Maintain backward compatibility with code using warehouseId
    public Long getWarehouseId() {
        return warehouse != null ? warehouse.getWarehouseId() : null;
    }

//    public void setWarehouseId(Long warehouseId) {
//        // This method intentionally left empty to maintain compatibility
//        // The warehouse relationship should be set directly instead
//    }
}
