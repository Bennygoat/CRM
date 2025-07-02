package com.example.demo.dto.response.dashboard;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class NameValueDto {

    private String name;
    private Number value;

    public NameValueDto() {}

    public NameValueDto(String name, Long value) {
        this.name = name;
        this.value = value;
    }

    public NameValueDto(String name, BigDecimal value) {
        this.name = name;
        this.value = value;
    }

}
