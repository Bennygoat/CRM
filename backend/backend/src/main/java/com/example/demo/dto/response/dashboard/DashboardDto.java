package com.example.demo.dto.response.dashboard;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DashboardDto {

    private List<KpiDto> kpis;
    private ChartDataDto<NameValueDto> stageDistribution;
    private ChartDataDto<NameValueDto> monthlyTrend;

    private ChartDataDto<NameValueDto> stageValue;
}
