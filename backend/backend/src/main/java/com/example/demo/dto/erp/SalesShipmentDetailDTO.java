package com.example.demo.dto.erp;

import com.example.demo.entity.SalesShipmentDetail;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class SalesShipmentDetailDTO {
    private Long itemId;
    private Long productId;
    private String productCode;
    private String productName;
    private BigDecimal shippedQuantity;

    public static SalesShipmentDetailDTO fromEntity(SalesShipmentDetail entity) {
        SalesShipmentDetailDTO dto = new SalesShipmentDetailDTO();
        dto.setItemId(entity.getItemId());
        dto.setShippedQuantity(entity.getShippedQuantity());
        if (entity.getProduct() != null) {
            dto.setProductId(entity.getProduct().getProductId());
            dto.setProductCode(entity.getProduct().getProductCode());
            dto.setProductName(entity.getProduct().getName());
        }
        return dto;
    }
}