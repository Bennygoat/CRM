package com.example.demo.dto.erp;


import com.example.demo.enums.MovementType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class InventoryAdjustmentDTO {

    @NotNull(message = "Product ID cannot be null.")
    private Long productId;

    @NotNull(message = "Warehouse ID cannot be null.")
    private Long warehouseId;

    @NotNull(message = "Quantity cannot be null.")
    private BigDecimal quantity;

    @NotNull(message = "Unit cost cannot be null.")
    private BigDecimal unitCost;

    @NotNull(message = "Movement type cannot be null.")
    private MovementType movementType;


    private String documentType;

    private Long documentId;

    private Long documentItemId;


    private Long userId;

}
