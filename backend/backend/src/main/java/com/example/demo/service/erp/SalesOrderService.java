package com.example.demo.service.erp;

import com.example.demo.dto.erp.*;
import com.example.demo.entity.*;
import com.example.demo.enums.PaymentStatus;
import com.example.demo.enums.SalesOrderStatus;
import com.example.demo.event.SalesOrderShippedEvent;
import com.example.demo.exception.DataConflictException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.CustomerBaseRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.SalesOrderRepository;
import com.example.demo.repository.WarehouseRepository;
import com.example.demo.specification.SalesOrderSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;



@Service
@RequiredArgsConstructor
public class SalesOrderService {
    private final SalesOrderRepository salesOrderRepository;
    private final ProductRepository productRepository;
    private final CustomerBaseRepository customerBaseRepository;
    private final WarehouseRepository warehouseRepository;
    private final InventoryService inventoryService;

    @Transactional
    public SalesOrder createSalesOrder(SalesOrderCreateDTO dto, Long userId){

        CustomerBase customer = customerBaseRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("找不到 ID 為 " + dto.getCustomerId() + " 的客戶"));

        if (!Boolean.TRUE.equals(customer.isActive())) {
            throw new DataConflictException("客戶 '" + customer.getCustomerName() + "' 為非啟用狀態，無法建立訂單。");
        }

        Warehouse warehouse = warehouseRepository.findById(dto.getWarehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("找不到 ID 為 " + dto.getWarehouseId() + " 的出貨倉庫"));



        SalesOrder newOrder = new SalesOrder();
        newOrder.setCustomer(customer);
        newOrder.setOrderDate(dto.getOrderDate());
        newOrder.setShippingAddress(dto.getShippingAddress());
        newOrder.setShippingMethod(dto.getShippingMethod());
        newOrder.setPaymentMethod(dto.getPaymentMethod());
        newOrder.setRemarks(dto.getRemarks());
        newOrder.setWarehouse(warehouse);


        newOrder.setOrderStatus(SalesOrderStatus.CONFIRMED);
        newOrder.setPaymentStatus(PaymentStatus.UNPAID);


        LocalDateTime now = LocalDateTime.now();
        newOrder.setCreatedBy(userId);
        newOrder.setUpdatedBy(userId);
        newOrder.setCreatedAt(now);
        newOrder.setUpdatedAt(now);


        BigDecimal totalNetAmount = BigDecimal.ZERO;
        int sequence = 1;
        for (SalesOrderDetailCreateDTO detailDTO : dto.getDetails()) {
            Product product = productRepository.findById(detailDTO.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("找不到 ID 為 " + detailDTO.getProductId() + " 的產品"));

            if (!product.getIsActive() || !product.getIsSalable()) {
                throw new DataConflictException("產品 '" + product.getName() + "' 非啟用或不可銷售狀態。");
            }

            SalesOrderDetail detail = new SalesOrderDetail();
            detail.setProduct(product);
            detail.setQuantity(detailDTO.getQuantity());
            detail.setUnitPrice(detailDTO.getUnitPrice());
            detail.setItemSequence(sequence++);
            detail.setDiscountRate(BigDecimal.ZERO);


            if (product.getUnit() != null) {
                detail.setUnitId(product.getUnit().getUnitId());
            }

            BigDecimal itemAmount = detail.getUnitPrice().multiply(detail.getQuantity());
            detail.setItemAmount(itemAmount);

            BigDecimal taxRateDivisor = new BigDecimal("1.05");
            BigDecimal itemNetAmount = itemAmount.divide(taxRateDivisor, 2, RoundingMode.HALF_UP);
            detail.setItemNetAmount(itemNetAmount);
            detail.setItemTaxAmount(itemAmount.subtract(itemNetAmount));

            detail.setCreatedBy(userId);
            detail.setUpdatedBy(userId);
            detail.setCreatedAt(now);
            detail.setUpdatedAt(now);

            newOrder.addDetail(detail);
            totalNetAmount = totalNetAmount.add(itemNetAmount);
        }


