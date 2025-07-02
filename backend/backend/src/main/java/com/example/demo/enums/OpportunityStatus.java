package com.example.demo.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum OpportunityStatus {

    OPEN("進行中"),      // 商機正在進行中
    WON("已贏單"),      // 商機已成交
    LOST("已丟單");   // 商機已丟失

    private final String displayName;

    OpportunityStatus(String displayName) {
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
