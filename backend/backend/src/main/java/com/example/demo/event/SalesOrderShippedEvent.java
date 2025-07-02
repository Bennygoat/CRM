package com.example.demo.event;


import java.time.LocalDateTime;

/**
 * 代表「訂單已出貨」的事件。
 * 這是一個不可變的資料物件 (Immutable DTO)。
 *
 * @param salesOrderId  已出貨的銷售訂單 ID
 * @param shipmentId    對應的出貨單 ID
 * @param shippedAt     出貨時間
 */
public record SalesOrderShippedEvent(
        Long salesOrderId,
        Long shipmentId,
        LocalDateTime shippedAt
) {
}
