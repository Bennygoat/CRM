package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.InventoryMovement;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryMovementRepository extends JpaRepository<InventoryMovement, Long>{


    @Query("SELECT YEAR(im.movementDate), MONTH(im.movementDate), SUM(im.totalCostChange) " +
            "FROM InventoryMovement im " +
            "WHERE im.movementDate >= :startDate " +
            "GROUP BY YEAR(im.movementDate), MONTH(im.movementDate)")
    List<Object[]> findMonthlyInventoryValueChangeFrom(LocalDateTime startDate);


    @Query("SELECT SUM(im.totalCostChange) FROM InventoryMovement im WHERE im.movementDate < :endDate")
    BigDecimal findTotalValueBefore(LocalDateTime endDate);
    Optional<InventoryMovement> findByDocumentItemId(Long documentItemId);
}
