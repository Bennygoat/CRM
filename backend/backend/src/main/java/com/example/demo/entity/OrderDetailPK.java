package com.example.demo.entity;

import java.io.Serializable;
import java.util.Objects;

public class OrderDetailPK implements Serializable {
    // 欄位名稱必須與 OrderDetail Entity 中的 @Id 欄位名稱完全一致
    private Long order;
    private Long product;

    // ✨ 1. 加入無參數建構子 (JPA 規範要求)
    public OrderDetailPK() {
    }

    // ✨ 2. 加入有參數建構子，方便未來可能的手動建立
    public OrderDetailPK(Long orderId, Long productId) {
        this.order = orderId;
        this.product = productId;
    }

    // Getters and Setters (Lombok 在這裡不適用，需要手動或由IDE產生)
    public Long getOrder() {
        return order;
    }

    public void setOrder(Long order) {
        this.order = order;
    }

    public Long getProduct() {
        return product;
    }

    public void setProduct(Long product) {
        this.product = product;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderDetailPK that = (OrderDetailPK) o;
        return Objects.equals(order, that.order) && Objects.equals(product, that.product);
    }

    @Override
    public int hashCode() {
        return Objects.hash(order, product);
    }
}
