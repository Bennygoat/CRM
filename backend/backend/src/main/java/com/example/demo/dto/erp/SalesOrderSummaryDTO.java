package com.example.demo.dto.erp;


import com.example.demo.entity.SalesOrder;
import com.example.demo.enums.SalesOrderStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class SalesOrderSummaryDTO {

    private Long salesOrderId;
    private String orderNumber;
    private LocalDate orderDate;
    private SalesOrderStatus orderStatus;
    private BigDecimal totalAmount;

    private Long customerId;
    private String customerName;

    public static SalesOrderSummaryDTO fromEntity(SalesOrder salesOrder){
        SalesOrderSummaryDTO dto = new SalesOrderSummaryDTO();
        dto.setSalesOrderId(salesOrder.getSalesOrderId());
        dto.setOrderNumber(salesOrder.getOrderNumber());
        dto.setOrderDate(salesOrder.getOrderDate());
        dto.setOrderStatus(salesOrder.getOrderStatus());
        dto.setTotalAmount(salesOrder.getTotalAmount());

        if(salesOrder.getCustomer() != null){
            dto.setCustomerId(salesOrder.getCustomer().getCustomerId());
            dto.setCustomerName(salesOrder.getCustomer().getCustomerName());
        }
        return dto;
    }
}
