package com.example.demo.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "inventory_movements")
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class InventoryMovement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "movement_id")
    private Long movementId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    @Column(name = "movement_type", nullable = false)
    private String movementType;

    @Column(name = "quantity_change", nullable = false, precision = 18, scale = 2)
    private BigDecimal quantityChange;

    @Column(name = "current_stock_after_movement", precision = 18, scale = 2)
    private BigDecimal currentStockAfterMovement;

    @Column(name = "document_type")
    private String documentType;

    @Column(name = "document_id")
    private Long documentId;

    @Column(name = "document_item_id")
    private Long documentItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recorded_by", nullable = false)
    private User recordedBy;

    @Column(name = "movement_date", nullable = false)
    private LocalDateTime movementDate;

    @Column(name = "unit_cost_at_movement", nullable = false, precision = 18, scale = 2)
    private BigDecimal unitCostAtMovement;

    @Column(name = "total_cost_change",  nullable = false, precision = 18, scale = 2)
    private BigDecimal totalCostChange;
}
