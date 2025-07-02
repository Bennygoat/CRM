package com.example.demo.enums;

public enum ContractStatus {
    DRAFT,      // 草稿：合約正在起草中。
    SENT,       // 已傳送：合約已傳送給客戶。
    SIGNED,     // 已簽署：合約已由所有方簽署。
    ACTIVE,     // 生效中：合約目前有效。
    EXPIRED,    // 已過期：合約已超過其結束日期。
    TERMINATED  // 已終止：合約在其結束日期前被終止。
}
