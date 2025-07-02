package com.example.demo.enums;

public enum MovementType {
    PURCHASE_IN,        // 採購入庫
    SALE_SHIPMENT_OUT,  // 銷售出貨
    ADJUSTMENT_IN,      // 庫存調整（增）
    ADJUSTMENT_OUT,     // 庫存調整（減）
    STOCK_RESERVE, // 庫存預留 (不影響實際庫存，影響可銷售庫存)
    STOCK_RELEASE,
    STOCK_RELEASE_RESERVATION, // 釋放預留
    CUSTOMER_RETURN_IN, // 客戶退貨入庫
    SUPPLIER_RETURN_OUT, // 退貨給供應商出庫
    MISCELLANEOUS_RECEIPT, // 其他入庫 (e.g. initial stock, non-PO receipt)
    MISCELLANEOUS_ISSUE // 其他出庫
    // TRANSFER_IN,
    // TRANSFER_OUT
}//TODO(joshkuei): Remain flexible for future movement types
