package com.example.demo.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class ContractRequest {

    private String title;
    private LocalDate endDate;
    private BigDecimal amount;
    private String termsAndConditions;
}
