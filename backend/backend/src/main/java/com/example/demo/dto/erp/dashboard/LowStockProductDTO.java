package com.example.demo.dto.erp.dashboard;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class LowStockProductDTO {
    private Long productId;
    private String productCode;
    private String productName;
    private Long warehouseId;
    private String warehouseName;
    private BigDecimal currentStock;
    private BigDecimal safetyStockQuantity;
}
