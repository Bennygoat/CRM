package com.example.demo.dto.erp;

import com.example.demo.entity.SalesShipment;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class SalesShipmentDTO {
    private Long shipmentId;
    private String shipmentNumber;
    private Long salesOrderId;
    private String salesOrderNumber;
    private LocalDate shipmentDate;
    private Long customerId;
    private String customerName;
    private Long warehouseId;
    private String warehouseName;
    private String shippingMethod;
    private String trackingNumber;
    private String shippingStatus;
    private LocalDateTime createdAt;
    private List<SalesShipmentDetailDTO> details;

    public static SalesShipmentDTO fromEntity(SalesShipment entity) {
        SalesShipmentDTO dto = new SalesShipmentDTO();
        dto.setShipmentId(entity.getShipmentId());
        dto.setShipmentNumber(entity.getShipmentNumber());
        dto.setShipmentDate(entity.getShipmentDate());
        dto.setShippingMethod(entity.getShippingMethod());
        dto.setTrackingNumber(entity.getTrackingNumber());
        dto.setShippingStatus(entity.getShippingStatus());
        dto.setCreatedAt(entity.getCreatedAt());

        if (entity.getSalesOrder() != null) {
            dto.setSalesOrderId(entity.getSalesOrder().getSalesOrderId());
            dto.setSalesOrderNumber(entity.getSalesOrder().getOrderNumber());
        }
        if (entity.getCustomer() != null) {
            dto.setCustomerId(entity.getCustomer().getCustomerId());
            dto.setCustomerName(entity.getCustomer().getCustomerName());
        }
        if (entity.getWarehouse() != null) {
            dto.setWarehouseId(entity.getWarehouse().getWarehouseId());
            dto.setWarehouseName(entity.getWarehouse().getName());
        }
        if (entity.getDetails() != null) {
            dto.setDetails(entity.getDetails().stream()
                    .map(SalesShipmentDetailDTO::fromEntity)
                    .collect(Collectors.toList()));
        }
        return dto;
    }
}