        newOrder.setTotalNetAmount(totalNetAmount);
        BigDecimal totalTaxAmount = totalNetAmount.multiply(new BigDecimal("0.05")).setScale(2, RoundingMode.HALF_UP);
        newOrder.setTotalTaxAmount(totalTaxAmount);
        newOrder.setTotalAmount(newOrder.getTotalNetAmount().add(newOrder.getTotalTaxAmount()));


        String orderNumber = "SO-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + "-" + (salesOrderRepository.count() + 1);
        newOrder.setOrderNumber(orderNumber);

        SalesOrder savedOrder = salesOrderRepository.save(newOrder);

        for (SalesOrderDetail detail : savedOrder.getDetails()) {
            inventoryService.reserveStock(
                    detail.getProduct().getProductId(),
                    savedOrder.getWarehouse().getWarehouseId(),
                    detail.getQuantity(),
                    "SALES_ORDER",
                    savedOrder.getSalesOrderId(),
                    detail.getItemId(), // This ID is now available
                    userId
            );
        }
//        return salesOrderRepository.save(newOrder);
        return savedOrder;
    }



    public Page<SalesOrderSummaryDTO> searchSalesOrders(
            Long customerId, SalesOrderStatus status,
            LocalDate startDate, LocalDate endDate,
            String keyword, Pageable pageable) {

        Specification<SalesOrder> spec = SalesOrderSpecification.findByCriteria(
                customerId, status, startDate, endDate, keyword);

        Page<SalesOrder> orderPage = salesOrderRepository.findAll(spec, pageable);

        return orderPage.map(SalesOrderSummaryDTO::fromEntity);
    }

    public SalesOrderViewDTO getSalesOrderById(Long orderId){
        SalesOrder order = salesOrderRepository.findById(orderId)
                .orElseThrow(()->new ResourceNotFoundException("找不到 ID 為 " + orderId + " 的銷售訂單"));

        return SalesOrderViewDTO.fromEntity(order);
    }

    @Transactional
    public SalesOrderViewDTO deleteSalesOrder(Long orderId, Long userId) {
        SalesOrder order = salesOrderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("找不到 ID 為 " + orderId + " 的銷售訂單"));

        if (order.getOrderStatus() != SalesOrderStatus.CONFIRMED) {
            throw new DataConflictException("訂單狀態為 " + order.getOrderStatus() + "，無法取消。只有已確認的訂單才能被取消。");
        }
        for (SalesOrderDetail detail : order.getDetails()) {
            inventoryService.releaseStock(
                    detail.getProduct().getProductId(),
                    order.getWarehouse().getWarehouseId(),
                    detail.getQuantity(),
                    "SALES_ORDER_CANCEL",
                    order.getSalesOrderId(),
                    detail.getItemId(),
                    userId
            );
        }

        order.setOrderStatus(SalesOrderStatus.CANCELLED);
        order.setUpdatedBy(userId);
        order.setUpdatedAt(LocalDateTime.now());

        SalesOrder cancelledOrder = salesOrderRepository.save(order);

        return SalesOrderViewDTO.fromEntity(cancelledOrder);
    }

    @Transactional
    public SalesOrderViewDTO updateSalesOrder(Long orderId, SalesOrderUpdateDTO updateDTO, Long userId) {
        SalesOrder order = salesOrderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("找不到 ID 為 " + orderId + " 的銷售訂單"));


        if (order.getOrderStatus() == SalesOrderStatus.SHIPPED ||
                order.getOrderStatus() == SalesOrderStatus.COMPLETED ||
                order.getOrderStatus() == SalesOrderStatus.CANCELLED) {
            throw new DataConflictException("訂單狀態為 " + order.getOrderStatus() + "，無法修改。");
        }
        for (SalesOrderDetail oldDetail : order.getDetails()) {
            inventoryService.releaseStock(
                    oldDetail.getProduct().getProductId(),
                    order.getWarehouse().getWarehouseId(),
                    oldDetail.getQuantity(),
                    "SALES_ORDER_UPDATE_OLD",
                    order.getSalesOrderId(),
                    oldDetail.getItemId(),
                    userId
            );
        }
        order.setOrderDate(updateDTO.getOrderDate());
        order.setShippingAddress(updateDTO.getShippingAddress());
        order.setShippingMethod(updateDTO.getShippingMethod());
        order.setPaymentMethod(updateDTO.getPaymentMethod());
        order.setRemarks(updateDTO.getRemarks());

        // Potentially update orderStatus or paymentStatus if they are part of DTO and logic allows
        // if (updateDTO.getOrderStatus() != null) {
        //     order.setOrderStatus(updateDTO.getOrderStatus());
        // }
        // if (updateDTO.getPaymentStatus() != null) {
        //     order.setPaymentStatus(updateDTO.getPaymentStatus());
        // }

        order.setUpdatedBy(userId);
        order.setUpdatedAt(LocalDateTime.now());


        order.getDetails().clear();

        BigDecimal totalNetAmount = BigDecimal.ZERO;
        int sequence = 1;
        for (SalesOrderDetailUpdateDTO detailDTO : updateDTO.getDetails()) {
            Product product = productRepository.findById(detailDTO.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("找不到 ID 為 " + detailDTO.getProductId() + " 的產品"));

            if (!product.getIsActive() || !product.getIsSalable()) {
                throw new DataConflictException("產品 '" + product.getName() + "' 非啟用或不可銷售狀態。");
            }

            SalesOrderDetail detail = new SalesOrderDetail();
            detail.setProduct(product);
            detail.setQuantity(detailDTO.getQuantity());
            detail.setUnitPrice(detailDTO.getUnitPrice());
            detail.setItemSequence(sequence++);
            detail.setDiscountRate(BigDecimal.ZERO);

            if (product.getUnit() != null) {
                detail.setUnitId(product.getUnit().getUnitId());
            }

            BigDecimal itemAmount = detail.getUnitPrice().multiply(detail.getQuantity());
            detail.setItemAmount(itemAmount);

            BigDecimal taxRateDivisor = new BigDecimal("1.05");
            BigDecimal itemNetAmountCal = itemAmount.divide(taxRateDivisor, 2, RoundingMode.HALF_UP);
            detail.setItemNetAmount(itemNetAmountCal);
            detail.setItemTaxAmount(itemAmount.subtract(itemNetAmountCal));

            detail.setCreatedBy(userId);
            detail.setUpdatedBy(userId);
            detail.setCreatedAt(LocalDateTime.now());
            detail.setUpdatedAt(LocalDateTime.now());

            order.addDetail(detail);
            totalNetAmount = totalNetAmount.add(itemNetAmountCal);
        }

        order.setTotalNetAmount(totalNetAmount);
        BigDecimal totalTaxAmount = totalNetAmount.multiply(new BigDecimal("0.05")).setScale(2, RoundingMode.HALF_UP);
        order.setTotalTaxAmount(totalTaxAmount);
        order.setTotalAmount(order.getTotalNetAmount().add(order.getTotalTaxAmount()));

        SalesOrder updatedOrder = salesOrderRepository.save(order);

        for (SalesOrderDetail newDetail : updatedOrder.getDetails()) {
            inventoryService.reserveStock(
                    newDetail.getProduct().getProductId(),
                    updatedOrder.getWarehouse().getWarehouseId(),
                    newDetail.getQuantity(),
                    "SALES_ORDER_UPDATE_NEW",
                    updatedOrder.getSalesOrderId(),
                    newDetail.getItemId(),
                    userId
            );
        }
        return SalesOrderViewDTO.fromEntity(updatedOrder);
    }

    @TransactionalEventListener
    @Transactional // 讓這個監聽方法本身也成為一個獨立的交易
    public void handleOrderShippedEvent(SalesOrderShippedEvent event) {
        System.out.println("收到訂單已出貨事件，訂單ID: " + event.salesOrderId());

        // 從事件中取得訂單 ID，並找到該訂單
        SalesOrder order = salesOrderRepository.findById(event.salesOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("監聽事件時找不到訂單，ID: " + event.salesOrderId()));

        // 執行原本在 InventoryService 中的更新邏輯
        order.setOrderStatus(SalesOrderStatus.SHIPPED);
        order.setPaymentStatus(PaymentStatus.PAID); // 根據您之前的需求，出貨即付款

        // 這裡可以安全地儲存，因為它在一個新的交易中
        salesOrderRepository.save(order);
    }
}