package com.example.demo.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Inventory;
import com.example.demo.entity.Product;
import com.example.demo.entity.Warehouse;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long>, JpaSpecificationExecutor<Inventory> {

    Optional<Inventory> findByProduct_ProductIdAndWarehouse_WarehouseId(Long productId, Long warehouseId);
    List<Inventory> findByProduct_ProductId(Long productId);
    Optional<Inventory> findByProductAndWarehouse(Product product, Warehouse warehouse);


    //prevent overbooking
    @Modifying
    @Query("UPDATE Inventory i " +
            "SET i.currentStock = i.currentStock - :quantity, " +
            "    i.unitsAllocated = i.unitsAllocated - :quantity " +
            "WHERE i.product.productId = :productId " +
            "  AND i.warehouse.warehouseId = :warehouseId " +
            "  AND i.currentStock >= :quantity " +
            "  AND i.unitsAllocated >= :quantity")
    int deductStockAndAllocation(
            @Param("productId") Long productId,
            @Param("warehouseId") Long warehouseId,
            @Param("quantity") BigDecimal quantity
    );


    @Query("SELECT i FROM Inventory i WHERE i.currentStock < i.product.safetyStockQuantity")
    List<Inventory> findLowStockInventories();


    @Query("SELECT COUNT(i) FROM Inventory i WHERE i.currentStock < i.product.safetyStockQuantity")
    Long countLowStockInventories();

    @Query("SELECT SUM(i.currentStock * i.averageCost) FROM Inventory i")
    BigDecimal findCurrentTotalInventoryValue();

    //TODO(joshkuei): Rename to make property path valid: Inventory -> product -> productId
    Optional<Inventory> findTopByProduct_ProductId(Long productId);
}



