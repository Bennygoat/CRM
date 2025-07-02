package com.example.demo.service.impl;

import com.example.demo.dto.response.dashboard.ChartDataDto;
import com.example.demo.dto.response.dashboard.DashboardDto;
import com.example.demo.dto.response.dashboard.KpiDto;
import com.example.demo.dto.response.dashboard.NameValueDto;
import com.example.demo.enums.OpportunityStatus;
import com.example.demo.repository.OpportunityRepository;
import com.example.demo.repository.SalesFunnelRepository;
import com.example.demo.service.DashboardService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final OpportunityRepository opportunityRepository;

    public DashboardServiceImpl(OpportunityRepository opportunityRepository) {
        this.opportunityRepository = opportunityRepository;
    }

    @Override
    public DashboardDto getDashboardData() {
        DashboardDto dashboardDto = new DashboardDto();

        // 1. KPI
        dashboardDto.setKpis(getKpiData());

        // 2. 取得商機階段分佈
        List<SalesFunnelRepository> funnelSummaries = opportunityRepository.getFunnelStageSummaries();

        funnelSummaries.sort(java.util.Comparator.comparing(summary -> summary.getStage().getSortOrder()));

        // 3. 使用 funnelSummaries 產生「圓餅圖」的數量資料
        List<NameValueDto> stageCountData = funnelSummaries.stream()
                .map(summary -> new NameValueDto(
                        summary.getStage().getDisplayName(), // 階段名稱
                        summary.getTotalCount()      // 階段數量
                ))
                .collect(Collectors.toList());
        dashboardDto.setStageDistribution(new ChartDataDto<>("商機階段分佈", stageCountData));

        // 4. 同樣使用 funnelSummaries 產生「長條圖」的金額資料
        List<NameValueDto> stageValueData = funnelSummaries.stream()
                .map(summary -> {
                    BigDecimal value = summary.getTotalValue() == null ? BigDecimal.ZERO : summary.getTotalValue();
                    return new NameValueDto(
                            summary.getStage().getDisplayName(), // 階段名稱
                            value                  // 階段總金額
                    );
                })
                .collect(Collectors.toList());
        dashboardDto.setStageValue(new ChartDataDto<>("各階段商機金額", stageValueData));

//        List<Object[]> stageResults = opportunityRepository.countOpenOpportunitiesByStage();
//        List<NameValueDto> stageData = stageResults.stream()
//                .map(result -> new NameValueDto(((OpportunityStage) result[0]).name(), (Long) result[1]))
//                .collect(Collectors.toList());
//        dashboardDto.setStageDistribution(new ChartDataDto<>("商機階段分佈", stageData));

        // 3. 過去12個月每月新增商機趨勢
        dashboardDto.setMonthlyTrend(getMonthlyOpportunityTrend());

        return dashboardDto;
    }

    private List<KpiDto> getKpiData() {
        List<KpiDto> kpis = new ArrayList<>();

        // --- 指標計算 ---
        long totalOpportunities = opportunityRepository.count();
        long wonOpportunitiesCount = opportunityRepository.countByStatus(OpportunityStatus.WON);

        // 轉換率 (以數量計算) = (成交數量 / 總商機數) * 100
        BigDecimal conversionRate = BigDecimal.ZERO;
        if (totalOpportunities > 0) {
            conversionRate = BigDecimal.valueOf(wonOpportunitiesCount)
                    .multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(totalOpportunities), 2, RoundingMode.HALF_UP);
        }

        // 取得總成交金額
        BigDecimal totalWonValue = opportunityRepository.sumExpectedValueByStatus(OpportunityStatus.WON);
        if (totalWonValue == null) {
            totalWonValue = BigDecimal.ZERO; // 如果沒有成交商機，總金額為 0
        }

        // 計算平均成交金額 = 總成交金額 / 成交數量
        BigDecimal averageWonValue = BigDecimal.ZERO;
        if (wonOpportunitiesCount > 0) {
            averageWonValue = totalWonValue.divide(BigDecimal.valueOf(wonOpportunitiesCount), 2, RoundingMode.HALF_UP);
        }

        kpis.add(new KpiDto("總商機數", BigDecimal.valueOf(totalOpportunities), "筆"));
        kpis.add(new KpiDto("轉換率", conversionRate, "%"));
        kpis.add(new KpiDto("總成交金額", totalWonValue, "元"));
        kpis.add(new KpiDto("平均成交金額", averageWonValue, "元"));

        return kpis;
    }

    /**
     * 取得過去12個月每月新增商機趨勢的圖表資料。
     * @return ChartDataDto 包含趨勢資料
     */
    private ChartDataDto<NameValueDto> getMonthlyOpportunityTrend() {
        // 定義日期格式為 "YYYY-MM"
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        // 計算12個月前的第一天
        LocalDateTime startDate = LocalDateTime.now().minusMonths(11).withDayOfMonth(1).toLocalDate().atStartOfDay();

        // 從資料庫查詢結果
        List<Object[]> trendResults = opportunityRepository.countNewOpportunitiesByMonth(startDate);

        // 將資料庫查詢結果轉換為 Map<String, Long>，方便後續處理
        Map<String, Long> monthlyCountsFromDb = trendResults.stream()
                .collect(Collectors.toMap(
                        result -> (String) result[0],
                        result -> (Long) result[1]
                ));

        // 建立一個包含過去12個月所有月份的 Map，並將初始值都設為 0L
        Map<String, Long> completeMonthlyData = new LinkedHashMap<>();
        YearMonth currentMonth = YearMonth.now();
        for (int i = 11; i >= 0; i--) {
            YearMonth month = currentMonth.minusMonths(i);
            completeMonthlyData.put(month.format(formatter), 0L);
        }

        // 將資料庫查到的數據，填入我們準備好的完整月份 Map 中
        completeMonthlyData.putAll(monthlyCountsFromDb);

        // 將最終的 Map 轉換為前端需要的 NameValueDto 列表
        List<NameValueDto> trendData = completeMonthlyData.entrySet().stream()
                .map(entry -> new NameValueDto(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());

        return new ChartDataDto<>("近一年商機趨勢", trendData);
    }
}
