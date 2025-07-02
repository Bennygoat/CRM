package com.example.demo.dto.erp;

import com.example.demo.entity.Product;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class ProductResponseDTO {
    private Long productId;
    private String productCode;
    private String name;
    private String description;
    private BigDecimal basePrice;
    private Boolean isActive;
    private Boolean isSalable;
    private String taxType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long categoryId;
    private String categoryName;
    private Long unitId;
    private String unitName;

    public static ProductResponseDTO fromEntity(Product product){
        ProductResponseDTO dto = new ProductResponseDTO();
        dto.setProductId(product.getProductId());
        dto.setProductCode(product.getProductCode());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setBasePrice(product.getBasePrice());
        dto.setIsActive(product.getIsActive());
        dto.setIsSalable(product.getIsSalable());
        dto.setTaxType(product.getTaxType());
        dto.setCreatedAt(product.getCreatedAt());
        dto.setUpdatedAt(product.getUpdatedAt());

        if(product.getCategory() != null){
            dto.setCategoryId(product.getCategory().getCategoryId());
            dto.setCategoryName(product.getCategory().getName());
        }
        if(product.getUnit() != null){
            dto.setUnitId(product.getUnit().getUnitId());
            dto.setUnitName(product.getUnit().getName());
        }

        return dto;


    }
}
