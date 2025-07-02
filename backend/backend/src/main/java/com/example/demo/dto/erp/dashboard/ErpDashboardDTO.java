package com.example.demo.dto.erp.dashboard;


import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ErpDashboardDTO {
    private KpiData kpis;
//    private ChartData salesTrend;
    private List<ProductRankDTO> topSellingProducts;
    private ComparisonChartData salesComparison;
    private ComparisonChartData inventoryValueComparison;
    private List<LowStockProductDTO> lowStockProducts;
}
