package com.example.demo.service.erp;


import com.example.demo.dto.erp.dashboard.*;
import com.example.demo.enums.PaymentStatus;
import com.example.demo.enums.SalesOrderStatus;
import com.example.demo.repository.InventoryMovementRepository;
import com.example.demo.repository.InventoryRepository;
import com.example.demo.repository.SalesOrderDetailRepository;
import com.example.demo.repository.SalesOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final SalesOrderRepository salesOrderRepository;
    private final SalesOrderDetailRepository salesOrderDetailRepository;
    private final InventoryRepository inventoryRepository;
    private final InventoryMovementRepository inventoryMovementRepository;

    public ErpDashboardDTO getErpDashboardSummary() {
        ErpDashboardDTO dashboard = new ErpDashboardDTO();

        dashboard.setKpis(getKpis());
//        dashboard.setSalesTrend(getSalesTrendForLast7Days());
        dashboard.setTopSellingProducts(getTopSellingProducts());
        dashboard.setLowStockProducts(getLowStockProducts());
        dashboard.setSalesComparison(getSalesComparisonChart());
        dashboard.setInventoryValueComparison(getInventoryValueComparisonChart());


        return dashboard;
    }

    private KpiData getKpis() {
        KpiData kpis = new KpiData();
        YearMonth currentMonth = YearMonth.now();
        LocalDate startOfMonth = currentMonth.atDay(1);
        LocalDate endOfMonth = currentMonth.atEndOfMonth();

        // 查詢本月銷售總額 (僅計算已出貨訂單)
        BigDecimal monthlySales = salesOrderRepository.findMonthlySalesTotalFrom(startOfMonth)
                .stream()
                .filter(result -> (Integer) result[0] == currentMonth.getYear() && (Integer) result[1] == currentMonth.getMonthValue())
                .map(result -> (BigDecimal) result[2])
                .findFirst()
                .orElse(BigDecimal.ZERO);
        kpis.setMonthlySalesTotal(monthlySales);

        // 查詢待處理訂單 (狀態為 CONFIRMED)
        kpis.setUnpaidSalesOrders(salesOrderRepository.countByPaymentStatus(PaymentStatus.UNPAID));
//        kpis.setPendingSalesOrders(pendingOrders);
//        kpis.setPendingShipments(pendingOrders);

        // 查詢低庫存商品數量
        kpis.setProductsBelowSafetyStock(inventoryRepository.countLowStockInventories());

        return kpis;
    }

    private ComparisonChartData getSalesComparisonChart() {
        LocalDate startDate = LocalDate.now().minusYears(1).withDayOfYear(1); // 從去年第一天開始查
        List<Object[]> results = salesOrderRepository.findMonthlySalesTotalFrom(startDate);

        return buildComparisonChartData(results);
    }

    private ComparisonChartData getInventoryValueComparisonChart() {
        LocalDate startDate = LocalDate.now().minusYears(1).withDayOfYear(1);
        LocalDateTime startDateTime = startDate.atStartOfDay();

        // 1. 取得「期初餘額」：計算圖表開始時間點之前的庫存總價值
        BigDecimal initialValue = inventoryMovementRepository.findTotalValueBefore(startDateTime);
        if (initialValue == null) {
            initialValue = BigDecimal.ZERO;
        }

        // 2. 取得圖表時間範圍內，每個月的「淨變動」
        List<Object[]> monthlyChangesRaw = inventoryMovementRepository.findMonthlyInventoryValueChangeFrom(startDateTime);
        Map<YearMonth, BigDecimal> monthlyChangesMap = monthlyChangesRaw.stream()
                .collect(Collectors.toMap(
                        r -> YearMonth.of((Integer) r[0], (Integer) r[1]),
                        r -> (r[2] instanceof Double ? BigDecimal.valueOf((Double) r[2]) : (BigDecimal) r[2])
                ));

        // 3. 採用「前向累加」演算法，計算每個月底的庫存總額
        BigDecimal runningTotal = initialValue;
        List<BigDecimal> thisYearData = new ArrayList<>(Collections.nCopies(12, BigDecimal.ZERO));
        List<BigDecimal> lastYearData = new ArrayList<>(Collections.nCopies(12, BigDecimal.ZERO));
        int currentYear = LocalDate.now().getYear();

        for (int i = 0; i < 24; i++) {
            YearMonth currentMonth = YearMonth.from(startDate.plusMonths(i));
            if (currentMonth.isAfter(YearMonth.now())) {
                break; // 不計算未來的月份
            }

            // 將當月的淨變動累加到總額上
            runningTotal = runningTotal.add(monthlyChangesMap.getOrDefault(currentMonth, BigDecimal.ZERO));

            // 將計算出的月底總額，填入對應年份的數據列表中
            if (currentMonth.getYear() == currentYear) {
                thisYearData.set(currentMonth.getMonthValue() - 1, runningTotal);
            } else if (currentMonth.getYear() == currentYear - 1) {
                lastYearData.set(currentMonth.getMonthValue() - 1, runningTotal);
            }
        }

        ComparisonChartData chart = new ComparisonChartData();
        chart.setSeries(List.of(
                new Series("今年", thisYearData),
                new Series("去年", lastYearData)
        ));

        return chart;
    }


