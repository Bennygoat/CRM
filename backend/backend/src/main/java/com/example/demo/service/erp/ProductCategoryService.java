package com.example.demo.service.erp;

import com.example.demo.dto.erp.ProductCategoryDto;
import com.example.demo.entity.ProductCategory;
import com.example.demo.repository.ProductCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductCategoryService {

    @Autowired
    private ProductCategoryRepository categoryRepository;

    public List<ProductCategoryDto> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private ProductCategoryDto convertToDto(ProductCategory category) {
        ProductCategoryDto dto = new ProductCategoryDto();
        dto.setCategoryId(category.getCategoryId());
        dto.setCategoryName(category.getName());
        return dto;
    }
}