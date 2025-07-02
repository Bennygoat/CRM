package com.example.demo.repository;

import com.example.demo.entity.SalesOrder;
import com.example.demo.enums.PaymentStatus;
import com.example.demo.enums.SalesOrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SalesOrderRepository extends JpaRepository<SalesOrder, Long>,
        JpaSpecificationExecutor<SalesOrder> {
    Long countByPaymentStatus(PaymentStatus status);

    @Query("SELECT FUNCTION('DATE', so.orderDate), SUM(so.totalAmount) " +
            "FROM SalesOrder so " +
            "WHERE so.orderStatus = 'SHIPPED' AND so.orderDate BETWEEN :startDate AND :endDate " +
            "GROUP BY FUNCTION('DATE', so.orderDate) " +
            "ORDER BY FUNCTION('DATE', so.orderDate)")
    List<Object[]> findDailySalesTotalBetween(LocalDate startDate, LocalDate endDate);

    @Query("SELECT YEAR(so.orderDate), MONTH(so.orderDate), SUM(so.totalAmount) " +
            "FROM SalesOrder so " +
            "WHERE so.orderStatus = 'SHIPPED' AND so.orderDate >= :startDate " +
            "GROUP BY YEAR(so.orderDate), MONTH(so.orderDate)")
    List<Object[]> findMonthlySalesTotalFrom(LocalDate startDate);
}
