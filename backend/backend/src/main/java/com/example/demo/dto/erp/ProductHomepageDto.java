package com.example.demo.dto.erp;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductHomepageDto {

    private Long productId;
    private String name;
    private String description;
    private BigDecimal price;
    private List<String> imageUrls;

}