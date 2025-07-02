package com.example.demo.dto.erp.dashboard;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ChartData {
    private List<String> labels;
    private List<BigDecimal> values;
}
