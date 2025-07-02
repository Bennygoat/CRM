package com.example.demo.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;


public enum OpportunityStage {

    INITIAL_CONTACT(0, "初步接洽"), // 初步接洽
    NEEDS_ANALYSIS(1,"需求分析"), // 需求分析
    PROPOSAL(2,"提案"), // 提案
    NEGOTIATION(3,"談判"), // 談判
    CLOSED_WON(4,"已成交"), // 已成交
    CLOSED_LOST(5,"已丟失"); // 已丟失

    @Getter
    private final int sortOrder;
    private final String displayName;

    OpportunityStage(int sortOrder, String displayName) {
        this.sortOrder = sortOrder;
        this.displayName = displayName;
    }

    @JsonValue
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
