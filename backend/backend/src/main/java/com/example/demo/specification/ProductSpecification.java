package com.example.demo.specification;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.example.demo.entity.Product;

import jakarta.persistence.criteria.Predicate;


public class ProductSpecification {

    public static Specification<Product> findByCriteria(Long categoryId, Boolean isSalable, String keyword) {
        
        return (root, query, criteriaBuilder) -> {

            
            List<Predicate> predicates = new ArrayList<>();

            
            if (categoryId != null) {
                predicates.add(criteriaBuilder.equal(root.get("category").get("categoryId"), categoryId));
            }

            
            if (isSalable != null) {
                predicates.add(criteriaBuilder.equal(root.get("isSalable"), isSalable));
            }
            
            
            if (StringUtils.hasText(keyword)) {
                
                Predicate nameLike = criteriaBuilder.like(root.get("name"), "%" + keyword + "%");
                Predicate codeLike = criteriaBuilder.like(root.get("productCode"), "%" + keyword + "%");
                
                predicates.add(criteriaBuilder.or(nameLike, codeLike));
            }

            
            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        };
    }
}