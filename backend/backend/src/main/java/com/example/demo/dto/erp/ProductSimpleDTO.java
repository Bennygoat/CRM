package com.example.demo.dto.erp;

import com.example.demo.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductSimpleDTO {
    private Long productId;
    private String productName;

    public static ProductSimpleDTO fromEntity(Product product) {
        return new ProductSimpleDTO(product.getProductId(), product.getName());
    }
}