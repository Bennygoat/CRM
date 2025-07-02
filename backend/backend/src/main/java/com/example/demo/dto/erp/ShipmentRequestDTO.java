package com.example.demo.dto.erp;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShipmentRequestDTO {

    @NotNull(message = "出貨倉庫ID不可為空")
    private Long warehouseId;

}
