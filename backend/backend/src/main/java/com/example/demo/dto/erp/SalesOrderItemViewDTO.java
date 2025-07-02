package com.example.demo.dto.erp;

import com.example.demo.entity.SalesOrderDetail;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class SalesOrderItemViewDTO {

    private Long productId;
    private String productCode;
    private String productName;
    private BigDecimal quantity;
    private BigDecimal unitPrice;
    private BigDecimal itemAmount;

    public static SalesOrderItemViewDTO fromEntity(SalesOrderDetail detail){
        SalesOrderItemViewDTO dto = new SalesOrderItemViewDTO();

        if(detail.getProduct() != null){
            dto.setProductId(detail.getProduct().getProductId());
            dto.setProductCode(detail.getProduct().getProductCode());
            dto.setProductName(detail.getProduct().getName());
        }

        dto.setQuantity(detail.getQuantity());
        dto.setUnitPrice(detail.getUnitPrice());
        dto.setItemAmount(detail.getItemAmount());
        return dto;
    }
}
