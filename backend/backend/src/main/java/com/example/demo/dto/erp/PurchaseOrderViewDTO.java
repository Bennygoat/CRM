package com.example.demo.dto.erp;
import com.example.demo.entity.PurchaseOrder;
import com.example.demo.enums.PurchaseOrderStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class PurchaseOrderViewDTO {

    private Long purchaseOrderId;
    private String orderNumber;
    private LocalDate orderDate;
    private PurchaseOrderStatus status;
    private String currency;
    private String remarks;

    private BigDecimal totalNetAmount;
    private BigDecimal totalTaxAmount;
    private BigDecimal totalAmount;
    // private BigDecimal totalCostAmount;

    private Long supplierId;
    private String supplierName;


    private List<PurchaseOrderDetailViewDTO> details;

    private Long createdBy;
    private Long updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdByName;
    private String updatedByName;

    public static PurchaseOrderViewDTO fromEntity(PurchaseOrder order) {
        PurchaseOrderViewDTO dto = new PurchaseOrderViewDTO();
        dto.setPurchaseOrderId(order.getPurchaseOrderId());
        dto.setOrderNumber(order.getOrderNumber());
        dto.setOrderDate(order.getOrderDate());
        dto.setStatus(order.getStatus());
        dto.setCurrency(order.getCurrency());
        dto.setRemarks(order.getRemarks());
        dto.setTotalNetAmount(order.getTotalNetAmount());
        dto.setTotalTaxAmount(order.getTotalTaxAmount());
        dto.setTotalAmount(order.getTotalAmount());
        // dto.setTotalCostAmount(order.getTotalCostAmount());

        // dto.setSupplierId(order.getSupplierId()); // No longer directly setting ID like this
        if (order.getSupplier() != null) {
            dto.setSupplierId(order.getSupplier().getSupplierId());
            dto.setSupplierName(order.getSupplier().getName());
        }

        if (order.getDetails() != null) {
            dto.setDetails(
                    order.getDetails().stream()
                            .map(PurchaseOrderDetailViewDTO::fromEntity)
                            .collect(Collectors.toList())
            );
        }

        dto.setCreatedBy(order.getCreatedBy());
        dto.setUpdatedBy(order.getUpdatedBy());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setUpdatedAt(order.getUpdatedAt());

        // For createdByName and updatedByName, you might need to fetch User entities
        // For now, leaving them as null or to be populated by service if necessary
        // if (order.getCreatedByUser() != null) {
        //     dto.setCreatedByName(order.getCreatedByUser().getUsername());
        // }
        // if (order.getUpdatedByUser() != null) {
        //     dto.setUpdatedByName(order.getUpdatedByUser().getUsername());
        // }

        return dto;
    }
}