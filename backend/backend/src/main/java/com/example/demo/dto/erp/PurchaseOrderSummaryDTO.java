package com.example.demo.dto.erp;


import com.example.demo.entity.PurchaseOrder;
import com.example.demo.enums.PurchaseOrderStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class PurchaseOrderSummaryDTO {

    private Long purchaseOrderId;
    private String orderNumber;
    private LocalDate orderDate;
    private PurchaseOrderStatus status;
    private BigDecimal totalAmount;
    private Long supplierId;
    private String supplierName; // Assuming supplier name is needed

    public static PurchaseOrderSummaryDTO fromEntity(PurchaseOrder purchaseOrder) {
        PurchaseOrderSummaryDTO dto = new PurchaseOrderSummaryDTO();
        dto.setPurchaseOrderId(purchaseOrder.getPurchaseOrderId());
        dto.setOrderNumber(purchaseOrder.getOrderNumber());
        dto.setOrderDate(purchaseOrder.getOrderDate());
        dto.setStatus(purchaseOrder.getStatus());
        dto.setTotalAmount(purchaseOrder.getTotalAmount());
        // dto.setSupplierId(purchaseOrder.getSupplierId()); // No longer directly setting ID like this
        if (purchaseOrder.getSupplier() != null) {
            dto.setSupplierId(purchaseOrder.getSupplier().getSupplierId());
            dto.setSupplierName(purchaseOrder.getSupplier().getName());
        }
        return dto;
    }
}