private ComparisonChartData buildComparisonChartData(List<Object[]> results) {
    ComparisonChartData chart = new ComparisonChartData();
    int currentYear = LocalDate.now().getYear();

    List<BigDecimal> thisYearData = new ArrayList<>(Collections.nCopies(12, BigDecimal.ZERO));
    List<BigDecimal> lastYearData = new ArrayList<>(Collections.nCopies(12, BigDecimal.ZERO));

    results.forEach(result -> {
        int year = (Integer) result[0];
        int month = (Integer) result[1];
        BigDecimal value = (result[2] instanceof Double ? BigDecimal.valueOf((Double) result[2]) : (BigDecimal) result[2]);
        if (value == null) value = BigDecimal.ZERO;

        if (year == currentYear) {
            thisYearData.set(month - 1, value);
        } else if (year == currentYear - 1) {
            lastYearData.set(month - 1, value);
        }
    });

    chart.setSeries(List.of(
            new Series("今年", thisYearData),
            new Series("去年", lastYearData)
    ));

    return chart;
}

private ChartData getSalesTrendForLast7Days() {
    LocalDate endDate = LocalDate.now();
    LocalDate startDate = endDate.minusDays(6);

    List<Object[]> results = salesOrderRepository.findDailySalesTotalBetween(startDate, endDate);
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd");

    List<String> labels = startDate.datesUntil(endDate.plusDays(1)).map(formatter::format).collect(Collectors.toList());
    List<BigDecimal> values = new ArrayList<>(Collections.nCopies(labels.size(), BigDecimal.ZERO));

    results.forEach(result -> {
        LocalDate date = ((java.sql.Date) result[0]).toLocalDate();
        BigDecimal total = (BigDecimal) result[1];
        int index = (int) startDate.until(date, java.time.temporal.ChronoUnit.DAYS);
        if (index >= 0 && index < values.size()) {
            values.set(index, total);
        }
    });

    return new ChartData(labels, values);
}

private List<ProductRankDTO> getTopSellingProducts() {
    LocalDate endDate = LocalDate.now();
    LocalDate startDate = endDate.minusDays(29); // 最近 30 天
    return salesOrderDetailRepository.findTopSellingProducts(startDate, endDate, PageRequest.of(0, 5)); // 取前 5 名
}

private List<LowStockProductDTO> getLowStockProducts() {
    return inventoryRepository.findLowStockInventories().stream()
            .map(inventory -> {
                LowStockProductDTO dto = new LowStockProductDTO();
                dto.setProductId(inventory.getProduct().getProductId());
                dto.setProductCode(inventory.getProduct().getProductCode());
                dto.setProductName(inventory.getProduct().getName());
                dto.setWarehouseId(inventory.getWarehouse().getWarehouseId());
                dto.setWarehouseName(inventory.getWarehouse().getName());
                dto.setCurrentStock(inventory.getCurrentStock());
                dto.setSafetyStockQuantity(new BigDecimal(inventory.getProduct().getSafetyStockQuantity()));
                return dto;
            }).collect(Collectors.toList());
}
}