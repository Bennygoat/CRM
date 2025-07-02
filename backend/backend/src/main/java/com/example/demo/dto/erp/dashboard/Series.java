package com.example.demo.dto.erp.dashboard;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Series {
    private String name;
    private List<BigDecimal> data;

//    public Series(String name, List<BigDecimal> data) {
//        this.name = name;
//        this.data = data;
//    }
}
