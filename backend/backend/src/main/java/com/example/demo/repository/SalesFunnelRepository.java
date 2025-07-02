package com.example.demo.repository;

import com.example.demo.enums.OpportunityStage;

import java.math.BigDecimal;

public interface SalesFunnelRepository {

    OpportunityStage getStage();
    Long getTotalCount();
    BigDecimal getTotalValue();
}
