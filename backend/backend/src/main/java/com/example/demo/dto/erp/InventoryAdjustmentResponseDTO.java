package com.example.demo.dto.erp;

import com.example.demo.entity.InventoryAdjustment;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class InventoryAdjustmentResponseDTO {
    private Long adjustmentId;
    private String adjustmentNumber;
    private LocalDate adjustmentDate;
    private String adjustmentType;
    private String status;
    private String remarks;
    private Long warehouseId;
    private String warehouseName;
    private LocalDateTime createdAt;
    private Long createdBy;
    private List<InventoryAdjustmentDetailResponseDTO> details;

    public static InventoryAdjustmentResponseDTO fromEntity(InventoryAdjustment entity) {
        InventoryAdjustmentResponseDTO dto = new InventoryAdjustmentResponseDTO();
        dto.setAdjustmentId(entity.getAdjustmentId());
        dto.setAdjustmentNumber(entity.getAdjustmentNumber());
        dto.setAdjustmentDate(entity.getAdjustmentDate());
        if (entity.getAdjustmentType() != null) {
            dto.setAdjustmentType(entity.getAdjustmentType().name());
        }
        dto.setStatus(entity.getStatus());
        dto.setRemarks(entity.getRemarks());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setCreatedBy(entity.getCreatedBy());

        if (entity.getWarehouse() != null) {
            dto.setWarehouseId(entity.getWarehouse().getWarehouseId());
            dto.setWarehouseName(entity.getWarehouse().getName());
        }

        if (entity.getDetails() != null) {
            dto.setDetails(entity.getDetails().stream()
                    .map(InventoryAdjustmentDetailResponseDTO::fromEntity)
                    .collect(Collectors.toList()));
        }
        return dto;
    }
}
