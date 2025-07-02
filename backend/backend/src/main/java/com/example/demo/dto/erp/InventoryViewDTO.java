package com.example.demo.dto.erp;


import com.example.demo.entity.Inventory;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class InventoryViewDTO {

    private Long inventoryId;


    private Long productId;
    private String productCode;
    private String productName;


    private Long warehouseId;
    private String warehouseName;


    private BigDecimal currentStock;
    private BigDecimal averageCost;
    private LocalDateTime lastUpdatedAt;

    public static InventoryViewDTO fromEntity(Inventory inventory) {
        InventoryViewDTO dto = new InventoryViewDTO();
        dto.setInventoryId(inventory.getInventoryId());

        if (inventory.getProduct() != null) {
            dto.setProductId(inventory.getProduct().getProductId());
            dto.setProductCode(inventory.getProduct().getProductCode());
            dto.setProductName(inventory.getProduct().getName());
        }

        if (inventory.getWarehouse() != null) {
            dto.setWarehouseId(inventory.getWarehouse().getWarehouseId());
            dto.setWarehouseName(inventory.getWarehouse().getName());
        }

        dto.setCurrentStock(inventory.getCurrentStock());
        dto.setAverageCost(inventory.getAverageCost());
        dto.setLastUpdatedAt(inventory.getUpdatedAt());

        return dto;
    }
}
