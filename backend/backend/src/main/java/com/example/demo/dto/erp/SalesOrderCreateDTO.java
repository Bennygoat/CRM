package com.example.demo.dto.erp;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class SalesOrderCreateDTO {
    @NotNull(message = "客戶ID不可為空")
    private Long customerId;

    @NotNull(message = "訂單日期不可為空")
    private LocalDate orderDate;

    @NotEmpty(message = "送貨地址不可為空")
    private String shippingAddress;

    @NotEmpty(message = "送貨方式不可為空")
    private String shippingMethod;

    @NotEmpty(message = "付款方式不可為空")
    private String paymentMethod;

    private String remarks;

    @NotNull(message = "出貨倉庫ID不可為空")
    private Long warehouseId;

    @NotEmpty(message = "訂單明細不可為空")
    @Valid
    private List<SalesOrderDetailCreateDTO> details;
}
