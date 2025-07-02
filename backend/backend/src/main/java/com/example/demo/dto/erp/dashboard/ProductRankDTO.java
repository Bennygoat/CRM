package com.example.demo.dto.erp.dashboard;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductRankDTO {
    private Long productId;
    private String productName;
    private BigDecimal totalSoldQuantity;

    public ProductRankDTO(Long productId, String productName, BigDecimal totalQuantitySold) {
        this.productId = productId;
        this.productName = productName;
        this.totalSoldQuantity = totalQuantitySold;
    }
}