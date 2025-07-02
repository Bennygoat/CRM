package com.example.demo.dto.erp;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class InventoryAdjustmentDetailCreateDTO {
    @NotNull(message = "產品ID不可為空")
    private Long productId;

    @NotNull(message = "倉庫ID不可為空")
    private Long warehouseId;

    @NotNull(message = "調整數量不可為空(正數為增加，負數為減少)")
    private BigDecimal adjustedQuantity;

    private String remarks;
}

