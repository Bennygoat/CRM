package com.example.demo.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderDetailDto {
    private Long productId;
    private String productName;
    private int quantity;
    private double unitPrice;

    // ✨ 將建構子宣告為 public，這樣才能在其他 package 中被呼叫
    public OrderDetailDto(Long productId, String productName, int quantity, double unitPrice) {
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }
}
