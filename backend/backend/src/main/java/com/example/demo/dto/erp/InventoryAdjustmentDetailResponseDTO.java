package com.example.demo.dto.erp;

import com.example.demo.entity.InventoryAdjustmentDetail;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class InventoryAdjustmentDetailResponseDTO {

    private Long itemId;
    private Long productId;
    private String productName;
    private String productCode;
    private BigDecimal adjustedQuantity;
    private String remarks;

    public static InventoryAdjustmentDetailResponseDTO fromEntity(InventoryAdjustmentDetail entity) {
        InventoryAdjustmentDetailResponseDTO dto = new InventoryAdjustmentDetailResponseDTO();
        dto.setItemId(entity.getItemId());
        dto.setAdjustedQuantity(entity.getAdjustedQuantity());
        dto.setRemarks(entity.getRemarks());

        if (entity.getProduct() != null) {
            dto.setProductId(entity.getProduct().getProductId());
            dto.setProductName(entity.getProduct().getName());
            dto.setProductCode(entity.getProduct().getProductCode());
        }
        return dto;
    }

}
