package com.example.demo.service.erp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.example.demo.dto.erp.*;
import com.example.demo.entity.Product;
import com.example.demo.entity.User;
import com.example.demo.exception.DataConflictException;
import com.example.demo.repository.*;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.example.demo.entity.PurchaseOrder;
import com.example.demo.entity.PurchaseOrderDetail;
// import com.example.demo.entity.Warehouse; // 如果 PurchaseOrder 層級的 warehouseId 不再使用，可以移除
import com.example.demo.exception.ResourceNotFoundException;

import com.example.demo.enums.PurchaseOrderStatus;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PurchaseOrderService {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final ProductRepository productRepository;
    private final SupplierRepository supplierRepository;
    private final WarehouseRepository warehouseRepository;
    private final UserRepository userRepository;

    @Transactional
    public PurchaseOrder createPurchaseOrder(PurchaseOrderCreateDTO podto, Long userId) {
        com.example.demo.entity.Supplier supplier = supplierRepository.findById(podto.getSupplierId())
                .orElseThrow(() -> new ResourceNotFoundException("找不到ID為" + podto.getSupplierId() + "的廠商"));

        PurchaseOrder newOrder = new PurchaseOrder();
        newOrder.setSupplier(supplier); // Set the Supplier entity
        newOrder.setOrderDate(podto.getOrderDate());
        newOrder.setCurrency(podto.getCurrency());
        newOrder.setRemarks(podto.getRemarks());


        newOrder.setStatus(PurchaseOrderStatus.DRAFT);

        newOrder.setCreatedBy(userId);
        newOrder.setUpdatedBy(userId);
        newOrder.setCreatedAt(LocalDateTime.now());
        newOrder.setUpdatedAt(LocalDateTime.now());

        BigDecimal totalNetAmount = BigDecimal.ZERO;

        BigDecimal totalCostAmountCalculated = BigDecimal.ZERO;


        for (PurchaseOrderDetailCreateDTO detailDTO : podto.getDetails()) {
            Product product = productRepository.findById(detailDTO.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("找不到ID為" + detailDTO.getProductId() + "的商品"));

            if(!product.getIsActive()){
                throw new DataConflictException("產品 '" + product.getName() + "' (ID: " + product.getProductId() + ") 目前為非啟用狀態，無法加入進貨單。");
            }
            com.example.demo.entity.Warehouse warehouse = warehouseRepository.findById(detailDTO.getWarehouseId())
                    .orElseThrow(
                            () -> new ResourceNotFoundException("找不到 ID 為 " + detailDTO.getWarehouseId() + " 的倉庫 (來自明細)"));

            PurchaseOrderDetail detail = new PurchaseOrderDetail();
            detail.setProduct(product); // Set Product entity
            detail.setQuantity(detailDTO.getQuantity());
            detail.setUnitPrice(detailDTO.getUnitPrice());
            detail.setWarehouse(warehouse); // Set Warehouse entity
            detail.setGift(false);

            BigDecimal itemNetAmount = detail.getUnitPrice().multiply(detailDTO.getQuantity());
            detail.setItemNetAmount(itemNetAmount);


            BigDecimal taxRate = new BigDecimal("0.05");
            BigDecimal itemTaxAmount = itemNetAmount.multiply(taxRate);
            detail.setItemTaxAmount(itemTaxAmount);

            detail.setItemAmount(itemNetAmount.add(itemTaxAmount));

            detail.setCreatedBy(userId);
            detail.setUpdatedBy(userId);
            detail.setCreatedAt(LocalDateTime.now());
            detail.setUpdatedAt(LocalDateTime.now());

            newOrder.addDetail(detail);

            totalNetAmount = totalNetAmount.add(itemNetAmount);

        }


        BigDecimal taxRate = new BigDecimal("0.05");
        newOrder.setTotalNetAmount(totalNetAmount);
        BigDecimal totalTaxAmount = totalNetAmount.multiply(taxRate);
        newOrder.setTotalTaxAmount(totalTaxAmount);
        newOrder.setTotalAmount(totalNetAmount.add(totalTaxAmount));


        newOrder.setTotalCostAmount(totalCostAmountCalculated);




        String orderNumber = "PO-" + LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"))
                + "-" + String.format("%04d", purchaseOrderRepository.count() + 1);

        newOrder.setOrderNumber(orderNumber);

        return purchaseOrderRepository.save(newOrder);
    }

    public PurchaseOrderViewDTO getPurchaseOrderViewById(Long id) {
        PurchaseOrder order = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("找不到ID為 " + id + " 的進貨單"));
        PurchaseOrderViewDTO dto = PurchaseOrderViewDTO.fromEntity(order);

        Optional<User> createdUser = userRepository.findById(order.getCreatedBy());
        createdUser.ifPresent(user -> dto.setCreatedByName(user.getUserName()));
        Optional<User> updatedUser = userRepository.findById(order.getUpdatedBy());
        updatedUser.ifPresent(user -> dto.setUpdatedByName(user.getUserName()));
        return dto;
    }

    public PurchaseOrder getPurchaseOrderById(Long id) {
        return purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("找不到ID為 " + id + " 的進貨單"));
    }


    @Transactional
    public PurchaseOrderViewDTO updatePurchaseOrder(Long id, PurchaseOrderUpdateDTO updateDTO, Long userId) {
        PurchaseOrder order = getPurchaseOrderById(id);

        // Basic validation for status if order is COMPLETED or CANCELLED
        if (order.getStatus() == PurchaseOrderStatus.RECEIVED || order.getStatus() == PurchaseOrderStatus.CANCELLED) {
            throw new DataConflictException("無法修改已完成或已取消的進貨單。狀態: " + order.getStatus());
        }

        com.example.demo.entity.Supplier supplier = supplierRepository.findById(updateDTO.getSupplierId())
                .orElseThrow(() -> new ResourceNotFoundException("找不到ID為" + updateDTO.getSupplierId() + "的廠商"));

        order.setSupplier(supplier); // Set the Supplier entity
        order.setOrderDate(updateDTO.getOrderDate());
        order.setCurrency(updateDTO.getCurrency()); // Consider if currency can be changed
        order.setRemarks(updateDTO.getRemarks());
        order.setUpdatedBy(userId);
        order.setUpdatedAt(LocalDateTime.now());

        // Handle details update:
        // Option 1: Clear existing and add new (simplest for full replacement)
        order.getDetails().clear(); // This requires CascadeType.ALL and orphanRemoval=true on the details collection in PurchaseOrder entity

        BigDecimal totalNetAmount = BigDecimal.ZERO;

        for (PurchaseOrderDetailUpdateDTO detailDTO : updateDTO.getDetails()) {
            Product product = productRepository.findById(detailDTO.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("找不到ID為 " + detailDTO.getProductId() + "的商品"));
            if(!product.getIsActive()){
                throw new DataConflictException("產品 '" + product.getName() + "' (ID: " + product.getProductId() + ") 目前為非啟用狀態，無法加入進貨單。");
            }
            com.example.demo.entity.Warehouse warehouse = warehouseRepository.findById(detailDTO.getWarehouseId())
                    .orElseThrow(() -> new ResourceNotFoundException("找不到 ID 為 " + detailDTO.getWarehouseId() + " 的倉庫"));

            PurchaseOrderDetail detail = new PurchaseOrderDetail();
            detail.setProduct(product); // Set Product entity
            detail.setQuantity(detailDTO.getQuantity());
            detail.setUnitPrice(detailDTO.getUnitPrice());
            detail.setWarehouse(warehouse); // Set Warehouse entity
            detail.setGift(detailDTO.getIsGift() != null ? detailDTO.getIsGift() : false);


            BigDecimal itemNetAmount = detail.getUnitPrice().multiply(detailDTO.getQuantity());
            detail.setItemNetAmount(itemNetAmount);

            BigDecimal taxRate = new BigDecimal("0.05"); // Assuming fixed tax rate
            BigDecimal itemTaxAmount = itemNetAmount.multiply(taxRate);
            detail.setItemTaxAmount(itemTaxAmount);
            detail.setItemAmount(itemNetAmount.add(itemTaxAmount));

            detail.setCreatedBy(userId); // Or keep original creator? For items, usually new user if modified.
            detail.setUpdatedBy(userId);
            detail.setCreatedAt(LocalDateTime.now()); // Or keep original?
            detail.setUpdatedAt(LocalDateTime.now());

            order.addDetail(detail);
            totalNetAmount = totalNetAmount.add(itemNetAmount);
        }

        BigDecimal taxRate = new BigDecimal("0.05");
        order.setTotalNetAmount(totalNetAmount);
        BigDecimal totalTaxAmount = totalNetAmount.multiply(taxRate);
        order.setTotalTaxAmount(totalTaxAmount);
        order.setTotalAmount(totalNetAmount.add(totalTaxAmount));
        // order.setTotalCostAmount(BigDecimal.ZERO); // Recalculate if applicable

        PurchaseOrder updatedOrder = purchaseOrderRepository.save(order);
        return getPurchaseOrderViewById(updatedOrder.getPurchaseOrderId());
    }

    @Transactional
    public PurchaseOrderViewDTO deletePurchaseOrder(Long id, Long userId) {
        PurchaseOrder order = getPurchaseOrderById(id);

        if (order.getStatus() == PurchaseOrderStatus.RECEIVED) {
            throw new DataConflictException("無法刪除已完成的進貨單。");
        }


        order.setStatus(PurchaseOrderStatus.CANCELLED);
        order.setUpdatedBy(userId);
        order.setUpdatedAt(LocalDateTime.now());
        purchaseOrderRepository.save(order);


        return getPurchaseOrderViewById(id);
    }

    public Page<PurchaseOrderSummaryDTO> searchPurchaseOrders(
            Long supplierId,
            PurchaseOrderStatus status,
            LocalDate startDate,
            LocalDate endDate,
            String keyword,
            Pageable pageable) {

        Specification<PurchaseOrder> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (supplierId != null) {
                predicates.add(criteriaBuilder.equal(root.get("supplier").get("supplierId"), supplierId));
            }
            if (status != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }
            if (startDate != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("orderDate"), startDate));
            }
            if (endDate != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("orderDate"), endDate));
            }
            if (keyword != null && !keyword.trim().isEmpty()) {
                String likePattern = "%" + keyword.toLowerCase() + "%";
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("orderNumber")), likePattern),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("remarks")), likePattern),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("supplier").get("name")), likePattern)
                ));
            }
            // Exclude CANCELLED orders by default unless explicitly requested (or handle via status filter)
            // predicates.add(criteriaBuilder.notEqual(root.get("status"), PurchaseOrderStatus.CANCELLED));


            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        Page<PurchaseOrder> purchaseOrderPage = purchaseOrderRepository.findAll(spec, pageable);
        return purchaseOrderPage.map(order -> {
            PurchaseOrderSummaryDTO dto = PurchaseOrderSummaryDTO.fromEntity(order);

            return dto;
        });
    }
}