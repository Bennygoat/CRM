package com.example.demo.dto.erp;

import java.math.BigDecimal;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PurchaseOrderDetailCreateDTO {

    @NotNull(message = "產品ID不可為空")
    private Long productId;

    @NotNull(message = "數量不可為空")
    @Min(value = 1, message = "數量至少為1")
    private BigDecimal quantity;

    @NotNull(message = "單價不可為空")
    @Min(value = 0, message = "單價不可為負數") //TODO(josh): delete
    private BigDecimal unitPrice;
    
    @NotNull(message = "倉庫ID不可為空") //TODO(josh): delete or null
    private Long warehouseId;




}
