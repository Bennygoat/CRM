package com.example.demo.dto.erp;


import com.example.demo.entity.PurchaseOrderDetail;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class PurchaseOrderDetailViewDTO {
    private Long id;
    private Long productId;
    private String productName;
    private BigDecimal quantity;
    private BigDecimal unitPrice;
    private BigDecimal itemNetAmount;
    private BigDecimal itemTaxAmount;
    private BigDecimal itemAmount;
    private Long warehouseId;
    private String warehouseName;
    private Boolean isGift;

    public static PurchaseOrderDetailViewDTO fromEntity(PurchaseOrderDetail detail) {
        PurchaseOrderDetailViewDTO dto = new PurchaseOrderDetailViewDTO();
        dto.setId(detail.getItemId());
        dto.setProductId(detail.getProductId());
        if (detail.getProduct() != null) {
            dto.setProductName(detail.getProduct().getName());
        }
        dto.setQuantity(detail.getQuantity());
        dto.setUnitPrice(detail.getUnitPrice());
        dto.setItemNetAmount(detail.getItemNetAmount());
        dto.setItemTaxAmount(detail.getItemTaxAmount());
        dto.setItemAmount(detail.getItemAmount());
        dto.setWarehouseId(detail.getWarehouseId());
        if (detail.getWarehouse() != null) {
            dto.setWarehouseName(detail.getWarehouse().getName());
        }
        dto.setIsGift(detail.isGift());
        return dto;
    }
}

