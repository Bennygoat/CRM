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
        // 1. 取得當前庫存總價值作為計算基準
        BigDecimal currentTotalValue = inventoryRepository.findCurrentTotalInventoryValue();
        if (currentTotalValue == null) currentTotalValue = BigDecimal.ZERO;

        // 2. 取得過去兩年的每月庫存價值「變動量」
        LocalDate startDate = LocalDate.now().minusYears(1).withDayOfYear(1);
        LocalDateTime startDateTime = startDate.atStartOfDay();
        List<Object[]> monthlyChanges = inventoryMovementRepository.findMonthlyInventoryValueChangeFrom(startDateTime);

        // 3. 從現在往回推算每個月的月底庫存總值
        BigDecimal[] monthlyValues = new BigDecimal[24];
        BigDecimal tempValue = currentTotalValue;

//        for (int i = 0; i < 24; i++) {
//            YearMonth targetMonth = YearMonth.now().minusMonths(i);
//
//            // 找到當月的變動量
//            BigDecimal change = monthlyChanges.stream()
//                    .filter(r -> (Integer)r[0] == targetMonth.getYear() && (Integer)r[1] == targetMonth.getMonthValue())
//                    .map(r -> (BigDecimal)r[2])
//                    .findFirst().orElse(BigDecimal.ZERO);
//
//            // 從現在往回推，所以要用「減法」。本月底的價值 = 上月底的價值 + 本月的變動
//            // => 上月底的價值 = 本月底的價值 - 本月的變動
//            tempValue = tempValue.subtract(change);
//
//            // 將計算出的上個月底的價值存起來
//            if (i < 23) { // 我們只需要存到24個月前，所以最多存23次
//                int targetIndex = (targetMonth.getYear() - startDate.getYear()) * 12 + (targetMonth.getMonthValue() - 1) -1;
//                if(targetIndex >= 0) monthlyValues[targetIndex] = tempValue;
//            }
//        }
//
//        // 將計算結果轉換為 Query 需要的 List<Object[]> 格式
//        List<Object[]> results = new ArrayList<>();
//        for (int i = 0; i < 24; i++) {
//            if (monthlyValues[i] != null) {
//                YearMonth ym = YearMonth.from(startDate.plusMonths(i));
//                results.add(new Object[]{ym.getYear(), ym.getMonthValue(), monthlyValues[i]});
//            }
//        }
//
//        return buildComparisonChartData(results);
        for (int i = 0; i <= 24; i++) {
            YearMonth processingMonth = YearMonth.now().minusMonths(i);

            BigDecimal change = monthlyChanges.stream()
                    .filter(r -> (Integer) r[0] == processingMonth.getYear() && (Integer) r[1] == processingMonth.getMonthValue())
                    .map(r -> (r[2] instanceof Double ? BigDecimal.valueOf((Double) r[2]) : (BigDecimal) r[2]))
                    .findFirst().orElse(BigDecimal.ZERO);

            if (i > 0) {
                int targetIndex = (processingMonth.getYear() - startDate.getYear()) * 12 + (processingMonth.getMonthValue() - 1);
                if (targetIndex >= 0 && targetIndex < 24) {
                    // 儲存的是上個月底的庫存價值
                    monthlyValues[targetIndex] = tempValue.subtract(change);
                }
            }
            tempValue = tempValue.subtract(change);
        }

        List<Object[]> results = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            if (monthlyValues[i] != null) {
                YearMonth ym = YearMonth.from(startDate.plusMonths(i));
                results.add(new Object[]{ym.getYear(), ym.getMonthValue(), monthlyValues[i]});
            }
        }

        return buildComparisonChartData(results);
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