package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Product;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    Optional<Product> findByProductCode(String productCode);

    /**
     * 【全新增加】根據商品分類的 ID 來查找所有商品。
     * Spring Data JPA 會自動解析這個方法名，產生類似 "WHERE category_id = ?" 的查詢。
     */
    List<Product> findByCategory_CategoryId(Long categoryId);
}
