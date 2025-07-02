package com.example.demo.repository;


import com.example.demo.dto.erp.dashboard.ProductRankDTO;
import com.example.demo.entity.SalesOrderDetail;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface SalesOrderDetailRepository extends JpaRepository<SalesOrderDetail, Long> {
    // [新增] 查詢熱銷商品排行
    @Query("SELECT new com.example.demo.dto.erp.dashboard.ProductRankDTO(" +
            "sod.product.productId, " +
            "sod.product.name, " +
            "SUM(sod.quantity)) " +
            "FROM SalesOrderDetail sod " +
            "WHERE sod.salesOrder.orderDate BETWEEN :startDate AND :endDate " +
            "GROUP BY sod.product.productId, sod.product.name " +
            "ORDER BY SUM(sod.quantity) DESC")
//    List<ProductRankDTO> findTopSellingProducts(LocalDate startDate, LocalDate endDate, Pageable pageable);
    List<ProductRankDTO> findTopSellingProducts(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable);
}
