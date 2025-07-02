package com.example.demo.dto.erp;

import com.example.demo.entity.BCustomer;
import com.example.demo.entity.CustomerBase;
import com.example.demo.entity.SalesOrder;
import com.example.demo.enums.PaymentStatus;
import com.example.demo.enums.SalesOrderStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class SalesOrderViewDTO {

    private Long salesOrderId;
    private String orderNumber;
    private LocalDate orderDate;
    private SalesOrderStatus orderStatus;
    private PaymentStatus paymentStatus;
    private String shippingAddress;
    private String paymentMethod;
    private String remarks;

    private BigDecimal totalNetAmount;
    private BigDecimal totalTaxAmount;
    private BigDecimal totalAmount;

    private Long customerId;
    private String customerName;
    private String customerType;
    private String customerPhone;
    private String taxIdNumber;

    private List<SalesOrderItemViewDTO> details;

    public static SalesOrderViewDTO fromEntity(SalesOrder order) {
        SalesOrderViewDTO dto = new SalesOrderViewDTO();

        dto.setSalesOrderId(order.getSalesOrderId());
        dto.setOrderNumber(order.getOrderNumber());
        dto.setOrderDate(order.getOrderDate());
        dto.setOrderStatus(order.getOrderStatus());
        dto.setPaymentStatus(order.getPaymentStatus());
        dto.setShippingAddress(order.getShippingAddress());
        dto.setPaymentMethod(order.getPaymentMethod());
        dto.setRemarks(order.getRemarks());
        dto.setTotalNetAmount(order.getTotalNetAmount());
        dto.setTotalTaxAmount(order.getTotalTaxAmount());
        dto.setTotalAmount(order.getTotalAmount());

        CustomerBase customer = order.getCustomer();
        if (customer != null) {
            dto.setCustomerId(customer.getCustomerId());
            dto.setCustomerName(customer.getCustomerName());
            dto.setCustomerPhone(customer.getTel());
            dto.setCustomerType(customer.getCustomerType().name());

            if (customer instanceof BCustomer bCustomer) {
                dto.setTaxIdNumber(bCustomer.getTinNumber());
            }
        }

        if (order.getDetails() != null) {
            dto.setDetails(
                    order.getDetails().stream()
                            .map(SalesOrderItemViewDTO::fromEntity)
                            .collect(Collectors.toList())
            );
        }

        return dto;
    }
}
