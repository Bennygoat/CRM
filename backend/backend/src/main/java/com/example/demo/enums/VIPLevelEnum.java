package com.example.demo.enums;

import com.example.demo.entity.VIPLevel;

public enum VIPLevelEnum {
    BRONZE(0, 0, "歡迎使用者"),
    SILVER(10000, 5000, "滿萬免運"),
    GOLD(50000, 20000, "9折優惠");

    public final double upgrade;
    public final double downgrade;
    public final String desc;

    VIPLevelEnum(double upgrade, double downgrade, String desc) {
        this.upgrade = upgrade;
        this.downgrade = downgrade;
        this.desc = desc;
    }

    public VIPLevel toEntity() {
        return VIPLevel.builder()
                .level(this.name())
                .upgradeThreshold(this.upgrade)
                .downgradeThreshold(this.downgrade)
                .bonusDescription(this.desc)
                .build();
    }
}
