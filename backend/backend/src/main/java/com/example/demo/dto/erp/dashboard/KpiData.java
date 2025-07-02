package com.example.demo.dto.erp.dashboard;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class KpiData {
    private BigDecimal monthlySalesTotal;
    private Long unpaidSalesOrders;
//    private Long pendingShipments;
    private Long productsBelowSafetyStock;
}
