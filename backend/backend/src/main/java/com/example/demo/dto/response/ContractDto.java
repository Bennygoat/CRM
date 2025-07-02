package com.example.demo.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public final class ContractDto {
    private final Long id;
    private final String contractNumber;
    private final String title;
    private final String contractType;
    private final String status;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final BigDecimal amount;
    private final Long opportunityId;
    private final Long contactId;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
}
